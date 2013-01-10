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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.FunctionPartInvocation;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class FunctionPartInvocationImpl extends FunctionInvocationImpl implements FunctionPartInvocation {
	private static int Slot_packageName=0;
	private static int Slot_functionPart=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + InvocationExpressionImpl.totalSlots();
	}
	
	static {
		int offset = FunctionInvocationImpl.totalSlots();
		Slot_packageName += offset;
		Slot_functionPart += offset;
	}
	@Override
	public String getPackageName() {
		return (String)slotGet(Slot_packageName);
	}
	
	@Override
	public void setPackageName(String value) {
		slotSet(Slot_packageName, value);
	}
	
	@Override
	public FunctionPart getFunctionPart() {
		if (slotGet(Slot_functionPart) == null) {
			setFunctionPart(resolveFunctionPart());
		}
		return (FunctionPart)slotGet(Slot_functionPart);
	}
	
	@Override
	public void setFunctionPart(FunctionPart value) {
		slotSet(Slot_functionPart, value);
	}
	
	@Override
	public Type getType() {
		return getFunction().getType();
	}
	
	private FunctionPart resolveFunctionPart() {
		return (FunctionPart)IRUtils.getEGLType(getFullyQualifiedName());
	}
	
	@Override
	public String getFullyQualifiedName() {
		return getPackageName() + "." + getId();
	}

	@Override
	public FunctionMember getTarget() {
		return getFunctionPart().getFunction();
	}

	@Override
	public void setTarget(Member value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FunctionMember getFunction() {
		return getFunctionPart().getFunction();
	}

	@Override
	public boolean isNullable() {
		return getFunction().isNullable();
	}
	
	
}
