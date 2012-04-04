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

import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;

public class EGLRUIHandlerDeleteStrategy {
	
	private int theCharactersRemoved = 0;
	private IEGLDocument currentDocument;
	private Handler handler;
	private String deletedWidgetName;
	
	public EGLRUIHandlerDeleteStrategy(Handler handler, IEGLDocument document){
		this.currentDocument  = document;
		this.handler = handler;
	}
	
	public int deleteFromHandler(final int index){
		SettingsBlockLocator locator = new SettingsBlockLocator();
		handler.accept(locator);
		if(locator.getSettingsBlock() != null){
			locator.getSettingsBlock().accept(new DefaultASTVisitor(){
				public boolean visit(SettingsBlock settingsBlock) {
					return true;
				}
				public boolean visit(final Assignment assignment) {
					try{
						if(assignment.getLeftHandSide().isName() && InternUtil.intern("initialUI") == ((Name)assignment.getLeftHandSide()).getIdentifier()){
							assignment.getRightHandSide().accept(new DefaultASTVisitor(){
								public boolean visit(ArrayLiteral arrayLiteral){
									List expressions = arrayLiteral.getExpressions();
									Node referenceNode = (Node)expressions.get(index);
									int referenceOffset = referenceNode.getOffset();
									int referenceLength = referenceNode.getLength();
									
									try{
										deletedWidgetName = currentDocument.get(referenceOffset, referenceLength);
									}catch(BadLocationException e){
										Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error deleting item from RUIHandler", e));
									}
									
									if(expressions.size() > 1){
										if(index == expressions.size() - 1){						
											// Removing the last element, need to remove a trailing comma from the previous element
											Node previousWidget = (Node)expressions.get(index - 1);
											try{
												theCharactersRemoved = (referenceOffset + referenceLength) - (previousWidget.getOffset() + previousWidget.getLength());
												currentDocument.replace(previousWidget.getOffset() + previousWidget.getLength(), theCharactersRemoved, "");
											}catch(BadLocationException e){
												Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error deleting last widget reference from RUIHandler", e));
											}
										}else{
											// Remove the element and a trailing comma
											Node nextWidget = (Node)expressions.get(index + 1);
											try{
												// Nodes do not offer a way to get their complete text, so we need to figure it out ourselves
												String nextWidgetText = currentDocument.get(referenceOffset, (nextWidget.getOffset() + nextWidget.getLength()) - referenceOffset);
												nextWidgetText = nextWidgetText.substring(nextWidgetText.indexOf(",", referenceLength) + 1, nextWidgetText.length()).trim();
												theCharactersRemoved = ((nextWidget.getOffset() + nextWidget.getLength()) - referenceOffset) - nextWidgetText.length();
												currentDocument.replace(referenceOffset, (nextWidget.getOffset() + nextWidget.getLength()) - referenceOffset, nextWidgetText);
											}catch(BadLocationException e){
												Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error deleting item from RUIHandler", e));
											}
										}
									}else{
										// Just remove the node
										try{
											theCharactersRemoved = referenceLength;
											currentDocument.replace(referenceOffset, referenceLength, "");
										}catch(BadLocationException e){
											Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error deleting item from RUIHandler", e));
										}
									}
									return false;
								}
								
							});
						}
					}catch(Exception e){
						Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Handler Update: Error deleting item from RUIhandler", e));
					}
					return false;
				}
			});
		}
		return theCharactersRemoved;
	}
	
	public String getDeletedWidgetName() {
		return deletedWidgetName;
	}
}
