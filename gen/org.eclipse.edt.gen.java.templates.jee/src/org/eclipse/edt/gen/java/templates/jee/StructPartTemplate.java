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
import org.eclipse.edt.mof.egl.StructPart;

public class StructPartTemplate extends org.eclipse.edt.gen.java.templates.StructPartTemplate implements Constants {

	public void genAnnotations(StructPart part, Context ctx, TabbedWriter out, Field field) {
		for (Annotation annot : field.getAnnotations()) {
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
		}
	}
}
