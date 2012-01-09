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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLFormUseStatementProposalHandler extends EGLAbstractProposalHandler {
	private boolean parens;
	private Node functionContainerPart;
	private String formgroupName;

	//choices are:
	//	IEGLConstants.FORM_PRINT_FORM
	//	IEGLConstants.FORM_TEXT_FORM
	//	IEGLConstants.KEYWORD_FORM (both print & text)
	String formTypes = IEGLConstants.KEYWORD_FORM;  //default is both
		
	public EGLFormUseStatementProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor, Node boundNode) {
		super(viewer, documentOffset, prefix, editor);
		
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof NestedFunction) {
				functionContainerPart = boundNode.getParent();
			}
			else if(boundNode instanceof Part) {
				functionContainerPart = boundNode;
			}
			boundNode = boundNode.getParent();
		}
	}

	public List getProposals() {
		return getProposals(false, IEGLConstants.KEYWORD_FORM, false);
	}

	public List getProposals(boolean parens) {
		return getProposals(parens, IEGLConstants.KEYWORD_FORM, false);
	}

	public List getProposals(String formTypes) {
		return getProposals(false, formTypes, false);
	}

	public List getProposals(String formTypes, boolean quotes) {
		return getProposals(false, formTypes, quotes);
	}

	public List getProposals(boolean parens, String formTypes, boolean quotes) {
		this.parens = parens;
		this.formTypes = formTypes;
		List proposals = new ArrayList();
		final Set usedFormGroups = new HashSet();
		if(functionContainerPart != null) {
			functionContainerPart.accept(new AbstractASTPartVisitor() {
				public void visitPart(Part part) {
					for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
						((Node) iter.next()).accept(new DefaultASTVisitor() {
							public boolean visit(UseStatement useStatement) {
								for(Iterator iter = useStatement.getNames().iterator(); iter.hasNext();) {
									Name nextName = (Name) iter.next();
									IBinding binding = nextName.resolveBinding();
									if(binding != null && IBinding.NOT_FOUND_BINDING != binding) {
										if(binding.isTypeBinding()) {
											ITypeBinding formgroupBinding = null;
											if(ITypeBinding.FORMGROUP_BINDING == ((ITypeBinding) binding).getKind()) {
												formgroupBinding = (ITypeBinding) binding;
											}
											else if(ITypeBinding.FORM_BINDING == ((ITypeBinding) binding).getKind()) {
												if(nextName.isQualifiedName()) {
													binding = ((QualifiedName) nextName).getQualifier().resolveBinding();
													if(binding != null && IBinding.NOT_FOUND_BINDING != binding) {
														if(ITypeBinding.FORMGROUP_BINDING == ((ITypeBinding) binding).getKind()) {
															formgroupBinding = (ITypeBinding) binding;
														}
													}
												}
											}
										}										
									}
								}
								return false;
							}
						});
					}
				}
			});
		}
		
		for (Iterator iter = usedFormGroups.iterator(); iter.hasNext();) {
			FormGroupBinding formGroupTypeBinding = (FormGroupBinding) iter.next();
			formgroupName = formGroupTypeBinding.getCaseSensitiveName();
			
			List dataBindings = formGroupTypeBinding.getForms();
			for(Iterator iter2 = dataBindings.iterator(); iter2.hasNext();) {
				ITypeBinding formBinding = (ITypeBinding) iter2.next();
				if(ITypeBinding.FORM_BINDING == formBinding.getKind() && isRightFormType((FormBinding) formBinding))
					if (formBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase()))
						proposals.add(createProposal((FormBinding) formBinding, quotes));
			}
		}
		return proposals;
	}

	/**
	 * @return
	 */
	private boolean isRightFormType(FormBinding formBinding) {
		if (formTypes.equalsIgnoreCase(IEGLConstants.KEYWORD_FORM))
			return true;
		
		IBinding subType = formBinding.getSubType();
		if(subType != null) {
			String formTypeString = subType.getName();
			return formTypeString.equalsIgnoreCase(formTypes);
		}
		
		return false;
	}

	/**
	 * @param dataBinding
	 * @return
	 */
	private EGLCompletionProposal createProposal(FormBinding formBinding, boolean quotes) {
		String proposalString = getProposalString(formBinding, quotes);
		String displayString = formBinding.getCaseSensitiveName();

		return new EGLCompletionProposal(viewer,
						displayString + " (" + getPartTypeString(formBinding) + ")", //$NON-NLS-1$ //$NON-NLS-2$
						proposalString,
						getAdditionalInfo(formBinding),
						getDocumentOffset() - getPrefix().length(),
						getPrefix().length(),
						proposalString.length(),
						getPartTypeImgKeyStr(getPartTypeString(formBinding)));
	}

	protected String getProposalString(FormBinding formBinding, boolean quotes) {
		String proposalString = getProposalString(formBinding.getCaseSensitiveName());
		if (quotes)
			proposalString = "\"" + proposalString +"\""; //$NON-NLS-1$ //$NON-NLS-2$
		return proposalString;
	}

	/**
	 * @param string
	 * @return
	 */
	private String getProposalString(String string) {
		if (parens)
			return "(" + string + ");"; //$NON-NLS-1$ //$NON-NLS-2$
		return string;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getAdditionalInfo(com.ibm.etools.egl.internal.pgm.bindings.EGLTypeBinding)
	 */
	protected String getAdditionalInfo(FormBinding formBinding) {
		String formTypeString = IEGLConstants.KEYWORD_FORM.toLowerCase();
		
		IBinding subType = formBinding.getSubType();
		if(subType != null) {
			formTypeString = subType.getCaseSensitiveName();
		}
		return
			MessageFormat.format(
				UINlsStrings.CAProposal_UseDeclarationIn,
				new String[] { formTypeString, formgroupName});
	}

}
