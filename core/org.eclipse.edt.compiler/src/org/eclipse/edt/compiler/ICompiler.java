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
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.serialization.ObjectStore;

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
	 * @return the list of compiler extensions registered with this compiler, never null.
	 * @see ICompilerExtension
	 */
	public List<ICompilerExtension> getExtensions();
	
	/**
	 * Registers the given compiler extension with this compiler.
	 * 
	 * @param extension The compiler extension.
	 */
	public void addExtension(ICompilerExtension extension);

	/**
	 * Returns a string representing the path (path segments separated by java.io.File.pathSeparator) to the directories containing
	 * archive file(s) (*.eglar, *.mofar). This set of directory paths is used to construct the chain of system environments. If a compiler
	 * does not wish to contribute to the set of system parts,
	 * 
	 * @return the system environment path, possibly null.
	 */
	public String getSystemEnvironmentPath();
	
	/**
	 * Returns a list of validators to be run on the node. For statement nodes only one validator may be returned. For all others there
	 * can be mutliple validators. Therefore extensions can replace statement validation, and append to part, function, and type validation.
	 * 
	 * @param node The node to validate.
	 * @see ASTValidator
	 * @return a list of validators to be run on the node, possibly null.
	 */
	public List<ASTValidator> getValidatorsFor(Node node);
	
	/**
	 * Returns a generator capable of creating a MOF part for the given node. Extensions can override what type gets generated,
	 * otherwise the compiler should return its own generator capable of creating its default type for the node.
	 * 
	 * @param node The node being generated.
	 * @see ElementGenerator
	 * @return a generator capable of creating a MOF part for the given node, possibly null.
	 */
	public ElementGenerator getElementGeneratorFor(Node node);
	
	/**
	 * Returns a map whose keys are the serialization scheme (egl or mof) and whose values are the ObjectStores for
	 * the EGLAR and MOFAR files on the system environment path
	 * 
	 * @return the object stores for the system path.
	 */
	public List<ZipFileBindingBuildPathEntry> getSystemBuildPathEntries();
	
}
