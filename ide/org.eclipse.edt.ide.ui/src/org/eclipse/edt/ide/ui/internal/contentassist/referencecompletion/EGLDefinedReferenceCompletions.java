/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.List;

public class EGLDefinedReferenceCompletions {

	private static IReferenceCompletion[] DEFINED_COMPLETIONS;

	//only used during development to check for duplicate states
//	private static ArrayList allValidStates;
	
	public static IReferenceCompletion[] getDefinedCompletions() {
		if (DEFINED_COMPLETIONS == null) {
			List completionList = new ArrayList();
			completionList.add(new EGLSQLAddStatementReferenceCompletion());
			completionList.add(new EGLSQLToStatementReferenceCompletion());
			completionList.add(new EGLSQLFromStatementRefrerenceCompletion());
			completionList.add(new EGLSQLForStatementReferenceCompletion());
			completionList.add(new EGLSQLPrepareWithStatementReferenceCompletion());
			completionList.add(new EGLSQLDeleteStatementReferenceCompletion());
			completionList.add(new EGLSQLCloseStatementReferenceCompletion());
			completionList.add(new EGLSQLGetStatementReferenceCompletion());
			completionList.add(new EGLSQLOpenStatementReferenceCompletion());
			completionList.add(new EGLSQLPrepareStatementReferenceCompletion());
			completionList.add(new EGLUsingStatementReferenceCompletion());
			completionList.add(new EGLAsIsaOperatorReferenceCompletion());
			completionList.add(new EGLArrayIndexReferenceCompletion());
			completionList.add(new EGLAssignmentStatementReferenceCompletion());
			completionList.add(new EGLBooleanExpressionReferenceCompletion());
			completionList.add(new EGLCallStatementReferenceCompletion());
			completionList.add(new EGLCallStatementWithCallbackReferenceCompletion());
			completionList.add(new EGLCaseStatementReferenceCompletion());
			completionList.add(new EGLConstantTypeReferenceCompletion());
			completionList.add(new EGLContinueStatementReferenceCompletion());
			completionList.add(new EGLExitStatementReferenceCompletion());
			completionList.add(new EGLExitProgramStatementReferenceCompletion());
			completionList.add(new EGLExtendsReferenceCompletion());
			completionList.add(new EGLForStatementReferenceCompletion());
			completionList.add(new EGLForeachIntoStatementReferenceCompletion());
			completionList.add(new EGLForeachStatementReferenceCompletion());
			completionList.add(new EGLForToStatementReferenceCompletion());
			completionList.add(new EGLFunctionArgumentsReferenceCompletion());
			completionList.add(new EGLFunctionParameterNameReferenceCompletion());
			completionList.add(new EGLFunctionParameterTypeReferenceCompletion());
			completionList.add(new EGLFunctionReturnsReferenceCompletion());
			completionList.add(new EGLImplementsReferenceCompletion());
			completionList.add(new EGLNewLogicLineReferenceCompletion());
			completionList.add(new EGLNewReferenceCompletion());
			completionList.add(new EGLSettingsBlockFieldReferenceCompletion());
			completionList.add(new EGLSettingsBlockAnnotationCompletion());
			completionList.add(new EGLSettingsBlockAnnotationCompletion2());
			completionList.add(new EGLNumericExpressionStatementReferenceCompletion());
			completionList.add(new EGLParameterModifierReferenceCompletion());
			completionList.add(new EGLCallArgumentsReferenceCompletion());
			completionList.add(new EGLExpressionPlusReferenceCompletion());
			completionList.add(new EGLParenthesizedExpressionReferenceCompletion());
			completionList.add(new EGLOperatorReferenceCompletion());
			completionList.add(new EGLSQLReplaceStatementReferenceCompletion());
			completionList.add(new EGLReturnStatementReferenceCompletion());
			completionList.add(new EGLReturnStatementWithParensReferenceCompletion());
			completionList.add(new EGLConcatenationExpressionReferenceCompletion());
			completionList.add(new EGLStructureItemNameReferenceCompletion());
			completionList.add(new EGLStructureItemTypeRefereceCompletion());
			completionList.add(new EGLSubScriptSubStringModifierReferenceCompletion());
			completionList.add(new EGLSubScriptSubString2ModifierReferenceCompletion());
			completionList.add(new EGLSubtypeReferenceCompletion());
			completionList.add(new EGLThrowStatementReferenceCompletion());
			completionList.add(new EGLTryOnExceptionStatementReferenceCompletion());
			completionList.add(new EGLTryOnException2StatementReferenceCompletion());
			completionList.add(new EGLUseStatementReferenceCompletion());
			completionList.add(new EGLVariableDotReferenceCompletion());
			completionList.add(new EGLVariableTypeReferenceCompletion());
			completionList.add(new EGLSQLWithStatementReferenceCompletion());
			completionList.add(new EGLAnnotationFieldReferenceCompletion());
			completionList.add(new EGLImportReferenceCompletion());
			completionList.add(new EGLInitializerCompletion());
			completionList.add(new EGLAnnotationValueAssignmentReferenceCompletion());
			
			DEFINED_COMPLETIONS = (IReferenceCompletion[]) completionList.toArray(new IReferenceCompletion[0]);
		}
		
		return DEFINED_COMPLETIONS;
	}
		
		
	public static void reset() {
		DEFINED_COMPLETIONS = null;
	}

}
