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

import java.io.StringWriter;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;


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
	
	public void genArrayResize(ArrayType type, Context ctx, TabbedWriter out, FunctionParameter array, Function function) throws MofObjectNotFoundException, DeserializationException{
		Annotation as400Array = array.getAnnotation(signature_AS400Array);
		MemberName arrayMember = factory.createMemberName();
		arrayMember.setId(array.getId());
		arrayMember.setMember(array);
		if(as400Array != null && as400Array.getValue(subKey_validElementCountVariable) != null){
			String validElementCountVariable = (String)as400Array.getValue(subKey_validElementCountVariable);
			Member cntMember = getMember(validElementCountVariable, function);
			MemberName countVariable = factory.createMemberName();
			countVariable.setId(validElementCountVariable);
			countVariable.setMember(cntMember);
			genArrayResize(countVariable, ctx, out, arrayMember, function);
		}
		genArrayResizeElements(type, ctx, out, arrayMember, function);
	}
	public void genArrayResize(ArrayType type, Context ctx, TabbedWriter out, MemberAccess array, Container container) throws MofObjectNotFoundException, DeserializationException{
		Annotation as400Array = array.getMember().getAnnotation(signature_AS400Array);
		if(as400Array != null && as400Array.getValue(subKey_validElementCountVariable) != null){
			String validElementCountVariable = (String)as400Array.getValue(subKey_validElementCountVariable);
			Member cntMember = getMember(validElementCountVariable, container);
			MemberAccess countVariable = factory.createMemberAccess();
			countVariable.setQualifier(array.getQualifier());
			countVariable.setId(validElementCountVariable);
			countVariable.setMember(cntMember);
			genArrayResize(countVariable, ctx, out, array, container);
		}
		genArrayResizeElements(type, ctx, out, array, container);
	}

	private Member getMember(String validElementCountVariable,
			Container container) {
		Member cntMember = null;
		for(Member member : container.getMembers()){
			if(member.getId().equalsIgnoreCase(validElementCountVariable)){
				cntMember = member;
				break;
			}
		}
		return cntMember;
	}
	private void genArrayResizeElements(ArrayType type, Context ctx, TabbedWriter out, Name array, Container container) throws MofObjectNotFoundException, DeserializationException{
		//FIXME what about multi dim arrays there is no elementCount for this case
		String tempName = ctx.nextTempName();
		StringWriter sw = new StringWriter();
		TabbedWriter writer = new TabbedWriter(sw);
		ArrayAccess arrayAccess = factory.createArrayAccess();
		arrayAccess.setArray(array);
		Field f = factory.createField();
		Type idxType = (Type)Environment.getCurrentEnv().find((MofConversion.Type_EGLInt));
		f.setType(idxType);
		f.setName(tempName);
		MemberName idx = factory.createMemberName();
		idx.setId(tempName);
		idx.setMember(f);
		arrayAccess.setIndex(idx);
		ctx.invoke(genArrayResize, type.getElementType(), ctx, writer, arrayAccess, container);
		if(sw.getBuffer().length() > 0){
			out.print("for(int ");
			out.print(tempName);
			out.print("= 1; ");
			out.print(tempName);
			out.print(" <= ");
			genArrayGetSize(ctx, out, array, container);
			out.print("; ");
			out.print(tempName);
			out.println("++)");
			out.println("{");
			out.println(sw.getBuffer().toString());
			out.println("}");
		}
	}
	private void genArrayGetSize(Context ctx, TabbedWriter out, Name array, Container container) throws MofObjectNotFoundException, DeserializationException{
		QualifiedFunctionInvocation inv = factory.createQualifiedFunctionInvocation();
		inv.setId(IEGLConstants.SYSTEM_WORD_GETSIZE);
		inv.setQualifier(array);
		ctx.invoke(genExpression, inv, ctx, out);
	}
	private void genArrayResize(Name validElementCountVariable, Context ctx, TabbedWriter out, Name array, Container container) throws MofObjectNotFoundException, DeserializationException{
		QualifiedFunctionInvocation inv = factory.createQualifiedFunctionInvocation();
		inv.setId(IEGLConstants.SYSTEM_WORD_RESIZE);
		Type idxType = (Type)Environment.getCurrentEnv().find((MofConversion.Type_EGLInt));
		AsExpression cnt = IRUtils.createAsExpression(validElementCountVariable, idxType);
		inv.getArguments().add(cnt);
		inv.setQualifier(array);
		FunctionStatement stmt = factory.createFunctionStatement();
		stmt.setExpr(inv);
		stmt.setContainer(container);
		ctx.invoke(genStatement, stmt, ctx, out);
	}
}