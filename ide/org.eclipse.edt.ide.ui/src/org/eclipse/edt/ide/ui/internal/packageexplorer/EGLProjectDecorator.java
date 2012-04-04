/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.packageexplorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

public class EGLProjectDecorator extends LabelProvider implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		try {
			if(element instanceof IProject) {
				IProject project = (IProject)element;
				if( project.hasNature( EGLCore.NATURE_ID ) ) {
					decoration.addOverlay( PluginImages.DESC_OVR_EGL );
					IEGLProject eglProject = EGLCore.create( project );
					if( eglProject.isBinary() ) {
						decoration.addOverlay( PluginImages.DESC_OBJS_EGL_BINARY_PROJECT_OPEN );
					}
				}
			}
		} catch( CoreException e ) {
			EGLLogger.log(this, e);
		}
	}


}
