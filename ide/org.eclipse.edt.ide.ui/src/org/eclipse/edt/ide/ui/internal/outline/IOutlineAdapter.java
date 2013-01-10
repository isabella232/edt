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
package org.eclipse.edt.ide.ui.internal.outline;

import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;

public interface IOutlineAdapter {

	// The following are from ITreeContentProvider	
	Object[] getChildren(Object parentElement);
	Object getParent(Object element);
	boolean hasChildren(Object element);
	
	// The following are from ILabelProvider
	Image getImage(Object element);
	String getText(Object element);
	
	// Added for our own use
	IRegion getHighlightRange(Object element);	
	void dispose();

}
