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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;

/**
 * Enables clients to run a generation request for a particular generator. The implementation
 * should take care of any setup required for the generator to run. It should also be stateless, in that
 * the same instance can be used to run any number of generations, possibly at the same time.
 */
public interface IGenerator {
	
	/**
	 * Generates a part.
	 * 
	 * @param file            Handle to the IFile from which the generation was run. Can be a source file or IR file.
	 * @param part            The IR of the part to generate.
	 * @param environment     The environment to use during generation for part lookups.
	 * @param invokedByBuild  Flag indicating if this generation is due to a build; false if from a user action.
	 */
	public void generate(IFile file, Part part, IEnvironment environment, boolean invokedByBuild);
	
	/**
	 * @return a unique identifier for this generator, which matches the ID in the plugin.xml extension.
	 */
	public String getId();
	
	/**
	 * Sets the identifier for this generator. This will be called when the class is instantiated, passing in the value from plugin.xml.
	 * 
	 * @param id  The identifier.
	 */
	public void setId(String id);
	
	/**
	 * @return the display name for this generator, never null or blank.
	 */
	public String getName();
	
	/**
	 * Sets the name of this generator (typically used for display purposes).
	 * 
	 * @param name  The name of this generator.
	 */
	public void setName(String name);
	
	/**
	 * @return the compiler to which this generator belongs.
	 */
	public ICompiler getCompiler();
	
	/**
	 * Sets the compiler for this generator.
	 * 
	 * @param compiler  The compiler.
	 */
	public void setCompiler(ICompiler compiler);
	
	/**
	 * @return true if this generator supports the given project.
	 */
	public boolean supportsProject(IProject project);
	
	/**
	 * @return an array of runtime containers provided by this generator; this may return null.
	 */
	public EDTRuntimeContainer[] getRuntimeContainers();
	
	/**
	 * Sets the version of this generator.
	 * 
	 * @param version  The generator version.
	 */
	public void setVersion(String version);
	
	/**
	 * @return the version of this generator, e.g. "1.0.0", never null.
	 */
	public String getVersion();
	
	/**
	 * Sets the provider of this generator, e.g. "Company XYZ".
	 * 
	 * @param provider  The provider.
	 */
	public void setProvider(String provider);
	
	/**
	 * @return the provider of this plug-in, e.g. "Company XYZ", never null.
	 */
	public String getProvider();
	
	/**
	 * Sets the description of this generator.
	 * 
	 * @param description  The description text.
	 */
	public void setDescription(String description);
	
	/**
	 * @return a blurb that describes this generator, never null.
	 */
	public String getDescription();
	
	/**
	 * Sets the ID of this generator's parent generator.
	 * 
	 * @param parentId  The parent's ID.
	 */
	public void setParentGeneratorId(String parentId);
	
	/**
	 * @return the ID of a parent generator, or null if this generator is not extending another generator.
	 */
	public String getParentGeneratorId();
}
