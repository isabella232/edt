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


public interface IClientProxy {
	
	public void addContext(IContext context);
    public void removeContext(IContext context);
	
    public void requestWidgetPositions(IContext context);
	public void terminateSession(IContext context);
	public void refreshBrowserContents(IContext context, final String location, final long modificationStamp, final boolean force);
	
}
