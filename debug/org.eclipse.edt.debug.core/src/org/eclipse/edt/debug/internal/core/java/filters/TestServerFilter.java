/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.core.java.filters;

import org.eclipse.edt.debug.core.java.filters.ExternalPluginClassFilter;

/**
 * Filters the classes of the test server as well as the service contribution classes.
 */
public class TestServerFilter extends ExternalPluginClassFilter
{
	@Override
	public String[] getPluginIds()
	{
		return new String[] { "org.eclipse.edt.ide.testserver", "org.eclipse.edt.ide.deployment.services" }; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
