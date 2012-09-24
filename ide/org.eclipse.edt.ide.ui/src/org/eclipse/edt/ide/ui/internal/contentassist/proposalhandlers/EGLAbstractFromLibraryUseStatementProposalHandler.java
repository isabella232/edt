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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public abstract class EGLAbstractFromLibraryUseStatementProposalHandler extends EGLAbstractProposalHandler {
	protected boolean mustHaveReturnCode;
	protected Node functionContainerPart;
	protected Set<String> containerPartFunctions = new HashSet<String>();

	public EGLAbstractFromLibraryUseStatementProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor, boolean mustHaveReturnCode, Node boundNode) {
		super(viewer, documentOffset, prefix, editor);
		this.mustHaveReturnCode = mustHaveReturnCode;
		
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof NestedFunction) {
				functionContainerPart = boundNode.getParent();
			}
			boundNode = boundNode.getParent();
		}
	}
	
	public List getProposals() {
		final Set librares = new HashSet();
		if(functionContainerPart != null) {
			functionContainerPart.accept(new AbstractASTPartVisitor() {
				public void visitPart(Part part) {
					for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
						((Node) iter.next()).accept(new DefaultASTVisitor() {
							public boolean visit(UseStatement useStatement) {
								for(Iterator iter = useStatement.getNames().iterator(); iter.hasNext();) {
									Name nextName = (Name) iter.next();
									Type type = nextName.resolveType();
									if(type instanceof Library ) {
										librares.add(type);
									}
								}
								return false;
							}
							
							public boolean visit(NestedFunction nestedFunction) {
								containerPartFunctions.add(nestedFunction.getName().getCaseSensitiveIdentifier().toLowerCase());
								return false;
							}
						});
					}
				}
			});
		}
		
		List proposals = new ArrayList();
		
		Library[] libraryContexts = (Library[]) librares.toArray(new Library[0]);
		for( int i = 0; i < libraryContexts.length; i++ ) {
			proposals.addAll(getProposals(libraryContexts, i));
		}
		
		return proposals;
	}

	protected abstract List getProposals(Library[] libraryContexts, int i);

}
