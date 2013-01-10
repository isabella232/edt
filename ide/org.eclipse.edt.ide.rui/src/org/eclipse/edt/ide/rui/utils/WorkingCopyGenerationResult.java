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
package org.eclipse.edt.ide.rui.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IProblemRequestorFactory;
import org.eclipse.edt.mof.egl.Element;

public class WorkingCopyGenerationResult implements IProblemRequestor, ISyntaxErrorRequestor, IGenerationMessageRequestor, IProblemRequestorFactory {

	private org.eclipse.edt.gen.deployment.util.WorkingCopyGenerationResult workingCopyGenerationResult = new org.eclipse.edt.gen.deployment.util.WorkingCopyGenerationResult();
	
	public org.eclipse.edt.gen.deployment.util.WorkingCopyGenerationResult getResult(){
		return workingCopyGenerationResult;
	}
	
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}
	public void acceptProblem(Node astNode, int problemKind) {
		workingCopyGenerationResult.hasError = true;
	}
	public void acceptProblem(Node astNode, int problemKind, int severity) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}
	public void acceptProblem(Node astNode, int problemKind, String[] inserts) {
		workingCopyGenerationResult.hasError = true;
	}
	public void acceptProblem(Node astNode, int problemKind, int severity, String[] inserts) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}
	public void acceptProblem(int startOffset, int endOffset, int problemKind, String[] inserts) {
		workingCopyGenerationResult.hasError = true;
	}
	public void acceptProblem(int startOffset, int endOffset, int problemKind, boolean isError, String[] inserts) {
		if(isError){
			workingCopyGenerationResult.hasError = true;
		}
	}
	public boolean hasError() {
		return workingCopyGenerationResult.hasError;
	}
	public boolean shouldReportProblem(int problemKind) {
		return false;
	}
	public void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void incorrectTerminal(int terminalType, int startOffset, int endOffset) {	
		workingCopyGenerationResult.hasError = true;
	}
	public void invalidCharacterInHexLiteral(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void invalidEscapeSequence(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void keywordAsName(int terminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void missingEndForPart(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void missingPreviousTerminal(int terminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void missingScopeCloser(int terminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void missingTerminal(int terminalType, int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void panicPhrase(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void tooManyErrors() {
		workingCopyGenerationResult.hasError = true;
	}
	public void unclosedBlockComment(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void unclosedDLI(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void unclosedSQL(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void unclosedSQLCondition(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void unclosedString(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void unexpectedPhrase(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void unexpectedPreviousTerminal(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void unexpectedTerminal(int startOffset, int endOffset) {	
		workingCopyGenerationResult.hasError = true;
	}
	public void whitespaceInDLI(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void whitespaceInSQL(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	public void whitespaceInSQLCondition(int startOffset, int endOffset) {
		workingCopyGenerationResult.hasError = true;
	}
	
	public void addMessage(IGenerationResultsMessage message) {
		if(!workingCopyGenerationResult.EGLMESSAGESTOIGNORE.contains(message.getId())) {
			if (message.isError()) {
				workingCopyGenerationResult.hasError = true;
			}
			
			if(workingCopyGenerationResult.genMessages == Collections.EMPTY_LIST) {
				workingCopyGenerationResult.genMessages = new ArrayList<String>();
			}

//TODO EDT
//			if (workingCopyGenerationResult.sendMessagesToGenerationResultsServer) {
//				GenerationResultsServer.getInstance().message(new MessageEventImpl(message.getSeverity(), message.getId(), message.getBuiltMessageWithLineAndColumn()));
//			}
			
			workingCopyGenerationResult.genMessages.add(message);
			
			if(message.isError() && !EGLMessage.EGLMESSAGE_GENERATION_FAILED.equals(message.getId())) {
				workingCopyGenerationResult.numGenErrors += 1;
			}
			else if(message.isWarning()) {
				workingCopyGenerationResult.numGenWarnings += 1;
			}	
		}
	}

	public void addMessages(List<IGenerationResultsMessage> newmsgs) {
		Iterator i = newmsgs.iterator();
		while (i.hasNext()) {
			EGLMessage msg = (EGLMessage) i.next();
			addMessage(msg);
		}
	}

	public List getMessages() {
		return workingCopyGenerationResult.genMessages;
	}
	
	public int getNumGenErrors() {
		return workingCopyGenerationResult.numGenErrors;
	}
	
	public int getNumGenWarnings() {
		return workingCopyGenerationResult.numGenWarnings;
	}

	public boolean isError() {
		return workingCopyGenerationResult.hasError;
	}
	
	public void clear() {
		workingCopyGenerationResult.hasError = false;
		workingCopyGenerationResult.genMessages = Collections.EMPTY_LIST;
		workingCopyGenerationResult.numGenWarnings = 0;
		workingCopyGenerationResult.numGenErrors = 0;
	}
	
	public boolean hasGenerationError(){
		return workingCopyGenerationResult.genMessages.size() > 0;
	}
	
	public void setHasError(boolean error){
		workingCopyGenerationResult.hasError = error;
	}
	
	public IProblemRequestor getContainerContextTopLevelProblemRequestor(IFile file, String functionPartName, String containerContextName, IPath containerContextPath, boolean containerContextDependent) {
		return this;
	}
	public IProblemRequestor getFileProblemRequestor(IFile file) {
		return this;
	}
	public IProblemRequestor getGenericTopLevelFunctionProblemRequestor(IFile file, String partName, boolean containerContextDependent) {
		return this;
	}
	public IProblemRequestor getProblemRequestor(IFile file, String partName) {
		return this;
	}
	public ISyntaxErrorRequestor getSyntaxErrorRequestor(IFile file) {
		return this;
	}
	public void sendMessagesToGenerationResultsServer(boolean bool) {
		workingCopyGenerationResult.sendMessagesToGenerationResultsServer = bool; 		
	}

	@Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}

	@Override
	public void acceptProblem(Node astNode, int problemKind, int severity, String[] inserts, ResourceBundle bundle) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}

	@Override
	public void acceptProblem(Element element, int problemKind) {
		workingCopyGenerationResult.hasError = true;
	}

	@Override
	public void acceptProblem(Element element, int problemKind, int severity) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}

	@Override
	public void acceptProblem(Element element, int problemKind, int severity, String[] inserts) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}

	@Override
	public void acceptProblem(Element element, int problemKind, int severity, String[] inserts, ResourceBundle bundle) {
		if(severity == IMarker.SEVERITY_ERROR){
			workingCopyGenerationResult.hasError = true;
		}
	}

	@Override
	public void acceptProblem(int startOffset, int endOffset, int problemKind, boolean isError, String[] inserts, ResourceBundle bundle) {
		if (isError) {
			workingCopyGenerationResult.hasError = true;
		}
	}

}
