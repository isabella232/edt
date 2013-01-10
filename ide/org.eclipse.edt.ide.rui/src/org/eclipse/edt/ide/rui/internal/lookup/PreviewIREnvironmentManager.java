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
package org.eclipse.edt.ide.rui.internal.lookup;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class PreviewIREnvironmentManager {
	private static String EGL_SCHEMA = Type.EGL_KeyScheme + Type.KeySchemeDelimiter;

	public static IEnvironment getPreviewIREnvironment(IProject project, File contextRoot) {
		ProjectEnvironment projectEnvironment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
		projectEnvironment.initIREnvironments();

		PreviewIREnvironment previewIREnvironment = new PreviewIREnvironment(projectEnvironment.getIREnvironment(),
				contextRoot);
		return previewIREnvironment;
	}

	public static String makeEGLKey(String key) {
		if (key != null && !key.startsWith(EGL_SCHEMA)) {
			key = EGL_SCHEMA + key;
		}
		return key;
	}

}
