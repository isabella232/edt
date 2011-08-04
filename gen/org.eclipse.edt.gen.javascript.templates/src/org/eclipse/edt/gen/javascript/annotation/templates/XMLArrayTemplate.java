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

public class XMLArrayTemplate extends JavaScriptTemplate {

	public void genAnnotation(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		out.print("annotations[\"XMLArray\"] = new egl.eglx.xml.binding.annotation.XMLArray(");
		out.print(annot.getValue("wrapped") == null ? "true, " : (((Boolean) annot.getValue("wrapped")).toString() + ", "));
		String[] elementNames = (String[]) annot.getValue("names");
		if (elementNames != null && elementNames.length > 0) {
			out.print("[");
			boolean addComma = false;
			for (String elementName : elementNames) {
				if (addComma) {
					out.print(", ");
				}
				out.print(quoted(elementName));
			}
			out.print("]");
		} else {
			out.print("null");
		}
		out.println(");");
	}
}
