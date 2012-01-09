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
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.EGLNewPropertiesHandler;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLNewSettingsBlockProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPropertyNameProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLVariableDotProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLPropertyNameReferenceCompletion extends EGLAbstractPropertyReferenceCompletion {
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; program a {"); //$NON-NLS-1$
		addContext("package a; program a { a = b, "); //$NON-NLS-1$
	}
	
	protected List returnCompletionProposals(
			ParseStack parseStack,
			final String prefix,
			final ITextViewer viewer,
			final int documentOffset) {
		this.viewer = viewer;
		this.documentOffset = documentOffset;

		final List proposals = new ArrayList();
		final List genericStereotypeProposals = new ArrayList();
		if (isState(parseStack, ((Integer) validStates.get(0)).intValue())
			&& !isState(parseStack, getState("package a; function a() new"))) { //$NON-NLS-1$

			newTypeBinding = null;
			//there are side effects of getLocation().  It also sets partLocation and propertyBlock
			int location = getLocation(new IBoundNodeProcessor() {
				public void processBoundNode(Node boundNode) {
					if (partLocation == EGLNewPropertiesHandler.locationNewExpression) {
						getBoundASTNode(viewer, documentOffset, new String[] {"", "a};", "a", "}};"}, new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							public boolean nodeIsValid(Node astNode) {
								boolean result = false;
								while(astNode != null && !result) {
									if(astNode instanceof SettingsBlock) {
										result = true;
									}
									astNode = astNode.getParent();
								}
								return result;
							}
						}, new IBoundNodeProcessor() {
							public void processBoundNode(Node boundNode) {
								Node node = boundNode.getParent();
								if(node instanceof Assignment) {
									node = node.getParent();
								}
								if(node instanceof SettingsBlock) {
									node = node.getParent();
								}
								final FunctionDataDeclaration functionDataDeclaration[] = new FunctionDataDeclaration[] {null};
								node.accept(new DefaultASTVisitor() {
									public boolean visit(FunctionDataDeclaration functionDataDecl) {
										functionDataDeclaration[0] = functionDataDecl;
										return false;
									}
								});
								if (functionDataDeclaration[0] != null) {
									Name name = ((NameType) functionDataDeclaration[0].getType()).getName();
									proposals.addAll(
											new EGLNewSettingsBlockProposalHandler(
												viewer,
												documentOffset,
												prefix).getProposals(settingsBlockList, name));
								}
							}
						});
					}
					if(newTypeBinding != null) {
						if(newTypeBinding instanceof AnnotationTypeBindingImpl) {
							proposals.addAll(
								new EGLPropertyNameProposalHandler(
									viewer,
									documentOffset,
									prefix, false).getProposals(createRulesForFields((AnnotationTypeBindingImpl) newTypeBinding), settingsBlockList));
						}
						else {
							IDataBinding dBinding = new ClassFieldBinding(newTypeBinding.getName(), null, newTypeBinding);
							proposals.addAll(
								new EGLVariableDotProposalHandler(
									viewer,
									documentOffset,
									prefix,
									editor,
									dBinding,
									newTypeBinding,
									null).getProposals(false, true, boundNode instanceof Part, settingsBlockList));
							
							if(boundNode instanceof Part) {
								IBinding binding = ((Part) boundNode).getName().resolveBinding();
								if(binding instanceof PartBinding) {
									IPartBinding partBinding = (IPartBinding) binding;
									IPartSubTypeAnnotationTypeBinding subType = partBinding.getSubType();
									if(Binding.isValidBinding(subType) && subType instanceof AnnotationTypeBindingImpl) {
										genericStereotypeProposals.addAll(
											new EGLPropertyNameProposalHandler(
												viewer,
												documentOffset,
												prefix, false).getProposals(createRulesForFields((AnnotationTypeBindingImpl) subType), settingsBlockList));
									}
								}
							}
						}
					}
				}
			});
			
			if(location != 0) {
				/*
				 * To handle cases where the class EGLNewPropertiesHandler does not know about a given
				 * subtype (eg. if the subtype were user-defined or not part of our system package.
				 */
				switch(location) {
					case EGLNewPropertiesHandler.locationAnyRecord:
					case EGLNewPropertiesHandler.locationHandler:
						proposals.addAll(genericStereotypeProposals);
				}
				proposals.addAll(
					new EGLPropertyNameProposalHandler(
						viewer,
						documentOffset,
						prefix, true).getProposals(location, settingsBlockList));
			}
		}
		return proposals;
	}

	private Collection createRulesForFields(AnnotationTypeBindingImpl binding) {
		List result = new ArrayList();
		for(Iterator iter = binding.getAnnotationRecord().getDeclaredFields().iterator(); iter.hasNext();) {
			result.add(new EGLPropertyRule((IDataBinding) iter.next()));
		}
		return result;
	}

	protected boolean isRecordDataDeclarationLocation(int location) {
		if (   location == EGLNewPropertiesHandler.locationStaticBasicRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationStaticIndexedRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationStaticMQRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationStaticRelativeRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationStaticSerialRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationStaticSQLRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationStaticVGUIRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationDynamicBasicRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationDynamicIndexedRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationDynamicMQRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationDynamicRelativeRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationDynamicSerialRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationDynamicSQLRecordDataDeclaration
			|| location == EGLNewPropertiesHandler.locationDynamicVGUIRecordDataDeclaration
			)
				return true;
		return false;
	}
}
