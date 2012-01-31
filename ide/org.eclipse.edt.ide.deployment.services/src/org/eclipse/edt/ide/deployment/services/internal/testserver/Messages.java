/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.internal.testserver;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
private static final String BUNDLE_NAME = "org.eclipse.edt.ide.deployment.services.internal.testserver.Messages"; //$NON-NLS-1$
	
	private Messages() {
		// Do not instantiate
	}
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	public static String ConfigServletBadStatus;
}
