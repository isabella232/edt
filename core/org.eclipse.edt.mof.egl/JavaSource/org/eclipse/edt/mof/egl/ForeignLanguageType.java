/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl;


public interface ForeignLanguageType extends Type {
	public final static int OBJIDJAVA = 1;
	public final static int JAVACHAR = 2;
	public final static int JAVABIGDECIMAL = 3;
	public final static int JAVABIGINTEGER = 4;
	public final static int JAVABYTE = 5;
	public final static int JAVADOUBLE = 6;
	public final static int JAVAFLOAT = 7;
	public final static int JAVASHORT = 8;
	public final static int JAVAINT = 9;
	public final static int JAVALONG = 10;
	public final static int JAVABOOLEAN = 11;
	public final static int NULL = 12;

	
	public int getForeignLanguageConstant();
	public String getName();
	
}
