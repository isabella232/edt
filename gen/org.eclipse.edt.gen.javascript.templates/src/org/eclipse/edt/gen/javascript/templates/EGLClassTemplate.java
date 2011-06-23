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

	public void validateClassBody(EGLClass part, Context ctx, Object... args) {
		ctx.validate(validateUsedParts, part, ctx, args);
		ctx.validate(validateFields, part, ctx, args);
		ctx.validate(validateFunctions, part, ctx, args);
		addNamespaceMap(part, ctx);
	}

	private void addNamespaceMap(EGLClass part, Context ctx){
		String localName = part.getName();
		String namespace = CommonUtilities.createNamespaceFromPackage(part);
		Annotation annot = part.getAnnotation("egl.core.xmlRootElement");
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
	public void validateUsedParts(EGLClass part, Context ctx, Object... args) {
		for (Part item : IRUtils.getReferencedPartsFor(part)) {
			ctx.validate(validateUsedPart, part, ctx, item);
		}
	}

	public void validateUsedPart(EGLClass part, Context ctx, Object... args) {
		ctx.validate(validate, (Part) args[0], ctx, args);
	}

	public void validateFields(EGLClass part, Context ctx, Object... args) {
		for (Field field : part.getFields()) {
			ctx.validate(validateField, part, ctx, field);
		}
	}

	public void validateField(EGLClass part, Context ctx, Object... args) {
		ctx.validate(validate, (Field) args[0], ctx, args);
	}

	public void validateFunctions(EGLClass part, Context ctx, Object... args) {
		for (Function function : part.getFunctions()) {
			ctx.validate(validateFunction, part, ctx, function);
		}
	}

	public void validateFunction(EGLClass part, Context ctx, Object... args) {
		ctx.validate(validate, (Function) args[0], ctx, args);
	}

	public void genClassHeader(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.defineClass(");
		out.print(singleQuoted(part.getPackageName().toLowerCase()));
		out.print(", ");
		out.print(quoted(part.getName()));
		out.print(", ");
		ctx.gen(genSuperClass, part, ctx, out, args);
		out.print(", ");
		out.println("{");
	}

	public void genClassBody(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genConstructors, part, ctx, out, args);
		out.println(",");
		ctx.gen(genSetEmptyMethods, part, ctx, out, args);
		out.println(",");
		ctx.gen(genInitializeMethods, part, ctx, out, args);
		out.println(",");
		ctx.gen(genCloneMethods, part, ctx, out, args);
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
		ctx.gen("genXmlAnnotations", part, ctx, out, args);
		out.println(",");
		ctx.gen("genNamespaceMap", part, ctx, out, args);
		ctx.gen(genFunctions, part, ctx, out, args);
		ctx.gen(genFields, part, ctx, out, args);
		ctx.gen(genGetterSetters, part, ctx, out, args);
	}

	public void genFields(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : part.getFields()) {
			ctx.gen(genField, part, ctx, out, field);
		}
	}

	public void genField(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genConstructors(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genConstructor, part, ctx, out, args);
	}

	public void genConstructor(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		// Generate default constructor
		out.print(quoted("constructor"));
		out.println(": function() {");
		ctx.gen(genLibraries, part, ctx, out, args);
		out.println("this.eze$$setInitial();");
		out.println("}");
	}

	@SuppressWarnings("unchecked")
	public void genLibraries(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		List<Library> libraries = (List<Library>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partLibrariesUsed);
		for (Library library : libraries) {
			ctx.gen(genLibrary, part, ctx, out, library);
		}
	}
	
	public void genLibrary(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInstantiation, args[0], ctx, out, args);
		out.println(";");
	}
	
	public void genSetEmptyMethods(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print(quoted("eze$$setEmpty"));
		out.println(": function() {");
		ctx.gen(genSetEmptyMethodBody, part, ctx, out, args);
		out.println("}");
	}

	public void genSetEmptyMethodBody(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : part.getFields()) {
			ctx.gen(genSetEmptyMethod, part, ctx, out, field);
		}
	}

	public void genSetEmptyMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print("this.");
		ctx.gen(genName, ((Field) args[0]), ctx, out, args);
		out.print(" = ");
		ctx.gen(genInitialization, ((Field) args[0]), ctx, out, args);
		out.println(";");
	}

	public void genInitializeMethods(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print(quoted("eze$$setInitial"));
		out.println(": function() {");
		ctx.gen(genInitializeMethodBody, part, ctx, out, args);
		out.println("};");
	}

	public void genInitializeMethodBody(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		// TODO sbg Not sure whether we need this out.println("eze$setEmpty();");
		for (Field field : part.getFields()) {
			ctx.gen(genInitializeMethod, part, ctx, out, field);
		}
	}

	public void genInitializeMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		if (((Field) args[0]).getInitializerStatements() != null)
			ctx.gen(genStatementNoBraces, ((Field) args[0]).getInitializerStatements(), ctx, out, args);
		else {
			if (((Field) args[0]).getContainer() != null && ((Field) args[0]).getContainer() instanceof Type)
				ctx.gen(genQualifier, ((Field) args[0]).getContainer(), ctx, out, args);
			ctx.gen(genName, ((Field) args[0]), ctx, out, args);
			out.print(" = ");
			ctx.gen(genInitialization, ((Field) args[0]), ctx, out, args);
			out.println(";");
		}
	}

	public void genCloneMethods(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print(quoted("eze$$clone"));
		out.println(": function() {");
		ctx.gen(genCloneMethodBody, part, ctx, out, args);
		out.println("}");
	}

	public void genCloneMethodBody(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		String temp1 = "ezert$$1";
		String temp2 = "ezert$$2";
		out.print("var ");
		out.print(temp1);
		out.println(" = this;");
		out.print("var ");
		out.print(temp2);
		out.print(" = ");
		ctx.gen(genInstantiation, part, ctx, out, args);
		out.println(";");

		out.print(temp2);
		out.println(".eze$$isNull = this.eze$$isNull;");

		out.print(temp2);
		out.println(".eze$$isNullable = this.eze$$isNullable;");

		// clone fields
		for (Field field : part.getFields()) {
			ctx.gen(genCloneMethod, part, ctx, out, field);
		}

		out.print(temp2);
		out.print(".setNull(");
		out.print(temp1);
		out.println("eze$$isNull);");

		out.print("return ");
		out.print(temp2);
		out.println(";");
	}

	public void genCloneMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		String temp1 = "ezert$$1";
		String temp2 = "ezert$$2";
		out.print(temp2);
		out.print(".");
		ctx.gen(genName, ((Field) args[0]), ctx, out, args);
		out.print(" = ");
		out.print(temp1);
		out.print(".");
		ctx.gen(genName, ((Field) args[0]), ctx, out, args);
		out.println(";");
	}

	public void genGetterSetters(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		String delim = ",";
		for (Field field : part.getFields()) {
			out.print(delim);
			ctx.gen(genGetterSetter, part, ctx, out, field);
		}
	}

	// we only generate getter and setters for fields within records or libraries, so do nothing if it gets back here
	public void genGetterSetter(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genFunctions(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		for (Function function : part.getFunctions()) {
			ctx.gen(genFunction, part, ctx, out, function);
		}
	}

	public void genFunction(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.println(",");
		ctx.gen(genDeclaration, (Function) args[0], ctx, out, args);
	}

	public void genAdditionalConstructorParams(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genAdditionalSuperConstructorArgs(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genDeclaration(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genSuperClass(EGLClass part, Context ctx, TabbedWriter out, Object... args) {}

	public void genXmlAnnotations(EGLClass part, Context ctx, TabbedWriter out, Object... args){
		out.print(quoted("eze$$getXmlPartAnnotations"));
		out.println(": function() {");
		//create the XMLAnnotationMap
		out.println("var xmlAnnotations = {};");
		Annotation annot = part.getAnnotation("egl.core.xmlRootElement");
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
		if (annot != null && annot.getValue("isNillable") instanceof Boolean)
		{
			isNillable = (Boolean) annot.getValue("isNillable");
		}
		out.println("xmlAnnotations[\"XMLRootElement\"] = new egl.egl.core.xml.XMLRootElement(" + 
				(name == null ? "null" : quoted(name)) + ", " +
				(namespace == null ? "null" : quoted(namespace)) + 
				", " + isNillable.toString() + ");");
		
		annot = part.getAnnotation("egl.core.XMLStructure");
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
			out.println("xmlAnnotations[\"XMLStructure\"] = egl.egl.core.xml.XMLStructure(" + quoted(value) + ");");
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
			ctx.gen("genXmlField", field, ctx, out, Integer.valueOf(idx));
			idx++;
		}		
		out.println("return fields;");
		out.println("}");
	}
	public void genNamespaceMap(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
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
