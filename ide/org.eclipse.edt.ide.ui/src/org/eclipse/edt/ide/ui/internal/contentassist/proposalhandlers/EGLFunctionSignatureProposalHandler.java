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
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLProposalContextInformation;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.ITextViewer;

public class EGLFunctionSignatureProposalHandler extends EGLAbstractProposalHandler {

	private Expression invocationTarget;

	public EGLFunctionSignatureProposalHandler(ITextViewer viewer, int documentOffset, Expression invocationTarget) {
		super(viewer, documentOffset, "");		
		this.invocationTarget = invocationTarget;
	}

	public List getProposals() {
		List proposals = new ArrayList();
		
		Object obj =  invocationTarget.getAttributeFromName(Name.OVERLOADED_FUNCTION_SET);
		if(obj != null && obj instanceof List) {
			List list = (List) obj;
			for(Object value : list) {
				addProposal(proposals, value);
			}			
		}
		else {		
			addProposal(proposals, invocationTarget.resolveMember());
		}
		
		return proposals;
	}

	private void addProposal(List proposals, Object obj) {
		Object proposal = createProposal(obj);
		if(proposal != null) {
			proposals.add(proposal);
		}
	}

	private Object createProposal(Object obj) {
		
		
		if (obj instanceof Function) {
			return createProposal((Function) obj);
		}
		
		if (obj instanceof Constructor) {
			Constructor constructor = (Constructor) obj;
			return createProposal("constructor", constructor.getParameters(), null, getNameFromElement(constructor.getContainer()));
		}
		
		if (obj instanceof Field) {
			Type type = BindingUtil.getBaseType(((Field)obj).getType());
			if (type instanceof Delegate) {
				Delegate delegate = (Delegate) type;
				return createProposal(delegate.getCaseSensitiveName(), delegate.getParameters(), delegate.getReturnType(), getPackageName(delegate));
			}
		}
		return null;
	}
	
	private Object createProposal(Function function) {
		return createProposal(function.getCaseSensitiveName(), function.getParameters(), function.getReturnType(), getNameFromElement(function.getContainer()));
	}

	private Object createProposal(String funcName, List<FunctionParameter> parameters, Type returnType, String declarerName) {
		if(parameters == null || parameters.size() == 0) {
			return null;
		}
		
		String parameterList = getParameterListString(parameters);
		
		StringBuffer proposalSB = new StringBuffer();
		proposalSB.append(funcName);
		proposalSB.append("(");
		proposalSB.append(parameterList);
		proposalSB.append(")");
		if(returnType != null) {
			proposalSB.append(" ");
			proposalSB.append(getTypeString(returnType));
		}
		proposalSB.append(" - ");
		proposalSB.append(declarerName);
		
		
		EGLCompletionProposal completionProposal = new EGLCompletionProposal(viewer,
			proposalSB.toString(),
			"",
			null,
			getDocumentOffset() - getPrefix().length(),			
			0,
			0,
			EGLCompletionProposal.NO_IMG_KEY);
		
		EGLProposalContextInformation contextInformation = new EGLProposalContextInformation(completionProposal, getLParenOffsetAfter(invocationTarget.getOffset()) + 1, proposalSB.toString(), parameterList);
		
		completionProposal.setContextInformation(contextInformation);
		
		return completionProposal;
	}
}
