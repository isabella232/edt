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
package org.eclipse.edt.gen.java.templates;



import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FieldTemplate extends JavaTemplate {

	public void preGen(Field field, Context ctx) {
		ctx.invoke(preGen, field.getType(), ctx);
		if(field.getContainer() instanceof Type){
			if(field.getAnnotation(Constants.AnnotationJsonName) == null) {
				//add an xmlElement
				try {
					Annotation annotation = CommonUtilities.getAnnotation(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.AnnotationJsonName);
					annotation.setValue(field.getId());
					field.addAnnotation(annotation);
				} catch (Exception e) {}
			}	
		}
	}

	public void genDeclaration(Field field, Context ctx, TabbedWriter out) {
		// write out the debug extension data
		CommonUtilities.generateSmapExtension(field, ctx);
		// process the field
		if(field.getContainer() != null){
			ctx.invoke(genXmlTransient, field.getContainer(), out);
			ctx.invoke(genAnnotations, field.getContainer(), ctx, out, field);
		}
		ctx.invokeSuper(this, genDeclaration, field, ctx, out);
		transientOption(field, out);
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" ");
		ctx.invoke(genName, field, ctx, out);
		out.println(";");
	}

	public void genAnnotations(Field field, Context ctx, TabbedWriter out) {
		for(Annotation annot : field.getAnnotations()){
			ctx.invoke(genAnnotation, annot.getEClass(), ctx, out, annot, field);
		}
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
				out.print("EglAny.ezeWrap(");
				if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
					ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
				else
					ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
				out.print(")");
			} else
				out.print("null");
		} else {
			if (field.isNullable())
				ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
			else if (TypeUtils.isReferenceType(field.getType())) {
				ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
			} else if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
				ctx.invoke(genDefaultValue, field.getType(), ctx, out, field);
			else
				ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
		}
	}

	public void genGetter(Field field, Context ctx, TabbedWriter out) {
		ctx.invoke(genAnnotations, field, ctx, out);
		out.print("public ");
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" get");
		genMethodName(field, ctx, out);
		out.println("() {");
		out.print("return (");
		ctx.invoke(genName, field, ctx, out);
		out.println(");");
		out.println("}");
	}

	public void genSetter(Field field, Context ctx, TabbedWriter out) {
		out.print("public void set");
		genMethodName(field, ctx, out);
		out.print("( ");
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
		out.println(" ezeValue ) {");
		out.print("this.");
		ctx.invoke(genName, field, ctx, out);
		out.println(" = ezeValue;");
		out.println("}");
	}

	protected void genMethodName(Field field, Context ctx, TabbedWriter out) {
		out.print(field.getName().substring(0, 1).toUpperCase());
		if (field.getName().length() > 1)
			out.print(field.getName().substring(1));
	}

	protected void transientOption(Field field, TabbedWriter out) {
		ExternalType et = CommonUtilities.getJavaExternalType(field.getType());
		if (et != null && !CommonUtilities.isSerializable(et))
			out.print("transient ");
	}
}
