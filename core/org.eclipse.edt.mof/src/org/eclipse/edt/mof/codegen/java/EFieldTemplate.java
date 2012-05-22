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


import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateContext;


public class EFieldTemplate extends MofImplTemplate {

	public void genImplSetGet(EField field, TemplateContext ctx, TabbedWriter out) {
		if (field.getEType().getEClassifier() == factory.getEListEDataType()) {
			out.println("@SuppressWarnings(\"unchecked\")");
		}
//		if (field.getType().getEClassifier() != azure.getEListEDataType()) {
//			out.println("@SuppressWarnings(\"unchecked\")");
//		}
		out.println("@Override");
		out.println("public " + getETypeName(field) + " " + GenUtils.getterName(field) + "() {");
		out.pushIndent();
		out.println("return (" + getETypeName(field) + ")slotGet(Slot_" + field.getName() + ");");
		out.popIndent();
		out.println('}');
		out.println();

		if (field.getEType().getEClassifier() != factory.getEListEDataType()) {
			out.println("@Override");
			out.println("public void " + setterName(field) + "(" + getETypeName(field) + " value) {");
			out.pushIndent();
			out.println("slotSet(Slot_" + field.getName() +", value);");
			out.popIndent();
			out.println('}');
			out.println();
		}
	}
	

	public void genInterfaceSetGet(EField field, TemplateContext ctx, TabbedWriter out) {
		out.println(getETypeName(field) + " " + getterName(field) + "();");
		out.println();

		if (field.getEType().getEClassifier() != factory.getEListEDataType()) {
			out.println("void " + setterName(field) + "(" + getETypeName(field) + " value);");
			out.println();
		}
	}


}
