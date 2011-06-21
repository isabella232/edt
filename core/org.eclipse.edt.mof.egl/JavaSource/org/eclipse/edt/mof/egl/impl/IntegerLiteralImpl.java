/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.math.BigInteger;

import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class IntegerLiteralImpl extends NumericLiteralImpl implements IntegerLiteral {
	
	private Type type = null;
	
	@Override
	public Integer getIntValue() {
		return Integer.decode(getValue());
	}
		
	@Override
	public Type getType() {
		if (type == null) {
			String value = getUnsignedValue();
			BigInteger bigInt = new BigInteger(value);
			
			if (bigInt.bitLength() < 16) {
				type = IRUtils.getEGLPrimitiveType(Type_Smallint);
				return type;
			}

			if (bigInt.bitLength() < 32) {
				type = IRUtils.getEGLPrimitiveType(Type_Int);
				return type;
			}

			if (bigInt.bitLength() < 64) {
				type = IRUtils.getEGLPrimitiveType(Type_Bigint);
				return type;
			}
			
			type = IRUtils.getEGLPrimitiveType(Type_Num, value.length(), 0);
			return type;
		}
		return type;
	}

}
