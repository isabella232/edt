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
import org.eclipse.edt.mof.egl.EGLClass;

public class XMLRootElementTemplate extends JavaScriptTemplate {

	public void genConstructorOptions(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, EGLClass part) {
		if (annot.getValue("name") != null && ((String) annot.getValue("name")).length() > 0) {
			out.println(quoted((String) annot.getValue("name")));
		}
		else{
			out.println(quoted(part.getName()));
			
		}
		out.println( ", ");
		if (annot.getValue("namespace") != null && ((String) annot.getValue("namespace")).length() > 0) {
			out.println(quoted((String) annot.getValue("namespace")));
		}
		else{
			out.println("null");
		}
		out.println( ", ");
		if (annot.getValue("nillable") != null) {
			out.println(((Boolean)annot.getValue("nillable")).toString());
		}
		else{
			out.println("false");
		}
	}

}
