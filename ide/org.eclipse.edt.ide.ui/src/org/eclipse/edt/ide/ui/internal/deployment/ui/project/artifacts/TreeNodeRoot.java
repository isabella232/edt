/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.utils.EGLProjectInfoUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.deployment.ui.DeploymentUtilities;
import org.eclipse.edt.ide.ui.internal.deployment.ui.SOAMessages;


/**
 *
 */
public class TreeNodeRoot extends TreeNodeFolder {

	/**
	 * @param parent
	 * @param resource
	 */
	public TreeNodeRoot(TreeNode parent, IResource resource) {
		super(parent, resource);
	}
	
    /**
     * @return <code>true</code> if this node has any children
     */
    public boolean hasChildren() {
    	return ! getChildren().isEmpty();
    }
    
	protected void generateChildren() {
		List addedProjects = new ArrayList();
		List results = new ArrayList();
		List projects = Util.getEGLProjectPath((IProject)this.resource);
		for (Iterator iterator = projects.iterator(); iterator.hasNext();) {
			EGLProject eglProject = (EGLProject) iterator.next();
			IProject project = eglProject.getProject();
			if (!addedProjects.contains(eglProject)) {
				addedProjects.add(eglProject);
				TreeNodeProject node = new TreeNodeProject(this, project);
				node.setDeployable(false);
				results.add(node);
			}
		}
		this.children = results;
	}

}
