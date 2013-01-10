/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class DefaultElementGenerator extends AbstractElementGenerator {
	
	public DefaultElementGenerator() {
		super(null);
	}

	public DefaultElementGenerator(IEnvironment env) {
		super(env);
	}

	@Override
	public boolean visit(AddStatement node) {
		stack.push(factory.createAddStatement());
		return false;
	}
	
	@Override
	public boolean visit(CallStatement callStatement) {
		stack.push(factory.createCallStatement());
		return false;
	};
	
	@Override
	public boolean visit(CloseStatement node) {
		stack.push(factory.createCloseStatement());
		return false;
	}

	@Override
	public boolean visit(DeleteStatement node) {
		stack.push(factory.createDeleteStatement());
		return false;
	}
	
	@Override
	public boolean visit(ExecuteStatement executeStatement) {
		stack.push(factory.createExecuteStatement());
		return false;
	};
	
	@Override
	public boolean visit(ForEachStatement node) {
		stack.push(factory.createForEachStatement());
		return false;
	}

	@Override
	public boolean visit(GetByKeyStatement node) {
		stack.push(factory.createGetByKeyStatement());
		return false;
	}

	@Override
	public boolean visit(GetByPositionStatement node) {
		stack.push(factory.createGetByPositionStatement());
		return false;
	}
	
	@Override
	public boolean visit(NestedFunction node) {
		
		Object obj = node.getName().resolveElement();
		if (obj instanceof Function) {
			Function function = (Function) obj;
			Annotation ann = function.getAnnotation(EGL_lang_reflect_package + ".Operation");
			if (ann != null) {
				Operation op = factory.createOperation();
				op.setOpSymbol((String)ann.getValue("opSymbol"));
				stack.push(op);
				return false;
			}
		}
		stack.push(factory.createFunction());
		return false;
	};

	@Override
	public boolean visit(OpenStatement node) {
		stack.push(factory.createOpenStatement());
		return false;
	}
	
	@Override
	public boolean visit(PrepareStatement prepareStatement) {
		stack.push(factory.createPrepareStatement());
		return false;
	};

	@Override
	public boolean visit(ReplaceStatement node) {
		stack.push(factory.createReplaceStatement());
		return false;
	}
}
