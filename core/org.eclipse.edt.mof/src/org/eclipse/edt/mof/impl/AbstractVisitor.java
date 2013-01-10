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
package org.eclipse.edt.mof.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.edt.mof.EVisitor;


public class AbstractVisitor implements EVisitor {
	
	private static final Object UNRESOLVED_METHOD = new Object();
	private static final int CACHE_MAX;
	private static Map<Class<?>, Map<Class<?>, Map<String, Object>>> methodsByClass = new HashMap<Class<?>, Map<Class<?>, Map<String, Object>>>();
	
	private boolean allowRevisit = true;
	private boolean trackParent = false;
	private Set<Object> visited;
	private Set<Object> endVisited;
	private Stack<Object> parents;
	private Stack<Integer> slotIndices;
	private Object returnData;
	
	static {
		int value = -1;
		String max = System.getProperty("edt.mof.visitor.cache.max", null);
		if (max != null) {
			try {
				value = Integer.parseInt(max);
			}
			catch (Exception e) {
			}
		}
		
		if (value == -1) {
			value = 1000;
		}
		CACHE_MAX = value;
	}
	
	public Set<Object> getVisited() {
		return visited;
	}
	public Set<Object> getEndVisited() {
		return endVisited;
	}

	public void setVisited(Set<Object> visited) {
		this.visited = visited;
	}

	public void setEndVisited(Set<Object> endVisited) {
		this.endVisited = endVisited;
	}

	public void disallowRevisit() {
		allowRevisit = false;
		visited = new HashSet<Object>();
		endVisited = new HashSet<Object>();
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
	
	public Stack<Object> getParents() {
		return parents;
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
			if (alreadyEndVisited(obj)) return;
			else endVisited.add(obj);
		}
		invokeEndVisit(clazz, obj);
	}

	public boolean willVisit(Object obj) {
		if (!allowRevisit) {
			if (alreadyVisited(obj)) { 
				return false;
			}
		}
		return true;
			
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
		// Get the method cache for the current class, which are cached by parameter type
		Map<Class<?>, Map<String, Object>> methodsForThisClassByParameterType = methodsByClass.get(this.getClass());
		
		if(methodsForThisClassByParameterType == null){
			methodsForThisClassByParameterType = new LinkedHashMap<Class<?>, Map<String,Object>>() {
				private static final long serialVersionUID = 1L;
				@Override
				protected boolean removeEldestEntry(java.util.Map.Entry<Class<?>, Map<String, Object>> eldest) {
					return size() > CACHE_MAX;
				}
			};
			methodsByClass.put(this.getClass(), methodsForThisClassByParameterType);
		}
		
		// Get all of the methods with the specified parameter type from this class, which are cached again by name
		Map<String, Object> methodsByName = methodsForThisClassByParameterType.get(clazz);
        
        if(methodsByName == null){
        	methodsByName = new HashMap<String, Object>(2); // We only use 2 names
        	methodsForThisClassByParameterType.put(clazz, methodsByName);
        }
        
        Object mapValue = methodsByName.get(methodName);
        if(mapValue == null){
        	// We have never tried to resolve this method before
    		try {
	            Method[] classMethods = this.getClass().getMethods();
	            for (Method nextMethod : classMethods) {
	                if(nextMethod.getParameterTypes().length == 1){
	                    if(nextMethod.getParameterTypes()[0].equals(clazz)){
	                    	if(nextMethod.getName().equals(methodName)){
	                            method = nextMethod;
	                            methodsByName.put(methodName, method);
	                            break;
	                        }
	                    }
	                }
	            }
	            if(method == null){
	            	methodsByName.put(methodName, UNRESOLVED_METHOD);
	            }
	        } catch (SecurityException e) {
	            throw new RuntimeException(e);
	        } 
    	}else if(mapValue != UNRESOLVED_METHOD){
    		// Assign the method that we resolved in the past
    		method = (Method)mapValue;
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

	private boolean alreadyEndVisited(Object obj) {
		return endVisited.contains(obj);
	}
	
	public void setReturnData(Object returnData) {
		this.returnData = returnData;
	}

	public Object getReturnData() {
		return returnData;
	}

}
