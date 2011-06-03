package org.eclipse.edt.gen.egl.templates;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

import javax.jws.WebParam;
import javax.jws.WebParam.Mode;

import org.eclipse.edt.gen.egl.Context;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;

public class AccessibleObjectTemplate extends EglTemplate{
	private static final String ARG = "arg";
	
	public void genName(AccessibleObject obj, Context ctx, FunctionMember functionMember, FunctionParameter parameter, Integer argIdx) {
		WebParam webParam = (WebParam)ctx.invoke(getAnnotation, (Object)obj, ctx, argIdx, WebParam.class);
		if(webParam != null &&
				webParam.name() != null && 
				webParam.name().length() > 0){
			parameter.setName(CommonUtilities.getValidEGLName(webParam.name()));
		}
		else{
			parameter.setName(ARG + String.valueOf(argIdx));
		}
	}	
	public void genParameterKind(AccessibleObject obj, Context ctx, FunctionMember functionMember, FunctionParameter parameter, Integer argIdx) {
		WebParam webParam = (WebParam)ctx.invoke(getAnnotation, (Object)obj, ctx, argIdx, WebParam.class);
		if(webParam != null){
			if(Mode.OUT.equals(webParam.mode())){
				parameter.setParameterKind(ParameterKind.PARM_OUT);
			}
			else if(Mode.INOUT.equals(webParam.mode())){
				parameter.setParameterKind(ParameterKind.PARM_INOUT);
			}
			else{
				parameter.setParameterKind(ParameterKind.PARM_IN);
			}
		}
		else{
			parameter.setParameterKind(ParameterKind.PARM_IN);
		}
	}	
	public void genFunctionParameter(AccessibleObject obj, Context ctx, FunctionMember functionMember, Integer argIdx, Type type) {
		FunctionParameter parameter = ctx.getFactory().createFunctionParameter();
		functionMember.addParameter(parameter);
		ctx.invoke(genName, (Object)obj, ctx, functionMember, parameter, argIdx);
		ctx.invoke(genType, (Object)type, ctx, parameter);
		ctx.invoke(genParameterKind, (Object)obj, ctx, functionMember, parameter, argIdx);
	}
	
	
}
