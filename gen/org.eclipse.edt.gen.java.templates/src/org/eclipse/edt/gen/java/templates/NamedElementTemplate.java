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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class NamedElementTemplate extends JavaTemplate {

	public void genAccessor(NamedElement element, Context ctx, TabbedWriter out) {
		String propertyFunction = CommonUtilities.getPropertyFunction(element, false, ctx);
		if (propertyFunction != null) {
			FunctionMember currentFunction = ctx.getCurrentFunction();
			if (currentFunction != null && propertyFunction.equals(currentFunction.getCaseSensitiveName()))
				genName(element, ctx, out);
			else {
				out.print(propertyFunction);
				out.print("()");
			}
		} else
			genName(element, ctx, out);
	}

	public void genName(NamedElement element, Context ctx, TabbedWriter out) {
		if (element instanceof Member && ((Member) element).getContainer() instanceof ExternalType) {
			Member member = (Member) element;
			if (CommonUtilities.isJavaExternalType((ExternalType) member.getContainer())) {
				// for java external types, either use the given external name or the name as is
				if (member.getAnnotation("eglx.lang.ExternalName") != null)
					out.print((String) member.getAnnotation("eglx.lang.ExternalName").getValue());
				else
					out.print(element.getCaseSensitiveName());
				return;
			}
		}
		out.print(JavaAliaser.getAlias(element.getCaseSensitiveName()));
	}
}
