/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;

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
	 * Returns the system environment. This is based on the path returned from getSystemEnvironment 
	 * 
	 * @param notifier  Used to report progress of loading the parts; this may be null.
	 * @return the system environment, never null.
	 */
	public ISystemEnvironment getSystemEnvironment(IBuildNotifier notifier);
	
	public List<ICompilerExtension> getExtensions();
	public void addExtension(ICompilerExtension extension);

	/**
	 * Returns a string representing the path (path segments separated by java.io.File.pathSeparator) to the directories containing
	 * archive file(s) (*.eglar, *.mofar). This set of directory paths is used to construct the chain of system environments. If a compiler
	 * does not wish to contribute to the set of system parts,
	 * 
	 * @return the system environment path, possibly null.
	 */
	public String getSystemEnvironmentPath();
	
	public List<ASTValidator> getValidatorsFor(Node node);
	public ElementGenerator getElementGeneratorFor(Node node);
	
}
