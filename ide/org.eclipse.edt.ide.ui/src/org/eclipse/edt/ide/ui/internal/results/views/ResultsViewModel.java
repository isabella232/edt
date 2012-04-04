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

/**
 *
 * This model contains information needed to know which logic part script corresponds
 * to which EGL Syntax Check list viewer.   The viewer controller is enough information
 * to uniquely identify the part.  I could have just stored that information directly instead
 * of using a model, but in case we decide that we need to cache more information, I've
 * used this approach.
 */
 
public class ResultsViewModel {
	
	private Object resultsIdentifier;

/*
 * Creates an instance of this model and caches away the piece of information needed to uniquely identify
 * this part.
 *
 * ResultsViewModel constructor comment.
 */
public ResultsViewModel(Object resultsIdentifier) {
	
	super();
	this.resultsIdentifier = resultsIdentifier;
}
/**
 * Return the viewerController
 */
public Object getResultsIdentifier() {
	return resultsIdentifier;
}
}
