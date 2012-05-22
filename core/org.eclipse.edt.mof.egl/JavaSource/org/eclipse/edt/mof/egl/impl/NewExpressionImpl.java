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

import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.NoSuchMemberError;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;


public class NewExpressionImpl extends InvocationExpressionImpl implements NewExpression {
	private static int Slot_target=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + InvocationExpressionImpl.totalSlots();
	}
	
	static {
		int offset = InvocationExpressionImpl.totalSlots();
		Slot_target += offset;
	}
	@Override
	public Constructor getTarget() {
		if (slotGet(Slot_target)== null) {
			try {
				resolveTarget();
			} catch (MofObjectNotFoundException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMemberError e) {
				throw new RuntimeException(e);
			}
		}
		return (Constructor)slotGet(Slot_target);
	}
	
	@Override
	public void setTarget(Constructor value) {
		slotSet(Slot_target, value);
	}

	@Override
	public Type getType() {
		return IRUtils.getEGLType(getId());
	}
	
	private void resolveTarget() throws MofObjectNotFoundException, NoSuchMemberError {
		// TODO Make NewExpression have an explicit type reference instead
		// of just an ID to make the type lookup work the same as
		// everywhere else
		Type type = IRUtils.getEGLType(getId());
		if (type == null) {
			throw new MofObjectNotFoundException(getId());
		}
		Constructor result = IRUtils.resolveConstructorReference((EGLClass)type.getClassifier(), getArguments());
		if (result == null) {
			throw new NoSuchMemberError("Member not found: " + getId());
		}
		setTarget(result);
	}

	@Override
	public QualifiedFunctionInvocation addQualifier(Expression expr) {
		throw new UnsupportedOperationException();
	}
}
