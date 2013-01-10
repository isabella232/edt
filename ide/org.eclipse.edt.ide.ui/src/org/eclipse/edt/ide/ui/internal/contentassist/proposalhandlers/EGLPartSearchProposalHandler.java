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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLPartSearchProposalHandler extends EGLAbstractProposalHandler {
	private String excludeName;
	private String[] subTypes;
	
	public EGLPartSearchProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	/*
	 * get proposals with nothing to exclude, all subTypes
	 */
	public List getProposals(int types) {
		return getProposals(types, "", new String[0]); //$NON-NLS-1$
	}

	/*
	 * Get the proposals based on all subTypes.
	 * Exclude any proposals that match excludeName.
	 * This is to prevent cases such as adding a structure item with a type
	 * the same as the record it is in (recursive)
	 */
	public List getProposals(int types, String excludeName) {
		return getProposals(types, excludeName, new String[0]);
	}

	/*
	 * Get the proposals based on the allowable type.
	 * restrict proposals to specific subtype
	 * Exclude any proposals that match excludeName.
	 * This is to prevent cases such as adding a structure item with a type
	 * the same as the record it is in (recursive)
	 */
	public List getProposals(int types, String excludeName, String subType) {
		return getProposals(types, excludeName, new String[] {subType});
	}

	/*
	 * Get the proposals based on the allowable types.
	 * restrict proposals to specific subtypes
	 * Exclude any proposals that match excludeName.
	 * This is to prevent cases such as adding a structure item with a type
	 * the same as the record it is in (recursive)
	 */
	public List getProposals(int types, String excludeName, String[] subTypes) {
		this.excludeName = excludeName;
		this.subTypes = subTypes;
		return createProposals(searchIndex(types), false);
	}

	/*
	 * Get the proposals based on the allowable types.
	 * restrict proposals to specific subtypes
	 * Exclude any proposals that match excludeName.
	 * This is to prevent cases such as adding a structure item with a type
	 * the same as the record it is in (recursive)
	 */
	public List getProposals(int types, String excludeName, String[] subTypes, boolean quoted) {
		this.excludeName = excludeName;
		this.subTypes = subTypes;
		return createProposals(searchIndex(types), quoted);
	}

	/*
	 * loop through the parts returned from the index search to create each of the proposals
	 */
	protected List createProposals(List parts, boolean quoted) {
		List proposals = new ArrayList();
		String currentFilePackageName;
		
		File eglFile = ((IEGLDocument) getViewer().getDocument()).getNewModelEGLFile();
		currentFilePackageName = eglFile.hasPackageDeclaration() ?
			eglFile.getPackageDeclaration().getName().getCanonicalName() :
			""; //$NON-NLS-1$
			
		for (Iterator iter = parts.iterator(); iter.hasNext();) {
			PartDeclarationInfo partDeclarationInfo = (PartDeclarationInfo) iter.next();
			if (partDeclarationInfo.getPartName().equalsIgnoreCase(excludeName))
				//do not add a proposal for this part
				continue;
			if (!validSubtype(partDeclarationInfo))
				continue;
			String partDeclarationPackageName = partDeclarationInfo.getPackageName();
			
			if (!shouldInclude(partDeclarationInfo)) {
				continue;
			}
			
			//If source is not in the default package, we don't want proposals for parts in the default package
			if(currentFilePackageName.length() == 0 || partDeclarationPackageName.length() != 0) {
				//Ignore private parts if not in the same package
				if (!currentFilePackageName.equalsIgnoreCase(partDeclarationPackageName)) {
					if (isPublic(partDeclarationInfo)) {
						EGLCompletionProposal proposal = createPartProposal(partDeclarationInfo, getPartType(partDeclarationInfo), quoted);
						if (proposal != null) proposals.add(proposal);
					}
				}
				else {
					EGLCompletionProposal proposal = createPartProposal(partDeclarationInfo, getPartType(partDeclarationInfo), quoted);
					if (proposal != null) proposals.add(proposal);
				}
			}
		}
		return proposals;
	}

	protected boolean validSubtype(PartDeclarationInfo partDeclarationInfo) {
		if (subTypes != null && subTypes.length > 0) {
			try {
				IPart part = partDeclarationInfo.resolvePart(createScope());
				if (part != null && (part instanceof SourcePart || part instanceof BinaryPart)) {
					IPart sourcePart = (IPart) part;
					String subTypeSignature = sourcePart.getSubTypeSignature();
					if (subTypeSignature != null) {
						String subtypeName = Signature.toString(subTypeSignature);
						if (subtypeName == null || !validSubtype(subtypeName)) {
							//do not add a proposal.  Only looking for specific subtypes
							return false;
						}
					}
					else
						return false;
				}
			} catch (EGLModelException e) {
				EGLLogger.log(this, e);
				return false;
			}
		}
		return true;
	}


	/**
	 * @param subtypeName
	 * @return
	 */
	private boolean validSubtype(String subtypeName) {
		for (int i = 0; i < subTypes.length; i++) {
			if (subTypes[i].equalsIgnoreCase(subtypeName))
				return true;
		}
		return false;
	}

	/*
	 * get the type of the part for the additional information
	 */
	protected String getPartType(PartDeclarationInfo part) {
		String partTypeName;
		switch (part.getPartType()) {
			case IEGLSearchConstants.PROGRAM :
				partTypeName = IEGLConstants.KEYWORD_PROGRAM;
				break;
			case IEGLSearchConstants.RECORD :
				partTypeName = IEGLConstants.KEYWORD_RECORD;
				break;
			case IEGLSearchConstants.FUNCTION :
				partTypeName = IEGLConstants.KEYWORD_FUNCTION;
				break;
			case IEGLSearchConstants.LIBRARY :
				partTypeName = IEGLConstants.KEYWORD_LIBRARY;
				break;
			case IEGLSearchConstants.HANDLER :
				partTypeName = IEGLConstants.KEYWORD_HANDLER;
				break;
			case IEGLSearchConstants.SERVICE :
				partTypeName = IEGLConstants.KEYWORD_SERVICE;
				break;
			case IEGLSearchConstants.INTERFACE :
				partTypeName = IEGLConstants.KEYWORD_INTERFACE;
				break;
			case IEGLSearchConstants.DELEGATE :
				partTypeName = IEGLConstants.KEYWORD_DELEGATE;
				break;
			case IEGLSearchConstants.EXTERNALTYPE :
				partTypeName = IEGLConstants.KEYWORD_EXTERNALTYPE;
				break;
			case IEGLSearchConstants.ENUMERATION :
				partTypeName = IEGLConstants.KEYWORD_ENUMERATION;
				break;
			case IEGLSearchConstants.CLASS :
				partTypeName = IEGLConstants.KEYWORD_CLASS;
				break;
			default :
				partTypeName = ""; //$NON-NLS-1$
				break;
		}
		return partTypeName;
	}

	/*
	 * search the index to find appropriate matches
	 */
	private List searchIndex(int types) {
		List parts = new ArrayList();
		try {
			new SearchEngine().searchAllPartNames(
				ResourcesPlugin.getWorkspace(),
				null,
				getPrefix().toCharArray(),
				IIndexConstants.PREFIX_MATCH,
				IEGLSearchConstants.CASE_INSENSITIVE,
				types,
				createScope(),
				new PartInfoRequestor(parts),
				IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				null);
		} catch (EGLModelException e) {
			EGLLogger.log(this, e);
		}
		return parts;
	}

	/*
	 * create the search scope
	 */
	protected IEGLSearchScope createScope() {
		return createProjectSearchScope();
	}
	/*
	 * place the cursor based on the text being pasted into the document
	 */
	protected int getCursorPosition(PartDeclarationInfo part) {
		return part.getPartName().length();
	}

	protected boolean isPublic(PartDeclarationInfo partInfo) {
		try {
			IPart type = (IPart) partInfo.resolvePart(createScope());
			if (type != null) return type.isPublic();
		} catch (EGLModelException e) {
			EGLLogger.log(this, e);
		} catch (NullPointerException e) {
			return false;
		}
		return false;
	}

	protected boolean shouldInclude(PartDeclarationInfo partDeclarationInfo) {
		return true;
	}
}
