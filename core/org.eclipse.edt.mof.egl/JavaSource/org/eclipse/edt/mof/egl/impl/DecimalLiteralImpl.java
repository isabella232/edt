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

import java.math.BigDecimal;

import org.eclipse.edt.mof.egl.DecimalLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class DecimalLiteralImpl extends NumericLiteralImpl implements DecimalLiteral {
	
	@Override
	public Type getType() {
		int i = getValue().indexOf('.');
		String val = getUnsignedValue();
		int decimals = val.substring(i+1).length();
		return IRUtils.getEGLPrimitiveType(Type_Decimal, val.length()-1, decimals);
	}

	@Override
	public Object getObjectValue() {
		return new BigDecimal( getValue() );
	}
}
