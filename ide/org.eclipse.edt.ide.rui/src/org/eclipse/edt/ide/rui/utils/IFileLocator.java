/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.utils;

import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import org.eclipse.edt.compiler.internal.eglar.EglarFile;
import org.eclipse.edt.compiler.internal.eglar.EglarFileCache;
import org.eclipse.edt.compiler.internal.eglar.EglarManifest;
import org.eclipse.edt.ide.core.utils.EGLProjectFileUtility;
//TODO EDT eglar
//import org.eclipse.edt.compiler.internal.eglar.EglarFileResource;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;


/**
 * A copy of FIleLocator that returns an IFile. The <code>Util.findIFile</code> method
 * now calls into this class and no longer has to fiddle with strings to convert from
 * a File back to an IFile
 *
 *
 */
public abstract class IFileLocator {

	protected static final String WEB_CONTENT = "WebContent";
	private List eglProjectPath;
	private String[] resourceLocations;
	private IProject project;
	
	public IFileLocator(IProject project)throws CoreException{
		this.project = project;
		this.eglProjectPath = org.eclipse.edt.ide.core.internal.utils.Util.getEGLProjectPath(project);
		resourceLocations = initResourceLocations(project);
	}
	
	protected IFileLocator(){
	}
	
	public IFile findFile(String name){
		String[] resourceLocations = getResourceLocations();
		for (Iterator iter1 = eglProjectPath.iterator(); iter1.hasNext();) {
			IEGLProject eglProject = (IEGLProject)iter1.next();
			for (int i = 0; i < resourceLocations.length; i++) {
				String location = resourceLocations[i];
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(eglProject.getPath().append(location).append(name));
				if(file.exists()){
					return file;
				}
			}
		}
//TODO EDT eglar		
//		try {
//			IEGLProject eglProject = EGLCore.create(project);
//			IEGLPathEntry[] entries = eglProject.getRawEGLPath();
//			for ( int j = 0; j < entries.length; j ++ ) {
//				IEGLPathEntry entry = entries[j];
//				if ( entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY && "eglar".equalsIgnoreCase(entry.getPath().getFileExtension()) ) {
//					for (int i = 0; i < resourceLocations.length; i++) {
//						String location = resourceLocations[i];
//						EglarFile eglar = EglarFileCache.instance.getEglarFile(EGLProjectFileUtility.getEglarAbsolutePath(entry.getPath(), eglProject.getProject()));
//						EglarManifest manifest = eglar.getManifest();
//						if ( manifest == null ) {
//							continue;
//						}
//						ZipEntry zipentry = eglar.getEntry( location + IPath.SEPARATOR +  name );
//						if(zipentry != null){
//							return new EglarFileResource( eglar, zipentry, "", project );
//						}					
//					}
//				}
//			}
//		} catch ( Exception e ) {
//			//e.printStackTrace();
//			//do nothing
//		}
		return null;
	}
	
	protected abstract String[] initResourceLocations(IProject project)throws CoreException;
	protected String[] getResourceLocations()
	{
		return resourceLocations;
	}
}
