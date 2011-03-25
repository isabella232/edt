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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.utils.Aliaser;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public abstract class PartTemplate extends TypeTemplate {

	IRUtils utils = new IRUtils();

	public void validate(Part part, Context ctx, Object... args) {
		validateClassBody(part, ctx, args);
	}

	public abstract void validateClassBody(Part part, Context ctx, Object... args);

	public void genPart(Part part, Context ctx, TabbedWriter out, Object... args) {
		genDefineClause(part, ctx, out, args);
		out.pushIndent();
		genBody(part, ctx, out, args);
		out.println(")");
		out.popIndent();
		out.println(");");
	}

	public abstract void genDefineClause(Part part, Context ctx, TabbedWriter out, Object... args);

	public abstract void genBody(Part part, Context ctx, TabbedWriter out, Object... args);


	public void genPartName(Part part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		if (ctx.mapsToNativeType(part)) {
			out.print(ctx.getNativeImplementationMapping(part));
		} else {
			out.print(part.getTypeSignature());
		}
	}

	public void genClassName(Part part, TabbedWriter out, Object... args) {
		out.print(getClassName(part));
	}

	public String getClassName(Part part) {
		// Data tables might have an alias property.
		String nameOrAlias;
		Annotation annot = part.getAnnotation(IEGLConstants.PROPERTY_ALIAS);
		if (annot != null) {
			nameOrAlias = (String) annot.getValue();
		} else {
			nameOrAlias = part.getId();
		}
		return Aliaser.getAlias(nameOrAlias);
	}
}
