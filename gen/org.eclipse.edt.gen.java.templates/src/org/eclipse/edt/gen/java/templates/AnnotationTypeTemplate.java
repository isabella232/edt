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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;

public class AnnotationTypeTemplate extends JavaTemplate {

	public void preGenClassBody(AnnotationType part, Context ctx) {}

	public void genPart(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genClassBody(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(AnnotationType part, Context ctx, TabbedWriter out) {}

	public void genXmlAnnotation(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot) {
		if("eglx.xml._bind.annotation.XmlAttribute".equalsIgnoreCase(type.getFullyQualifiedName())){
			String name = (String)annot.getValue("name");
			String namespace = (String)annot.getValue("namespace");
			Boolean required = (Boolean)annot.getValue("required");
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
		else if("eglx.xml._bind.annotation.XmlElement".equalsIgnoreCase(type.getFullyQualifiedName())){
			String name = (String)annot.getValue("name");
			String namespace = (String)annot.getValue("namespace");
			Boolean required = (Boolean)annot.getValue("required");
			Boolean nillable = (Boolean)annot.getValue("nillable");
			out.print("@javax.xml.bind.annotation.XmlElement(");
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
				addComma = true;
			}
			if(nillable != null){
				if(addComma){
					out.print(", ");
				}
				out.print("nillable=" + nillable.toString());
			}
			out.println(")");
		}
		else if("eglx.xml._bind.annotation.XMLRootElement".equalsIgnoreCase(type.getFullyQualifiedName())){
			String name = (String)annot.getValue("name");
			String namespace = (String)annot.getValue("namespace");
			out.print("@javax.xml.bind.annotation.XmlRootElement(");
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
			}
			out.println(")");
		}
	}

}
