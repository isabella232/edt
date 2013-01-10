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

import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.ui.wizards.PartOperation;
import org.eclipse.edt.ide.ui.wizards.PartTemplateException;

public class RUIWidgetOperation extends PartOperation {
	private String widgetName;
	public static final String WIDGET_TEMPLATE_ID = "org.eclipse.edt.ide.ui.templates.rui_widget"; //$NON-NLS-1$
	
	public RUIWidgetOperation(HandlerConfiguration configuration, String widgetName) {
		super(configuration);
		this.widgetName = widgetName;
	}

	protected String getFileContents() throws PartTemplateException {
		
		String templateid = WIDGET_TEMPLATE_ID; //$NON-NLS-1$		
		HandlerConfiguration configuration = (HandlerConfiguration) super.configuration;
		String partName = widgetName;
		
		String relativeCSSFile = new Path("css/" + configuration.getProjectName() + ".css").toString();
		
		return getFileContents(
			"widget", //$NON-NLS-1$
			templateid,
			new String[] {
				"${widgetName}", //$NON-NLS-1$
				"${cssFile}", //$NON-NLS-1$
			},
			new String[] {
				partName,
				relativeCSSFile,
			});
	}
}
