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
package org.eclipse.edt.gen.deployment.javascript.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.gen.deployment.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Type;


public class TypeTemplate extends JavaScriptTemplate {
	public void genDependentPart(EGLClass part, Context ctx, TabbedWriter out, Boolean addComma) {}
	
	public void genDependentParts(Type type, Context ctx, TabbedWriter out, Boolean addComma) {		
		List<Type> processedParts = (List<Type>)ctx.get(genDependentParts);
		if(processedParts == null){
			processedParts = new ArrayList<Type>();
			ctx.put(genDependentParts, processedParts);
		}
		processedParts.add(type);
	}
}
