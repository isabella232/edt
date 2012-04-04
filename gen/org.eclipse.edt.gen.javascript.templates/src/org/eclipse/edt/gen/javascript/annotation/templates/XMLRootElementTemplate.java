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
package org.eclipse.edt.gen.javascript.annotation.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Type;

public class XMLRootElementTemplate extends JavaScriptTemplate {

	public void genConversionControlAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, EGLClass part) {
		ctx.invokeSuper(this, genConversionControlAnnotation, (Type)aType, ctx, out, Boolean.TRUE, annot, part);
	}
	public void genConstructorOptions(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, EGLClass part) {
		if(annot.getValue("name") instanceof String && !"##default".equals(annot.getValue("name"))){
			out.print(quoted((String) annot.getValue("name")));
		}
		else{
			out.print(quoted(part.getName()));
		}
		out.print( ", ");
		if(annot.getValue("namespace") instanceof String && !"##default".equals(annot.getValue("namespace"))){
			out.print(quoted((String) annot.getValue("namespace")));
		}
		else{
			out.print("null");
		}
		out.print( ", ");
		if (annot.getValue("nillable") != null) {
			out.print(((Boolean)annot.getValue("nillable")).toString());
		}
		else{
			out.print("false");
		}
	}

}
