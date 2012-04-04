/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.builder;

/**
 * @author svihovec
 *
 */
public class GenericTopLevelFunctionProblemRequestor extends AbstractTopLevelFunctionContextDependentProblemRequestor {

	public GenericTopLevelFunctionProblemRequestor(IProblemRequestor requestor, boolean containerContextDependent) {
		super(requestor, containerContextDependent);
	}

	public boolean shouldReportProblem(int problemKind) {
		if(isContainerContextDependent() && problemKind == IProblemRequestor.TYPE_CANNOT_BE_RESOLVED){
			return false;
		}else {
			return !messagesWithLineNumberInserts.contains(new Integer(problemKind));
		}
	}
}
