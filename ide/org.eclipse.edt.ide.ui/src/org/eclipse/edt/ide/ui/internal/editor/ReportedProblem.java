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
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class ReportedProblem {
	
	int startOffset;
	int endOffset;
	int severity;
	int lineNumber = -1;
	int problemKind = -1;
	String message;
	String errorMsgCode;
	IDocument document;
	
	/**
	 * @param document 
	 * @param filePath
	 * @param errorMsg
	 * @param problem
	 */
	public ReportedProblem(IDocument document, String filePath, String errorMsg, Problem problem) {
		this.startOffset = problem.getStartOffset();
		this.endOffset = problem.getEndOffset();
		this.severity = translateSeverity(problem.getSeverity());
		this.errorMsgCode = errorMsg;
		this.problemKind = problem.getProblemKind();
		this.document = document;
		
		
		int lineNumber = getLineNumberOfOffset(startOffset);
		int problemKind = problem.getProblemKind();
		String[] inserts = problem.getInserts();
		
		if (problemKind != -1) {
			this.message = getErrorMessageText(problemKind, startOffset, lineNumber, severity, DefaultProblemRequestor.getMessageFromBundle(problemKind, inserts, problem.getResourceBundle()));
		}
		else {
			this.message = getErrorMessageText(problemKind, startOffset, lineNumber, severity, inserts[0]);
		}	
		this.lineNumber = lineNumber + 1;
	}

	private String getErrorMessageText(int problemKind, int startOffset, int lineNumber, int severity, String msgText) {
		StringBuffer result = new StringBuffer();
		result.append(getMessagePrefix(problemKind, startOffset, lineNumber, severity));
		result.append(" ");
		result.append(msgText);
		return result.toString();
	}
	
	private String getMessagePrefix(int problemKind, int startOffset, int lineNumber, int severity) {
		StringBuffer prefix = new StringBuffer();
		prefix.append("IWN.");
		prefix.append(errorMsgCode);
		if(problemKind != -1) {
			prefix.append(".");
			prefix.append(Integer.toString(problemKind));
		}
		if(IMarker.SEVERITY_ERROR == severity) {
			prefix.append(".e");
		}
		else if(IMarker.SEVERITY_WARNING == severity) {
			prefix.append(".w");
		}
		else if(IMarker.SEVERITY_INFO == severity) {
			prefix.append(".i");
		}
		prefix.append(" ");
		
		prefix.append(Integer.toString(lineNumber+1));
		
		prefix.append("/");
		
		int offsetOnLine = startOffset - getLineOffset(lineNumber);
		
		prefix.append(Integer.toString(offsetOnLine+1));
		
		return prefix.toString();
	}
	
	private int getLineOffset(int lineNumber){
		int offset = 0;
		try {
			offset = document.getLineOffset(lineNumber);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return offset;
	}
	
	private int getLineNumberOfOffset(int offset) {	
		int line = 0;
		try {
			line = document.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	private int translateSeverity(int severity) {
        switch(severity){
        	case org.eclipse.edt.compiler.internal.core.builder.IMarker.SEVERITY_ERROR: return IMarker.SEVERITY_ERROR;
        	case org.eclipse.edt.compiler.internal.core.builder.IMarker.SEVERITY_WARNING: return IMarker.SEVERITY_WARNING;
        	case org.eclipse.edt.compiler.internal.core.builder.IMarker.SEVERITY_INFO: return IMarker.SEVERITY_INFO;
        	default: return IMarker.SEVERITY_ERROR;
        }
    }

	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public int getSeverity() {
		return severity;
	}

	public String getMessage() {
		return message;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getProblemKind() {
		return problemKind;
	}
	
	
}
