/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.search;

import org.eclipse.core.runtime.IPath;

/**
 * A <code>IPartNameRequestor</code> collects search results from a <code>searchAllPartNames</code>
 * query to a <code>SearchEngine</code>. Clients must implement this interface and pass
 * an instance to the <code>searchAllTypeNames(...)</code> method. Only top-level and
 * member types are reported. Local types are not reported.
 * <p>
 * This interface may be implemented by clients.
 * </p>
 */
public interface IPartNameRequestor {

void acceptPart(char[] packageName, char[] simpleTypeName, char partType, char[][] enclosingTypeNames, String path, IPath projectPath);
}
