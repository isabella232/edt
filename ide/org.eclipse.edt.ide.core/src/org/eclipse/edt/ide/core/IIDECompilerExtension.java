/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.ICompilerExtension;

public interface IIDECompilerExtension extends ICompilerExtension {
	
	/**
	 * @return a unique identifier for this compiler extension, which matches the ID in the plugin.xml extension.
	 */
	public String getId();
	
	/**
	 * Sets the identifier for this compiler extension. This will be called when the class is instantiated, passing in the value from plugin.xml.
	 * 
	 * @param id  The identifier.
	 */
	public void setId(String id);
	
	/**
	 * @return the display name for this compiler, never null or blank.
	 */
	public String getName();
	
	/**
	 * Sets the name of this compiler (typically used for display purposes).
	 * 
	 * @param name  The name of this compiler.
	 */
	public void setName(String name);
	
	/**
	 * Sets the version of this compiler.
	 * 
	 * @param version  The compiler version.
	 */
	public void setVersion(String version);
	
	/**
	 * @return the version of this compiler, e.g. "1.0.0", never null.
	 */
	public String getVersion();
	
	/**
	 * Sets the ID of another extension which this extension extends.
	 * 
	 * @param requires  The depending extension ID.
	 */
	public void setRequires(String requires);
	
	/**
	 * @return the ID of another extension which this extension extends.
	 */
	public String getRequires();
	
	/**
	 * Sets the ID of the compiler being extended.
	 * 
	 * @param id  The compiler's ID
	 */
	public void setCompilerId(String id);
	
	/**
	 * @return the ID of the compiler being extended.
	 */
	public String getCompilerId();
}
