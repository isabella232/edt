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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Field;

public class FieldTemplate extends MemberTemplate {

	public void validate(Field field, Context ctx, Object... args) {
		ctx.validate(validate, (EObject) field.getType(), ctx, args);
	}

	public void genInstantiation(Field field, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		ctx.gen(genInstantiation, field.getType(), ctx, out, field);
	}
	
	public void genDefaultValue(Field field, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		if (isReferenceType(field.getType()) || field.isNullable()) {
			out.print("null");
		}
		else if (ctx.mapsToPrimitiveType(field.getType().getClassifier())){
			ctx.gen(genDefaultValue, field.getType(), ctx, out, field);
		}
		else {
			ctx.gen(genInstantiation, (EObject)field.getType(), ctx, out, field);			
		}
	}

}
