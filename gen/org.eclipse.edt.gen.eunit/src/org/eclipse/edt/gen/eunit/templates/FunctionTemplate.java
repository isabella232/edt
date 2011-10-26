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
package org.eclipse.edt.gen.eunit.templates;

import java.util.List;

import org.eclipse.edt.gen.eunit.CommonUtilities;
import org.eclipse.edt.gen.eunit.Constants;
import org.eclipse.edt.gen.eunit.Context;
import org.eclipse.edt.gen.eunit.TestCounter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.utils.EList;

public class FunctionTemplate extends EUnitTemplate {

	private static final String FQ_TESTANNOTATION = CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".Test";

	@SuppressWarnings("unchecked")
	public void preGen(Function function, Context ctx, TestCounter couter) {
		List<String> functions = (List<String>) ctx.getAttribute(
				ctx.getClass(), Constants.SubKey_partFunctionsWanted);

		// test the functions, only care about the ones that is the following
		// signature
		// function runTestMethod() {@Test}
		// end

		List<FunctionParameter> funcParms = function.getParameters();
		int paramCnt = (funcParms != null) ? funcParms.size() : 0;
		if(paramCnt == 0){
			Annotation testAnnot = function.getAnnotation(FQ_TESTANNOTATION);
			if(testAnnot != null){
				Object targetLangs = testAnnot.getValue("targetLang");
				if(targetLangs != null){
					//should be EList
//					if(targetLangs instanceof EList<E>){
//						//list of enumeration
//					}
				}
				
				functions.add(function.getName());
				couter.increment();
			}
		}		
	}

}
