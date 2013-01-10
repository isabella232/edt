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
package org.eclipse.edt.ide.ui.internal.project.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.graphics.Point;

public abstract class WizardNode implements IWizardNode {
	
	private IWizard wizard;

	public WizardNode() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizardNode#dispose()
	 */
	public void dispose() {
		if (wizard != null) {
			wizard = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizardNode#getExtent()
	 */
	public Point getExtent() {
		return new Point(-1, -1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizardNode#getWizard()
	 */
	public IWizard getWizard() {
		if (wizard != null)
			return wizard; // we've already created it

		try {
			wizard = createWizard(); // create instance of target wizard
		} catch (CoreException e) {
			// need to pop something out to log
			return null;
		}
		return wizard;
	}
	
	protected abstract IWizard createWizard() throws CoreException;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizardNode#isContentCreated()
	 */
	public boolean isContentCreated() {
		return wizard != null;
	}
	
	/**
	 * returns true if the contributed wizard node is active.
	 * 
	 * @return
	 */
	public boolean isActive() {
		return true;
	}

}
