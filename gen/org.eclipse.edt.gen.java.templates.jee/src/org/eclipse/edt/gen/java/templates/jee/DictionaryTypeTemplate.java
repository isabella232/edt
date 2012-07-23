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
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;

public class DictionaryTypeTemplate extends org.eclipse.edt.gen.java.templates.eglx.lang.DictionaryTypeTemplate implements Constants { 
	public void preGenAddXMLSchemaType(Type type, Context ctx, Field field){
//		if (org.eclipse.edt.gen.java.CommonUtilities.getAnnotation(field, Constants.AnnotationXmlJavaTypeAdapter, ctx) == null) {
//			Annotation annotation = CommonUtilities.getLocalAnnotation(ctx, Constants.AnnotationXmlJavaTypeAdapter);
//			if(annotation != null){
//				annotation.setValue("value", "org.eclipse.edt.runtime.java.eglx.xml.DictionaryAdapter");
//				org.eclipse.edt.gen.java.CommonUtilities.addGeneratorAnnotation(field, annotation, ctx);
//			}
//		}
	}
}
