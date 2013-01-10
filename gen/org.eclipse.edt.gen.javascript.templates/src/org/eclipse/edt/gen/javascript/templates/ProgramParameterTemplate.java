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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ProgramParameter;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ProgramParameterTemplate extends JavaScriptTemplate {

	public void genDeclaration(ProgramParameter field, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(ProgramParameter mbr, Context ctx, TabbedWriter out, TypeNameKind arg) {
		if (mbr.getType() == null)
			return;
		else if (ctx.mapsToPrimitiveType(mbr.getType().getClassifier()) && !mbr.isNullable() && TypeUtils.isValueType(mbr.getType()))
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavascriptPrimitive);
		else if (ctx.mapsToPrimitiveType(mbr.getType().getClassifier()))
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavascriptObject);
		else if (ctx.mapsToNativeType(mbr.getType().getClassifier()))
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.EGLInterface);
		else
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavascriptObject);
	}
}
