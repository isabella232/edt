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
package org.eclipse.edt.gen.java.templates.jtopen.eglx.lang;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.eglx.jtopen.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class DecimalTypeTemplate extends org.eclipse.edt.gen.java.templates.eglx.lang.DecimalTypeTemplate implements Constants{
	
	public void genDecimals(FixedPrecisionType type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		out.print(type.getDecimals());
	}
	public void genDecimals(Type type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		out.print(((Integer)typeAnnot.getValue(subKey_decimals)).toString());
	}
	
	public void genLength(FixedPrecisionType type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		out.print(type.getLength());
	}
	public void genLength(Type type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		out.print(((Integer)typeAnnot.getValue(subKey_length)).toString());
	}
	public void preGenAS400Annotation(FixedPrecisionType type, Context ctx, Member member){
		if (member.getAnnotation(Constants.signature_AS400DecimalPacked) == null &&
				member.getAnnotation(Constants.signature_AS400DecimalFloat) == null &&
				member.getAnnotation(Constants.signature_AS400DecimalZoned) == null) {
			try {
				Annotation annot = getAS400FunctionParameterAnnotation(type, ctx);
				if(annot != null){
					member.addAnnotation(annot);
				}
			}
			catch (Exception e) {}
		}
	}
	public Annotation getAS400FunctionParameterAnnotation(FixedPrecisionType type, Context ctx){
		try {
			Annotation annotation = CommonUtilities.getAnnotation(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.signature_AS400DecimalPacked);
			annotation.setValue(subKey_decimals, type.getDecimals());
			annotation.setValue(subKey_length, type.getLength());
			return annotation;
		}
		catch (Exception e) {
			return null;
		}
	}
}