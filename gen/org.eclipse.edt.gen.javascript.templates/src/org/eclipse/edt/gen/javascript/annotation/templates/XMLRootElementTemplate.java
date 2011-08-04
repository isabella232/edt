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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.EGLClass;

public class XMLRootElementTemplate extends JavaScriptTemplate {

	public void genAnnotation(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, EGLClass part) {
		String namespace = null;
		if (annot.getValue("namespace") != null && ((String) annot.getValue("namespace")).length() > 0) {
			namespace = (String) annot.getValue("namespace");
		}
		String name = part.getName();
		if (annot.getValue("name") != null && ((String) annot.getValue("name")).length() > 0) {
			name = (String) annot.getValue("name");
		}
		Boolean isNillable = Boolean.FALSE;
		if (annot.getValue("nillable") != null) {
			isNillable = CommonUtilities.convertBoolean(annot.getValue("nillable"));
		}
		out.println("annotations[\"XMLRootElement\"] = new egl.eglx.xml.binding.annotation.XMLRootElement(" + (name == null ? "null" : quoted(name)) + ", "
			+ (namespace == null ? "null" : quoted(namespace)) + ", " + isNillable.toString() + ");");
	}
}
