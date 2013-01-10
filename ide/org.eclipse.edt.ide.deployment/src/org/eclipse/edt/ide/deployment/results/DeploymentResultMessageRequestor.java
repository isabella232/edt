/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;

public class DeploymentResultMessageRequestor implements IGenerationMessageRequestor
{
	private IDeploymentResultsCollector resultsCollector;
	private List messages = new ArrayList();
	private boolean hasError = false;
	
	public DeploymentResultMessageRequestor( IDeploymentResultsCollector resultsCollector) {
		this.resultsCollector = resultsCollector;
	}
	public void addMessage(IGenerationResultsMessage message) 
	{
		messages.add(message);
		if( !hasError && message.getSeverity() == EGLMessage.EGL_ERROR_MESSAGE )
		{
			hasError = true;
		}
		resultsCollector.addMessage(DeploymentUtilities.convert(message));
	}
	public void addMessages(List<IGenerationResultsMessage> list) 
	{
		for( Iterator itr = list.iterator(); itr.hasNext();)
		{
			EGLMessage message = (EGLMessage)itr.next();
			messages.add(message);
			if( !hasError && message.getSeverity() == EGLMessage.EGL_ERROR_MESSAGE )
			{
				hasError = true;
			}
			resultsCollector.addMessage(DeploymentUtilities.convert(message));
		}
	}
	public void clear() {
	}
	public List getMessages() {
		return messages;
	}
	public boolean isError() 
	{
		return hasError;
	}
	public void sendMessagesToGenerationResultsServer(boolean bool) {
	}
	
}
