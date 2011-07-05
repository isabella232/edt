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

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class TypeTemplate extends JavaTemplate {

	public void preGen(Type type, Context ctx) {
		// when we get here, it is because a type is being referenced by the original part being generated. Add it to the
		// types used table if it doesn't already exist. The first thing we want to check for, is to make sure the
		// unqualified type name (the last node in the name) is not already taken. If it is already taken, we can't do an
		// import for it and when used throughout the program, will have to be fully qualified at all times.
		if (type instanceof Delegate) {
			// All delegates use our runtime Delegate class. No import needed.
			return;
		}
		CommonUtilities.processImport(ctx.getNativeImplementationMapping(type.getClassifier()), ctx);
		// in the case where the model name doesn't match the interface name, then we also need to process the interface
		// name as it will exist
		if (ctx.getNativeMapping(ctx.getNativeMapping(type.getClassifier().getTypeSignature())) != null)
			CommonUtilities.processImport(ctx.getNativeMapping(type.getClassifier().getTypeSignature()), ctx);
		if (ctx.mapsToPrimitiveType(type.getClassifier())) {
			// if this primitive type is really a primitive, it will map back to the java object. We want to use that java
			// object instead of the primitive mapped simply to a java object. For example, egl.lang.Int16 maps to short (the
			// primitive) and then short maps to the java object java.lang.Short. We want to always use the object for the
			// imports.
			if (ctx.getPrimitiveMapping(ctx.getPrimitiveMapping(type.getClassifier().getTypeSignature())) != null)
				CommonUtilities.processImport(ctx.getPrimitiveMapping(ctx.getPrimitiveMapping(type.getClassifier().getTypeSignature())), ctx);
			else
				CommonUtilities.processImport(ctx.getPrimitiveMapping(type.getClassifier().getTypeSignature()), ctx);
		}
	}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaImplementation);
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
			out.print("null");
		else
			out.print("\"Invalid default value\"");
	}

	public void genRuntimeConstraint(Type type, Context ctx, TabbedWriter out) {
		genRuntimeTypeName(type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".class");
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		// are we looking for the default (java primitive) or specifically java primitive, if it exists
		if (arg == TypeNameKind.JavaPrimitive) {
			if (ctx.mapsToPrimitiveType(type.getClassifier())) {
				out.print(ctx.getPrimitiveMapping(type.getClassifier()));
				return;
			}
		}
		// are we looking for the java object
		if (arg == TypeNameKind.JavaObject) {
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
		if (arg == TypeNameKind.JavaImplementation) {
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
		if (arg == TypeNameKind.JavaImplementation)
			out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
		else
			// must be an egl interface name we want
			out.print(ctx.getNativeInterfaceMapping(type.getClassifier()));
	}

	public void genConstructorOptions(Type type, Context ctx, TabbedWriter out) {
		// no default
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out) {
		// no default
	}

	public void genAssignment(Type type, Context ctx, TabbedWriter out, Expression arg1, Expression arg2) {
		// if the lhs is non-nullable but the rhs is nullable, we have a special case
		if (!TypeUtils.isReferenceType(arg1.getType()) && !arg1.isNullable() && arg2.isNullable()) {
			// if this is a well-behaved assignment, we can avoid the temporary
			if (IRUtils.hasSideEffects(arg2)) {
				String temporary = ctx.nextTempName();
				ctx.invoke(genRuntimeTypeName, arg2.getType(), ctx, out, TypeNameKind.JavaObject);
				out.print(" " + temporary + " = ");
				ctx.invoke(genExpression, arg2, ctx, out);
				out.println(";");
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(" = " + temporary + " == null ? ");
				ctx.invoke(genDefaultValue, type, ctx, out, arg1);
				out.print(" : " + temporary);
			} else if (TypeUtils.isReferenceType(arg2.getType())) {
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(" = ");
				ctx.invoke(genExpression, arg2, ctx, out);
			} else {
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(" = ");
				ctx.invoke(genExpression, arg2, ctx, out);
				out.print(" == null ? ");
				ctx.invoke(genDefaultValue, type, ctx, out, arg1);
				out.print(" : ");
				ctx.invoke(genExpression, arg2, ctx, out);
			}
		} else {
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(" = ");
			ctx.invoke(genExpression, arg2, ctx, out);
		}
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// if either side of this expression is nullable, or if there is no direct java operation, we need to use the runtime
		if ((arg.getLHS().isNullable() || arg.getRHS().isNullable()) || CommonUtilities.getNativeJavaOperation(arg, ctx).length() == 0) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
		} else {
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(CommonUtilities.getNativeJavaOperation(arg, ctx));
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
		}
	}

	public void genUnaryExpression(Type type, Context ctx, TabbedWriter out, UnaryExpression arg) {
		// we only need to check for minus sign and if found, we need to change it to -()
		if (arg.getOperator().equals("-"))
			out.print(arg.getOperator() + "(");
		ctx.invoke(genExpression, arg.getExpression(), ctx, out);
		// we only need to check for minus sign and if found, we need to change it to -()
		if (arg.getOperator().equals("-"))
			out.print(")");
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
}
