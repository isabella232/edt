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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * A structure whose information is collected on the design page and passed to the overlay.
 * The structure holds information about screen capture of the web browser when transparency is not
 * supported by the platform. 
 */
public class EvDesignCaptureInformation {
	public boolean		bCaptureRunning				= false;
	public Control		controlFocusBeforeCapture	= null;
	public Image		imageBrowser				= null;
	public Rectangle	rectCapture					= new Rectangle( 0, 0, 0, 0 );
}
