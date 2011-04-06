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
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class PartTemplate extends JavaScriptTemplate {

	IRUtils utils = new IRUtils();

	public void validatePart(Part part, Context ctx, Object... args) {
		ctx.validate(validateClassBody, part, ctx, args);
	}

	public void genPart(Part part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genClassHeader, part, ctx, out, args);
		out.pushIndent();
		ctx.gen(genClassBody, part, ctx, out, args);
		out.println(")");
		out.popIndent();
		out.println(");");
	}

	public void genPartName(Part part, Context ctx, TabbedWriter out, Object... args) {
		if (ctx.mapsToNativeType(part))
			out.print(ctx.getNativeImplementationMapping(part));
		else
			out.print(part.getTypeSignature());
	}

	public void genClassName(Part part, TabbedWriter out, Object... args) {
		// Data tables might have an alias property.
		String nameOrAlias;
		Annotation annot = part.getAnnotation(IEGLConstants.PROPERTY_ALIAS);
		if (annot != null)
			nameOrAlias = (String) annot.getValue();
		else
			nameOrAlias = part.getId();
		out.print(Aliaser.getAlias(nameOrAlias));
	}

	public void genSuperClass(Part part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genSuperClass, part, ctx, out, args);
	}
}
