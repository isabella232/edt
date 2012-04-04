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
package org.eclipse.edt.compiler.generationServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.generationServer.parts.IElementInfo;
import org.eclipse.edt.compiler.generationServer.parts.IElementInfoVisitor;
import org.eclipse.edt.compiler.generationServer.parts.IEmbeddedMemberInfo;
import org.eclipse.edt.compiler.generationServer.parts.IExternalMemberInfo;
import org.eclipse.edt.compiler.generationServer.parts.IPartInfo;
import org.eclipse.edt.compiler.generationServer.parts.IUnresolvedPartInfo;


public class GenerationServer {
	private static List listeners;

	private GenerationServer() {
		super();
		// This class is never to be instantiated
	}
	
	public static boolean hasListeners() {
		return getListeners().size() > 0;
	}
	
	public static void addListener(IGenerationListener listener) {
		getListeners().add(listener);
	}
	
	public static void removeListener(IGenerationListener listener) {
		getListeners().remove(listener);
		
	}
	
	private static List getListeners() {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		return listeners;
	}
	
	public static void begin() {
		Iterator i = getListeners().iterator();
		while (i.hasNext()) {
			IGenerationListener listener = (IGenerationListener)i.next();
			listener.begin();
		}
	}
	
	public static void end() {
		Iterator i = getListeners().iterator();
		while (i.hasNext()) {
			IGenerationListener listener = (IGenerationListener)i.next();
			listener.end();
		}
	}

	public static void acceptGeneratedPart(IPartInfo part) {
		if (part == null) {
			return;
		}
		
		Iterator i = getListeners().iterator();
		while (i.hasNext()) {
			IGenerationListener listener = (IGenerationListener)i.next();
			listener.acceptGeneratedPart(part);
		}
	}

	public static void acceptAssociatedPart(IPartInfo part) {
		if (part == null) {
			return;
		}
		
		Iterator i = getListeners().iterator();
		while (i.hasNext()) {
			IGenerationListener listener = (IGenerationListener)i.next();
			listener.acceptAssociatedPart(part);
		}
	}

	public static void reset() {
		listeners = null;
	}
	
	//Given an array of partInfos, return an array of all parts (including referenced parts).
	//All parts in the array will be unique.
	public static IPartInfo[] getAllUniqueReferecedParts(IPartInfo[] parts) {
		List list = new ArrayList();
		for (int i = 0; i < parts.length; i++) {
			addAllUnique(parts[i].getAllReferencedParts(), list);
		}
		
		return (IPartInfo[])list.toArray(new IPartInfo[list.size()]);
	}

	//Given an array of ElementInfos, return an array of all elements (including referenced parts, nested functions, referenced programs, etc).
	//All parts in the array will be unique.
	public static IElementInfo[] getAllUniqueReferecedElements(IElementInfo[] elements) {
		final List list = new ArrayList();
		IElementInfoVisitor visitor = new IElementInfoVisitor() {

			public void endVisit(IPartInfo info) {
			}
			public void endVisit(IEmbeddedMemberInfo info) {
			}
			public void endVisit(IUnresolvedPartInfo info) {
			}
			public void endVisit(IExternalMemberInfo info) {
			}

			public boolean visit(IPartInfo info) {
				addAllUnique(info.getAllReferencedParts(), list);
				return false;
			}

			public boolean visit(IEmbeddedMemberInfo info) {
				if (!list.contains(info)) {
					list.add(info);
				}
				return false;
			}

			public boolean visit(IUnresolvedPartInfo info) {
				if (!list.contains(info)) {
					list.add(info);
				}
				return false;
			}
			public boolean visit(IExternalMemberInfo info) {
				if (!list.contains(info)) {
					list.add(info);
				}
				return false;
			}
			
		};
			
		for (int i = 0; i < elements.length; i++) {
			
			elements[i].accept(visitor);
		}
		
		return (IElementInfo[])list.toArray(new IElementInfo[list.size()]);
	}

	private static void addAllUnique(Object[] parts, List list) {
		for (int i = 0; i < parts.length; i++) {
			if (!(list.contains(parts[i]))) {
				list.add(parts[i]);
			}
		}
	}
}
