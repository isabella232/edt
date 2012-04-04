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
package org.eclipse.edt.compiler.internal.core.lookup.Enumerations;

import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;

/**
 * @author Harmon
 */
public abstract class Enumeration {
    
	public static final int ALIGNKIND				= 1;
	public static final int BOOLEAN					= 2;
	public static final int CALLINGCONVENTIONKIND	= 3;
	public static final int CASEFORMATKIND			= 4;
	public static final int COLORKIND				= 5;
	public static final int COMMTYPEKIND			= 6;
	public static final int DATASOURCE				= 7;
	public static final int DEVICETYPEKIND			= 8;
	public static final int DISPLAYUSEKIND			= 9;
	public static final int DLICALLINTERFACEKIND	= 10;
	public static final int EVENTKIND				= 11;
	public static final int EXPORTFORMAT			= 12;
	public static final int HIGHLIGHTKIND			= 13;
	public static final int INDEXORIENTATIONKIND	= 14;
    public static final int INTENSITYKIND			= 15;
    public static final int LINEWRAPKIND			= 16;
    public static final int OUTLINEKIND				= 17;
    public static final int PCBKIND					= 18;
    public static final int PFKEYKIND				= 19;
    public static final int PROTECTKIND				= 20;
    public static final int SELECTTYPEKIND			= 21;
    public static final int SIGNKIND				= 22;
    public static final int UITYPEKIND				= 23;
    public static final int WINDOWATTRIBUTEKIND		= 24;
    public static final int ORDERINGKIND			= 25;
    public static final int SCOPEKIND				= 26;
    public static final int FILLCHARACTERKIND		= 27;
    public static final int ELEMENTKIND				= 28;
    public static final int TYPEKIND				= 29;
    public static final int WHITESPACEKIND			= 30;
    public static final int ELEMENTTYPEKIND			= 31;
    public static final int RESTFORMATKIND			= 32;
    public static final int PARAMETERMODFIERKIND	= 33;
    
    public abstract EnumerationTypeBinding getType();
    public abstract boolean isResolvable();
}
