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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FieldTemplate extends JavaScriptTemplate {

	public void preGen(Field field, Context ctx) {
		ctx.invoke(preGen, field.getType(), ctx);
		if (org.eclipse.edt.gen.CommonUtilities.getAnnotation(field, Constants.AnnotationXmlElement, ctx) == null &&
						org.eclipse.edt.gen.CommonUtilities.getAnnotation(field, Constants.AnnotationXmlAttribute, ctx) == null) {
			try {
				Annotation annotation = org.eclipse.edt.gen.CommonUtilities.annotationNewInstance(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationXmlElement);
				annotation.setValue("name", field.getCaseSensitiveName());
				org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(field, annotation, ctx);
			} catch (Exception e) {}
		}

		if(org.eclipse.edt.gen.CommonUtilities.getAnnotation(field, Constants.AnnotationJsonName, ctx) == null) {
			//add an xmlElement
			try {
				Annotation annotation = org.eclipse.edt.gen.CommonUtilities.annotationNewInstance(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationJsonName);
				annotation.setValue(field.getCaseSensitiveName());
				org.eclipse.edt.gen.CommonUtilities.addGeneratorAnnotation(field, annotation, ctx);
			} catch (Exception e) {}
		}	
		
		for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(field, ctx)) {
			ctx.invoke(preGen, annot.getEClass(), ctx, annot, field);
		}
	}

	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {
		out.print("var ");
		ctx.invoke(genName, field, ctx, out);
		out.print(" = ");
		ctx.invoke(genInitialization, field, ctx, out);
		out.println(";");
	}

	public void genQualifier(Field field, Context ctx, TabbedWriter out) {
		/*
		 * TODO sbg Ensure that genQualifier is needed for something (e.g., generating "this" in handlers or widgets) --
		 * otherwise, remove it altogether.
		 */
		if (field.getContainer() != null && field.getContainer() instanceof Type)
			ctx.invoke(genQualifier, field.getContainer(), ctx, out, field);
	}

	public void genInstantiation(Field field, Context ctx, TabbedWriter out) {
		ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
	}

	public void genInitialization(Field field, Context ctx, TabbedWriter out) {
		// is this an inout or out temporary variable to a function. if so, then we need to default or instantiate for
		// our parms, and set to null for inout
		if (ctx.getAttribute(field, org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != null
			&& ctx.getAttribute(field, org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN) {
			// if the value associated with the temporary variable is 2, then it is to be instantiated (OUT parm)
			// otherwise it is to be defaulted to null (INOUT parm), as there is an assignment already created
			if (ctx.getAttribute(field, org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) == ParameterKind.PARM_OUT) {
				out.print(Constants.JSRT_EGL_NAMESPACE + ctx.getNativeMapping("eglx.lang.EAny") + ".ezeWrap(");
				if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
					ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
				else
					ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
				out.print(")");
			} else
				out.print("null");
		} else {
			if (field.isNullable() || TypeUtils.isReferenceType(field.getType()))
				ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
			else if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
				ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
			else
				ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
		}
	}

	public void genGetter(Field field, Context ctx, TabbedWriter out) {
		out.println(",");
		out.print(quoted(genGetterSetterFunctionName("get", field)));
		out.println(": function() {");
		out.print("return ");
		ctx.invoke(genName, field, ctx, out);
		out.println(";");
		out.println("}");
	}

	protected static String genGetterSetterFunctionName(String prefix, Field field) {
		StringBuilder name = new StringBuilder(prefix);
		name.append(field.getCaseSensitiveName().substring(0, 1).toUpperCase());
		if (field.getCaseSensitiveName().length() > 1)
			name.append(field.getCaseSensitiveName().substring(1));
		return name.toString();
	}

	public void genSetter(Field field, Context ctx, TabbedWriter out) {
		out.println(",");
		out.print(quoted(genGetterSetterFunctionName("set", field)));
		out.println(": function(ezeValue) {");
		out.print("this.");
		ctx.invoke(genName, field, ctx, out);
		out.println(" = ezeValue;");
		out.println("}");
	}

	public void genAnnotations(Field field, Context ctx, TabbedWriter out, Integer arg) {
		Boolean supportConversion = (Boolean)ctx.invoke(supportsConversion, field.getType(), ctx);
		if((field.getAccessKind() == null || field.getAccessKind().equals(AccessKind.ACC_PUBLIC)) &&
				(supportConversion == null || supportConversion.booleanValue())){
			out.println("annotations = {};");
			
			for(Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(field, ctx)){
				ctx.invoke(genConversionControlAnnotation, annot.getEClass(), ctx, out, annot, field);
			}
			
			out.print("this.fieldInfos[" + arg.toString() + "] =");
			out.print("new egl.eglx.services.FieldInfo(");
			Annotation property = CommonUtilities.getPropertyAnnotation(field);
			if (property != null) {
				out.print(FieldTemplate.genGetterSetterFunctionName("get", field));
				out.print(", ");
				out.print(FieldTemplate.genGetterSetterFunctionName("set", field));
				out.print(", \"");
			} else {
				out.print("\"");
				ctx.invoke(genName, field, ctx, out);
				out.print("\", ");
				out.print("\"");
				ctx.invoke(genName, field, ctx, out);
				out.print("\", \"");
			}
			if(field.isNullable()){
				out.print("?");
			}
			ctx.invoke(genSignature, field.getType(), ctx, out);
			out.print("\", ");
			ctx.invoke(genFieldInfoTypeName, field.getType(), ctx, out, TypeNameKind.JavascriptImplementation);
			out.println(", annotations);");
		}
	}

}
