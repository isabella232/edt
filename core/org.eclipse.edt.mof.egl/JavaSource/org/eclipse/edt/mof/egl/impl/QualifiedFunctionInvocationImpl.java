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

import java.util.List;

import org.eclipse.edt.mof.egl.AmbiguousFunctionReferenceError;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

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
			} catch (Exception e) {
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
		
		QualifiedFunctionInvocation newInv = IrFactory.INSTANCE.createQualifiedFunctionInvocation();
		newInv.setId(getId());
		newInv.getArguments().addAll(getArguments());
		newInv.setQualifier(getQualifier());
		newInv.getAnnotations().addAll(getAnnotations());
		
		if (getQualifier() instanceof LHSExpr) {
			newInv.setQualifier(((LHSExpr)getQualifier()).addQualifier(expr));
		}
		else if (getQualifier() instanceof InvocationExpression) {
			newInv.setQualifier(((InvocationExpression)getQualifier()).addQualifier(expr));
		}
		else if (getQualifier() instanceof ThisExpression) {
			newInv.setQualifier(expr);
		}
		return newInv;
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
	
	private Function resolveFunction() {
		StructPart container = (StructPart)getQualifier().getType().getClassifier();
		NamedElement[] argTypes = new NamedElement[getArguments().size()];
		int i = 0;
		for (Expression expr : getArguments()) {
			
			if (expr instanceof Name && ((Name)expr).getNamedElement() instanceof Function) {
				argTypes[i] = (Function) ((Name)expr).getNamedElement();
			}
			else {			
				argTypes[i] = (Classifier)expr.getType().getClassifier();
			}			
			i++;
		}
		List<Function> result = null;
		result = TypeUtils.getBestFitFunction(container, getId(), argTypes);
		if (result.isEmpty()) throw new NoSuchFunctionError();
		if (result.size() > 1) throw new AmbiguousFunctionReferenceError();
		return result.get(0);
	}
	
	@Override
	public boolean isNullable() {
		return getTarget().isNullable();
	}

}
