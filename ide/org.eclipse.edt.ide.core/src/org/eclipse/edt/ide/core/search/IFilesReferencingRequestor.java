/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

/**
 * A <code>IFilesReferencingRequestor</code> collects search results from a <code>searchAllPartsReferencing</code>
 * query to a <code>SearchEngine</code>. Clients must implement this interface and pass
 * an instance to the <code>searchAllPartsReferencing(...)</code> method. Only top-level and
 * parts are reported. Local parts are not reported.
 * <p>
 * This interface may be implemented by clients.
 * </p>
 */
public interface IFilesReferencingRequestor {

void acceptPart(char[] packageName, char[] simpleTypeName, String path);
}
