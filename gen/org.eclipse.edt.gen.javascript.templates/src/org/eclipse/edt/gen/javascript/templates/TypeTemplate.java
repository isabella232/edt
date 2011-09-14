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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class TypeTemplate extends JavaScriptTemplate {

	public void preGen(Type type, Context ctx) {
		// types may override this validation for specific checking
	}

	public Boolean isAssignmentBreakupWanted(Type type, Context ctx, String arg) {
		// types can override this to cause an compound assignment expression to be broken up 
		// the arg contains the operation being asked about
		return false;
	}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavascriptImplementation);
		out.print("(");
		ctx.invoke(genConstructorOptions, type, ctx, out);
		out.print(")");
	}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genInstantiation, type, ctx, out);
	}

	public void genInvocation(Type type, Context ctx, TabbedWriter out, InvocationExpression arg) {
		ctx.invoke(genInvocation, arg, ctx, out);
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, TypedElement arg) {
		if (arg.isNullable())
			out.print("null");
		else
			ctx.invoke(genDefaultValue, type, ctx, out);
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, Expression arg) {
		if (arg.isNullable())
			out.print("null");
		else
			ctx.invoke(genDefaultValue, type, ctx, out);
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out) {
		if (TypeUtils.isReferenceType(type))
			ctx.invoke(genInstantiation, type, ctx, out); //out.print("null");
		else
			out.print("\"Invalid default value\"");
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavascriptPrimitive);
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		// are we looking for the default (java primitive) or specifically java primitive, if it exists
		if (arg == TypeNameKind.JavascriptPrimitive) {
			if (ctx.mapsToPrimitiveType(type.getClassifier())) {
				out.print(ctx.getPrimitiveMapping(type.getClassifier()));
				return;
			}
		}
		// are we looking for the java object
		if (arg == TypeNameKind.JavascriptObject) {
			if (ctx.mapsToPrimitiveType(type.getClassifier())) {
				String item = ctx.getPrimitiveMapping(type.getClassifier());
				if (ctx.getPrimitiveMapping(item) == null)
					out.print(item);
				else
					out.print(ctx.getPrimitiveMapping(item));
				return;
			}
		}
		// we couldn't resolve the java types, so we have to check for the java implementation name
		if (arg == TypeNameKind.JavascriptImplementation) {
			if (ctx.mapsToPrimitiveType(type.getClassifier())) {
				String item = ctx.getPrimitiveMapping(type.getClassifier());
				if (ctx.getPrimitiveMapping(item) == null)
					out.print(item);
				else
					out.print(ctx.getPrimitiveMapping(item));
				return;
			}
		}
		// type an egl implementation name
		if (arg == TypeNameKind.EGLImplementation) {
			if (ctx.mapsToNativeType(type.getClassifier())) {
				out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
				return;
			}
		}
		// select the proper default to use. we have run out of options
		if (arg == TypeNameKind.JavascriptImplementation)
			out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
		else
			// must be an egl interface name we want
			out.print(ctx.getNativeInterfaceMapping(type.getClassifier()));
	}

	public void genConstructorOptions(Type type, Context ctx, TabbedWriter out) {
		// no default
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		ctx.invoke(genTypeDependentOptions, type, ctx, out);
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out) {
		// no default
	}
	
	public void genInitializeStatement(Type type, Context ctx, TabbedWriter out, Field arg) {
		if (arg.getInitializerStatements() != null) {
			ctx.invoke(genStatementNoBraces, arg.getInitializerStatements(), ctx, out);
		}
	}


	public void genAssignment(Type type, Context ctx, TabbedWriter out, Expression arg1, Expression arg2, String arg3) {
		// if the lhs is non-nullable but the rhs is nullable, we have a special case
		if (!arg1.isNullable() && arg2.isNullable()) {
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(arg3);
			out.print("(function(x){ return x != null ? (x) : ");
			ctx.invoke(genDefaultValue, type, ctx, out, arg1);
			out.print("; })");
			out.print("(");
			ctx.invoke(genExpression, arg2, ctx, out);
			out.print(")");
		} else {
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(arg3);
			ctx.invoke(genExpression, arg2, ctx, out);
		}
	}
	
	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		//NOGO sbg Copied from AnyTypeTemplate
		if (arg.getConversionOperation() != null) {
			out.print(ctx.getNativeImplementationMapping((Classifier) arg.getConversionOperation().getContainer()) + '.');
			out.print("from");
			out.print(ctx.getNativeTypeName(arg.getConversionOperation().getParameters().get(0).getType()));
			out.print("(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			if (ctx.getPrimitiveMapping(arg.getObjectExpr().getType().getClassifier().getTypeSignature()) == null) {
				out.print(",\"");
				ctx.invoke(genSignature, arg.getObjectExpr().getType(), ctx, out, arg);
				out.print("\"");
			}
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
			out.print(")");
		} else if (ctx.mapsToPrimitiveType(arg.getEType())) {
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeCast(");
			if (arg.getObjectExpr().getType() != TypeUtils.Type_ANY) {
				BoxingExpression boxingExpr = IrFactory.INSTANCE.createBoxingExpression();
				boxingExpr.setExpr(arg.getObjectExpr());
				ctx.invoke(genExpression, boxingExpr, ctx, out);
			}
			else {
				ctx.putAttribute(arg.getObjectExpr(), Constants.DONT_UNBOX, Boolean.TRUE);
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
				ctx.putAttribute(arg.getObjectExpr(), Constants.DONT_UNBOX, Boolean.FALSE);
			}
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
			out.print(")");
		} else {
			out.print(eglnamespace + "egl.lang.EglAny.ezeCast("); // TODO sbg need to dynamically get class name
			if (arg.getObjectExpr().getType() != TypeUtils.Type_ANY) {
				BoxingExpression boxingExpr = IrFactory.INSTANCE.createBoxingExpression();
				boxingExpr.setExpr(arg.getObjectExpr());
				ctx.invoke(genExpression, boxingExpr, ctx, out);
			}
			else {
				ctx.putAttribute(arg.getObjectExpr(), Constants.DONT_UNBOX, Boolean.TRUE);
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
				ctx.putAttribute(arg.getObjectExpr(), Constants.DONT_UNBOX, Boolean.FALSE);
			}
			out.print(", ");
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.JavascriptImplementation);
			out.print(")");
		}
	}

	
	public void genTypeBasedAssignment(Type type, Context ctx, TabbedWriter out, Assignment arg) {
		String operator = "=";
		if (arg.getOperator() != null && arg.getOperator().length() > 0) {
			operator = arg.getOperator();
		}
		// For compound assignments like lhs += rhs, we unravel them into lhs = lhs + rhs in JavaScript
		if ((operator != null) && (operator.length() == 2) && ("=".equals(operator.substring(1, 2)))) { 
			String op = operator.substring(0,1);
			BinaryExpression binExpr = IrFactory.INSTANCE.createBinaryExpression();
			binExpr.setLHS(arg.getLHS());
			binExpr.setOperator(op);
			binExpr.setRHS(arg.getRHS());
			ctx.invoke(genAssignment, arg.getLHS(), ctx, out, binExpr, " = ");
		}
		else {
			ctx.invoke(genAssignment, arg.getLHS(), ctx, out, arg.getRHS(), " " + CommonUtilities.getNativeJavaScriptAssignment(operator) + " ");
		}
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// if either side of this expression is nullable, or if there is no direct java operation, we need to use the runtime
		if ((arg.getLHS().isNullable() || arg.getRHS().isNullable()) || CommonUtilities.getNativeJavaScriptOperation(arg, ctx).length() == 0) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("("); // TODO sbg Not needed for JavaScript? ezeProgram, ");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
		} else {
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(CommonUtilities.getNativeJavaScriptOperation(arg, ctx));
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
		}
	}

	public void genUnaryExpression(Type type, Context ctx, TabbedWriter out, UnaryExpression arg) {
		// we only need to check for minus sign and if found, we need to change it to -()
		if (arg.getOperator().equals("-") || arg.getOperator().equals("!")) {
			out.print(arg.getOperator() + "(");
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
			out.print(")");
		} else
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out, TypedElement arg) {
		/* In EDT, nullable is a characteristic of the field, not the type, so this is no
		 * longer appropriate.....
		  if (arg.isNullable())
			out.print("?");
		 */
		ctx.invoke(genSignature, type, ctx, out);
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out, Expression arg) {
		/* In EDT, nullable is a characteristic of the field, not the type, so this is no
		 * longer appropriate.....
		  if (arg.isNullable())
			out.print("?");
		 */
		ctx.invoke(genSignature, type, ctx, out);
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out) {
		// TODO sbg In RBD, the type may be null which has a runtime signature of "V;" -- do we need to handle that, and if
		// so, how?
		out.print(type.getTypeSignature());
	}

	public void genContainerBasedAccessor(Type type, Context ctx, TabbedWriter out, Function arg) {
		// no default
	}

	public void genContainerBasedAssignment(Type type, Context ctx, TabbedWriter out, Assignment arg1, Field arg2) {
		ctx.invoke(genAssignment, arg1, ctx, out);
	}

	public void genContainerBasedArrayAccess(Type type, Context ctx, TabbedWriter out, ArrayAccess arg1, Field arg2) {
		ctx.invoke(genArrayAccess, arg1, ctx, out);
	}

	public void genContainerBasedMemberAccess(Type type, Context ctx, TabbedWriter out, MemberAccess arg1, Member arg2) {
		ctx.invoke(genMemberAccess, arg1, ctx, out);
	}

	public void genContainerBasedMemberName(Type type, Context ctx, TabbedWriter out, MemberName arg1, Member arg2) {
		ctx.invoke(genMemberName, arg1, ctx, out);
	}

	public void genContainerBasedNewExpression(Type type, Context ctx, TabbedWriter out, Expression arg) {
		ctx.invoke(genNewExpression, arg, ctx, out);
	}

	public void genContainerBasedInvocation(Type type, Context ctx, TabbedWriter out, Expression arg) {
		ctx.invoke(genInvocation, arg, ctx, out);
	}

	public void genFieldInfoTypeName(Type type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, arg);
	}
	public void genCloneMethod(Type type, Context ctx, TabbedWriter out) {
	}
}
