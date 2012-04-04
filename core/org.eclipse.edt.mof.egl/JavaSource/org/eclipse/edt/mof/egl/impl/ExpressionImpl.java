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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;

public abstract class ExpressionImpl extends ElementImpl implements Expression {
	
	@Override
	public abstract Type getType();
	
	@Override
	public Expression getQualifier() {
		// Default implementation returns nothing
		return null;
	}
	
	@Override
	public boolean isNullable() {
		return false;
	}
}
