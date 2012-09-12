/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.io.IIOBufferReader;
import org.eclipse.edt.compiler.internal.io.IIOBufferWriter;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectIREnvironment;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.SerializationException;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author winghong
 */
public class BinaryFileManager {
	
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
							bufferFile.setDerived(true, null);
				    }else{
				    	bufferFile.delete(true,null);
				    }
				} else if(entry.length > 0){
					// Default implementation just writes out the bytes for the new build file...
				    if(DEBUG){
				        System.out.println("Writing new file " + bufferFile.getName());//$NON-NLS-1$
				    }
					bufferFile.create(inputStream, IResource.FORCE, null);
					bufferFile.setDerived(true, null);
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
				entries.add(NameUtile.getAsName(path.toString())); // Intern entry names, which in this case are part names
			}
		
			return entries;
		}

		public InputStream getInputStream(String name)throws IOException{
			throw new UnsupportedOperationException();
		}
	}

	private static BinaryFileManager INSTANCE = new BinaryFileManager();
	   
    private BinaryFileManager() {
        super();
    }

    public static BinaryFileManager getInstance() {
        return INSTANCE;
    }

    private IFile[] getOutputFileForRead(String packageName, String partName, IProject project){
    	try{
    		IContainer output = ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getOutputLocation();
    		IPath pathWithoutExt = new Path(IRFileNameUtility.toIRFileName(packageName).replace('.', '/')).append(IRFileNameUtility.toIRFileName(partName));
    		return new IFile[] {
    				output.getFile(pathWithoutExt.addFileExtension("eglxml")),
    				output.getFile(pathWithoutExt.addFileExtension("eglbin")),
    				output.getFile(pathWithoutExt.addFileExtension("mofxml")),
    				output.getFile(pathWithoutExt.addFileExtension("mofbin"))
    		};
    	}catch(Exception e){
    		throw new BuildException(e);
    	}
    
    }
    
    public void removePart(String packageName, String name, IProject project){
    	removePart(packageName, name, project, true);
    }
    
    public void removePart(String packageName, String name, IProject project, boolean deleteFromDisk){
    	if (deleteFromDisk) {
	   		IFile[] files = getOutputFileForRead(packageName, name, project);
	   		
	   		// Remove from the disk
	   		for (IFile file : files) {
	   			if (file.exists()) {
			   		Writer writer = new Writer(ResourcesPlugin.getWorkspace().getRoot().getFile(file.getFullPath()));
			   		writer.allEntriesRemoved();
	   			}
	   		}
    	}
    	
	    // remove this part from the part cache
    	ProjectIREnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project).getIREnvironment();
    	env.remove(eglIRKey(packageName, name));
    	env.remove(mofIRKey(packageName, name));
    }
    
    public static String eglIRKey(String packageName, String partName) {
		StringBuilder buf = new StringBuilder();
		buf.append(Type.EGL_KeyScheme);
		buf.append(Type.KeySchemeDelimiter);
		if (packageName != null && packageName.length() > 0) {
			buf.append(packageName);
			buf.append('.');
		}
		buf.append(partName);
		return buf.toString();
	}
    
    public static String mofIRKey(String packageName, String partName) {
		StringBuilder buf = new StringBuilder();
		if (packageName != null && packageName.length() > 0) {
			buf.append(packageName);
			buf.append('.');
		}
		buf.append(partName);
		return buf.toString();
	}
    
    public void addPackage(String packages,IProject project){
    	IContainer container = ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getOutputLocation();
    	try {
    		org.eclipse.edt.ide.core.internal.builder.Util.createFolder(new Path(IRFileNameUtility.toIRFileName(packages).replace('.', '/')),container);
		} catch (CoreException e) {
			throw new BuildException(e);
		}    	
    }
    
    public void removePackage(String packages,IProject project){
    	IContainer container = ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getOutputLocation();
    	container = container.getFolder(new Path(IRFileNameUtility.toIRFileName(packages).replace('.', '/')));
    	if (container.exists() && container.getType() != IResource.PROJECT){ // if somehow using the project root, don't delete it
    		try {
    			container.delete(true,null);
			} catch (CoreException e) {
				throw new BuildException(e);
			}
    	}    	
    }
}
