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
package org.eclipse.edt.mof.codegen.java;


import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateContext;


public class EEnumTemplate extends MofImplTemplate {
	
	public void genImpl(EEnum enumType, TemplateContext ctx, TabbedWriter out) {
		out.print("package ");
		out.print(getPackageName(enumType, ctx));
		out.println(';');
		out.println();

		out.print("public enum ");
		out.print(getETypeName(enumType));
		out.println(" {");
		out.pushIndent();
		
		for (int i = 0; i < enumType.getLiterals().size(); i++) {
			EEnumLiteral entry = enumType.getLiterals().get(i);
			out.print(entry.getName());
			if (i < enumType.getLiterals().size()-1)
				out.println(",");
		}
		out.println();
		out.popIndent();
		out.println("}");
	}
	
	public void genInterface(EEnum enumType, TemplateContext ctx, TabbedWriter out) {
		// Do nothing
	}
}
