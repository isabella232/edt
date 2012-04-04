/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.OverloadedFunctionSet;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLProposalContextInformation;
import org.eclipse.jface.text.ITextViewer;

public class EGLFunctionSignatureProposalHandler extends EGLAbstractProposalHandler {

	private Expression invocationTarget;

	public EGLFunctionSignatureProposalHandler(ITextViewer viewer, int documentOffset, Expression invocationTarget) {
		super(viewer, documentOffset, "");		
		this.invocationTarget = invocationTarget;
	}

	public List getProposals() {
		List proposals = new ArrayList();
		
		OverloadedFunctionSet functionSet = (OverloadedFunctionSet) invocationTarget.getAttributeFromName(Name.OVERLOADED_FUNCTION_SET);
		if(functionSet != null) {
			for(Iterator iter = functionSet.getNestedFunctionBindings().iterator(); iter.hasNext();) {
				IBinding nextFunc = (IBinding) iter.next();
				addProposal(proposals, nextFunc);
			}			
		}
		else {		
			addProposal(proposals, invocationTarget.resolveTypeBinding());
		}
		
		return proposals;
	}

	private void addProposal(List proposals, IBinding binding) {
		Object proposal = createProposal(binding);
		if(proposal != null) {
			proposals.add(proposal);
		}
	}

	private Object createProposal(IBinding binding) {
		if(Binding.isValidBinding(binding)) {
			if(binding.isFunctionBinding()) {
				return createProposal((IFunctionBinding) binding); 
			}
			else if(binding.isDataBinding()) {
				IDataBinding dBinding = (IDataBinding) binding;
				switch(dBinding.getKind()) {
					case IDataBinding.NESTED_FUNCTION_BINDING:
						return createProposal((IFunctionBinding) dBinding.getType());
					case IDataBinding.CONSTRUCTOR_BINDING:
						ConstructorBinding cBinding = (ConstructorBinding) dBinding;
						return createProposal(cBinding.getCaseSensitiveName(), cBinding.getParameters(), null, cBinding.getDeclaringPart().getCaseSensitiveName());
				}
			}
			else if(binding.isTypeBinding()) {
				ITypeBinding tBinding = (ITypeBinding) binding;
				switch(tBinding.getKind()) {
					case ITypeBinding.DELEGATE_BINDING:
						DelegateBinding delBinding = (DelegateBinding) tBinding;
						return createProposal(delBinding.getCaseSensitiveName(), delBinding.getParemeters(), delBinding.getReturnType(), getPackageName(delBinding));
				}
			}
		}
		return null;
	}
	
	private Object createProposal(IFunctionBinding fBinding) {
		IPartBinding declarer = fBinding.getDeclarer();
		String declarerName = null;
		if(declarer == null) {
			if(invocationTarget instanceof QualifiedName) {
				declarerName = ((QualifiedName) invocationTarget).getQualifier().getCanonicalName();
			}
		}
		else {
			declarerName = declarer == fBinding ? getPackageName(declarer) : declarer.getCaseSensitiveName();
		}
		return createProposal(fBinding.getCaseSensitiveName(), fBinding.getParameters(), fBinding.getReturnType(), declarerName);
	}

	private Object createProposal(String funcName, List parameters, ITypeBinding returnType, String declarerName) {
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
			proposalSB.append(StatementValidator.getTypeString(returnType));
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
