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
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class EGLPackageConfiguration extends EGLContainerConfiguration {
	
	/** The Package. */
	private String fPackage;
	
	public EGLPackageConfiguration()
	{
		super();
		setDefaultAttributes();
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		
		if (selection == null || selection.isEmpty()) {
			setDefaultAttributes();
			return;
		}
		
		IEGLElement eelem = null;
		
		if(selection.getFirstElement() instanceof IFolder)
			eelem = EGLCore.create((IFolder)selection.getFirstElement());
			
		if(eelem == null)
			eelem = getInitialEGLElement(selection);	

		internalInit(eelem);
		String pName= ""; //$NON-NLS-1$
		if (eelem != null) {
		    IPackageFragment pf = null;
		    if(eelem.getElementType() == IEGLElement.PACKAGE_FRAGMENT)
		        pf = (IPackageFragment)eelem;
		    else if(eelem instanceof IEGLFile && eelem.getParent().getElementType() == IEGLElement.PACKAGE_FRAGMENT)
		    	pf = (IPackageFragment)(eelem.getParent());		
		    
			if (pf!=null && !pf.isDefaultPackage())
				pName= pf.getElementName();
		}
		setFPackage(pName);	
	}

	/**
	 * @return
	 */
	public String getFPackage() {
		return fPackage;
	}

	/**
	 * @param fragment
	 */
	public void setFPackage(String fragment) {
		fPackage = fragment;
	}
	
	/**
	 * Initializes the source folder field with a valid package fragement root.
	 * The package fragement root is computed from the given EGL element.
	 * 
	 * @param elem the EGL element used to compute the initial package
	 *    fragment root used as the source folder
	 */
	private void internalInit(IEGLElement elem) {
		IPackageFragmentRoot initRoot= null;
		if (elem != null) {
			initRoot= (IPackageFragmentRoot) elem.getAncestor(IEGLElement.PACKAGE_FRAGMENT_ROOT);
			if (initRoot == null || initRoot.isArchive()) {
				IEGLProject eproject= elem.getEGLProject();
				if (eproject != null) {
					try {
						initRoot= null;
						if (eproject.exists()) {
							IPackageFragmentRoot[] roots= eproject.getPackageFragmentRoots();
							for (int i= 0; i < roots.length; i++) {
								if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
									initRoot= roots[i];
									break;
								}
							}							
						}
					} catch (EGLModelException e) {
						EGLLogger.log(this, e);
					}
					if (initRoot == null) {
						initRoot= eproject.getPackageFragmentRoot(eproject.getResource());
					}
				}
			}
			setSourceFolderName(initRoot.getElementName());
		}
	}
	
	private void setDefaultAttributes() {
		String sourceFolderPath = ""; //$NON-NLS-1$
		
		//If the project is specified, but source folder is not, attempt to prefill source folder
		if(!getProjectName().equals("") && getSourceFolderName().equals("")){ //$NON-NLS-1$ //$NON-NLS-2$
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
			IEGLProject eProject = EGLCore.create(project);
			
			IPreferenceStore store = EDTCoreIDEPlugin.getPlugin().getPreferenceStore();
			sourceFolderPath = store.getString(EDTCorePreferenceConstants.EGL_SOURCE_FOLDER);
			
			if(sourceFolderPath == null || sourceFolderPath.equals("")) {
				IPackageFragmentRoot[] roots;
				try{
					roots = eProject.getAllPackageFragmentRoots();
					if(roots.length>0){
						sourceFolderPath = roots[0].getElementName();
					}
				} catch(EGLModelException e){
					EGLLogger.log(this, e);
				}
			}
		}
		
		setSourceFolderName(sourceFolderPath);
		
		fPackage = ""; //$NON-NLS-1$
	}

}
