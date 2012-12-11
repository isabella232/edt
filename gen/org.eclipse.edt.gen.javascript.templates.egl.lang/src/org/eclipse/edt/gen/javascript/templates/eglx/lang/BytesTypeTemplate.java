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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.SequenceType;

public class BytesTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(SequenceType type, Context ctx, TabbedWriter out) {
		out.print("egl.eglx.lang.EBytes.ezeNew(");
		out.print(type.getLength());
		out.print(')');
	}

	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out) {
		out.print("egl.eglx.lang.EBytes.ezeNew([])");
	}

	public void genSignature(SequenceType type, Context ctx, TabbedWriter out) {
		StringBuilder signature = new StringBuilder("g");
		if(type.getLength() != null && type.getLength() > 0){
			signature.append(type.getLength());
		}
		signature.append(';');
		out.print(signature.toString());
	}

	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out) {
		out.print("G;");
	}

}
