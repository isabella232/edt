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
package org.eclipse.edt.gen.java.templates.jee;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;

public class FunctionTemplate extends org.eclipse.edt.gen.java.templates.FunctionTemplate implements Constants {

	public void genFunctionSignatures(Function function, Context ctx, TabbedWriter out) {
		out.print("@FunctionSignature(name=\"");
		ctx.invoke(genName, function, ctx, out);
		out.print("\", parameters={");
		ctx.foreach(function.getParameters(), ',', genFunctionParameterSignature, ctx, out);
		if (function.getReturnType() != null) {
			if (function.getParameters() != null && function.getParameters().size() > 0) {
				out.print(", ");
			}
			FunctionParameter ret = ctx.getFactory().createFunctionParameter();
			ret.setType(function.getReturnType());
			ret.setName("return");
			ctx.invoke(genFunctionParameterSignature, ret, ctx, out);
		}
		out.println("})");
	}
}
