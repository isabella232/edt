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
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class TypeTemplate extends JavaTemplate {

	public void validate(Type type, Context ctx, Object... args) {
		// when we get here, it is because a type is being referenced by the original part being validated. Add it to the
		// types used table if it doesn't already exist. The first thing we want to check for, is to make sure the
		// unqualified type name (the last node in the name) is not already taken. If it is already taken, we can't do an
		// import for it and when used throughout the program, will have to be fully qualified at all times.
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

	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genConversionOperation, type, ctx, out, args)) {
			// check to see if a conversion is required
			if (((AsExpression) args[0]).getConversionOperation() != null) {
				out.print(ctx.getNativeImplementationMapping((Classifier) ((AsExpression) args[0]).getConversionOperation().getContainer()) + '.');
				out.print(((AsExpression) args[0]).getConversionOperation().getName());
				out.print("(");
				ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
				ctx.gen(genTypeDependentOptions, ((AsExpression) args[0]).getEType(), ctx, out, args);
				out.print(")");
			} else if (ctx.mapsToPrimitiveType(((AsExpression) args[0]).getEType())) {
				ctx.gen(genRuntimeTypeName, ((AsExpression) args[0]).getEType(), ctx, out, TypeNameKind.EGLImplementation);
				out.print(".ezeCast(");
				ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
				ctx.gen(genTypeDependentOptions, ((AsExpression) args[0]).getEType(), ctx, out, args);
				out.print(")");
			} else {
				out.print("AnyObject.ezeCast(");
				ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
				out.print(", ");
				ctx.gen(genRuntimeTypeName, ((AsExpression) args[0]).getEType(), ctx, out, TypeNameKind.JavaImplementation);
				out.print(".class)");
			}
		}
	}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genInstantiation, type, ctx, out, args)) {
			out.print("new ");
			ctx.gen(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaImplementation);
			out.print("(");
			ctx.gen(genConstructorOptions, type, ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
			out.print(")");
		}
	}

	public void genInvocation(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genInvocation, type, ctx, out, args))
			ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genDefaultValue, type, ctx, out, args)) {
			if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
				out.print("null");
			else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
				out.print("null");
			else if (TypeUtils.isReferenceType(type))
				out.print("null");
			else
				out.print("\"Invalid default value\"");
		}
	}

	public void genRuntimeConstraint(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genRuntimeConstraint, type, ctx, out, args)) {
			genRuntimeTypeName(type, ctx, out, TypeNameKind.EGLImplementation);
			out.print(".class");
		}
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genRuntimeTypeName, type, ctx, out, args)) {
			// are we looking for the default (java primitive) or specifically java primitive, if it exists
			if (args.length == 0 || args[0] == null || args[0] == TypeNameKind.JavaPrimitive) {
				if (ctx.mapsToPrimitiveType(type.getClassifier())) {
					out.print(ctx.getPrimitiveMapping(type.getClassifier()));
					return;
				}
			}
			// are we looking for the java object
			if (args[0] == TypeNameKind.JavaObject) {
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
			if (args[0] == TypeNameKind.JavaImplementation) {
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
			if (args[0] == TypeNameKind.EGLImplementation) {
				if (ctx.mapsToNativeType(type.getClassifier())) {
					out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
					return;
				}
			}
			// select the proper default to use. we have run out of options
			if (args[0] == TypeNameKind.JavaImplementation)
				out.print(ctx.getNativeImplementationMapping(type.getClassifier()));
			else
				// must be an egl interface name we want
				out.print(ctx.getNativeInterfaceMapping(type.getClassifier()));
		}
	}

	public void genConstructorOptions(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genConstructorOptions, type, ctx, out, args)) {
			// no default
		}
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genTypeDependentOptions, type, ctx, out, args)) {
			// no default
		}
	}

	public void genAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genAssignment, type, ctx, out, args)) {
			// if the lhs is non-nullable but the rhs is nullable, we have a special case
			if (!TypeUtils.isReferenceType(((Expression) args[0]).getType()) && !((Expression) args[0]).isNullable() && ((Expression) args[1]).isNullable()) {
				// if this is a well-behaved assignment, we can avoid the temporary
				if (IRUtils.hasSideEffects(((Expression) args[1]))) {
					String temporary = ctx.nextTempName();
					ctx.gen(genRuntimeTypeName, ((Expression) args[1]).getType(), ctx, out, TypeNameKind.JavaObject);
					out.print(" " + temporary + " = ");
					ctx.gen(genExpression, ((Expression) args[1]), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
					out.println(";");
					ctx.gen(genExpression, ((Expression) args[0]), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
					out.print(" = " + temporary + " == null ? ");
					ctx.gen(genDefaultValue, type, ctx, out, ((Expression) args[0]));
					out.print(" : " + temporary);
				} else if (TypeUtils.isReferenceType(((Expression) args[1]).getType())) {
					ctx.gen(genExpression, (Expression) args[0], ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
					out.print(" = ");
					ctx.gen(genExpression, (Expression) args[1], ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				} else {
					ctx.gen(genExpression, ((Expression) args[0]), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
					out.print(" = ");
					ctx.gen(genExpression, ((Expression) args[1]), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
					out.print(" == null ? ");
					ctx.gen(genDefaultValue, type, ctx, out, ((Expression) args[0]));
					out.print(" : ");
					ctx.gen(genExpression, ((Expression) args[1]), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				}
			} else {
				ctx.gen(genExpression, (Expression) args[0], ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				out.print(" = ");
				ctx.gen(genExpression, (Expression) args[1], ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
			}
		}
	}

	public void genSubstringAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genSubstringAssignment, type, ctx, out, args)) {
			// no default
		}
	}

	public void genSubstringAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genSubstringAccess, type, ctx, out, args)) {
			// no default
		}
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genBinaryExpression, type, ctx, out, args)) {
			// if either side of this expression is nullable, or if there is no direct java operation, we need to use the
			// runtime
			if ((((BinaryExpression) args[0]).getLHS().isNullable() || ((BinaryExpression) args[0]).getRHS().isNullable())
				|| CommonUtilities.getNativeJavaOperation((BinaryExpression) args[0], ctx).length() == 0) {
				out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
				out.print(CommonUtilities.getNativeRuntimeOperationName((BinaryExpression) args[0]));
				out.print("(");
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				out.print(", ");
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
			} else {
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
				out.print(CommonUtilities.getNativeJavaOperation((BinaryExpression) args[0], ctx));
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
			}
		}
	}

	public void genUnaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genUnaryExpression, type, ctx, out, args)) {
			// we only need to check for minus sign and if found, we need to change it to -()
			if (((UnaryExpression) args[0]).getOperator().equals("-"))
				out.print(((UnaryExpression) args[0]).getOperator() + "(");
			ctx.gen(genExpression, ((UnaryExpression) args[0]).getExpression(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
			// we only need to check for minus sign and if found, we need to change it to -()
			if (((UnaryExpression) args[0]).getOperator().equals("-"))
				out.print(")");
		}
	}

	public void genContainerBasedAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedAssignment, type, ctx, out, args))
			ctx.gen(genAssignment, (Assignment) args[0], ctx, out, args);
	}

	public void genContainerBasedArrayAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedArrayAccess, type, ctx, out, args))
			ctx.gen(genArrayAccess, (ArrayAccess) args[0], ctx, out, args);
	}

	public void genContainerBasedMemberAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedMemberAccess, type, ctx, out, args))
			ctx.gen(genMemberAccess, (MemberAccess) args[0], ctx, out, args);
	}

	public void genContainerBasedMemberName(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (!org.eclipse.edt.gen.CommonUtilities.processTypeList(genContainerBasedMemberName, type, ctx, out, args))
			ctx.gen(genMemberName, (MemberName) args[0], ctx, out, args);
	}
}
