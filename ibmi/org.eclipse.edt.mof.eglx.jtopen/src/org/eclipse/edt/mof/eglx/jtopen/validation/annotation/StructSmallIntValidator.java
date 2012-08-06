/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.jtopen.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.MofConversion;


public abstract class StructSmallIntValidator extends AbstractStructParameterAnnotationValidator {

	protected List<String> getSupportedTypes() {
		List<String> list = new ArrayList<String>();
		list.add(MofConversion.Type_EGLSmallint);
		return list;
	}
}
