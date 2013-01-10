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
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.deployment.ui.DeploymentUtilities;

/**
 *
 */
public abstract class TreeNode {
	
	public List children;
	TreeNode parent;
	IResource resource;
	boolean deployable;
	boolean isChecked;
	boolean isGrayed;

	/**
	 * 
	 */
	public TreeNode(TreeNode parent, IResource resource) {
		this.resource = resource;
		this.parent = parent;
		this.deployable = true;
		this.isChecked = true;
	}
	
	public boolean hasChildren() {
		return false;
	}
	
	public void addChild(TreeNode child) {
		this.children.add(child);
	}
	
	public void addChildren(List children) {
		this.children.addAll(children);
	}
	
	public List getChildren() {
		if (this.children == null) {
			generateChildren();
		}
		return this.children;
	}

	protected void generateChildren() {
		List results = new ArrayList();
		try {
			DeploymentUtilities.buildResourceTree(this, results);
		} catch (CoreException e) {
			DeploymentUtilities.displayErrorDialog("Messages.TreeNode_0", "Messages.TreeNode_1");
			EDTUIPlugin.log(e);
		}
		this.children = results;
	}

	public IResource getResource() {
		return resource;
	}

	public TreeNode getParent() {
		return parent;
	}
	
	public boolean isDeployable() {
		return deployable;
	}

	public void setDeployable(boolean deployable) {
		this.deployable = deployable;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	public boolean isGrayed() {
		return isGrayed;
	}
	
	public void setGrayed(boolean isGrayed) {
		this.isGrayed = isGrayed;
	}

}
