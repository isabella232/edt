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
package org.eclipse.edt.mof.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.edt.mof.EVisitor;


public class AbstractVisitor implements EVisitor {
	private boolean allowRevisit = true;
	private boolean trackParent = false;
	private Set<Object> visited;
	private Stack<Object> parents;
	private Stack<Integer> slotIndices;
	private Object returnData;
	
	public Set<Object> getVisited() {
		return visited;
	}

	public void setVisited(Set<Object> visited) {
		this.visited = visited;
	}

	public void disallowRevisit() {
		allowRevisit = false;
		visited = new HashSet<Object>();
	}
	
	public void allowParentTracking() {
		trackParent = true;
		parents = new Stack<Object>();
		slotIndices = new Stack<Integer>();
	}
	
	public boolean isTrackingParent() {
		return trackParent;
	}
	
	public void pushParent(Object parent) {
		parents.push(parent);
	}
	
	public Object popParent() {
		return parents.pop();
	}
	
	public Object getParent() {
		return parents.peek();
	}
	
	public void pushSlotIndex(int index) {
		slotIndices.push(index);
	}
	
	public Integer popSlotIndex() {
		return slotIndices.pop();
	}
		
	public int getParentSlotIndex() {
		return slotIndices.peek();
	}

	public void primEndVisit(Object obj) {
		Class<?> clazz = obj.getClass();
		if (!allowRevisit) {
			if (alreadyVisited(obj)) return;
		}
		invokeEndVisit(clazz, obj);
	}

	public boolean primVisit(Object obj) {
		if (!allowRevisit) {
			if (alreadyVisited(obj)) return false;
			else visited.add(obj);
		}
		Class<?> clazz = obj.getClass();
		boolean visitChildren = invokeVisit(clazz, obj);
		return visitChildren;
	}

	@Override
	public void endVisit(Object obj) {
		// Do nothing at the super type level
	}

	@Override
	public boolean visit(Object obj) {
		return true;
	}

	private boolean invokeVisit(Class<?> clazz, Object obj) {
		boolean visitChildren = false;
		Method method;
		if (clazz.getInterfaces().length == 0) {
			visitChildren = visit(obj);
		}
		else {
			try {
				method = getMethod("visit", clazz.getInterfaces()[0], true);
				if (method == null) {
					visitChildren = visit(obj);
				}
				else {
					visitChildren = (Boolean)method.invoke(this, obj);
				}
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} 
		}
		return visitChildren;

	}
	
	private Method primGetMethod(String methodName, Class<?> clazz) {
        Method method = null;
        try {
            Method[] methods = this.getClass().getMethods();
            for (Method nextMethod : methods) {
                if(nextMethod.getName().equals(methodName)){
                    if(nextMethod.getParameterTypes().length == 1){
                        if(nextMethod.getParameterTypes()[0].equals(clazz)){
                            method = nextMethod;
                            break;
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } 
        return method;
    }
	
	private Method getMethod(String methodName, Class<?> ifaceClass, boolean doGet) {
		Method method = null;
		if (doGet) {
			method = primGetMethod(methodName, ifaceClass);
		}
		if (method == null) {
			for (Class<?>iface : ifaceClass.getInterfaces()) {
				method = primGetMethod(methodName, iface);
				if (method != null) break;
			}
		}
		if (method == null) {
			if (ifaceClass.getInterfaces().length > 0) {
				method = getMethod(methodName, ifaceClass.getInterfaces()[0], false);
			}
		}
		return method;
	}
	
	@SuppressWarnings("unchecked")
	private void invokeEndVisit(Class<?>clazz, Object obj) {
		Method method;
		if (clazz.getInterfaces().length == 0) {
			endVisit(obj);
		}
		else {
			try {
				method = getMethod("endVisit", clazz, true);
				if (method == null) {
					endVisit(obj);
				}
				else {
					method.invoke(this, obj);
				}
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} 
		}
	}
	
	private boolean alreadyVisited(Object obj) {
		return visited.contains(obj);
	}

	public void setReturnData(Object returnData) {
		this.returnData = returnData;
	}

	public Object getReturnData() {
		return returnData;
	}

}
