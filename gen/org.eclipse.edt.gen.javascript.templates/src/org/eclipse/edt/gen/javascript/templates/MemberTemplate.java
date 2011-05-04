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
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class MemberTemplate extends JavaScriptTemplate {

	public void genDeclaration(Member field, Context ctx, TabbedWriter out, Object... args) {}

	public void genRuntimeTypeName(Member mbr, Context ctx, TabbedWriter out, Object... args) {
		if (mbr.getType() == null)
			return;
		else if (ctx.getAttribute(mbr, org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ((Integer) ctx.getAttribute(mbr, org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable)).intValue() != 0) {
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavascriptObject, mbr);
		} else if (ctx.mapsToPrimitiveType(mbr.getType().getClassifier()) && !mbr.isNullable() && TypeUtils.isValueType(mbr.getType()))
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavascriptPrimitive);
		else if (ctx.mapsToPrimitiveType(mbr.getType().getClassifier()))
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavascriptObject);
		else if (ctx.mapsToNativeType(mbr.getType().getClassifier()))
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.EGLInterface);
		else
			ctx.gen(genRuntimeTypeName, (EObject) mbr.getType(), ctx, out, TypeNameKind.JavascriptObject);
	}
}
