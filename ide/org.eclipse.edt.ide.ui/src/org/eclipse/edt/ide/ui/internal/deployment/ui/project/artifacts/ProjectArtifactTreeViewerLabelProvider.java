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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.LabelProvider;

/**
 *
 */
public class ProjectArtifactTreeViewerLabelProvider extends LabelProvider {

	/**
	 * 
	 */
	public ProjectArtifactTreeViewerLabelProvider() {
	}
	
	public String getText(Object element) {
		if (element instanceof TreeNodeJavaSourceFolder){
			return ((TreeNodeJavaSourceFolder)element).getName();
		}else if (element instanceof TreeNode) {
			IResource res = ((TreeNode)element).getResource();
			//TODO fix before checkin
//			if ( res instanceof EglarResource ) {
//				EglarResource eglarRes = (EglarResource)res;
//				return eglarRes.getName() + " (" + eglarRes.getEglarFile().getName() + ")";
//			} else {
				return ((TreeNode)element).getResource().getName();
//			}
		}
		return super.getText(element);
	}

}
