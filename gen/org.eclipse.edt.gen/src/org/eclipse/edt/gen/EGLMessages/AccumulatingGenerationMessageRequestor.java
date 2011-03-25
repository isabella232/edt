/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

public class AccumulatingGenerationMessageRequestor implements IGenerationMessageRequestor {
	List<EGLMessage> list = new ArrayList<EGLMessage>();
	boolean error = false;

	public AccumulatingGenerationMessageRequestor() {
		super();
	}

	public void addMessage(EGLMessage message) {
		list.add(message);
		if (message.isError())
			error = true;
	}

	public void addMessages(List<EGLMessage> newmsgs) {
		Iterator<EGLMessage> i = newmsgs.iterator();
		while (i.hasNext()) {
			EGLMessage msg = i.next();
			addMessage(msg);
		}
	}

	public List<EGLMessage> getMessages() {
		return list;
	}

	public boolean isError() {
		return error;
	}

	public void clear() {
		error = false;
		list = new ArrayList<EGLMessage>();
	}
}
