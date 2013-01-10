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
package org.eclipse.edt.ide.ui.internal.results.views;

import org.eclipse.jface.viewers.ListViewer;

public class EditorAbstractResultsListViewerAction extends AbstractResultsListViewerAction {
	/**
	 * Constructor for EGLUtilitiesResultListViewerAction.
	 * @param text
	 */
	protected EditorAbstractResultsListViewerAction(String text) {
		super(text);
	}
	
	public EditorAbstractResultsListViewerAction(String text, AbstractResultsViewPart viewPart, int type) {
		super(text, viewPart, type);
	}

	public ListViewer getCurrentViewer() {
		AbstractResultsViewPart castViewPart = (AbstractResultsViewPart) getViewPart();
		return (ListViewer) castViewPart.getMultiPageViewer().getViewer(castViewPart.getMultiPageViewer().getActivePageIndex());
	}
}
