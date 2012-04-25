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
package org.eclipse.edt.ide.compiler.gen;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.mof.egl.Part;

/**
 * Subclass of the base Java generator command to do certain things in the Eclipse way.
 */
public class EclipseEGL2JavaEE extends EclipseEGL2Java {

	public EclipseEGL2JavaEE(IFile eglFile, Part part, IGenerator generator) {
		super(eglFile, part, generator);
	}
}
