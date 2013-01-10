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
package org.eclipse.edt.compiler.internal;

/**
 * @author jshavor
 * 
 * This class represent an EGL system word (function or variable).
 * Most of the time a subclass is instantiated, not this class
 */
public abstract class EGLSystemWord {

	protected String name;
	protected String additonalInformation;   //for content assit
	protected int type;
	private int specialFunctionType;
	protected String library;				// Jeff 11-12 for Dave

	public EGLSystemWord(String name, int type, int specialFunctionType, String library) {
		super();
		this.name = name;
		this.type = type;
		this.specialFunctionType = specialFunctionType;
		this.library = library;
	}

	public String getName() {
		return name;
	}

	public boolean isArrayWord() {
		return library.equals(EGLSystemWordHandler.dynamicArraySystemWord);
	}

	public boolean isSystemFunction() {
		return false;
	}

	public boolean isSystemVariable() {
		return false;
	}

	public boolean isDictionary() {
	    return (type & EGLSystemWordHandler.SYSTEM_WORD_DICTIONARY) != 0;
	}
	
	public boolean isPresentationAttributes() {
		return false;
	}
	
	public boolean isPSBDataRecord() {
		return false;
	}
	
	public boolean isRef() {
		return (type & EGLSystemWordHandler.SYSTEM_WORD_REF) != 0;
	}
	
	/**
	 * Returns whether word represents a function that is only valid for use within a page handler.
	 */
	public boolean isPageHandlerSystemFunction() {
		return (type & EGLSystemWordHandler.SYSTEM_WORD_PAGEHANDLER ) != 0;
	}
	
	/**
	 * Returns whether word represents a function that is only valid for use within a report handler.
	 */
	public boolean isReportHandlerSystemFunction() {
		return (type & EGLSystemWordHandler.SYSTEM_WORD_REPORTHANDLER ) != 0;
	}

	public String toString() {
		return name;
	}

	public int getSpecialFunctionType() {
		return specialFunctionType;
	}

	public int getType() {
		return type;
	}

	public String getAdditonalInformation() {
		return additonalInformation;
	}
	
	public String getLibrary() {
		return library;
	}

}
