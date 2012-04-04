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
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.formatting.ui.CodeFormatterConfigurationBlock;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public class CodeFormatterPreferencePage extends PropertyAndPreferencePage {

	public static final String PREF_ID= "org.eclipse.edt.ide.ui.formatterPreferences"; //$NON-NLS-1$
	public static final String PROP_ID= "org.eclipse.edt.ide.ui.formatterPropertyPage"; //$NON-NLS-1$
	
	private CodeFormatterConfigurationBlock fConfigurationBlock;
	
	public void createControl(Composite parent) {
		
		fConfigurationBlock = new CodeFormatterConfigurationBlock(getProject());
		super.createControl(parent) ;
				
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IUIHelpConstants.FORMATTER_PREFERENCE_PAGE);
	}
	
	protected Control createPreferenceContent(Composite composite) {
		return fConfigurationBlock.createContents(composite);
	}

	protected String getPreferencePageID() {
		return PREF_ID ;
	}

	protected String getPropertyPageID() {
		//Uncomment if there is ever a project property page for formatting
		//return PROP_ID ;
		return null;
	}

	protected boolean hasProjectSpecificOptions(IProject project) {
		return fConfigurationBlock.hasProjectSpecificOptions(project);
	}

	public void dispose() {
		if(fConfigurationBlock != null)
			fConfigurationBlock.dispose();
		super.dispose() ;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
		super.performDefaults();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		if (fConfigurationBlock != null && !fConfigurationBlock.performOk()) {
			return false;
		}	
		return super.performOk();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public void performApply() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performApply();
		}	
		super.performApply();
	}
	
	public boolean performCancel() {
		if (fConfigurationBlock != null && !fConfigurationBlock.performCancel()) {
			return false;
		}			
		return super.performCancel() ;
	}
}
