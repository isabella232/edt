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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class SequenceTypeTemplate extends JavaTemplate {

	public void genTypeDependentOptions(SequenceType type, Context ctx, TabbedWriter out) {
		out.print(", ");
		out.print(type.getLength());
	}

	public void genSubstringAccess(SequenceType type, Context ctx, TabbedWriter out, SubstringAccess arg) {
		out.print(ctx.getNativeImplementationMapping(arg.getType()) + ".substring(");
		ctx.invoke(genExpression, arg.getStringExpression(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(arg.getStart(), TypeUtils.Type_INT), ctx, out, arg.getStart());
		out.print(", ");
		ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(arg.getEnd(), TypeUtils.Type_INT), ctx, out, arg.getEnd());
		out.print(")");
	}
	
	public void genTypeBasedAssignment(Type type, Context ctx, TabbedWriter out, Assignment asn) {
		// When assigning a bytes(x) to a bytes(y), and x < y, we need to generate
		// something special for the RHS.
		LHSExpr lhs = asn.getLHS();
		Expression rhs = asn.getRHS();
		Type lhsType = lhs.getType();
		Type rhsType = rhs.getType();
		if (rhsType.getClassifier().equals(lhsType.getClassifier())
				&& !(asn.getRHS() instanceof AsExpression)
				&& rhsType instanceof SequenceType
				&& lhsType instanceof SequenceType
				&& ((SequenceType)rhsType).getLength() < ((SequenceType)lhsType).getLength()
				&& TypeUtils.getTypeKind(rhsType) == TypeUtils.TypeKind_BYTES
				&& TypeUtils.getTypeKind(lhsType) == TypeUtils.TypeKind_BYTES)
		{
			if (!lhs.isNullable() && rhs.isNullable()) 
			{
				// if this is a well-behaved assignment, we can avoid the temporary
				if (org.eclipse.edt.gen.CommonUtilities.hasSideEffects(rhs, ctx)) 
				{
					String temporary = ctx.nextTempName();
					ctx.invoke(genRuntimeTypeName, lhsType, ctx, out, TypeNameKind.JavaObject);
					out.print(" " + temporary + " = ");
					genBytesRHS(rhs, rhsType, lhs, lhsType, ctx, out);
					out.println(";");
					ctx.invoke(genExpression, lhs, ctx, out);
					out.print(" = ");
					if (!(rhs instanceof BoxingExpression)) 
					{
						out.print("(");
						ctx.invoke(genRuntimeTypeName, rhsType, ctx, out, TypeNameKind.JavaObject);
						ctx.invoke(genRuntimeTypeExtension, rhsType, ctx, out);
						out.print(") ");
					}
					out.print("org.eclipse.edt.javart.util.JavartUtil.checkNullable(" + temporary + ")");
				} 
				else 
				{
					ctx.invoke(genExpression, lhs, ctx, out);
					out.print(" = ");
					if (!(rhs instanceof BoxingExpression)) 
					{
						out.print("(");
						ctx.invoke(genRuntimeTypeName, rhsType, ctx, out, TypeNameKind.JavaObject);
						ctx.invoke(genRuntimeTypeExtension, rhsType, ctx, out);
						out.print(") ");
					}
					out.print("org.eclipse.edt.javart.util.JavartUtil.checkNullable(");
					genBytesRHS(rhs, rhsType, lhs, lhsType, ctx, out);
					out.print(")");
				}
			}
			else
			{
				ctx.invoke(genExpression, lhs, ctx, out);
				out.print(" = ");
				genBytesRHS(rhs, rhsType, lhs, lhsType, ctx, out);
				// check to see if we are unboxing RHS temporary variables (inout and out types only)
				if (CommonUtilities.isBoxedOutputTemp(rhs, ctx))
				{
					out.print(".ezeUnbox()");
				}
			}
		}
		else
		{
			ctx.invokeSuper(this, genTypeBasedAssignment, type, ctx, out, asn);
		}
	}
	
	private void genBytesRHS(Expression rhs, Type rhsType, LHSExpr lhs, Type lhsType, Context ctx, TabbedWriter out)
	{
		ctx.invoke(genRuntimeTypeName, rhsType, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".ezeAssignToLonger(");
		ctx.invoke(genExpression, lhs, ctx, out);
		out.print(", ");
		out.print(((SequenceType)lhsType).getLength());
		out.print(", ");
		ctx.invoke(genExpression, rhs, ctx, out);
		out.print(')');
	}
}
