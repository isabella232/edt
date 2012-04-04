/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.document.utils;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;

public class EGLRUIHandlerUpdateStrategy {
	
	private org.eclipse.edt.compiler.core.ast.File fileAST;
	private boolean foundInitialUI = false;
	private IEGLDocument currentDocument;
	private Handler handler;
	private int theCharactersAdded = 0;
	
	public EGLRUIHandlerUpdateStrategy(Handler handler, IEGLDocument document){
		this.currentDocument  = document;
		this.fileAST = currentDocument.getNewModelEGLFile();
		this.handler = handler;
	}
	
	public int updateHandler(final String insertText, final int index){
		SettingsBlockLocator locator = new SettingsBlockLocator();
		handler.accept(locator);
		if(locator.getSettingsBlock() != null){
			locator.getSettingsBlock().accept(new DefaultASTVisitor(){
				public boolean visit(SettingsBlock settingsBlock) {
					return true;
				}
				public void endVisit(SettingsBlock settingsBlock) {
					if(!foundInitialUI){
						try{
							ASTRewrite rewrite = ASTRewrite.create(fileAST);
							String childrenString = "{ initialUI = [ " + insertText + " ] }";
							rewrite.setText(settingsBlock, childrenString);
							rewrite.rewriteAST(currentDocument).apply(currentDocument);
							theCharactersAdded = childrenString.length();
						}catch(BadLocationException e){
							Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Handler Update: Error creating initialUI field", e));
						}
					}
				}
				public boolean visit(final Assignment assignment) {
					try{
						if(assignment.getLeftHandSide().isName() && InternUtil.intern("initialUI") == ((Name)assignment.getLeftHandSide()).getIdentifier()){
							foundInitialUI = true;
							assignment.getRightHandSide().accept(new DefaultASTVisitor(){
								public boolean visit(ArrayLiteral array){
									try{
										if(array.getExpressions().size() == 0){
											String childrenString = "initialUI = [ " + insertText + " ]";
											ASTRewrite rewrite = ASTRewrite.create(fileAST);
											rewrite.setText(assignment, childrenString);
											rewrite.rewriteAST(currentDocument).apply(currentDocument);
											theCharactersAdded = childrenString.length();
										}else{
											Node insertNode;
											String nodeText;
											if(index < array.getExpressions().size()){
												// insert into beginning or middle
												insertNode = (Node)array.getExpressions().get(index);
												nodeText = currentDocument.get(insertNode.getOffset(), insertNode.getLength());
												nodeText = insertText + ", " + nodeText;	
											}else{
												// insert at end
												insertNode = (Node)array.getExpressions().get(index - 1);
												nodeText = currentDocument.get(insertNode.getOffset(), insertNode.getLength());
												nodeText = nodeText + ", " + insertText;
											}														
											ASTRewrite rewrite = ASTRewrite.create(fileAST);
											rewrite.setText(insertNode, nodeText);
											rewrite.rewriteAST(currentDocument).apply(currentDocument);
											theCharactersAdded = nodeText.length();
										}
									}catch(BadLocationException e){
										Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Handler Update: Error updating initialUI field", e));
									}
									return false;
								}
								
							});
						}
					}catch(Exception e){
						Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Handler Update: Error updating initialUI field", e));
					}
					return false;
				}
			});
		}else{
			try{
				ASTRewrite rewrite = ASTRewrite.create(fileAST);
				String subTypeText = currentDocument.get(handler.getSubType().getOffset(), handler.getSubType().getLength());
				String childrenString = subTypeText + " { initialUI = [ " + insertText + " ] } ";
				rewrite.setText(handler.getSubType(), childrenString);
				rewrite.rewriteAST(currentDocument).apply(currentDocument);
				theCharactersAdded = childrenString.length();
			}catch(BadLocationException e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Handler Update: Error creating settings block", e));
			}
		}
		return theCharactersAdded;
	}
}
