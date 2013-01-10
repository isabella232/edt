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

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.mof.utils.NameUtile;

public class AssignmentLocator extends DefaultASTVisitor {
	
	private String internedPropertyName;
	private Assignment theAssignment;
	
	public AssignmentLocator(String internedPropertyName){
		this.internedPropertyName = internedPropertyName;
	}
	
	public Assignment getAssignment(){
		return theAssignment;
	}
	
	public boolean visit(SettingsBlock settingsBlock){
		List settings = settingsBlock.getSettings();
		for (Iterator iterator = settings.iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			node.accept(new DefaultASTVisitor(){
				public boolean visit(Assignment assignment){
					assignment.getLeftHandSide().accept(new DefaultASTVisitor(){
						public boolean visit(SimpleName simpleName){
							if(NameUtile.equals(simpleName.getIdentifier(), internedPropertyName)){
								theAssignment = (Assignment)simpleName.getParent();
							}
							return false;
						}
					});
					return false;
				}
			});
			
			if(theAssignment != null){
				break;
			}
		}
		return false;
	}
}
