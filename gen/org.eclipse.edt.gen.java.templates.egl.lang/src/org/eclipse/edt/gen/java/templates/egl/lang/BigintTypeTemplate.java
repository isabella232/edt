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
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.TypedElement;

public class BigintTypeTemplate extends JavaTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("(long) 0");
	}
}
