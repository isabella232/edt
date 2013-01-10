/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.utils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.edt.ide.core.internal.model.util.IEGLProjectFileUtility;

public class EGLProjectFileUtility implements IEGLProjectFileUtility {

	public boolean isBinaryProject(IProject proj) {
		try {
			String value = ResourceValueStoreUtility.getInstance().getValue(proj, new QualifiedName(null, IEGLProjectFileUtility.BINARYPROJECT_KEY), false);
			return ("true".equalsIgnoreCase(value));
		} catch (CoreException e) {
		}
		return false;
	}
	
	 public static IFolder createFolder(IContainer root, IPath folderPath){
	    	IFolder folder = null;
	    	if((folder = root.getFolder(folderPath)).exists()){
	    		return folder;	//already exist
	    	}
	    	String[] segs = folderPath.segments();
	    	for(String seg: segs){
				if(segs != null){
					IFolder curFolder = root.getFolder(new Path(seg));
					if(!curFolder.exists()){
						try {
							curFolder.create(false, true, null);
						} catch (CoreException e) {
							e.printStackTrace();
							return null;
						}
					}
					root = curFolder;
				}
			}
	    	return root.getFolder(folderPath);
	    }
}
