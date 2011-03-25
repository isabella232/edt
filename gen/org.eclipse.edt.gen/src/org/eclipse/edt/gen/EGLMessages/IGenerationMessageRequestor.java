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

import java.util.List;

public interface IGenerationMessageRequestor {
	void addMessage (EGLMessage message);
	boolean isError();
	List<EGLMessage> getMessages();
	void addMessages(List<EGLMessage> list);
	void clear();
}
