/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IProblemRequestor;

/**
 * @author twilson
 * created	Aug 1, 2003
 */
public class EGLFileProblemFinder {
	
	public static void process(
		IEGLFile unitElement, 
		IProblemRequestor problemRequestor,
		IProgressMonitor monitor)
		throws EGLModelException {
			
			// TODO implement
		}
}
