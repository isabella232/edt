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
package org.eclipse.edt.gen.java.templates.egl.lang;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.TypedElement;

public class SmallfloatTypeTemplate extends JavaTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("0");
	}

	public void genConversionOperation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (CommonUtilities.isHandledByJavaWithoutCast(((AsExpression) args[0]).getObjectExpr(), ((AsExpression) args[0]), ctx)) {
			ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		} else if (CommonUtilities.isHandledByJavaWithCast(((AsExpression) args[0]).getObjectExpr(), ((AsExpression) args[0]), ctx)) {
			out.print("(" + ctx.getPrimitiveMapping(((AsExpression) args[0]).getType()) + ")");
			out.print("(");
			ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
			out.print(")");
		} else
			ctx.genSuper(genConversionOperation, EGLClass.class, type, ctx, out, args);
	}
}
