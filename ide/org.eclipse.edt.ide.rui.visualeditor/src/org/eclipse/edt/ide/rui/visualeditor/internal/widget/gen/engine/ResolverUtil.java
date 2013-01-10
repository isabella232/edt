/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.engine;

public class ResolverUtil {
	public static String alaisWidgetName(String widgetName){
		int index = widgetName.lastIndexOf("_");
		if(index != -1){
			return widgetName.substring(0, index);
		}else{
			return widgetName;
		}
	}
}
