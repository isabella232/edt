/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class EGLProjectFileUtilityLocator {

	public static EGLProjectFileUtilityLocator INSTANCE = new EGLProjectFileUtilityLocator();
	private boolean attemptedToLoadUtil = false;
	private IEGLProjectFileUtility util = null;

	private EGLProjectFileUtilityLocator() {
		super();
	}

	public IEGLProjectFileUtility getUtil() {
		if (!attemptedToLoadUtil) {
			attemptedToLoadUtil = true;

			IConfigurationElement[] elements = Platform.getExtensionRegistry()
					.getConfigurationElementsFor("org.eclipse.edt.ide.ui.eglprojectFileUtility"); //$NON-NLS-1$

			if (elements != null && elements.length > 0) {
				try {
					util = (IEGLProjectFileUtility)elements[0].createExecutableExtension("class");
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return util;
	}

}
