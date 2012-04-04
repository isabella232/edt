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

import java.util.Locale;

import org.eclipse.edt.javart.resources.RunUnitBase;
import org.eclipse.edt.javart.resources.StartupInfo;

import eglx.lang.AnyException;

public class JSERunUnit extends RunUnitBase 
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public JSERunUnit( StartupInfo startInfo ) throws AnyException 
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

		Locale.setDefault( loc );
		localizedText.switchLocale( loc );
	}
	
	@Override
	public void exit() 
	{
		System.exit( getReturnCode() );
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
		// A transfer to transaction effectively begins a new RunUnit.
		return trans.toTransaction;
	}
}
