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
package org.eclipse.edt.ide.ui.internal.handlers;

import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.search.ui.ISearchPageContainer;

public class FindProjectReferencesHandler extends SearchHandler {

	public void run() {
		iScope = ISearchPageContainer.SELECTED_PROJECTS_SCOPE;
		iLimitTo = IEGLSearchConstants.REFERENCES;
		super.run();
	}
}
