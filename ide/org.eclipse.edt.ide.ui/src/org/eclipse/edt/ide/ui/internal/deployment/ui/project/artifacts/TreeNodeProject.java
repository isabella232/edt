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
package org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.eglar.EglarFile;
import org.eclipse.edt.compiler.internal.eglar.EglarFileCache;
import org.eclipse.edt.compiler.internal.eglar.EglarManifest;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.utils.EGLProjectInfoUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.deployment.ui.DeploymentUtilities;
import org.eclipse.edt.ide.ui.internal.deployment.ui.SOAMessages;

/**
 *
 */
public class TreeNodeProject extends TreeNodeFolder {

	private static final String WEB_CONTENT = "WebContent";
	
	/**
	 * 
	 */
	public TreeNodeProject(TreeNode parent, IResource resource) {
		super(parent, resource);
	}
	
    /**
     * @return <code>true</code> if this node has any children
     */
    public boolean hasChildren() {
    	return ! getChildren().isEmpty();
    }
    
	protected void generateChildren() {
		this.children = new ArrayList();
		//TODO These children providers should not be part of this tree
		this.children.addAll(generateJavaScriptChildren());
		this.children.addAll(generateJavaChildren());
		this.children.addAll(generateEglarChildren());
	}
	
	private List<TreeNodeFolder> generateJavaScriptChildren(){
		
		List<IResource> results = new ArrayList<IResource>();
		List<TreeNodeFolder> children = new ArrayList<TreeNodeFolder>();
		
		try {
			DeploymentUtilities.findFolder((IProject)this.resource, results, WEB_CONTENT);
			DeploymentUtilities.findFolder((IProject)this.resource, results,  EGLProjectInfoUtility.getGeneratedJavaScriptFolder((IProject)this.resource));
		} catch (CoreException e) {
			DeploymentUtilities.displayErrorDialog(SOAMessages.TreeNode_0, SOAMessages.TreeNode_1);
			EDTUIPlugin.log(e);
		}
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			IFolder folder = (IFolder) iterator.next();
			TreeNodeFolder node = new TreeNodeJavaScriptFolder(this, folder);
			node.setDeployable(false);
			children.add(node);			
		}
		
		return children;
	}
	
	private List<TreeNodeFolder> generateJavaChildren(){
		List<IResource> results = new ArrayList<IResource>();
		List<TreeNodeFolder> children = new ArrayList<TreeNodeFolder>();
		try {
			DeploymentUtilities.getJavaSourceFolders((IProject)this.resource, results);
		} catch (CoreException e) {
			DeploymentUtilities.displayErrorDialog(SOAMessages.TreeNode_0, SOAMessages.TreeNode_1);
			EDTUIPlugin.log(e);
		}
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			IFolder folder = (IFolder) iterator.next();
			TreeNodeJavaSourceFolder node = new TreeNodeJavaSourceFolder(this, folder, folder.getProjectRelativePath().toString());
			node.setDeployable(false);
			children.add(node);			
		}
		return children;
	}
	
	private List<TreeNodeFolder> generateEglarChildren() {
		List<IResource> results = new ArrayList<IResource>();
		List<TreeNodeFolder> children = new ArrayList<TreeNodeFolder>();
		
		IEGLProject eglProject = EGLCore.create((IProject)this.resource);
		try {
			IEGLPathEntry[] entries = eglProject.getRawEGLPath();
			for ( int j = 0; j < entries.length; j ++ ) {
				IEGLPathEntry entry = entries[j];
				if ( entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY && "eglar".equalsIgnoreCase(entry.getPath().getFileExtension()) ) {
					EglarFile eglar = null;
					if ( entry.getPath().toFile().exists() ) {
						eglar = EglarFileCache.instance.getEglarFile( entry.getPath().toFile() );
					} else {
						eglar = EglarFileCache.instance.getEglarFile( ResourcesPlugin.getWorkspace().getRoot().findMember( entry.getPath() ).getRawLocation().toFile() );
					}
					EglarManifest manifest = eglar.getManifest();
					if ( manifest == null ) {
						continue;
					}
					
					ZipEntry zipEntry = eglar.getEntry( WEB_CONTENT + "/" );
					//TODO fix before checkin
//					if(zipEntry != null){
//						EglarFolderResource folder = new EglarFolderResource( eglar, zipEntry, "" );
//						TreeNodeFolder node = new TreeNodeJavaScriptFolderInEglar(this, folder);
//						node.setDeployable(false);
//						children.add(node);	
//					}	
					
//					String[] jars = manifest.getJavaJars();
//					if(jars != null && jars.length > 0) {
//						IPath jarsPath = new Path(jars[0]);
//						jarsPath = jarsPath.removeLastSegments(1);
//						ZipEntry tempEntry = new ZipEntry(jarsPath + "/");
//						EglarFolderResource folder = new EglarFolderResource( eglar, tempEntry, "" );
//						TreeNodeFolder node = new TreeNodeJarFolderInEglar(this, folder);
//						children.add(node);	
//					}
					
				}
			}
		}catch (Exception e) {
			DeploymentUtilities.displayErrorDialog(SOAMessages.TreeNode_0, SOAMessages.TreeNode_1);
			EDTUIPlugin.log(e);
		}

		return children;
	}

}
