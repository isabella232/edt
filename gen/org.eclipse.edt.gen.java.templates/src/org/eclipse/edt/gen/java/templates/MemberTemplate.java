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

import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class MemberTemplate extends JavaTemplate {

	public void genDeclaration(Member field, Context ctx, TabbedWriter out, Object... args) {
		AccessKind access = field.getAccessKind();
		if (access == AccessKind.ACC_PRIVATE)
			out.print("private ");
		else
			out.print("public ");
	}

	public void genRuntimeTypeName(Member mbr, Context ctx, TabbedWriter out, Object... args) {
		if (mbr.getType() == null)
			out.print("void");
		else if (ctx.getAttribute(mbr, Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ((Integer) ctx.getAttribute(mbr, Constants.Annotation_functionArgumentTemporaryVariable)).intValue() != 0) {
			out.print("AnyBoxedObject<");
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavaObject, mbr);
			out.print(">");
		} else if (ctx.mapsToPrimitiveType(mbr.getType().getClassifier()) && !mbr.isNullable() && TypeUtils.isValueType(mbr.getType()))
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavaPrimitive, mbr);
		else if (ctx.mapsToPrimitiveType(mbr.getType().getClassifier()))
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavaObject, mbr);
		else if (ctx.mapsToNativeType(mbr.getType().getClassifier()))
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.EGLInterface, mbr);
		else
			ctx.gen(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavaObject, mbr);
	}
}
