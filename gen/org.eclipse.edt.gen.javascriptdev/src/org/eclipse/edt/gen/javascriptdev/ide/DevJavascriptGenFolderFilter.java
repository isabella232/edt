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
package org.eclipse.edt.gen.javascriptdev.ide;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.AbstractGenerator;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class DevJavascriptGenFolderFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer arg0, Object parent, Object element) {
		if(element instanceof IFolder){
			if(isGenFolder((IFolder) element))
				return false;
		}
		return true;
	}
	
	private boolean isGenFolder(IFolder folder){
		HashSet<String> genFolders = getGenFolders(folder.getProject());
		String folderPath = folder.getProjectRelativePath().toString().replace("\\", "/");
		Iterator iter = genFolders.iterator();
		while(iter.hasNext()){
			String genFolder = (String) iter.next();
			if(genFolder.equals(folderPath))
				return true;
		}
		return false;
	}
	
	private HashSet<String> getGenFolders( IProject project ) {
		HashSet<String> genFolders = new HashSet<String>();
		IGenerator[] gens = ProjectSettingsUtility.getGenerators( project );
		AbstractGenerator devGen = null;
		for( IGenerator gen : gens ) {
			if(gen instanceof AbstractGenerator && gen.getId().equalsIgnoreCase(ProjectConfiguration.JAVASCRIPT_DEV_GENERATOR_ID)){
				devGen = (AbstractGenerator) gen;				
			}
		}		
		try {
			if(devGen!=null && project.hasNature(EGLCore.NATURE_ID)){
				genFolders.add(devGen.getOutputDirectory(project).substring(2).replace("\\", "/"));
				IEGLProject eglProject = EGLCore.create(project);
				IPackageFragment[] roots= eglProject.getPackageFragments();
				for(int i=0;i<roots.length;i++){
					genFolders.add(devGen.getOutputDirectory(roots[i].getResource()).substring(2).replace("\\", "/"));
					IEGLElement[] elements = roots[i].getChildren();
					for(int j=0;j<elements.length;j++){
						IEGLElement element = elements[j];
						if(element.getElementType() == IEGLElement.EGL_FILE){
							genFolders.add(devGen.getOutputDirectory(element.getResource()).substring(2).replace("\\", "/"));
						}
					}
				}
			}
		} catch (EGLModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return genFolders;
	}

}
