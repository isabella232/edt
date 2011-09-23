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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.resources.Platform;
import org.eclipse.edt.javart.resources.Trace;
import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDesc;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;
import org.eclipse.edt.javart.util.JavartUtil;

import eglx.persistence.sql.SQLDataSource;

public class SysLib extends ExecutableBase {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private static Map<String, RuntimeDeploymentDesc> deploymentDescs = new HashMap<String, RuntimeDeploymentDesc>();
	private static Map<QName, Binding> resources = new HashMap<QName, Binding>();
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
			proc = java.lang.Runtime.getRuntime().exec(
					Platform.SYSTEM_TYPE == Platform.WIN ? 
							new String[] { "cmd", "/c", commandString } 
							: new String[] { "/bin/sh", "-c", commandString } );
		}
		catch (IOException ex) {
			InvocationException ix = new InvocationException();
			ix.name = commandString;
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
				proc.waitFor();
			}
			catch (InterruptedException ex) {
				InvocationException ix = new InvocationException();
				ix.name = commandString;
				throw ix.fillInMessage( Message.RUN_COMMAND_FAILED, commandString, ex );
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
	public static String getMessage(String key, eglx.lang.EList<String> inserts) {
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
	 * Calls the Power Server to commit changes.
	 */
	public static void commit() throws AnyException {
		RuntimeException errorException = null;
		Trace trace = Runtime.getRunUnit().getTrace();
		boolean tracing = trace.traceIsOn(Trace.GENERAL_TRACE);
		try {
			if (tracing) {
				trace.put("commit()");
				trace.put("    committing Recoverable Resources ...");
			}

			/* Commit recoverable resource */
			Runtime.getRunUnit().commit();
		}
		catch (AnyException jx) {
			String message = JavartUtil.errorMessage( Message.SYSTEM_FUNCTION_ERROR, new Object[] { "SysLib.commit", jx.getMessage() });
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
		Trace trace = Runtime.getRunUnit().getTrace();
		boolean tracing = trace.traceIsOn(Trace.GENERAL_TRACE);
		try {
			if (tracing) {
				trace.put("rollBack()");
				trace.put("    resetting Recoverable Resources ...");
			}
			/* Roll back recoverable resources */
			Runtime.getRunUnit().rollback();
		}
		catch (AnyException jx) {
			String message = JavartUtil.errorMessage( Message.SYSTEM_FUNCTION_ERROR, new Object[] { "SysLib.rollBack", jx.getMessage() });
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
		if (output == null)
			output = "";
		System.out.println(output);
	}

	/**
	 * Write to standard error
	 */
	public static void writeStderr(String output) {
		if (output == null)
			output = "";
		System.err.println(output);
	}
	/**
	 * get the resource binding from the egldd
	 */
	public static Object getResource(String bindKey) throws AnyException{
		return getResource(bindKey, getProperty(Constants.APPLICATION_PROPERTY_FILE_NAME_KEY));
	}
	/**
	 * get the resource binding from the egldd
	 */
	public static Object getResource(String bindingKey, String propertyFileName)  throws AnyException{
		QName resourceId = new QName(propertyFileName, bindingKey);
		Binding binding = resources.get(resourceId);
		if(binding == null){
			RuntimeDeploymentDesc dd = getDeploymentDesc(propertyFileName);
			binding = getBinding(bindingKey, dd);
			if(binding == null){
				binding = getBinding(bindingKey, dd.getIncludedDescs());
			}
			if(binding != null){
				resources.put(resourceId, binding);
			}
		}
		Object resource = null;
		if(binding instanceof SQLDatabaseBinding){
			resource = new SQLDataSource(((SQLDatabaseBinding)binding).getSqlDB());
		}
		return resource;
	}
	
	private static RuntimeDeploymentDesc getDeploymentDesc(String propertyFileName){
		RuntimeDeploymentDesc dd = deploymentDescs.get(propertyFileName);
		if(dd == null){
			if(propertyFileName.charAt(0) != '.' || propertyFileName.charAt(0) != '/'){
				propertyFileName = '/' + propertyFileName;
			}
			if(!propertyFileName.endsWith("-bnd.xml")){
				propertyFileName += "-bnd.xml";
			}
			InputStream is = org.eclipse.edt.javart.Runtime.getRunUnit().getClass().getResourceAsStream(propertyFileName);
			if(is == null){
				throw new AnyException(new FileNotFoundException(propertyFileName));
			}
			else{
				try {
					dd = RuntimeDeploymentDesc.createDeploymentDescriptor(propertyFileName, is);
				} catch (Exception e) {
					throw new AnyException(e);
				}
				deploymentDescs.put(propertyFileName, dd);
			}
		}
		return dd;
	}
	
	private static Binding getBinding(String name, List<String> includes){
		List<RuntimeDeploymentDesc> includedDDs = new ArrayList<RuntimeDeploymentDesc>();
		Binding binding = null;
		for(String ddName : includes){
			RuntimeDeploymentDesc includedDD = getDeploymentDesc(ddName);
			binding = getBinding(name, includedDD);
			if(binding != null){
				break;
			}
			else{
				includedDDs.add(includedDD);
			}
		}
		for(RuntimeDeploymentDesc includedDD : includedDDs){
			binding = getBinding(name, includedDD.getIncludedDescs());
			if(binding != null){
				break;
			}
		}
		return binding;
	}
	private static Binding getBinding(String name, RuntimeDeploymentDesc dd){
		for(Binding binding : dd.getBindings()){
			if(name.equalsIgnoreCase(binding.getName())){
				return binding;
			}
		}
		return null;
	}
}
