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
package org.eclipse.edt.ide.core.model.bde;



import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.EGLCore;

/**
 * The central access point for models representing plug-ins found in the workspace
 * and in the targret platform.
 * <p>
 * This class provides static methods only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 * 
 * @since 3.3
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PluginRegistry {

	/**
	 * Returns all plug-ins and (possibly) fragments in the workspace as well as all plug-ins and (possibly)
	 *  fragments that are checked on the Target Platform preference page.
	 * <p>
	 * If a workspace plug-in/fragment has the same ID as a target plug-in, the target counterpart
	 * is skipped and not included.
	 * </p>
	 * <p>
	 * The returned result includes fragments only if <code>includeFragments</code>
	 * is set to true
	 * </p>
	 * @return all plug-ins and (possibly) fragments in the workspace as well as all plug-ins and 
	 * (possibly) fragments that are checked on the Target Platform preference page.
	 */
	public static IPluginModelBase[] getActiveModels() {
//		return Activator.getPlugin().getModelManager().getActiveModels();
		return new IPluginModelBase[0];
	}

	/**
	 * Returns all plug-in models in the workspace
	 * 
	 * @return all plug-in models in the workspace
	 */
	public static IPluginModelBase[] getWorkspaceModels() {
//		return Activator.getPlugin().getModelManager().getWorkspaceModels();
		return new IPluginModelBase[0];
	}

	/**
	 * Return the model manager that keeps track of plug-ins in the target platform
	 * 
	 * @return  the model manager that keeps track of plug-ins in the target platform
	 */
	public static IPluginModelBase[] getExternalModels() {
//		return Activator.getPlugin().getModelManager().getExternalModels();
		return new IPluginModelBase[0];
	}
}

