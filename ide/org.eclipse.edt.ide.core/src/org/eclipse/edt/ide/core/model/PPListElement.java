/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.model;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class PPListElement {
	
	public static final String SOURCEATTACHMENT= "sourcepath"; //$NON-NLS-1$
	public static final String SOURCEATTACHMENTROOT= "rootpath"; //$NON-NLS-1$
	public static final String JAVADOC= "javadoc"; //$NON-NLS-1$
	public static final String OUTPUT= "output"; //$NON-NLS-1$
	public static final String EXCLUSION= "exclusion"; //$NON-NLS-1$
	
	private IEGLProject fProject;
	
	private int fEntryKind;
	private IPath fPath;
	private IResource fResource;
	private boolean fIsExported;
	private boolean fIsMissing;
	
	private PPListElement fParentContainer;
		
	private IEGLPathEntry fCachedEntry;
	private ArrayList fChildren;	
	
	public PPListElement(IEGLProject project, int entryKind, IPath path, IResource res) {
		fProject= project;
		fEntryKind= entryKind;
		fPath= path;
		fResource= res;
		this.initial();
	}
	
	private void initial() {
		fChildren= new ArrayList();
		fIsExported= false;
		
		fIsMissing= false;
		fCachedEntry= null;
		fParentContainer= null;
		
		switch (fEntryKind) {
			case IEGLPathEntry.CPE_SOURCE:
				createAttributeElement(OUTPUT, null);
				createAttributeElement(EXCLUSION, new Path[0]);
				break;
			case IEGLPathEntry.CPE_LIBRARY:
			case IEGLPathEntry.CPE_VARIABLE:
				createAttributeElement(SOURCEATTACHMENT, null);
//				createAttributeElement(JAVADOC, null);
				break;
			case IEGLPathEntry.CPE_PROJECT:
				break;
			case IEGLPathEntry.CPE_CONTAINER:
				try {
					IEGLPathContainer container= EGLCore.getEGLPathContainer(fPath, fProject);
					if (container != null) {
						IEGLPathEntry[] entries= container.getEGLPathEntries();
						for (int i= 0; i < entries.length; i++) {
							PPListElement curr= createFromExisting(entries[i], fProject);
							curr.setParentContainer(this);
							fChildren.add(curr);
						}						
					}
				} catch (EGLModelException e) {
				}			
				break;
			default:
		}
	}
	
	public IEGLPathEntry getEGLPathEntry() {
		if (fCachedEntry == null) {
			fCachedEntry= newEGLPathEntry();
		}
		return fCachedEntry;
	}
	

	private IEGLPathEntry newEGLPathEntry() {
		switch (fEntryKind) {
			case IEGLPathEntry.CPE_SOURCE:
				IPath outputLocation= (IPath) getAttribute(OUTPUT);
				IPath[] exclusionPattern= (IPath[]) getAttribute(EXCLUSION);
				return EGLCore.newSourceEntry(fPath, exclusionPattern, outputLocation);
			case IEGLPathEntry.CPE_LIBRARY:
				IPath attach= (IPath) getAttribute(SOURCEATTACHMENT);
				IEGLPathEntry entry = EGLCore.newLibraryEntry(fPath, attach, null, isExported());
				return entry;
			case IEGLPathEntry.CPE_PROJECT:
				IEGLPathEntry entryTemp = EGLCore.newProjectEntry(fPath, isExported());
				if(fResource != null && fResource instanceof IProject) {
					IEGLProject proj = EGLCore.create((IProject)fResource);
					entryTemp.setBinaryProject(proj.isBinary());
				}
				return entryTemp;
			case IEGLPathEntry.CPE_CONTAINER:
				return EGLCore.newContainerEntry(fPath, isExported());
			case IEGLPathEntry.CPE_VARIABLE:
				IPath varAttach= (IPath) getAttribute(SOURCEATTACHMENT);
				return EGLCore.newVariableEntry(fPath, varAttach, null, isExported());
			default:
				return null;
		}
	}
	
	/**
	 * Gets the classpath entry path.
	 * @see IEGLPathEntry#getPath()
	 */
	public IPath getPath() {
		return fPath;
	}

	/**
	 * Gets the classpath entry kind.
	 * @see IEGLPathEntry#getEntryKind()
	 */	
	public int getEntryKind() {
		return fEntryKind;
	}

	/**
	 * Entries without resource are either non existing or a variable entry
	 * External jars do not have a resource
	 */
	public IResource getResource() {
		return fResource;
	}
	
	public PPListElementAttribute setAttribute(String key, Object value) {
		PPListElementAttribute attribute= findAttributeElement(key);
		if (attribute == null) {
			return null;
		}
		attribute.setValue(value);
		attributeChanged(key);
		return attribute;
	}
	
	private PPListElementAttribute findAttributeElement(String key) {
		for (int i= 0; i < fChildren.size(); i++) {
			Object curr= fChildren.get(i);
			if (curr instanceof PPListElementAttribute) {
				PPListElementAttribute elem= (PPListElementAttribute) curr;
				if (key.equals(elem.getKey())) {
					return elem;
				}
			}
		}		
		return null;		
	}
	
	public Object getAttribute(String key) {
		PPListElementAttribute attrib= findAttributeElement(key);
		if (attrib != null) {
			return attrib.getValue();
		}
		return null;
	}
	
	private void createAttributeElement(String key, Object value) {
		fChildren.add(new PPListElementAttribute(this, key, value));
	}	
	
	
	public Object[] getChildren(boolean hideOutputFolder) {
		if (hideOutputFolder && fEntryKind == IEGLPathEntry.CPE_SOURCE) {
			return new Object[] { findAttributeElement(EXCLUSION) };
		}
		return fChildren.toArray();
	}
	
	private void setParentContainer(PPListElement element) {
		fParentContainer= element;
	}
	
	public PPListElement getParentContainer() {
		return fParentContainer;
	}	
	
	private void attributeChanged(String key) {
		fCachedEntry= null;
	}
	
	
	/*
	 * @see Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other != null && other.getClass().equals(getClass())) {
			PPListElement elem= (PPListElement)other;
			return elem.fEntryKind == fEntryKind && elem.fPath.equals(fPath);
		}
		return false;
	}
	
	/*
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return fPath.hashCode() + fEntryKind;
	}

	/**
	 * Returns if a entry is missing.
	 * @return Returns a boolean
	 */
	public boolean isMissing() {
		return fIsMissing;
	}

	/**
	 * Sets the 'missing' state of the entry.
	 */
	public void setIsMissing(boolean isMissing) {
		fIsMissing= isMissing;
	}

	/**
	 * Returns if a entry is exported (only applies to libraries)
	 * @return Returns a boolean
	 */
	public boolean isExported() {
		return fIsExported;
	}

	/**
	 * Sets the export state of the entry.
	 */
	public void setExported(boolean isExported) {
		if (isExported != fIsExported) {
			fIsExported = isExported;
			
			attributeChanged(null);
		}
	}

	/**
	 * Gets the project.
	 * @return Returns a IEGLProject
	 */
	public IEGLProject getEGLProject() {
		return fProject;
	}
	
	public static PPListElement createFromExisting(IEGLPathEntry curr, IEGLProject project) {
		IPath path= curr.getPath();
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();

		// get the resource
		IResource res= null;
		boolean isMissing= false;
		URL javaDocLocation= null;

		switch (curr.getEntryKind()) {
			case IEGLPathEntry.CPE_CONTAINER:
				res= null;
				try {
					isMissing= (EGLCore.getEGLPathContainer(path, project) == null);
				} catch (EGLModelException e) {
					isMissing= true;
				}
				break;
			
			case IEGLPathEntry.CPE_VARIABLE:
				IPath resolvedPath= EGLCore.getResolvedVariablePath(path);
				res= null;
				isMissing=  root.findMember(resolvedPath) == null && !resolvedPath.toFile().isFile();
//				javaDocLocation= JavaUI.getLibraryEGLdocLocation(resolvedPath);
				break;
			case IEGLPathEntry.CPE_LIBRARY:
				res= root.findMember(path);
				if (res == null) {
					if ("eglar".equalsIgnoreCase(path.getFileExtension())) {
						if (root.getWorkspace().validatePath(path.toString(), IResource.FOLDER).isOK()) {
							res= root.getFolder(path);
						}
					}
					isMissing= !path.toFile().isFile(); // look for external JARs
				}
//				javaDocLocation= JavaUI.getLibraryEGLdocLocation(path);
				break;
			
			case IEGLPathEntry.CPE_SOURCE:
				res= root.findMember(path);
				if (res == null) {
					if (root.getWorkspace().validatePath(path.toString(), IResource.FOLDER).isOK()) {
						res= root.getFolder(path);
					}
					isMissing= true;
				}
				break;
			case IEGLPathEntry.CPE_PROJECT:
				res= root.findMember(path);
				isMissing= (res == null);
				break;
		}
		PPListElement elem = new PPListElement(project, curr.getEntryKind(), path, res);
		elem.setExported(curr.isExported());
		elem.setAttribute(SOURCEATTACHMENT, curr.getSourceAttachmentPath());
		elem.setAttribute(JAVADOC, javaDocLocation);
		elem.setAttribute(OUTPUT, curr.getOutputLocation());
		elem.setAttribute(EXCLUSION, curr.getExclusionPatterns()); 

//		if (project.exists()) {
			elem.setIsMissing(isMissing);
//		}
		return elem;
	}	
	
	
    public void setAttributesFromExisting(PPListElement existing) {
    	Assert.isTrue(existing.getEntryKind() == getEntryKind());
		PPListElementAttribute[] attributes= existing.getAllAttributes();
		for (int i= 0; i < attributes.length; i++) {
			PPListElementAttribute curr= attributes[i];
			PPListElementAttribute elem= findAttributeElement(curr.getKey());
			if (elem == null) {
				createAttributeElement(curr.getKey(), curr.getValue());
			} else {
				elem.setValue(curr.getValue());
			}
		}
    }
    
	public PPListElementAttribute[] getAllAttributes() {
		ArrayList res= new ArrayList();
		for (int i= 0; i < fChildren.size(); i++) {
			Object curr= fChildren.get(i);
			if (curr instanceof PPListElementAttribute) {
				res.add(curr);
			}
		}		
		return (PPListElementAttribute[]) res.toArray(new PPListElementAttribute[res.size()]);
	}

}
