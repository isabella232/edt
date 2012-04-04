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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import java.util.Comparator;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;


/**
 * Compares widgets by their position.  Widgets that have smaller origins (larger containers) are later in the list.
 * So list is ordered from larger origins to smaller origins.
 */
public class ComparatorWidgetPositions implements Comparator {

	public int compare( Object objA, Object objB ) {
		WidgetPart widgetA = (WidgetPart)objA;
		WidgetPart widgetB = (WidgetPart)objB;
		
		if( widgetA.getBounds().x < widgetB.getBounds().x && widgetA.getBounds().y < widgetB.getBounds().y )
			return 1;
		
		// It doesn't matter if they have equal bounds
		//--------------------------------------------
		return -1;
	}
}
