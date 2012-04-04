/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.document.utils;

import org.eclipse.edt.mof.egl.utils.InternUtil;

public interface IVEConstants {

	public static final String BOOLEAN_TYPE = InternUtil.intern("boolean");
	public static final String CHOICE_TYPE = InternUtil.intern("choice");
	public static final String INTEGER_TYPE = InternUtil.intern("integer");
	public static final String STRING_TYPE = InternUtil.intern("string");
	public static final String STRING_ARRAY_TYPE = InternUtil.intern("stringarray");
	public static final Object COLOR_TYPE = InternUtil.intern("color");
	
	public static final String TYPE_NAME_VARIABLE = "${typeName:";
	
}
