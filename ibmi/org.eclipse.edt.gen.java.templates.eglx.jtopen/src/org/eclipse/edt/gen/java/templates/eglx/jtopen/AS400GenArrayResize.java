/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import java.io.StringWriter;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.Environment;

public class AS400GenArrayResize {
	static AS400GenArrayResize INSTANCE = new AS400GenArrayResize();
	public void genArrayResizeParameter(FunctionParameter parameter, Context ctx, TabbedWriter out, Function functionContainer){
		if(parameter.getType() instanceof ArrayType){
			genResizeMember(getCountMember(parameter, ctx),
					createMemberName(parameter), (ArrayType)parameter.getType(),
					ctx, out, functionContainer, functionContainer);
		}
		else if(parameter.getType() instanceof LogicAndDataPart){
			processFields((LogicAndDataPart)parameter.getType(), ctx, out, createMemberName(parameter), functionContainer);
		}
	}

	private void genResizeMember(Expression cntMember, LHSExpr array, ArrayType arrayType, Context ctx, TabbedWriter out, Container countContainer, Function functionContainer) {
		if(cntMember != null){
			genArrayResizeExpression(cntMember, ctx, out, array, functionContainer);
		}
		genArrayResizeElements(arrayType, ctx, out, array, functionContainer);
	}
	
	private MemberAccess createMemberAccess(Member member, LHSExpr qual){
		MemberAccess ma = JavaTemplate.factory.createMemberAccess();
		ma.setId(member.getId());
		ma.setMember(member);
		ma.setQualifier(qual);
		return ma;
	}
	
	private MemberName createMemberName(Member member){
		MemberName mn = JavaTemplate.factory.createMemberName();
		mn.setId(member.getId());
		mn.setMember(member);
		return mn;
	}
	
	private MemberName getCountMember(Member member, Context ctx) {
		Annotation as400Array = org.eclipse.edt.gen.java.templates.eglx.jtopen.CommonUtilities.getAnnotation(member, Constants.signature_AS400Array, ctx);
		return as400Array == null ? null : (MemberName)as400Array.getValue(Constants.subKey_validElementCountVariable);
	}
	
	private void genArrayResizeElements(ArrayType type, Context ctx, TabbedWriter out, LHSExpr array, Function functionContainer) {
		//FIXME what about multi dim arrays there is no elementCount for this case
		String tempName = ctx.nextTempName();
		StringWriter sw = new StringWriter();
		TabbedWriter writer = new TabbedWriter(sw);
		ArrayAccess arrayAccess = JavaTemplate.factory.createArrayAccess();
		arrayAccess.setArray(array);
		Field f = JavaTemplate.factory.createField();
		Type idxType = null;
		try {
			idxType = (Type)Environment.getCurrentEnv().find((MofConversion.Type_EGLInt));
		} catch (Exception e) {}
		f.setType(idxType);
		f.setName(tempName);
		MemberName idx = JavaTemplate.factory.createMemberName();
		idx.setId(tempName);
		idx.setMember(f);
		arrayAccess.setIndex(idx);
		processElements(type.getElementType(), ctx, writer, arrayAccess, functionContainer);
		if(sw.getBuffer().length() > 0){
			out.print("for(int ");
			out.print(tempName);
			out.print("= 1; ");
			out.print(tempName);
			out.print(" <= ");
			genArrayGetSize(ctx, out, array, functionContainer);
			out.print("; ");
			out.print(tempName);
			out.println("++)");
			out.println("{");
			out.println(sw.getBuffer().toString());
			out.println("}");
		}
	}
	private void processElements(Type type, Context ctx, TabbedWriter out, ArrayAccess arrayElement, Function functionContainer) {
//		if(type instanceof ArrayType){
//			genResizeMember(createMemberAccess(getCountMember(org.eclipse.edt.gen.java.templates.eglx.jtopen.CommonUtilities.getAnnotation(field, Constants.signature_AS400Array, ctx),
//												part), qual),
//					createMemberAccess(field, qual), ctx, out, part, functionContainer);
//		}
		/*else*/ if(type instanceof LogicAndDataPart){
			processFields((LogicAndDataPart)type, ctx, out, arrayElement, functionContainer);
		}
	}
	private void genArrayGetSize(Context ctx, TabbedWriter out, LHSExpr array, Function container){
		QualifiedFunctionInvocation inv = JavaTemplate.factory.createQualifiedFunctionInvocation();
		inv.setId(IEGLConstants.SYSTEM_WORD_GETSIZE);
		inv.setQualifier(array);
		ctx.invoke(JavaTemplate.genExpression, inv, ctx, out);
	}
	private void genArrayResizeExpression(Expression validElementCountVariable, Context ctx, TabbedWriter out, Expression array, Container container){
		QualifiedFunctionInvocation inv = JavaTemplate.factory.createQualifiedFunctionInvocation();
		inv.setId(IEGLConstants.SYSTEM_WORD_RESIZE);
		Type idxType = null;
		try {
			idxType = (Type)Environment.getCurrentEnv().find((MofConversion.Type_EGLInt));
		} catch (Exception e){}
		AsExpression cnt = IRUtils.createAsExpression(validElementCountVariable, idxType);
		inv.getArguments().add(cnt);
		inv.setQualifier(array);
		FunctionStatement stmt = JavaTemplate.factory.createFunctionStatement();
		stmt.setExpr(inv);
		stmt.setContainer(container);
		ctx.invoke(JavaTemplate.genStatement, stmt, ctx, out);
	}

	private void processFields(LogicAndDataPart part, Context ctx, TabbedWriter out, LHSExpr qual, Function functionContainer) {
		for(Field field : part.getFields()){
			//build new container expression
			if(field.getType() instanceof ArrayType){
				genResizeMember(getCountMember(field, ctx),
						createMemberAccess(field, qual), 
						(ArrayType)field.getType(),
						ctx, out, part, functionContainer);
			}
			else if(field.getType() instanceof LogicAndDataPart){
				processFields((LogicAndDataPart)field.getType(), ctx, out, createMemberAccess(field, qual), functionContainer);
			}
		}
	}


}
