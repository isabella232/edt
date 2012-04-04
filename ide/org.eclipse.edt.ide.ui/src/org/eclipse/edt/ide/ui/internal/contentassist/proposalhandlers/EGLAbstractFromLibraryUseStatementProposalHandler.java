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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.UseStatement;
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
									IBinding binding = nextName.resolveBinding();
									if(binding != null && IBinding.NOT_FOUND_BINDING != binding) {
										if(binding.isTypeBinding() && ITypeBinding.LIBRARY_BINDING == ((ITypeBinding) binding).getKind()) {
											librares.add(binding);
										}
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
		
		LibraryBinding[] libraryContexts = (LibraryBinding[]) librares.toArray(new LibraryBinding[0]);
		for( int i = 0; i < libraryContexts.length; i++ ) {
			proposals.addAll(getProposals(libraryContexts, i));
		}
		
		return proposals;
	}

	protected abstract List getProposals(LibraryBinding[] libraryContexts, int i);

}
