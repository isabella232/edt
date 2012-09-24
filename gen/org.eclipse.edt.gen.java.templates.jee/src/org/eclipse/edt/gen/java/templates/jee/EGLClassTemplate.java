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
import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Type;

public class EGLClassTemplate extends org.eclipse.edt.gen.java.templates.EGLClassTemplate implements Constants {

	public void preGenClassBody(EGLClass part, Context ctx) {
		super.preGenClassBody(part, ctx);
		// process out extension
		ctx.invoke(preGenAnnotations, part, ctx);
		ctx.invoke(preGenPartAnnotation, part, ctx);
	}

	public void preGenAnnotations(EGLClass part, Context ctx) {
		for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(part, ctx)) {
			ctx.invoke(preGen, annot.getEClass(), ctx, annot, part);
		}
	}

	public void preGenPartAnnotation(EGLClass part, Context ctx) {
		if (org.eclipse.edt.gen.CommonUtilities.getAnnotation(part, Constants.AnnotationXMLRootElement, ctx) == null) {
			// add an xmlRootElement
			try {
				Annotation annotation = CommonUtilities.annotationNewInstance(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationXMLRootElement);
				annotation.setValue("name", part.getCaseSensitiveName());
				org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(part, annotation, ctx);
			}
			catch (Exception e) {}
		}
	}

	public void genClassHeader(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genPartAnnotations, part, ctx, out);
		// process the rest
		super.genClassHeader(part, ctx, out);
	}

	public void genPartAnnotations(EGLClass part, Context ctx, TabbedWriter out) {
		for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(part, ctx)) {
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot);
		}
	}
}
