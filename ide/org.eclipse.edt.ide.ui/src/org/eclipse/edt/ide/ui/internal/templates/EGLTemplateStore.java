/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.templates;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

// TODO EDT Look for a newer way to implement this
public class EGLTemplateStore extends ContributionTemplateStore {

	public EGLTemplateStore(IPreferenceStore store, String key) {
		super(store, key);
	}

	public EGLTemplateStore(ContextTypeRegistry registry, IPreferenceStore store, String key) {
		super(registry, store, key);
	}

	protected void internalAdd(TemplatePersistenceData data) {
		super.internalAdd(data);
	}

}
