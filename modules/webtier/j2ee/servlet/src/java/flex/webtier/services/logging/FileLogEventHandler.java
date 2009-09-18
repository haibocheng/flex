////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////
package flex.webtier.services.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import flex.webtier.util.PropertiesUtil;


/**
 * Sends log events to a file.
 *
 * @author Karl Moss
 * @since 11Apr2001
 */
public class FileLogEventHandler
    extends LogEventHandler
{
    public static final String DEFAULT_FILENAME = "flex.log";
    public static final String DEFAULT_ROTATION_SIZE = "200k";
    public static final int DEFAULT_ROTATION_FILES = 3;
    public static final long DEFAULT_CLOSE_DELAY = 5000; // 5 seconds

    public static final int UNINITIALIZED = 0;
    public static final int STARTED = 1;
    public static final int STOPPED = 2;

    protected int status = UNINITIALIZED;
    protected static String newline;
    protected static int newlineLen;
    protected long closeDelay = DEFAULT_CLOSE_DELAY;
    protected long lastWriteTime;
    protected String currentFilename;
    protected PrintWriter out;

    /**
     * Scheduler service
     */
    private Timer timer;

    private String filename = DEFAULT_FILENAME;
    private int rotationSize;
    private String rotationSizeString = DEFAULT_ROTATION_SIZE;
    private int rotationFiles = DEFAULT_ROTATION_FILES;
    private static String localizedDateFormat = null;
    private static final String _SERVER_DATE = "{server.date}";

    protected boolean scheduled = false;

    static
    {
        // Get the system newline character(s)
        newline = System.getProperty("line.separator");
        if (newline != null) {
            newlineLen = newline.length();
        }
    }

    public FileLogEventHandler()
    {
        calculateRotationSize();
        timer = new Timer();
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getFilename()
    {
        return filename;
    }

    public long getFileSize()
    {
        return new File(filename).length();
    }

    public void setRotationSize(String size)
    {
        this.rotationSizeString = size;
        calculateRotationSize();
    }

    public String getRotationSize()
    {
        return rotationSizeString;
    }

    public int getRotationSizeValue()
    {
        return rotationSize;
    }

    protected void calculateRotationSize()
    {
        String s = rotationSizeString;
        if (s == null)
        {
            s = DEFAULT_ROTATION_SIZE;
        }
        int multiplier = 1;
        int value = 200;
        if (s.endsWith("k") || s.endsWith("K"))
        {
            s = s.substring(0, s.length() - 1);
            multiplier = 1024;
        }
        else if (s.endsWith("m") || s.endsWith("M"))
        {
            s = s.substring(0, s.length() - 1);
            multiplier = 1024000;
        }
        try
        {
            value = Integer.parseInt(s);
        }
        catch (Exception ex)
        {
            // If there is a number format, default
            multiplier = 1024;
        }
        rotationSize = value * multiplier;
    }

    public void setRotationFiles(int files)
    {
        this.rotationFiles = files;
    }

    public int getRotationFiles()
    {
        return rotationFiles;
    }

    public void setCloseDelay(long delay)
    {
        this.closeDelay = delay;
    }

    public long getCloseDelay()
    {
        return closeDelay;
    }

    public void start()
    {
        status = STARTED;
    }

    public synchronized void stop()
    {
        status = STOPPED;
        timer.cancel();
        closeFile();
    }

    public synchronized boolean logEvent(LogEvent event)
    {
        // We will not log messages if we are stopped or destroyed, but we can
        // handle all other cases
        if ((status != UNINITIALIZED) &&
            (status != STOPPED))
        {
            String msg = event.getFormattedMessage(getFormat());

            // Check to see if we need to rotate the file. This will also
            // open the file that we need to write to
            checkForRotation(event, msg);

            if (out != null)
            {
                try
                {
                    out.println(msg);
                    if (closeDelay <= 0)
                    {
                        closeFile();
                    }
                    else
                    {
                        lastWriteTime = System.currentTimeMillis();
                        if (!scheduled)
                        {
                            try
                            {
                                timer.schedule(new CloseFileTask(), closeDelay);
                                scheduled = true;
                            }
                            catch (Exception ex)
                            {
                                // Ignore scheduling errors. Just means that the file
                                // will remain open
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    // Got some error. Dump to stderr and stop the logger
                    ex.printStackTrace(System.err);
                    try
                    {
                        stop();
                    }
                    catch (Exception e)
                    {
                        // Ignore errors on stop
                    }
                }
            }
            else
            {
                // No print stream means we've got serious probles. Shut down
                try
                {
                    stop();
                }
                catch (Exception ex)
                {
                    // Ignore errors on stop
                }
            }
        }

        return true;
    }

    /**
      * Check to see if a file rotation is necessary
      * @param event The log event
      * into the current log
      */
    protected void checkForRotation(LogEvent event, String msg)
    {
        // Get the name of the file, substituting variables
        String newFilename = PropertiesUtil.expandDynamicVariables(getFilename(),
                        event.getLogTime(), event.getVariables(getFormat()));

        // If the name has changed, open the file
        if (!newFilename.equals(currentFilename)) {
            closeFile();
            out = openFile(newFilename);
        }

        // See if the user wants to rotate based on size
        if (getRotationSizeValue() > 0) {

            int insertLen = msg.length();

            boolean tooBig = false;
            try {

                // Note that the underlying OS may cache the write,
                // even though we have flushed the stream. The length
                // may not change until the OS caches are actually
                // written to disk and the file header is updated.
                long len = (new File(currentFilename)).length();
                if ((len + insertLen + newlineLen) > getRotationSizeValue()) {
                    tooBig = true;
                }
            }
            catch (Exception ex) {
                // Ignore errors
            }

            // If the file is too big, rotate it
            if (tooBig) {

                // Remove the last file in the chain
                removeFile(getRotationName(currentFilename, rotationFiles));

                for (int i = rotationFiles - 1; i > 0; i--) {
                    renameFile(getRotationName(currentFilename, i),
                               getRotationName(currentFilename, i + 1));
                }

                // Now close the current file and rename it to the first on in the path
                closeFile();
                if (rotationFiles > 0) {
                    renameFile(currentFilename, getRotationName(currentFilename, 1));
                } else {
                    removeFile(currentFilename);
                }
                out = openFile(currentFilename);
            }
        }

        // If there is no file opened at this point open it
        if (out == null) {
            out = openFile(currentFilename);
        }
    }

    protected void closeFile()
    {
        if (out != null)
        {
            try
            {
                out.close();
            }
            catch (Exception ex)
            {
                // Ignore errors on close
            }
            out = null;
        }
    }

    /**
      * Open the given file. The file is opened in append mode with
      * auto-flush on.
      * @param fname The filename
      * @return A print writer to the file, or null if not opened.
      */
    protected PrintWriter openFile(String fname)
    {
        PrintWriter out = null;

        try {

            // Open the file in append mode
            FileOutputStream fos = new FileOutputStream(fname, true);
            
            // Get a print writer in auto-flush mode
            out = new PrintWriter(new OutputStreamWriter(fos, "UTF8"), true);

            currentFilename = fname;
        }
        catch (FileNotFoundException ex) {

            // If we get a file not found it's probably due to not being able to
            // open the file due to a missing directory. Attempt to create the
            // directory
            File f = new File(fname);
            String parent = f.getParent();
            if (parent != null) {

                f = new File(parent);
                if (!f.exists() && f.mkdirs()) {

                    // Attempt to open one more time only if the directories were
                    // created
                    out = openFile(fname);
                }
                else {
                    ex.printStackTrace(System.err);
                }
            }
            else {
                ex.printStackTrace(System.err);
            }
        }
        catch (Exception ex) {

            // Dump to stderr. Hopefully stderr isn't being
            // logged back to the FileLogWriter or the user won't
            // get any feedback...
            ex.printStackTrace(System.err);
        }
        return out;
    }

    /**
      * Format a rotation filename given the original name and the
      * rotation number
      */
    protected String getRotationName(String fullpath, int n)
    {
        String name;

        int pos = 0;
        if (fullpath.lastIndexOf("/") != -1) {
            // avoid the case where '.' only exists in the filepath
            String filename = fullpath.substring(fullpath.lastIndexOf("/") + 1);
            if (filename.lastIndexOf(".") != -1) {
                pos = fullpath.lastIndexOf(".");
            }
        }
        else if (fullpath.lastIndexOf("\\") != -1) {
            // avoid the case where '.' only exists in the filepath
            String filename = fullpath.substring(fullpath.lastIndexOf("\\") + 1);
            if (filename.lastIndexOf(".") != -1) {
                pos = fullpath.lastIndexOf(".");
            }
        } else {
            // Look for the file extension
            pos = fullpath.lastIndexOf(".");
        }

        // If there is an extension, shove the rotation number
        // before it (foo.log -> foo_1.log)
        if (pos > 0) {
            name = fullpath.substring(0, pos) + "_" + n + fullpath.substring(pos);
        }
        else {
            // Just append the rotation number
            name = fullpath + "_" + n;
        }
        return name;
    }

    /**
      * Attempt to delete the given file
      * @param name The name of the file
      * @return true if the file was deleted
      */
    protected boolean removeFile(String name)
    {
        boolean rc = false;

        try {
            rc = (new File(name)).delete();
        }
        catch (Exception ex) {
        }
        return rc;
    }

    /**
      * Attempt to rename the given file
      * @param name The name of the file
      * @param newName The new name of the file
      * @return true if the file was renamed
      */
    protected boolean renameFile(String name, String newName)
    {
        boolean rc = false;
        try {
            File newFile = new File(newName);

            rc = (new File(name)).renameTo(newFile);
        }
        catch (Exception ex) {
        }
        return rc;
    }

    public static String expandServerDate(String formatString) {
        int dayPos = 0;
        int monthPos = 0;
        int index = 0;

        if (formatString != null) {
            index = formatString.indexOf(_SERVER_DATE);
            if (index == -1) return formatString;
        }

        if (localizedDateFormat == null)  {
            localizedDateFormat =  (new SimpleDateFormat()).toPattern();
            dayPos = localizedDateFormat.indexOf('d');
            monthPos = localizedDateFormat.indexOf('M');
            localizedDateFormat = (monthPos < dayPos) ? "{date MM/dd HH:mm:ss}" : "{date dd/MM HH:mm:ss}";
        }

        if (formatString == null) return localizedDateFormat;
        return formatString.substring(0,index) + localizedDateFormat + formatString.substring(index + _SERVER_DATE.length());
    }


    private class CloseFileTask extends TimerTask
    {
        public void run()
        {
            scheduleFileClose();
        }
    }

    // scheduleFileClose will close the file, if possible
    // otherwise, it will schedule a file close event, if necessary
    public synchronized void scheduleFileClose()
    {
        // Determine if the file should be closed
        scheduled = false;

        if (out != null)
        {
            long now = System.currentTimeMillis();
            if ((lastWriteTime + closeDelay) <= now)
            {
                closeFile();
            }
            else
            {
                // Need to reschedule
                try
                {
                    long ms = (lastWriteTime + closeDelay) - now;
                    timer.schedule(new CloseFileTask(), ms);
                    scheduled = true;
                }
                catch (Exception ex)
                {
                    // Ignore scheduling errors
                }
            }
        }
    }
}
