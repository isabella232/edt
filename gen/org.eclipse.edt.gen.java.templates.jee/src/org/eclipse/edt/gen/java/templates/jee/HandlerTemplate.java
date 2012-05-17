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
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Type;

public class HandlerTemplate extends org.eclipse.edt.gen.java.templates.HandlerTemplate  implements Constants{
	public void preGenAnnotations(Handler type, Context ctx, Field field) {
		// process our extension
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
	public void genFieldAnnotations(Handler type, Context ctx, TabbedWriter out, Field field) {
		for (Annotation annot : field.getAnnotations()) {
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
		}
	}

}
