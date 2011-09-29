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
package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Type;

public class NumberTypeTemplate extends JavaTemplate {

	public void genConstructorOptions(EGLClass type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber"))
			out.print("0");
		else
			ctx.invokeSuper(this, genConstructorOptions, type, ctx, out);
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for number type, always use the runtime
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
		} else
			ctx.invokeSuper(this, genBinaryExpression, type, ctx, out, arg);
	}

	public Boolean isAssignmentBreakupWanted(Type type, Context ctx, String arg, Type rhsType) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber"))
			return true;
		else
			return (Boolean) ctx.invokeSuper(this, org.eclipse.edt.gen.Constants.isAssignmentBreakupWanted, type, ctx, arg, rhsType);
	}
}
