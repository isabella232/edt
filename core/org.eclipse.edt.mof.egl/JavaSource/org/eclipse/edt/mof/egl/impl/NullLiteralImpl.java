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

import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class NullLiteralImpl extends LiteralImpl implements NullLiteral, MofConversion {

	@Override
	public Type getType() {
		return IRUtils.getEGLType(Type_NULL);
	}

	@Override
	public boolean isNullable() {
		return true;
	}
	
	
}
