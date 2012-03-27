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
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class NullTypeTemplate extends JavaTemplate {

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// if the lhs argument is not the nulltype, and if it is not a value type, pass to super. same goes for rhs
		if ((!(arg.getLHS() instanceof NullLiteral) && !TypeUtils.isReferenceType(arg.getLHS().getType()) && !arg.getLHS().isNullable())
			|| (!(arg.getRHS() instanceof NullLiteral) && !TypeUtils.isReferenceType(arg.getRHS().getType()) && !arg.getRHS().isNullable()))
			ctx.invokeSuper(this, genBinaryExpression, type, ctx, out, arg);
		else {
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(CommonUtilities.getNativeNullTypeJavaOperation(arg, ctx));
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
		}
	}
}
