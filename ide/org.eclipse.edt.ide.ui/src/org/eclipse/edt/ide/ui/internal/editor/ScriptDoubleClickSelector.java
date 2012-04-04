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

package org.eclipse.edt.ide.ui.internal.editor;

import java.util.HashMap;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;


/**
 * Double click strategy aware of Java identifier syntax rules.
 */
public class ScriptDoubleClickSelector implements ITextDoubleClickStrategy {
	private final static String IF= "if";
	private final static String END= "end";
	private final static String ELSE= "else";
	private final static String WHILE= "while";
	private final static String FOR= "for";
	private final static String FOREACH= "foreach";
	private final static String TRY= "try";
	private final static String ONEXCEPTION= "onexception";
	private final static String CASE= "case";
	private final static String WHEN= "when";
	private final static String OTHERWISE= "otherwise";
	private final static String OPENUI= "openui";
	private final static String ONEVENT= "onevent";

	protected ITextViewer fText;
	protected int fPos;
	protected int fStartPos;
	protected int fEndPos;

	protected static char[] fgBrackets= {'{', '}', '(', ')', '[', ']', '"', '"'};
	protected static HashMap fgConditionalPairs = new HashMap();
	
	static {
		fgConditionalPairs.put(IF, END);
		fgConditionalPairs.put(IF, ELSE);
		fgConditionalPairs.put(ELSE, END);
		fgConditionalPairs.put(WHILE, END);
		fgConditionalPairs.put(CASE, END);
		fgConditionalPairs.put(FOR, END);
		fgConditionalPairs.put(FOREACH, END);
		fgConditionalPairs.put(TRY, END);
		fgConditionalPairs.put(TRY, ONEXCEPTION);
		fgConditionalPairs.put(ONEXCEPTION, END);
		fgConditionalPairs.put(WHEN, WHEN);
		fgConditionalPairs.put(WHEN, OTHERWISE);
		fgConditionalPairs.put(OTHERWISE, END);
		fgConditionalPairs.put(OPENUI, END);
		fgConditionalPairs.put(ONEVENT, ONEVENT);
		fgConditionalPairs.put(ONEVENT, END);
	}

	public ScriptDoubleClickSelector() {
		super();
	}
	/**
	 * @see ITextDoubleClickStrategy#doubleClicked
	 */
	public void doubleClicked(ITextViewer text) {

		fPos= text.getSelectedRange().x;

		if (fPos < 0)
			return;

		fText= text;

		if (selectBracketBlock()) return;
		if (selectConditionalBlock()) return;
		selectWord();
	}
	protected boolean selectConditionalBlock() {
		if (matchConditional()) {

			if (fStartPos == fEndPos)
				fText.setSelectedRange(fStartPos, 0);
			else
				fText.setSelectedRange(fStartPos, fEndPos - fStartPos);

			return true;
		}
		return false;
	}
	protected boolean matchConditional() {
		//get the node at offset, 
		//test to see if the offset is at the start of the block for this node
		//if this node has an "end" (using visitor pattern)
		IDocument doc= fText.getDocument();
		if(doc instanceof IEGLDocument)
		{
			IEGLDocument egldoc = (IEGLDocument)doc;
			Node node = egldoc.getNewModelNodeAtOffset(fPos);
			if(node != null)	//if there is a node
			{
				ScriptDoubleClickVisitor visitor = new ScriptDoubleClickVisitor(doc, fPos) ;
				node.accept(visitor);
				if(visitor.foundDoubleClickOffset())
				{
					int doubleClickOffset = visitor.getDoubleClickOffset();
					fStartPos = Math.min(fPos, doubleClickOffset);
					fEndPos = Math.max(fPos, doubleClickOffset);
					return true;
				}
			}
		}
		return false;
/*		node = doc.getNodeAtOffset(fPos);
		GetNodeBodyVisitor visitor = new GetNodeBodyVisitor(fPos);		//starting cursor
		node.accept(getNodeBodyVisitor)
*/		
		
		//if this is at the start point, get the length of the node, minus 3(length of "end")
		//that's the offset we need to highlight
		
		//	if this at the start of the block
		//		set start position
		//		search for matching end string (can be multiple) and set end position
		
		
		// else if is this just before the end of the block
		//		set end position
		//		search for matching start string and set start position

	}
	protected boolean matchBracketsAt() {

		char prevChar, nextChar;

		int i;
		int bracketIndex1= fgBrackets.length;
		int bracketIndex2= fgBrackets.length;

		fStartPos= -1;
		fEndPos= -1;

		// get the chars preceding and following the start position
		try {

			IDocument doc= fText.getDocument();

			prevChar= doc.getChar(fPos - 1);
			nextChar= doc.getChar(fPos);

			// is the char either an open or close bracket?
			for (i= 0; i < fgBrackets.length; i= i + 2) {
				if (prevChar == fgBrackets[i]) {
					fStartPos= fPos - 1;
					bracketIndex1= i;
				}
			}
			for (i= 1; i < fgBrackets.length; i= i + 2) {
				if (nextChar == fgBrackets[i]) {
					fEndPos= fPos;
					bracketIndex2= i;
				}
			}

			if (fStartPos > -1 && bracketIndex1 < bracketIndex2) {
				fEndPos= searchForClosingBracket(fStartPos, prevChar, fgBrackets[bracketIndex1 + 1], doc);
				if (fEndPos > -1)
					return true;
				else
					fStartPos= -1;
			} else if (fEndPos > -1) {
				fStartPos= searchForOpenBracket(fEndPos, fgBrackets[bracketIndex2 - 1], nextChar, doc);
				if (fStartPos > -1)
					return true;
				else
					fEndPos= -1;
			}

		} catch (BadLocationException x) {
		}

		return false;
	}
	protected boolean matchWord() {

		IDocument doc= fText.getDocument();

		try {

			int pos= fPos;
			char c;

			while (pos >= 0) {
				c= doc.getChar(pos);
				// In EGL '-' is a legal character for a name
				if (!(Character.isJavaIdentifierPart(c) || c == '-'))
					break;
				--pos;
			}

			fStartPos= pos;

			pos= fPos;
			int length= doc.getLength();

			while (pos < length) {
				c= doc.getChar(pos);
				// In EGL '-' is a legal character for a name
				if (!(Character.isJavaIdentifierPart(c) || c == '-'))
					break;
				++pos;
			}

			fEndPos= pos;

			return true;

		} catch (BadLocationException x) {
		}

		return false;
	}
	protected int searchForClosingBracket(int startPos, char openBracket, char closeBracket, IDocument doc) throws BadLocationException {
		int stack= 1;
		int closePos= startPos + 1;
		int length= doc.getLength();
		char nextChar;

		while (closePos < length && stack > 0) {
			nextChar= doc.getChar(closePos);
			if (nextChar == openBracket && nextChar != closeBracket)
				stack++;
			else if (nextChar == closeBracket)
				stack--;
			closePos++;
		}

		if (stack == 0)
			return closePos - 1;
		else
			return -1;

	}
	protected int searchForOpenBracket(int startPos, char openBracket, char closeBracket, IDocument doc) throws BadLocationException {
		int stack= 1;
		int openPos= startPos - 1;
		char nextChar;

		while (openPos >= 0 && stack > 0) {
			nextChar= doc.getChar(openPos);
			if (nextChar == closeBracket && nextChar != openBracket)
				stack++;
			else if (nextChar == openBracket)
				stack--;
			openPos--;
		}

		if (stack == 0)
			return openPos + 1;
		else
			return -1;
	}
	protected boolean selectBracketBlock() {
		if (matchBracketsAt()) {

			if (fStartPos == fEndPos)
				fText.setSelectedRange(fStartPos, 0);
			else
				fText.setSelectedRange(fStartPos + 1, fEndPos - fStartPos - 1);

			return true;
		}
		return false;
	}
	protected void selectWord() {
		if (matchWord()) {

			if (fStartPos == fEndPos)
				fText.setSelectedRange(fStartPos, 0);
			else
				fText.setSelectedRange(fStartPos + 1, fEndPos - fStartPos - 1);
		}
	}
}
