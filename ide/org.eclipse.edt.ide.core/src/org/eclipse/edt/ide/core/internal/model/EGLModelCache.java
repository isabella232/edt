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
package org.eclipse.edt.ide.core.internal.model;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.ide.core.model.IEGLElement;


/**
 * The cache of java elements to their respective info.
 */
public class EGLModelCache {
	public static final int DEFAULT_PROJECT_SIZE = 5;  // average 25552 bytes per project.
	public static final int PKG_CACHE_SIZE = 500;
	public static final int OPENABLE_CACHE_SIZE = 2000;
	
	/**
	 * Active EGL Model Info
	 */
	protected EGLModelInfo modelInfo;
	
	/**
	 * Cache of open projects and package fragment roots.
	 */
	protected Map rootCache;

	/**
	 * Cache of open projects and package fragment roots.
	 */
	protected Map <IEGLElement, Object> projectCache;
	
	/**
	 * Cache of open package fragments
	 */
	protected Map pkgCache;

	/**
	 * Cache of open compilation unit and class files
	 */
	protected OverflowingLRUCache openableCache;

	/**
	 * Cache of open children of openable EGL Model EGL elements
	 */
	protected Map childrenCache;
	
public EGLModelCache() {
	this.rootCache = new HashMap(50);
	this.projectCache = new HashMap <IEGLElement, Object>(DEFAULT_PROJECT_SIZE);
	this.pkgCache = new HashMap(PKG_CACHE_SIZE);
	this.openableCache = new ElementCache(OPENABLE_CACHE_SIZE);
	this.childrenCache = new HashMap(OPENABLE_CACHE_SIZE*20); // average 20 chilren per openable
}

public double openableFillingRatio() {
	return this.openableCache.fillingRatio();
}
public int pkgSize() {
	return this.pkgCache.size();
}
	
/**
 *  Returns the info for the element.
 */
public Object getInfo(IEGLElement element) {
	try {
	switch (element.getElementType()) {
		case IEGLElement.EGL_MODEL:
			return this.modelInfo;
		case IEGLElement.EGL_PROJECT: //TODO For the project is different...
			return this.projectCache.get(element);
		case IEGLElement.PACKAGE_FRAGMENT_ROOT: 
			return this.rootCache.get(element);
		case IEGLElement.PACKAGE_FRAGMENT:
			return this.pkgCache.get(element);
		case IEGLElement.EGL_FILE:
			return this.openableCache.get(element);
		default:
			return this.childrenCache.get(element);
	}}
	catch(Exception ex) {
		ex.printStackTrace();
		return null;
	}
}

/**
 *  Returns the info for this element without
 *  disturbing the cache ordering.
 */
protected Object peekAtInfo(IEGLElement element) {
	switch (element.getElementType()) {
		case IEGLElement.EGL_MODEL:
			return this.modelInfo;
		case IEGLElement.EGL_PROJECT:
			return this.projectCache.get(element);
		case IEGLElement.PACKAGE_FRAGMENT_ROOT:
			return this.rootCache.get(element);
		case IEGLElement.PACKAGE_FRAGMENT:
			return this.pkgCache.get(element);
		case IEGLElement.EGL_FILE:
			return this.openableCache.get(element);
		default:
			return this.childrenCache.get(element);
	}
}

protected void resetJarTypeCache() {
//	this.jarTypeCache = new LRUCache((int) (DEFAULT_OPENABLE_SIZE * getMemoryRatio()));
}

/**
 * Remember the info for the element.
 */
protected void putInfo(IEGLElement element, Object info) {
	switch (element.getElementType()) {
		case IEGLElement.EGL_MODEL:
			this.modelInfo = (EGLModelInfo) info;
			break;
		case IEGLElement.EGL_PROJECT:
			this.projectCache.put(element, info);
			break;
		case IEGLElement.PACKAGE_FRAGMENT_ROOT:
			this.rootCache.put(element, info);
			break;
		case IEGLElement.PACKAGE_FRAGMENT:
			this.pkgCache.put(element, info);
			break;
		case IEGLElement.EGL_FILE:
			this.openableCache.put(element, info);
			break;
		default:
			this.childrenCache.put(element, info);
	}
}
/**
 * Removes the info of the element from the cache.
 */
protected void removeInfo(IEGLElement element) {
	switch (element.getElementType()) {
		case IEGLElement.EGL_MODEL:
			this.modelInfo = null;
			break;
		case IEGLElement.EGL_PROJECT:
			this.projectCache.remove(element);
			break;
		case IEGLElement.PACKAGE_FRAGMENT_ROOT:
			this.rootCache.remove(element);
			break;
		case IEGLElement.PACKAGE_FRAGMENT:
			this.pkgCache.remove(element);
			break;
		case IEGLElement.EGL_FILE:
			this.openableCache.remove(element);
			break;
		default:
			this.childrenCache.remove(element);
	}
}
}
