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
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLSubScriptSubStringModifierReferenceCompletion
		extends
			EGLAbstractReferenceCompletion {

	/**
	 * 
	 */
	public EGLSubScriptSubStringModifierReferenceCompletion() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() a=b["); //$NON-NLS-1$
		addContext("package a; function a() a=b[c,"); //$NON-NLS-1$
		addContext("package a; function a() a=b[c]["); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion#returnCompletionProposals(org.eclipse.edt.ide.core.internal.errors.ParseStack, java.lang.String, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		//see comments above about contexts with same state
		if (isState(parseStack, ((Integer) validStates.get(0)).intValue()) ||
			isState(parseStack, ((Integer) validStates.get(1)).intValue()) ||
			isState(parseStack, ((Integer) validStates.get(2)).intValue())) {
			getBoundASTNode(viewer, documentOffset, new String[] {"x]", "x", "", "x]=x;"}, new CompletedNodeVerifier() {
				public boolean nodeIsValid(Node astNode) {
					while(astNode != null) {
						if(astNode instanceof ArrayAccess || astNode instanceof SubstringAccess) {
							return true;
						}
						astNode = astNode.getParent();
					}
					return false;
				}
			}, new IBoundNodeProcessor() {
				public void processBoundNode(Node boundNode) {
					if(boundNode instanceof ArrayAccess) {
						IDataBinding data = ((ArrayAccess) boundNode).getArray().resolveDataBinding();
						ITypeBinding type = ((ArrayAccess) boundNode).getArray().resolveTypeBinding();
						if(type != null && IBinding.NOT_FOUND_BINDING != type) {
							if(ITypeBinding.ARRAY_TYPE_BINDING == type.getKind() ||
							   ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getKind() && !Primitive.isStringType(((PrimitiveTypeBinding) type).getPrimitive()) ||
							   data != null && IBinding.NOT_FOUND_BINDING != data && IDataBinding.STRUCTURE_ITEM_BINDING == data.getKind() && ((StructureItemBinding) data).isMultiplyOccuring()) {
								//Get all integer data item variable proposals
								proposals.addAll(
									new EGLDeclarationProposalHandler(viewer,
										documentOffset,
										prefix,
										boundNode)
											.getDataItemProposals(EGLDeclarationProposalHandler.INTEGER_DATAITEM));
							
								//Get all record variable proposals
								proposals.addAll(
									new EGLDeclarationProposalHandler(viewer,
										documentOffset,
										prefix,
										boundNode)
											.getRecordProposals(EGLDeclarationProposalHandler.ALL_RECORDS));
								return;
							}
						}
					}
					else if(boundNode instanceof SubstringAccess) {
						ITypeBinding type = ((SubstringAccess) boundNode).getPrimary().resolveTypeBinding();
						if(type != null && IBinding.NOT_FOUND_BINDING != type) {
							if(ITypeBinding.ARRAY_TYPE_BINDING == type.getKind() ||
							   ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getKind() && Primitive.isStringType(((PrimitiveTypeBinding) type).getPrimitive())) {
								//Get all integer data item variable proposals
								proposals.addAll(
									new EGLDeclarationProposalHandler(viewer,
										documentOffset,
										prefix,
										boundNode)
											.getDataItemProposals(EGLDeclarationProposalHandler.INTEGER_DATAITEM));
							
								//Get all record variable proposals
								proposals.addAll(
									new EGLDeclarationProposalHandler(viewer,
										documentOffset,
										prefix,
										boundNode)
											.getRecordProposals(EGLDeclarationProposalHandler.ALL_RECORDS));
								return;
							}
						}
					}
					boundNode = boundNode.getParent();							
				}
			});
		}
		
		return proposals;
	}
}
