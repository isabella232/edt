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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLUseStatementProposalHandler extends EGLAbstractProposalHandler {
	
	public EGLUseStatementProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	public List getProposals() {
		List proposals = new ArrayList();
		int partTypes = getTypes(getDocumentOffset(), getViewer());
		if (partTypes == 0)
			return proposals;
		List types = new ArrayList();
		try {
			new SearchEngine().searchAllPartNames(ResourcesPlugin.getWorkspace(),
				null,
				getPrefix().toCharArray(),
				IIndexConstants.PREFIX_MATCH,
				IEGLSearchConstants.CASE_INSENSITIVE,
				partTypes,
				createProjectSearchScope(),
				new PartInfoRequestor(types),
				IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				null);
		} catch (EGLModelException e) {
			EGLLogger.log(this, e);
		}
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			PartDeclarationInfo part = (PartDeclarationInfo) iter.next();
			
			if(canBeUsedByActivePart(part)){
				String partTypeName;
				switch (part.getPartType()) {
					case IEGLSearchConstants.TABLE :
						partTypeName = IEGLConstants.KEYWORD_DATATABLE;
						break;
					case IEGLSearchConstants.FORMGROUP :
						partTypeName = IEGLConstants.KEYWORD_FORMGROUP;
						break;
					case IEGLSearchConstants.LIBRARY :
						partTypeName = IEGLConstants.KEYWORD_LIBRARY;
						break;
					case IEGLSearchConstants.FORM :
						partTypeName = IEGLConstants.KEYWORD_FORM;
						break;
					case IEGLSearchConstants.HANDLER :
						partTypeName = IEGLConstants.KEYWORD_HANDLER;
						break;
					default :
						partTypeName = ""; //$NON-NLS-1$
						break;
				}
				proposals.add(createPartProposal(part, partTypeName));
			}	

		}
		return proposals;
	}
	
	private boolean canBeUsedByActivePart(PartDeclarationInfo partDeclareInfo){
		
		IEGLDocument document = (IEGLDocument) viewer.getDocument();
		if(null  == ((EGLDocument)document).getNewModelEGLFile().getPackageDeclaration()){
			return true;
		}
		//Part not in default package can't use library in default package.
		if(null != partDeclareInfo.getPackageName() && partDeclareInfo.getPackageName().length() > 0){
			return true;
		}
		
		return false;
	}

	private int getTypes(int documentOffset, ITextViewer viewer) {
		IEGLDocument document = (IEGLDocument) viewer.getDocument();
		final int[] result = new int[] {0};
		Node eglPart = EGLModelUtility.getPartNode(document, documentOffset);
		eglPart.accept(new DefaultASTVisitor() {
			public void endVisit(Program program) {
				result[0] = IEGLSearchConstants.TABLE | IEGLSearchConstants.FORMGROUP | IEGLSearchConstants.LIBRARY;
			}
			
			public void endVisit(Library library) {
				result[0] = IEGLSearchConstants.TABLE | IEGLSearchConstants.FORMGROUP | IEGLSearchConstants.LIBRARY;
			}
			
			public void endVisit(Handler handler) {
				result[0] = IEGLSearchConstants.TABLE | IEGLSearchConstants.LIBRARY;
			}
			
			public void endVisit(Service service) {
				result[0] = IEGLSearchConstants.TABLE | IEGLSearchConstants.LIBRARY;
			}
			
			public void endVisit(FormGroup formGroup) {
				result[0] = IEGLSearchConstants.FORM;
			}
		});		 
		return result[0];
	}
}
