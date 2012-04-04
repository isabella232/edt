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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.internal.EGLNewPropertiesHandler;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ElementKind;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.util.CapabilityFilterUtility;
import org.eclipse.jface.text.ITextViewer;

public class EGLSubtypeReferenceCompletion extends EGLAbstractReferenceCompletion {
	String additionalInfoText = ""; //$NON-NLS-1$
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; record a type"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, String prefix, ITextViewer viewer, int documentOffset) {
		//plugin com.ibm.etools.edt.core.ide must be initialized for calls to EGLNewPropertiesHandler to work
		EDTCoreIDEPlugin.getPlugin();
		
		ArrayList result = new ArrayList();
		List subTypes = getSubtypes(viewer, documentOffset);
		if (subTypes != null) {
			int prefixLength = prefix.length();
			for (int i = 0; i < subTypes.size(); i++) {
				if (((String) subTypes.get(i)).toUpperCase().startsWith(prefix.toUpperCase()))
					result.add(
						new EGLCompletionProposal(viewer,
							null,
							(String) subTypes.get(i),
							additionalInfoText,
							documentOffset - prefixLength,
							prefixLength,
							((String) subTypes.get(i)).length(),
							EGLCompletionProposal.NO_IMG_KEY));
			}
		}
					
		return result;
	}
	
	private List getSubtypes(final ITextViewer viewer, final int documentOffset) {
		Node eglPart = getPart(viewer, documentOffset);
		if (eglPart == null)
			return null;
		
		final List elementKinds = new ArrayList();
		
		eglPart.accept(new DefaultASTVisitor() {
//			public void endVisit(Program program) {
//				additionalInfoText = UINlsStrings.CAProposal_ProgramType;
//				elementKinds.add(ElementKind.PROGRAMPART);
//			}
			
			public void endVisit(Record record) {
				additionalInfoText = UINlsStrings.CAProposal_RecordType;
				elementKinds.add(ElementKind.RECORDPART);
				elementKinds.add(ElementKind.STRUCTUREDRECORDPART);
				elementKinds.add(ElementKind.VGUIRECORDPART);
			}
				
			public void endVisit(DataTable table) {
				additionalInfoText = UINlsStrings.CAProposal_TableType;
				elementKinds.add(ElementKind.DATATABLEPART);
				
			}
			
			public void endVisit(Library library) {
				additionalInfoText = UINlsStrings.CAProposal_LibraryType;
				elementKinds.add(ElementKind.LIBRARYPART);
				
			}
			
			public void endVisit(Handler handler) {
				additionalInfoText = UINlsStrings.CAProposal_HandlerType;
				elementKinds.add(ElementKind.HANDLERPART);
				
			}
			
			public void endVisit(TopLevelForm form) {
				additionalInfoText = UINlsStrings.CAProposal_FormType;
				elementKinds.add(ElementKind.FORMPART);
				
			}

			public void endVisit(Interface iFace) {
				additionalInfoText = UINlsStrings.CAProposal_InterfaceType;
				elementKinds.add(ElementKind.INTERFACEPART);
			}
			
			public void endVisit(ExternalType externalType) {
				additionalInfoText = UINlsStrings.CAProposal_ExternalTypeType;
				elementKinds.add(ElementKind.EXTERNALTYPEPART);
			}			
			
			public void endVisit(FormGroup fGroup) {
				Node nestedPart = getNestedPart(viewer, documentOffset);
				nestedPart.accept(new DefaultASTVisitor() {
					public void endVisit(NestedForm nestedForm) {
						additionalInfoText = UINlsStrings.CAProposal_FormType;
						elementKinds.add(ElementKind.FORMPART);						
					}
				});
			}
		});
		
		Collection rules = CapabilityFilterUtility.filterPropertyRules(EGLNewPropertiesHandler.createRulesForSubtypes((EnumerationDataBinding[]) elementKinds.toArray(new EnumerationDataBinding[0])));
		List subTypes = new ArrayList();
		for(Iterator iter = rules.iterator(); iter.hasNext();) {
			subTypes.add(((EGLPropertyRule) iter.next()).getName());
		}
		return subTypes;
	}

}
