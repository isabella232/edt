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
import org.eclipse.edt.mof.egl.FloatingPointLiteral;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FloatingPointLiteralTemplate extends JavaTemplate {

	public void genExpression(FloatingPointLiteral expr, Context ctx, TabbedWriter out) {
		if (TypeUtils.getTypeKind(expr.getType()) == TypeUtils.TypeKind_SMALLFLOAT)
			out.print(expr.getValue() + "f");
		else
			out.print(expr.getValue() + "d");
	}
}
