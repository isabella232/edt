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

import java.util.Map;

import org.eclipse.edt.compiler.Context;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;


public interface ElementGenerator {
	void setEnvironment(IEnvironment env);
	void setContext(Context context);
	void setCurrentBindingLevelPart(Part part);
	void setCurrentPart(MofSerializable currentPart);
	void setCurrentFunction(FunctionMember currentFunc);
	Element generate(Node node, Map<Object, EObject> bindingToElementMap);
}
