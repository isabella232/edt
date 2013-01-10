/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.libraries.wizards;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizardNode;
import org.eclipse.edt.ide.ui.wizards.PartOperation;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;
import org.eclipse.jface.wizard.IWizardNode;

public class LibraryOperation extends PartOperation {


	protected String codeTemplateId;
	
	public LibraryOperation(LibraryConfiguration configuration, String codeTemplateId) {
		super(configuration);
		this.codeTemplateId = codeTemplateId;
	} 
	
	public LibraryOperation(LibraryConfiguration configuration, String codeTemplateId, ISchedulingRule rule) {
		super(configuration, rule);
		this.codeTemplateId = codeTemplateId;
	}

	protected String getFileContents() throws PartTemplateException {
		LibraryConfiguration configuration = (LibraryConfiguration) super.configuration;
		String partName = configuration.getLibraryName();
		
		return getFileContents(
			"library", //$NON-NLS-1$
			codeTemplateId,
			new String[] {
				"${libraryName}" //$NON-NLS-1$
			},
			new String[] {
				partName
			});
	}
}
