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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.SubstringAccess;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class SequenceTypeTemplate extends JavaTemplate {

	public void genTypeDependentOptions(SequenceType type, Context ctx, TabbedWriter out) {
		out.print(", ");
		out.print(type.getLength());
	}

	public void genSubstringAccess(SequenceType type, Context ctx, TabbedWriter out, SubstringAccess arg) {
		out.print(ctx.getNativeImplementationMapping(arg.getType()) + ".substring(");
		ctx.invoke(genExpression, arg.getStringExpression(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(arg.getStart(), TypeUtils.Type_INT), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(arg.getEnd(), TypeUtils.Type_INT), ctx, out);
		out.print(")");
	}
}
