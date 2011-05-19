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
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of ICompiler intended to be subclassed by clients.
 */
public abstract class AbstractCompiler implements ICompiler {
	
	/**
	 * The id.
	 */
	protected String id;
	
	/**
	 * The (display) name.
	 */
	protected String name;
	
	/**
	 * The generators.
	 */
	protected List<IGenerator> generators;
	
	/**
	 * The id of the preference page associated with this compiler.
	 */
	protected String preferencePageId;
	
	/**
	 * The compiler version.
	 */
	protected String version;
	
	/**
	 * Constructor.
	 */
	public AbstractCompiler() {
		this.generators = new ArrayList<IGenerator>();
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setName(String name) {
		if (name != null && name.length() == 0) { // treat blank like null
			this.name = null;
		}
		else {
			this.name = name;
		}
	}
	
	@Override
	public String getName() {
		return name == null ? id : name;
	}
	
	@Override
	public File getSystemEnvironmentRoot() {
		return null;
	}
	
	@Override
	public List<IGenerator> getGenerators() {
		return generators;
	}
	
	@Override
	public void addGenerator(IGenerator generator) {
		generators.add(generator);
	}
	
	@Override
	public void setPreferencePageId(String id) {
		this.preferencePageId = id;
	}
	
	@Override
	public String getPreferencePageId() {
		return preferencePageId;
	}
	
	@Override
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String getVersion() {
		return version == null ? "" : version; //$NON-NLS-1$
	}
}
