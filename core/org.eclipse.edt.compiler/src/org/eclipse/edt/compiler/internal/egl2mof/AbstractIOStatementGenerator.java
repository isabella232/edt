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
package org.eclipse.edt.compiler.internal.egl2mof;

import java.util.Map;

import org.eclipse.edt.compiler.Context;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.AddStatement;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.CloseStatement;
import org.eclipse.edt.mof.egl.ConverseStatement;
import org.eclipse.edt.mof.egl.DeleteStatement;
import org.eclipse.edt.mof.egl.DisplayStatement;
import org.eclipse.edt.mof.egl.ExecuteStatement;
import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.GetByKeyStatement;
import org.eclipse.edt.mof.egl.GetByPositionKind;
import org.eclipse.edt.mof.egl.GetByPositionStatement;
import org.eclipse.edt.mof.egl.IOStatement;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.OpenStatement;
import org.eclipse.edt.mof.egl.PrepareStatement;
import org.eclipse.edt.mof.egl.ReplaceStatement;
import org.eclipse.edt.mof.egl.ShowStatement;
import org.eclipse.edt.mof.serialization.IEnvironment;


public class AbstractIOStatementGenerator extends Egl2Mof implements
		IOStatementGenerator {
	
	public IrFactory irFactory = IrFactory.INSTANCE;

	public AbstractIOStatementGenerator(IEnvironment env) {
		super(env);
	}

	@Override
	public CallStatement genCallStatement(org.eclipse.edt.compiler.core.ast.CallStatement stmt, 
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (CallStatement)stack.pop();
	}
	
	@Override
	public AddStatement genAddStatement(org.eclipse.edt.compiler.core.ast.AddStatement stmt, 
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (AddStatement)stack.pop();
	}
	
	@Override
	public CloseStatement genCloseStatement(
			org.eclipse.edt.compiler.core.ast.CloseStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (CloseStatement)stack.pop();
	}


	@Override
	public ConverseStatement genConverseStatement(org.eclipse.edt.compiler.core.ast.ConverseStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (ConverseStatement)stack.pop();

	}
	
	@Override
	public DeleteStatement genDeleteStatement(
			org.eclipse.edt.compiler.core.ast.DeleteStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (DeleteStatement)stack.pop();
	}

	@Override
	public ExecuteStatement genExecuteStatement(
			org.eclipse.edt.compiler.core.ast.ExecuteStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (ExecuteStatement)stack.pop();
	}

	@Override
	public ForEachStatement genForEachStatement(
			org.eclipse.edt.compiler.core.ast.ForEachStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (ForEachStatement)stack.pop();
	}

	@Override
	public GetByKeyStatement genGetByKeyStatement(
			org.eclipse.edt.compiler.core.ast.GetByKeyStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (GetByKeyStatement)stack.pop();
	}

	@Override
	public GetByPositionStatement genGetByPositionStatement(
			org.eclipse.edt.compiler.core.ast.GetByPositionStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (GetByPositionStatement)stack.pop();
	}

	@Override
	public OpenStatement genOpenStatement(
			org.eclipse.edt.compiler.core.ast.OpenStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (OpenStatement)stack.pop();
	}

	@Override
	public PrepareStatement genPrepareStatement(
			org.eclipse.edt.compiler.core.ast.PrepareStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (PrepareStatement)stack.pop();
	}

	@Override
	public ReplaceStatement genReplaceStatement(
			org.eclipse.edt.compiler.core.ast.ReplaceStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (ReplaceStatement)stack.pop();
	}

	@Override
	public OpenUIStatement genOpenUIStatement(org.eclipse.edt.compiler.core.ast.OpenUIStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (OpenUIStatement)stack.pop();

	}

	@Override
	public ShowStatement genShowStatement(org.eclipse.edt.compiler.core.ast.ShowStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (ShowStatement)stack.pop();
	}

	@Override
	public DisplayStatement genDisplayStatement(
			org.eclipse.edt.compiler.core.ast.DisplayStatement stmt,
			Map<IBinding, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		stmt.accept(this);
		return (DisplayStatement)stack.pop();
	}
	

	public boolean visit(IOStatement stmt) {
		System.out.println("Unhandled IOStatement type: " + stmt.getClass().getName());
		stack.push(null);
		return false;
	}
	
	public GetByPositionKind getDirective(org.eclipse.edt.compiler.core.ast.GetByPositionStatement node) {
		if (node.isAbsoluteDirection()) {
			return GetByPositionKind.ABSOLUTE;
		}
		if (node.isCurrentDirection()) {
			return GetByPositionKind.CURRENT;
		}
		if (node.isFirstDirection()) {
			return GetByPositionKind.FIRST;
		}
		if (node.isLastDirection()) {
			return GetByPositionKind.LAST;
		}
		if (node.isNextDirection()) {
			return GetByPositionKind.NEXT;
		}
		if (node.isPreviousDirection()) {
			return GetByPositionKind.PREVIOUS;
		}
		if (node.isRelativeDirection()) {
			return GetByPositionKind.RELATIVE;
		}

		return null;
	}

	@Override
	public void setCurrentPart(MofSerializable currentPart) {
		this.currentPart = currentPart;
	}
	
	@Override
	public void setCurrentFunction(FunctionMember currentFunc) {
		this.currentFunction = currentFunc;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

}
