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
package org.eclipse.edt.gen.java.annotation.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;

public class AnnotationTypeTemplate extends JavaTemplate {

	public void preGenClassBody(AnnotationType part, Context ctx) {}

	public void genPart(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genClassBody(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(AnnotationType type, Context ctx, TabbedWriter out, TypeNameKind typeKind, Annotation annot) {
		out.print(ctx.getNativeImplementationMapping(type));
	}
	
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Field field) {}
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot) {}
	public void genJavaAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot) {
		out.print("@");
		ctx.invoke(genRuntimeTypeName, (Type)aType, ctx, out, TypeNameKind.JavaObject, annot);
		out.print("(");
		ctx.invoke(genConstructorOptions, (Type)aType, ctx, out, annot);
		out.println(")");
	}
	public void genJavaAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		out.print("@");
		ctx.invoke(genRuntimeTypeName, (Type)aType, ctx, out, TypeNameKind.JavaObject, annot);
		out.print("(");
		ctx.invoke(genConstructorOptions, (Type)aType, ctx, out, annot, field);
		out.println(")");
	}
}
