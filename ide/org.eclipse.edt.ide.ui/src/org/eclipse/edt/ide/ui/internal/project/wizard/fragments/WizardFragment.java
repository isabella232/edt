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
package org.eclipse.edt.ide.ui.internal.project.wizard.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.ui.internal.project.wizard.pages.ProjectWizardMainPage;
import org.eclipse.swt.widgets.Composite;

public abstract class WizardFragment {
	
	protected Composite renderOn;
	private ProjectWizardMainPage parentPage;
	private List isCompleteListeners = new ArrayList();
	private boolean isComplete = true;

	public WizardFragment(Composite renderOn, ProjectWizardMainPage parentPage) {
		this.renderOn = renderOn;
		this.parentPage = parentPage;
	}
	
	public abstract Composite renderSection();
	
	public abstract void dispose();

	public ProjectWizardMainPage getParentPage() {
		return parentPage;
	}
	
	/**
	 * Subclasses can override.
	 * Returns <code>true</code> if the fragment contains complete information and the
	 * page that the fragment resides on therefore also complete. 
	 * Should be used when deciding if the *next* button of the wizard should be enabled
	 * @return
	 */
	public boolean isComplete() {
		return this.isComplete;
	}
	
	/**
	 * Sets the isComplete flag and then fires an event to all
	 * registered listeners
	 * @param isComplete
	 */
	protected void setIsComplete(boolean isComplete) {
		/**
		 * set and fire only if the flag value has changed
		 */
		if (this.isComplete != isComplete) {
			this.isComplete = isComplete;
			fireIsCompleteChange();
		}
	}
	
	/**
	 * Register an isComplete listener
	 * @param listener
	 */
	public void registerIsCompleteListener(IisCompleteListener listener) {
		this.isCompleteListeners.add(listener);
	}
	
	/**
	 * Fire an event to all registered isComplete listeners
	 */
	public void fireIsCompleteChange() {
		for (Iterator iter = this.isCompleteListeners.iterator(); iter.hasNext();) {
			IisCompleteListener listener = (IisCompleteListener) iter.next();
			listener.handle(this.isComplete);
		}
	}

}
