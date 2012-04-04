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

import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.codegen.api.TemplateException;


public class EFactoryImplTemplate extends MofImplTemplate {

	public void genFactory(List<EClassifier> types, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		String packageName = types.get(0).getPackageName();
		String factoryName = (String)ctx.get(CTX_FactoryName);
		if (factoryName == null) {
			String[] packageNames = packageName.split("[.]");
			factoryName = packageNames[packageNames.length-1].substring(0,1).toUpperCase();
			factoryName += packageNames[packageNames.length-1].substring(1);
			factoryName += "Factory";
		}
		
		// Package and import declarations
		out.print("package ");
		out.print(packageName);
		out.println(";");
		out.println();
		out.println("import org.eclipse.edt.mof.EFactory;");
		out.println("import org.eclipse.edt.mof.EClass;");
		out.println("import org.eclipse.edt.mof.EEnum;");
		out.println("import org.eclipse.edt.mof.EDataType;");
		out.print("import ");
		out.print(packageName);
		out.print(".impl.");
		out.print(factoryName);
		out.println("Impl;");
		out.println();
		
		// Interface header
		out.print("public interface ");
		out.print(factoryName);
		out.print(" extends EFactory ");
		out.println(" {");
		out.print("public static final ");
		out.print(factoryName);
		out.print(" INSTANCE = new ");
		out.print(factoryName);
		out.println("Impl();");
		out.print("public String packageName = ");
		out.print("\"");
		out.print(packageName);
		out.println("\";");
		out.println();
		
		// Generate EClassifier typeSignature Constants
		for (EClassifier classifier : types) {
			out.print("String ");
			out.print(classifier.getName());
			out.print(" = packageName+");
			out.print("\".");
			out.print(classifier.getName());
			out.println("\";");
		}
		out.println();

		// Classifier get methods
		for (EClassifier classifier : types) {
			String type = "EClass";
			if (classifier instanceof EEnum) {
				type = "EEnum";
			}
			else if (classifier instanceof EDataType) {
				type = "EDataType";
			}
			String name = classifier.getName();
			out.print(type);
			out.print(" get");
			out.print(name);
			out.print(type);
			out.println("();");
		}
		
		// Instance creation methods
		for (EClassifier classifier : types) {
			if (classifier instanceof EClass && !(((EClass)classifier).isInterface() || ((EClass)classifier).isAbstract())) {
				EClass eClass = (EClass)classifier;
				String name = eClass.getName();
				out.print("public ");
				out.print(name);
				out.print(" create");
				out.print(name);
				out.println("();");
			}
		}
		
		out.println("}");

	}

	public void genFactoryImpl(List<EClassifier> types, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		
		String packageName = types.get(0).getPackageName();
		String factoryName = (String)ctx.get(CTX_FactoryName);
		if (factoryName == null) {
			String[] packageNames = packageName.split("[.]");
			factoryName = packageNames[packageNames.length-1].substring(0,1).toUpperCase();
			factoryName += packageNames[packageNames.length-1].substring(1);
			factoryName += "Factory";
		}
		out.print("package ");
		out.print(packageName);
		out.println(".impl;");
		out.println();
		out.print("import ");
		out.print(packageName);
		out.println(".*;");
		out.println("import org.eclipse.edt.mof.EClass;");
		out.println("import org.eclipse.edt.mof.EEnum;");
		out.println("import org.eclipse.edt.mof.EDataType;");
		out.println("import org.eclipse.edt.mof.impl.EFactoryImpl;");
		out.println();
		
		out.print("public class ");
		out.print(factoryName);
		out.print("BaseImpl");
		out.print(" extends EFactoryImpl implements ");
		out.print(factoryName);
		out.println(" {");
		
		for (EClassifier classifier : types) {
			String type = "EClass";
			if (classifier instanceof EEnum) {
				type = "EEnum";
			}
			else if (classifier instanceof EDataType) {
				type = "EDataType";
			}
			String name = classifier.getName();
			out.println("@Override");
			out.print("public ");
			out.print(type);
			out.print(" get");
			out.print(name);
			out.print(type);
			out.println("() {");
			out.print("return ");
			out.print('(');
			out.print(type);
			out.print(')');
			out.print("getTypeNamed(");
			out.print(name);
			out.println(");");
			out.println('}');
			out.println();

		}

		for (EClassifier classifier : types) {
			if (classifier instanceof EClass && !(((EClass)classifier).isInterface() || ((EClass)classifier).isAbstract())) {
				EClass eClass = (EClass)classifier;
				String name = eClass.getName();
				out.println("@Override");
				out.print("public ");
				out.print(name);
				out.print(" create");
				out.print(name);
				out.println("() {");
				out.print("return ");
				out.print('(');
				out.print(name);
				out.print(")get");
				out.print(name);
				out.println("EClass().newInstance();");
				out.println("}");
				out.println();
			}
		}
		
		out.println("}");
	}
	

}
