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
package org.eclipse.edt.ide.core;

import java.io.File;
import java.util.List;

/**
 * Enables clients to define a compiler. A compiler may contribute to the System parts used during compilation.
 */
public interface ICompiler {
	
	/**
	 * @return a unique identifier for this compiler, which matches the ID in the plugin.xml extension.
	 */
	public String getId();
	
	/**
	 * Sets the identifier for this compiler. This will be called when the class is instantiated, passing in the value from plugin.xml.
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
	 * Returns an optional system environment to be appended to the core system environment. The file should
	 * be a handle to the directory containing any archives (*.eglar, *.mofar).
	 * 
	 * @return the system environment path, possibly null.
	 */
	public File getSystemEnvironmentRoot();
	
	/**
	 * @return the generators registered with this compiler, never null.
	 */
	public List<IGenerator> getGenerators();
	
	/**
	 * Adds a generator to be used with this compiler.
	 * 
	 * @param generator  The generator.
	 */
	public void addGenerator(IGenerator generator);
	
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
}
