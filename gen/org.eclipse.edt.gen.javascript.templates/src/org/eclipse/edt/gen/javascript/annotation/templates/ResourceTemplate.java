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


import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.StringLiteral;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ResourceTemplate extends JavaScriptTemplate {

	public void genDefaultValue(AnnotationType type, Context ctx, TabbedWriter out, Annotation annot, Field field) {
		ExternalType serviceLib = (ExternalType)TypeUtils.getType(TypeUtils.EGL_KeyScheme + Constants.LibrarySys); //.clone();
		QualifiedFunctionInvocation invocation = factory.createQualifiedFunctionInvocation();
		PartName partName  = factory.createPartName();
		partName.setType(serviceLib);
		invocation.setId("getResource");
		invocation.setQualifier(partName);
		StringLiteral uri = factory.createStringLiteral();
		invocation.getArguments().add(uri);
		if(annot.getValue(Constants.SubKey_uri) instanceof String && !((String)annot.getValue(Constants.SubKey_uri)).isEmpty()){
			uri.setValue((String)annot.getValue(Constants.SubKey_uri));
		}
		else{
			uri.setValue("binding:" + field.getName());
		}
		ctx.invoke(genExpression, invocation, ctx, out);
	}
}
