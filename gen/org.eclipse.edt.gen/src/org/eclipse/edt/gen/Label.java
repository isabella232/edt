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
package org.eclipse.edt.gen;

public class Label {
	private String name;
	private String flag;
	public static final String LABEL_NAME = "EzeLabel_";
	private int type;
	public static final int LABEL_TYPE_GENERIC = 0;
	public static final int LABEL_TYPE_CASE = 1;
	public static final int LABEL_TYPE_FOR = 2;
	public static final int LABEL_TYPE_FOREACH = 3;
	public static final int LABEL_TYPE_IF = 4;
	public static final int LABEL_TYPE_OPENUI = 5;
	public static final int LABEL_TYPE_WHILE = 6;

	public Label(EglContext ctx, int type) {
		this.name = LABEL_NAME + ctx.nextTempIndex();
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
}
