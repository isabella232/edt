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

public class JsonNameTemplate extends JavaScriptTemplate {

	public void genAnnotation(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		// create the AnnotationMap
		// add attribute or element
		String jsonName = null;
		if (annot != null ){
			jsonName = (String)annot.getValue();
		}
		
		out.print("annotations[\"JsonName\"] = ");
		if (jsonName == null || jsonName.length() == 0) {
			jsonName = field.getId();
		}
		out.println("new egl.eglx.json.JsonName(\"" + jsonName + "\");");
	}
}
