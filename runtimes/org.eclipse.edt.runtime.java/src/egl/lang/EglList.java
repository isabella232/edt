/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package egl.lang;


public interface EglList<T> extends EglAny, java.util.List<T> {

	public int getMaxSize();
	
	public void setMaxSize(int max); 
	
	public void appendElement(T element);
	
	public void removeElement(T element);
	
	public T getElement(int index);
	public void setElement(T element, int index);

}
