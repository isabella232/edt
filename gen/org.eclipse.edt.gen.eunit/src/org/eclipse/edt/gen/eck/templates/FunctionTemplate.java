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
package org.eclipse.edt.gen.eck.templates;

import java.util.List;

import org.eclipse.edt.gen.eck.CommonUtilities;
import org.eclipse.edt.gen.eck.Constants;
import org.eclipse.edt.gen.eck.Context;
import org.eclipse.edt.gen.eck.TestCounter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;

public class FunctionTemplate extends EckTemplate {

//	private static final String FQ_LOGRECORD = CommonUtilities.ECKRUNTIME_PACKAGENAME + ".Log";
//	private static final String FQ_STATUSRECORD = CommonUtilities.ECKRUNTIME_PACKAGENAME + ".Status";
//	private static final String FQ_TESTUTILHANDLER = CommonUtilities.ECKRUNTIME_PACKAGENAME + ".TestUtil";
	private static final String FQ_TESTANNOTATION = CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".Test";

	@SuppressWarnings("unchecked")
	public void preGen(Function function, Context ctx, TestCounter couter) {
		List<String> functions = (List<String>) ctx.getAttribute(
				ctx.getClass(), Constants.SubKey_partFunctionsWanted);

		// test the functions, only care about the ones that is the following
		// signature
		// delegate runTestMethod(outR Result, stat Status out) end

		List<FunctionParameter> funcParms = function.getParameters();
		int paramCnt = (funcParms != null) ? funcParms.size() : 0;
		if(paramCnt == 0){
			Annotation testAnnot = function.getAnnotation(FQ_TESTANNOTATION);
			if(testAnnot != null){
				functions.add(function.getName());
				couter.increment();
			}
		}
		
//		if (paramCnt == 1) {
//			FunctionParameter funcParm = funcParms.get(0);
//			Type paramType = funcParm.getType();
//			
//			if(checkHandlerParameter(funcParm.getParameterKind(), ParameterKind.PARM_INOUT, paramType, paramType.getTypeSignature(), FQ_TESTUTILHANDLER))
//				functions.add(function.getName());
//		}

//		if (paramCnt == 2) { // only 2 parameter
//			// 1st parameter is Log inout
//			FunctionParameter funcParm1 = funcParms.get(0);
//			Type paramType1 = funcParm1.getType();
//
//			if (checkParameter(funcParm1.getParameterKind(),
//					ParameterKind.PARM_INOUT, paramType1,
//					paramType1.getTypeSignature(), FQ_LOGRECORD)) {
//				// 2nd parameter is Status out
//				FunctionParameter funcParm2 = funcParms.get(1);
//				Type paramType2 = funcParm2.getType();
//				if (checkParameter(funcParm2.getParameterKind(),
//						ParameterKind.PARM_OUT, paramType2,
//						paramType2.getTypeSignature(), FQ_STATUSRECORD)) {
//					functions.add(function.getName());
//				}
//			}
//		}
	}

	private boolean checkParameter(ParameterKind paramKind,
			ParameterKind checkParmKind, Type paramType, String paramTypeSig,
			String checkParamTypeSig) {
		return (paramKind == checkParmKind && paramType instanceof Record && paramTypeSig
				.equals(checkParamTypeSig));
	}

	private boolean checkHandlerParameter(ParameterKind paramKind,
			ParameterKind checkParmKind, Type paramType, String paramTypeSig,
			String checkParamTypeSig) {
		return (paramKind == checkParmKind && paramType instanceof Handler && paramTypeSig
				.equals(checkParamTypeSig));
	}
}
