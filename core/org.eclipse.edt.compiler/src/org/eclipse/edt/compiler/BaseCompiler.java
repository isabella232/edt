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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
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
	 * Extensions to the compiler.
	 */
	protected List<ICompilerExtension> extensions;
	
	protected Map<Class, List<ICompilerExtension>> astTypeToExtensions;
	
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
		this.extensions = new ArrayList<ICompilerExtension>();
		this.astTypeToExtensions = new HashMap<Class, List<ICompilerExtension>>();
	}
	
	public List<ICompilerExtension> getExtensions() {
		return extensions;
	}
	
	public void addExtension(ICompilerExtension extension) {
		if (!extensions.contains(extension)) {
			extensions.add(extension);
			
			// Register the AST statement types that can be extended.
			Class[] types = extension.getExtendedTypes();
			if (types != null && types.length > 0) {
				for (int i = 0; i < types.length; i++) {
					List<ICompilerExtension> list = astTypeToExtensions.get(types[i]);
					if (list == null) {
						list = new ArrayList<ICompilerExtension>();
						astTypeToExtensions.put(types[i], list);
					}
					list.add(extension);
				}
			}
		}
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
			//TODO should contributed paths go at the beginning or end?
			StringBuilder buf = new StringBuilder(100);
			
			buf.append(SystemEnvironmentUtil.getSystemLibraryPath(ProgramImpl.class, "lib"));
			
			for (ICompilerExtension ext : extensions) {
				String[] paths = ext.getSystemEnvironmentPaths();
				if (paths != null && paths.length > 0) {
					for (int i = 0; i < paths.length; i++) {
						if (paths[i] != null && paths[i].trim().length() > 0) {
							buf.append(File.pathSeparatorChar);
							buf.append(paths[i].trim());
						}
					}
				}
			}
			
			systemEnvironmentRootPath = buf.toString();
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
	public List<ASTValidator> getValidatorsFor(Node node) {
		List<ICompilerExtension> nodeExtensions = astTypeToExtensions.get(node.getClass());
		if (nodeExtensions != null && nodeExtensions.size() > 0) {
			for (ICompilerExtension ext : nodeExtensions) {
				List<ASTValidator> validators = new ArrayList<ASTValidator>(nodeExtensions.size() + 1);
				ASTValidator validator = ext.getValidatorFor(node);
				if (validator != null) {
					validators.add(validator);
				}
				return validators;
			}
		}
		
		return null;
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		List<ICompilerExtension> nodeExtensions = astTypeToExtensions.get(node.getClass());
		if (nodeExtensions != null && nodeExtensions.size() > 0) {
			for (ICompilerExtension ext : nodeExtensions) {
				ElementGenerator generator = ext.getElementGeneratorFor(node);
				if (generator != null) {
					return generator;
				}
			}
		}
		return null;
	}
}
