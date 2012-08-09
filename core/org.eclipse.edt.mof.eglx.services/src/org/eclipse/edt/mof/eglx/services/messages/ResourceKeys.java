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
package org.eclipse.edt.mof.eglx.services.messages;

import java.util.ResourceBundle;



/**
 * @author winghong
 */
public class ResourceKeys{

	private static final String BUNDLE_FOR_KEYS= "org.eclipse.edt.mof.eglx.services.messages.ValidationResources"; //$NON-NLS-1$
	private static ResourceBundle bundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_KEYS);

	public static ResourceBundle getResourceBundleForKeys() {
		return bundleForConstructedKeys;
	}
	
    
    
}
