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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Type;

public class XMLRootElementTemplate extends org.eclipse.edt.gen.java.templates.JavaTemplate implements Constants {

	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot) {
		ctx.invoke(genJavaAnnotation, (Type) aType, ctx, out, annot);
	}

	public void genConstructorOptions(AnnotationType annotType, Context ctx, TabbedWriter out, Annotation annot) {
		boolean addComma = false;
		if (annot.getValue("name") instanceof String && !"##default".equals(annot.getValue("name"))) {
			out.print("name=\"" + (String) annot.getValue("name") + "\"");
			addComma = true;
		}
		if (annot.getValue("namespace") instanceof String && !"##default".equals(annot.getValue("namespace"))) {
			if (addComma) {
				out.print(", ");
			}
			out.print("namespace=\"" + (String) annot.getValue("namespace") + "\"");
		}
	}
}
