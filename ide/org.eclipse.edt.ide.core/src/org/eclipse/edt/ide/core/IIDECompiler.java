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
package org.eclipse.edt.ide.core;

/**
 * Enables clients to define a compiler. A compiler may contribute to the System parts used during compilation.
 */
public interface IIDECompiler extends org.eclipse.edt.compiler.ICompiler {
		
	/**
	 * @return the preference page id of this compiler.
	 */
	public String getPreferencePageId();
	
	/**
	 * Sets the preference page id of this compiler.
	 * 
	 * @param name  The preference page id.
	 */
	public void setPreferencePageId(String id);
	}
