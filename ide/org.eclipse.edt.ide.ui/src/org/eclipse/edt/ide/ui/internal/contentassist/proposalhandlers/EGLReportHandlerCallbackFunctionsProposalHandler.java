/*******************************************************************************
 * Copyright Ã¦Â¼?2000, 2011 IBM Corporation and others.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.part.HandlerValidator;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLReportHandlerCallbackFunctionsProposalHandler extends EGLAbstractProposalHandler {
	
	private boolean isJasperReportHandler;
	private Set definedFunctionNames = new HashSet();

	public EGLReportHandlerCallbackFunctionsProposalHandler(ITextViewer viewer, int documentOffset, String prefix, Node boundNode) {
		super(viewer, documentOffset, prefix);
		
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof Handler) {
				isJasperReportHandler = ((Handler) boundNode).getName().resolveBinding().getAnnotation(EGLUIJASPER, IEGLConstants.HANDLER_SUBTYPE_JASPER) != null;
				((Handler) boundNode).accept(new DefaultASTVisitor() {
					public boolean visit(Handler handler) {
						return true;
					}
					
					public boolean visit(NestedFunction nestedFunction) {
						definedFunctionNames.add(nestedFunction.getName().getCanonicalName().toLowerCase());
						return false;
					}
				});
			}
			boundNode = boundNode.getParent();
		}
	}

	public List getProposals() {
		List proposals = new ArrayList();
		if(isJasperReportHandler) {
			IFunctionBinding[] jasperFunctions = HandlerValidator.JasperReportCallbackFunctions;
			for(int i = 0; i < jasperFunctions.length; i++) {
				IFunctionBinding function = jasperFunctions[i];
				String functionName = function.getCaseSensitiveName();
				if(!definedFunctionNames.contains(functionName.toLowerCase())) {
					if (functionName.toUpperCase().startsWith(getPrefix().toUpperCase())) {
						StringBuffer buffer = new StringBuffer(functionName);
						buffer.append("("); //$NON-NLS-1$
						for (Iterator iter = function.getParameters().iterator(); iter.hasNext();) {
							IDataBinding parm = (IDataBinding) iter.next();
							buffer.append(parm.getCaseSensitiveName());
							buffer.append(" "); //$NON-NLS-1$
							buffer.append(parm.getType().getName().toLowerCase());
							if (iter.hasNext()) buffer.append(", "); //$NON-NLS-1$
						}
						buffer.append(")"); //$NON-NLS-1$
						proposals.add(createProposal(getPrefix(), viewer, getDocumentOffset(), functionName, buffer.toString()));
					}
				}
			}
		}
		return proposals;
	}

	private Object createProposal(String prefix, ITextViewer viewer, int documentOffset, String functionName, String proposalText) {
		return
			new EGLCompletionProposal(viewer,
				functionName,
				proposalText,
				UINlsStrings.CAProposal_JasperReportHandlerFunction,
				documentOffset - prefix.length(),
				prefix.length(),
				proposalText.length());
	}

}
