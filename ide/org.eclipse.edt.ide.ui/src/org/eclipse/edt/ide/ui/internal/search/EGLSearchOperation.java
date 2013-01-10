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
package org.eclipse.edt.ide.ui.internal.search;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class EGLSearchOperation extends WorkspaceModifyOperation {
	
//	private IWorkspace fWorkspace;
	private IEGLElement fElementPattern;
	private int fLimitTo;
	private String fStringPattern;
//	private boolean fIsCaseSensitive;
//	private int fSearchFor;
//	private IEGLSearchScope fScope;
	private String fScopeDescription;
	private EGLSearchResultCollector fCollector;
	
	protected EGLSearchOperation(
				IWorkspace workspace,
				int limitTo,
				IEGLSearchScope scope,
				String scopeDescription,
				EGLSearchResultCollector collector) {
//		fWorkspace= workspace;
		fLimitTo= limitTo;
//		fScope= scope;
		fScopeDescription= scopeDescription;
		fCollector= collector;
//		fCollector.setOperation(this);
	}
	
	public EGLSearchOperation(
				IWorkspace workspace,
				IEGLElement pattern,
				int limitTo,
				IEGLSearchScope scope,
				String scopeDescription,
				EGLSearchResultCollector collector) {
		this(workspace, limitTo, scope, scopeDescription, collector);
		fElementPattern= pattern;
	}
	
	public EGLSearchOperation(
				IWorkspace workspace,
				String pattern,
				boolean caseSensitive,
				int searchFor, 
				int limitTo,
				IEGLSearchScope scope,
				String scopeDescription,
				EGLSearchResultCollector collector) {
		this(workspace, limitTo, scope, scopeDescription, collector);
		fStringPattern= pattern;
//		fIsCaseSensitive= caseSensitive;
//		fSearchFor= searchFor;
	}
	
	protected void execute(IProgressMonitor monitor) throws CoreException {
		fCollector.setProgressMonitor(monitor);
		
		// Also search working copies
//		SearchEngine engine= new SearchEngine(EGLUI.getSharedWorkingCopiesOnEGLPath());
		
//		if (fElementPattern != null)
//			engine.search(fWorkspace, fElementPattern, fLimitTo, fScope, fCollector);
//		else
//			engine.search(fWorkspace, SearchEngine.createSearchPattern(fStringPattern, fSearchFor, fLimitTo, fIsCaseSensitive), fScope, fCollector);
	}

	String getSingularLabel() {
		String desc= null;
		if (fElementPattern != null) {
			if (fLimitTo == IEGLSearchConstants.REFERENCES
			&& fElementPattern.getElementType() == IEGLElement.FUNCTION)
				desc= PrettySignature.getUnqualifiedFunctionSignature((IFunction)fElementPattern);
			else
				desc= fElementPattern.getElementName();
			if ("".equals(desc) && fElementPattern.getElementType() == IEGLElement.PACKAGE_FRAGMENT) //$NON-NLS-1$
				desc= EGLSearchMessages.EGLSearchOperationDefault_package;
		}
		else
			desc= fStringPattern;

		String[] args= new String[] {desc, fScopeDescription}; //$NON-NLS-1$
		switch (fLimitTo) {
			case IEGLSearchConstants.DECLARATIONS:
				return EGLSearchMessages.bind(EGLSearchMessages.EGLSearchOperation_singularDeclarationsPostfix, args);
			case IEGLSearchConstants.REFERENCES:
				return EGLSearchMessages.bind(EGLSearchMessages.EGLSearchOperation_singularReferencesPostfix, args);
			case IEGLSearchConstants.ALL_OCCURRENCES:
				return EGLSearchMessages.bind(EGLSearchMessages.EGLSearchOperation_singularOccurrencesPostfix, args);
			default:
				return EGLSearchMessages.bind(EGLSearchMessages.EGLSearchOperation_singularOccurrencesPostfix, args);
		}
	}

	String getPluralLabelPattern() {
		String desc= null;
		if (fElementPattern != null) {
			if (fLimitTo == IEGLSearchConstants.REFERENCES
			&& fElementPattern.getElementType() == IEGLElement.FUNCTION)
				desc= PrettySignature.getUnqualifiedFunctionSignature((IFunction)fElementPattern);
			else
				desc= fElementPattern.getElementName();
			if ("".equals(desc) && fElementPattern.getElementType() == IEGLElement.PACKAGE_FRAGMENT) //$NON-NLS-1$
				desc= EGLSearchMessages.EGLSearchOperationDefault_package;
		}
		else
			desc= fStringPattern;

		String[] args= new String[] {desc, "{0}", fScopeDescription}; //$NON-NLS-1$
		switch (fLimitTo) {
			case IEGLSearchConstants.DECLARATIONS:
				return EGLSearchMessages.bind(EGLSearchMessages.EGLSearchOperation_pluralDeclarationsPostfix, args);
			case IEGLSearchConstants.REFERENCES:
				return EGLSearchMessages.bind(EGLSearchMessages.EGLSearchOperation_pluralReferencesPostfix, args);
			case IEGLSearchConstants.ALL_OCCURRENCES:
				return EGLSearchMessages.bind(EGLSearchMessages.EGLSearchOperation_pluralOccurrencesPostfix, args);
			default:
				return EGLSearchMessages.bind(EGLSearchMessages.EGLSearchOperation_pluralOccurrencesPostfix, args);
		}
	}
	
	ImageDescriptor getImageDescriptor() {
		if (fLimitTo == IEGLSearchConstants.DECLARATIONS)
			return PluginImages.DESC_OBJS_SEARCH_DECL;
		else
			return PluginImages.DESC_OBJS_SEARCH_REF;
	}
}
