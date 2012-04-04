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
package org.eclipse.edt.ide.core.internal.search;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;

/**
 * A type info element that represent an unresolveable type. This can happen if
 * the search engine reports a type name that doesn't exist in the workspace.
 */
public class UnresolvablePartInfo extends PartInfo {
	
	private final String fPath;
	
	public UnresolvablePartInfo(String pkg, String name, char[][] enclosingTypes, String path) {
		super(pkg, name, enclosingTypes);
		fPath= path;
	}
	
	public int getElementType() {
		return PartInfo.UNRESOLVABLE_PART_INFO;
	}
	
	public String getPath() {
		return fPath;
	}
	
	public IPath getPackageFragmentRootPath() {
		return new Path(fPath);
	}
	
	protected IEGLElement getEGLElement(IEGLSearchScope scope) {
		return null;
	}

    /* (non-Javadoc)
     * @see com.ibm.etools.egl.model.internal.core.search.PartInfo#getPartType()
     */
    public char getPartType() {
        return 0;
    }
}
