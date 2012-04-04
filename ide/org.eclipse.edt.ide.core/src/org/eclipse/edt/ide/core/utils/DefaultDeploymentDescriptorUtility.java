/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.PartWrapper;
import org.eclipse.edt.ide.core.Logger;
import org.osgi.service.prefs.BackingStoreException;

public class DefaultDeploymentDescriptorUtility {
	
	public static PartWrapper getDefaultDeploymentDescriptor(IResource resource) {
		PartWrapper partWrapper = null;
		
		String partPath = ProjectSettingsUtility.getDefaultDeploymentDescriptor(resource);
		String partName = "";
		if (partPath != null && partPath.length() > 0) {
			partName = new Path(partPath).removeFileExtension().lastSegment();
		}
		
		partWrapper = new PartWrapper();
		partWrapper.setPartPath(partPath);
		partWrapper.setPartName(partName);
		
		return partWrapper;
	}
	
	public static void setDefaultDeploymentDescriptor(IResource resource, PartWrapper defaultDD) {
		try {
			String pathValue = null;
			if (defaultDD != null) {
				pathValue = defaultDD.getPartPath();
			}
			// Always store the deployment descriptor values.
			ProjectSettingsUtility.setDefaultDeploymentDescriptor(resource.getProject(), pathValue);
			
		} catch (BackingStoreException e) {
			Logger.log(DefaultDeploymentDescriptorUtility.class, "DefaultDeploymentDescriptorUtility.setDefaultDeploymentDescriptor() - BackingStoreException", e); //$NON-NLS-1$
		}
	}
}
