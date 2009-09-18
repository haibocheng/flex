////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2008 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger.threadsafe;

import flash.tools.debugger.Session;
import flash.tools.debugger.SourceFile;

/**
 * Thread-safe wrapper for flash.tools.debugger.SourceFile
 * @author Mike Morearty
 */
public class ThreadSafeSourceFile extends ThreadSafeDebuggerObject implements SourceFile {
	
	private SourceFile fSourceFile;
	
	private ThreadSafeSourceFile(Object syncObj, SourceFile sourceFile) {
		super(syncObj);
		fSourceFile = sourceFile;
	}

	/**
	 * Wraps a SourceFile inside a ThreadSafeSourceFile.  If the passed-in SourceFile
	 * is null, then this function returns null.
	 */
	public static ThreadSafeSourceFile wrap(Object syncObj, SourceFile sourceFile) {
		if (sourceFile != null)
			return new ThreadSafeSourceFile(syncObj, sourceFile);
		else
			return null;
	}

	/**
	 * Wraps an array of SourceFiles inside an array of ThreadSafeSourceFiles.
	 */
	public static ThreadSafeSourceFile[] wrapArray(Object syncObj, SourceFile[] sourceFiles) {
		ThreadSafeSourceFile[] threadSafeSourceFiles = new ThreadSafeSourceFile[sourceFiles.length];
		for (int i=0; i<sourceFiles.length; ++i) {
			threadSafeSourceFiles[i] = wrap(syncObj, sourceFiles[i]);
		}
		return threadSafeSourceFiles;
	}

	/**
	 * Returns the raw SourceFile underlying a ThreadSafeSourceFile.
	 */
	public static SourceFile getRaw(SourceFile f) {
		if (f instanceof ThreadSafeSourceFile)
			return ((ThreadSafeSourceFile)f).fSourceFile;
		else
			return f;
	}

	public static Object getSyncObject(SourceFile sf) {
		return ((ThreadSafeSourceFile)sf).getSyncObject();
	}

	public String getRawName() {
		synchronized (getSyncObject()) {
			return fSourceFile.getRawName();
		}
	}

	public String getFunctionNameForLine(Session session, int lineNum) {
		synchronized (getSyncObject()) {
			return fSourceFile.getFunctionNameForLine(session, lineNum);
		}
	}

	public String[] getFunctionNames(Session session) {
		synchronized (getSyncObject()) {
			return fSourceFile.getFunctionNames(session);
		}
	}

	public int getId() {
		synchronized (getSyncObject()) {
			return fSourceFile.getId();
		}
	}

	public String getLine(int lineNum) {
		synchronized (getSyncObject()) {
			return fSourceFile.getLine(lineNum);
		}
	}

	public int getLineCount() {
		synchronized (getSyncObject()) {
			return fSourceFile.getLineCount();
		}
	}

	public int getLineForFunctionName(Session session, String name) {
		synchronized (getSyncObject()) {
			return fSourceFile.getLineForFunctionName(session, name);
		}
	}

	public String getName() {
		synchronized (getSyncObject()) {
			return fSourceFile.getName();
		}
	}

	public int getOffsetForLine(int lineNum) {
		synchronized (getSyncObject()) {
			return fSourceFile.getOffsetForLine(lineNum);
		}
	}

	public String getPackageName() {
		synchronized (getSyncObject()) {
			return fSourceFile.getPackageName();
		}
	}

	public String getFullPath() {
		synchronized (getSyncObject()) {
			return fSourceFile.getFullPath();
		}
	}

	public String getBasePath() {
		synchronized (getSyncObject()) {
			return fSourceFile.getBasePath();
		}
	}

	public String toString() {
		synchronized (getSyncObject()) {
			return fSourceFile.toString();
		}
	}
}
