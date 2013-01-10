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
package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Type;

public class DecimalTypeTemplate extends JavaTemplate {

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaImplementation);
		out.print(".ZERO");
	}

	public Boolean isAssignmentBreakupWanted(Type type, Context ctx, Assignment expr) {
		// the arg contains the operation being asked about
		return true;
	}

	public Boolean isMathLibDecimalBoxingWanted(Type type, Context ctx) {
		// types can override this to cause/prevent mathlib decimals/precision boxing to be done
		return true;
	}
}
