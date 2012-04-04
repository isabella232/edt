/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

public class EglPathRootLocator
{
	private IEGLProject eglProject;
	
	public EglPathRootLocator(IProject project){
		eglProject = EGLCore.create(project);
	}
	
	public IFile findFile(String name){
		
		try
		{
			IPackageFragmentRoot[] sources = eglProject.getAllPackageFragmentRoots();
			for (int i = 0; i < sources.length; i++) {
				IFile file = sources[i].getEGLProject().getProject().getFile(name);
				if(file.exists()){
					return file;
				}
			}
		}
		catch( EGLModelException eglme ){}
		return null;
	}
}
