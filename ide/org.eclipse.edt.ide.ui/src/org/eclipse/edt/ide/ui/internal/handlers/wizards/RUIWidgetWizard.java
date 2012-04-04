/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizard;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;

public class RUIWidgetWizard extends TemplateWizard implements IPageChangingListener {
	private static final String WIZPAGENAME_RUIWidgetWizardPage = "WIZPAGENAME_RUIWidgetWizardPage"; //$NON-NLS-1$
	protected NewHandlerSummaryPage summaryPage;
	protected HandlerConfiguration configuration;
	protected String part;
	
	public RUIWidgetWizard() {
		super();
		setNeedsProgressMonitor(true);
		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());		
	}

	public HandlerConfiguration getConfiguration() {
		return (HandlerConfiguration)((NewHandlerWizard)getParentWizard()).getConfiguration();
	}
	
	public void addPages(){
		summaryPage = new NewHandlerSummaryPage();		
		addPage(summaryPage);
	}
	
	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);

		if (wizardContainer != null) {
			((WizardDialog) wizardContainer).addPageChangingListener(this);
		}
		processInput();
	}
	
	public boolean performFinish() {
		processInput();
		getConfiguration().setHandlerType(HandlerConfiguration.WIDGET_HANDLER);
		if(part == null)
			return false;
		((NewHandlerWizard)getParentWizard()).setContentObj(part);
		return true;
	}

	private void processInput() {
		RUIWidgetOperation op = new RUIWidgetOperation(getConfiguration(), getConfiguration().getFileName());
		try{
			part = op.getFileContents();
			if(summaryPage != null){
				summaryPage.setContent(part);
//				summaryPage.setMessages(getFilteredMessages());
			}			
		}catch (Exception ex) {
			ex.printStackTrace();		 
		}
	}

	@Override
	public void handlePageChanging(PageChangingEvent event) {		
		if (event.getTargetPage() == summaryPage) {
			processInput();			
		}
	}
}
