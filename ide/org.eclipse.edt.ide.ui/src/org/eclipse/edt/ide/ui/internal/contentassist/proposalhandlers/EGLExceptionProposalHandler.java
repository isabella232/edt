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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.util.ExceptionHandler;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLExceptionProposalHandler extends EGLAbstractProposalHandler {

	public EGLExceptionProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	public EGLExceptionProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals() {
		List proposals = new ArrayList();
		EDTCoreIDEPlugin.getPlugin();		//This will make sure the system packages are initialized
		
		Collection exceptions = ExceptionHandler.getFilteredSystemExceptions(annoTypeMgr);
		for (Iterator iter = exceptions.iterator(); iter.hasNext();) {
			Binding binding = (Binding) iter.next();
			if (binding.getName().toUpperCase().startsWith(getPrefix().toUpperCase()))
				proposals.add(createProposal(binding));
		}
		
		//add anyexception
		addAnyException(proposals);
		return proposals;

	}
	
	/*
	 * The edt anyexception is not a IEGLConstants.RECORD_SUBTYPE_EXCEPTION(all exceptions are this kind before) kind,  in order to reduce the impact, the anyexception was added to the proposals directly,
	 * if the system package was changed, the function should be changed. 
	 */
	private  void addAnyException(List proposals){
		proposals.add(new EGLCompletionProposal(viewer,
				"anyexception - egl.lang (record)",
				"anyexception",
				UINlsStrings.CAProposal_Exception,
				getDocumentOffset() - getPrefix().length(),
				getPrefix().length(),
				"anyexception".length(),
				EGLCompletionProposal.RELEVANCE_EXCEPTION));
	}

	private EGLCompletionProposal createProposal(Binding binding) {
		String exceptionName = binding.getCaseSensitiveName();
		String proposalString = getProposalString(exceptionName);
		return
			new EGLCompletionProposal(viewer,
				getDisplayString(binding),
				proposalString,
				UINlsStrings.CAProposal_Exception,
				getDocumentOffset() - getPrefix().length(),
				getPrefix().length(),
				proposalString.length(),
				EGLCompletionProposal.RELEVANCE_EXCEPTION);
	}

	protected String getProposalString(String exceptionName) {
		return exceptionName;
	}

	private String getDisplayString(Binding binding) {
		String packageName[] = ((FlexibleRecordBinding) binding).getPackageName();
		StringBuffer buffer = new StringBuffer();
		buffer.append(binding.getCaseSensitiveName());
		buffer.append(" - "); //$NON-NLS-1$
		boolean first = true;
		for (int i = 0; i < packageName.length; i++) {
			if (!first) buffer.append("."); //$NON-NLS-1$
			buffer.append(packageName[i]);
			first = false;
		}
		buffer.append(" ("); //$NON-NLS-1$
		buffer.append(IEGLConstants.KEYWORD_RECORD);
		buffer.append(")"); //$NON-NLS-1$
		return buffer.toString();
	}
}
