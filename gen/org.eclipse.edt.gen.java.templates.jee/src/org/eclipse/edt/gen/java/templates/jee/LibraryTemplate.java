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
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Library;

public class LibraryTemplate extends org.eclipse.edt.gen.java.templates.LibraryTemplate implements Constants {

	public void genAnnotations(Library library, Context ctx, TabbedWriter out, Field field) {
		for (Annotation annot : field.getAnnotations()) {
			if (annot.getEClass().getETypeSignature().equals("eglx.lang.Resource")) {
				ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
			}
		}
	}

	public void genXmlTransient(Library part, Context ctx, TabbedWriter out) {
		out.println("@javax.xml.bind.annotation.XmlTransient");
	}
}
