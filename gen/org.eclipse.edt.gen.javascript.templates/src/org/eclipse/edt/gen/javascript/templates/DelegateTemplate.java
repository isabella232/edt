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
import org.eclipse.edt.mof.egl.Delegate;

public class DelegateTemplate extends JavaScriptTemplate {

	public void preGenClassBody(Delegate part, Context ctx) {}

	public void genPart(Delegate part, Context ctx, TabbedWriter out) {}

	public void genClassBody(Delegate part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(Delegate part, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(Delegate part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		out.print("\"\"");
	}
	
	public void genDefaultValue(Delegate part, Context ctx, TabbedWriter out) {
		out.print("null");
	}
	
	public Boolean supportsConversion(Delegate part, Context ctx) {
		return Boolean.FALSE;
	}

}
