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
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Type;

public class XMLValueTemplate extends JavaScriptTemplate {

	public void genConversionControlAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, EGLClass part) {
		ctx.invokeSuper(this, genConversionControlAnnotation, (Type)aType, ctx, out, Boolean.TRUE, annot, part);
	}
	public void genConstructorOptions(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, EGLClass part) {
		Object kind = annot.getValue("kind");
		if(kind instanceof EnumerationEntry){
			ctx.invoke(genRuntimeTypeName, kind, ctx, out);
		}
	}

}
