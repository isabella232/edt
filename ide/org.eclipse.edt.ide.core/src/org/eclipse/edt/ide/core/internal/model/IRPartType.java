/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

public interface IRPartType {
	int PART_PROGRAM = 1;
	int PART_RECORD = 2;
	int PART_STRUCTURED_RECORD = 3;
	int PART_CLASS_RECORD = 4;
	int PARTTYPE_LIBRARY = 5;
	int PART_INTERFACE = 6;
	int PART_HANDLER = 7;
	int PART_DATAITEM = 9;
	int PART_FUNCTION = 10;
	int PART_LIBRARY = 11;
	int PART_SERVICE = 12;
	int PART_FORM = 13;
	int PART_FORMGROUP = 14;
	int PART_DATATABLE = 15;
	int PART_DELEGATE = 16;
	int PART_EXTERNALTYPE = 17;
	int PART_ENUMERATION = 18;
}
