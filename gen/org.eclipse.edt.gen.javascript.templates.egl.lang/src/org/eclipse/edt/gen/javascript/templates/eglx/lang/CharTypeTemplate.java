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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.SequenceType;

public class CharTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a char with a length needed
	public void genSignature(SequenceType type, Context ctx, TabbedWriter out) {
		String signature = "C" + type.getLength() + ";";
		out.print(signature);
	}

	// this method gets invoked when there is a char with no length needed
	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out) {
		String signature = "C;";
		out.print(signature);
	}
}
