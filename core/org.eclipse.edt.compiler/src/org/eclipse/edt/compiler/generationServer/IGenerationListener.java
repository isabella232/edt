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
package org.eclipse.edt.compiler.generationServer;

import org.eclipse.edt.compiler.generationServer.parts.IPartInfo;

public interface IGenerationListener {
	
	/**
	 *  This method is invoked at the beginning of a generation for a given part
	 */
	public void begin();
	
	/**
	 * @param part
	 * Provides notification that the given part was generated. This method may be called multiple times for a single generation
	 * if associated parts (DataTables and FormGroups are to be generated
	 */
	public void acceptGeneratedPart(IPartInfo part);
	
	/**
	 * @param part
	 * Provides notification that the given generatable part was encountered during generation. While this part is
	 * if associated parts (DataTables and FormGroups are to be generated
	 */
	public void acceptAssociatedPart(IPartInfo part);
	
	/**
	 * This method is invoked at the end of generation for a given part
	 */
	public void end();
	 
}
