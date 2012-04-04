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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;

public interface IWorkingCopyBuildPathEntry extends IBuildPathEntry {
	IPartOrigin getPartOrigin(String[] packageName,String partName);
}
