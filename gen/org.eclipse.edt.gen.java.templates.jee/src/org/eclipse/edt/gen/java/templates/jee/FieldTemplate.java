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
import org.eclipse.edt.gen.java.jee.CommonUtilities;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;

public class FieldTemplate extends org.eclipse.edt.gen.java.templates.FieldTemplate implements Constants {

	public void preGen(Field field, Context ctx) {
		super.preGen(field, ctx);
		// process our extension
		if (field.getContainer() instanceof Type) {
			if (field.getAnnotation(Constants.AnnotationJsonName) == null) {
				// add an xmlElement
				try {
					Annotation annotation = CommonUtilities.getAnnotation(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationJsonName);
					annotation.setValue(field.getId());
					field.addAnnotation(annotation);
				}
				catch (Exception e) {}
			}
		}
	}

	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {
		// process the field
		if (field.getContainer() != null) {
			ctx.invoke(genXmlTransient, field.getContainer(), ctx, out);
			ctx.invoke(genAnnotations, field.getContainer(), ctx, out, field);
		}
		// process the rest
		super.genDeclaration(field, ctx, out);
	}

	public void genAnnotations(Field field, Context ctx, TabbedWriter out) {
		for (Annotation annot : field.getAnnotations()) {
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
		}
	}

	public void genGetter(Field field, Context ctx, TabbedWriter out) {
		ctx.invoke(genAnnotations, field, ctx, out);
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
