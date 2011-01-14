/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
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

import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;

public class QualifiedFunctionInvocationImpl extends InvocationExpressionImpl implements QualifiedFunctionInvocation {
	private static int Slot_target=0;
	private static int Slot_qualifier=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + InvocationExpressionImpl.totalSlots();
	}
	
	static {
		int offset = InvocationExpressionImpl.totalSlots();
		Slot_target += offset;
		Slot_qualifier += offset;
	}
	@Override
	public FunctionMember getTarget() {
		if (slotGet(Slot_target) == null) {
			try {
				setTarget(resolveFunction());
			} catch (NoSuchFunctionError e) {
				throw new RuntimeException(e);
			}
		}
		return (FunctionMember)slotGet(Slot_target);
	}
	
	@Override
	public void setTarget(FunctionMember value) {
		slotSet(Slot_target, value);
	}
	
	@Override
	public Expression getQualifier() {
		return (Expression)slotGet(Slot_qualifier);
	}
	
	@Override
	public void setQualifier(Expression value) {
		slotSet(Slot_qualifier, value);
	}

	@Override
	public QualifiedFunctionInvocation addQualifier(Expression expr) {
		if (getQualifier() instanceof MemberAccess) {
			setQualifier( ((MemberAccess)getQualifier()).addQualifier(expr));
		}
		else if (getQualifier() instanceof MemberName){
			setQualifier(((MemberName)getQualifier()).addQualifier(expr));
		}
		else if (getQualifier() instanceof InvocationExpression) {
			setQualifier(((InvocationExpression)getQualifier()).addQualifier(expr));
		}
		return this;
	}
	
	@Override
	public Type getType() {
		Type type = getTarget().getType();
		if (type instanceof GenericType && type.getClassifier() == null) {
			type = ((GenericType)type).resolveTypeParameter(getQualifier().getType());
		}
		return type;
	}
	
	@Override
	public Type getParameterTypeForArg(int index) {
		Type type = super.getParameterTypeForArg(index);
		if (type instanceof GenericType && type.getClassifier() == null) {
			type = ((GenericType)type).resolveTypeParameter(getQualifier().getType());
		}
		return type;

	}
	
	// TODO: Handle function overloading
	private FunctionMember resolveFunction() throws NoSuchFunctionError {
		Container container = (Container)getQualifier().getType().getClassifier();
		FunctionMember result = null;
		for (Member mbr : container.getMembers()) {
			if (mbr.getId().equals(getId())) { 
				result = (FunctionMember)mbr;
				break;
			}
		}
		if (result == null)
			throw new NoSuchFunctionError("Member not found: " + getId());
		
		return result;
	}
	
	

}