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
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;


public class TimestampTypeTemplate extends org.eclipse.edt.gen.java.templates.eglx.lang.TimestampTypeTemplate implements Constants {
	public void genPattern(TimestampType type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		out.print(pattern(type));
	}
	public void genPattern(Type type, Context ctx, TabbedWriter out, Annotation typeAnnot) {
		String eglPattern = typeAnnot == null ? null : (String)typeAnnot.getValue(subKey_eglPattern);
		if(eglPattern == null){
			eglPattern = "*yyyyMMddhhmmss";
		}
		out.print(eglPattern);
	}
	public void preGenAS400Annotation(TimestampType type, Context ctx, Member member){
		if (member.getAnnotation(Constants.signature_AS400Timestamp) == null) {
			try {
				Annotation annot = getAS400FunctionParameterAnnotation(type, ctx);
				if(annot != null){
					member.addAnnotation(annot);
				}
			}
			catch (Exception e) {}
		}	
	}
	private String pattern(TimestampType type){
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		return pattern;
		
	}
	public Annotation getAS400FunctionParameterAnnotation(TimestampType type, Context ctx){
		try {
			Annotation annotation = CommonUtilities.getAnnotation(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.signature_AS400Timestamp);
			annotation.setValue(subKey_eglPattern, pattern(type));
			return annotation;
		}
		catch (Exception e) {
			return null;
		}
	}
}