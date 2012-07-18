/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.impl.ProgramImpl;
import org.eclipse.edt.mof.egl.lookup.EglLookupDelegate;
import org.eclipse.edt.mof.serialization.Environment;

/**
 * Base implementation of ICompiler intended to be subclassed by clients.
 */
public class BaseCompiler implements ICompiler {	
	
	protected String systemEnvironmentRootPath;
	private ISystemEnvironment systemEnvironment;
	
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
	public BaseCompiler() {
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
			systemEnvironmentRootPath = SystemEnvironmentUtil.getSystemLibraryPath(ProgramImpl.class, "lib");
		}
		return systemEnvironmentRootPath;
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
	public synchronized ISystemEnvironment getSystemEnvironment(IBuildNotifier notifier) {
		if (systemEnvironment == null) {
			systemEnvironment = createSystemEnvironment(notifier);
		}
		return systemEnvironment;
	}
	
	protected ISystemEnvironment createSystemEnvironment(IBuildNotifier notifier) {
		SystemEnvironment sysEnv = new SystemEnvironment(Environment.getCurrentEnv(), null, getAllImplicitlyUsedEnumerations(), this);
    	Environment.getCurrentEnv().registerLookupDelegate(Type.EGL_KeyScheme, new EglLookupDelegate());
		sysEnv.initializeSystemPackages(getSystemEnvironmentPath(), new SystemPackageBuildPathEntryFactory(), notifier);
		return sysEnv;
	}
	
	@Override
	public List<String> getImplicitlyUsedEnumerations() {
		return new ArrayList<String>();
	}
	
	@Override
	public List<String> getAllImplicitlyUsedEnumerations() {
		return getImplicitlyUsedEnumerations();
	}

	@Override
	public StatementValidator getValidatorFor(Statement stmt) {
		return null;
	}
	
	@Override
	public PartValidator getValidatorFor(Part part) {
		return null;
	}
}
