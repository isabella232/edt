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


import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class XMLValueTemplate extends JavaTemplate implements Constants {

	public void preGen(AnnotationType aType, Context ctx, Annotation annot, EGLClass part) {
		// add an xmlValue to the field
		Object kind = annot.getValue("kind");
		if (kind != null) {
			if ("simpleContent".equalsIgnoreCase(CommonUtilities.getEnumerationName(kind))) {
				for (Field field : part.getFields()) {
					if (org.eclipse.edt.gen.CommonUtilities.getAnnotation(field, Constants.AnnotationXmlAttribute, ctx) == null) {
						org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(field, annot, ctx);
						break;
					}
				}
			}
		}
	}

	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot) {}

	public void genAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Member member) {
		out.print("@");
		ctx.invoke(genRuntimeTypeName, (Type) aType, ctx, out, TypeNameKind.JavaObject, annot);
		out.println();
	}

}
