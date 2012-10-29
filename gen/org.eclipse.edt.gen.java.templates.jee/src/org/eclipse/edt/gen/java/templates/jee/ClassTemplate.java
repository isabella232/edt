/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;
import org.eclipse.edt.mof.egl.Class;

public class ClassTemplate extends org.eclipse.edt.gen.java.templates.ClassTemplate implements Constants
{
	public void preGenAnnotations(Class type, Context ctx, Field field) {
		// process our extension
		if (org.eclipse.edt.gen.CommonUtilities.getAnnotation(field, Constants.AnnotationJsonName, ctx) == null) {
			// add an xmlElement
			try {
				Annotation annotation = CommonUtilities.annotationNewInstance(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationJsonName);
				annotation.setValue(field.getCaseSensitiveName());
				org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(field, annotation, ctx);
			}
			catch (Exception e) {}
		}
		if (org.eclipse.edt.gen.CommonUtilities.getAnnotation(field, Constants.AnnotationXMLSchemaType, ctx) == null) {
			// if there is not a scema type we may need to add it for type like time, date, timestamp
			ctx.invoke(preGenAddXMLSchemaType, field.getType(), ctx, field);
		}
		if (!field.getCaseSensitiveName().equals(JavaAliaser.getAlias(field.getCaseSensitiveName())) &&
				CommonUtilities.getAnnotation(field, Constants.AnnotationXmlElement, ctx) == null &&
						CommonUtilities.getAnnotation(field, Constants.AnnotationXmlAttribute, ctx) == null) {
			//if the generated name is not the same as the EGL name
			//if there is no XMLElement or Attribute add an XMLElement with the EGL name
			//if no name is specified on an XMLElement or XMLAttribute set the name to the EGL name, other wise use the supplied name
			try {
				Annotation annotation = CommonUtilities.annotationNewInstance(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationXmlElement);
				annotation.setValue("name", field.getCaseSensitiveName());
				org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(field, annotation, ctx);
			} catch (Exception e) {}
		}
	}
	public void genFieldAnnotations(Class type, Context ctx, TabbedWriter out, Field field) {
		for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(field, ctx)) {
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
		}
	}
}
