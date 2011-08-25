/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.lang;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.eclipse.edt.javart.*;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.resources.Platform;
import org.eclipse.edt.javart.resources.Trace;
import org.eclipse.edt.javart.util.JavartUtil;

import egl.lang.AnyException;

public class SysLib extends ExecutableBase {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private static RunUnit staticRu;

	/**
	 * Constructor
	 * @param ru The rununit
	 * @throws AnyException
	 */
	public SysLib(RunUnit ru) throws AnyException {
		super(ru);
		this.staticRu = ru;
	}

	/**
	 * Returns the value of the named property, or a null/empty string if there's no such property.
	 */
	public static String getProperty(String propertyName) {
		String value = staticRu.getProperties().get(propertyName.trim());
		if (value == null)
			value = System.getProperty(propertyName.trim());
		return value;
	}

	/**
	 * Suspend current thread for a specified amount of time. The time to wait is specified in seconds in EGL, with fractions
	 * honored down to two decimal places.
	 */
	public static void wait(BigDecimal time) {
		// Truncate any extra digits by shifting the decimal point
		// over two places and converting the value to a long.
		time = time.movePointRight(2);
		time = new BigDecimal(BigInteger.valueOf(time.longValue()));
		try {
			Thread.sleep(time.longValue() * 10);
		}
		catch (IllegalArgumentException e) {
			// no-op
		}
		catch (InterruptedException e) {
			// no-op
		}
	}

	/**
	 * Run an external command in the foreground, in LINE mode. This does not return until the command has completed.
	 */
	public static void callCmd(String commandString) throws AnyException {
		runCommand(commandString, true, true);
	}

	/**
	 * Run an external command in the background, in LINE mode. This returns immediately, not waiting for the command to
	 * complete.
	 */
	public static void startCmd(String commandString) throws AnyException {
		runCommand(commandString, true, true);
	}

	private static void runCommand(String commandString, boolean lineMode, boolean wait) throws AnyException {
		final Process proc;
		try {
			proc = Platform.SYSTEM_TYPE == Platform.WIN ? Runtime.getRuntime().exec(new String[] { "cmd", "/c", commandString }) : Runtime.getRuntime().exec(
				new String[] { "/bin/sh", "-c", commandString });
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		if (wait) {
			new Thread() {
				public void run() {
					InputStream inputStream = proc.getErrorStream();
					try {
						while (inputStream.read() != -1);
					}
					catch (IOException ioe) {}
				}
			}.start();

			new Thread() {
				public void run() {
					InputStream inputStream = proc.getInputStream();
					try {
						while (inputStream.read() != -1);
					}
					catch (IOException ioe) {}
				}
			}.start();

			try {
				proc.waitFor();
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Returns a formatted message from the RunUnit's message bundle, or null if no message with the key is found.
	 */
	public static String getMessage(String key) {
		return getMessage(key, null);
	}

	/**
	 * Returns a formatted message from the RunUnit's message bundle, or null if no message with the key is found.
	 */
	public static String getMessage(String key, egl.lang.EglList<String> inserts) {
		// Get the inserts as Strings.
		String[] insertStrings = null;
		if (inserts != null) {
			insertStrings = new String[inserts.size()];
			for (int i = 0; i < insertStrings.length; i++) {
				insertStrings[i] = (String) inserts.get(i);
			}
		}
		// Look up the message.
		key = key.trim();
		String message = staticRu.getLocalizedText().getMessage(key, insertStrings);
		return message;
	}

	/**
	 * Calls the Power Server to commit changes.
	 */
	public static void commit() throws AnyException {
		RuntimeException errorException = null;
		Trace trace = staticRu.getTrace();
		boolean tracing = trace.traceIsOn(Trace.GENERAL_TRACE);
		try {
			if (tracing) {
				trace.put("commit()");
				trace.put("    committing Recoverable Resources ...");
			}

			/* Commit recoverable resource */
			staticRu.commit();
		}
		catch (AnyException jx) {
			String message = JavartUtil.errorMessage((Executable) null, Message.SYSTEM_FUNCTION_ERROR, new Object[] { "SysLib.commit", jx.getMessage() });
			errorException = new RuntimeException(message); //TODO this should be one of our exceptions
		}
		finally {
			if (errorException == null) {
				// Commit OK.
				if (tracing)
					trace.put("<-- commit()   rc = 0");
			} else {
				// Commit failed.
				if (tracing)
					trace.put("<-- commit()   rc <> 0 ");
				throw errorException;
			}
		}
	}

	/**
	 * Calls the Power Server and resource manager to rollback changes.
	 */
	public static void rollback() throws AnyException {
		RuntimeException errorException = null;
		Trace trace = staticRu.getTrace();
		boolean tracing = trace.traceIsOn(Trace.GENERAL_TRACE);
		try {
			if (tracing) {
				trace.put("rollBack()");
				trace.put("    resetting Recoverable Resources ...");
			}
			/* Roll back recoverable resources */
			staticRu.rollback();
		}
		catch (AnyException jx) {
			String message = JavartUtil.errorMessage((Executable) null, Message.SYSTEM_FUNCTION_ERROR, new Object[] { "SysLib.rollBack", jx.getMessage() });
			errorException = new RuntimeException(message); //TODO this should be one of our exceptions
		}
		finally {
			if (errorException == null) {
				// Rollback went OK.
				if (tracing)
					trace.put("<-- rollBack()   rc = 0");
			} else {
				// Rollback failed.
				if (tracing)
					trace.put("<-- rollBack()   rc <> 0");
				throw errorException;
			}
		}
	}

	/**
	 * Change the locale of the running program dynamically.
	 */
	public static void setLocale(String languageCode) {
		Locale locale = new Locale(languageCode);
		staticRu.switchLocale(locale);
	}

	/**
	 * Change the locale of the running program dynamically.
	 */
	public static void setLocale(String languageCode, String countryCode) {
		Locale locale = new Locale(languageCode, countryCode);
		staticRu.switchLocale(locale);
	}

	/**
	 * Change the locale of the running program dynamically.
	 */
	public static void setLocale(String languageCode, String countryCode, String variant) {
		Locale locale = new Locale(languageCode, countryCode, variant);
		staticRu.switchLocale(locale);
	}

	/**
	 * Write to standard output
	 */
	public static void writeStdout(String output) {
		System.out.println(output);
	}

	/**
	 * Write to standard error
	 */
	public static void writeStderr(String output) {
		System.err.println(output);
	}
}
