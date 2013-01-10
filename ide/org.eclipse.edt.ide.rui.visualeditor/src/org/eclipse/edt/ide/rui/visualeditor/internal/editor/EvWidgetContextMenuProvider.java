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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.graphics.Point;


public interface EvWidgetContextMenuProvider {
	public void refreshContextMenu(WidgetPart selectedWidget, MenuManager manager, EvDesignOverlay designOverlay, Point mousePosition);
}
