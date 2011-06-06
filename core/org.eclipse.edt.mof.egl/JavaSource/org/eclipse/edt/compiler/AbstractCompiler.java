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
package org.eclipse.edt.compiler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.compiler.SystemPackageBuildPathEntryFactory;
import org.eclipse.edt.mof.egl.lookup.EglLookupDelegate;
import org.eclipse.edt.mof.egl.mof2binding.Mof2Binding;
import org.eclipse.edt.mof.serialization.Environment;

/**
 * Base implementation of ICompiler intended to be subclassed by clients.
 */
public abstract class AbstractCompiler implements ICompiler {	
	
	protected String systemEnvironmentRootPath;
	protected ISystemEnvironment systemEnvironment;
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
	public String getSystemEnvironmentPath() {
		if (systemEnvironmentRootPath == null) {
			//TODO after refactoring the compiler, the locator class should be this class!
			systemEnvironmentRootPath = SystemEnvironmentUtil.getSystemLibraryPath(BindingCreator.class, "lib");
		}
		return systemEnvironmentRootPath;
	}
	
	@Override
	public void setSystemEnvironmentPath(String path) {
		this.systemEnvironmentRootPath = path;
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
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String getVersion() {
		return version == null ? "" : version; //$NON-NLS-1$
	}
		
	@Override
	public ISystemEnvironment getSystemEnvironment() {
		if (systemEnvironment == null) {
			systemEnvironment = createSystemEnvironment();
		}
		return systemEnvironment;
	}
	
	protected ISystemEnvironment createSystemEnvironment() {
		SystemEnvironment sysEnv = new SystemEnvironment(Environment.INSTANCE, null);
    	Environment.INSTANCE.registerLookupDelegate(Type.EGL_KeyScheme, new EglLookupDelegate());
		sysEnv.initializeSystemPackages(getSystemEnvironmentPath(), new SystemPackageBuildPathEntryFactory(new Mof2Binding(sysEnv)));
		return sysEnv;
	}
}
