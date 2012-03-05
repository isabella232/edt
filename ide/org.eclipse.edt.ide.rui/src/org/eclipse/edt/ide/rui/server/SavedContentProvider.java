/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.server;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.deployment.javascript.HTMLGenerator;
import org.eclipse.edt.gen.deployment.javascript.NoContextHTMLGenerator;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.rui.utils.DebugFileLocator;
import org.eclipse.edt.ide.rui.utils.DebugIFileLocator;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.rui.utils.IFileLocator;
import org.eclipse.edt.mof.serialization.IEnvironment;


public class SavedContentProvider extends AbstractContentProvider {

	protected FileLocator getFileLocator(IProject project) throws CoreException {
		return new DebugFileLocator(project);
	}
	
	protected IFileLocator getIFileLocator(IProject project) throws CoreException {
		return new DebugIFileLocator(project);
	}
	
	protected HTMLGenerator getDevelopmentGenerator(AbstractGeneratorCommand processor, List egldds,
			HashMap eglProperties, String userMsgLocale, String runtimeMsgLocale) {
		return new NoContextHTMLGenerator(processor, egldds, eglProperties, userMsgLocale, runtimeMsgLocale);
	}

	@Override
	protected IEnvironment getEnvironmentForGeneration(IProject project) {
		return ProjectEnvironmentManager.getInstance().getIREnvironment(project);
	}
}
