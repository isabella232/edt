/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.jee;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;

public class FunctionParameterTemplate extends org.eclipse.edt.gen.java.templates.FunctionParameterTemplate implements Constants {

	public void genDeclaration(FunctionParameter decl, Context ctx, TabbedWriter out) {
		ctx.put(Constants.SubKey_keepAnnotationsOnTheSameLine, Boolean.TRUE);
		for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(decl, ctx)) {
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, decl);
		}
		ctx.remove(Constants.SubKey_keepAnnotationsOnTheSameLine);
		// process the rest
		super.genDeclaration(decl, ctx, out);
	}

	public void genFunctionParameterSignature(FunctionParameter parameter, Context ctx, TabbedWriter out) {
		out.print("@FunctionParameter(jsonInfo=@Json(name=\"");
		ctx.invoke(genName, parameter, ctx, out);
		out.print("\", clazz=");
		Integer arrayDimension = (Integer) ctx.invoke(genFieldTypeClassName, parameter.getType(), ctx, out, new Integer(0));
		out.print(".class, asOptions={");
		ctx.invoke(genJsonTypeDependentOptions, parameter.getType(), ctx, out);
		out.print("}), kind=FunctionParameterKind.");
		out.print(convert(parameter.getParameterKind()));
		if (arrayDimension > 0) {
			out.print(", arrayDimensions=");
			out.print(arrayDimension.toString());
		}
		out.print(")");
	}

	private String convert(ParameterKind parameterKind) {
		if (ParameterKind.PARM_IN.equals(parameterKind)) {
			return "IN";
		} else if (ParameterKind.PARM_OUT.equals(parameterKind)) {
			return "OUT";
		} else if (ParameterKind.PARM_INOUT.equals(parameterKind)) {
			return "INOUT";
		} else {
			return "RETURN";
		}
	}
}
