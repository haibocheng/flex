////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flash.tools.debugger.concrete;

/**
 * This class can be to count the number of messages
 * received during a debug session.
 *
 */
public class DMessageCounter implements DProtocolNotifierIF
{
	long[] m_inCounts;
	long[] m_outCounts;

	public DMessageCounter()
	{
		m_inCounts = new long[DMessage.InSIZE+1];
		m_outCounts = new long[DMessage.OutSIZE+1];

		clearArray(m_inCounts);
		clearArray(m_outCounts);
	}

	public void disconnected()
	{
		// We're being notified (via the DProtocolNotifierIF interface) that
		// the socket connection has been broken.  If anyone is waiting for
		// a message to come in, they ain't gonna get one.  So, we'll notify()
		// them so that they can wake up and realize that the connection has
		// been broken.
		Object inLock = getInLock();
		synchronized (inLock) { inLock.notifyAll(); }
		Object outLock = getOutLock();
		synchronized (outLock) { outLock.notifyAll(); }
	}

	/**
	 * Returns the object on which external code can call "wait" in order
	 * to block until a message is received.
	 */
	public Object getInLock() { return m_inCounts; }

	/**
	 * Returns the object on which external code can call "wait" in order
	 * to block until a message is sent.
	 */
	public Object getOutLock() { return m_outCounts; }

	/**
	 * Collect stats on outgoing messages 
	 */
	public void messageSent(DMessage msg)
	{
	    int type = msg.getType();
		if (type < 0 || type >=DMessage.OutSIZE)
			type = DMessage.OutSIZE;

		Object outLock = getOutLock();
		synchronized (outLock) {
			m_outCounts[type] += 1;
			outLock.notifyAll(); // tell anyone who is waiting that a message has been sent
		}
	}

	/** 
	 * Collect stats on the messages 
	 */
	public void messageArrived(DMessage msg, DProtocol which)
	{
		/* extract type */
		int type = msg.getType();

//		System.out.println("msg counter ="+type);

		/* anything we don't know about goes in a special slot at the end of the array. */
		if (type < 0 || type >= DMessage.InSIZE)
			type = DMessage.InSIZE;

		Object inLock = getInLock();
		synchronized (inLock) {
			m_inCounts[type] += 1;
			inLock.notifyAll(); // tell anyone who is waiting that a message has been received
		}
	}

	/* getters */
	public long   getInCount(int type)  { synchronized (getInLock()) { return m_inCounts[type]; } }
	public long   getOutCount(int type) { synchronized (getOutLock()) { return m_outCounts[type]; } }

	/* setters */
	public void clearInCounts()			{ synchronized (getInLock()) { clearArray(m_inCounts); } }
	public void clearOutCounts()		{ synchronized (getOutLock()) { clearArray(m_outCounts); } }

	/**
	 * Clear out the array 
	 */
	void clearArray(long[] ar)
	{
		for(int i=0; i<ar.length; i++)
			ar[i] = 0;
	}
}
