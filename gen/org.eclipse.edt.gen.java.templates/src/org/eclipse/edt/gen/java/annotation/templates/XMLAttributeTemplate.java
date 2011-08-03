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
package org.eclipse.edt.gen.java.annotation.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Field;

public class XMLAttributeTemplate extends JavaTemplate {

	public void genAnnotation(AnnotationType annotType, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		String name = (String)annot.getValue("name");
		String namespace = (String)annot.getValue("namespace");
		Boolean required = (Boolean)(annot.getValue("required"));
		out.print("@javax.xml.bind.annotation.XmlAttribute(");
		boolean addComma = false;
		if(name != null && !"##default".equals(name)){
			out.print("name=\"" + name + "\"");
			addComma = true;
		}
		if(namespace != null && !"##default".equals(namespace)){
			if(addComma){
				out.print(", ");
			}
			out.print("namespace=\"" + namespace + "\"");
			addComma = true;
		}
		if(required != null){
			if(addComma){
				out.print(", ");
			}
			out.print("required=" + required.toString());
		}
		out.println(")");
	}
}
