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
package org.eclipse.edt.compiler.internal.interfaces;

import java.util.List;

import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;

public interface IGenerationMessageRequestor {
	void addMessage (IGenerationResultsMessage message);
	boolean isError();
	List<IGenerationResultsMessage> getMessages();
	void addMessages(List<IGenerationResultsMessage> list);
	void clear();
}
