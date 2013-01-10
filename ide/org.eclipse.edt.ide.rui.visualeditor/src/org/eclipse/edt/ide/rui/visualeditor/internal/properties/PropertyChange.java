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
package org.eclipse.edt.ide.rui.visualeditor.internal.properties;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;

/**
 * A class that encapsulates a property change that can be used in a list of property changes.
 */
public class PropertyChange {
	public String				strPropertyID	= null;
	public String				strPropertyType	= null;
	public WidgetPropertyValue	valueOld		= null;
	public WidgetPropertyValue	valueNew		= null;
	public WidgetPart			widget			= null;
}
