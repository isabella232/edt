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

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode;

public interface IGenVariableResolver {
	//internal use
	public static final String TypeName = "${typeName}";
	public static final String WidgetType = "${widgetType}";
	
	//open to user
	public static final String WidgetName = "${widgetName}";
	public static final String FormManagerName = "${formManagerName}";
	public static final String BindingName = "${bindingName}";
	public static final String ShortBindingName = "${shortBindingName}";
	public static final String LabelText = "${labelText}";
	public static final String ChildWidgets = "${childWidgets}";
	public static final String Rows = "${rows}";
	public static final String Row = "${row}";
	public static final String Columns = "${columns}";
	public static final String Column = "${column}";
	public static final String StartRepeat = "${startRepeat}";
	public static final String EndRepeat = "${endRepeat}";
	public static final String ControllerName = "${controllerName}";
	public static final String NameLabelName = "${nameLabelName}";
	public static final String ErrorLabelName = "${errorLabelName}";
	public static final String FormFieldName = "${formFieldName}";
	public static final String FormFields = "${formFields}";
	public static final String ValidStateSetterName = "${validStateSetterName}";	
	public void resolve(GenNode genNode);
}
