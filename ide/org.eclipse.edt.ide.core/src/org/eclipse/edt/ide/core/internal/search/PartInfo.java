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
package org.eclipse.edt.ide.core.internal.search;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.internal.model.Util;

public abstract class PartInfo {

	final String fName;
	final String fPackage;
	final char[][] fEnclosingNames;
	
	public static final int UNRESOLVABLE_PART_INFO= 1;
	public static final int PART_DECL_INFO=2;
	public static final int IFILE_TYPE_INFO= 3;
	
	static final char SEPARATOR= '/';
	static final char EXTENSION_SEPARATOR= '.';
	static final char PACKAGE_PART_SEPARATOR='.';
	
	protected PartInfo(String pkg, String name, char[][] enclosingParts) {
		fPackage= pkg;
		fName= name;
		fEnclosingNames= enclosingParts;
	}	
	
	/**
	 * Returns this type info's kind encoded as an integer.
	 * 
	 * @return the type info's kind
	 */
	public abstract int getElementType();
	
	/**
	 * Returns the path reported by the <tt>IPartNameRequestor</tt>.
	 * 
	 * @return the path of the type info
	 */
	public abstract String getPath();
	
	/**
	 * Returns the <tt>IEGLElement</tt> this type info stands for.
	 *  
	 * @param scope the scope used to resolve the <tt>IEGLElement</tt>.
	 * @return the <tt>IEGLElement</tt> this info stands for.
	 * @throws EGLModelException if an error occurs while access the EGL
	 * model.
	 */
	protected abstract IEGLElement getEGLElement(IEGLSearchScope scope) throws EGLModelException;
	
	/**
	 * Returns the package fragment root path of this type info.
	 * 
	 * @return the package fragment root as an <tt>IPath</tt>.
	 */
	public abstract IPath getPackageFragmentRootPath();
	
	/**
	 * See IIndexConstants
	 */
	public abstract char getPartType();
	
	/**
	 * Returns the type name.
	 * 
	 * @return the info's type name.
	 */
	public String getPartName() {
		return fName;
	}
	
	/**
	 * Returns the package name.
	 * 
	 * @return the info's package name.
	 */ 
	public String getPackageName() {
		return fPackage;
	}

	/**
	 * Returns true if the info is enclosed in the given scope
	 */
	public boolean isEnclosed(IEGLSearchScope scope) {
		boolean enclosing = scope.encloses(getPath());
		//for eglar, also should verify if the part's project is in the scope
		if(enclosing && this instanceof PartDeclarationInfo){
			PartDeclarationInfo partInfo = (PartDeclarationInfo)this;
			if(Util.isEGLARFileName(partInfo.getFolder())){
				IPath[] projects = scope.enclosingProjects();
				String eglarRefProj = partInfo.getProject();
				boolean projectEquals = false;
				for(IPath scopeProj: projects){
					String scopeProjName = scopeProj.lastSegment();
					if(scopeProjName.equalsIgnoreCase(eglarRefProj)){
						projectEquals = true;
						break;
					}
				}
				enclosing = projectEquals;
			}
		}
		return enclosing;
	}

	/**
	 * Gets the enclosing name (dot separated).
	 */
	public String getEnclosingName() {
		StringBuffer buf= new StringBuffer();
		if (fEnclosingNames != null) {
			for (int i= 0; i < fEnclosingNames.length; i++) {
				if (i != 0) {
					buf.append('.');
				}			
				buf.append(fEnclosingNames[i]);
			}
		}
		return buf.toString();
	}	
	
	/**
	 * Gets the type qualified name: Includes enclosing type names, but
	 * not package name. Identifiers are separated by dots.
	 */
	public String getPartQualifiedName() {
		if (fEnclosingNames != null && fEnclosingNames.length > 0) {
			StringBuffer buf= new StringBuffer();
			for (int i= 0; i < fEnclosingNames.length; i++) {
				buf.append(fEnclosingNames[i]);
				buf.append('.');
			}
			buf.append(fName);
			return buf.toString();
		}
		return fName;
	}
	
	/**
	 * Gets the fully qualified type name: Includes enclosing type names and
	 * package. All identifiers are separated by dots.
	 */
	public String getFullyQualifiedName() {
		StringBuffer buf= new StringBuffer();
		if (fPackage.length() > 0) {
			buf.append(fPackage);
			buf.append('.');
		}
		if (fEnclosingNames != null) {
			for (int i= 0; i < fEnclosingNames.length; i++) {
				buf.append(fEnclosingNames[i]);
				buf.append('.');
			}
		}
		buf.append(fName);
		return buf.toString();
	}
	
	/**
	 * Gets the fully qualified type container name: Package name or
	 * enclosing type name with package name.
	 * All identifiers are separated by dots.
	 */
	public String getPartContainerName() {
		if (fEnclosingNames != null && fEnclosingNames.length > 0) {
			StringBuffer buf= new StringBuffer();
			if (fPackage.length() > 0) {
				buf.append(fPackage);
			}
			for (int i= 0; i < fEnclosingNames.length; i++) {
				if (buf.length() > 0) {
					buf.append('.');
				}
				buf.append(fEnclosingNames[i]);
			}
			return buf.toString();
		}
		return fPackage;
	}	
	
	/**
	 * Resolves the type in a scope it was searched for.
	 * The parent project of JAR files is the first project found in scope.
	 * Returns null if the type could not be resolved
	 */	
	public IPart resolvePart(IEGLSearchScope scope) throws EGLModelException {
		IEGLElement elem = getEGLElement(scope);
		if (elem instanceof IEGLFile)
			return EGLModelUtil.findPartInEGLFile((IEGLFile)elem, getPartQualifiedName());
		return (IPart)elem;
	}

	/* non java-doc
	 * debugging only
	 */		
	public String toString() {
		StringBuffer buf= new StringBuffer();
		buf.append("path= "); //$NON-NLS-1$
		buf.append(getPath());
		buf.append("; pkg= "); //$NON-NLS-1$
		buf.append(fPackage);
		buf.append("; enclosing= "); //$NON-NLS-1$
		buf.append(getEnclosingName());
		buf.append("; name= ");		 //$NON-NLS-1$
		buf.append(fName);
		return buf.toString();
	}	
}
