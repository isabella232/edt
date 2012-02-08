/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.jtopen.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.ui.SOAMessages;
import org.eclipse.edt.ide.ui.internal.wizards.EGLDDBindingWizardPage;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizard;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.RestBindingWizardPage;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.BindingBaseConfiguration;
import org.eclipse.edt.ide.ui.wizards.BindingRestConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class IBMiBindingWizardPageFactory  implements IExecutableExtensionFactory, IExecutableExtension {

	private String pageName;
	
	@Override
	public Object create() throws CoreException {
		return new IBMiBindingWizardPage( pageName );
	}

	@Override
	public void setInitializationData(IConfigurationElement contribution, String arg1, Object arg2) throws CoreException {
		pageName = contribution.getAttribute("name");
		
	}
}
