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
package org.eclipse.edt.gen.java.templates.jee;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Member;

public class XmlJavaTypeAdapterTemplate extends JavaTemplate implements Constants {

	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot) {
		genAnnotation(annot, out);
	}
	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		genAnnotation(annot, out);
	}
	private void genAnnotation(Annotation annot, TabbedWriter out) {
		out.print("@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter");
		out.print("(");
		out.print("value=" + (String) annot.getValue("value") + ".class");
		if(annot.getValue("type") != null && ((String)annot.getValue("type")).length() > 0){
			out.print(", type=" + (String) annot.getValue("type") + ".class" );
		}
		out.println(")");
	}
}
