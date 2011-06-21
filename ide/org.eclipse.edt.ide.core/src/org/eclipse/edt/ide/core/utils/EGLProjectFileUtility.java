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
package org.eclipse.edt.ide.core.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.edt.ide.core.internal.model.util.IEGLProjectFileUtility;

public class EGLProjectFileUtility implements IEGLProjectFileUtility {

	public boolean isBinaryProject(IProject proj) {
		try {
			String value = ResourceValueStoreUtility.getInstance().getValue(proj, new QualifiedName(null, IEGLProjectFileUtility.BINARYPROJECT_KEY), false);
			return ("true".equalsIgnoreCase(value));
		} catch (CoreException e) {
		}
		return false;
	}
	

}
