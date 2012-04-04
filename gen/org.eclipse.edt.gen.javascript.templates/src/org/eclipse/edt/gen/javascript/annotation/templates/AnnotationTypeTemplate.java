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
package org.eclipse.edt.gen.javascript.annotation.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;

public class AnnotationTypeTemplate extends JavaScriptTemplate {

	public void preGenClassBody(AnnotationType part, Context ctx) {}

	public void genPart(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genClassBody(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(AnnotationType type, Context ctx, TabbedWriter out, TypeNameKind typeKind, Annotation annot) {
		out.print(ctx.getNativeImplementationMapping(type));
	}
	public void genAnnotationKey(AnnotationType type, Context ctx, TabbedWriter out) {
		out.print(ctx.getNativeTypeName(type));
	}
	
	public void genConversionControlAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, EGLClass part) {}
	public void genConversionControlAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Field field) {}
	public void genConversionControlAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Boolean isSuperImpl, Annotation annot, EGLClass part) {
		out.print("this.annotations[\"");//RATLC01814387
		ctx.invoke(genAnnotationKey, (Type)aType, ctx, out);
		out.print("\"] = new ");
		ctx.invoke(genRuntimeTypeName, (Type)aType, ctx, out, TypeNameKind.JavascriptImplementation, annot);
		out.print("(");
		ctx.invoke(genConstructorOptions, (Type)aType, ctx, out, annot, part);
		out.println(");");
	}
	public void genConversionControlAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Boolean isSuperImpl, Annotation annot, Field field) {
		out.print("annotations[\"");//RATLC01814387
		ctx.invoke(genAnnotationKey, (Type)aType, ctx, out);
		out.print("\"] = new ");
		ctx.invoke(genRuntimeTypeName, (Type)aType, ctx, out, TypeNameKind.JavascriptImplementation, annot);
		out.print("(");
		ctx.invoke(genConstructorOptions, (Type)aType, ctx, out, annot, field);
		out.println(");");
	}
	public void genDefaultValue(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) {}
}
