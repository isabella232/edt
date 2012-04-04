/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.builder;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.edt.ide.core.internal.lookup.AbstractASTManager;


/**
 * @author winghong
 */
public class ASTManager extends AbstractASTManager implements IResourceChangeListener{

    private static final ASTManager INSTANCE = new ASTManager();
	
	private ASTManager(){}
	
	public static ASTManager getInstance(){
		return INSTANCE;
	}
	
	public void resourceChanged(IResourceChangeEvent event) {
		switch(event.getType()){
			case IResourceChangeEvent.PRE_BUILD:
				clear();
				break;
			default:
		}		
	}   
}
