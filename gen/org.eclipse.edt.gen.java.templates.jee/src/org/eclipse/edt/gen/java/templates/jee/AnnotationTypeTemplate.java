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
package org.eclipse.edt.gen.java.templates.jee;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class AnnotationTypeTemplate extends JavaTemplate implements Constants {

	public void preGen(AnnotationType aType, Context ctx, Annotation annot, EGLClass part) {}

	public void preGenClassBody(AnnotationType part, Context ctx) {}

	public void genPart(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genClassBody(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(AnnotationType type, Context ctx, TabbedWriter out, TypeNameKind typeKind, Annotation annot) {
		out.print(ctx.getNativeImplementationMapping(type));
	}

	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {}

	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot) {}

	public void genJavaAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot) {
		out.print("@");
		ctx.invoke(genRuntimeTypeName, (Type) aType, ctx, out, TypeNameKind.JavaObject, annot);
		out.print("(");
		ctx.invoke(genConstructorOptions, (Type) aType, ctx, out, annot);

		if (ctx.get(Constants.SubKey_keepAnnotationsOnTheSameLine) != null) {
			out.print(") ");
		} else {
			out.println(")");
		}
	}

	public void genJavaAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		out.print("@");
		ctx.invoke(genRuntimeTypeName, (Type) aType, ctx, out, TypeNameKind.JavaObject, annot);
		out.print("(");
		ctx.invoke(genConstructorOptions, (Type) aType, ctx, out, annot, member);
		if (ctx.get(Constants.SubKey_keepAnnotationsOnTheSameLine) != null) {
			out.print(") ");
		} else {
			out.println(")");
		}
	}
}
