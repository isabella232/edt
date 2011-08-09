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

public class XMLElementTemplate extends JavaScriptTemplate {

	public void genAnnotationKey(AnnotationType type, Context ctx, TabbedWriter out) {
		out.print("XMLStyle");
	}
	public void genConstructorOptions(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		if (annot.getValue("name") instanceof String && ((String) annot.getValue("name")).length() > 0)
		{
			out.print(quoted((String) annot.getValue("name")));
		}
		else{
			out.print(quoted(field.getId()));
		}
		out.print(", ");
		if (annot.getValue("namespace") instanceof String && ((String) annot.getValue("namespace")).length() > 0)
		{
			out.print(quoted((String) annot.getValue("namespace")));
		}
		else{
			out.print("null");
		}
	}

}
