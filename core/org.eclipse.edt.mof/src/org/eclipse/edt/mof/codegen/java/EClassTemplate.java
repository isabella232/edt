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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EParameter;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.Template;
import org.eclipse.edt.mof.codegen.api.TemplateContext;
import org.eclipse.edt.mof.codegen.api.TemplateException;


public class EClassTemplate extends MofImplTemplate {

	public void genImpl(EClass eClass, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		
		genImplPackageDecl(eClass, ctx, out);
		genImplImports(eClass, ctx, out);
		genImplClassHeader(eClass, ctx, out);
		genBaseClassBody(eClass, ctx, out);
		
		Template fieldTemplate;
		for (EField field : eClass.getEFields()) {
			fieldTemplate = ctx.getTemplateFor(field.getEClass());
			fieldTemplate.gen("genImplSetGet", field, ctx, out);
		}
		
		Template functionTemplate;
		for (EFunction function : eClass.getEFunctions()) {
			functionTemplate = ctx.getTemplateFor(function.getEClass());
			functionTemplate.gen("genImpl", function, ctx, out);
		}
		
		out.popIndent();
		out.print('}');

	}
	/**
	 * Generate only the fields as part of a Base class that can regenerated.
	 * See <code>genImplExtendBase</code> which would extend the base class
	 * to contain functions that are intended to be written.  The extended class
	 * is not intended to be regenerated.
	 * @param eClass
	 * @param ctx
	 * @param out
	 * @throws TemplateException
	 */
	public void genImplBase(EClass eClass, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		
		genImplPackageDecl(eClass, ctx, out);
		genImplImports(eClass, ctx, out);
		genBaseClassHeader(eClass, ctx, out);
		genBaseClassBody(eClass, ctx, out);
		
		Template fieldTemplate;
		for (EField field : eClass.getEFields()) {
			fieldTemplate = ctx.getTemplateFor(field.getEClass());
			fieldTemplate.gen("genImplSetGet", field, ctx, out);
		}
		out.popIndent();
		out.print('}');

	}

	public void genImplExtendsBase(EClass eClass, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		
		genImplPackageDecl(eClass, ctx, out);

		Set<EClassifier> imports = new HashSet<EClassifier>();
		imports.add(eClass);
		for (EFunction function : eClass.getEFunctions()) {
			EType type = function.getEType();
			if (type != null && needsImport(type.getEClassifier())) {
				imports.add((EClassifier)type.getEClassifier());
			}
			for (EParameter parm : function.getEParameters()) {
				type = parm.getEType().getEClassifier();
				if (type != null && needsImport((EClassifier)type)) {
					imports.add((EClassifier)type);
				}
			}
		}
		for (EClassifier classifier : imports) {
			out.print("import ");
			out.print(classifier.getETypeSignature());
			out.println(";");
		}
		out.println();
			
		genImplExtendsBaseClassHeader(eClass, ctx, out);
				
		Template functionTemplate;
		for (EFunction function : eClass.getEFunctions()) {
			functionTemplate = ctx.getTemplateFor(function.getEClass());
			functionTemplate.gen("genImpl", function, ctx, out);
		}
		
		out.popIndent();
		out.print('}');

	}

	
	public void genInterface(EClass eClass, TemplateContext ctx, TabbedWriter out) throws TemplateException {
		
		genPackageDecl(eClass, ctx, out);
		genImports(eClass, ctx, out);
		genClassHeader(eClass, ctx, out);
				
		Template fieldTemplate;
		for (EField field : eClass.getEFields()) {
			fieldTemplate = ctx.getTemplateFor(field.getEClass());
			fieldTemplate.gen("genInterfaceSetGet", field, ctx, out);
		}

		Template functionTemplate;
		for (EFunction function : eClass.getEFunctions()) {
			functionTemplate = ctx.getTemplateFor(function.getEClass());
			functionTemplate.gen("genInterface", function, ctx, out);
		}
		
		out.popIndent();
		out.print('}');

	}

	
	public void genImplPackageDecl(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		out.print("package ");
		out.print(getPackageName(eClass, ctx));
		out.println(".impl;");
		out.println();
	}
	
	public void genImplImports(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		for (EClassifier type : GenUtils.getImportTypes(eClass, true)) {
			genImplImport(eClass, type, ctx, out);
		}
		out.println();
	}
	
	public void genImplImport(EClass eClass, EClassifier importType, TemplateContext ctx, TabbedWriter out) {
		if (!eClass.getSuperTypes().isEmpty() && eClass.getSuperTypes().get(0) == importType) {
			out.print("import ");
			out.print(getPackageName(importType, ctx));
			out.print(".impl.");
			out.print(importType.getEClassifier().getName());
			out.print("Impl");
			out.println(";");
		}
		out.print("import ");
		out.print(getETypeSignature(importType, ctx));
		out.println(';');
	}
	
	public void genBaseClassHeader(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		boolean noSuperType = getSuperType(eClass) == null;
		out.print("public abstract ");
		out.print("class ");
		out.print(eClass.getName());
		out.print("ImplBase");
		out.print(noSuperType ? " " : " extends " + getSuperType(eClass).getName() + "Impl");
		out.print(" implements ");
		out.print(eClass.getName());
		out.println(" {");
		out.pushIndent();
	}

	public void genImplExtendsBaseClassHeader(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		out.print("public");
		out.print(eClass.isAbstract() ? " abstract " : " ");
		out.print("class ");
		out.print(eClass.getName());
		out.print("Impl");
		out.print(" extends " + eClass.getName() + "ImplBase");
		out.print(" implements ");
		out.print(eClass.getName());
		out.println(" {");
		out.pushIndent();
	}

	public void genImplClassHeader(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		boolean noSuperType = getSuperType(eClass) == null;
		out.print("public");
		out.print(eClass.isAbstract() ? " abstract " : " ");
		out.print("class ");
		out.print(eClass.getName());
		out.print("Impl");
		out.print(noSuperType ? " " : " extends " + getSuperType(eClass).getName() + "Impl");
		out.print(" implements ");
		out.print(eClass.getName());
		out.println(" {");
		out.pushIndent();
	}


	public void genBaseClassBody(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		boolean noSuperType = getSuperType(eClass) == null;
		if (!eClass.getEFields().isEmpty()) {
			for (int i=0; i<eClass.getEFields().size(); i++) { 
				out.println("private static int Slot_" + eClass.getEFields().get(i).getName() + "=" + i + ";");
			}
			out.println("private static int totalSlots = " + eClass.getEFields().size() + ";");
			out.println();
			out.println("public static int totalSlots() {");
			out.pushIndent();
			if (noSuperType) {
				out.println("return totalSlots;");
			} else {
				out.println("return totalSlots + " + GenUtils.getSuperType(eClass).getName() + "Impl.totalSlots();");
			} 
			
			out.popIndent();
			out.println('}');
			out.println();
			
			if (!noSuperType) {	
				out.println("static {");
				out.pushIndent();
				out.print("int offset = ");
				out.print(GenUtils.getSuperType(eClass).getName());
				out.println("Impl.totalSlots();");
			
				for (int i=0; i<eClass.getEFields().size(); i++) {
					out.println("Slot_" + eClass.getEFields().get(i).getName() + " += offset;");
				}
				out.popIndent();
				out.println('}');
			}
		}
	}
	
	public void genPackageDecl(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		out.print("package ");
		out.print(getPackageName(eClass, ctx));
		out.println(';');
		out.println();
	}
	
	public void genImports(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		for (EClassifier type : getImportTypes(eClass, false)) {
			genImport(type, ctx, out);
		}
		out.println();
	}
	
	public void genImport(EClassifier type, TemplateContext ctx, TabbedWriter out) {
		out.print("import ");
		out.print(getETypeSignature(type, ctx));
		out.println(';');
	}
	
	public void genClassHeader(EClass eClass, TemplateContext ctx, TabbedWriter out) {
		boolean noSuperType = getSuperType(eClass) == null;
		out.print("public interface ");
		out.print(eClass.getName());
		if (!noSuperType) {
			out.print(" extends ");
			List<EClass> types = eClass.getSuperTypes();
			for(int i = 0; i< types.size(); i++) {
				out.print(types.get(i).getName());
				if (i < types.size() - 1) {
					out.print(", ");
				}
			}
		}
		out.println(" {");
		out.pushIndent();

	}

}
