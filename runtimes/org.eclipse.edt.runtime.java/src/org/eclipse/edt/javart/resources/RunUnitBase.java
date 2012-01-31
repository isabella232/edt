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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.EglExit;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.ExitProgram;
import org.eclipse.edt.javart.ExitRunUnit;
import org.eclipse.edt.javart.FatalProblem;

import eglx.lang.AnyException;

import org.eclipse.edt.javart.Program;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.Transfer;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

/**
 * This is a RunUnit.
 */
public abstract class RunUnitBase implements RunUnit, Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * Version information (major.minor only).
	 * TODO Keep the value of this field up to date!
	 */
	public static final String VERSION = "0.7";

	/**
	 * The StartupInfo that was used to create this RunUnit.
	 */
	private StartupInfo startupInfo;

	/**
	 * The properties of this RunUnit.
	 */
	protected JavartProperties properties;

	/**
	 * The object used to trace this RunUnit's actions.
	 */
	protected Trace trace;

	/**
	 * The object for Locale-based information.
	 */
	protected LocalizedText localizedText;

	/**
	 * Keeps track of resources.
	 */
	private transient ResourceManager resourceManager;
	

	/**
	 * The currently running executable, or null if nothing is running.
	 */
	private Executable currentExecutable;
	
	/**
	 * The libraries that have been loaded.
	 */
	protected HashMap<String, Executable> libraries;

	/**
	 * If the RunUnit ends with an error it is stored in this field.
	 */
	private AnyException fatalError;
	
	/**
	 * The returnCode of the RunUnit, determined by the returnCode field of the
	 * last Program.
	 */
	private int returnCode;
	
	/**
	 * The context used for JNDI lookups.
	 */
	protected InitialContext initialContext;

	/**
	 * Makes a new RunUnit.
	 *
	 * @param startInfo  info about what kind of RunUnit is needed.
	 */
	public RunUnitBase( StartupInfo startInfo ) throws AnyException
	{
		startupInfo = startInfo;

		// The properties come from files.
		if ( startInfo.getProperties() != null )
		{
			properties = startInfo.getProperties();
		}
		else
		{
			properties = new JavartPropertiesFile( startupInfo.getPropertyFilePath() );
		}

		// Initialize fields.
		trace = new Trace(
				properties.get( "egl.trace.type" ),
				properties.get( "egl.trace.device.option", "2" ),
				properties.get( "egl.trace.device.spec" ) );
		localizedText = new LocalizedText( properties );
		resourceManager = new ResourceManager();
		libraries = new HashMap<String, Executable>();

		// Trace the information about this RunUnit.
		if ( trace.traceIsOn() )
		{
			trace.put( "*** " + new Date() + " ***" );
			trace.put( "*** " + localizedText.getDateFormatter().format( new Date() ) + " ***" );
			trace.put( " " );
			trace.put( "RunUnit: " + startInfo.getRuName() );
			trace.put( "Version: " + VERSION );
			trace.put( "System: " + Platform.SYSTEM_TYPE );
			trace.put( properties.getInfo() );
			if ( trace.traceIsOn( Trace.PROPERTIES_TRACE ) )
			{
				traceProperties();
			}
			trace.put( localizedText.getInfo() );
			trace.put( getTrace().getInfo() );
			trace.put( "java.class.path: " + System.getProperty( "java.class.path" ) );
			trace.put( "java.library.path: " + System.getProperty( "java.library.path" ) );
			trace.put( " " );
		}
	}

	/**
	 * @return the StartupInfo.
	 */
	public StartupInfo getStartupInfo()
	{
		return startupInfo;
	}

	/**
	 * @return the properties.
	 */
	public JavartProperties getProperties()
	{
		return properties;
	}

	/**
	 * @return the trace object.
	 */
	public Trace getTrace()
	{
		return trace;
	}
	
	/**
	 * @return the ResourceManager.
	 */
	public ResourceManager getResourceManager()
	{
		return resourceManager;
	}

	/**
	 * Registers resource <CODE>rs</CODE>.
	 *
	 * @param rs  the resource to register.
	 */
	public void registerResource( RecoverableResource rs )
	{
		resourceManager.register( rs );
	}

	/**
	 * Unregisters resource <CODE>rs</CODE>.
	 *
	 * @param rs  the resource to unregister.
	 */
	public void unregisterResource( RecoverableResource rs )
	{
		resourceManager.unregister( rs );
	}

	/**
	 * Commits changes made by programs in the RunUnit.
	 */
	public void commit() throws AnyException
	{
		resourceManager.commit( this );
	}

	/**
	 * Rolls back changes made by programs in the RunUnit.
	 */
	public void rollback() throws AnyException
	{
		resourceManager.rollback( this );
	}

	/**
	 * Starts the RunUnit, running the Program by calling its main method.
	 * Transfers are handled here.  When the last Program is done, endRunUnit
	 * will be called.
	 *
	 * @param program  the initial Program of this RunUnit.
	 */
	public void start( Program program ) throws Exception
	{
		try
		{
			while ( true )
			{
				try
				{
					currentExecutable = program;
					program.main();
					endRunUnit( program );
					return;
				}
				catch ( Transfer trans )
				{
					program = setupTransfer( trans );
				}
			}
		}
		catch ( EglExit exit )
		{
			// This is not an error.
			endRunUnit( program );
		}
		catch ( Exception ex )
		{
			endRunUnit( program, ex );
		}
	}

	/**
	 * Call this to end a RunUnit normally.  It performs cleanup tasks, commits
	 * and releases all resources.  If there's an error at any point,
	 * endRunUnit(Program, Exception) is called.
	 *
	 * @param program  the last Program in the RunUnit.
	 */
	public void endRunUnit( Executable program ) throws Exception
	{
		if ( trace.traceIsOn() )
		{
			trace.put( "endRunUnit " + startupInfo.getRuName()
						+ " (normal termination) with returnCode=" + returnCode );
		}

		try
		{
			commit();
			resourceManager.exit( this );
			
			trace.close();
			currentExecutable = null;
		}
		catch ( Exception ex )
		{
			endRunUnit( program, ex );
		}
	}

	/**
	 * Call this to end a RunUnit because of an error.  It performs cleanup tasks,
	 * rolls back and releases all resources.
	 *
	 * @param program  the last Program in the RunUnit.
	 * @param ex       the reason that the RunUnit is ending.
	 */
	public void endRunUnit( Executable program, Exception ex ) throws Exception
	{
		// The return code is always 693 if there was an error.
		returnCode = 693;

		if ( trace.traceIsOn() )
		{
			trace.put( "endRunUnit " + startupInfo.getRuName()
						+ " (error termination) with returnCode=" + returnCode );
		}

		fatalError = JavartUtil.makeEglException( ex );
		String message = fatalError.getMessage();
		if ( message.length() == 0 )
		{
			message = fatalError.toString();
		}
		System.out.println( message );
		if ( AnyException.STACK_TRACES )
		{
			ex.printStackTrace( System.out );
		}

		try
		{
			rollback();
		}
		catch ( Exception ex2 )
		{
			// Ignore it.
		}

		try
		{
			resourceManager.exit( this );
		}
		catch ( Exception ex2 )
		{
			// Ignore it.
		}

		trace.close();
		currentExecutable = null;
	}

	/**
	 * Returns the one and only instance of the given library for use in
	 * this RunUnit.
	 *
	 * @param name  the fully-qualified class of the library.
	 * @return the library instance to use.
	 * @throws AnyException
	 */
	public Executable loadLibrary( String name ) throws AnyException
	{
		Executable library = libraries.get( name );
		if ( library == null )
		{
			library = loadExecutable( name );
			libraries.put( name, library );
		}

		return library;
	}

	/**
	 * Returns a new instance of the given Executable.
	 *
	 * @param name  the fully-qualified class name.
	 * @return the new Executable.
	 * @throws AnyException if the load fails.
	 */
	@Override
	public Executable loadExecutable( String name ) throws AnyException
	{
		try
		{
			Class pgmClass = Class.forName( name, true, getClass().getClassLoader() );
			return (Executable)pgmClass.newInstance();
		}
		catch ( Throwable ex )
		{
			if ( ex instanceof InvocationTargetException )
			{
				// An exception was thrown by the constructor.  The
				// exception is wrapped by the InvocationTargetException.
				ex = ((InvocationTargetException)ex).getTargetException();
			}

			FatalProblem problem = new FatalProblem();
			problem.initCause( ex );
			throw problem.fillInMessage( Message.CREATE_OBJECT_FAILED, ex );
		}
	}

	/**
	 * Returns an error message formatted in the language of the default Locale.
	 * Only use this when we can't figure out which language to use for error
	 * messages.
	 *
	 * @param id       the message ID.
	 * @param inserts  the inserts.
	 * @return the formatted message.
	 */
	protected static String formatMessageInDefaultLocale( String id,
			Object[] inserts )
	{
		ResourceBundle bundle =
			ResourceBundle.getBundle( "com.ibm.javart.messages.MessageBundle" );
		String message = bundle.getString( id );
		MessageFormat mf = new MessageFormat( message );
		return mf.format( inserts );
	}

	/**
	 * Writes the properties to the trace object.  If the property
	 * "egl.jdbc.default.database.user.password" is set, we write a
	 * question mark.
	 */
	private void traceProperties()
	{
		Properties props = properties.getProperties();
		if ( props != null )
		{
			// Use a TreeSet of keys so the output is sorted by key.
			Enumeration names = props.propertyNames();
			TreeSet keySet = new TreeSet();
			while ( names.hasMoreElements() )
			{
				keySet.add( names.nextElement() );
			}
			Iterator sortedKeys = keySet.iterator();
			while ( sortedKeys.hasNext() )
			{
				String key = (String)sortedKeys.next();
				String val = props.getProperty( key );
				if ( key.equals( "egl.jdbc.default.database.user.password" ) )
				{
					val = "?";
				}
				trace.put( " > " + key + '=' + val );
			}
		}
	}

	/**
	 * @return the returnCode of the last Program in the RunUnit.
	 */
	public int getReturnCode()
	{
		return returnCode;
	}

	/**
	 * Sets the return code of the current Program in the RunUnit.
	 *
	 * @param rc  the return code.
	 */
	public void setReturnCode( int rc )
	{
		returnCode = rc;
	}

	/**
	 * If the RunUnit has ended due to an error, it will be returned.  Otherwise
	 * null is returned.
	 */
	public AnyException getFatalError()
	{
		return fatalError;
	}

	/**
	 * @return the object for Locale-based information.
	 */
	public LocalizedText getLocalizedText()
	{
		return localizedText;
	}

	/**
	 * Switches to the given Locale.
	 *
	 * @param loc  the Locale to use.
	 */
	public abstract void switchLocale( Locale loc );

	/**
	 * Exit the current program.
	 *
	 * @throws ExitProgram
	 */
	public void exitProgram() throws ExitProgram
	{
		if ( trace.traceIsOn() )
		{
			trace.put( "Exit Program" );
		}
		throw ExitProgram.getSingleton();
	}

	/**
	 * Exit the entire run unit.
	 *
	 * @throws ExitRunUnit
	 */
	public void exitRunUnit() throws ExitRunUnit
	{
		if ( trace.traceIsOn() )
		{
			trace.put( "Exit RunUnit" );
		}

		throw ExitRunUnit.getSingleton();
	}

	/**
	 * Called when a transfer is taking place.  Some resources will be committed,
	 * released, and/or closed.
	 *
	 * @param toTransaction  true/false for toTransaction/toProgram.
	 * @throws Exception if there's an error.
	 */
	public void transferCleanup( boolean toTransaction )
	{
		// Close and release some resources.  This includes all files, except
		// MQ files when transferring to a program.
		resourceManager.transferCleanup( this, toTransaction );

		// There's more to do if we're transferring to a transaction.
		if ( toTransaction )
		{
			unloadLibraries();
		}
	}
	
	/**
	 * Unloads all libraries.
	 */
	public void unloadLibraries() throws AnyException
	{
		libraries.clear();
	}
		
	/**
	 * Serializes an instance of this class.
	 * 
	 * @param out  The output stream.
	 * @throws IOException
	 */
	private void writeObject( ObjectOutputStream out )
			throws IOException
	{
		// Users can tell us what to do with the ResourceManager during serialization.
		// Save their choice in the properies object so we're able to properly deserialize.
		String noRollback = properties.get( "org.eclipse.edt.noRollbackOnSerialize" );
		if ( noRollback == null )
		{
			noRollback = Boolean.getBoolean( "org.eclipse.edt.noRollbackOnSerialize" ) ? "true" : "false";
			properties.put( "org.eclipse.edt.noRollbackOnSerialize", noRollback );
		}
		
		out.defaultWriteObject();
		
		if ( noRollback.equals( "false" ) )
		{
			try
			{
				resourceManager.rollback( this );
			}
			catch ( AnyException je )
			{
				throw new IOException( je.getMessage() );
			}
			finally
			{
				try
				{
					resourceManager.exit( this );
				}
				catch ( AnyException je )
				{
					throw new IOException( je.getMessage() );
				}
			}
			
			// We must serialize the resource manager last because it will
			// unregister anything that's not serializable. The field is transient
			// because we need to perform a rollback and exit before it's written.
			out.writeObject( resourceManager );
		}
	}
	
	/**
	 * Deserializes an instance of this class.
	 * 
	 * @param in  The input stream.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject( ObjectInputStream in )
			throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		// Users can tell us what to do with the ResourceManager during serialization.
		if ( "true".equals( properties.get( "org.eclipse.edt.noRollbackOnSerialize" ) ) )
		{
			resourceManager = new ResourceManager();
		}
		else
		{
			resourceManager = (ResourceManager)in.readObject();
		}
	}

	/**
	 * @return the active executable, or null if nothing is running.
	 */
	@Override
	public Executable getActiveExecutable() throws AnyException
	{
		return currentExecutable;
	}
	
	@Override
	public void setActiveExecutable( Executable executable )
	{
		this.currentExecutable = executable;
	}

	/**
	 * Makes changes necessary for a Transfer from one program to another.
	 *
	 * @param trans  information about the transfer.
	 * @return the new Program.
	 */
	private Program setupTransfer( Transfer trans ) throws Exception
	{
		// If this is a transfer to a transaction with a different program,
		// the new Program may use a different set of properties.  Do the switch 
		// before the new Program is created so its constructor will use the 
		// fields of this RU which might change along with the properties.
		if ( newPropertiesNeeded( trans ) )
		{
			String newName = JavartUtil.removePackageName( trans.name );
			String oldName = currentExecutable._name();
			if ( !newName.equals( oldName ) )
			{
				String newPropertiesFilePath = trans.name.replace( '.', '/' ) + ".properties";
				properties = new JavartPropertiesFile( newPropertiesFilePath );
	
				// Replace the objects that depend on the properties.
				trace = new Trace(
						properties.get( "egl.trace.type" ),
						properties.get( "egl.trace.device.option", "2" ),
						properties.get( "egl.trace.device.spec" ) );
				localizedText = new LocalizedText( properties );
			}
		}
	
		// Create the new Program.  The new Program will be set as the active executable later. 
		Program newProgram = (Program)loadExecutable( trans.name );
	
		// Initialize the new Program.
		if ( trans.input != null && newProgram._inputRecord() != null )
		{
			newProgram._inputRecord().ezeCopy(trans.input);
		}
	
		return newProgram;
	}
	
	@Override
	public Object jndiLookup( String name ) throws NamingException
	{
		if ( initialContext == null )
		{
			//TODO provide API so that users can specify parameters to be passed? Or just require they set the appropriate env vars.
			initialContext = new InitialContext();
		}
		return initialContext.lookup( name );
	}
	
	/**
	 * Tells if a fresh set of properties should be loaded before a transfer.
	 * 
	 * @param trans  information about the transfer.
	 * @return true if a fresh set of properties should be loaded before a transfer.
	 */
	protected abstract boolean newPropertiesNeeded( Transfer trans );
}
