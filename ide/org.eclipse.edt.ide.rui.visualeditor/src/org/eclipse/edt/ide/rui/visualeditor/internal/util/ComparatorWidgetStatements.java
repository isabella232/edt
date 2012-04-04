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
 * Compares widget statement offset locations.
 */
public class ComparatorWidgetStatements implements Comparator {

	public int compare( Object objA, Object objB ) {
		WidgetPart widgetA = (WidgetPart)objA;
		WidgetPart widgetB = (WidgetPart)objB;

		if( widgetA.getStatementOffset() < widgetB.getStatementOffset() )
			return -1;

		// Since no statements overlap, an equality should never occur
		// Never return equal, otherwise parts will not be added to the tree set
		//----------------------------------------------------------------------
		return 1;
	}
}
