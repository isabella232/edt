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
package org.eclipse.edt.ide.ui.internal.editor;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.ElseBlock;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnEventBlock;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class ScriptDoubleClickVisitor extends AbstractASTVisitor {

	private IDocument fDoc;
	
	//the selection cursor's position.x
	private int cursorOffset = -1;
	
	//staring and ending offset of a node
//	private int startingPos = -1;
//	private int endPos = -1;
	
	//what's the select offset if to highlight the body of the part
	private int doubleClickSelOffset = -1;
	private boolean foundDoubleClickOffset = false;
	
	public ScriptDoubleClickVisitor(IDocument doc, int cusorPosition) {
		fDoc = doc;
		cursorOffset = cusorPosition;
	}
	
	public boolean foundDoubleClickOffset()
	{
		return foundDoubleClickOffset;
	}
	
	public int getDoubleClickOffset()
	{
		return doubleClickSelOffset;
	}
	
	private void calculateStartingEndingPosition(Node startingPositionNode, Node currentNode)
	{
		int blockStartingPos = startingPositionNode.getOffset() + startingPositionNode.getLength();
		calculateStartingEndingPosition(blockStartingPos, currentNode);
	}
	
	private void calculateStartingEndingPosition(int blockStartingPosition, Node currentNode)
	{
		int blockEndPos = currentNode.getOffset() + currentNode.getLength() - IEGLConstants.KEYWORD_END.length();
		calculateStartingEndingPosition(blockStartingPosition, blockEndPos);
	}
	
	private void calculateStartingEndingPosition(int blockStartingPosition, int blockEndingPosition)
	{
		if(cursorOffset == blockStartingPosition)
		{
			foundDoubleClickOffset = true;
			doubleClickSelOffset = blockEndingPosition;
		}
		else if(cursorOffset == blockEndingPosition)
		{
			foundDoubleClickOffset = true;
			doubleClickSelOffset = blockStartingPosition;
		}
		else
			foundDoubleClickOffset = false;				
	}
	
	
	public boolean visit(Record record) {
		calcaulatePartPosition(record);
		return false;
	}
	
	public boolean visit(DataTable dataTable)
	{
		calcaulatePartPosition(dataTable);
		return false;
	}
		
	public boolean visit(Enumeration enumeration) {
		calcaulatePartPosition(enumeration);
		return false;
	};
	
	public boolean visit(ExternalType externalType) {
		Name startingPositionNode = null;
		if(externalType.hasSubType())
			startingPositionNode = externalType.getSubType();
		else if(!externalType.getExtendedTypes().isEmpty())
			startingPositionNode = (Name) externalType.getExtendedTypes().get(0);
		else
			startingPositionNode = externalType.getName();
		calculateStartingEndingPosition(startingPositionNode, externalType);
		return false;
	};
	
	public boolean visit(FormGroup formGroup)
	{
		Name startingPositionNode = formGroup.getName();
		calculateStartingEndingPosition(startingPositionNode, formGroup);
		return false;
	}
	
	public boolean visit(TopLevelForm topLevelForm)
	{
		calcaulatePartPosition(topLevelForm);
		return false;
	}
		
	public boolean visit(Handler handler)
	{
		calcaulatePartPosition(handler);
		return false;
	}
	
	private int searchForClosingBracket(int startPos, char closeBracket, IDocument doc)
	{
		int closePos = startPos;
		int length = doc.getLength();
		char nextChar;
		
		boolean bFnd = false;
		try
		{
			while(closePos < length && !bFnd)
			{
				nextChar = doc.getChar(closePos);
				if(nextChar == closeBracket)			
					bFnd = true;
				closePos ++;
			}
		} catch (BadLocationException e) {
			closePos = -1;
			e.printStackTrace();
		}

		if(bFnd)
			return closePos;
		else
			return -1;
	}
	
	public boolean visit(Program program)
	{
		//let's see if this program has any program parameters
		//if so, get the last parameters, then continue till we found the right closing parant ')'
		List params = program.getParameters();
		if(params != null && !params.isEmpty())
		{
			int size = params.size();
			//get the last param type
			Type paramTypeNode = ((ProgramParameter)(params.get(size-1))).getType();
			int endofParam = paramTypeNode.getOffset() + paramTypeNode.getLength();
			//now try to find the closing brace ')'
			//its offset will be the startingPos for the node body
			int closingBracketPos = searchForClosingBracket(endofParam, ')', fDoc);
			calculateStartingEndingPosition(closingBracketPos, program);				
		} 
		else
			calcaulatePartPosition(program);
		return false;		
	}
	
	public boolean visit(Library library)
	{
		calcaulatePartPosition(library);	
		return false;
	}

	private void calcaulatePartPosition(Part part) {
		Name startingPositionNode = null;
		if(part.hasSubType())
			startingPositionNode = part.getSubType();
		else
			startingPositionNode = part.getName();
		calculateStartingEndingPosition(startingPositionNode, part);
	}
	
	public boolean visit(TopLevelFunction topLevelFunction)
	{
		int startingNodePos = -1;
		if(topLevelFunction.hasReturnType())
		{
			Node returnTypeNode = topLevelFunction.getReturnType();
			startingNodePos = returnTypeNode.getOffset() + returnTypeNode.getLength();
			//try to get the closing paren ')' for the return 
		}
		else
		{
			//try to get the closing paren ')' for the function parameters
			Name functionNameNode = topLevelFunction.getName();
			startingNodePos = functionNameNode.getOffset() + functionNameNode.getLength();
		}
		int closingParenPos = searchForClosingBracket(startingNodePos, ')', fDoc);
		calculateStartingEndingPosition(closingParenPos, topLevelFunction);
		
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction)
	{
		Node startingNode = null;
		if(nestedFunction.hasReturnType())
		{
			startingNode = nestedFunction.getReturnType();
			//try to get the closing paren ') for the return type
		}
		else
		{
			//try to get the closing paren ')' for the function parameters
			startingNode = nestedFunction.getName();
		}
		int startingNodePos = startingNode.getOffset() + startingNode.getLength();
		int closingParenPos = searchForClosingBracket(startingNodePos, ')', fDoc);
		calculateStartingEndingPosition(closingParenPos, nestedFunction);
		
		return false;
	}
	
	public boolean visit(Service service)
	{
		Name startingPositionNode = null;
		List implementList = service.getImplementedInterfaces();
		if(implementList != null && !implementList.isEmpty())
		{
			//get the last implements name
			int size = implementList.size();
			startingPositionNode = (Name)(implementList.get(size-1));			
		}
		else
			startingPositionNode = service.getName();
		calculateStartingEndingPosition(startingPositionNode, service);
		return false;
	}
	
	public boolean visit(Interface interfaceNode)
	{
		calcaulatePartPosition(interfaceNode);
		return false;
	}
	
	public boolean visit(NestedForm nestedForm)
	{
		Name startingPositionNode = null;
		if(nestedForm.hasSubType())
			startingPositionNode = nestedForm.getSubType();
		else
			startingPositionNode = nestedForm.getName();
		calculateStartingEndingPosition(startingPositionNode, nestedForm);
		return false;
	}

	public boolean visit(TryStatement tryStatement)
	{
		//starting position is the node offset + 3(length of the "try" keyword)
		int startingPosition = tryStatement.getOffset() + IEGLConstants.KEYWORD_TRY.length();		
		List exceptionBlocks = tryStatement.getOnExceptionBlocks();
		//you may have 0 or * onExceptionBlock
		return visitParentWithChildrenBlock(startingPosition, tryStatement, exceptionBlocks);		
	}

	private boolean visitParentWithChildrenBlock(int startingPosition, Node parentNode, List childBlocks) {
		
		if(childBlocks != null && !childBlocks.isEmpty())
		{
			int parentNodeEndPos = parentNode.getOffset() + parentNode.getLength();
		
			//if the cursor is right before the keywards "END", and the parent has child block		
			if(cursorOffset == parentNodeEndPos-IEGLConstants.KEYWORD_END.length())
			{
				int size = childBlocks.size();
				Node lastChildBlock = (Node)(childBlocks.get(size-1));
				//we need to treat this node as onException block
				lastChildBlock.accept(this);
			}
			else
			{
				//it ends before the keyward "onException" block
				Node firstChildBlock = (Node)(childBlocks.get(0));
				//the block ends before the onException block
				int blockEndPos = firstChildBlock.getOffset();
				calculateStartingEndingPosition(startingPosition, blockEndPos);
			}
		}
		else
		{
			//use end
			calculateStartingEndingPosition(startingPosition, parentNode);
		}
		return false;
	}
	
	private int getOnExceptionBlockStartingPosition(OnExceptionBlock onExceptionBlock)
	{
		int onExceptionBlockStartingPos = onExceptionBlock.getOffset() + IEGLConstants.KEYWORD_ONEXCEPTION.length();
		if(onExceptionBlock.hasExceptionDeclaration())
		{
			Type exceptionType = onExceptionBlock.getExceptionType();
			int exceptionTypePos = exceptionType.getOffset() + exceptionType.getLength();
			onExceptionBlockStartingPos = searchForClosingBracket(exceptionTypePos, ')', fDoc);
		}
		return onExceptionBlockStartingPos;		
	}
	
	public boolean visit(OnExceptionBlock onExceptionBlock)
	{
		Node parentNode = onExceptionBlock.getParent();
		if(parentNode instanceof TryStatement)
		{
			TryStatement tryStatement = (TryStatement)parentNode;
			List exceptionBlocks = tryStatement.getOnExceptionBlocks();
			int startingPosition = getOnExceptionBlockStartingPosition(onExceptionBlock);
			
			visitListChildrenBlock(onExceptionBlock, parentNode, exceptionBlocks, startingPosition);
		}
		return false;
	}
	
	public boolean visit(CaseStatement caseStatement)
	{
		if(caseStatement.hasCriterion())
		{
			Expression expr = caseStatement.getCriterion();
			int exprEndPos = expr.getOffset() + expr.getLength();
			//no need to find the closing paren, the case criterion expression length included the parenthesis.
			//int closingParenPos = searchForClosingBracket(exprEndPos, ')', fDoc);
			calculateStartingEndingPosition(exprEndPos, caseStatement);				
		}
		else
		{
			int startingPosition = caseStatement.getOffset() + IEGLConstants.KEYWORD_CASE.length();
			calculateStartingEndingPosition(startingPosition, caseStatement);
		}
		return false;
	}
	
	public boolean visit(IfStatement ifStatement)
	{
		Expression expr = ifStatement.getCondition();
		int exprEndPos = expr.getOffset() + expr.getLength();
		int blockStartingPos = searchForClosingBracket(exprEndPos, ')', fDoc);
		if(ifStatement.hasElse())
		{
			//it ends before the else block
			ElseBlock elseNode = ifStatement.getElse();
			
			int ifStatementEndPos = ifStatement.getOffset() + ifStatement.getLength();			
			//if the cursor is right before the keyword "END", and the if statement has else block
			if(cursorOffset == ifStatementEndPos-IEGLConstants.KEYWORD_END.length())
			{
				//we need to treat this node as elseBlock
				elseNode.accept(this);
			}
			else
			{
				int blockEndingPos = elseNode.getOffset();
				calculateStartingEndingPosition(blockStartingPos, blockEndingPos);
			}						
		}
		else
		{
			//it ends before "end"
			calculateStartingEndingPosition(blockStartingPos, ifStatement);
		}
		return false;
	}
		
	public boolean visit(ElseBlock elseBlock)
	{
		return visitSecondHalfChildBlock(elseBlock, IEGLConstants.KEYWORD_ELSE.length());
	}
		
	private boolean visitSecondHalfChildBlock(Node secondHalfNode, int keywordLength)
	{
		Node parentNode = secondHalfNode.getParent();
		if(cursorOffset == secondHalfNode.getOffset())
		{
			//cursor is at the beginning of the 2nd half node(else, onException..), 
			//so we need to treat it as the end of its parent
			parentNode.accept(this);
		}
		else
		{
			int startingPosition = secondHalfNode.getOffset() + keywordLength;
			calculateStartingEndingPosition(startingPosition, parentNode);
		}
		return false;
	}
	
	public boolean visit(WhileStatement whileStatement)
	{
		Expression expr = whileStatement.getExpr();
		int exprEndPos = expr.getOffset() + expr.getLength();
		int blockStartingPos = searchForClosingBracket(exprEndPos, ')', fDoc);
		calculateStartingEndingPosition(blockStartingPos, whileStatement);
		return false;		
	}
	
	public boolean visit(ForStatement forStatement)
	{
		int startSearchRParen = forStatement.getOffset();
		Expression stepExpr = forStatement.getDeltaExpression();
		if(stepExpr != null)
		{
			startSearchRParen = stepExpr.getOffset() + stepExpr.getLength();
		}
		else
		{
			//no explicit step expression
			Expression toExpr = forStatement.getEndIndex();
			startSearchRParen = toExpr.getOffset() + toExpr.getLength();
		}
		int blockStartingPos = searchForClosingBracket(startSearchRParen, ')', fDoc);
		calculateStartingEndingPosition(blockStartingPos, forStatement);
		return false;
	}
	
	public boolean visit(ForEachStatement forEachStatement)
	{
		int startSearchRParen = forEachStatement.getClosingParenOffset();
		int blockStartingPos = searchForClosingBracket(startSearchRParen, ')', fDoc);
		calculateStartingEndingPosition(blockStartingPos, forEachStatement);
		return false;
	}
	
	public boolean visit(OpenUIStatement openUIStatement)
	{
		//starts after the "OPENUI" keyword
		int startingPosition = openUIStatement.getOffset() + IEGLConstants.KEYWORD_OPENUI.length();

		//you may have 0 or 1 bind block, but we can't get the "BIND" offset, since there is no bind node
		//the OpenUIStatement only returns list of the bind expression, not the bind node
//		if(openUIStatement.hasBindClause())
//		{
//			//it ends right before the bind keyword
//		}
//		else

		
		List eventBlocks = openUIStatement.getEventBlocks();
		//you may have 0 or * onEvent blocks				
		return visitParentWithChildrenBlock(startingPosition, openUIStatement, eventBlocks);
	}
	
	private int getOnEventBlockStartingPosition(OnEventBlock onEventBlock)
	{
		int onEventBlockStartingPos = onEventBlock.getOffset() + IEGLConstants.KEYWORD_ONEVENT.length();
		if(onEventBlock.hasStringList())
		{
			List fields = onEventBlock.getStringList();
			int size = fields.size();
			//get the last one
			Node lastField = (Node)(fields.get(size-1));
			int lastFieldEndPos = lastField.getOffset() + lastField.getLength();
			onEventBlockStartingPos = searchForClosingBracket(lastFieldEndPos, ')', fDoc);
		}
		else
			onEventBlockStartingPos = searchForClosingBracket(onEventBlockStartingPos, ')', fDoc);
		return onEventBlockStartingPos;
	}
	
	public boolean visit(OnEventBlock onEventBlock)
	{
		Node parentNode = onEventBlock.getParent();
		if(parentNode instanceof OpenUIStatement)
		{
			OpenUIStatement openUIStatment = (OpenUIStatement)parentNode;
			List eventBlocks = openUIStatment.getEventBlocks();
			int startingPosition = getOnEventBlockStartingPosition(onEventBlock);			
			
			visitListChildrenBlock(onEventBlock, parentNode, eventBlocks, startingPosition);
		}

		return false;
	}

	private void visitListChildrenBlock(Node childBlock, Node parentNode, List childBlocks, int startingPosition) {
		int size = childBlocks.size();
		boolean bFnd = false;
		int index=-1;
		for(int i=0; i<size && !bFnd; i++)
		{
			Node childBlockElement = (Node)(childBlocks.get(i));
			if(childBlockElement.getOffset() == childBlock.getOffset())	
			{
				bFnd = true;		//we found the node that's passed in
				index = i;
			}
		}
		if(bFnd)
		{
			//int startingPosition = onEventBlock.getOffset() + IEGLConstants.KEYWORD_ONEVENT.length();
			
			if(index==0 && index==size-1)	//only 1 onEvent block
			{
				if(cursorOffset == childBlock.getOffset())
					parentNode.accept(this);
				else
					calculateStartingEndingPosition(startingPosition, parentNode);
			}
			else if(index == size-1)		//last onEvent and more than 1 onEvent
			{
				if(cursorOffset == childBlock.getOffset())
				{
					Node previousEventBlock = (Node)(childBlocks.get(index-1));
					previousEventBlock.accept(this);
				}
				else
					calculateStartingEndingPosition(startingPosition, parentNode);
			}
			else
			{
				Node nextChildBlock = (Node)(childBlocks.get(index+1));
				int endingPosition = nextChildBlock.getOffset();
				
				if(index==0)			//1st onEvent and more than 1 onEvent
				{
					if(cursorOffset == childBlock.getOffset())
						parentNode.accept(this);
					else
						calculateStartingEndingPosition(startingPosition, endingPosition);
				}
				else		//in the middle
				{
					if(cursorOffset == childBlock.getOffset())
					{
						Node previousChildBlock = (Node)(childBlocks.get(index-1));
						previousChildBlock.accept(this);						
					}
					else
						calculateStartingEndingPosition(startingPosition, endingPosition);
				}
			}

		}
	}	
}
