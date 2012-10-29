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

import org.eclipse.edt.mof.utils.NameUtile;

public interface IVEConstants {

	public static final String BOOLEAN_TYPE = NameUtile.getAsName("boolean");
	public static final String CHOICE_TYPE = NameUtile.getAsName("choice");
	public static final String INTEGER_TYPE = NameUtile.getAsName("integer");
	public static final String STRING_TYPE = NameUtile.getAsName("string");
	public static final String STRING_ARRAY_TYPE = NameUtile.getAsName("stringarray");
	public static final Object COLOR_TYPE = NameUtile.getAsName("color");
	
	public static final String TYPE_NAME_VARIABLE = "${typeName:";
	
}
