/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core;


import java.io.PrintStream;
import java.util.logging.Level;

/**
 * Small convenience class to log messages to plugin's log file and also, if desired,
 * the console. This class should only be used by classes in this plugin. Other
 * plugins should make their own copy, with appropriate ID.
 */
public class Logger {

	private static java.util.logging.Logger msgLogger;

	// Controls whether or not log/trace messages also go to the console.
	// Normally, leave this as false.  Change to true temporarily, if desired, for debugging.
	private static boolean displayToConsole = false;

	/*
	 * No instances.
	 */
	private Logger() {
	}

	public static void log(Object source, String message) {
		doLog(source, message, null);
	}

	public static void log(Object source, String message, Throwable throwable) {
		doLog(source, message, throwable);
	}

	public static void log(Object source, Throwable throwable) {
		doLog(source, null, throwable);
	}

	public static void trace(Object source, String message) {
		doTrace(Level.FINE, source, message);
	}

	public static void traceFiner(Object source, String message) {
		doTrace(Level.FINER, source, message);
	}

	public static void traceFinest(Object source, String message) {
		doTrace(Level.FINEST, source, message);
	}

	private static void doLog(Object source, String message, Throwable throwable) {
		Level level = getLevelFor(throwable);
		message = getMessageFor(source, message);
		try {
			//$NON-NLS-1$
			getLog().log(level, message, throwable);
		} catch (Exception ignored) {
		} finally {
			if (displayToConsole) {
				PrintStream out = getPrintStreamFor(level);
				out.println(message);
				if (throwable != null)
					throwable.printStackTrace(out);
			}
		}
	}

	private static void doTrace(Level level, Object source, String message) {
		String bigMessage =
			source.toString()
				+ ": " //$NON-NLS-1$
				+ " time: " //$NON-NLS-1$
				+ System.currentTimeMillis()
				+ " " //$NON-NLS-1$
				+ Thread.currentThread().toString()
				+ " " //$NON-NLS-1$
				+ message;
		try {
			getLog().log(level, bigMessage);
		} catch (Exception ignored) {
		} finally {
			if (displayToConsole) {
				System.out.println(getMessageFor(source, message));
			}
		}
	}

	private static java.util.logging.Logger getLog() {
		if (msgLogger == null) {
			msgLogger = EDTCoreIDEPlugin.getPlugin().getLogger();
		}
		return msgLogger;
	}

	private static String getMessageFor(Object source, String message) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(source);
		if (message != null) {
			buffer.append(": "); //$NON-NLS-1$
			buffer.append(message);
		}
		return buffer.toString();
	}

//	private static int getStatusLevelFor(Level level) {
//		if(level == Level.WARNING)
//			return Status.WARNING;
//		if(level == Level.SEVERE)
//			return Status.ERROR;
//		
//		return Status.INFO;
//	}

	private static PrintStream getPrintStreamFor(Level level) {
		if(level == Level.WARNING || level == Level.SEVERE)
			return System.err;
		return System.out;
	}

	private static Level getLevelFor(Throwable throwable) {
		return (throwable == null) ? Level.WARNING : Level.SEVERE;
	}
}
