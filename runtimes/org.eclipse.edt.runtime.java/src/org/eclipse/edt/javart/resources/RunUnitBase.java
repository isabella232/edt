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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeSet;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.EglExit;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.ExitProgram;
import org.eclipse.edt.javart.ExitRunUnit;
import org.eclipse.edt.javart.FatalProblem;
import egl.lang.AnyException;
import org.eclipse.edt.javart.Program;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.Transfer;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

/**
 * This is a RunUnit.
 */
public class RunUnitBase implements RunUnit, Serializable
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
	 * Contains all of the currently active Programs.  The bottom of the stack
	 * is index zero.
	 */
	protected List<Program> programStack;

	/**
	 * The last program popped off the programStack, if programStack is empty.
	 */
	private Program lastProgram;
	
	/**
	 * The libraries that have been loaded.
	 */
	protected HashMap<String, Executable> libraries;

	/**
	 * If the RunUnit ends with an error it is stored in this field.
	 */
	private Exception fatalError;
	
	/**
	 * Dynamically loads Service proxies
	 */
//	ServiceBinder serviceBinder;

	/**
	 * The returnCode of the RunUnit, determined by the returnCode field of the
	 * last Program.
	 */
	private int returnCode;

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
//			trace.put( "*** " + localizedText.getDateFormatter().format( new Date() ) + " ***" );
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
	 * Starts the RunUnit, running the Executable by calling its main method.
	 * Transfers are handled here.  When the last program is done, endRunUnit
	 * will be called.
	 *
	 * @param program  the initial Program of this RunUnit.
	 */
	public void debugStart( Program program, String...args ) throws Exception
	{
		try
		{
			while ( true )
			{
				try
				{
					program._start(args);
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
	}
	/**
	 * Starts the RunUnit, running the Executable by calling its main method.
	 * Transfers are handled here.  When the last program is done, endRunUnit
	 * will be called.
	 *
	 * @param program  the initial Program of this RunUnit.
	 */
	public void start( Program program, String...args ) throws Exception
	{
		try
		{
			while ( true )
			{
				try
				{
					program._start(args);
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
	 * Starts the RunUnit, running the Program by calling its start method.
	 * Transfers are handled here.  When the last program is done, endRunUnit
	 * will NOT be called.  Only use this for programs called by Java wrappers.
	 *
	 * @param program  the initial Program of this RunUnit.
	 */
	public void startWrapped( Program program, String...args ) throws AnyException
	{
		try
		{
			while ( true )
			{
				try
				{
					program._finishTransfer();
					program._start(args);
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
		}
		catch ( AnyException jx )
		{
			throw jx;
		}
		catch ( Exception ex )
		{
			// TODO Revisit runtime exception handling when EGL code mapped directly
			// to java runtime class.  This will allow actual stack traces that
			// point back to actual place in code without having to keep track of
			// this at runtime.
			Program programInError = null;  //program._runUnit().activeProgram();
			throw new AnyException( Message.CAUGHT_JAVA_EXCEPTION,
					JavartUtil.errorMessage(
							programInError,
							Message.CAUGHT_JAVA_EXCEPTION,
							new Object[]{ ex.toString() } ) );
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

		fatalError = ex;
		String message;
		if ( ex instanceof AnyException )
		{
			message = ex.getMessage();
			if ( message == null || message.length() == 0 )
			{
				message = ex.toString();
			}
		}
		else if ( program != null )
		{
			message =
				JavartUtil.errorMessage( program, Message.UNHANDLED_EXCEPTION,
					new String[] { ex.toString() } );
		}
		else
		{
			message =
				JavartUtil.errorMessage( program, Message.UNHANDLED_EXCEPTION,
					new String[] { ex.toString() } );
		}
		System.out.println( message );

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
	}


	/**
	 * Adds a library instance to the RunUnit.
	 *
	 * @param name  the fully-qualified class of the library.
	 * @param the library instance.
	 */
	public void addLibrary( String name, Executable library )
	{
		libraries.put( name, library );
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
			library = loadProgramByName( name );
			libraries.put( name, library );
		}

		return library;
	}

	/**
	 * Returns a new instance of the given Program.
	 *
	 * @param name  the fully-qualified class name.
	 * @return the new Program.
	 * @throws AnyException if the load fails.
	 */
	@SuppressWarnings("unchecked")
	public Executable loadProgramByName( String name ) throws AnyException
	{
		try
		{
			Class pgmClass = Class.forName( name );
			Class[] classes = { RunUnit.class };
			Constructor cons = pgmClass.getDeclaredConstructor( classes );
			Object[] args = { this };
			return (Executable)cons.newInstance( args );
		}
		catch ( Throwable ex )
		{
			if ( ex instanceof InvocationTargetException )
			{
				// An exception was thrown by the constructor.  The
				// exception is wrapped by the InvocationTargetException.
				ex = ((InvocationTargetException)ex).getTargetException();
			}

			String message = JavartUtil.errorMessage(
					this,
					Message.CREATE_OBJECT_FAILED,
					new Object[] { name, ex } );
			throw new FatalProblem( Message.CREATE_OBJECT_FAILED, message );
		}
	}

	/**
	 * Returns a new instance of the given Program.  It will have its own
	 * RunUnit, which can be obtained by calling <CODE>program.runUnit()</CODE>.
	 *
	 * @param name  the fully-qualified class name.
	 * @return the new Program.
	 * @throws FatalProblem if the load fails.
	 */
	public static Program loadProgramByNameInNewRU( String name )
		throws FatalProblem
	{
		RunUnitBase ru = null;
		try
		{
			// Load the program's Class.
			Class pgmClass = Class.forName( name );

			// Call static method _startupInfo() to get its StartupInfo.
			Method siMethod = pgmClass.getDeclaredMethod( "_startupInfo", new Class[ 0 ] );
			StartupInfo si = (StartupInfo)siMethod.invoke( null, (Object[])null );

			// Make a new RunUnit from the StartupInfo.
			ru = new RunUnitBase( si );

			// Get the program(RunUnit) constructor.
			Constructor cons = pgmClass.getDeclaredConstructor( new Class[] { RunUnitBase.class } );

			// Construct the program.
			return (Program)cons.newInstance( new Object[] { ru } );
		}
		catch ( Throwable ex )
		{
			if ( ex instanceof InvocationTargetException )
			{
				// An exception was thrown by the constructor.  The
				// exception is wrapped by the InvocationTargetException.
				ex = ((InvocationTargetException)ex).getTargetException();
			}

			String message;
			if ( ru != null )
			{
				message = JavartUtil.errorMessage( ru,
						Message.CREATE_OBJECT_FAILED,
						new Object[] { name, ex } );
			}
			else
			{
				// We didn't manage to make the RunUnit, so we don't know what
				// language to use for the message.  Use the default Locale.
				message = formatMessageInDefaultLocale(
						Message.CREATE_OBJECT_FAILED, new Object[] { name, ex } );
			}
			throw new FatalProblem( Message.CREATE_OBJECT_FAILED, message );
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
	public Exception getFatalError()
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
	public void switchLocale( Locale loc )
	{
		if ( trace.traceIsOn() )
		{
			trace.put( "Change Locale to <" + loc.getDisplayName() + ">" );
		}

		localizedText.switchLocale( loc );
//TODO JEE behavior
//		if ( !startupInfo.isJ2EE() )
//		{
//			Locale.setDefault( loc );
//		}
	}



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
		// We don't actually unload anything.  Instead, call _sqlCleanup and 
		// _initUnsavedFields on all libraries.  This will reset them to their
		// initial state.  See RATLC01163582.
		//
		// This may be a bit of a memory leak, since a library may never be used
		// again but we won't release it.  To do that we'd have to load libraries
		// when they're actually used, rather than at the beginning of the program.
		Iterator<Executable> iterator = libraries.values().iterator();
		while ( iterator.hasNext() )
		{
			Executable lib = iterator.next();
			lib._cleanup();
			
			// TODO There is no segmentation in the standard Eclipse implementation of EGL
			// IBM version would add this behavior.
//			try
//			{
//				lib._initUnsavedFields();
//			}
//			catch ( Exception ex )
//			{
//				if ( ex instanceof AnyException )
//				{
//					throw (AnyException)ex;
//				}
//				else
//				{
//					JavaObjectException jox = new JavaObjectException( lib );
//
//					String msg = ex.getMessage();
//					String className = ex.getClass().getName();
//					if ( msg == null || msg.trim().length() == 0 )
//					{
//						msg = className;
//					}
//					
//					jox.message = msg;
//					jox.exceptionType= className;
//					jox.messageID = Message.CAUGHT_JAVA_EXCEPTION;
//					
//					throw jox.exception();
//				}
//			}
		}
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
		// RATLC01487901: Users can tell us what to do with the ResourceManager
		// during serialization.  Save their choice in the properies object so
		// we're able to properly deserialize.
		String noRollback = properties.get( "com.ibm.egl.noRollbackOnSerialize" );
		if ( noRollback == null )
		{
			noRollback = Boolean.getBoolean( "com.ibm.egl.noRollbackOnSerialize" ) ? "true" : "false";
			properties.put( "com.ibm.egl.noRollbackOnSerialize", noRollback );
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

		// RATLC01487901: Users can tell us what to do with the ResourceManager
		// during serialization.
		if ( "true".equals( properties.get( "com.ibm.egl.noRollbackOnSerialize" ) ) )
		{
			resourceManager = new ResourceManager();
		}
		else
		{
			resourceManager = (ResourceManager)in.readObject();
		}
	}

	/**
	 * Returns the top of the program stack, or the Dummy Program if the stack is empty.
	 */
	public Program activeProgram() throws AnyException
	{
		if ( programStack.isEmpty() )
		{
			if ( lastProgram != null )
			{
				return lastProgram;
			}
		}
		return (Program)programStack.get( programStack.size() - 1 );
	}

	/**
	 * Returns the top of the program stack, or null if it's empty.
	 */
	public Program peekProgram()
	{
		if ( programStack.isEmpty() )
		{
			return null;
		}
		return (Program)programStack.get( programStack.size() - 1 );
	}

	/**
	 * Pops the program stack.
	 *
	 * @return the Program that had been on top of the program stack.
	 */
	public Program popProgram()
	{
		Program program = (Program)programStack.remove( programStack.size() - 1 );
		if ( program != null )
		{
			boolean retain = false;
			try
			{
				retain = program._retainOnExit( 3 );
			}
			catch ( AnyException ex )
			{
				// Won't happen.
			}
	
			if ( !retain )
			{
				program._cleanup();
			}
			if ( programStack.isEmpty() )
			{
				lastProgram = program;
			}
		}
		return program;
	}

	/**
	 * Pushes a program onto the program stack.
	 */
	public void pushProgram( Program program )
	{
		programStack.add( program );
		lastProgram = null;
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
		// outside of J2EE, the new Program uses a different set of properties.
		// Do the switch before the new Program is created, because its
		// constructor will grab references to fields of this RU, and they might
		// change along with the properties.
		if ( /* TODO JEE behavior  !startupInfo.isJ2EE() && */ trans.toTransaction )
		{
			String newName = JavartUtil.removePackageName( trans.name );
			String oldName =
				((Program)programStack.get( programStack.size() - 1 ))._alias();
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
	
//				// Make a new strlib.  Some of its variables are set according to
//				// the properties.
//				StrLib strlib;
//				try
//				{
//					strlib = new egl.lang.StrLib( this );
//				}
//				catch ( AnyException jx )
//				{
//					// This won't happen.
//				}
//				libraries.put( "egl.core.StrLib_Lib", strlib );
			}
		}
	
		// Create the new Program and pop the previous one off the stack.  The
		// new Program will be pushed onto the stack later. 
		Program newProgram = (Program)loadProgramByName( trans.name );
		popProgram();
	
		// Initialize the new Program.
		if ( trans.input != null && newProgram._inputRecord() != null )
		{
			newProgram._inputRecord().ezeCopy(trans.input);
		}
	
		return newProgram;
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Executable getExecutable(String name) throws AnyException {
		return loadProgramByName(name);
	}

	/**
	 * Returns the default defaultDateFormat. The value either comes from the properties
	 * or the Locale.
	 */
	public String getDefaultDefaultDateFormat( Locale lc )
	{
		String property = getProperties().get( "egl.default.dateFormat" );
		String pattern;
		if ( property != null && property.length() > 0 )
		{
			pattern = property;
		}
		else
		{
			DateFormat df = 
				( lc == null ? 
						DateFormat.getDateInstance( DateFormat.SHORT ) 
						: DateFormat.getDateInstance( DateFormat.SHORT, lc ) );
			if ( df instanceof SimpleDateFormat )
			{
				SimpleDateFormat sdf = (SimpleDateFormat)df;
				pattern = sdf.toPattern();
			}
			else
			{
				pattern = "MM/dd/yyyy";
			}
		}

		return pattern;
	}

	/**
	 * Returns the default defaultTimeFormat. The value either comes from the properties
	 * or the Locale.
	 */
	public String getDefaultDefaultTimeFormat( Locale lc )
	{
		String property = getProperties().get( "egl.default.timeFormat" );
		String pattern;
		if ( property != null && property.length() > 0 )
		{
			pattern = property;
		}
		else
		{
			DateFormat tf = 
				( lc == null ? 
						DateFormat.getTimeInstance( DateFormat.SHORT )
						: DateFormat.getTimeInstance( DateFormat.SHORT, lc ) );
			if ( tf instanceof SimpleDateFormat )
			{
				SimpleDateFormat stf = (SimpleDateFormat)tf;
				pattern = stf.toPattern();
			}
			else
			{
				pattern = "HH:mm:ss";
			}
		}

		return pattern;
	}

	/**
	 * Returns the default defaultTimestampFormat. The value comes from the properties
	 */
	public String getDefaultDefaultTimestampFormat()
	{
		String property = getProperties().get( "egl.default.timestampFormat" );
		String pattern;
		if ( property != null && property.length() > 0 )
		{
			pattern = property;
		}
		else
		{
			pattern = "yyyy-MM-dd-HH.mm.ss.SSSSSS";
		}

		return pattern;
	}


	// TODO: Access StrLib if available to get runtime
	// changes to these values
	@Override
	public String getDefaultDateFormat() {
		return getDefaultDefaultDateFormat(null);
	}	

	@Override
	public String getDefaultNumericFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultTimestampFormat() {
		return getDefaultDefaultTimestampFormat();
	}

	@Override
	public String getDefaultTimeFormat() {
		return getDefaultDefaultTimestampFormat();
	}	
}
