/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import java.util.List;
import java.util.Locale;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.resources.Platform;

public class SysLib extends ExecutableBase {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * Constructor
	 * @param ru The rununit
	 * @throws AnyException
	 */
	public SysLib() throws AnyException {
	}

	/**
	 * Returns the value of the named property, or a null/empty string if there's no such property.
	 */
	public static String getProperty(String propertyName) {
		String value = Runtime.getRunUnit().getProperties().get(propertyName.trim());
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
	public static int callCmd(String commandString) throws AnyException {
		return runCommand(commandString, true, true);
	}

	/**
	 * Run an external command in the background, in LINE mode. This returns immediately, not waiting for the command to
	 * complete.
	 */
	public static void startCmd(String commandString) throws AnyException {
		runCommand(commandString, true, false);
	}

	private static int runCommand(String commandString, boolean lineMode, boolean wait) throws AnyException {
		final Process proc;
		try {
			proc = java.lang.Runtime.getRuntime().exec(
					Platform.SYSTEM_TYPE == Platform.WIN ? 
							new String[] { "cmd", "/c", commandString } 
							: new String[] { "/bin/sh", "-c", commandString } );
		}
		catch (IOException ex) {
			InvocationException ix = new InvocationException();
			ix.name = commandString;
			ix.initCause( ex );
			throw ix.fillInMessage( Message.RUN_COMMAND_FAILED, commandString, ex );
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
				return proc.waitFor();
			}
			catch (InterruptedException ex) {
				InvocationException ix = new InvocationException();
				ix.name = commandString;
				ix.initCause( ex );
				throw ix.fillInMessage( Message.RUN_COMMAND_FAILED, commandString, ex );
			}
		}
		else {
			return 0;
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
	public static String getMessage(String key, List<String> inserts) {
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
		String message = Runtime.getRunUnit().getLocalizedText().getMessage(key, (Object[])insertStrings);
		return message;
	}

	/**
	 * Commits changes.
	 */
	public static void commit() throws AnyException {
		Runtime.getRunUnit().commit();
	}

	/**
	 * Rolls back changes.
	 */
	public static void rollback() throws AnyException {
		Runtime.getRunUnit().rollback();
	}

	/**
	 * Change the locale of the running program dynamically.
	 */
	public static void setLocale(String languageCode) {
		Locale locale = new Locale(languageCode);
		Runtime.getRunUnit().switchLocale(locale);
	}

	/**
	 * Change the locale of the running program dynamically.
	 */
	public static void setLocale(String languageCode, String countryCode) {
		Locale locale = new Locale(languageCode, countryCode);
		Runtime.getRunUnit().switchLocale(locale);
	}

	/**
	 * Change the locale of the running program dynamically.
	 */
	public static void setLocale(String languageCode, String countryCode, String variant) {
		Locale locale = new Locale(languageCode, countryCode, variant);
		Runtime.getRunUnit().switchLocale(locale);
	}

	/**
	 * Write to standard output
	 */
	public static void writeStdout(String output) {
		if (output == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		System.out.println(output);
	}

	/**
	 * Write to standard error
	 */
	public static void writeStderr(String output) {
		if (output == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		System.err.println(output);
	}
}
