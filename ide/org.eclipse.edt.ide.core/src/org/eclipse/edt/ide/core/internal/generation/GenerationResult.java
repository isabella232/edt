/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;


import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.ide.core.generation.IGenerationResult;

/**
 * @version 	1.0
 * @author
 */
public class GenerationResult implements IGenerationResult{
  
	private PartWrapper part = null;
	
	private IGenerationResultsMessage[] messages = null;
	
	private int type;
	private boolean hasErrors = false;
	private boolean hasWarnings = false;
	
	public GenerationResult(PartWrapper part, EGLMessage[] messages, int type)
	{
		this(messages, type);
		this.part = part;
	}
	
	protected GenerationResult(EGLMessage[] messages, int type)
	{
		this.messages = convertMessages(messages);
		this.type = type;
	
		initalizeHasErrors();
		initializeHasWarnings();
	}
	
	private IGenerationResultsMessage[] convertMessages(EGLMessage[] eglMsgs) {
		if (eglMsgs == null) {
			return null;
		}
		IGenerationResultsMessage[] genMessages = new IGenerationResultsMessage[eglMsgs.length];
		for (int i = 0; i < genMessages.length; i++) {
			genMessages[i] = convertMessage(eglMsgs[i]);
		}
		return genMessages;
	}
	
	private IGenerationResultsMessage convertMessage(EGLMessage eglMsg) {
		if (eglMsg == null) {
			return null;
		}
		return new GenerationResultsMessage(eglMsg);
	}
	
	
	public PartWrapper getPart()
	{
		return part;
	}
	
	public IGenerationResultsMessage[] getMessages()
	{
		return messages;
	}
	
	/**
	 * @return Returns the hasErrors.
	 */
	public boolean hasErrors() {
		return hasErrors;
	}
	
	private void initalizeHasErrors(){
		hasErrors = false;
		IGenerationResultsMessage msgs[] = getMessages();
		for (int i = 0; i < msgs.length; i++) {
			if (messages[i].isError()){
				hasErrors = true;
				break;
			}
		}
	}
	
	public boolean hasWarnings() {
		return hasWarnings;
	}
	
	private void initializeHasWarnings(){
		hasWarnings = false;
		IGenerationResultsMessage msgs[] = getMessages();
		for (int i = 0; i < msgs.length; i++) {
			if (messages[i].getSeverity() == IGenerationResultsMessage.EGL_WARNING_MESSAGE){
				hasWarnings = true;
				break;
			}
		}
	}

	public int getType(){
		return type;
	}
}
