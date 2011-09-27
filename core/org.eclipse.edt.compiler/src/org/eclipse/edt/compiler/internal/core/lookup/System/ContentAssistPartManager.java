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

package org.eclipse.edt.compiler.internal.core.lookup.System;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.ExternalTypeBinding;

public class ContentAssistPartManager {
	private Map contentAssistParts = new HashMap();

	public ContentAssistPartManager(ContentAssistPartManager parent) {
		if (null != parent) {
			contentAssistParts.putAll(parent.contentAssistParts);
		}
	}

	public Map getContentAssistParts() {
		return contentAssistParts;
	}

	public void addContentAssistPart(ExternalTypeBinding contentAssistPart) {
		contentAssistParts.put(contentAssistPart.getName(), contentAssistPart);
	}

}
