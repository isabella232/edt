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

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.mof.utils.NameUtile;

public class EGLRUIHandlerLocatorStrategy {
	
	private Node result;
	private Handler handler;
	
	public EGLRUIHandlerLocatorStrategy(Handler handler){
		this.handler = handler;
	}
	
	public Node locateIndex(final int index){
		SettingsBlockLocator locator = new SettingsBlockLocator();
		handler.accept(locator);
		if(locator.getSettingsBlock() != null){
			locator.getSettingsBlock().accept(new DefaultASTVisitor(){
				public boolean visit(SettingsBlock settingsBlock) {
					return true;
				}
				public boolean visit(final Assignment assignment) {
					if(assignment.getLeftHandSide().isName() && NameUtile.equals(NameUtile.getAsName("initialUI"), ((Name)assignment.getLeftHandSide()).getIdentifier())){
						assignment.getRightHandSide().accept(new DefaultASTVisitor(){
							public boolean visit(ArrayLiteral arrayLiteral){
								List expressions = arrayLiteral.getExpressions();
								result = (Node)expressions.get(index);
								return false;
							}
						});
					}
					return false;
				}
			});
		}
		return result;
	}
}
