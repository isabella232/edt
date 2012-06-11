/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.util;

import java.util.List;
import java.util.Stack;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.LoopStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.MultiOperandExpression;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.WhileStatement;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.impl.EObjectImpl;

public class ExpressionParenter  extends AbstractVisitor{
	
	private Stack<Statement> stmtStack = new Stack<Statement>();
	private Part part;
	
	public ExpressionParenter(Part part) {
		disallowRevisit();
		this.part = part;
		part.accept(this);
	}


	public boolean visit(Part part) {
		//do not visit any parts besides the one we are interested in
		return this.part == part;
	}


	
	public void endVisit(Expression exp) {

		if (!stmtStack.isEmpty()) {
			Statement stmt = stmtStack.peek();
			Annotation loc = stmt.getAnnotation(IEGLConstants.EGL_LOCATION);
			if (loc != null) {
				Annotation ann = IrFactory.INSTANCE.createDynamicAnnotation(IEGLConstants.EGL_STATEMENTLOCATION);
				ann.setValue(IEGLConstants.MNEMONIC_VALUE, loc);
				exp.addAnnotation(ann);
			}
		}
	}
	
	public boolean visit(Statement stmt) {
		stmtStack.push(stmt);
		return true;
	}
	
	public void endVisit(Statement stmt) {
		if (!stmtStack.isEmpty()) {
			stmtStack.pop();
		}
	}
		
	
}
