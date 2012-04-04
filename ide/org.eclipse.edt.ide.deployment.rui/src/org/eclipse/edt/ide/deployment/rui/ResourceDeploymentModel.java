/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNode;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeFile;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeFolder;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeJarFolderInEglar;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeJavaScriptFolder;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeJavaScriptFolderInEglar;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeJavaSourceFolder;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeRoot;

public class ResourceDeploymentModel {

	/**
	 * A list of the IFile artifacts the user wants to deploy with these handlers
	 */
	private Map<IFolder, Set<IFile>> javaFiles = new HashMap<IFolder, Set<IFile>>();
	/**
	 * A list of the IFolder artifacts that are required to be able
	 * to deploy the IFile artifacts
	 */
	private Map<IFolder, Set<IFolder>> javaFolders = new HashMap<IFolder, Set<IFolder>>();
	/**
	 * A list of the IFile artifacts the user wants to deploy with these handlers
	 */
	private Map<IFolder, Set<IFile>> javascriptFiles = new HashMap<IFolder, Set<IFile>>();
	/**
	 * A list of the IFolder artifacts that are required to be able
	 * to deploy the IFile artifacts
	 */
	private Map<IFolder, Set<IFolder>> javascriptFolders = new HashMap<IFolder, Set<IFolder>>();
	
	/**
	 * A list of the IFile artifacts the user wants to deploy with these handlers
	 */
	private Map<IFolder, Set<IFile>> javaJarFiles = new HashMap<IFolder, Set<IFile>>();	
	
	/**
	 * A list of the IFolder artifacts that are required to be able
	 * to deploy the IFile artifacts
	 */
	private Map<IFolder, Set<IFolder>> javaJarFolders = new HashMap<IFolder, Set<IFolder>>();
	/**
	 * current active root node
	 */
	private TreeNodeRoot activeRoot;

	private Set<IFolder> activeFolderSet;
	private Set<IFile> activeFileSet;
	/**
	 * A convenience list of the additional resources that should not be deployed
	 */
	private List<String> resourceOmissions;
	
	public ResourceDeploymentModel(IProject sourceProject, List<String> resourceOmissions) 
	{
		this.activeRoot = new TreeNodeRoot(null, sourceProject);		
		this.resourceOmissions = resourceOmissions;
	}
	
	private void initializeResourceTree(){
		if (this.activeRoot.children == null) {
			cleanupFoldersToDeploy(activeRoot);
			buildTree(this.activeRoot, inOmissions(activeRoot));
			
			/**
			 * gather up all the resources that the user also wants deployed with the handler
			 */
			walkTree(this.activeRoot);
		}
	}
	
	private void walkTree(TreeNode sourceNode) {
		for (Iterator<TreeNode> iterator = sourceNode.getChildren().iterator(); iterator.hasNext();) {
			TreeNode node = iterator.next();
			if (node instanceof TreeNodeJavaScriptFolder || node instanceof TreeNodeJavaScriptFolderInEglar) {
				activeFolderSet = javascriptFolders.get(node.getResource());
				if( activeFolderSet == null )
				{
					activeFolderSet = new LinkedHashSet<IFolder>();
					javascriptFolders.put((IFolder)node.getResource(), activeFolderSet);
				}
				activeFileSet = javascriptFiles.get(node.getResource());
				if( activeFileSet == null )
				{
					activeFileSet = new LinkedHashSet<IFile>();
					javascriptFiles.put((IFolder)node.getResource(), activeFileSet);
				}
			}else if (node instanceof TreeNodeJavaSourceFolder) {
				activeFolderSet = javaFolders.get(node.getResource());
				if( activeFolderSet == null )
				{
					activeFolderSet = new LinkedHashSet<IFolder>();
					javaFolders.put((IFolder)node.getResource(), activeFolderSet);
				}
				activeFileSet = javaFiles.get(node.getResource());
				if( activeFileSet == null )
				{
					activeFileSet = new LinkedHashSet<IFile>();
					javaFiles.put((IFolder)node.getResource(), activeFileSet);
				}
			} else if (node instanceof TreeNodeJarFolderInEglar) {
				activeFolderSet = javaJarFolders.get(node.getResource());
				if( activeFolderSet == null )
				{
					activeFolderSet = new LinkedHashSet<IFolder>();
					javaJarFolders.put((IFolder)node.getResource(), activeFolderSet);
				}
				activeFileSet = javaJarFiles.get(node.getResource());
				if( activeFileSet == null )
				{
					activeFileSet = new LinkedHashSet<IFile>();
					javaJarFiles.put((IFolder)node.getResource(), activeFileSet);
				}
			} else if (node.isDeployable() && node.isChecked()) {
				if( activeFolderSet == null )
				{
					
				}
				else {
					if (node instanceof TreeNodeFolder) {
						activeFolderSet.add((IFolder)node.getResource());
					} else if (node instanceof TreeNodeFile) {
						activeFileSet.add((IFile)node.getResource());
					}
				}
			}
			walkTree(node);
		}
		
	}
	
	public Map<IFolder, Set<IFile>> getJavaFiles() {
		initializeResourceTree();
		return javaFiles;
	}

	public Map<IFolder, Set<IFolder>> getJavaFolders() {
		initializeResourceTree();
		return javaFolders;
	}

	public Map<IFolder, Set<IFile>> getJavascriptFiles() {
		initializeResourceTree();
		return javascriptFiles;
	}

	public Map<IFolder, Set<IFolder>> getJavascriptFolders() {
		initializeResourceTree();
		return javascriptFolders;
	}
	
	
	public Map<IFolder, Set<IFile>> getJavaJarFiles() {
		initializeResourceTree();
		return javaJarFiles;
	}

	public Map<IFolder, Set<IFolder>> getJavaJarFolders() {
		initializeResourceTree();
		return javaJarFolders;
	}
	
	private void buildTree(TreeNode node, boolean forceUnChecked) {
		for (Iterator<TreeNode> iterator = node.getChildren().iterator(); iterator.hasNext();) {
			TreeNode child = iterator.next();
			if (child.isDeployable()) {
				child.setChecked(!forceUnChecked && !inOmissions(child));
			}
			buildTree(child, forceUnChecked || inOmissions(child));
			if( child instanceof TreeNodeFolder ){
				child.setChecked(((TreeNodeFolder)child).hasEnabledFiles());
			}
		}
	}
	
	private void cleanupFoldersToDeploy(TreeNode node) {
		for (Iterator<TreeNode> iterator = node.getChildren().iterator(); iterator.hasNext();) {
			TreeNode child = iterator.next();
			cleanupFoldersToDeploy(child);
			if( child instanceof TreeNodeFolder ){
				((TreeNodeFolder)child).forceRescanForEnabledFiles();
			}
		}
	}
	
	private boolean inOmissions(TreeNode node) {
		if (this.resourceOmissions != null) {
			return this.resourceOmissions.contains(node.getResource().getFullPath().toString());
		}
		return false;
	}
}
