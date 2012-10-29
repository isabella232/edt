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
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.mof.utils.NameUtile;

public class EGLRUIWidgetDeleteStrategy {
	
	private IEGLDocument currentDocument;
	private Handler handler;
	private String deletedWidgetName;
	private int currentSetting = 0;
	
	public EGLRUIWidgetDeleteStrategy(Handler handler, IEGLDocument document){
		this.currentDocument  = document;
		this.handler = handler;
	}
	
	public void deleteTargetWidget(){
		SettingsBlockLocator locator = new SettingsBlockLocator();
		handler.accept(locator);
		if(locator.getSettingsBlock() != null){
			locator.getSettingsBlock().accept(new DefaultASTVisitor(){
				public boolean visit(SettingsBlock settingsBlock) {
					return true;
				}
				public boolean visit(final SetValuesExpression setvalues) {
					currentSetting ++;
					return false;
				}
				public boolean visit(final Assignment assignment) {
					try{
						currentSetting ++;
						if(assignment.getLeftHandSide().isName() && NameUtile.equals(NameUtile.getAsName("targetWidget"), ((Name)assignment.getLeftHandSide()).getIdentifier())){
							deletedWidgetName = assignment.getRightHandSide().getCanonicalString();
							int referenceOffset = assignment.getOffset();
							int referenceLength = assignment.getLength();
							List settings = ((SettingsBlock)assignment.getParent()).getSettings();
							if ( currentSetting == settings.size() ) {
								Node preNode = (Node)settings.get( currentSetting - 2);
								referenceLength += referenceOffset - (preNode.getOffset() + preNode.getLength());
								referenceOffset = preNode.getOffset() + preNode.getLength();
							} else {
								Node nextNode = (Node)settings.get( currentSetting );
								referenceLength = nextNode.getOffset() - referenceOffset;
							}
							currentDocument.replace(referenceOffset, referenceLength, "");
						}
					}catch(Exception e){
						Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Handler Update: Error deleting item from RUIhandler", e));
					}
					return false;
				}
			});
		}
	}
	
	public String getDeletedWidgetName() {
		return deletedWidgetName;
	}
}
