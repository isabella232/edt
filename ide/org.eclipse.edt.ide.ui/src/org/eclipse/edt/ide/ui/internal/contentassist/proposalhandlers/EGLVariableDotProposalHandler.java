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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLVariableDotProposalHandler extends EGLAbstractProposalHandler {
	private Type qualifierType;
	private Expression qualifierExpression;
	private boolean isVariable;

	public EGLVariableDotProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		Type qualifierType,
		boolean isVariable,
		Expression qualifierExpression) {
			
			super(viewer, documentOffset, prefix, editor);
			this.qualifierType = qualifierType;
			this.qualifierExpression = qualifierExpression;
			this.isVariable = isVariable;
	}

	public List getProposals(List propertyBlockList) {
		return getProposals(true, propertyBlockList);
	}

	public List getProposals(boolean includeFunctions, List propertyBlockList) {
		return getProposals(includeFunctions, false, false, propertyBlockList);
	}
	
	public List getProposals(boolean includeFunctions, boolean addEquals, List propertyBlockList) {
		return getProposals(includeFunctions, addEquals, false, propertyBlockList);
	}
	
	public List getProposals(boolean includeFunctions, boolean addEquals, boolean includePrivateFields, List propertyBlockList) {
		List result = new ArrayList();
		if(qualifierType != null && qualifierType.getClassifier() != null) {
			
			List list = BindingUtil.getAllFields(qualifierType.getClassifier());
			if (!isVariable) {
				list = filterStaticMembers(list);
			}
			result.addAll(getFieldProposals(list, addEquals, includePrivateFields, propertyBlockList));
			if(includeFunctions) {
				list = BindingUtil.getAllFunctions(qualifierType.getClassifier());
				if (!isVariable) {
					list = filterStaticMembers(list);
				}
				result.addAll(getFunctionProposals(
					list,
					UINlsStrings.bind(UINlsStrings.CAProposal_LibraryFunction, getTypeString(qualifierType)),
					EGLCompletionProposal.RELEVANCE_MEMBER));
			}
			
			return result;
				
		}
		return Collections.EMPTY_LIST;
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

	private List getFieldProposals(List<Field> fields, boolean addEquals, boolean includePrivateFields, List propertyBlockList, String ImgKeyStr) {
		List result = new ArrayList();
		String fieldImgKeyStr = ImgKeyStr == "" ? PluginImages.IMG_OBJS_ENV_VAR : ImgKeyStr;

		for(Field field : fields) {
			if(!includePrivateFields&& field.getAccessKind() == AccessKind.ACC_PRIVATE && !(qualifierExpression instanceof ThisExpression)) {
				continue;
			}
				String proposalString = field.getCaseSensitiveName();
				if (proposalString.toUpperCase().startsWith(getPrefix().toUpperCase())) {
						String displayString = proposalString + " : " + getTypeString(field.getType()) +  " - " + getNameFromElement(field.getContainer());	//$NON-NLS-1$;
						if (!containsProperty(proposalString, propertyBlockList)) {
							if (addEquals) {
								if (needSetFunctionForTheField(field)) {
									proposalString = proposalString + " ::= "; //$NON-NLS-1$
								} else {
									proposalString = proposalString + " = "; //$NON-NLS-1$
								}
							}
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
		}
		return result;
	}
	
	private List getFieldProposals(List<Field> fields, boolean addEquals, boolean includePrivateFields, List propertyBlockList) {
		return this.getFieldProposals(fields, addEquals, includePrivateFields, propertyBlockList, "");
	}
	
	private List getFunctionProposals(List<Function> functions, String additionalInformation, int relevance) {
		List result = new ArrayList();
		for(Function function : functions) {
			//Adding this check on 10/04/2006 for RATLC01129262.  From what I can tell private functions should never
			//be returned.  If I am wrong, need to parameterize a boolean to determine whether or not to return private functions.
			if (function.getAccessKind() != AccessKind.ACC_PRIVATE) {
				if (function.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
					result.addAll(createFunctionInvocationProposals(function, additionalInformation, relevance, false));
				}
			}
		}
		return result;
	}

	public static boolean needSetFunctionForTheField(Field field){
		for (Annotation ann : field.getAnnotations()) {
			if ((ann.getEClass() != null && (ann.getEClass().getName().equalsIgnoreCase("Property") || 
					ann.getEClass().getName().equalsIgnoreCase("EGLProperty")))) {
				Object value = ann.getValue(org.eclipse.edt.compiler.core.IEGLConstants.PROPERTY_SETMETHOD);
				if(value != null && value.equals("")){
					return true;
				}
			}
		}
		
		return(false);
	}

}
