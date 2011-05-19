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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.compiler.EGL2IR;
import org.eclipse.edt.mof.serialization.AbstractObjectStore;
import org.eclipse.edt.mof.serialization.Deserializer;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.SerializationException;

public class IFileSystemObjectStore extends AbstractObjectStore {
	private static final boolean DEBUG = false;
	
	static final String MOFBIN = ".mofbin";
	static final String MOFXML = ".mofxml"; 
	
	IPath root;
	String fileExtension;
	
	public IFileSystemObjectStore(IPath root, IEnvironment env) {
		super(env);
		this.root = root;
	}
	
	public IFileSystemObjectStore(IPath root, IEnvironment env, String storageFormat) {
		super(env, storageFormat);
		this.root = root;
		this.fileExtension = storageFormat == ObjectStore.XML ? MOFXML : MOFBIN;
	}

	public IFileSystemObjectStore(IPath root, IEnvironment env, String storageFormat, String fileExtension) {
		super(env, storageFormat);
		this.root = root;
		this.fileExtension = fileExtension;
		this.supportedScheme = fileExtension.equals(EGL2IR.EGLXML) ? Type.EGL_KeyScheme : ObjectStore.DefaultScheme;
	}
	
	public Deserializer createDeserializer(String typeSignature) {
		try {
			IPath path = root.append(typeSignature.replace('.', '/') + getFileExtension());
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
						file.setDerived(true);
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
			    file.setDerived(true);
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
			fileExtension = storageFormat.equals(BINARY) ? MOFBIN : MOFXML;
		}
		return fileExtension;
	}
}
