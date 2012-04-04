/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.eglar.FileInEglar;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragment;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;

/**
 * A <tt>IFilePartInfo</tt> represents a type in a class or java file.
 */
public class IFilePartInfo extends PartInfo {
	
	private final String fProject;
	private final String fFolder;
	private final String fFile;
	private final String fExtension;
	private final char fPartType;
	
	public static final int EGLAR_FILE = 1;
	public static final int EGL_FILE = 2;
	
	//fContainerLoc: where the eglar is located, for external eglar, it is the folder 
	//containing the eglar; for non-external eglar, it is the project containing the eglar
	private final String fContainerLoc;	
	private boolean isExternal;
	
	public IFilePartInfo(String pkg, String name, char[][] enclosingTypes, String project, String sourceFolder, String file, String extension, char partType) {
		super(pkg, name, enclosingTypes);
		fProject= project;
		fFolder= sourceFolder;
		fFile= file;
		fExtension= extension;
		fPartType = partType;
		fContainerLoc = null;
		isExternal = false;
	}
	
	public IFilePartInfo(String pkg, String name, char[][] enclosingTypes, String project, String containerLocation, String sourceFolder, String file, String extension, char partType, boolean isExternal) {
		super(pkg, name, enclosingTypes);
		fProject= project;
		fFolder= sourceFolder;
		fContainerLoc = containerLocation;
		fFile= file;
		fExtension= extension;
		fPartType = partType;
		this.isExternal = isExternal;
	}
	
	public int getElementType() {
		return PartInfo.IFILE_TYPE_INFO;
	}
	
	//for eglar: find the project using the eglar, then get the EGLElement via package fragment
	//for source file: find the egl source file directly
	protected IEGLElement getEGLElement(IEGLSearchScope scope) {
		IResource iResource = null;
		if(Util.isEGLARFileName(fFolder)) {
			iResource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(fProject));
			if(iResource == null){
				return null;
			}
			IClassFile classFile = getClassFile( iResource, fPackage, EGLAR_FILE, fFile + "." + fExtension );
			return classFile;
		} else {
			iResource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getPath()));
			return EGLCore.create(iResource);
		}
	}
	
	/**
	 * Return the IPackageFragment for the JavaPackage for
	 * 
	 * @javaClass.
	 */
	public static IPackageFragment getPackageFragment(IResource iResource, String packageName) {
		try {
			IEGLProject eglProject = EGLCore.create(iResource.getProject());
			IPackageFragment[] fragments = eglProject.getPackageFragments();
			for(IPackageFragment fragment : fragments) {
				if(fragment.getElementName().equalsIgnoreCase(packageName)) {
					return fragment;
				}
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static IPackageFragment getPackageFragment(IResource iResource, String packageName, int fileType) {
		try {
			IEGLProject eglProject = EGLCore.create(iResource.getProject());
			IPackageFragment[] fragments = eglProject.getPackageFragments();
			for(IPackageFragment fragment : fragments) {
				if(fileType == EGL_FILE && fragment instanceof EglarPackageFragment){
					continue;
				}
				if(fileType == EGLAR_FILE && !(fragment instanceof EglarPackageFragment)){
					continue;
				}
				if(fragment.getElementName().equalsIgnoreCase(packageName)) {
					return fragment;
				}
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static IPackageFragment[] getPackageFragments(IResource iResource, String packageName, int fileType) {
		try {
			List list = new ArrayList();
			IEGLProject eglProject = EGLCore.create(iResource.getProject());
			IPackageFragment[] fragments = eglProject.getPackageFragments();
			for(IPackageFragment fragment : fragments) {
				if(fileType == EGL_FILE && fragment instanceof EglarPackageFragment){
					continue;
				}
				if(fileType == EGLAR_FILE && !(fragment instanceof EglarPackageFragment)){
					continue;
				}
				if(fragment.getElementName().equalsIgnoreCase(packageName)) {
					list.add(fragment);
				}
			}
			return (IPackageFragment[])list.toArray(new IPackageFragment[list.size()]);
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IClassFile getClassFile(IResource iResource, String packageName, int fileType, String classFileName) {
		IPackageFragment[] frags = getPackageFragments(iResource, packageName, fileType);
		if (frags == null) {
			return null;
		}
		for (int i = 0; i < frags.length; i++) {
			IClassFile cFile= frags[i].getClassFile(classFileName);
			if (cFile != null && cFile.exists() && cFile.getPart() != null) {
				return cFile;
			}
		}
		return null;
	}
	
	//for Part in eglar, getPackageFragmentRootPath() returns the location where the eglar is imported into
	public IPath getPackageFragmentRootPath() {
		StringBuffer buffer= new StringBuffer(fProject);
		if (fFolder != null && fFolder.length() > 0) {
			if(!isExternal){
				buffer.append(PartInfo.SEPARATOR);
			} else{
				buffer.append(File.separator);
			}
			buffer.append(fFolder);
		}
		return new Path(buffer.toString());
	}
		
	//for Part in eglar, getPath() returns the full path of the ir file where it is contained, rather
	//than where it is imported into
	public String getPath() {
		StringBuffer result = new StringBuffer( ( Util.isEGLARFileName(fFolder) ? FileInEglar.EGLAR_PREFIX  : "" ) + fContainerLoc);
		
		if (fFolder != null && fFolder.length() > 0) {
			if(!isExternal){
				result.append(PartInfo.SEPARATOR);
			} else{
				result.append(File.separator);
			}
			result.append(fFolder);			
		}
		if(Util.isEGLARFileName(fFolder)) {
			result.append( FileInEglar.EGLAR_SEPARATOR );
		}
		result.append(PartInfo.SEPARATOR);
		String packageName = getPackageName();
		if(!"".equals(packageName)) {
			//TODO verify if replace('.', '/') correct, 6/18
			result.append(packageName.replace(PartInfo.PACKAGE_PART_SEPARATOR, PartInfo.SEPARATOR));
			result.append(PartInfo.SEPARATOR);
		}
		result.append(fFile);
		result.append('.');
		result.append(fExtension);
		return result.toString();
	}
	
	//for Part in eglar, getContainerPath() returns the location where the eglar is contained, rather
	//than where the eglar is imported into
	public String getContainerPath() {
		StringBuffer buffer= new StringBuffer(fContainerLoc);
		if (fFolder != null && fFolder.length() > 0) {
			if(!isExternal){
				buffer.append(PartInfo.SEPARATOR);
			} else{
				buffer.append(File.separator);
			}
			buffer.append(fFolder);
		}
		return buffer.toString();
	}
	
	public String getProject() {
		return fProject;
	}
	
	public String getFolder() {
		return fFolder;
	}
	
	public String getFileName() {
		return fFile;
	}
	
	public String getExtension() {
		return fExtension;
	}
	/**
	 * @return
	 */
	public char getPartType() {
		return fPartType;
	}
	
	public boolean isExternal() {
		return isExternal;
	}
}
