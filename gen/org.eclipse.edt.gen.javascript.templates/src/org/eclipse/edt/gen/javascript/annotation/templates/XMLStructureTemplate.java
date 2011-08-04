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

public class XMLStructureTemplate extends JavaScriptTemplate {

	public void genAnnotation(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, EGLClass part) {
		String value;
		/*
		 * choice = 1, sequence = 2, simpleContent = 3, unordered = 4
		 */
		switch ((Integer) annot.getValue("value")) {
			case 1:
				value = "choice";
				break;
			case 2:
				value = "sequence";
				break;
			case 3:
				value = "simpleContent";
				break;
			default:
				value = "unordered";
			out.println("annotations[\"XMLStructure\"] = egl.eglx.xml.binding.annotation.XMLStructure(" + quoted(value) + ");");
		}
	}
}
