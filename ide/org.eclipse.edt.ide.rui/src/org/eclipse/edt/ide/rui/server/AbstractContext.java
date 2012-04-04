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
package org.eclipse.edt.ide.rui.server;

public abstract class AbstractContext implements IContext
{
	private final String url;
	private final Integer key;
	protected IServerContentProvider contentProvider;
	
	public AbstractContext( String url, Integer key, IServerContentProvider provider ) {
		this.url = url;
		this.key = key;
		this.contentProvider = provider;
	}

	public String getUrl() {
		return url;
	}
	
	public Integer getKey() {
		return key;
	}
	
	public IServerContentProvider getContentProvider() {
		return contentProvider;
	}
}
