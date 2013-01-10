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
package org.eclipse.edt.ide.ui.internal.formatting;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.AccumulatingSyntaxErrorMessageRequestor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.SyntaxError;
import org.eclipse.edt.ide.ui.editor.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

public class EGLCodeFormatter extends CodeFormatter {

	private Map fPreferenceSetting;
	private static final String CONSOLE_FORMATTING = "EGL Formatting"; //$NON-NLS-1$
	
	public EGLCodeFormatter(){
		
	}
	
	public EGLCodeFormatter(Map options) {
		this();
		fPreferenceSetting = options;
	}
	

	public TextEdit format(IDocument document, int offset, int length, 
			int initialIndentationLevel, String lineSeparator, ISyntaxErrorRequestor syntxErrRequestor) {
		if (offset < 0 || length < 0) {
			throw new IllegalArgumentException();
		}
		
		CodeFormatterVisitor formatVisitor = new CodeFormatterVisitor(fPreferenceSetting, document, offset, length, initialIndentationLevel, lineSeparator);
		
		File fileAST = getFileAST(document);
		if(fileAST != null){
			//check for syntax error
			//do not format if there is syntax error in the file
			boolean callerPassInNullSyntaxErrRequestor = false;
			if(syntxErrRequestor == null){
				//if caller pass in null, we will create a default one
				syntxErrRequestor = new AccumulatingSyntaxErrorMessageRequestor();
				callerPassInNullSyntaxErrRequestor = true;
			}
			boolean hasSyntaxError = fileAST.accept(syntxErrRequestor);
			if(!hasSyntaxError)		//no syntax error
				return formatVisitor.format(fileAST);
			else if(callerPassInNullSyntaxErrRequestor && syntxErrRequestor instanceof AccumulatingSyntaxErrorMessageRequestor){
				//let's log it in the system console for debugging purpose
				AccumulatingSyntaxErrorMessageRequestor errRequetor = (AccumulatingSyntaxErrorMessageRequestor)syntxErrRequestor;
				Map syntaxErrMsgs = errRequetor.getSyntaxErrors();
				Set keyset = syntaxErrMsgs.keySet();
				for(Iterator it = keyset.iterator(); it.hasNext();){
					try{
						SyntaxError syntaxErr = (SyntaxError)it.next();
						String errmsg = (String)(syntaxErrMsgs.get(syntaxErr));		
						int lineNum = document.getLineOfOffset(syntaxErr.startOffset);
						//int lineOffset = document.getLineOffset(lineNum);
						String errToken = document.get(syntaxErr.startOffset, syntaxErr.endOffset-syntaxErr.startOffset);
						
						System.out.println("Found syntax error: " + errmsg); //$NON-NLS-1$
						System.out.println("at line: " + (lineNum+1) + " for token: " + errToken); //$NON-NLS-1$ //$NON-NLS-2$
					}		
					catch(BadLocationException e){
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}
	
	private MessageConsole findFormattingErrorConsole(){
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMgr = plugin.getConsoleManager();
		IConsole[] existing = conMgr.getConsoles();
		for(int i=0; i<existing.length; i++){
			if(CONSOLE_FORMATTING.equals(existing[i].getName()))
				return (MessageConsole)existing[i];			
		}
		
		//no console found, so create a new one
		MessageConsole formattingConsole = new MessageConsole(CONSOLE_FORMATTING, null);
		conMgr.addConsoles(new IConsole[]{formattingConsole});
		return formattingConsole;
	}

}
