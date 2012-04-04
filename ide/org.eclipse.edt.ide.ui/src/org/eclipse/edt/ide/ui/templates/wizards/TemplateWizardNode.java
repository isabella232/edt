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
package org.eclipse.edt.ide.ui.templates.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.IWizard;

import org.eclipse.edt.ide.ui.internal.project.wizards.WizardNode;
import org.eclipse.edt.ide.ui.templates.ITemplate;

public class TemplateWizardNode extends WizardNode {
	private IConfigurationElement configElement;
	private IWizard parentWizard;
	private ITemplate template;

	public TemplateWizardNode(ITemplate template, IConfigurationElement configElement) {
		super();
		
		this.template = template;
		this.configElement = configElement;
	}
	
	protected IWizard createWizard() throws CoreException {
		try {
			IWizard wiz = (IWizard)configElement.createExecutableExtension("wizardClass");
			if (wiz instanceof ITemplateWizard) {
				((ITemplateWizard)wiz).setParentWizard(parentWizard);
				((ITemplateWizard)wiz).setTemplate(template);
			}
			return wiz;
		} catch (Exception ex) {
			ex.printStackTrace();				
		}
		
		return null;
	}
	
	
	public IWizard getParentWizard() {
		return parentWizard;
	}

	public void setParentWizard(IWizard parentWizard) {
		this.parentWizard = parentWizard;
		
		if (isContentCreated()) {
			((ITemplateWizard)getWizard()).setParentWizard(parentWizard);
		}
	}

	public IConfigurationElement getConfigElement() {
		return configElement;
	}

	public ITemplate getTemplate() {
		return template;
	}
}
