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
package org.eclipse.edt.mof.codegen.java;

import java.util.List;

import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EParameter;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.codegen.api.TemplateException;



public class EFunctionTemplate extends MofImplTemplate {

	public void genImpl(EFunction function, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		out.println();
		out.println("@Override");
		genFunctionHeader(function, ctx, out);
		out.println(" {");
		out.pushIndent();
		out.println("// TODO: Default generated implementation");
		if (function.getEType() != null) {
			EClassifier eClass = function.getEType().getEClassifier();
			out.print("return ");
			if (eClass instanceof EDataType && !eClass.getName().equals("EList")) {
				Object value = ((EDataType)eClass).getDefaultValue();
				if (value == null)
					out.print("null");
				else
					out.print(((EDataType)eClass).getDefaultValue().toString());
			}
			else {
				out.print("null");
			}
			out.println(';');
		}
		out.popIndent();
		out.println('}');
	}
	
	public void genInterface(EFunction function, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		out.println();
		genFunctionHeader(function, ctx, out);
		out.println(';');
	}
	
	private void genFunctionHeader(EFunction function, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		out.print("public ");
		out.print(getETypeName(function));
		out.print(' ');
		out.print(function.getName());
		out.print('(');
		List<EParameter> parms = function.getEParameters();
		for (int i = 0; i < parms.size(); i++) {
			EParameter parm = parms.get(i);
			out.print(getETypeName(parm.getEType()));
			out.print(' ');
			out.print(parm.getName());
			if (i < parms.size()-1) {
				out.print(',');
			}
		}
		out.print(')');
	}
}
