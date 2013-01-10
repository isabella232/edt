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
package org.eclipse.edt.compiler;

/**
 * Base implementation of IGenerator intended to be subclassed by clients.
 */
public abstract class AbstractGenerator implements IGenerator {
	
	/**
	 * The id.
	 */
	private String id;
	
	/**
	 * The (display) name.
	 */
	private String name;
	
	/**
	 * The associated compiler.
	 */
	private ICompiler compiler;
	
	/**
	 * The generator version.
	 */
	protected String version;
	
	/**
	 * The (display) description.
	 */
	protected String description;
	
	/**
	 * The (display) provider.
	 */
	protected String provider;
	
	/**
	 * The ID of the parent generator.
	 */
	protected String parentGenId;
	
	/**
	 * The target language of this generator.
	 */
	protected String language;
	
	/**
	 * The ID of the generator with which this generator is enabled, or null if this generator determines its own enablement.
	 */
	protected String enabledWith;
	
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
	public ICompiler getCompiler() {
		return compiler;
	}
	
	@Override
	public void setCompiler(ICompiler compiler) {
		this.compiler = compiler;
	}
		
	@Override
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String getVersion() {
		return version == null ? "" : version; //$NON-NLS-1$
	}
	
	@Override
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	@Override
	public String getProvider() {
		return provider == null ? "" : provider; //$NON-NLS-1$
	}
	
	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	@Override
	public String getDescription() {
		return description == null ? "" : description; //$NON-NLS-1$
	}
	
	@Override
	public void setParentGeneratorId(String id) {
		this.parentGenId = id;
	}
	
	@Override
	public String getParentGeneratorId() {
		return parentGenId;
	}
	
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@Override
	public String getLanguage() {
		return this.language;
	}
	
	@Override
	public void setEnabledWith(String id) {
		this.enabledWith = id;
	}
	
	@Override
	public String getEnabledWith() {
		return this.enabledWith;
	}
}
