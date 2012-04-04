/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.wizard.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.Separator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public abstract class ElementWizardPage extends WizardPage {
	
	private IStatus fCurrStatus;
	private boolean fPageVisible;

	/**
	 * @param pageName
	 */
	public ElementWizardPage(String pageName) {
		super(pageName);
		fPageVisible = false;
		fCurrStatus = new StatusInfo();
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		fPageVisible= visible;
		
		if (visible && shouldResetErrorStatus() && fCurrStatus.matches(IStatus.ERROR)) {
			StatusInfo status= new StatusInfo();
			status.setError("");  //$NON-NLS-1$
			fCurrStatus= status;
		} 
		updateStatus(fCurrStatus);
	}	
	
	protected boolean shouldResetErrorStatus(){
		return true;
	}
	
	/**
	 * Creates a separator line. Expects a <code>GridLayout</code> with at least 1 column.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createSeparator(Composite composite, int nColumns) {
		(new Separator(SWT.SEPARATOR | SWT.HORIZONTAL)).doFillIntoGrid(composite, nColumns, convertHeightInCharsToPixels(1));		
	}

	/**
	 * Updates the status line and the ok button according to the given status
	 * 
	 * @param status status to apply
	 */
	protected void updateStatus(IStatus status) {
		fCurrStatus= status;
		setPageComplete(!status.matches(IStatus.ERROR));
		if (fPageVisible) {
			StatusUtil.applyToStatusLine(this, status);
		}
	}
	
	/**
	 * Updates the status line and the ok button according to the status evaluate from
	 * an array of status. The most severe error is taken.  In case that two status with 
	 * the same severity exists, the status with lower index is taken.
	 * 
	 * @param status the array of status
	 */
	protected void updateStatus(IStatus[] status) {
		updateStatus(StatusUtil.getMostSevere(status));
	}

	/**
	 * Returns the recommended maximum width for text fields (in pixels). This
	 * method requires that createContent has been called before this method is
	 * call. Subclasses may override to change the maximum width for text 
	 * fields.
	 * 
	 * @return the recommended maximum width for text fields.
	 */
	protected int getMaxFieldWidth() {
		return convertWidthInCharsToPixels(40);
	}	
	
	//reload the value of the controls from the configuration, this should be called when the page become active (by getNextPage() in the wizard)
	public void updateControlValues(){}
	
}
