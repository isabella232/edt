/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.StructuredRecord;


public class StructuredRecordImpl extends StructPartImpl implements StructuredRecord {

	@Override
	public Integer getSizeInBytes() {
		int length = 0;
		for (StructuredField field : getStructuredFields()) {
			if (field.getParent() == null) {
				length += field.getSizeInBytes() * field.getOccurs();
			}
		}
		return length;
	}

	@Override
	public List<StructuredField> getAllLeafItems() {
		List<StructuredField> leaves = new ArrayList<StructuredField>();
		collectAllLeafItems(leaves);
		return leaves;
	}

	@Override
	public void collectAllLeafItems(List<StructuredField> items) {
		for (StructuredField field : getStructuredFields()) {
			field.collectAllLeafItems(items);
		}

	}
	
	@Override
	public boolean isNativeType() {
		return false;
	}

}
