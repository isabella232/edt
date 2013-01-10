/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout;

import java.util.Collection;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlayDropLocation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;


public class HandlerWidgetLayout extends AbstractWidgetLayout {

	public void setupDropLocations(Collection listDropLocations) {
		// Create the drop location
		//-------------------------
		Rectangle rectBounds = parentComposite.getClientArea();
		EvDesignOverlayDropLocation location = new EvDesignOverlayDropLocation();
		location.setBounds( rectBounds.x, rectBounds.y, rectBounds.width, rectBounds.height );
		location.iIndex = 0;
		location.iLocation = SWT.CENTER;
		location.widgetParent = null;

		listDropLocations.add( location );

	}

}
