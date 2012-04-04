/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget.InsertDataNode;


public class GenModelBuilder {
	private static GenModelBuilder instance;
	private GenModel genModel;
	
	public static GenModelBuilder getInstance(){
		if(instance == null){
			instance = new GenModelBuilder();
		}
		return instance;
	}
	
	private GenModelBuilder(){}
	
	public GenModel create(InsertDataNode insertDataNode){
		genModel = new GenModel();
		if(insertDataNode.isGen()){
			ComposeGenNode root = new ComposeGenNode(genModel, insertDataNode);
			genModel.setRoot(root);
			processChildren(root, insertDataNode, genModel);
		}
		cleanModel(genModel);
		return genModel;
	}
	
	public GenModel getGenModel(){
		return genModel;
	}
	
	private void cleanModel(GenModel genModel){
		List<GenNode> children = genModel.getRoot().getChildren();
		List<GenNode> clearedChildren = new ArrayList<GenNode>();
		for(GenNode child : children){
			if(child.getInsertDataNode().getParent().getParent() == null ){
				clearedChildren.add(child);
			}else if(child.getInsertDataNode().getParent().getDataTemplate().getDataMapping().isGenChildWidget()){
				clearedChildren.add(child);
			}
		}
		genModel.getRoot().setChildren(clearedChildren);
	}
	
	private void processChildren(ComposeGenNode parentCGN, InsertDataNode parentIDN, GenModel genModel){
		for(InsertDataNode childIDN : parentIDN.getChildren()){
			if(childIDN.isGen()){
				if(childIDN.isArray() && childIDN.getNodeType().equals(InsertDataNode.NodeType.TYPE_RECORD_ALL)){
					ComposeGenNode childCGN = new ComposeGenNode(genModel, childIDN);
					parentCGN.addChild(childCGN);
					processChildren(childCGN, childIDN, genModel);
				}else {
						GenNode childGN = new GenNode(genModel, childIDN);
						parentCGN.addChild(childGN);
						processChildren(parentCGN, childIDN, genModel);
				};
			}
			
			processChildren(parentCGN, childIDN, genModel);
				
		}
	}
}
