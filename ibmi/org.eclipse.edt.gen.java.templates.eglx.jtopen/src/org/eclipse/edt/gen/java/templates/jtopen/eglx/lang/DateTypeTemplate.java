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
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;


public class DateTypeTemplate extends org.eclipse.edt.gen.java.templates.eglx.lang.DateTypeTemplate  implements Constants{
	public void preGenAS400Annotation(Type type, Context ctx, Member member){
		if (member.getAnnotation(Constants.signature_AS400Date) == null) {
			try {
				Annotation annot = getAS400FunctionParameterAnnotation(type, ctx);
				if(annot != null){
					member.addAnnotation(annot);
				}
			}
			catch (Exception e) {}
		}	
	}
	public Annotation getAS400FunctionParameterAnnotation(Type type, Context ctx){
		try {
			return CommonUtilities.getAnnotation(ctx, Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.signature_AS400Date);
		}
		catch (Exception e) {
			return null;
		}
	}
}