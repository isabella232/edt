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
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.impl.ProgramImpl;
import org.eclipse.edt.mof.egl.lookup.EglLookupDelegate;
import org.eclipse.edt.mof.impl.Bootstrap;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.ZipFileObjectStore;

/**
 * Base implementation of ICompiler intended to be subclassed by clients.
 */
public class BaseCompiler implements ICompiler {
	
	private static final String EDT_JAR_EXTENSION = ".eglar";
	private static final String EDT_MOF_EXTENSION = ".mofar";

	protected String systemEnvironmentRootPath;
	private List<ZipFileBindingBuildPathEntry> systemPathEntries;
	
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
			
			buf.append(SystemLibraryUtil.getSystemLibraryPath(ProgramImpl.class, "lib"));
			
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
	public List<ASTValidator> getValidatorsFor(Node node) {
		List<ICompilerExtension> nodeExtensions = astTypeToExtensions.get(node.getClass());
		if (nodeExtensions != null && nodeExtensions.size() > 0) {
			List<ASTValidator> validators = new ArrayList<ASTValidator>(nodeExtensions.size() + 1);
			for (ICompilerExtension ext : nodeExtensions) {
				ASTValidator validator = ext.getValidatorFor(node);
				if (validator != null) {
					validators.add(validator);
					
					// For statement validation, only one validator is allowed.
					if (node instanceof Statement) {
						return validators;
					}
				}
			}
			return validators;
		}
		
		return null;
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		List<ICompilerExtension> nodeExtensions = astTypeToExtensions.get(node.getClass());
		if (nodeExtensions != null && nodeExtensions.size() > 0) {
			for (ICompilerExtension ext : nodeExtensions) {
				//TODO should we issue an error if multiple extensions want to replace generation?
				ElementGenerator generator = ext.getElementGeneratorFor(node);
				if (generator != null) {
					return generator;
				}
			}
		}
		return null;
	}
	
	public List<ZipFileBindingBuildPathEntry> getSystemBuildPathEntries() {
		return getSystemBuildPathEntries(getSystemEnvironmentPath());
	}

	public List<ZipFileBindingBuildPathEntry> getSystemBuildPathEntries(String systemEnvPath) {
		
		if (systemPathEntries == null) {
			systemPathEntries = new ArrayList<ZipFileBindingBuildPathEntry>();
			String[] paths = NameUtil.toStringArray(systemEnvPath, File.pathSeparator);
			Environment env = new Environment();
			Bootstrap.initialize(env);
			env.registerLookupDelegate(Type.EGL_KeyScheme, new EglLookupDelegate());
			
			for (int i = 0; i < paths.length; i++) {
				File libfolder = new File(paths[i]);
				if (libfolder.exists() && libfolder.isDirectory()){
					File[] files = libfolder.listFiles();
					
				  	for (File file : files){
				  		if (file.isFile()) {
					  		if (file.getName().endsWith(EDT_JAR_EXTENSION)){
					  			EglarBuildPathEntry entry = new EglarBuildPathEntry(null, file.getAbsolutePath(), EGL2IR.EGLXML);
					  			ObjectStore store = new ZipFileObjectStore(file, env, ObjectStore.XML, EGL2IR.EGLXML, Type.EGL_KeyScheme, entry);
					  			entry.setStore(store);
					  			env.registerObjectStore(Type.EGL_KeyScheme, store);
					  			systemPathEntries.add(entry);
				  			}
					  		else {
						  		if (file.getName().endsWith(EDT_MOF_EXTENSION)){
						  			MofarBuildPathEntry entry = new MofarBuildPathEntry(null, file.getAbsolutePath(), ZipFileObjectStore.MOFXML);					  			
						  			ObjectStore store = new ZipFileObjectStore(file, env, ObjectStore.XML, ZipFileObjectStore.MOFXML, entry);
						  			entry.setStore(store);
						  			env.registerObjectStore(ObjectStore.DefaultScheme, store);
						  			systemPathEntries.add(entry);
						  		}
					  		}
				  		}
				  		
					 }	
					
				}
			}
		}
		return systemPathEntries;
		
	}
}
