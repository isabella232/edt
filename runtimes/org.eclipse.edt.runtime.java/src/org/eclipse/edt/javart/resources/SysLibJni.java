/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.resources;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.Program;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

/**
 * Native interface for EGL SysLib library
 */
public class SysLibJni
{
	/** Whether the shared library is loaded yet */
	private static boolean isLoaded = false;

	/**
	 * Run an external program in the foreground. This does not return until the
	 * command has completed.
	 * 
	 * @param program
	 *            The program
	 * @param commandString
	 *            The command string
	 * @param modeString
	 *            The mode value ("form" or "line")
	 * @throws JavartException
	 */
	public static void callCmd( RunUnit ru, String commandString, String modeString )
			throws JavartException
	{
		boolean linemode = (modeString == null || "line".equalsIgnoreCase( modeString ));

		runCmd( ru, commandString, true, linemode );
	}

	/**
	 * Start an external command in the background. This returns immediately,
	 * not waiting for the command to complete.
	 * 
	 * @param ru
	 *            The RunUnit
	 * @param commandString
	 *            The command
	 * @param modeString
	 *            The mode value ("form" or "line")
	 * @throws JavartException
	 */
	public static void startCmd( RunUnit ru, String commandString, String modeString )
			throws JavartException
	{
		boolean linemode = (modeString == null || "line".equalsIgnoreCase( modeString ));

		runCmd( ru, commandString, false, linemode );
	}

	/**
	 * Run an external program.
	 * 
	 * @param ru
	 *            The EGL RunUnit
	 * @param commandString
	 *            The command line for the external program
	 * @param wait
	 *            Whether to wait for the command to complete before returning
	 * @param linemode
	 *            Whether the command runs in line mode (true) vs form mode
	 *            (false)
	 * @throws JavartException
	 */
	private static int runCmd( RunUnit ru, String commandString, boolean wait,
			boolean linemode ) throws JavartException
	{
		if ( !isLoaded )
		{
			initLibrary( ru );
		}

		try
		{
			int result;

			
			result = doRunCommand( commandString, wait );

			return result;
		}
		catch ( Exception e )
		{
			String message = JavartUtil.errorMessage(
					(Executable)null,
					Message.RUN_COMMAND_FAILED,
					new Object[] { commandString, e } );
			JavartUtil.throwRuntimeException( Message.RUN_COMMAND_FAILED, message, null );
			// Won't get here
			return 0;
		}
	}
	
	private static boolean isRunUnitJ2EE(Program program)
    {
		//TODO JEE behavior  return program._runUnit().getStartupInfo().isJ2EE();
		return false;
    }
	
	/**
	 * Initialize the shared library if necessary. This only happens once.
	 * 
	 * @param program
	 *            The program
	 * @throws JavartException
	 */
	private static void initLibrary( RunUnit ru ) throws JavartException
	{
		if ( isLoaded )
		{
			return;
		}

		try
		{
			System.loadLibrary( "EGLsysLibJni7" );
		}
		catch ( Throwable t )
		{
			String message = JavartUtil.errorMessage(
					(Executable)null,
					Message.LOAD_LIBRARY_FAILED,
					new Object[] { "EGLsysLibJni7", t } );
			JavartUtil.throwRuntimeException( Message.LOAD_LIBRARY_FAILED, message, null );
		}

		isLoaded = true;
	}

	/**
	 * This is the native function to invoke an external command.
	 * 
	 * @param commandString
	 *            The command string
	 * @param wait
	 *            Whether to wait for the command to complete before returning
	 *            (true) or to return immediately (false)
	 * @return 0 for success, -1 for failure
	 * @throws JavartException
	 */
	public native static int doRunCommand( String commandString, boolean wait )
			throws JavartException;
	
	// TODO Move this stuff to RunUnit which will be specific for each platform difference
    private static int doJ2EERunCommand( String commandString, boolean wait )
			throws Exception
	{
		final Process proc = Platform.SYSTEM_TYPE == Platform.WIN
				? Runtime.getRuntime().exec( new String[] {
						"cmd", "/c", commandString
				} )
				: Runtime.getRuntime().exec( new String[] {
						"/bin/sh", "-c", commandString
				} );
		
		if ( !wait )
		{
			return 0;
		}
		
		new Thread()
		{
			public void run()
			{
				InputStream inputStream = proc.getErrorStream();
				try
				{
					while ( inputStream.read() != -1 )
						;
				}
				catch ( IOException ioe )
				{
				}
			}
		}.start();

		new Thread()
		{
			public void run()
			{
				InputStream inputStream = proc.getInputStream();
				try
				{
					while ( inputStream.read() != -1 )
						;
				}
				catch ( IOException ioe )
				{
				}
			}
		}.start();

		return proc.waitFor();
	}
    
 }
