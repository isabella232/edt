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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.StringLiteral;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class DedicatedServiceTemplate extends JavaScriptTemplate {


	public void genDefaultValue(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) throws MofObjectNotFoundException, DeserializationException {
		Annotation eglLocation = field.getAnnotation(IEGLConstants.EGL_LOCATION);
		NewExpression newExpr = factory.createNewExpression();
		newExpr.setId(Constants.PartHttpProxy);
		if (eglLocation != null)
			newExpr.addAnnotation(eglLocation);
		String serviceName = (String)annot.getValue("serviceName");
		if(serviceName != null){
			StringLiteral stringLiteral = factory.createStringLiteral();
			if (eglLocation != null)
				stringLiteral.addAnnotation(eglLocation);
			stringLiteral.setValue(serviceName);
			newExpr.getArguments().add(stringLiteral);
		}
		ctx.invoke(genNewExpression, newExpr, ctx, out);
		out.println(";");
	}

}
