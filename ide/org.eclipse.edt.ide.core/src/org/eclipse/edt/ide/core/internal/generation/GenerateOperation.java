/*******************************************************************************
 * Copyright © 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.generate.GenerateEnvironment;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;

public class GenerateOperation {

	protected IProject getProject(String filename) {
		IPath path = new Path(filename);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
		return project;
	}

	protected String[] getPackageName(String filename, ProjectInfo projectInfo) {
		IPath path = new Path(filename);
		path = path.removeFirstSegments(1); // project name
		path = path.removeLastSegments(1);// filename
		String[] retVal = Util.pathToStringArray(path);
		while (retVal.length > 0) {
			if (projectInfo.hasPackage(InternUtil.intern(retVal))) {
				break;
			}
			path = path.removeFirstSegments(1);// source folder
			retVal = Util.pathToStringArray(path);
		}
		return retVal;
	}
	
	protected boolean isDeployment(String partName) {
		if (partName == null) {
			return false;
		}

		// for now, the only time a partname would have a "." in it, is if it is
		// a deployment
		return partName.indexOf(".") > 0;
	}


	protected IFile getFile(String name) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(name));
		return file;
	}

	protected Part findPart(String partName, GenerateEnvironment environment) throws PartNotFoundException {

//		String[] packageName = getPackageName(partName);
//
//		int index = partName.lastIndexOf(".");
//		String name = partName;
//		if (index > -1) {
//			name = name.substring(index + 1);
//		}
//
//		return environment.findPart(InternUtil.intern(packageName), InternUtil.intern(name));
		return null;
	}

	protected String[] getPackageName(String partName) {
		List list = new ArrayList();
		String remaining = partName;
		int index = partName.indexOf(".");
		while (index > -1) {
			String pkg = remaining.substring(0, index);
			remaining = remaining.substring(index + 1);
			list.add(pkg);
			index = remaining.indexOf(".");
		}
		return (String[]) list.toArray(new String[list.size()]);

	}

}
