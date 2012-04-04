/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen;

public class GenManager {
	private boolean genFromDataView = false;
	private String genRootWidgetName;
	
	private static GenManager instance;
	
	private GenManager(){}
	
	public static GenManager getInstance(){
		if(instance == null){
			instance = new GenManager();
		}
		return instance;
	}
	
	public boolean isGenFromDataView(){
		return genFromDataView;
	}
	
	public void setGenFromDataView(boolean genFromDataView){
		this.genFromDataView = genFromDataView;
	}

	public String getGenRootWidgetName() {
		return genRootWidgetName;
	}

	public void setGenRootWidgetName(String genRootWidgetName) {
		this.genRootWidgetName = genRootWidgetName;
	}
}
