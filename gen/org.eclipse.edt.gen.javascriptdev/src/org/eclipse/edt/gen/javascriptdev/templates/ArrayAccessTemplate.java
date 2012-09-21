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
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.StringLiteralTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;

public class ArrayAccessTemplate extends org.eclipse.edt.gen.javascript.templates.ArrayAccessTemplate {
	
	@Override
	public void genCheckNullArgs(ArrayAccess expr, Context ctx, TabbedWriter out) {
		super.genCheckNullArgs(expr, ctx, out);
		
		// Add egl var name for more informative error message.
		String name = null;
		Expression array = expr.getArray();
		if (array instanceof NamedElement) {
			name = ((NamedElement)array).getCaseSensitiveName();
		}
		else if (array instanceof Name) {
			name = ((Name)array).getId();
		}
		
		if (name != null) {
			out.print(", \"" + StringLiteralTemplate.addStringEscapes(name) + "\"");
		}
	}
	
	public void genSetLocalFunctionVariable(ArrayAccess access, Context ctx, TabbedWriter out) {
		// No need to update - it's still the same object
	}
}
