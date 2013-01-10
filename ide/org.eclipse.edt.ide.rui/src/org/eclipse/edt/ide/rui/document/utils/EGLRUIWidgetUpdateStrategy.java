/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.jface.text.BadLocationException;

public class EGLRUIWidgetUpdateStrategy {
	
	private org.eclipse.edt.compiler.core.ast.File fileAST;
	private IEGLDocument currentDocument;
	private Handler handler;
	private int theCharactersAdded = 0;
	
	public EGLRUIWidgetUpdateStrategy(Handler handler, IEGLDocument document){
		this.currentDocument  = document;
		this.fileAST = currentDocument.getNewModelEGLFile();
		this.handler = handler;
	}
	
	public int insertTargetWidget(final String insertText){
		SettingsBlockLocator locator = new SettingsBlockLocator();
		handler.accept(locator);
		if(locator.getSettingsBlock() != null){
			locator.getSettingsBlock().accept(new DefaultASTVisitor(){
				public boolean visit(SettingsBlock settingsBlock) {
					try{
						ASTRewrite rewrite = ASTRewrite.create(fileAST);
						String childrenString = "targetWidget = " + insertText;
						
						if ( settingsBlock.getSettings().size() > 0 ) {
							Node firstNode = (Node)settingsBlock.getSettings().get(0);
							String nodeText = currentDocument.get(firstNode.getOffset(), settingsBlock.getLength() - (firstNode.getOffset() - settingsBlock.getOffset()) );
							childrenString = "{" + childrenString + ", " + nodeText;
						} else {
							childrenString = "{" + childrenString + "}";
						}
						rewrite.setText(settingsBlock, childrenString);
						rewrite.rewriteAST(currentDocument).apply( currentDocument);
						theCharactersAdded = childrenString.length();
					}catch(BadLocationException e){
						Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Handler Update: Error creating initialUI field", e));
					}
					return false;
				}
			});
		}else{
			try{
				ASTRewrite rewrite = ASTRewrite.create(fileAST);
				String subTypeText = currentDocument.get(handler.getSubType().getOffset(), handler.getSubType().getLength());
				String childrenString = subTypeText + " {targetWidget =  " + insertText + "}";
				rewrite.setText(handler.getSubType(), childrenString);
				rewrite.rewriteAST(currentDocument).apply( currentDocument);;
				theCharactersAdded = childrenString.length();
			}catch(BadLocationException e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Handler Update: Error creating settings block", e));
			}
		}
		return theCharactersAdded;
	}
}
