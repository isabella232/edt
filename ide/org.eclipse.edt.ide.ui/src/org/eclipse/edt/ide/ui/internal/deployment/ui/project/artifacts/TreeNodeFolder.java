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

import java.util.Iterator;

import org.eclipse.core.resources.IResource;

/**
 *
 */
public class TreeNodeFolder extends TreeNode {

	private Boolean hasEnabledFiles = null;
	private boolean supportDynamicLoading = false;
	/**
	 * 
	 */
	public TreeNodeFolder(TreeNode parent, IResource resource) {
		super(parent, resource);
	}

	/**
     * @return <code>true</code> if this node has any children
     */
    public boolean hasChildren() {
    	return ! getChildren().isEmpty();
    }
    
    public void forceRescanForEnabledFiles(){
    	hasEnabledFiles = null;
    }
    public Boolean hasEnabledFiles(){
    	if( hasEnabledFiles == null ){
    		hasEnabledFiles = setHasEnabledFiles();
    	}
    	return hasEnabledFiles;
    }
    
    private Boolean setHasEnabledFiles(){
    	if( hasChildren() ){
    		for( Iterator itr = getChildren().iterator(); itr.hasNext(); ){
    			TreeNode node = (TreeNode)itr.next();
    			if( node instanceof TreeNodeFile && 
    				((TreeNodeFile)node).isChecked() ){
    				return Boolean.TRUE;
    			}
    			else if ( node instanceof TreeNodeFolder && 
    					((TreeNodeFolder)node).hasEnabledFiles() ){
    				
    				return Boolean.TRUE;
    			}
    		}
    	}
    	return Boolean.FALSE;
    }
}
