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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Type;

public class AnyTypeTemplate extends TypeTemplate {

	// TODO: Currently the JS runtime for convertAnyToType is very specific
	// about the current type signatures in RBD and is closed to that set
	// of types.  Question: should not that function simply check if the
	// current Any expression ISA the target type and if not throw type cast
	// exception otherwise just return the eze$$value of the Any value?
	public void genConversion(Type type, Context ctx, TabbedWriter out, Object...args) {
		AsExpression asExpr = (AsExpression)args[0];
		out.print("egl.convertAnyToType(");
		ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out);
		out.print(", ");
		out.print(quoted(asExpr.getEType().getTypeSignature()));
		out.print(')');
	}

}
