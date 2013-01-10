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
package org.eclipse.edt.ide.ui.internal.wizards.buildpaths;

import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.jface.viewers.ViewerSorter;


public class PPListElementSorter extends ViewerSorter {
	
	private static final int SOURCE= 0;
	private static final int PROJECT= 1;
	private static final int LIBRARY= 2;
	private static final int VARIABLE= 3;
	private static final int CONTAINER= 4;
	private static final int OTHER= 5;
	
	/*
	 * @see ViewerSorter#category(Object)
	 */
	public int category(Object obj) {
		if (obj instanceof PPListElement) {
			switch (((PPListElement)obj).getEntryKind()) {
			case IEGLPathEntry.CPE_LIBRARY:
				return LIBRARY;
			case IEGLPathEntry.CPE_PROJECT:
				return PROJECT;
			case IEGLPathEntry.CPE_SOURCE:
				return SOURCE;
			case IEGLPathEntry.CPE_VARIABLE:
				return VARIABLE;
			case IEGLPathEntry.CPE_CONTAINER:
				return CONTAINER;
			}
		}
		return OTHER;
	}

}
