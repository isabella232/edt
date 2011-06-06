/*******************************************************************************
 * Copyright © 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.binding;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.ISystemPackageBuildPathEntryFactory;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.compiler.internal.io.IIOBufferReader;
import org.eclipse.edt.compiler.internal.io.IIOBufferWriter;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.ide.core.ICompiler;
import org.eclipse.edt.ide.core.internal.builder.IDEEnvironment;
import org.eclipse.edt.ide.core.internal.builder.IFileSystemObjectStore;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.ZipFileBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.compiler.EGL2IR;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.SerializationException;
import org.eclipse.edt.mof.serialization.ZipFileObjectStore;

/**
 * @author winghong
 */
public class BinaryFileManager implements IResourceChangeListener {
	
	private static class ObjectStoreCache {
		private HashMap<IPath, List<ObjectStore>> storeMap = new HashMap();
		
		public void registerObjectStores(IProject project, IDEEnvironment env, List<IProject> seen) {
			if (seen.contains(project)) {
				return;
			}
			
			boolean isInitialProject = seen.size() == 0;
			seen.add(project);
			
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
			
			// output directory
			IPath path = buildPath.getOutputLocation().getFullPath(); //TODO update this when we support multiple bin dirs
			List<ObjectStore> stores = storeMap.get(path);
			if (stores == null) {
				stores = new ArrayList();
				storeMap.put(path, stores);
				
				ObjectStore store = new IFileSystemObjectStore(path, env, ObjectStore.XML);
				stores.add(store);
				env.registerObjectStore(IEnvironment.DefaultScheme, store);
				
				store = new IFileSystemObjectStore(path, env, ObjectStore.XML, EGL2IR.EGLXML);
				stores.add(store);
				env.registerObjectStore(Type.EGL_KeyScheme, store);
	        }
	        else {
	        	for (Iterator<ObjectStore> it = stores.iterator(); it.hasNext();) {
	        		ObjectStore store = it.next();
	        		store.setEnvironment(env);
	        		env.registerObjectStore(store.getKeyScheme(), store);
	        	}
	        }
			env.addRoot(path.toFile());
			
			if (isInitialProject) {
				for (ObjectStore store : stores) {
					env.setDefaultSerializeStore(store.getKeyScheme(), store);
				}
			}
			
			try {
				IEGLProject eglProject = EGLCore.create(project);
				IEGLPathEntry[] entries = eglProject.getResolvedEGLPath(true);
				
				for (int i = 0; i < entries.length; i++) {
					// add source entries and entried for project
					switch (entries[i].getEntryKind()) {
						case IEGLPathEntry.CPE_SOURCE:
							path = entries[i].getPath();
							stores = storeMap.get(path);
				        	if (stores == null) {
				        		stores = new ArrayList();
				        		storeMap.put(path, stores);
				        		
				        		ObjectStore store = new IFileSystemObjectStore(path, env, ObjectStore.XML, EGL2IR.EGLXML);
						        stores.add(store);
						        env.registerObjectStore(Type.EGL_KeyScheme, store);
				        	}
				        	else {
				        		for (Iterator<ObjectStore> it = stores.iterator(); it.hasNext();) {
				        			ObjectStore store = it.next();
					        		store.setEnvironment(env);
					        		env.registerObjectStore(store.getKeyScheme(), store);
					        	}
				        	}
				        	env.addRoot(path.toFile());
							break;
							
						case IEGLPathEntry.CPE_LIBRARY:
							path = entries[i].getPath();
							stores = storeMap.get(path);
				        	if (stores == null) {
				        		stores = new ArrayList();
				        		storeMap.put(path, stores);
				        		
				        		String extension = path.lastSegment().endsWith(ISystemPackageBuildPathEntryFactory.EDT_JAR_EXTENSION)
			        					? EGL2IR.EGLXML : ZipFileObjectStore.MOFXML;
			        			IZipFileEntryManager entry = ZipFileBuildPathEntryManager.getInstance().getZipFileBuildPathEntry(project, path);
			        			ObjectStore store = new ZipFileObjectStore(path.toFile(), env, ObjectStore.XML, extension, entry);
						        stores.add(store);
						        env.registerObjectStore(store.getKeyScheme(), store);
				        	}
				        	else {
				        		for (Iterator<ObjectStore> it = stores.iterator(); it.hasNext();) {
				        			ObjectStore store = it.next();
					        		store.setEnvironment(env);
					        		env.registerObjectStore(store.getKeyScheme(), store);
					        	}
				        	}
				        	env.addRoot(path.toFile());
							break;
							
						case IEGLPathEntry.CPE_PROJECT:
							if (isInitialProject || entries[i].isExported()) {
								IResource dependency = ResourcesPlugin.getWorkspace().getRoot().findMember(entries[i].getPath());
								if (dependency instanceof IProject) {
									registerObjectStores((IProject)dependency, env, seen);
								}
							}
							break;
					}
				}
			}
			catch (CoreException ce) {
			}
		}
		
		public void removeAllStores(IProject project) {
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
			storeMap.remove(buildPath.getOutputLocation().getFullPath()); //TODO update this when we support multiple bin dirs
			try {
				IEGLProject eglProject = EGLCore.create(project);
				IPackageFragmentRoot[] packageFragmentRoots = eglProject.getPackageFragmentRoots();
				for (int i = 0; i < packageFragmentRoots.length; i++) {
					storeMap.remove(packageFragmentRoots[i].getPath());
				}
			}
			catch (CoreException ce) {
			}
		}
		
		public void removeObsoleteStores(IProject project) {
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
			
			List<IPath> pathsToKeep = new ArrayList();
			pathsToKeep.add(buildPath.getOutputLocation().getFullPath()); //TODO update this when we support multiple bin dirs
			try {
				IEGLProject eglProject = EGLCore.create(project);
				IPackageFragmentRoot[] packageFragmentRoots = eglProject.getPackageFragmentRoots();
				for (int i = 0; i < packageFragmentRoots.length; i++) {
					pathsToKeep.add(packageFragmentRoots[i].getPath());
				}
			}
			catch (CoreException ce) {
			}
			
			String projectName = project.getName();
			for (Iterator<Map.Entry<IPath, List<ObjectStore>>> it = storeMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry<IPath, List<ObjectStore>> next = it.next();
				IPath nextPath = next.getKey();
				if (projectName.equals(nextPath.segment(0))) {
					// Belongs to this project. Check if it's still in the build path.
					if (!pathsToKeep.contains(nextPath)) {
						it.remove();
					}
				}
			}
		}
	}
	
	private static class EnvironmentCache {
		private final HashMap<IProject, IDEEnvironment> projectMap = new HashMap();
		private final ObjectStoreCache storeCache = new ObjectStoreCache();
		
		public IDEEnvironment getEnvironment(IProject project) {
			IDEEnvironment env = projectMap.get(project);
			if (env == null) {
				env = new IDEEnvironment(project);
				projectMap.put(project, env);
				
		        // 1. Register object stores for the project and its dependencies.
		        storeCache.registerObjectStores(project, env, new ArrayList());
		        
		        // 2. Register object stores for system environments.
		        ICompiler compiler = ProjectSettingsUtility.getCompiler(project);		        
		        if (compiler != null) {
		        	 env.appendStores(compiler.getSystemEnvironment().getStores());
		        }
			}
			return env;
		}
		
		public EObject get(IProject project, String[] packageName, String partName) throws DeserializationException, MofObjectNotFoundException {
			IDEEnvironment env = getEnvironment(project);
			return env.find(key(packageName, partName));
		}
		
		public IPartBinding getPartBinding(IProject project, String[] packageName, String partName) {
			IDEEnvironment env = getEnvironment(project);
			return env.getPartBinding(packageName, partName);
		}
		
		public void save(IProject project, MofSerializable part) throws SerializationException {
			getEnvironment(project).save(part, true);
		}
		
		public void remove(IProject project, String[] packageName, String partName){
			IDEEnvironment env = projectMap.get(project);
			if (env != null) {
				env.remove(key(packageName, partName));
			}
		}
		
		public void remove(IProject project){
			IDEEnvironment env = projectMap.remove(project);
			
			if (env != null) {
				// Null out the environment in the stores that had this environment set.
				for (Iterator<List<ObjectStore>> it = storeCache.storeMap.values().iterator(); it.hasNext();) {
					for (Iterator<ObjectStore> stores = it.next().iterator(); stores.hasNext();) {
						ObjectStore store = stores.next();
						if (store.getEnvironment() == env) {
							store.setEnvironment(null);
						}
					}
				}
			}
		}
		
		public void clear(){
			projectMap.clear();
			
			// Null-out all environments in the stores.
			for (Iterator<List<ObjectStore>> it = storeCache.storeMap.values().iterator(); it.hasNext();) {
				for (Iterator<ObjectStore> stores = it.next().iterator(); stores.hasNext();) {
					stores.next().setEnvironment(null);
				}
			}
		}
		
		private String key(String[] packageName, String partName) {
			StringBuilder buf = new StringBuilder();
			buf.append(Type.EGL_KeyScheme);
			buf.append(Type.KeySchemeDelimiter);
			if (packageName != null && packageName.length > 0) {
				buf.append(IRUtils.concatWithSeparator(packageName, "."));
				buf.append('.');
			}
			buf.append(partName);
			return buf.toString();
		}
	}
	
	public static class Writer implements IIOBufferWriter {
		
		private static final boolean DEBUG = false;
	    
		private byte[] entry;
		private IFile bufferFile;
		
		public Writer(IFile bufferFile) {
			this.bufferFile = bufferFile;
		}
		
		public void beginWriting() throws IOException {
			entry = null;
		}
		
		public void writeEntry(String entryName, Object value) throws IOException, SerializationException {
			this.entry = (byte[])value;
		}

		public void finishWriting() throws IOException {
			InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(entry));
			
			try{
				if (bufferFile.exists()) {
					// Deal with shared output folders... last one wins... no collision cases detected
				    if(DEBUG){
				        System.out.println("Writing changed file " + bufferFile.getName());//$NON-NLS-1$
				    }			    
				    if (entry.length > 0){
				    	bufferFile.setContents(inputStream, true, false, null);
						if (!bufferFile.isDerived())
							bufferFile.setDerived(true);
				    }else{
				    	bufferFile.delete(true,null);
				    }
				} else if(entry.length > 0){
					// Default implementation just writes out the bytes for the new build file...
				    if(DEBUG){
				        System.out.println("Writing new file " + bufferFile.getName());//$NON-NLS-1$
				    }
					bufferFile.create(inputStream, IResource.FORCE, null);
					bufferFile.setDerived(true);
				}
			}catch(CoreException e){
				throw new IOException("EclipseZipFileIOBufferWriter::finishWriting"); //$NON-NLS-1$
			}finally{
				inputStream.close();
			}
		}

		public void allEntriesRemoved() {
			try{
			    if(DEBUG){
					System.out.println("Removing file " + bufferFile.getName());//$NON-NLS-1$
			    }
			    if(bufferFile.exists()){
			    	bufferFile.delete(true, false, null);
			    }
			}catch(CoreException e){
				throw new BuildException(e);
			}
		}
	}
	
	public static class Reader implements IIOBufferReader {

		private IFile bufferFile;
		
		public Reader(IFile bufferFile) {
			this.bufferFile = bufferFile;
		}

		public Object readEntry(String name) throws IOException {
			Object result = null;
			
			if(bufferFile.exists()){
				BufferedInputStream inputStream;
				try{
					inputStream = new BufferedInputStream(bufferFile.getContents());
					try{
						byte[] contents = new byte[inputStream.available()];
						inputStream.read(contents);
						result = contents;
					}finally{
						inputStream.close();
					}
				}catch (CoreException e) {
					throw new BuildException("CoreException", e); //$NON-NLS-1$
				}
			}
			
			return result;
		}

		public List getEntries() throws IOException {
			// Always one entry with the name of the part, which is the name of the file
			ArrayList entries = new ArrayList();
			if(bufferFile.exists()){
				String name =new String(bufferFile.getName());
				IPath path = new Path(name);
				path = path.removeFileExtension();
				entries.add(InternUtil.intern(path.toString())); // Intern entry names, which in this case are part names
			}
		
			return entries;
		}

		public InputStream getInputStream(String name)throws IOException{
			throw new UnsupportedOperationException();
		}
	}

	private static BinaryFileManager INSTANCE = new BinaryFileManager();
	private final EnvironmentCache environmentCache;
	   
    private BinaryFileManager() {
        super();
        
        this.environmentCache = new EnvironmentCache();
    }

    public static BinaryFileManager getInstance() {
        return INSTANCE;
    }

    private IFile getOutputFileForRead(String[] packageName, String partName, IProject project){
    	try{
    		return ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getOutputLocation().getFile(Util.stringArrayToPath(IRFileNameUtility.toIRFileName(packageName)).append(IRFileNameUtility.toIRFileName(partName)).addFileExtension("ir"));
    	}catch(Exception e){
    		throw new BuildException(e);
    	}
    
    }
    
    public void close(IProject project){
    	environmentCache.remove(project);
    }
    
    public IPartBinding readPartBinding (String[] packageName,String name,IProject project){
    	try{
    		return environmentCache.getPartBinding(project, packageName, name);
	    }catch(Exception e){
    		throw new PartRestoreFailedException(packageName,name,e);
    	}
    }   

    public Part readPart(String[] packageName, String name, IProject project){
    	try {
    		Object part = environmentCache.get(project, packageName, name);
    		if (part instanceof Part) {
    			return (Part)part;
    		}
    		return null;
    	}
    	catch (Exception e) {
    		throw new BuildException(e.getMessage(), e);
    	}
    }
    
    public void write(MofSerializable object, String[] packageName, String partName, IProject project){
    	IPartOrigin origin = ProjectInfoManager.getInstance().getProjectInfo(project).getPartOrigin(packageName, partName);
		if (origin.isOriginEGLFile()){
			try {
				environmentCache.save(project, object);
			}
			catch (SerializationException e) {
				throw new BuildException("IOException", e);
			}
		}
	}
    
    public void removePart(String[] packageName, String name, IProject project){
    	removePart(packageName, name, project, true);
    }
    
    public void removePart(String[] packageName, String name, IProject project, boolean deleteFromDisk){
    	if (deleteFromDisk) {
	   		IPath path = getOutputFileForRead(packageName, name, project).getFullPath();
	   		
	   		// Remove from the disk
	   		Writer writer = new Writer(ResourcesPlugin.getWorkspace().getRoot().getFile(path));
	   		writer.allEntriesRemoved();
    	}
    	
	    // remove this part from our part cache
	    environmentCache.remove(project, packageName, name);
    }
    
    public void addPackage(String[] packages,IProject project){
    	IContainer container = ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getOutputLocation();
    	try {
    		org.eclipse.edt.ide.core.internal.builder.Util.createFolder(Util.stringArrayToPath(IRFileNameUtility.toIRFileName(packages)),container);
		} catch (CoreException e) {
			throw new BuildException(e);
		}    	
    }
    
    public void removePackage(String[] packages,IProject project){
    	IContainer container = ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getOutputLocation();
    	container = container.getFolder(Util.stringArrayToPath(IRFileNameUtility.toIRFileName(packages)));
    	if (container.exists()){
    		try {
    			container.delete(true,null);
			} catch (CoreException e) {
				throw new BuildException(e);
			}
    	}    	
    }
    
    public void remove(IProject project){
    	environmentCache.storeCache.removeAllStores(project);
    	environmentCache.remove(project);
    }
    
    public void clean(IProject project){
    	environmentCache.storeCache.removeObsoleteStores(project);
    	environmentCache.remove(project);
     }
    
    public static IFile getOutputFileForWrite(String[] packageName, String partName, IProject project) throws CoreException{
    	IContainer outputLocation = ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getOutputLocation();
    	IPath packagePath = Util.stringArrayToPath(IRFileNameUtility.toIRFileName(packageName));
    	
    	IContainer container = outputLocation;
    	if (packagePath.segmentCount() > 0) {
    		container = org.eclipse.edt.ide.core.internal.builder.Util.createFolder(packagePath, outputLocation);
    	}
    	
    	IPath filePath = new Path(IRFileNameUtility.toIRFileName(partName)).addFileExtension("ir");
    	return container.getFile(filePath);
    }

    /**
     * As a saftey precation, clean out the part cache before we build.
     * 
     * To reduce the amount of memory used, clean out the part cache after a build.
     */
    public void resourceChanged(IResourceChangeEvent event) {
		switch(event.getType()){
			case IResourceChangeEvent.POST_BUILD:
			case IResourceChangeEvent.PRE_BUILD:
				environmentCache.clear();
				break;
			default:
		}		
	}
    
    public IDEEnvironment getEnvironment(IProject project) {
    	return environmentCache.getEnvironment(project);
    }
}

