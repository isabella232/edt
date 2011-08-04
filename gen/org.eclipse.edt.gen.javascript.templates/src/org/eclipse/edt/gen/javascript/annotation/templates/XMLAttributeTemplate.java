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
package org.eclipse.edt.gen.javascript.annotation.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Field;

public class XMLAttributeTemplate extends JavaScriptTemplate {

	public void genAnnotation(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		String xmlNamespace = AnnotationUtilities.getNamespace(annot);
		String xmlName = AnnotationUtilities.getName(annot);
		// add attribute or element
		out.print("annotations[\"XMLStyle\"] = ");
		if (xmlName == null || xmlName.length() == 0) {
			xmlName = field.getId();
		}
		out.print("new egl.eglx.xml.binding.annotation.XMLAttribute(");
		out.print("\"" + xmlName + "\", ");
		out.print(xmlNamespace == null ? "null" : "\"" + xmlNamespace + "\"");
		out.println(");");
	}

}
