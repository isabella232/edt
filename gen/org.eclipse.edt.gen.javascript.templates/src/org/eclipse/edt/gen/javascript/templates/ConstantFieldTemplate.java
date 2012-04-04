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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Part;

public class ConstantFieldTemplate extends JavaScriptTemplate {

	public void genDeclaration(ConstantField field, Context ctx, TabbedWriter out) {
		ctx.invoke(genQualifier, field, ctx, out);
		ctx.invoke(genName, field, ctx, out);
		out.print(" = ");
		ctx.invoke(genInitialization, field, ctx, out);
		out.println(";");
		
		Container cnr = field.getContainer();
		if (cnr instanceof Part) 
			ctx.invoke(genInitializeStatement, field.getContainer(), ctx, out, field);
	}

	public void genSetter(ConstantField field, Context ctx, TabbedWriter out) {
		// Don't generate a setter for consts.
	}
}
