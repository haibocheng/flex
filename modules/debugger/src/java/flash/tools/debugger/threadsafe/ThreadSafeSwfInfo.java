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

import flash.tools.debugger.InProgressException;
import flash.tools.debugger.Session;
import flash.tools.debugger.SourceFile;
import flash.tools.debugger.SwfInfo;

/**
 * Thread-safe wrapper for flash.tools.debugger.SwfInfo
 * @author Mike Morearty
 */
public class ThreadSafeSwfInfo extends ThreadSafeDebuggerObject implements SwfInfo {
	
	private SwfInfo fSwfInfo;
	
	private ThreadSafeSwfInfo(Object syncObj, SwfInfo swfInfo) {
		super(syncObj);
		fSwfInfo = swfInfo;
	}

	/**
	 * Wraps a SwfInfo inside a ThreadSafeSwfInfo.  If the passed-in SwfInfo
	 * is null, then this function returns null.
	 */
	public static ThreadSafeSwfInfo wrap(Object syncObj, SwfInfo swfInfo) {
		if (swfInfo != null)
			return new ThreadSafeSwfInfo(syncObj, swfInfo);
		else
			return null;
	}

	/**
	 * Wraps an array of SwfInfos inside an array of ThreadSafeSwfInfos.
	 */
	public static ThreadSafeSwfInfo[] wrapArray(Object syncObj, SwfInfo[] swfinfos) {
		ThreadSafeSwfInfo[] threadSafeSwfInfos = new ThreadSafeSwfInfo[swfinfos.length];
		for (int i=0; i<swfinfos.length; ++i) {
			threadSafeSwfInfos[i] = wrap(syncObj, swfinfos[i]);
		}
		return threadSafeSwfInfos;
	}

	public static Object getSyncObject(SwfInfo swfInfo) {
		return ((ThreadSafeSwfInfo)swfInfo).getSyncObject();
	}

	public boolean containsSource(SourceFile f) {
		synchronized (getSyncObject()) {
			return fSwfInfo.containsSource(ThreadSafeSourceFile.getRaw(f));
		}
	}

	public String getPath() {
		synchronized (getSyncObject()) {
			return fSwfInfo.getPath();
		}
	}

	public int getSourceCount(Session s) throws InProgressException {
		synchronized (getSyncObject()) {
			return fSwfInfo.getSourceCount(ThreadSafeSession.getRaw(s));
		}
	}

	public SourceFile[] getSourceList(Session s) throws InProgressException {
		synchronized (getSyncObject()) {
			return ThreadSafeSourceFile.wrapArray(getSyncObject(), fSwfInfo.getSourceList(ThreadSafeSession.getRaw(s)));
		}
	}

	public int getSwdSize(Session s) throws InProgressException {
		synchronized (getSyncObject()) {
			return fSwfInfo.getSwdSize(ThreadSafeSession.getRaw(s));
		}
	}

	public int getSwfSize() {
		synchronized (getSyncObject()) {
			return fSwfInfo.getSwfSize();
		}
	}

	public String getUrl() {
		synchronized (getSyncObject()) {
			return fSwfInfo.getUrl();
		}
	}

	public boolean isProcessingComplete() {
		synchronized (getSyncObject()) {
			return fSwfInfo.isProcessingComplete();
		}
	}

	public boolean isUnloaded() {
		synchronized (getSyncObject()) {
			return fSwfInfo.isUnloaded();
		}
	}
}
