/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import org.eclipse.osgi.util.NLS;

public class EGLSearchMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.ui.internal.search.EGLSearchMessages"; //$NON-NLS-1$

	private EGLSearchMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, EGLSearchMessages.class);
	}

	public static String EGLSearchPageSearchForLabel;
	public static String EGLSearchPageSearchForProgram;
	public static String EGLSearchPageSearchForDelegate;
	public static String EGLSearchPageSearchForExternalType;
	public static String EGLSearchPageSearchForFunction;
	public static String EGLSearchPageSearchForPagehandler;
	public static String EGLSearchPageSearchForReporthandler;
	public static String EGLSearchPageSearchForLibrary;
	public static String EGLSearchPageSearchForDatatable;
	public static String EGLSearchPageSearchForRecord;
	public static String EGLSearchPageSearchForDataitem;
	public static String EGLSearchPageSearchForFormgroup;
	public static String EGLSearchPageSearchForForm;
	public static String EGLSearchPageSearchForService;
	public static String EGLSearchPageSearchForInterface;
	public static String EGLSearchPageSearchForAnnotation;
	public static String EGLSearchPageSearchForStereotype;
	public static String EGLSearchPageSearchForAny;

	public static String EGLSearchPageLimitToLabel;
	public static String EGLSearchPageLimitToDeclarations;
	public static String EGLSearchPageLimitToReferences;
	public static String EGLSearchPageLimitToAllOccurrences;

	public static String EGLSearchPageExpressionLabel;
	public static String EGLSearchPageExpressionCaseSensitive;

	public static String WorkspaceScope;
	public static String WorkingSetScope;
	public static String SelectionScope;
	public static String PartsScope;

	public static String EnclosingProjectsScope;
	public static String EnclosingProjectScope;

	public static String EGLSearchOperationDefault_package;
	public static String SearchResultCollectorMatch;
	public static String SearchResultCollectorMatches;
	public static String SearchResultCollectorDone;

	public static String EGLSearchQuerySearchfor_references;
	public static String EGLSearchQuerySearchfor_declarations;
	public static String EGLSearchQuerySearch_label;
	public static String EGLSearchQueryStatusOkMessage;

	public static String EGLSearchResultLabelProviderRemoved_resourceLabel;
	public static String EGLSearchResultLabelProviderCountFormat;

	public static String EGLSearchResultPageSort_nameLabel;
	public static String EGLSearchResultPageSort_pathLabel;
	public static String EGLSearchResultPageErrorMarker;
	public static String EGLSearchResultPageSort_byLabel;

	public static String EGLSearchActionReference;
	public static String EGLSearchActionDeclaration;
	public static String EGLSearchActionWorkspace;
	public static String EGLSearchActionProject;
	public static String EGLSearchActionWorkingset;
	public static String EGLSearchActionPartslist;

	public static String EGLPRVFindDialogButtonFind;
	public static String EGLPRVFindDialogButtonClose;

	public static String EGLPRVFindDialogTitle;
	public static String EGLPRVFindDialogForwardLabel;
	public static String EGLPRVFindDialogBackwordLabel;
	public static String EGLPRVFindDialogDirectionLabel;
	public static String EGLPRVFindDialogOptionsLabel;
	public static String EGLPRVFindDialogOptionsCasesensitiveLabel;
	public static String EGLPRVFindDialogOptionsWrapsearchLabel;
	public static String EGLPRVFindDialogOptionsWholewordLabel;
	public static String EGLPRVFindDialogTypeTextLabel;
	public static String EGLPRVFindDialogTypePartLabel;
	public static String EGLPRVFindDialogTypeLabel;
	public static String EGLPRVFindDialogParntnotfoundLabel;

	public static String SearchPotentialMatchDialogTitleFoundPotentialMatch;
	public static String SearchPotentialMatchDialogTitleFoundPotentialMatches;
	public static String SearchPotentialMatchDialogMessage;

	public static String SearchErrorCreateEGLElementTitle;
	public static String SearchErrorCreateEGLElementMessage;

	public static String EGLElementActionOperationUnavailableTitle;
	public static String EGLElementActionOperationUnavailableGeneric;

	public static String EGLSearchOperation_singularDeclarationsPostfix;
	public static String EGLSearchOperation_singularReferencesPostfix;
	public static String EGLSearchOperation_singularOccurrencesPostfix;

	public static String EGLSearchOperation_pluralDeclarationsPostfix;
	public static String EGLSearchOperation_pluralReferencesPostfix;
	public static String EGLSearchOperation_pluralOccurrencesPostfix;

	public static String FileSearchQuery_pluralPattern;

	public static String FileSearchQuery_singularLabel;

	public static String Search_Error_openEditor_title;
	public static String Search_Error_openEditor_message;

	public static String Search_Error_search_title;
	public static String Search_Error_search_message;
	
	public static String EGLSearchResultLabelProviderDefaultPackage;

}
