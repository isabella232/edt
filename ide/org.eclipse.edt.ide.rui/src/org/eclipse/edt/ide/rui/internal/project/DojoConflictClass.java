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

public class DojoConflictClass extends AbstractWidgetLibraryConflict implements IWidgetLibraryConflict {

	public static final String[] conflictLibs = {"org.eclipse.edt.rui.dojo_0.8.1", "org.eclipse.edt.rui.dojo.remote_0.8.1", "org.eclipse.edt.rui.dojo.mobile_0.7.0"}; 
	public DojoConflictClass() {
		super();
	}
	
	@Override
	public boolean isConflict(String library) {
		for(String conflictLib : conflictLibs){
			if(conflictLib.equals(library))
				return true;
		}
		return false;
	}
	
	
}
