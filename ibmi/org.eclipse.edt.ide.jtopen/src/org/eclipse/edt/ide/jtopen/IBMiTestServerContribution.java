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
package org.eclipse.edt.ide.jtopen;

import org.eclipse.edt.ide.testserver.AbstractTestServerContribution;
import org.eclipse.edt.ide.testserver.ClasspathUtil;
import org.eclipse.edt.ide.testserver.TestServerConfiguration;

/**
 * Contributes the EDT JTOpen runtime to the test server's classpath.
 */
public class IBMiTestServerContribution extends AbstractTestServerContribution {
	
	@Override
	public String[] getClasspathAdditions(TestServerConfiguration config) {
		String entry = ClasspathUtil.getClasspathEntry("org.eclipse.edt.runtime.java.jtopen"); //$NON-NLS-1$
		if (entry != null) {
			return new String[]{entry};
		}
		return null;
	}
}
