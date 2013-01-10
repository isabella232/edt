/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.gen.java.jee.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;

public class DateTypeTemplate extends org.eclipse.edt.gen.java.templates.eglx.lang.DateTypeTemplate implements Constants {

	public void preGenAddXMLSchemaType(Type type, Context ctx, Field field){
		// if there is not a scema type we may need to add it for type like time, date, timestamp
		try {
			Annotation annotation = org.eclipse.edt.gen.CommonUtilities.annotationNewInstance(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationXMLSchemaType);
			annotation.setValue("name", "date");
			org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(field, annotation, ctx);
		}
		catch (Exception e) {}
		if (org.eclipse.edt.gen.CommonUtilities.getAnnotation(field, Constants.AnnotationXmlJavaTypeAdapter, ctx) == null) {
			Annotation annotation = CommonUtilities.getLocalAnnotation(ctx, Constants.AnnotationXmlJavaTypeAdapter);
			if(annotation != null){
				annotation.setValue("value", "org.eclipse.edt.runtime.java.eglx.xml.DateAdapter");
				org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(field, annotation, ctx);
			}
		}
	}
}
