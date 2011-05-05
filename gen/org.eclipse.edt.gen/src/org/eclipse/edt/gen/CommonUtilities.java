package org.eclipse.edt.gen;

import org.eclipse.edt.gen.EglContext.TypeLogicKind;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class CommonUtilities {

	public static boolean processTypeList(String method, Type type, EglContext ctx, TabbedWriter out, Object... args) {
		// did we have a list of types to check, otherwise use the default
		if (isProcessWithTypeList(args)) {
			// pass the first type in the list (just past the TypeLogicKind value and the field). We must cast to EObject to
			// avoid reaccessing the logic that brought us here, located in the Type version of ctx.gen
			ctx.gen(method, getTypeFromList(args), ctx, out, genProcessWithoutTypeList(args));
			return true;
		} else if (isProcessWithoutTypeList(args)) {
			ctx.gen(method, (EObject) type, ctx, out, genFinishWithoutTypeList(args));
			return true;
		} else
			return false;
	}

	private static boolean isProcessWithTypeList(Object... args) {
		// we are looking for a TypeLogicKind.Process value followed by at least one object
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i] instanceof TypeLogicKind && (TypeLogicKind) args[i] == TypeLogicKind.Process)
				return true;
		}
		return false;
	}

	private static EObject getTypeFromList(Object... args) {
		// we are looking for a TypeLogicKind.Process value and returning the first object past it
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i] instanceof TypeLogicKind && (TypeLogicKind) args[i] == TypeLogicKind.Process)
				return (EObject) args[i + 1];
		}
		return null;
	}

	private static Object[] genProcessWithoutTypeList(Object... args) {
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

	private static boolean isProcessWithoutTypeList(Object... args) {
		// we are looking for a TypeLogicKind.Process value followed by no object
		if (args.length > 0 && args[args.length - 1] instanceof TypeLogicKind && (TypeLogicKind) args[args.length - 1] == TypeLogicKind.Process)
			return true;
		return false;
	}

	private static Object[] genFinishWithoutTypeList(Object... args) {
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

	public static boolean isArgumentToBeAltered(FunctionParameter parameter, Expression expression, EglContext ctx) {
		if (parameter.getParameterKind() == ParameterKind.PARM_IN) {
			// if the parameter is reference then do not make a temporary
			if (TypeUtils.isReferenceType(parameter.getType()))
				return false;
			// if the argument and parameter types mismatch, or if nullable, or not java primitive, then create a
			// temporary
			if (!parameter.getType().equals(expression.getType()) || parameter.isNullable() || expression.isNullable()
				|| !ctx.mapsToPrimitiveType(parameter.getType()))
				return true;
			return false;
		} else
			return isBoxedParameterType(parameter, ctx);
	}

	public static boolean isBoxedParameterType(FunctionParameter parameter, EglContext ctx) {
		if (parameter.getParameterKind() == ParameterKind.PARM_INOUT) {
			if (TypeUtils.isReferenceType(parameter.getType()))
				return true;
			else if (ctx.mapsToPrimitiveType(parameter.getType()))
				return true;
			else if (parameter.isNullable())
				return true;
		} else if (parameter.getParameterKind() == ParameterKind.PARM_OUT)
			return true;
		return false;
	}
}
