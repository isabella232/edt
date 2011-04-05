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

import org.eclipse.edt.gen.EglContext.TypeLogicKind;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BinaryExpression;
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

	public void genInstantiation(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genInstantiation, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genInstantiation, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else {
			out.print("new ");
			ctx.gen(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaImplementation);
			out.print("(");
			ctx.gen(genConstructorOptions, type, ctx, out, genWithoutTypeList(args));
			out.print(")");
		}
	}

	public void genInvocation(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genInvocation, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genInvocation, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else
			ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, genWithoutTypeList(args));
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genDefaultValue, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genDefaultValue, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else {
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
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genRuntimeConstraint, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genRuntimeConstraint, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else {
			genRuntimeTypeName(type, ctx, out, TypeNameKind.EGLImplementation);
			out.print(".class");
		}
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genRuntimeTypeName, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genRuntimeTypeName, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else {
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
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genConstructorOptions, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genConstructorOptions, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genTypeDependentOptions, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genTypeDependentOptions, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
	}

	public void genAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genAssignment, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genAssignment, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else {
			// if the lhs is non-nullable but the rhs is nullable, we have a special case
			if (!TypeUtils.isReferenceType(((Expression) args[0]).getType()) && !((Expression) args[0]).isNullable() && ((Expression) args[1]).isNullable()) {
				// if this is a well-behaved assignment, we can avoid the temporary
				if (IRUtils.hasSideEffects(((Expression) args[1]))) {
					String temporary = ctx.nextTempName();
					ctx.gen(genRuntimeTypeName, ((Expression) args[1]).getType(), ctx, out, TypeNameKind.JavaObject);
					out.print(" " + temporary + " = ");
					ctx.gen(genExpression, ((Expression) args[1]), ctx, out, genWithoutTypeList(args));
					out.println(";");
					ctx.gen(genExpression, ((Expression) args[0]), ctx, out, genWithoutTypeList(args));
					out.print(" = " + temporary + " == null ? ");
					ctx.gen(genDefaultValue, type, ctx, out, ((Expression) args[0]));
					out.print(" : " + temporary);
				} else if (TypeUtils.isReferenceType(((Expression) args[1]).getType())) {
					ctx.gen(genExpression, (Expression) args[0], ctx, out, genWithoutTypeList(args));
					out.print(" = ");
					ctx.gen(genExpression, (Expression) args[1], ctx, out, genWithoutTypeList(args));
				} else {
					ctx.gen(genExpression, ((Expression) args[0]), ctx, out, genWithoutTypeList(args));
					out.print(" = ");
					ctx.gen(genExpression, ((Expression) args[1]), ctx, out, genWithoutTypeList(args));
					out.print(" == null ? ");
					ctx.gen(genDefaultValue, type, ctx, out, ((Expression) args[0]));
					out.print(" : ");
					ctx.gen(genExpression, ((Expression) args[1]), ctx, out, genWithoutTypeList(args));
				}
			} else {
				ctx.gen(genExpression, (Expression) args[0], ctx, out, genWithoutTypeList(args));
				out.print(" = ");
				ctx.gen(genExpression, (Expression) args[1], ctx, out, genWithoutTypeList(args));
			}
		}
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genBinaryExpression, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genBinaryExpression, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else {
			// if either side of this expression is nullable, or if there is no direct java operation, we need to use the
			// runtime
			if ((((BinaryExpression) args[0]).getLHS().isNullable() || ((BinaryExpression) args[0]).getRHS().isNullable())
				|| CommonUtilities.getNativeJavaOperation((BinaryExpression) args[0], ctx).length() == 0) {
				out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
				out.print(CommonUtilities.getNativeRuntimeOperationName((BinaryExpression) args[0]));
				out.print("(ezeProgram, ");
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, genWithoutTypeList(args));
				out.print(", ");
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, genWithoutTypeList(args));
				out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
			} else {
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, genWithoutTypeList(args));
				out.print(CommonUtilities.getNativeJavaOperation((BinaryExpression) args[0], ctx));
				ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, genWithoutTypeList(args));
			}
		}
	}

	public void genUnaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genUnaryExpression, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genUnaryExpression, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else {
			// we only need to check for minus sign and if found, we need to change it to -()
			if (((UnaryExpression) args[0]).getOperator().equals("-"))
				out.print(((UnaryExpression) args[0]).getOperator() + "(");
			ctx.gen(genExpression, ((UnaryExpression) args[0]).getExpression(), ctx, out, genWithoutTypeList(args));
			// we only need to check for minus sign and if found, we need to change it to -()
			if (((UnaryExpression) args[0]).getOperator().equals("-"))
				out.print(")");
		}
	}

	public void genContainerBasedAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genContainerBasedAssignment, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genContainerBasedAssignment, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else
			ctx.gen(genAssignment, (Assignment) args[0], ctx, out, args);
	}

	public void genContainerBasedArrayAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genContainerBasedArrayAccess, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genContainerBasedArrayAccess, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else
			ctx.gen(genArrayAccess, (ArrayAccess) args[0], ctx, out, args);
	}

	public void genContainerBasedMemberAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genContainerBasedMemberAccess, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genContainerBasedMemberAccess, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else
			ctx.gen(genMemberAccess, (MemberAccess) args[0], ctx, out, args);
	}

	public void genContainerBasedMemberName(Type type, Context ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(genContainerBasedMemberName, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
		} else if (isProcessWithoutTypeList(args))
			ctx.gen(genContainerBasedMemberName, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
		else
			ctx.gen(genMemberName, (MemberName) args[0], ctx, out, args);
	}

	public static boolean isProcessWithTypeList(Object... args) {
		// we are looking for a TypeLogicKind.Process value followed by at least one object
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i] instanceof TypeLogicKind && (TypeLogicKind) args[i] == TypeLogicKind.Process)
				return true;
		}
		return false;
	}

	public static EObject getTypeFromList(Object... args) {
		// we are looking for a TypeLogicKind.Process value and returning the first object past it
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i] instanceof TypeLogicKind && (TypeLogicKind) args[i] == TypeLogicKind.Process)
				return (EObject) args[i + 1];
		}
		return null;
	}

	public static Object[] genProcessWithoutTypeList(Object... args) {
		// we need to find the TypeLogicKind.Process, keep all of the objects before it, but remove all of the objects after
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof TypeLogicKind && (TypeLogicKind) args[i] == TypeLogicKind.Process) {
				Object[] objects = new Object[i + 1];
				for (int j = 0; j < i; j++) {
					objects[j] = args[j];
				}
				objects[i] = TypeLogicKind.Process;
				return objects;
			}
		}
		return null;
	}

	public static boolean isProcessWithoutTypeList(Object... args) {
		// we are looking for a TypeLogicKind.Process value followed by no object
		if (args.length > 0 && args[args.length - 1] instanceof TypeLogicKind && (TypeLogicKind) args[args.length - 1] == TypeLogicKind.Process)
			return true;
		return false;
	}

	public static Object[] genFinishWithoutTypeList(Object... args) {
		// we need to change the TypeLogicKind.Process to TypeLogicKind.Finish, but keep all of the objects before it
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof TypeLogicKind && (TypeLogicKind) args[i] == TypeLogicKind.Process) {
				Object[] objects = new Object[i + 1];
				for (int j = 0; j < i; j++) {
					objects[j] = args[j];
				}
				objects[i] = TypeLogicKind.Finish;
				return objects;
			}
		}
		return null;
	}

	public static Object[] genWithoutTypeList(Object... args) {
		// we need to remove the TypeLogicKind.Process/TypeLogicKind.Finish if it exists, but keep all of the objects before
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof TypeLogicKind) {
				Object[] objects = new Object[i];
				for (int j = 0; j < i; j++) {
					objects[j] = args[j];
				}
				return objects;
			}
		}
		return args;
	}
}
