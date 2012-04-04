/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
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
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.edt.javart.Constants;

import eglx.lang.AnyException;

/**
 * Keeps track of resources for a RunUnit.
 */
public class ResourceManager implements Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * List of recoverable resources.
	 */
	private ArrayList resourceList;

	/**
	 * Create a new instance of the resource manager.
	 */
	public ResourceManager()
	{
		resourceList = new ArrayList( 7 );
	}
	
	/**
	 * @return the list of recoverable resources.
	 */
	public ArrayList getResourceList()
	{
		return resourceList;
	}

	/**
	 * Commit all changes.
	 */
	public void commit( RunUnitBase ru ) throws AnyException
	{
		if ( resourceList.size() > 0 )
		{
			// Iterate over a copy of the list so resources can be registered and
			// unregistered while we're looping.
			for ( Iterator i = new ArrayList( resourceList ).iterator(); i.hasNext(); )
			{
				((RecoverableResource)i.next()).commit( ru );
			}
		}
	}

	/**
	 * Rolls back changes.
	 */
	public void rollback( RunUnitBase ru ) throws AnyException
	{
		if ( resourceList.size() > 0 )
		{
			// Iterate over a copy of the list so resources can be registered and
			// unregistered while we're looping.
			for ( Iterator i = new ArrayList( resourceList ).iterator(); i.hasNext(); )
			{
				((RecoverableResource)i.next()).rollback( ru );
			}
		}
	}

	/**
	 * Do any cleanup required.  The RunUnit is coming to an end.
	 *
	 * @param ru  the run unit.
	 */
	public void exit( RunUnitBase ru ) throws AnyException
	{
		if ( resourceList.size() > 0 )
		{
			// Iterate over a copy of the list so resources can be registered and
			// unregistered while we're looping.
			for ( Iterator i = new ArrayList( resourceList ).iterator(); i.hasNext(); )
			{
				((RecoverableResource)i.next()).exit( ru );
			}
		}
	}

	/**
	 * If this RecoverableResource is a File, close the file.
	 * <P>
	 * If this RecoverableResource is a Caller and toTransaction is true, clean
	 * up and release resources.
	 *
	 * @param ru  the run unit.
	 * @param toTransaction  true if it's a transfer to a transaction not a program.
	 */
	public void transferCleanup( RunUnitBase ru, boolean toTransaction ) 
		throws AnyException
	{
		if ( resourceList.size() > 0 )
		{
			// Iterate over a copy of the list so resources can be registered and
			// unregistered while we're looping.
			for ( Iterator i = new ArrayList( resourceList ).iterator(); i.hasNext(); )
			{
				((RecoverableResource)i.next()).transferCleanup( ru, toTransaction );
			}
		}
	}
	
	/**
	 * Check to see if a resource is already registered.
	 */
	public boolean isRegistered( RecoverableResource rs )
	{
		return resourceList.contains( rs );
	}

	/**
	 * Registers resource <CODE>rs</CODE>.
	 * 
	 * @param rs  the resource to register.
	 */
	public void register( RecoverableResource rs )
	{
		if ( !resourceList.contains( rs ) )
		{
			resourceList.add( rs );
		}
	}

	/**
	 * Unregisters resource <CODE>rs</CODE>.
	 * 
	 * @param rs  the resource to unregister.
	 */
	public void unregister( RecoverableResource rs )
	{
		resourceList.remove( rs );
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
		if ( resourceList.size() > 0 )
		{
			// Iterate over a copy of the list so resources can be registered and
			// unregistered while we're looping.
			for ( Iterator i = new ArrayList( resourceList ).iterator(); i.hasNext(); )
			{
				RecoverableResource next = (RecoverableResource)i.next();
				if ( !(next instanceof Serializable) )
				{
					unregister( next );
				}
			}
		}
		
		out.defaultWriteObject();
	}
}
