/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

public class RUIHandlerWizard extends TemplateWizard implements IPageChangingListener {	
	private static final String WIZPAGENAME_RUIHandlerWizardPage = "WIZPAGENAME_RUIHandlerWizardPage"; //$NON-NLS-1$
	protected RUIHandlerWizardPage inputPage;
	protected NewHandlerSummaryPage summaryPage;
	protected HandlerConfiguration configuration;
	protected String part;
	
	public RUIHandlerWizard() {
		super();
		setNeedsProgressMonitor(true);
		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());
	}

	public HandlerConfiguration getConfiguration() {
		return (HandlerConfiguration)((NewHandlerWizard)getParentWizard()).getConfiguration();
	}
	
	public void addPages(){
		inputPage = new RUIHandlerWizardPage(WIZPAGENAME_RUIHandlerWizardPage);
		addPage(inputPage);
		summaryPage = new NewHandlerSummaryPage();
		addPage(summaryPage);
	}
	
	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);

		if (wizardContainer != null) {
			((WizardDialog) wizardContainer).addPageChangingListener(this);
		}
	}
	
	public boolean performFinish() {
		if(inputPage == null){
			processInput(getConfiguration().getHandlerName());			
		}else{
			if (inputPage.isInputNeedsProcessing()) {
				processInput(inputPage.getHandlerTitle());
			}
		}
		getConfiguration().setHandlerType(HandlerConfiguration.HANDLER_HANDLER);
		if(part == null)
			return false;
		((NewHandlerWizard)getParentWizard()).setContentObj(part);			
		return true;
	}

	private void processInput(String handlerName) {
		RUIHandlerOperation op = new RUIHandlerOperation(getConfiguration(), handlerName);
		try{
			part = op.getFileContents();
			if(summaryPage != null){
				summaryPage.setContent(part);
		//		summaryPage.setMessages(getFilteredMessages());
			}	
		}catch (Exception ex) {
			ex.printStackTrace();		 
		}
	}

	@Override
	public void handlePageChanging(PageChangingEvent event) {
		if (event.getCurrentPage() == inputPage && inputPage.isInputNeedsProcessing() && event.getTargetPage() == summaryPage) {
			processInput(inputPage.getHandlerTitle());
		}else if(event.getTargetPage() == inputPage){
			inputPage.updateHandlerName();
		}
	}

	
}
