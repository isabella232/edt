/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.IEGLModelMarker;

import org.eclipse.edt.compiler.internal.IEGLMarker;

/**
 * @author svihovec
 *
 */
public class EGLRemoveMarkersBuilder extends IncrementalProjectBuilder {

    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        
        removeProblemsAndTasksFor(getProject());
    	return null;
    }
    
    public static void removeProblemsAndTasksFor(IResource resource) {
    	try {
    		if (resource != null && resource.exists()) {
    			resource.deleteMarkers(IEGLMarker.EGL_PROBLEM_MARKER, false, IResource.DEPTH_INFINITE);
    			resource.deleteMarkers(IEGLModelMarker.TASK_MARKER, false, IResource.DEPTH_INFINITE);
    		}
    	} catch (CoreException e) {} // assume there were no problems
    }


}
