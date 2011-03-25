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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Part;

public class ExternalTypeTemplate extends PartTemplate {

	public void validateClassBody(Part part, Context ctx, Object... args) {}

	public void genPart(Part part, Context ctx, TabbedWriter out, Object... args) {}

	public void genClassBody(Part part, Context ctx, TabbedWriter out, Object... args) {}

	public void genClassHeader(Part part, Context ctx, TabbedWriter out, Object... args) {}

	public void genAccessor(Part part, Context ctx, TabbedWriter out, Object... args) {
		genPartName(part, ctx, out, args);
	}

	public void genRuntimeTypeName(Part part, Context ctx, TabbedWriter out, Object... args) {
		genPartName(part, ctx, out, args);
	}
}
