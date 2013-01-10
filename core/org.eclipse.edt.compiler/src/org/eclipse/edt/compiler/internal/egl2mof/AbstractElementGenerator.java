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

import java.util.Map;

import org.eclipse.edt.compiler.Context;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;


public abstract class AbstractElementGenerator extends Egl2Mof implements ElementGenerator {
	
	public IrFactory irFactory = IrFactory.INSTANCE;

	public AbstractElementGenerator(IEnvironment env) {
		super(env);
	}

	@Override
	public Element generate(Node node, Map<Object, EObject> bindingToElementMap) {
		eObjects = bindingToElementMap;
		node.accept(this);
		return (Element)stack.pop();
	}
	
	@Override
	public void setCurrentPart(MofSerializable currentPart) {
		this.currentPart = currentPart;
	}

	@Override
	public void setCurrentBindingLevelPart(Part part) {
		this.currentBindingLevelPart = part;
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
