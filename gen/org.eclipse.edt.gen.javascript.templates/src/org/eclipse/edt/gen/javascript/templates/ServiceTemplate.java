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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;

public class ServiceTemplate extends JavaScriptTemplate {

	public void genPart(Service service, Context ctx, TabbedWriter out) {}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out) {
		out.print("null");
	}

	public void genDefaultValue(Service service, Context ctx, TabbedWriter out, Field field) {
		if(field.getAnnotation(Constants.AnnotationResource)!= null){
			Annotation annot = field.getAnnotation(Constants.AnnotationResource);
			ctx.invoke(genDefaultValue, annot.getEClass(), ctx, out, annot, field);
		}
		else if(field.getAnnotation(Constants.AnnotationDedicatedService)!= null){
			Annotation annot = field.getAnnotation(Constants.AnnotationDedicatedService);
			ctx.invoke(genDefaultValue, annot.getEClass(), ctx, out, annot, field);
		}
		else{
			ctx.invokeSuper(this, genDefaultValue, service, ctx, out, field);
		}
			
	}
	public void genConversionOperation(Service service, Context ctx, TabbedWriter out, AsExpression arg) {
		ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
	}
}
