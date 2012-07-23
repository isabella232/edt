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
package org.eclipse.edt.gen.javascript.templates;

import java.util.List;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class EGLClassTemplate extends JavaScriptTemplate {

	private static final String INITIALIZER_FUNCTION = "eze$$setInitial";
	private static final String DEFAULTS_FUNCTION = "eze$$setEmpty";

	public void preGenClassBody(EGLClass part, Context ctx) {
		ctx.invoke(preGenUsedParts, part, ctx);
		ctx.invoke(preGenFields, part, ctx);
		ctx.invoke(preGenFunctions, part, ctx);
		if (org.eclipse.edt.gen.CommonUtilities.getAnnotation(part, Constants.AnnotationXMLRootElement, ctx) == null) {
			// add an xmlRootElement
			try {
				Annotation annotation = org.eclipse.edt.gen.CommonUtilities.annotationNewInstance(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationXMLRootElement);
				annotation.setValue("name", part.getId());
				org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(part, annotation, ctx);
			}
			catch (Exception e) {}
		}
		addNamespaceMap(part, ctx);		
	}

	private void addNamespaceMap(EGLClass part, Context ctx) {
		String localName = part.getName();
		String namespace = CommonUtilities.createNamespaceFromPackage(part);
		
		Annotation annot = org.eclipse.edt.gen.CommonUtilities.getAnnotation(part, Constants.AnnotationXMLRootElement, ctx);
		if (annot != null) {
			if (annot.getValue("namespace") != null && ((String) annot.getValue("namespace")).length() > 0) {
				namespace = (String) annot.getValue("namespace");
			}
			if (annot.getValue("name") != null && ((String) annot.getValue("name")).length() > 0) {
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
	
	public void genPart(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genAMDHeader, part, ctx, out);
		ctx.invokeSuper(this, genPart, part, ctx, out);
		out.println("});");
	}
	
	public void genModuleName(EGLClass part, StringBuilder buf) {
		buf.append("\"");
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			buf.append(JavaScriptAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		buf.append(JavaScriptAliaser.getAlias(part.getId()));
		buf.append("\"");
	}
	
	public void genCSSFile(EGLClass part, TabbedWriter out){
		
	}
	
	private String getIncludeFileName(EGLClass part) {
		Annotation a = null;
		if(part instanceof Handler)
			a = part.getAnnotation( CommonUtilities.isRUIHandler( part ) ? Constants.RUI_HANDLER : Constants.RUI_WIDGET);
		else if(part instanceof ExternalType){
			a = part.getAnnotation( "eglx.javascript.JavaScriptObject" );
		}
		if ( a != null ){
			String fileName = (String)a.getValue( "includeFile" );
			return fileName;
		}
		return null;
	}
	
	public void genIncludeFile(EGLClass part, TabbedWriter out){
		String fileName = getIncludeFileName(part);
		if ( fileName != null && fileName.length() > 0 ){
			out.println("egl.loadFile(\"" + fileName + "\",\"" +  part.getFullyQualifiedName() + "\");");
		}
	}	
	
	public void genIncludeFileDependent(EGLClass part, TabbedWriter out){
		String fileName = getIncludeFileName(part);
		if ( fileName != null && fileName.length() > 0  && (fileName.endsWith(".html") || fileName.endsWith(".htm"))){
			out.print(".concat(egl.eze$$externalJS[\"" + part.getFullyQualifiedName() + "\"])");
		}
	}
	
	public void genAMDHeader(EGLClass part, Context ctx, TabbedWriter out) throws NoSuchMethodException {
		ctx.invoke(genIncludeFile, part, out);
		out.print("define([");
		StringBuilder buf = new StringBuilder();
		ctx.invoke(genDependent, part, ctx, out, buf);
		if( buf.length() > 0){
			String bufStr = buf.toString().substring(0, buf.length() - 2);
			out.print(bufStr);
		}
		out.print("]");
		ctx.invoke(genIncludeFileDependent, part, out);
		out.println(",function(){");
		ctx.invoke(genCSSFile, part, out);		
	}
	
	public void genDependent(EGLClass part, Context ctx, TabbedWriter out, StringBuilder buf) throws NoSuchMethodException {
		for (Part refPart: IRUtils.getReferencedPartsFor(part)) {
			if ( CommonUtilities.canBeGeneratedToJavaScript(refPart) && CommonUtilities.isNativeType(refPart)){
				ctx.invoke(genModuleName, refPart, buf);
				buf.append(", ");							
			}
		}		
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
		out.print(quoted("eze$$fileName"));
		out.print(" : ");
		out.print(quoted(part.getFileName()));
		out.println(", ");
	}

	public void genClassBody(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genConstructors, part, ctx, out);
		ctx.invoke(genSetEmptyMethods, part, ctx, out);
		ctx.invoke(genInitializeMethods, part, ctx, out);
		ctx.invoke(genCloneMethods, part, ctx, out);
		// genGetFieldSignaturesMethod(part, ctx, out, args);
		ctx.invoke(genAnnotations, part, ctx, out);
		ctx.invoke(genFieldAnnotations, part, ctx, out);
		ctx.invoke(genFunctions, part, ctx, out);
		ctx.invoke(genGetterSetters, part, ctx, out);
		ctx.invoke(genToString, part, ctx, out);
	}

	public void genToString(EGLClass part, Context ctx, TabbedWriter out) {
		out.println(",");
		out.print(quoted("toString"));
		out.println(": function() {");
		out.println("return \"[" + part.getId() + "]\";");
		out.println("}");
	}

	public void genFields(EGLClass part, Context ctx, TabbedWriter out) {
		for (Field field : part.getFields()) {
			ctx.invoke(genField, part, ctx, out, field);
		}
	}

	public void genField(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		if (arg instanceof ConstantField) {
			ctx.invoke(genDeclaration, arg, ctx, out);
		}
	}

	public void genConstructors(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genConstructor, part, ctx, out);
	}

	public void genConstructor(EGLClass part, Context ctx, TabbedWriter out) {
		// Generate default constructor
		out.print(quoted("constructor"));
		out.println(": function() {");
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
		out.println(",");
		out.print(quoted(DEFAULTS_FUNCTION));
		out.println(": function() {");
		ctx.invoke(genSetEmptyMethodBody, part, ctx, out);
		out.println("}");
	}

	public void genSetEmptyMethodBody(EGLClass part, Context ctx, TabbedWriter out) {
		ctx.invoke(genLibraries, part, ctx, out);
		ctx.invoke(genFields, part, ctx, out);
		for (Field field : part.getFields()) {
			if (!(field instanceof ConstantField)) {
				ctx.invoke(genSetEmptyMethod, part, ctx, out, field);
			}
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
		out.println(",");
		out.print(quoted(INITIALIZER_FUNCTION));
		out.println(": function() {");
		ctx.invoke(genInitializeMethodBody, part, ctx, out);
		out.println("}");
	}

	public void genInitializeMethodBody(EGLClass part, Context ctx, TabbedWriter out) {
		out.println("this."+DEFAULTS_FUNCTION + "();");
		
		for (Field field : part.getFields()) {
			// Need to generate annotations before the initializers, in case the annotation offers a default value
			// that gets overridden by the initializers.
			for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(field, ctx)) {
				try {
					ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field, genInitializeMethod);
				}
				catch (TemplateException ex) {
					//NOGO sbg Seems bogus, but apparently we lack templates for some annotations?
				}
			}
			ctx.invoke(genInitializeMethod, part, ctx, out, field);
		}

		/*
		 * If there are part-level initializer statements, process them AFTER the field-level initializers so that they take
		 * precedence. TODO confirm this with Tim et al.
		 */
		if (part.getInitializerStatements() != null) {
			ctx.invoke(genStatementNoBraces, part.getInitializerStatements(), ctx, out);
		}
	}
	
	public void genInitializeMethod(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		if (!(arg instanceof ConstantField)) {
			ctx.invoke(genInitializeStatement, arg.getType(), ctx, out, arg);
		}
	}

	public void genInitializerStatements(Field field, Context ctx, TabbedWriter out) {
		ctx.invoke(genStatementNoBraces, field.getInitializerStatements(), ctx, out);
	}

	public void genCloneMethods(EGLClass part, Context ctx, TabbedWriter out) {
		out.println(",");
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

		// clone fields
		for (Field field : part.getFields()) {
			ctx.invoke(genCloneMethod, part, ctx, out, field);
		}

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
		if (arg.isNullable() || TypeUtils.isReferenceType(arg.getType())) {
			out.print(" === null ? null : ");
			out.print(temp1);
			out.print(".");
			ctx.invoke(genName, arg, ctx, out);
			ctx.invoke(genCloneMethod, arg.getType(), ctx, out);
		}
		out.println(";");
	}

	public void genGetterSetters(EGLClass part, Context ctx, TabbedWriter out) {
		for (Field field : part.getFields()) {
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

	public void genAnnotations(EGLClass part, Context ctx, TabbedWriter out) {
		out.println(",");
		out.print(quoted("eze$$getAnnotations"));
		out.println(": function() {");
		out.println("if(this.annotations === undefined){");
		out.println("this.annotations = {};");
		for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(part, ctx)) {
			ctx.invoke(genConversionControlAnnotation, annot.getEClass(), ctx, out, annot, part);
		}
		out.println("}");
		out.println("return this.annotations;");
		out.println("}");
	}

	public void genFieldAnnotations(EGLClass part, Context ctx, TabbedWriter out) {
		out.println(",");
		out.print(quoted("eze$$getFieldInfos"));
		out.println(": function() {");
		out.println("if(this.fieldInfos === undefined){");
		out.println("var annotations;");
		out.println("this.fieldInfos = new Array();");
		int idx = 0;
		for (Field field : part.getFields()) {
			if (field instanceof ConstantField || field.isStatic()) {
				continue;
			}
			ctx.invoke(genAnnotations, field, ctx, out, Integer.valueOf(idx));
			idx++;
		}
		out.println("}");
		out.println("return this.fieldInfos;");
		out.println("}");
	}

	public void genQualifier(EGLClass part, Context ctx, TabbedWriter out, NamedElement arg) {
		for (Member mbr : part.getAllMembers()) {
			if (mbr.getId().equalsIgnoreCase(arg.getId())) {
				Object alias = ctx.getAttribute(ctx.getClass(), Constants.QUALIFIER_ALIAS);
				if (alias != null) 
					out.print(alias.toString());
				else
					out.print("this.");
				
				break;
			}
		}
	}
}
