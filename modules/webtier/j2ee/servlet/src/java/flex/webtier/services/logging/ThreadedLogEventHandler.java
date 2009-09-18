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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Maintains a queue of log events and processes them in a background
 * thread (via the Scheduler). This allows callers to post log events
 * and not have to wait for them to be delivered.
 *
 * @author Karl Moss
 * @since 11Apr2001
 */
public class ThreadedLogEventHandler
    extends LogEventHandler
{
    protected Vector queue = new Vector();
    protected int queuePointer = 0;
    protected boolean flushing;
    protected Object lockObject = new Object();
    protected ArrayList handlers = new ArrayList();
    protected Timer timer;

    public ThreadedLogEventHandler()
    {
        timer = new Timer();
    }

    public void addLogEventHandler(LogEventHandler handler)
    {
        handlers.add(handler);
    }

    public void init()
    {
        Iterator i = handlers.iterator();
        while (i.hasNext())
        {
            LogEventHandler eh = (LogEventHandler)i.next();
            eh.init();
        }
    }

    public void start()
    {
        Iterator i = handlers.iterator();
        while (i.hasNext())
        {
            LogEventHandler eh = (LogEventHandler)i.next();
            eh.start();
        }
    }

    public void stop()
    {
        Iterator i = handlers.iterator();
        while (i.hasNext())
        {
            while (i.hasNext())
            {
                LogEventHandler eh = (LogEventHandler)i.next();
                eh.stop();
            }
        }

        timer.cancel();
        flush();
        super.stop();
    }
    
    public void flush()
    {
        if (!queue.isEmpty()) 
        {
            flushing = true;
            while (processNextInQueue());
            flushing = false;
        }
    }
    
    public boolean logEvent(LogEvent event)
    {
        int size = 0;
        
        // Add the event to the end of the queue
        synchronized(lockObject) 
        {
            queue.addElement(event);
            size = queue.size();
        }

        // If this is an error or warning event, flush the queue immediately
        if (size == 1)
        {
            // Schedule ourselves to run immediately and return to the caller.
            // Only need to do this if we just placed the first events in the queue
            try 
            {
                timer.schedule(new ProcessQueueTask(), 0);
            }
            catch (Exception ex)
            {
                // This can happen if the scheduler is not running. Instead of
                // scheduling ourselves to run, just run directly causing
                // all log events to be processed.
                new ProcessQueueTask().run();
            }
        }
        
        return true;
    }
    
    private class ProcessQueueTask extends TimerTask
    {
        public void run()
        {
            if (!flushing)
            {
                while (processNextInQueue()) {}
            }
        }
    }

    /**
     * Process the next entry in the queue.
     * @return true if there are more entires in the queue
     */
    protected synchronized boolean processNextInQueue()
    {
        boolean more = false;
        
        if (queuePointer < queue.size()) 
        {
            more = true;
            
            // Get the event from the queue
            LogEvent event = (LogEvent) queue.elementAt(queuePointer);
            
            // Clear the event
            queue.setElementAt(null, queuePointer++);

            // send the event to the handlers
            Iterator i = handlers.iterator();
            while (i.hasNext())
            {
                LogEventHandler eh = (LogEventHandler)i.next();
                boolean b = eh.log(event);
                    
                // The logger requested that it be the last in the chain
                if (!b) 
                {
                    break;
                }            
            }

            // Are we empty now?
            synchronized(lockObject) 
            {
                if (queuePointer >= queue.size()) 
                {
                    queue.removeAllElements();
                    queuePointer = 0;
                    more = false;
                }
            }
            
            // If we just emptied the queue, flush my children
            if (!more) 
            {
                i = handlers.iterator();
                while (i.hasNext())
                {
                    LogEventHandler eh = (LogEventHandler)i.next();
                    eh.flush();
                }
            }
        }
        return more;
    }


}
