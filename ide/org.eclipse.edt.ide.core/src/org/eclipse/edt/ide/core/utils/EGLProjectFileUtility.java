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
package org.eclipse.edt.ide.core.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProject;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProjectManager;
import org.eclipse.edt.ide.core.internal.model.EGLModel;
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
	
	//Returns an IPath that represents the path to the given eglar. NOTE: This path is not necessarily absolute. To 
	//get an absolute path, use AbsolutePathUtility.getAbsolutePathString(path) with the returned value from this method
    public IPath resolvePathToEGLAR(IPath path, IProject wsProject, ExternalProject extProject) {
    	
 		Object obj = EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), path, true);
		if (obj != null) {
			return path;
		}
		
		//This could be referencing a file that is in an external project
		if (path.toString().startsWith("/")) {
			String projName = path.segment(0);
			ExternalProject proj = ExternalProjectManager.getInstance().getProject(projName, wsProject);
			
			if (proj != null) {
				String loc = proj.getLocation();
				IPath newPath = new Path(loc);
				newPath = newPath.append(path.removeFirstSegments(1));
				return newPath;
			}
		}
		else {
			if (extProject != null) {
				String loc = extProject.getLocation();
				IPath newPath = new Path(loc);
				newPath = newPath.append(path);
				return newPath;
			}
		}
			
		return path;
    	
    }

}
