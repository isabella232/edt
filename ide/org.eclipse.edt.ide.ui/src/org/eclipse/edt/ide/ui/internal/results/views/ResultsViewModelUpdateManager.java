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

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Item;

/**
 * The class that keeps track of the models and tabs for the EGL Syntax Check Results view
 */

public class ResultsViewModelUpdateManager {
	// a mapping of tab to the corresponding model
	private HashMap tabToModelMap;

	// a mapping of model to the corresponding tab
	private HashMap modelToTabMap;

	/**
	 * EGLSyntaxCheckResultsViewModelUpdateManager constructor comment.
	 */
	public ResultsViewModelUpdateManager() {
		super();
		this.tabToModelMap = new HashMap();
		this.modelToTabMap = new HashMap();

	}
	/**
	 * add the model and tab to both hash maps
	 */
	public void addModel(ResultsViewModel model, Item tab) {
		tabToModelMap.put(tab, model);
		modelToTabMap.put(model, tab);
	}
	/**
	 * This is being used to retrieve the proper tab.
	 *
	 * @param viewerController - which can be used to determine which model and tab this part corresponds to
	 */
	public CTabItem getTabForPart(Object resultsIdentifier) {

		// code to loop thru all the tabs
		for (Iterator iter = modelToTabMap.keySet().iterator(); iter.hasNext();) {

			// check the saved models for a match
			ResultsViewModel model = (ResultsViewModel) iter.next();
			if (model.getResultsIdentifier() == resultsIdentifier) {
				return (CTabItem) modelToTabMap.get(model);
			}
		}

		return null;

	}
	/**
	 * This is being used to retrieve the proper resultsIdentifier.
	 *
	 * @param theTab - which can be used to determine which model and controller this tab corresponds to
	 */
	public Object getResultsIdentifierForTab(CTabItem theTab) {

		// code to loop thru all the models
		for (Iterator iter = tabToModelMap.keySet().iterator(); iter.hasNext();) {

			// check the saved tabs for a match
			CTabItem tab = (CTabItem) iter.next();
			if (tab == theTab) {
				return ((ResultsViewModel) tabToModelMap.get(tab)).getResultsIdentifier();
			}
		}

		return null;

	}
	/**
	 * This method removes the model and corresponding tab when a viewer has been removed.
	 *
	 */
	public void removeModel(Item selTab) {

		ResultsViewModel model = (ResultsViewModel) tabToModelMap.get(selTab);
		if (model != null) {
			tabToModelMap.remove(selTab);
			modelToTabMap.remove(model);
			model = null;
		}
	}
}
