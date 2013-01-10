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
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class OutlineLabelProvider extends LabelProvider {

	private OutlineAdapterFactory factory;

	public OutlineLabelProvider(OutlineAdapterFactory factory) {
		super();
		this.factory = factory;
	}

	/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
	public IRegion getHighlightRange(Object element) {
			return factory.adapt(element).getHighlightRange(element);
		}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 * 
	 * This method uses an image registery to both cache images and to not fool
	 * with disposing them individually
	 */
	public Image getImage(Object element) {
		return factory.adapt(element).getImage(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		return factory.adapt(element).getText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() { 
		factory.dispose();
		super.dispose();
	}

}
