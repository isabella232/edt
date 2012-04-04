/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.EGLMessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;

public class AccumulatingGenerationMessageRequestor implements IGenerationMessageRequestor {
	List<IGenerationResultsMessage> list = new ArrayList<IGenerationResultsMessage>();
	boolean error = false;

	public AccumulatingGenerationMessageRequestor() {
		super();
	}

	public void addMessage(IGenerationResultsMessage message) {
		list.add(message);
		if (message.isError())
			error = true;
	}

	public void addMessages(List<IGenerationResultsMessage> newmsgs) {
		Iterator<IGenerationResultsMessage> i = newmsgs.iterator();
		while (i.hasNext()) {
			IGenerationResultsMessage msg = i.next();
			addMessage(msg);
		}
	}

	public List<IGenerationResultsMessage> getMessages() {
		return list;
	}

	public boolean isError() {
		return error;
	}

	public void clear() {
		error = false;
		list = new ArrayList<IGenerationResultsMessage>();
	}
}
