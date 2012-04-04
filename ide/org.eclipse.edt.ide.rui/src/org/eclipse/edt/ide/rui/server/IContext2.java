/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.edt.ide.rui.server.EvServer.Event;

public interface IContext2 extends IContext {
	public void handleEvent(Event event);
	
	/**
	 * @return true if this context wants the test server used for service invocations.
	 */
	public boolean useTestServer();
}
