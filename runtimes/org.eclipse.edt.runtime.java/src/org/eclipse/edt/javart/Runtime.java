/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart;

/**
 * Runtime provides access to the proper RunUnit for an EGL application.  It
 * operates in two ways: either storing one RunUnit in a static variable, or
 * many RunUnits, one per thread.  Use one RunUnit for stand-alone programs, and
 * one RunUnit per thread in an application server.
 * <P>
 * A setter is provided for each mode of operation.  In order to prevent errors, 
 * once one of the setters has been used, the other setter will throw an 
 * IllegalStateException.
 * <P>
 * The creator of a RunUnit is responsible for calling one of the Runtime
 * setter methods. 
 */
public class Runtime
{
	/**
	 * The static RunUnit, used for stand-alone programs.
	 */
	private static RunUnit staticRU;
	
	/**
	 * The per-thread RunUnits, used in application servers.
	 */
	private static ThreadLocal<RunUnit> threadRUs;
	
	/**
	 * Sets the RunUnit to be used by stand-alone programs.
	 * 
	 * @param ru  the RunUnit to use.
	 * @throws IllegalStateException if a per-thread RunUnit has been set.
	 */
	public static void setStaticRunUnit( RunUnit ru )
	{
		if ( threadRUs != null )
		{
			throw new IllegalStateException();
		}
		
		staticRU = ru;
	}
	
	/**
	 * Sets the RunUnit to be used by programs in an application server.
	 * 
	 * @param ru  the RunUnit to use in the current thread.
	 * @throws IllegalStateException if a static RunUnit has been set.
	 */
	public static void setThreadRunUnit( RunUnit ru )
	{
		if ( staticRU != null )
		{
			throw new IllegalStateException();
		}
		
		if ( threadRUs == null )
		{
			threadRUs = new ThreadLocal<RunUnit>();
		}
		
		threadRUs.set( ru );
	}
	
	/**
	 * Returns the appropriate RunUnit, either the static one for stand-alone
	 * programs, or the RunUnit for this thread for programs running in an
	 * application server.
	 * <P>
	 * Returns null in two cases: 1) neither setter has been called 2) there's
	 * one RunUnit per thread, but setThreadRunUnit hasn't been called in this
	 * thread yet. 
	 * 
	 * @return the RunUnit to use, or null.
	 */
	public static RunUnit getRunUnit()
	{
		if ( staticRU != null )
		{
			return staticRU;
		}
		else if ( threadRUs != null )
		{
			return threadRUs.get();
		}
		else
		{
			return null;
		}
	}
}
