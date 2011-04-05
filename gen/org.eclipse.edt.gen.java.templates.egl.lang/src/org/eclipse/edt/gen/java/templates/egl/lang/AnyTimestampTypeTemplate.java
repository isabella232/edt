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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class AnyTimestampTypeTemplate extends JavaTemplate {

	// this method gets invoked when there is a specific timestamp needed
	public void genDefaultValue(TimestampType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	// this method gets invoked when there is a generic (unknown) timestamp needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else {
			ctx.gen(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
			out.print(".defaultValue()");
		}
	}
}
