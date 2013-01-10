/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.jface.text.ITextViewer;

public class EGLImportProposalHandler extends EGLAbstractProposalHandler {
	public EGLImportProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals() {
		//JON60 - needsupport for package names
		List proposals = new ArrayList();
		List packages = new ArrayList();
		try {
			new SearchEngine().searchAllPartNames(ResourcesPlugin.getWorkspace(),
				null,
				null,
				IIndexConstants.PREFIX_MATCH,
				IEGLSearchConstants.CASE_INSENSITIVE,
				IEGLSearchConstants.PACKAGE,
				SearchEngine.createWorkspaceScope(),
				new PartInfoRequestor(packages),
				IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				null);
		} catch (EGLModelException e) {
			EGLLogger.log(this, e);
		}
		
		return proposals;
	}

}
