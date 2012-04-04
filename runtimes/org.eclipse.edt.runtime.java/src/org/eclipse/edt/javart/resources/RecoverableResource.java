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

import org.eclipse.edt.javart.RunUnit;

import eglx.lang.AnyException;

public interface RecoverableResource 
{
	/**
	 * Commits changes.
	 *
	 * @param ru  the run unit.
	 */
	public void commit( RunUnit ru ) throws AnyException;

	/**
	 * Rolls back changes.
	 *
	 * @param ru  the run unit.
	 */
	public void rollback( RunUnit ru ) throws AnyException;

	/**
	 * Do any cleanup required.  The RunUnit is coming to an end.
	 *
	 * @param ru  the run unit.
	 */
	public void exit( RunUnit ru ) throws AnyException;

	/**
	 * If this RecoverableResource is a File, close the file.
	 * <P>
	 * If this RecoverableResource is a Caller and toTransaction is true, clean
	 * up and release resources.
	 *
	 * @param ru  the run unit.
	 * @param toTransaction  true if it's a transfer to a transaction not a program.
	 */
	public void transferCleanup( RunUnit ru, boolean toTransaction ) 
		throws AnyException;
}
