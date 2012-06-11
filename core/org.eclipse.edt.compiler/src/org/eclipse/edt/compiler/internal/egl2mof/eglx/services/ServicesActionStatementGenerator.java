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
package org.eclipse.edt.compiler.internal.egl2mof.eglx.services;

import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.egl2mof.AbstractIOStatementGenerator;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.eglx.services.ServicesCallStatement;
import org.eclipse.edt.mof.eglx.services.ServicesFactory;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class ServicesActionStatementGenerator extends AbstractIOStatementGenerator {
	
	ServicesFactory factory = ServicesFactory.INSTANCE;
	final ServicesActionStatementGenerator generator = this;

	
	public ServicesActionStatementGenerator() {
		super(null);
	}
	
	public ServicesActionStatementGenerator(IEnvironment env) {
		super(env);
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.CallStatement callStatement) {
		ServicesCallStatement stmt = factory.createServicesCallStatement();
		callStatement.getInvocationTarget().accept(this);
		stmt.setInvocationTarget((Expression)stack.pop());
		if (callStatement.hasArguments()) {
			for (Node node : (List<Node>)callStatement.getArguments()) {
				node.accept(this);
				stmt.getArguments().add((Expression)stack.pop());
			}
		}

		if (callStatement.getUsing() != null) {
			callStatement.getUsing().accept(this);
			stmt.setUsing((Expression)stack.pop());
		}
		
		if (callStatement.getCallSynchronizationValues() != null) {
			if (callStatement.getCallSynchronizationValues().getReturnTo() != null) {
				callStatement.getCallSynchronizationValues().getReturnTo().accept(this);
				stmt.setCallback((Expression)stack.pop());
			}
			if (callStatement.getCallSynchronizationValues().getOnException() != null) {
				callStatement.getCallSynchronizationValues().getOnException().accept(this);
				stmt.setErrorCallback((Expression)stack.pop());
			}
			if (callStatement.getCallSynchronizationValues().getReturns() != null) {
				callStatement.getCallSynchronizationValues().getReturns().accept(this);
				stmt.setReturns((LHSExpr)stack.pop());
			}
		}
		stack.push(stmt);
		if (callStatement.hasSettingsBlock())
			processSettings(stmt, callStatement.getSettingsBlock());
		return false;
	}
	
}
