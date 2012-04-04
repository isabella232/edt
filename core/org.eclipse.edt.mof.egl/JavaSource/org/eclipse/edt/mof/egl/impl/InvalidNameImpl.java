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
import org.eclipse.edt.mof.egl.InvalidName;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Type;

public class InvalidNameImpl extends NameImpl implements InvalidName {

	@Override
	public Type getType() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public NamedElement getNamedElement() {
		return null;
	}

	@Override
	public MemberAccess addQualifier(Expression expr) {
		MemberAccess mbrAccess = IrFactory.INSTANCE.createMemberAccess();
		mbrAccess.setId(getId());
		mbrAccess.setQualifier(expr);
		return mbrAccess;
	}

}
