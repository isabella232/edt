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
/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.deployment.javascript.HTMLGenerator;
import org.eclipse.edt.gen.deployment.javascript.NoContextHTMLGenerator;
import org.eclipse.edt.ide.rui.utils.DebugFileLocator;
import org.eclipse.edt.ide.rui.utils.DebugIFileLocator;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.rui.utils.IFileLocator;


public class SavedContentProvider extends AbstractContentProvider {

	protected FileLocator getFileLocator(IProject project) throws CoreException {
		return new DebugFileLocator(project);
	}
	
	protected IFileLocator getIFileLocator(IProject project) throws CoreException {
		return new DebugIFileLocator(project);
	}
	
	protected HTMLGenerator getDevelopmentGenerator(AbstractGeneratorCommand processor, String egldd, HashMap eglProperties, String userMsgLocale, String runtimeMsgLocale) {
		return new NoContextHTMLGenerator(processor, egldd, eglProperties, userMsgLocale, runtimeMsgLocale);
	}
}
