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
package org.eclipse.edt.ide.ui.project.templates;

import org.eclipse.jface.wizard.IWizard;

public interface IProjectTemplateWizard extends IWizard {
	/**
	 * Returns the parent wizard this template wizard is sitting in.
	 * 
	 * @return
	 */
	public IWizard getParentWizard();
	
	/**
	 * Sets the parent wizard.
	 * 
	 * @param parent
	 */
	public void setParentWizard(IWizard parent);
	
	/**
	 * Sets the template associated with this wizard.
	 * 
	 * @param template
	 */
	public void setTemplate(IProjectTemplate template);
	
	/**
	 * Returns the template associated with this wizard.
	 * 
	 * @return
	 */
	public IProjectTemplate getTemplate();
}
