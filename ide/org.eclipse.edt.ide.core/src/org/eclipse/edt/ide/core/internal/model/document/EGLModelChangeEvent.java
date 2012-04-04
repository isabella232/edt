/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.document;


/**
 * @author winghong
 */
public class EGLModelChangeEvent {

	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int CHANGE = 2;
	
	private int type;
	
	public EGLModelChangeEvent(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		switch(type) {
			case ADD: buffer.append("Added: "); break;
			case REMOVE: buffer.append("Removed: "); break;
			case CHANGE: buffer.append("Changed: "); break;
		}
		
		return buffer.toString();
	}

}
