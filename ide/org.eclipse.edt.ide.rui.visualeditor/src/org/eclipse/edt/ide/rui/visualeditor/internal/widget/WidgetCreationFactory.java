/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.gef.requests.CreationFactory;

public class WidgetCreationFactory implements CreationFactory, IExecutableExtension{
	protected String _strObjectType = null;

	protected static WidgetCreationFactory _instance = null;
	
	/**
	 * Do not create, use getInstance instead.
	 */
	private WidgetCreationFactory(){
	}
	
	/**
	 * 
	 */
	public static WidgetCreationFactory getInstance(){
		if( _instance == null )
			_instance = new WidgetCreationFactory();
		return _instance;
	}
	
	/**
	 * Declared in CreationFactory
	 */
	public Object getNewObject() {
		return null;
	}

	/**
	 * Declared in CreationFactory
	 */
	public Object getObjectType() {
		return null;
	}

	/**
	 * Declared in IExecutableExtension
	 */
	public void setInitializationData( IConfigurationElement config, String propertyName, Object data ) throws CoreException {
	}

	/**
     * 
     */
	public final void setObjectType( String strObjectType ) {
		_strObjectType = strObjectType;
	}
}
