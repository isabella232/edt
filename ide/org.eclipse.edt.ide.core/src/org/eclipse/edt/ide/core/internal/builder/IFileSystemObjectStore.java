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
package org.eclipse.edt.ide.core.internal.builder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.AbstractObjectStore;
import org.eclipse.edt.mof.serialization.CachingObjectStore;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Deserializer;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.SerializationException;
import org.eclipse.edt.mof.serialization.ZipFileObjectStore;

public class IFileSystemObjectStore extends AbstractObjectStore implements CachingObjectStore {
	private static final boolean DEBUG = false;
	
	private Map<String,EObject> cache;
	
	IPath root;
	String fileExtension;
	
	public IFileSystemObjectStore(IPath root, IEnvironment env) {
		super(env);
		this.root = root;
		this.cache = new HashMap<String, EObject>();
	}
	
	public IFileSystemObjectStore(IPath root, IEnvironment env, String storageFormat) {
		this(root, env, storageFormat, ObjectStore.XML.equals(storageFormat) ? ZipFileObjectStore.MOFXML : ZipFileObjectStore.MOFBIN);
	}

	public IFileSystemObjectStore(IPath root, IEnvironment env, String storageFormat, String fileExtension) {
		super(env, storageFormat, EGL2IR.EGLXML.equals(fileExtension) ? Type.EGL_KeyScheme : ObjectStore.DefaultScheme);
		this.root = root;
		this.fileExtension = fileExtension;
		this.cache = new HashMap<String, EObject>();
	}
	
	public Deserializer createDeserializer(String typeSignature) {
		try {			
			IPath path = root.append(typeSignature.toUpperCase().toLowerCase().replace('.', '/') + getFileExtension());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			
			if (file.exists()) {
				BufferedInputStream inputStream;
				try {
					inputStream = new BufferedInputStream(file.getContents());
					return factory.createDeserializer(inputStream, env);
				}
				catch(CoreException e) {
					throw new BuildException("CoreException", e); //$NON-NLS-1$
				}
			}
		}
		catch(Exception e) {
		}
		return null;
	}

	@Override
	public void primRemove(String key) {
		// key already has the scheme removed
		String normKey = key.toUpperCase().toLowerCase();
		cache.remove(normKey);
	}
	
	@Override
	public void put(String key, EObject obj) throws SerializationException {
		super.put(key, obj);
		
		if (obj != null) {
			String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
			cache.put(normKey, obj);
		}
	}

	@Override
	public void store(String typeSignature, Object obj) {
		if (!(obj instanceof byte[])) {
			throw new IllegalArgumentException("Object not of type: byte[]");
		}
		
		byte[] entry = (byte[])obj;
		InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(entry));
		try {
			IFile file = getOutputFileForWrite(typeSignature);
			if (file.exists()) {
				// Deal with shared output folders... last one wins... no collision cases detected
			    if (DEBUG) {
			        System.out.println("Writing changed file " + file.getName());//$NON-NLS-1$
			    }			    
			    if (entry.length > 0) {
			    	file.setContents(inputStream, true, false, null);
					if (!file.isDerived()) {
						file.setDerived(true, null);
					}
			    }
			    else {
			    	file.delete(true,null);
			    }
			}
			else if (entry.length > 0) {
				// Default implementation just writes out the bytes for the new build file...
			    if(DEBUG){
			        System.out.println("Writing new file " + file.getName());//$NON-NLS-1$
			    }
			    file.create(inputStream, IResource.FORCE, null);
			    file.setDerived(true, null);
			}
		}
		catch(CoreException e) {
			throw new SerializationException(e);
		}
		finally {
			try {
				inputStream.close();
			}
			catch(IOException e) {
				throw new SerializationException(e);
			}
		}
	}
	
	private IFile getOutputFileForWrite(String typeSignature) throws CoreException {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		String relativeFilePath = typeSignature.replace('.', '/') + getFileExtension();
		int lastSlash = relativeFilePath.lastIndexOf( '/' );
		if ( lastSlash != -1 )
		{
			// Make sure parent folders are created.
			Util.createFolder(new Path(relativeFilePath.substring(0, lastSlash)), workspaceRoot.getFolder(root));
		}
		
    	return workspaceRoot.getFile(root.append(relativeFilePath));
    }

	@Override
	public boolean containsKey(String key) {
		key = removeSchemeFromKey(key);
		IPath path = root.append(key.replace('.', '/') + getFileExtension());
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		return file.exists();
	}
	
	@Override
	public long lastModified(String key) {
		key = removeSchemeFromKey(key);
		IPath path = root.append(key.replace('.', '/') + getFileExtension());
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		return file.getModificationStamp();
	}
	
	public String getFileExtension() {
		if (fileExtension == null) {
			fileExtension = BINARY.equals(storageFormat) ? ZipFileObjectStore.MOFBIN : ZipFileObjectStore.MOFXML;
		}
		return fileExtension;
	}
	
	@Override
	public EObject get(String key) throws DeserializationException {
		String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
		EObject value = cache.get(normKey);
		if (value == null) {
			value = super.get(key);
			if (value != null) {
				cache.put(normKey, value);
			}
		}
		return value;
	}

	@Override
	public EObject getFromCache(String key) {
		String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
		return cache.get(normKey);
	}
	
	@Override
	public void addToCache(String key, EObject object) {
		String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
		cache.put(normKey, object);
	}

	@Override
	public void clearCache() {
		cache.clear();
	}
	
	public List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs) {
		if (!containsPkg(pkg)) {
			return new ArrayList<String>();
		}
		IPath path = root.append(pkg.replace('.', '/'));
		IFolder pkgDir = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		return getAllKeysFromPkg(pkgDir, pkg, includeSubPkgs);
		
	}
	
	private List<String> getAllKeysFromPkg(final IFolder pkgDir, final String pkg,  final boolean includeSubPkgs) {

		final List<String> list = new ArrayList<String>();
		try {
			pkgDir.accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					
					if (resource.equals(pkgDir)) {
						return true;
					}
					
					if (resource.getType() == IResource.FOLDER) {
						if (includeSubPkgs) {
							String subPkgName;
							if (pkg.length() > 0) {
								subPkgName = pkg + "." + resource.getName();
							}
							else {
								subPkgName = resource.getName();
							}
							list.addAll(getAllKeysFromPkg((IFolder)resource, subPkgName, includeSubPkgs));
						}
					}
					else {
						if (resource.getType() == IResource.FILE) {
							if (getFileExtension().equals(getFileExtension((IFile)resource))) {
								if (pkg.length() > 0)
									list.add(getScheme() + pkg + "." + getFileName((IFile)resource));
								else
									list.add(getScheme() + getFileName((IFile)resource));						
							}
						}
					}
					return false;
				}
			});
		} catch (CoreException e) {
			e.printStackTrace();
		}
						
		return list;
	}
	
	private String getFileExtension(IFile file) {
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if (index < 0) {
			return null;
		}
		
		return name.substring(index);
	}

	private String getFileName(IResource file) {
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if (index < 0) {
			return name;
		}
		
		return name.substring(0, index);
	}
	
	private boolean containsPkg(String pkg) {
		IPath path = root.append(pkg.replace('.', '/'));
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		return folder.exists();
	}
		
	private String getScheme() {
		if (getFileExtension().equals(ZipFileObjectStore.MOFBIN) || getFileExtension().equals(ZipFileObjectStore.MOFXML))  {
			return "";
		}
		else {
			return "egl:";
		}
	}
	
	@Override
	public String toString() {
		// For easier debugging.
		return "IFileSystemObjectStore root=" + root;
	}
}
