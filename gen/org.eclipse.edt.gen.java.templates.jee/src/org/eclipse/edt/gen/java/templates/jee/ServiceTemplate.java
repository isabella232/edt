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

import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Service;

public class ServiceTemplate extends org.eclipse.edt.gen.java.templates.ServiceTemplate implements Constants {

	@SuppressWarnings("unchecked")
	public void preGenPartImport(Service service, Context ctx) {
		super.preGenPartImport(service, ctx);
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), org.eclipse.edt.gen.java.Constants.SubKey_partTypesImported);
		if (!typesImported.contains("org.eclipse.edt.javart.json.Json"))
			typesImported.add("org.eclipse.edt.javart.json.Json");
	}

	public void genFunction(Service service, Context ctx, TabbedWriter out, Function arg) {
		ctx.invoke(genFunctionSignatures, arg, ctx, out);
		ctx.invoke(genDeclaration, arg, ctx, out);
	}
	
	public void genFieldAnnotations(Service service, Context ctx, TabbedWriter out, Field field) {
		for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(field, ctx)) {
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
		}
	}
}
