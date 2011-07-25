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

import java.util.List;
import java.util.Map;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class EGLClassTemplate extends JavaScriptTemplate {

	public void preGenClassBody(EGLClass part, Context ctx) {
		ctx.invoke(preGenUsedParts, part, ctx);
		ctx.invoke(preGenFields, part, ctx);
		ctx.invoke(preGenFunctions, part, ctx);
		addNamespaceMap(part, ctx);
	}

	private void addNamespaceMap(EGLClass part, Context ctx){
		String localName = part.getName();
		String namespace = CommonUtilities.createNamespaceFromPackage(part);
		Annotation annot = part.getAnnotation("eglx.xml._bind.annotation.xmlRootElement");
		if(annot != null){
			if (annot.getValue("namespace") != null && 
					((String)annot.getValue("namespace")).length() > 0)
			{
				namespace = (String) annot.getValue("namespace");
			}
			if (annot.getValue("name") != null && 
					((String)annot.getValue("name")).length() > 0)
			{
				localName = (String) annot.getValue("name");
			}
			
		}
		ctx.addNamespace(namespace, localName, part.getFullyQualifiedName());
	}
	public void preGenUsedParts(EGLClass part, Context ctx) {
		for (Part item : IRUtils.getReferencedPartsFor(part)) {
			ctx.invoke(preGenUsedPart, part, ctx, item);
		}
	}

	public void preGenUsedPart(EGLClass part, Context ctx, Part arg) {
		ctx.invoke(preGen, arg, ctx);
	}

	public void preGenFields(EGLClass part, Context ctx) {
		for (Field field : part.getFields()) {
			ctx.invoke(preGenField, part, ctx, field);
		}
	}

	public void preGenField(EGLClass part, Context ctx, Field arg) {
		ctx.invoke(preGen, arg, ctx);
	}

	public void preGenFunctions(EGLClass part, Context ctx) {
		for (Function function : part.getFunctions()) {
			ctx.invoke(preGenFunction, part, ctx, function);
		}
	}

	public void preGenFunction(EGLClass part, Context ctx, Function arg) {
		ctx.invoke(preGen, arg, ctx);
	}

	public void genClassHeader(EGLClass part, Context ctx, TabbedWriter out) {
		out.print("egl.defineClass(");
		out.print(singleQuoted(part.getPackageName().toLowerCase()));
		out.print(", ");
		out.print(quoted(part.getName()));
		out.print(", ");
		ctx.invoke(genSuperClass, part, ctx, out);
		out.print(", ");
		out.println("{");
	}

	public void genClassBody(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genConstructors, part, ctx, out);
		out.println(",");
		ctx.invoke(genSetEmptyMethods, part, ctx, out);
		out.println(",");
		ctx.invoke(genInitializeMethods, part, ctx, out);
		out.println(",");
		ctx.invoke(genCloneMethods, part, ctx, out);
		// out.println(",");
		// genGetFieldSignaturesMethod(part, ctx, out, args);
		// out.println(",");
		// genGetJSONNamesMethod(part, ctx, out, args);
		// out.println(",");
		// genFromJSONMethod(part, ctx, out, args);
		// out.println(",");
		// genToJSONMethod(part, ctx, out, args);
		// out.println(",");
		out.println(",");
		ctx.invoke(genXmlAnnotations, part, ctx, out);
		out.println(",");
		ctx.invoke(genNamespaceMap, part, ctx, out);
		ctx.invoke(genFunctions, part, ctx, out);
		ctx.invoke(genFields, part, ctx, out);
		ctx.invoke(genGetterSetters, part, ctx, out);
	}

	public void genFields(EGLClass part, Context ctx, TabbedWriter out) {
		for (Field field : part.getFields()) {
			ctx.invoke(genField, part, ctx, out, field);
		}
	}

	public void genField(EGLClass part, Context ctx, TabbedWriter out, Field arg) {}

	public void genConstructors(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genConstructor, part, ctx, out);
	}

	public void genConstructor(EGLClass part, Context ctx, TabbedWriter out) {
		// Generate default constructor
		out.print(quoted("constructor"));
		out.println(": function() {");
		ctx.invoke(genLibraries, part, ctx, out);
		out.println("this.eze$$setInitial();");
		out.println("}");
	}

	@SuppressWarnings("unchecked")
	public void genLibraries(EGLClass part, Context ctx, TabbedWriter out) {
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partLibrariesUsed);
		for (Library library : libraries) {
			ctx.invoke(genLibrary, part, ctx, out, library);
		}
	}
	
	public void genLibrary(EGLClass part, Context ctx, TabbedWriter out, Library arg) {
		ctx.invoke(genInstantiation, arg, ctx, out);
		out.println(";");
	}
	
	public void genSetEmptyMethods(EGLClass part, Context ctx, TabbedWriter out) {
		out.print(quoted("eze$$setEmpty"));
		out.println(": function() {");
		ctx.invoke(genSetEmptyMethodBody, part, ctx, out);
		out.println("}");
	}

	public void genSetEmptyMethodBody(EGLClass part, Context ctx, TabbedWriter out) {
		for (Field field : part.getFields()) {
			ctx.invoke(genSetEmptyMethod, part, ctx, out, field);
		}
	}

	public void genSetEmptyMethod(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		out.print("this.");
		ctx.invoke(genName, arg, ctx, out);
		out.print(" = ");
		ctx.invoke(genInitialization, arg, ctx, out);
		out.println(";");
	}

	public void genInitializeMethods(EGLClass part, Context ctx, TabbedWriter out) {
		out.print(quoted("eze$$setInitial"));
		out.println(": function() {");
		ctx.invoke(genInitializeMethodBody, part, ctx, out);
		out.println("};");
	}

	public void genInitializeMethodBody(EGLClass part, Context ctx, TabbedWriter out) {
		// TODO sbg Not sure whether we need this out.println("eze$setEmpty();");
		for (Field field : part.getFields()) {
			ctx.invoke(genInitializeMethod, part, ctx, out, field);
		}
	}

	public void genInitializeMethod(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		if (arg.getInitializerStatements() != null)
			ctx.invoke(genStatementNoBraces, arg.getInitializerStatements(), ctx, out);
		else {
			if (arg.getContainer() != null && arg.getContainer() instanceof Type)
				ctx.invoke(genQualifier, arg.getContainer(), ctx, out, arg);
			ctx.invoke(genName, arg, ctx, out);
			out.print(" = ");
			ctx.invoke(genInitialization, arg, ctx, out);
			out.println(";");
		}
	}

	public void genCloneMethods(EGLClass part, Context ctx, TabbedWriter out) {
		out.print(quoted("eze$$clone"));
		out.println(": function() {");
		ctx.invoke(genCloneMethodBody, part, ctx, out);
		out.println("}");
	}

	public void genCloneMethodBody(EGLClass part, Context ctx, TabbedWriter out) {
		String temp1 = "ezert$$1";
		String temp2 = "ezert$$2";
		out.print("var ");
		out.print(temp1);
		out.println(" = this;");
		out.print("var ");
		out.print(temp2);
		out.print(" = ");
		ctx.invoke(genInstantiation, part, ctx, out);
		out.println(";");

		out.print(temp2);
		out.println(".eze$$isNull = this.eze$$isNull;");

		out.print(temp2);
		out.println(".eze$$isNullable = this.eze$$isNullable;");

		// clone fields
		for (Field field : part.getFields()) {
			ctx.invoke(genCloneMethod, part, ctx, out, field);
		}

		out.print(temp2);
		out.print(".setNull(");
		out.print(temp1);
		out.println("eze$$isNull);");

		out.print("return ");
		out.print(temp2);
		out.println(";");
	}

	public void genCloneMethod(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		String temp1 = "ezert$$1";
		String temp2 = "ezert$$2";
		out.print(temp2);
		out.print(".");
		ctx.invoke(genName, arg, ctx, out);
		out.print(" = ");
		out.print(temp1);
		out.print(".");
		ctx.invoke(genName, arg, ctx, out);
		out.println(";");
	}

	public void genGetterSetters(EGLClass part, Context ctx, TabbedWriter out) {
		String delim = ",";
		for (Field field : part.getFields()) {
			out.print(delim);
			ctx.invoke(genGetterSetter, part, ctx, out, field);
		}
	}

	// we only generate getter and setters for fields within records or libraries, so do nothing if it gets back here
	public void genGetterSetter(EGLClass part, Context ctx, TabbedWriter out, Field arg) {}

	public void genFunctions(EGLClass part, Context ctx, TabbedWriter out) {
		for (Function function : part.getFunctions()) {
			ctx.invoke(genFunction, part, ctx, out, function);
		}
	}

	public void genFunction(EGLClass part, Context ctx, TabbedWriter out, Function arg) {
		out.println(",");
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genAdditionalConstructorParams(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genAdditionalSuperConstructorArgs(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genDeclaration(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genSuperClass(EGLClass part, Context ctx, TabbedWriter out) {}

	public void genXmlAnnotations(EGLClass part, Context ctx, TabbedWriter out){
		out.print(quoted("eze$$getXmlPartAnnotations"));
		out.println(": function() {");
		//create the XMLAnnotationMap
		out.println("var xmlAnnotations = {};");
		Annotation annot = part.getAnnotation("eglx.xml._bind.annotation.xmlRootElement");
		String namespace = null;
		if (annot != null && annot.getValue("namespace") != null && 
				((String)annot.getValue("namespace")).length() > 0)
		{
			namespace = (String) annot.getValue("namespace");
		}
		String name = part.getName();
		if (annot != null && annot.getValue("name") != null && 
				((String)annot.getValue("name")).length() > 0)
		{
			name = (String) annot.getValue("name");
		}
		Boolean isNillable = Boolean.FALSE;
		if (annot != null && annot.getValue("nillable") != null)
		{
			isNillable = CommonUtilities.convertBoolean(annot.getValue("nillable"));
		}
		out.println("xmlAnnotations[\"XMLRootElement\"] = new egl.eglx.xml._bind.annotation.XMLRootElement(" + 
				(name == null ? "null" : quoted(name)) + ", " +
				(namespace == null ? "null" : quoted(namespace)) + 
				", " + isNillable.toString() + ");");
		
		annot = part.getAnnotation("eglx.xml._bind.annotation.XMLStructure");
		if (annot != null && annot.getValue("value") != null) 
		{
			String value;
			/*  choice = 1,
			  sequence = 2,
			  simpleContent = 3,
			  unordered = 4*/
			switch((Integer)annot.getValue("value")){
				case 1:
					value = "choice";
					break;
				case 2:
					value = "sequence";
					break;
				case 3:
					value = "simpleContent";
					break;
				default:
					value = "unordered";
			}
			out.println("xmlAnnotations[\"XMLStructure\"] = egl.eglx.xml._bind.annotation.XMLStructure(" + quoted(value) + ");");
		} 		

		out.println("return xmlAnnotations;");
		out.println("},");
		out.print(quoted("eze$$getXmlFields"));
		out.println(": function() {");
		out.println("var xmlAnnotations;");
		out.println("fields = new Array();");
		int idx = 0;
		for (Field field : part.getFields()) {
			if (field instanceof ConstantField ||
					field.isStatic()) {
				continue;
			}
			ctx.invoke(genXmlField, field, ctx, out, Integer.valueOf(idx));
			idx++;
		}		
		out.println("return fields;");
		out.println("}");
	}
	public void genNamespaceMap(EGLClass part, Context ctx, TabbedWriter out) {
		out.print(quoted("eze$$resolvePart"));
		out.println(": function(/*string*/ namespace, /*string*/ localName) {");
		out.println("if(this.namespaceMap == undefined){");
		out.println("this.namespaceMap = {};");
		for (Map.Entry<String, String> entry : ctx.getNamespaceMap().entrySet()) {
			out.println("this.namespaceMap[" + quoted(entry.getKey())+ "] = " + quoted(entry.getValue()) + ";");
		}		
		out.println("}");
		out.println("var newObject = null;");
		out.println("var className = this.namespaceMap[namespace + \"{\" + localName + \"}\"];");
		out.println("if(className != undefined && className != null){");
		out.println("newObject = instantiate(className, []);");
		out.println("};");
		out.println("return newObject;");
		out.println("}");
	}
}
