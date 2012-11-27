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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLVariableDotProposalHandler extends EGLAbstractProposalHandler {
	private Type qualifierType;
	private boolean isVariable;

	public EGLVariableDotProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		Type qualifierType,
		boolean isVariable) {
			
			super(viewer, documentOffset, prefix, editor);
			this.qualifierType = qualifierType;
			this.isVariable = isVariable;
	}

	public List getProposals(boolean includeFunctions) {
		return getProposals(includeFunctions, new ArrayList<Field>());
	}
		
	public List getProposals(boolean includeFunctions, List<Field> excludeFields) {
		List result = new ArrayList();
		if(qualifierType != null && qualifierType.getClassifier() != null) {
			List list;
			if (qualifierType.getClassifier() instanceof Enumeration) {
				Enumeration enm = (Enumeration) qualifierType.getClassifier();
				list = enm.getAllMembers();
			}
			else {
				list = BindingUtil.getAllFields(qualifierType.getClassifier());
			}
			list = filterPrivateMembers(qualifierType.getClassifier(), list);
			list = filterExcludedFields(excludeFields, list);
			if (!isVariable) {
				list = filterStaticMembers(list);
			}
			result.addAll(getFieldProposals(list));
			if(includeFunctions) {
				list = BindingUtil.getAllFunctions(qualifierType.getClassifier());
				list = filterPrivateMembers(qualifierType.getClassifier(), list);
				if (!isVariable && !(qualifierType.getClassifier() instanceof Service)) {
					list = filterStaticMembers(list);
				}
				result.addAll(getFunctionProposals(
					list,
					UINlsStrings.bind(UINlsStrings.CAProposal_LibraryFunction, getTypeString(qualifierType)),
					EGLCompletionProposal.RELEVANCE_MEMBER,
					true));
			}
			
			return result;
				
		}
		return Collections.EMPTY_LIST;
	}
	
	private List filterExcludedFields(List<Field> exclude, List list) {
		List newList = new ArrayList();
		for (Object obj : list) {
			if (!exclude.contains(obj)) {
				newList.add(obj);
			}
		}
		return newList;
	}

	private List filterPrivateMembers(Classifier classifier, List list) {
		List newList = new ArrayList();
		for (Object obj : list) {
			Member mbr = (Member) obj;
			if (mbr.getAccessKind() != AccessKind.ACC_PRIVATE || mbr.getContainer() == classifier) {
				newList.add(mbr);
			}
		}
		return newList;
	}
	
	
	private List filterStaticMembers(List members) {
		List filteredList = new ArrayList();
		for(Iterator iter = members.iterator(); iter.hasNext();) {
			Member next = (Member) iter.next();
			if (next.isStatic()) {
				filteredList.add(next);
			}
		}
		return filteredList;
	}

	private List getFieldProposals(List<Member> fields, String ImgKeyStr) {
		List result = new ArrayList();
		String fieldImgKeyStr = ImgKeyStr == "" ? PluginImages.IMG_OBJS_ENV_VAR : ImgKeyStr;

		for(Member field : fields) {
				String proposalString = field.getCaseSensitiveName();
				if (proposalString.toUpperCase().startsWith(getPrefix().toUpperCase())) {
						String displayString = proposalString + " : " + getTypeString(field.getType()) +  " - " + getNameFromElement(field.getContainer());	//$NON-NLS-1$;
						result.add(new EGLCompletionProposal(viewer,
												displayString,
												proposalString,
												getAdditionalInfo(field),
												getDocumentOffset() - getPrefix().length(),
												getPrefix().length(),
												proposalString.length(),
												EGLCompletionProposal.RELEVANCE_MEMBER-1,
												fieldImgKeyStr));
			}
		}
		return result;
	}
	
	private List getFieldProposals(List<Member> fields) {
		return this.getFieldProposals(fields, "");
	}

	private List getFunctionProposals(List<Function> functions, String additionalInformation, int relevance) {
		return getFunctionProposals(functions, additionalInformation, relevance, false);
	}

	private List getFunctionProposals(List<Function> functions, String additionalInformation, int relevance, boolean returnPrivateFunctions) {
		List result = new ArrayList();
		for(Function function : functions) {
			//Adding this check on 10/04/2006 for RATLC01129262.  From what I can tell private functions should never
			//be returned.  If I am wrong, need to parameterize a boolean to determine whether or not to return private functions.
			if (returnPrivateFunctions || function.getAccessKind() != AccessKind.ACC_PRIVATE) {
				if (function.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
					result.addAll(createFunctionInvocationProposals(function, additionalInformation, relevance, false));
				}
			}
		}
		return result;
	}

}
