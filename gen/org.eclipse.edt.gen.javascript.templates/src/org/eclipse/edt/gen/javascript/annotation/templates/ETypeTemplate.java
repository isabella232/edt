/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Part;

public class ETypeTemplate extends JavaScriptTemplate {

	public void genConversionControlAnnotation(EType type, Context ctx, TabbedWriter out, Annotation annot, Object obj) {
	}

	public void genDefaultValue(EType type, Context ctx, TabbedWriter out, Annotation annot, Field field){
	}
	
	public void preGen(EType type, Context ctx, Annotation annot, Field field){
	}
	
	public void preGen(EType type, Context ctx, Annotation annot, Part part){
	}
	
	public void preGen(EType type, Context ctx, Annotation annot, Function function){
	}
}
