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


import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import org.eclipse.edt.compiler.internal.eglar.EglarFile;
import org.eclipse.edt.compiler.internal.eglar.EglarFileCache;
import org.eclipse.edt.compiler.internal.eglar.EglarManifest;
import org.eclipse.edt.ide.core.utils.EGLProjectFileUtility;
//TODO EDT eglar
//import com.ibm.etools.edt.internal.core.ide.eglar.EglarFileResource;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;

public class SourceLocator
{
	private IEGLProject eglProject;
	
	public SourceLocator(IProject project){
		eglProject = EGLCore.create(project);
	}
	
	public IFile findFile(String name){
//TODO EDT eglar		
//		try
//		{
//			IPackageFragmentRoot[] sources = eglProject.getAllPackageFragmentRoots();
//			for (int i = 0; i < sources.length; i++) {
//				if(sources[i] instanceof EglarPackageFragmentRoot){
//					IPath eglarPath = ((EglarPackageFragmentRoot)sources[i]).getPath();
//					EglarFile eglar = EglarFileCache.instance.getEglarFile( EGLProjectFileUtility.getEglarAbsolutePath(eglarPath, eglProject.getProject()) );
//					EglarManifest manifest = eglar.getManifest();
//					if ( manifest == null ) {
//						continue;
//					}
//					String wsdlInEglarName = name.replace(File.separatorChar, '/').toLowerCase();
//					if(wsdlInEglarName.startsWith("/")){
//						wsdlInEglarName = wsdlInEglarName.substring(1);
//					}
//					ZipEntry zipEntry = eglar.getEntry( wsdlInEglarName );
//					if(zipEntry != null){
//						return new EglarFileResource(eglar, zipEntry, "");
//					}
//				}
//				else{
//					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(sources[i].getParent().getPath().append(sources[i].getElementName()).append(name));
//					if(file != null && file.exists()){
//						return file;
//					}
//				}
//				
//			}
//		}
//		catch( EGLModelException eglme ){}
//		catch (IOException e) {}
		return null;
	}
}
