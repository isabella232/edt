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
package org.eclipse.edt.javart;

import java.util.Locale;

import javax.naming.InitialContext;

import org.eclipse.edt.javart.resources.RunUnitBase;
import org.eclipse.edt.javart.resources.StartupInfo;

import eglx.lang.AnyException;

public class JEERunUnit extends RunUnitBase 
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The context used for JNDI lookups.
	 */
	protected InitialContext initialContext;

	public JEERunUnit( StartupInfo startInfo ) throws AnyException 
	{
		super( startInfo );
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
	}

	@Override
	public void exit() 
	{
		// Nothing to do here.
	}

	/**
	 * Tells if a fresh set of properties should be loaded before a transfer.
	 * 
	 * @param trans  information about the transfer.
	 * @return true if a fresh set of properties should be loaded before a transfer.
	 */
	@Override
	protected boolean newPropertiesNeeded( Transfer trans )
	{
		// In JEE all properties come from the same place so there's no need to reload.
		return false;
	}

	@Override
	public Object jndiLookup( String name ) throws Exception
	{
		if ( initialContext == null )
		{
			//TODO provide API so that users can specify parameters to be passed? Or just require they set the appropriate env vars.
			initialContext = new InitialContext();
		}
		return initialContext.lookup( name );
	}
}
