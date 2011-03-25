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
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.MemberName;

public class MemberNameTemplate extends NameTemplate {

	public void genExpression(MemberName expr, Context ctx, TabbedWriter out, Object... args) {
		out.print("this.");
		Annotation prop = getProperty(expr);
		if (prop == null) {
			genName(expr.getMember(), ctx, out, args);
		}
		else {
			out.print((String)prop.getValue(getMethod));
			out.print("()");
		}
		if (isWrapped(expr.getMember(), ctx)) {
			out.print('.');
			out.print(eze$$value);
		}
	}
	
}
