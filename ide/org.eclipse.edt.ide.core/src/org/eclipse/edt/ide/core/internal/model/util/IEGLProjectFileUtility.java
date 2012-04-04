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
package org.eclipse.edt.ide.core.internal.model.util;

import org.eclipse.core.resources.IProject;

public interface IEGLProjectFileUtility {
	static final String BINARYPROJECT_KEY = "BinaryProject";
	
	boolean isBinaryProject(IProject proj);
}
