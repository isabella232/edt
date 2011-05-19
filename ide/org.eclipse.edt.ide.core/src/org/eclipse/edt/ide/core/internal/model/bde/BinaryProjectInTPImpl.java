/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.bde;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.edt.ide.core.model.bde.PluginRegistry;
import org.eclipse.edt.ide.core.model.bde.WorkspacePluginModel;

import org.eclipse.edt.ide.core.internal.model.IBinaryProjectInTP;

public class BinaryProjectInTPImpl implements IBinaryProjectInTP {

	public boolean isProjectDisabledInTP(String projectName) {
		IPluginModelBase[] bases = PluginRegistry.getActiveModels();
		for(IPluginModelBase base : bases) {
			if(base instanceof WorkspacePluginModel || (base.getInstallLocation() == null)) {
				continue;
			}
			IPath path = new Path(base.getInstallLocation());
			int segLen = path.segmentCount();
			if(segLen > 1) {
				if(path.segments()[segLen - 1].equals(projectName)) {
					return false;
				}
			}
		}
		return true;
	}

}
