package org.eclipse.edt.ide.rui.wizards;

import org.eclipse.edt.ide.rui.internal.wizards.RuiNewWizardMessages;
import org.eclipse.edt.ide.rui.wizards.pages.ProjectWizardRUILibraryPage;

public class MobileWebClientProjectTemplateWizard extends
		WebClientProjectTemplateWizard {
	@Override
	public void addPages() {
		super.addPages();
		
		libraryPage.setTitle(RuiNewWizardMessages.MobileWebClientWithServicesProjectTemplateWizard_0);
	}
	
	protected ProjectWizardRUILibraryPage getLibraryPage(){
		return new ProjectWizardRUILibraryPage(RuiNewWizardMessages.MobileRUILibraryPage, template.getWidgetLibraryContainer());
	}

}
