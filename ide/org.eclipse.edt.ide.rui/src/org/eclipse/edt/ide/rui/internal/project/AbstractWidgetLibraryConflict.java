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
package org.eclipse.edt.ide.rui.internal.project;

public abstract class AbstractWidgetLibraryConflict {

	public AbstractWidgetLibraryConflict() {
		super();
	}

	public boolean isConflict(String[] libraries) {
		for(String library : libraries){
			if(isConflict(library))
				return true;
		}
		return false;
	}

	public abstract boolean isConflict(String library);

}
