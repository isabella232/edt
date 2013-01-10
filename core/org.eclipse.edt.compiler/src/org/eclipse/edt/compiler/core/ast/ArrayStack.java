/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.core.ast;


public class ArrayStack {
	
	Object[] ary;
	int top = -1;
	
	public ArrayStack() {
		this(10);
	}
	
	public ArrayStack(int initialCapacity) {
		ary = new Object[initialCapacity];
	}

	public Object elementAt(int i) {
		return ary[i];
	}

	public void removeAllElements() {
		top = -1;
	}

	public void push(Object object) {
		if(top+1 == ary.length) {
			Object[] newAry = new Object[ary.length*2];
			System.arraycopy(ary, 0, newAry, 0, ary.length);
			ary = newAry;
		}
		ary[++top] = object;
	}

	public Object peek() {
		return ary[top];
	}

	public Object pop() {
		Object result = ary[top];
		ary[top] = null;
		top -= 1;
		return result;
	}

	public int size() {
		return top+1;
	}

	public boolean empty() {
		return top == -1;
	}

	public Object get(int i) {
		return ary[i];
	}

	public Object[] toArray(Object[] a) {
		if (a.length < top+1)
            a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), top+1);
		
		System.arraycopy(ary, 0, a, 0, top+1);

        if (a.length > top+1)
            a[top+1] = null;

        return a;
	}
}
