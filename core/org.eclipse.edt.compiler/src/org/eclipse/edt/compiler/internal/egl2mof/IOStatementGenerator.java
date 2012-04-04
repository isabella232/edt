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
package org.eclipse.edt.compiler.internal.egl2mof;

import java.util.HashMap;
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
import org.eclipse.edt.mof.egl.GetByPositionStatement;
import org.eclipse.edt.mof.egl.OpenStatement;
import org.eclipse.edt.mof.egl.PrepareStatement;
import org.eclipse.edt.mof.egl.ReplaceStatement;
import org.eclipse.edt.mof.egl.ShowStatement;
import org.eclipse.edt.mof.serialization.IEnvironment;


public interface IOStatementGenerator {
	Map<String, Class<? extends IOStatementGenerator>> Registry = new HashMap<String, Class<? extends IOStatementGenerator>>();

	void setEnvironment(IEnvironment env);
	void setContext(Context context);
	
	CallStatement genCallStatement(org.eclipse.edt.compiler.core.ast.CallStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	AddStatement genAddStatement(org.eclipse.edt.compiler.core.ast.AddStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	CloseStatement genCloseStatement(org.eclipse.edt.compiler.core.ast.CloseStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	ConverseStatement genConverseStatement(org.eclipse.edt.compiler.core.ast.ConverseStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	DeleteStatement genDeleteStatement(org.eclipse.edt.compiler.core.ast.DeleteStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	DisplayStatement genDisplayStatement(org.eclipse.edt.compiler.core.ast.DisplayStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	ExecuteStatement genExecuteStatement(org.eclipse.edt.compiler.core.ast.ExecuteStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	ForEachStatement genForEachStatement(org.eclipse.edt.compiler.core.ast.ForEachStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	GetByKeyStatement genGetByKeyStatement(org.eclipse.edt.compiler.core.ast.GetByKeyStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	GetByPositionStatement genGetByPositionStatement(org.eclipse.edt.compiler.core.ast.GetByPositionStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	OpenStatement genOpenStatement(org.eclipse.edt.compiler.core.ast.OpenStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	OpenUIStatement genOpenUIStatement(org.eclipse.edt.compiler.core.ast.OpenUIStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	PrepareStatement genPrepareStatement(org.eclipse.edt.compiler.core.ast.PrepareStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	ReplaceStatement genReplaceStatement(org.eclipse.edt.compiler.core.ast.ReplaceStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	ShowStatement genShowStatement(org.eclipse.edt.compiler.core.ast.ShowStatement stmt, Map<IBinding, EObject> bindingToElementMap );
	void setCurrentPart(MofSerializable currentPart);
	void setCurrentFunction(FunctionMember currentFunc);
}
