/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

/**
 * Use this class to provide a similar functionality as a MultiPageEditorPart
 * For viewers only:
 *
 * Provides external functionality of:
 *   : getViewers;
 *   : addViewer
 *   : removeViewer.
 *   : getActiveViewer
 *	 : getContainer() for creating additional viewers
 *	 : setPageText();
 */

// provider to selection event for the tabs.
// This includes user selection and programmatical selection

public class ResultsMultiPageViewer extends AbstractMultiPageViewer implements ISelectionProvider {
	
/**
 * ResultsMultiPageViewer constructor comment.
 */
public ResultsMultiPageViewer(Composite parent) {
	super(parent);
}
/**
 * Returns the currently selected Viewer
 * return null: if non currently selected
 * Creation date: (7/1/2001 10:22:50 AM)
 */

 // maybe to only return currentViewer?
 public CTabItem getTabForCurrentViewer() {
	 return getContainer().getItem(getActivePageIndex());
	 

}
/**
 * Passes on the results to be displayed in the appropriate window
 *
 */
public void setResults (java.util.List problems, CTabItem theTab) {

	int theIndex = ((CTabFolder) getContainer()).indexOf(theTab);
	
	if (getViewer(theIndex) != null)
		getViewer(theIndex).setInput(problems);
}
}
