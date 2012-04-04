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
package org.eclipse.edt.mof.egl;


public interface LHSExpr extends Expression {
	
	/**
	 * Returns true if LHSExpr is able to semantically "hold" the 'null' value 
	 * Examples of this would be MemberAccess expressions that reference nullable Fields
	 * or ArrayAccess expressions that reference ArrayType declarations that have the
	 * ArrayType.elementsNullable flag set on
	 */
	boolean isNullable();
	
	LHSExpr addQualifier(Expression expr);
}
