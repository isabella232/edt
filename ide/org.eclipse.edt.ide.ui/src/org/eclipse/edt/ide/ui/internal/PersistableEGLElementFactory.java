/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.ui.IMemento;

public class PersistableEGLElementFactory {
	private static final String KEY= "elementID"; //$NON-NLS-1$
	private static final String FACTORY_ID= "org.eclipse.jdt.ui.PersistableJavaElementFactory"; //$NON-NLS-1$

	private IEGLElement fElement;
	
	/**
	 * Create a JavaElementFactory.  
	 */
	public PersistableEGLElementFactory() {
	}

	/**
	 * Create a JavaElementFactory.  This constructor is typically used
	 * for our IPersistableElement side.
	 */
	public PersistableEGLElementFactory(IEGLElement element) {
		fElement= element;
	}

	/*
	 * @see IElementFactory
	 */
	public IAdaptable createElement(IMemento memento) {
	
		String identifier= memento.getString(KEY);
		if (identifier != null) {
			return EGLCore.create(identifier);
		}
		return null;
	}
	
	/*
	 * @see IPersistableElement.
	 */
	public String getFactoryId() {
		return FACTORY_ID;
	}
	/*
	 * @see IPersistableElement
	 */
	public void saveState(IMemento memento) {
		memento.putString(KEY, fElement.getHandleIdentifier());
	}
}
