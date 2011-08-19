/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.server;

import java.util.HashMap;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.deployment.javascript.DesignPaneHTMLGenerator;
import org.eclipse.edt.gen.deployment.javascript.HTMLGenerator;



/**
 * Load the file being requested by the browser
 */
public class DesignPaneContentProvider extends WorkingCopyContentProvider {

	public DesignPaneContentProvider(EvEditorProvider editorProvider) {
		super(editorProvider);
	}

	protected HTMLGenerator getDevelopmentGenerator(AbstractGeneratorCommand processor, String egldd, HashMap eglProperties, String userMsgLocale, String runtimeMsgLocale) {
		return new DesignPaneHTMLGenerator(processor, egldd, eglProperties, userMsgLocale, runtimeMsgLocale);
	}	
}
