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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class MemberTemplate extends JavaTemplate {

	public void genDeclaration(Member field, Context ctx, TabbedWriter out) {
		AccessKind access = field.getAccessKind();
		if (field.getCaseSensitiveName().equalsIgnoreCase("main") && field.getContainer() instanceof Program)
			out.print("public ");
		else if (access == AccessKind.ACC_PRIVATE)
			out.print("private ");
		else
			out.print("public ");
	}

	public void genRuntimeTypeName(Member mbr, Context ctx, TabbedWriter out, TypeNameKind arg) {
		if (mbr.getType() == null)
			out.print("void");
		else if (CommonUtilities.isBoxedOutputTemp(mbr, ctx)) {
			out.print("AnyBoxedObject<");
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavaObject);
			ctx.invoke(genRuntimeTypeExtension, mbr.getType(), ctx, out);
			out.print(">");
		} else if (ctx.mapsToPrimitiveType(mbr.getType().getClassifier()) && !mbr.isNullable() && TypeUtils.isValueType(mbr.getType())) {
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavaPrimitive);
			ctx.invoke(genRuntimeTypeExtension, mbr.getType(), ctx, out);
		} else if (ctx.mapsToPrimitiveType(mbr.getType().getClassifier())) {
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavaObject);
			ctx.invoke(genRuntimeTypeExtension, mbr.getType(), ctx, out);
		} else if (ctx.mapsToNativeType(mbr.getType().getClassifier())) {
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.EGLInterface);
			ctx.invoke(genRuntimeTypeExtension, mbr.getType(), ctx, out);
		} else {
			ctx.invoke(genRuntimeTypeName, mbr.getType(), ctx, out, TypeNameKind.JavaObject);
			ctx.invoke(genRuntimeTypeExtension, mbr.getType(), ctx, out);
		}
	}
}
