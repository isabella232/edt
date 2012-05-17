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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;

public class FieldTemplate extends org.eclipse.edt.gen.java.templates.FieldTemplate implements Constants {

	public void preGen(Field field, Context ctx) {
		super.preGen(field, ctx);
		if(field.getContainer() != null){
			ctx.invoke(preGenAnnotations, field.getContainer(), ctx, field);
		}
	}

	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {
		// process the field
		if (field.getContainer() != null) {
			//xml annotations need to appear on the getter or if there is no getter the field
			ctx.invoke(genFieldAnnotations, field.getContainer(), ctx, out, field);
		}
		// process the rest
		super.genDeclaration(field, ctx, out);
	}

	public void genGetter(Field field, Context ctx, TabbedWriter out) {
		if (field.getContainer() != null) {
			ctx.invoke(genGetterAnnotations, field.getContainer(), ctx, out, field);
		}
		// process the rest
		super.genGetter(field, ctx, out);
	}

	public void genResourceAnnotation(Field field, Context ctx, TabbedWriter out) {
		if (field.getAnnotation(Constants.AnnotationResource) != null) {
			Annotation annot = field.getAnnotation(Constants.AnnotationResource);
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
		}
	}
}
