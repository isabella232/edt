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
import org.eclipse.edt.mof.egl.Field;

public class XMLSchemaTypeTemplate extends JavaScriptTemplate {

	public void genConversionControlAnnotation(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		// add xmlschema type
		if (annot.getValue("name") instanceof String && ((String) annot.getValue("name")).length() > 0)
		{
			out.print("annotations[\"");
			ctx.invoke(genAnnotationKey, annot.getEClass(), ctx, out);
			out.print("\"] = new ");
			ctx.invoke(genRuntimeTypeName, annot.getEClass(), ctx, out, TypeNameKind.JavascriptImplementation, annot);
			out.print("(");
			ctx.invoke(genConstructorOptions, annot.getEClass(), ctx, out, annot, field);
			out.println(");");
		}
	}

	public void genConstructorOptions(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		out.print(quoted((String) annot.getValue("name")));
		if(annot.getValue("namespace") instanceof String && !"http://www.w3.org/2001/XMLSchema".equals(annot.getValue("namespace"))){
			out.print(", ");
			out.print(quoted((String)annot.getValue("namespace")));
		}
	}
}
