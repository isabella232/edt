/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget;

public class InsertWidgetWizardUtil {
	public static boolean isAnEmbedRecord(InsertDataNode insertDataNode){
		if(!insertDataNode.isArray() && insertDataNode.getNodeType().equals(InsertDataNode.NodeType.TYPE_RECORD_ALL) && insertDataNode.getParent() != null){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isAPrimitiveArrayInRecord(InsertDataNode insertDataNode){
		if(insertDataNode.getParent() != null && insertDataNode.getParent().getNodeTypeDetails().contains(InsertDataNode.NodeTypeDetail.TYPE_RECORD_WITH_PRIMITIVE_ARRAY)
				&& insertDataNode.getNodeType().equals(InsertDataNode.NodeType.TYPE_PRIMITIVE_ALL) && insertDataNode.isArray()){
			return true;
		}
		return false;
	}
}
