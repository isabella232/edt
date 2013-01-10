/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;

public class TypeTemplate extends org.eclipse.edt.gen.javascript.templates.TypeTemplate {
	
	public void genDebugTypeInfo(Type type, Context ctx, TabbedWriter out) {
		out.print(type.getTypeSignature());
	}
	
	public void genInitializeStatement(Type type, Context ctx, TabbedWriter out, Field arg) {
		super.genInitializeStatement(type, ctx, out, arg);
		ctx.invoke(Constants.genSetWidgetLocation, arg, Boolean.FALSE, ctx, out);
	}
}
