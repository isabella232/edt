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
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;

public class AnyTimestampTypeTemplate extends JavaTemplate {

	// this method gets invoked when there is a specific timestamp needed
	public void genDefaultValue(TimestampType type, Context ctx, TabbedWriter out) {
		processDefaultValue(type, ctx, out);
	}

	// this method gets invoked when there is a generic (unknown) timestamp needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out) {
		processDefaultValue(type, ctx, out);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".defaultValue()");
	}
}
