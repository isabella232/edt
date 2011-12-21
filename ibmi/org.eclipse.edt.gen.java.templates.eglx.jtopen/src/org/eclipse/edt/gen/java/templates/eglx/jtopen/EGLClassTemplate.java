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
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;


public class EGLClassTemplate extends org.eclipse.edt.gen.java.templates.EGLClassTemplate implements Constants {

	public void genArrayResize(EGLClass part, Context ctx, TabbedWriter out, FunctionParameter parameter, Container container){
		if(!ctx.mapsToPrimitiveType(part)){
			MemberAccess ma = factory.createMemberAccess();
			ma.setId(parameter.getId());
			processFields(part, ctx, out, ma);
		}
	}
	public void genArrayResize(EGLClass part, Context ctx, TabbedWriter out, MemberAccess array, Container container){
		if(!ctx.mapsToPrimitiveType(part)){
			MemberAccess ma = factory.createMemberAccess();
			ma.setQualifier(array);
			processFields(part, ctx, out, ma);
		}
	}
	public void genArrayResize(EGLClass part, Context ctx, TabbedWriter out, ArrayAccess array, Container container){
		if(!ctx.mapsToPrimitiveType(part)){
			MemberAccess ma = factory.createMemberAccess();
			ma.setQualifier(array);
			processFields(part, ctx, out, ma);
		}
	}
	private void processFields(EGLClass part, Context ctx, TabbedWriter out, MemberAccess ma) {
		for(Member field : part.getMembers()){
			//build new container expression
			ma.setMember(field);
			ma.setId(field.getId());
			ctx.invoke(genArrayResize, field.getType(), ctx, out, ma, part);
		}
	}
	public void preGenAS400Annotation(EGLClass type, Context ctx, Member member){
	}
	public Annotation getAS400FunctionParameterAnnotation(EGLClass type, Context ctx){
		return null;
	}
	public void genArrayResize(EGLClass type, Context ctx, TabbedWriter out, Element member, Container container){
	}
}
