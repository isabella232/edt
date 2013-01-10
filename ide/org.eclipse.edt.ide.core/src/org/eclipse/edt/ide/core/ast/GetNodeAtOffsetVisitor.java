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
package org.eclipse.edt.ide.core.ast;

import org.eclipse.edt.compiler.core.ast.AbstractASTNodeVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;

public class GetNodeAtOffsetVisitor extends AbstractASTNodeVisitor {

	private Node targetNode;
	private int targetOffset;
	private int targetLength;
	
	public GetNodeAtOffsetVisitor(int offset) {
		targetOffset = offset;
		targetLength = 0;
	}
	
	public GetNodeAtOffsetVisitor(int offset,int length) {
		targetOffset = offset;
		this.targetLength = length;
	}
	
	public Node getNode()
	{
		return targetNode;
	}
	
	/**
	 * if the offset is at a white space/comments, it will return the most close parent node for this offset
	 * the caller of this method needs to determine if this is what they want. 
	 * the offset is AT or to the right of the node offset and the cursor is to(NOT at) the left of the end of node
	 * in other words, it includes the node offset, but exclude the offset+length of the node
	 *
	 * @param node
	 * @return
	 */
	protected boolean isNodeAtOffset(Node node)
	{
		if(node.getOffset() <= targetOffset && targetOffset < node.getOffset() + node.getLength())
		{
			//we found the target node
			targetNode = node;		
			if (node.getOffset() == targetOffset){
				if ((node.getOffset() + node.getLength()) == (targetOffset + targetLength)){
					if (node instanceof Name){
						return false;
					}
				}
			}else if (node instanceof QualifiedName){
				Name qualifier = ((QualifiedName)node).getQualifier();
				if ((qualifier.getOffset() + qualifier.getLength() + 1) == targetOffset && (targetOffset + targetLength == node.getOffset() + node.getLength())){
					return false;
				}
				
			}
			return true;
		}		
		return false;
	}
	
	public boolean visitNode(Node node) {
		return isNodeAtOffset(node);
	}
	
	public void endVisitNode(Node node) {
	}
	
}
