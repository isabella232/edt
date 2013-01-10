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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.FunctionPart;

public class FunctionPartTemplate extends JavaTemplate {

	public void preGenClassBody(FunctionPart part, Context ctx) {}

	public void genPart(FunctionPart part, Context ctx, TabbedWriter out) {}

	public void genClassBody(FunctionPart part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(FunctionPart part, Context ctx, TabbedWriter out) {}
}
