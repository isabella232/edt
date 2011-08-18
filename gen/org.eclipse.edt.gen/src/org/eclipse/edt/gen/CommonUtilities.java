package org.eclipse.edt.gen;

import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class CommonUtilities {

	public static boolean isArgumentToBeAltered(FunctionParameter parameter, Expression expression, EglContext ctx) {
		if (parameter.getParameterKind() == ParameterKind.PARM_IN) {
			// if the parameter is reference then do not make a temporary
			if (TypeUtils.isReferenceType(parameter.getType()))
				return false;
			// if the argument and parameter types mismatch, or if nullable, or not java primitive, then create a
			// temporary
			if (!parameter.getType().equals(expression.getType()) || parameter.isNullable() || expression.isNullable()
				|| !ctx.mapsToPrimitiveType(parameter.getType()))
				//if the parameter is a const then we should not make a copy
				return !parameter.isConst();
			return false;
		} else{
			//if the parameter is a const we should not make a copy
			return isBoxedParameterType(parameter, ctx) && !parameter.isConst();
		}
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
	
	public static Annotation includeEndOffset(Annotation annotation, EglContext ctx){
		annotation.addAnnotation(ctx.getFactory().createAnnotation(EGLMessage.IncludeEndOffset));
		return annotation;
	}
}
