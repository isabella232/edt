/*******************************************************************************
 * Copyright Â© 2011 IBM Corporation and others.
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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.edt.compiler.internal.PartWrapper;
import org.eclipse.edt.ide.core.Logger;

public class DefaultDeploymentDescriptorUtility {
	
	public static final String DEFAULT_DEPLOYMENT_DESCRIPTOR_PATH = "DefaultDeploymentDescriptorPath"; //$NON-NLS-1$
	
	public static PartWrapper getDefaultDeploymentDescriptor(IResource resource) {
		PartWrapper partWrapper = null;
		try {
			String partPath = ResourceValueStoreUtility.getInstance().getValue(resource, new QualifiedName(null, getPathKey()));
			String partName = "";
			if (partPath != null && partPath.length() > 0) {
				partName = new Path(partPath).removeFileExtension().lastSegment();
			}
			
			partWrapper = new PartWrapper();
			partWrapper.setPartPath(partPath);
			partWrapper.setPartName(partName);
			
			
		} catch (CoreException e) {
			Logger.log(DefaultDeploymentDescriptorUtility.class, "DefaultDeploymentDescriptorUtility.getDefaultDeploymentDescriptor() - CoreException", e); //$NON-NLS-1$
		}
		return partWrapper;
	}
	
	private static String getPathKey() {		
		return DEFAULT_DEPLOYMENT_DESCRIPTOR_PATH;
	}
	
	public static void setDefaultDeploymentDescriptor(IResource resource, PartWrapper defaultDD) {
		try {
			String pathValue = null;
			String nameValue = null;
			if (defaultDD != null) {
				pathValue = defaultDD.getPartPath();
				nameValue = defaultDD.getPartName();
			}
			// Always store the deployment descriptor values.
			ResourceValueStoreUtility.getInstance().setValue(resource, new QualifiedName(null, getPathKey()), pathValue);
			
			if(defaultDD == null){
				// A value is being cleared.  We need to look for all other values that could have mapped to this value, and remove those as well
				ResourceValueStoreUtility.getInstance().setValue(resource, new QualifiedName(null, getPathKey()), pathValue);
			}
		} catch (CoreException e) {
			Logger.log(DefaultDeploymentDescriptorUtility.class, "DefaultDeploymentDescriptorUtility.setDefaultDeploymentDescriptor() - CoreException", e); //$NON-NLS-1$
		}
	}
}
