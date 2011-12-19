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
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;


public class ArrayTypeTemplate extends org.eclipse.edt.gen.java.templates.ArrayTypeTemplate implements Constants{
	public void genDecimals(ArrayType type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		ctx.invoke(genDecimals, getRootType(type), ctx, out, typeAnnot);
	}
	
	public void genLength(ArrayType type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		ctx.invoke(genLength, getRootType(type), ctx, out, typeAnnot);
	}
	
	public void genPattern(ArrayType type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		ctx.invoke(genPattern, getRootType(type), ctx, out, typeAnnot);
	}
	
	public void preGenAS400Annotation(ArrayType type, Context ctx, Field field){
		Annotation annot = field.getAnnotation(signature_AS400Array);
		if (annot != null){
			if(annot.getValue(subKey_elementTypeAS400Annotation) == null) {
				try {
					annot.setValue(subKey_elementTypeAS400Annotation, getAS400FunctionParameterAnnotation(type, ctx));
				}
				catch (Exception e) {}
			}
		}
	}
	
	private Type getRootType(ArrayType type){
		while(((ArrayType)type).getElementType() instanceof ArrayType){
			type = (ArrayType)((ArrayType)type).getElementType();
		}
		return ((ArrayType)type).getElementType();
	}
	public Annotation getAS400FunctionParameterAnnotation(ArrayType type, Context ctx){
		return (Annotation)ctx.invoke(getAS400FunctionParameterAnnotation, getRootType(type), ctx);
	}
}