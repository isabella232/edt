/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLFunctionPartSearchProposalHandler extends EGLPartSearchProposalHandler {
//	private boolean mustHaveReturnCode;
	private boolean referencedFunctions;
	Node functionContainerPart;
	
	/**
	 * @param documentOffset
	 * @param prefix
	 * @param viewer
	 * @param editor
	 */
	public EGLFunctionPartSearchProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor, boolean mustHaveReturnCode, Node boundNode) {
		super(viewer, documentOffset, prefix, editor);
		
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof NestedFunction) {
				functionContainerPart = boundNode.getParent();
			}
			boundNode = boundNode.getParent();
		}
		
		if(functionContainerPart != null) {
			IAnnotationBinding aBinding = ((Part) functionContainerPart).getName().resolveBinding().getAnnotation(EGLLANG, IEGLConstants.PROPERTY_INCLUDEREFERENCEDFUNCTIONS);
			referencedFunctions = aBinding != null && Boolean.YES == aBinding.getValue();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler#getPartType(com.ibm.etools.egl.model.internal.core.search.PartDeclarationInfo)
	 */
	protected String getPartType(PartDeclarationInfo part) {
		return IEGLConstants.KEYWORD_FUNCTION;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler#createProposal(com.ibm.etools.egl.model.internal.core.search.PartDeclarationInfo)
	 * 
	 * return null if do not want to give this proposal
	 */
	protected EGLCompletionProposal createPartProposal(PartDeclarationInfo partDeclarationInfo, String partTypeName) {
		String packageName = getPackageName(partDeclarationInfo);
		String displayString = partDeclarationInfo.getPartName() + " - " + packageName; //$NON-NLS-1$
		String proposalString = getProposalString(partDeclarationInfo, false);
		Point selection = getFirstParmSelection(proposalString);

		EGLCompletionProposal eglCompletionProposal =
			new EGLCompletionProposal(viewer,
				displayString + " (function)", //$NON-NLS-1$
				proposalString,
				getPartReferenceAdditionalInformation(packageName, partDeclarationInfo, partTypeName),
				getDocumentOffset() - getPrefix().length(),
				getPrefix().length(),
				//JON60 - temp -1 until get fct parms
				selection.x-1,
				EGLCompletionProposal.RELEVANCE_MEDIUM,
				selection.y,
				PluginImages.IMG_OBJS_FUNCTION);

		//Do not add import if default package
		if (partDeclarationInfo.getPackageName().length() > 0) {
			eglCompletionProposal.setImportPackageName(partDeclarationInfo.getPackageName());
			eglCompletionProposal.setImportPartName(partDeclarationInfo.getPartName());
		}

		return eglCompletionProposal;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler#createProposals(java.lang.String, java.util.List)
	 */
	protected List createProposals(List parts, boolean quoted) {
		List proposals = new ArrayList();
		if (referencedFunctions)
			proposals.addAll(super.createProposals(parts, quoted));
		proposals.addAll(getNestedFunctionProposals());
		return proposals;
	}

	/**
	 * add nested function proposals
	 */
	private List getNestedFunctionProposals() {
		List proposals = new ArrayList();
		if(functionContainerPart != null) {
			FunctionContainerBinding pBinding = (FunctionContainerBinding) ((Part) functionContainerPart).getName().resolveBinding();
			for(Iterator iter = pBinding.getDeclaredFunctions(true).iterator(); iter.hasNext();) {
				IFunctionBinding fBinding = (IFunctionBinding) ((IDataBinding) iter.next()).getType();
				
				String name = fBinding.getName();
				if (name.toUpperCase().startsWith(getPrefix().toUpperCase()))
					proposals.addAll(createFunctionInvocationProposals(fBinding, UINlsStrings.CAProposal_NestedFunction, EGLCompletionProposal.RELEVANCE_MEDIUM, false));				
			}
		}
		return proposals;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getProposalString(com.ibm.etools.egl.model.internal.core.search.PartDeclarationInfo)
	 */
	protected String getProposalString(PartDeclarationInfo partDeclarationInfo, boolean includePackageName) {
		StringBuffer buffer = new StringBuffer(partDeclarationInfo.getPartName());
		buffer.append("("); //$NON-NLS-1$
		buffer.append(")"); //$NON-NLS-1$

		return buffer.toString();
	}

	public List getProposals() {
		return super.getProposals(IEGLSearchConstants.FUNCTION);
	}

}
