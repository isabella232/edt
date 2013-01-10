/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.viewsupport;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRootContainer;
import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class EGLElementLabels {
	
	/**
		 * Method names contain parameter types.
		 * e.g. <code>foo(int)</code>
		 */
		public final static int M_PARAMETER_TYPES= 1 << 0;
	
		/**
		 * Method names contain parameter names.
		 * e.g. <code>foo(index)</code>
		 */
		public final static int M_PARAMETER_NAMES= 1 << 1;	
	
		/**
		 * Method names contain thrown exceptions.
		 * e.g. <code>foo throws IOException</code>
		 */
		public final static int M_EXCEPTIONS= 1 << 2;
	
		/**
		 * Method names contain return type (appended)
		 * e.g. <code>foo : int</code>
		 */
		public final static int M_APP_RETURNTYPE= 1 << 3;
	
		/**
		 * Method names contain return type (appended)
		 * e.g. <code>int foo</code>
		 */
		public final static int M_PRE_RETURNTYPE= 1 << 4;	

		/**
		 * Method names are fully qualified.
		 * e.g. <code>java.util.Vector.size</code>
		 */
		public final static int M_FULLY_QUALIFIED= 1 << 5;
	
		/**
		 * Method names are post qualified.
		 * e.g. <code>size - java.util.Vector</code>
		 */
		public final static int M_POST_QUALIFIED= 1 << 6;
	
		/**
		 * Initializer names are fully qualified.
		 * e.g. <code>java.util.Vector.{ ... }</code>
		 */
		public final static int I_FULLY_QUALIFIED= 1 << 7;
	
		/**
		 * Type names are post qualified.
		 * e.g. <code>{ ... } - java.util.Map</code>
		 */
		public final static int I_POST_QUALIFIED= 1 << 8;		
	
		/**
		 * Field names contain the declared type (appended)
		 * e.g. <code>int fHello</code>
		 */
		public final static int F_APP_TYPE_SIGNATURE= 1 << 9;
	
		/**
		 * Field names contain the declared type (prepended)
		 * e.g. <code>fHello : int</code>
		 */
		public final static int F_PRE_TYPE_SIGNATURE= 1 << 10;	

		/**
		 * Fields names are fully qualified.
		 * e.g. <code>java.lang.System.out</code>
		 */
		public final static int F_FULLY_QUALIFIED= 1 << 11;
	
		/**
		 * Fields names are post qualified.
		 * e.g. <code>out - java.lang.System</code>
		 */
		public final static int F_POST_QUALIFIED= 1 << 12;	
	
		/**
		 * Type names are fully qualified.
		 * e.g. <code>java.util.Map.MapEntry</code>
		 */
		public final static int T_FULLY_QUALIFIED= 1 << 13;
	
		/**
		 * Type names are type container qualified.
		 * e.g. <code>Map.MapEntry</code>
		 */
		public final static int T_CONTAINER_QUALIFIED= 1 << 14;
	
		/**
		 * Type names are post qualified.
		 * e.g. <code>MapEntry - java.util.Map</code>
		 */
		public final static int T_POST_QUALIFIED= 1 << 15;
	
		/**
		 * Declarations (import container / declarartion, package declarartion) are qualified.
		 * e.g. <code>java.util.Vector.class/import container</code>
		 */	
		public final static int D_QUALIFIED= 1 << 16;
	
		/**
		 * Declarations (import container / declarartion, package declarartion) are post qualified.
		 * e.g. <code>import container - java.util.Vector.class</code>
		 */	
		public final static int D_POST_QUALIFIED= 1 << 17;	

		/**
		 * Class file names are fully qualified.
		 * e.g. <code>java.util.Vector.class</code>
		 */	
		public final static int CF_QUALIFIED= 1 << 18;
	
		/**
		 * Class file names are post qualified.
		 * e.g. <code>Vector.class - java.util</code>
		 */	
		public final static int CF_POST_QUALIFIED= 1 << 19;
	
		/**
		 * Compilation unit names are fully qualified.
		 * e.g. <code>java.util.Vector.java</code>
		 */	
		public final static int CU_QUALIFIED= 1 << 20;
	
		/**
		 * Compilation unit names are post  qualified.
		 * e.g. <code>Vector.java - java.util</code>
		 */	
		public final static int CU_POST_QUALIFIED= 1 << 21;

		/**
		 * Package names are qualified.
		 * e.g. <code>MyProject/src/java.util</code>
		 */	
		public final static int P_QUALIFIED= 1 << 22;
	
		/**
		 * Package names are post qualified.
		 * e.g. <code>java.util - MyProject/src</code>
		 */	
		public final static int P_POST_QUALIFIED= 1 << 23;

		/**
		 * Package Fragment Roots contain variable name if from a variable.
		 * e.g. <code>JRE_LIB - c:\java\lib\rt.jar</code>
		 */
		public final static int ROOT_VARIABLE= 1 << 24;
	
		/**
		 * Package Fragment Roots contain the project name if not an archive (prepended).
		 * e.g. <code>MyProject/src</code>
		 */
		public final static int ROOT_QUALIFIED= 1 << 25;
	
		/**
		 * Package Fragment Roots contain the project name if not an archive (appended).
		 * e.g. <code>src - MyProject</code>
		 */
		public final static int ROOT_POST_QUALIFIED= 1 << 26;	
	
		/**
		 * Add root path to all elements except Package Fragment Roots and Java projects.
		 * e.g. <code>java.lang.Vector - c:\java\lib\rt.jar</code>
		 * Option only applies to getElementLabel
		 */
		public final static int APPEND_ROOT_PATH= 1 << 27;

		/**
		 * Add root path to all elements except Package Fragment Roots and Java projects.
		 * e.g. <code>java.lang.Vector - c:\java\lib\rt.jar</code>
		 * Option only applies to getElementLabel
		 */
		public final static int PREPEND_ROOT_PATH= 1 << 28;

		/**
		 * Package names are compressed.
		 * e.g. <code>o*.e*.search</code>
		 */	
		public final static int P_COMPRESSED= 1 << 29;
	
		/**
		 * Post qualify referenced package fragement roots. For example
		 * <code>jdt.jar - org.eclipse.jdt.ui</code> if the jar is referenced
		 * from another project.
		 */
		public final static int REFERENCED_ROOT_POST_QUALIFIED= 1 << 30; 
	
		/**
		 * Qualify all elements
		 */
		public final static int ALL_FULLY_QUALIFIED= F_FULLY_QUALIFIED | M_FULLY_QUALIFIED | I_FULLY_QUALIFIED | T_FULLY_QUALIFIED | D_QUALIFIED | CF_QUALIFIED | CU_QUALIFIED | P_QUALIFIED | ROOT_QUALIFIED;

		/**
		 * Post qualify all elements
		 */
		public final static int ALL_POST_QUALIFIED= F_POST_QUALIFIED | M_POST_QUALIFIED | I_POST_QUALIFIED | T_POST_QUALIFIED | D_POST_QUALIFIED | CF_POST_QUALIFIED | CU_POST_QUALIFIED | P_POST_QUALIFIED | ROOT_POST_QUALIFIED;

		/**
		 *  Default options (M_PARAMETER_TYPES enabled)
		 */
		public final static int ALL_DEFAULT= M_PARAMETER_TYPES;

		/**
		 *  Default qualify options (All except Root and Package)
		 */
		public final static int DEFAULT_QUALIFIED= F_FULLY_QUALIFIED | M_FULLY_QUALIFIED | I_FULLY_QUALIFIED | T_FULLY_QUALIFIED | D_QUALIFIED | CF_QUALIFIED | CU_QUALIFIED;

		/**
		 *  Default post qualify options (All except Root and Package)
		 */
		public final static int DEFAULT_POST_QUALIFIED= F_POST_QUALIFIED | M_POST_QUALIFIED | I_POST_QUALIFIED | T_POST_QUALIFIED | D_POST_QUALIFIED | CF_POST_QUALIFIED | CU_POST_QUALIFIED;


		public final static String CONCAT_STRING= NewWizardMessages.EGLElementLabels_concat_string; // " - "; //$NON-NLS-1$
		public final static String COMMA_STRING= NewWizardMessages.EGLElementLabels_comma_string; // ", "; //$NON-NLS-1$
		public final static String DECL_STRING= NewWizardMessages.EGLElementLabels_declseparator_string; // "  "; // use for return type //$NON-NLS-1$
	/*
	 * Package name compression
	 */
	private static String fgPkgNamePattern= ""; //$NON-NLS-1$
	private static String fgPkgNamePrefix;
	private static String fgPkgNamePostfix;
	private static int fgPkgNameChars;
	private static int fgPkgNameLength= -1;

	private EGLElementLabels() {
	}
	
	public static String getTextLabel(Object obj, int flags) {
		if (obj instanceof IEGLElement) {
			return getElementLabel((IEGLElement) obj, flags);
		} else if (obj instanceof IAdaptable) {
			IWorkbenchAdapter wbadapter= (IWorkbenchAdapter) ((IAdaptable)obj).getAdapter(IWorkbenchAdapter.class);
			if (wbadapter != null) {
				return wbadapter.getLabel(obj);
			}
		}
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * Returns the label for a Java element. Flags as defined above.
	 */
	public static String getElementLabel(IEGLElement element, int flags) {
		StringBuffer buf= new StringBuffer(60);
		getElementLabel(element, flags, buf);
		return buf.toString();
	}
	
	/**
	 * Returns the label for a Java element. Flags as defined above.
	 */
	public static void getElementLabel(IEGLElement element, int flags, StringBuffer buf) {
		int type= element.getElementType();
		IPackageFragmentRoot root= null;
	
		if (type != IEGLElement.EGL_MODEL && type != IEGLElement.EGL_PROJECT && type != IEGLElement.PACKAGE_FRAGMENT_ROOT)
			root= EGLModelUtil.getPackageFragmentRoot(element);
		if (root != null && getFlag(flags, PREPEND_ROOT_PATH)) {
			getPackageFragmentRootLabel(root, ROOT_QUALIFIED, buf);
			buf.append(CONCAT_STRING);
		}		
	
		switch (type) {
//			case IEGLElement.FUNCTION:
//				getFunctionLabel((IFucntion) element, flags, buf);
//				break;
//			case IEGLElement.FIELD: 
//				getFieldLabel((IField) element, flags, buf);
//				break;
//			case IEGLElement.INITIALIZER:
//				getInitializerLabel((IInitializer) element, flags, buf);
//				break;				
//			case IEGLElement.TYPE: 
//				getTypeLabel((IType) element, flags, buf);
//				break;
			case IEGLElement.EGL_FILE: 
				getEGLFileLabel((IEGLFile) element, flags, buf);
				break;					
//			case IEGLElement.PART: 
//				getCompilationUnitLabel((ICompilationUnit) element, flags, buf);
//				break;	
			case IEGLElement.PACKAGE_FRAGMENT: 
				getPackageFragmentLabel((IPackageFragment) element, flags, buf);
				break;
			case IEGLElement.PACKAGE_FRAGMENT_ROOT: 
				getPackageFragmentRootLabel((IPackageFragmentRoot) element, flags, buf);
				break;
			case IEGLElement.IMPORT_CONTAINER:
			case IEGLElement.IMPORT_DECLARATION:
//			case IEGLElement.PACKAGE_DECLARATION:
//				getDeclararionLabel(element, flags, buf);
//				break;
			case IEGLElement.EGL_PROJECT:
			case IEGLElement.EGL_MODEL:
				buf.append(element.getElementName());
				break;
			default:
				buf.append(element.getElementName());
		}
	
//		if (root != null && getFlag(flags, APPEND_ROOT_PATH)) {
//			buf.append(CONCAT_STRING);
//			getPackageFragmentRootLabel(root, ROOT_QUALIFIED, buf);
//		}
	}
	
	/**
	 * Appends the label for a class file to a StringBuffer. Considers the CF_* flags.
	 */	
	public static void getEGLFileLabel(IEGLFile eglFile, int flags, StringBuffer buf) {
		if (getFlag(flags, CF_QUALIFIED)) {
			IPackageFragment pack= (IPackageFragment) eglFile.getParent();
			if (!pack.isDefaultPackage()) {
				buf.append(pack.getElementName());
				buf.append('.');
			}
		}
		buf.append(eglFile.getElementName());
	
		if (getFlag(flags, CF_POST_QUALIFIED)) {
			buf.append(CONCAT_STRING);
			getPackageFragmentLabel((IPackageFragment) eglFile.getParent(), 0, buf);
		}
	}
	
	/**
	 * Appends the label for a package fragment to a StringBuffer. Considers the P_* flags.
	 */	
	public static void getPackageFragmentLabel(IPackageFragment pack, int flags, StringBuffer buf) {
		if (getFlag(flags, P_QUALIFIED)) {
			getPackageFragmentRootLabel((IPackageFragmentRoot) pack.getParent(), ROOT_QUALIFIED, buf);
			buf.append('/');
		}
		refreshPackageNamePattern();
		if (pack.isDefaultPackage()) {
			buf.append(NewWizardMessages.NewElementWizardDefaultpackageLabel);
		} else if (getFlag(flags, P_COMPRESSED) && fgPkgNameLength >= 0) {
				String name= pack.getElementName();
				int start= 0;
				int dot= name.indexOf('.', start);
				while (dot > 0) {
					if (dot - start > fgPkgNameLength-1) {
						buf.append(fgPkgNamePrefix);
						if (fgPkgNameChars > 0)
							buf.append(name.substring(start, Math.min(start+ fgPkgNameChars, dot)));
						buf.append(fgPkgNamePostfix);
					} else
						buf.append(name.substring(start, dot + 1));
					start= dot + 1;
					dot= name.indexOf('.', start);
				}
				buf.append(name.substring(start));
		} else {
			buf.append(pack.getElementName());
		}
		if (getFlag(flags, P_POST_QUALIFIED)) {
			buf.append(CONCAT_STRING);
			getPackageFragmentRootLabel((IPackageFragmentRoot) pack.getParent(), ROOT_QUALIFIED, buf);
		}
	}
	
	/**
	 * Appends the label for a package fragment root to a StringBuffer. Considers the ROOT_* flags.
	 */	
	public static void getPackageFragmentRootLabel(IPackageFragmentRoot root, int flags, StringBuffer buf) {
//		if (root.isArchive())
//			getArchiveLabel(root, flags, buf);
//		else
			getFolderLabel(root, flags, buf);
	}
	
	private static void refreshPackageNamePattern() {
		String pattern= getPkgNamePatternForPackagesView();
		if (pattern.equals(fgPkgNamePattern))
			return;
		else if (pattern.equals("")) { //$NON-NLS-1$
			fgPkgNamePattern= ""; //$NON-NLS-1$
			fgPkgNameLength= -1;
			return;
		}
		fgPkgNamePattern= pattern;
		int i= 0;
		fgPkgNameChars= 0;
		fgPkgNamePrefix= ""; //$NON-NLS-1$
		fgPkgNamePostfix= ""; //$NON-NLS-1$
		while (i < pattern.length()) {
			char ch= pattern.charAt(i);
			if (Character.isDigit(ch)) {
				fgPkgNameChars= ch-48;
				if (i > 0)
					fgPkgNamePrefix= pattern.substring(0, i);
				if (i >= 0)
					fgPkgNamePostfix= pattern.substring(i+1);
				fgPkgNameLength= fgPkgNamePrefix.length() + fgPkgNameChars + fgPkgNamePostfix.length();					
				return;
			}
			i++;
		}
		fgPkgNamePrefix= pattern;
		fgPkgNameLength= pattern.length();
	}
	
	private static void getFolderLabel(IPackageFragmentRoot root, int flags, StringBuffer buf) {
		IResource resource= root.getResource();
		boolean rootQualified= getFlag(flags, ROOT_QUALIFIED);
		boolean referencedQualified= getFlag(flags, REFERENCED_ROOT_POST_QUALIFIED) && EGLModelUtil.isReferenced(root) && resource != null;
		if (rootQualified) {
			buf.append(root.getPath().makeRelative().toString());
		} else {
			if (resource != null)
				buf.append(resource.getProjectRelativePath().toString());
			else
				if(root instanceof EglarPackageFragmentRootContainer) {
					buf.append(((EglarPackageFragmentRootContainer)root).getLabel());				
				} else if(root instanceof EglarPackageFragmentRoot) {
					buf.append(root.getElementName());
					buf.append(CONCAT_STRING);
					buf.append(root.getPath().removeLastSegments(1).toOSString());
				} else {
					buf.append(root.getElementName());	
				}
			if (referencedQualified) {
				buf.append(CONCAT_STRING);
				buf.append(resource.getProject().getName());
			}
			else if (getFlag(flags, ROOT_POST_QUALIFIED)) {
				buf.append(CONCAT_STRING);
				buf.append(root.getParent().getElementName());
			}
		}
	}
	
	private static boolean getFlag(int flags, int flag) {
		return (flags & flag) != 0;
	}
	
	private static String getPkgNamePatternForPackagesView() {
		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
		if (!store.getBoolean(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES))
			return ""; //$NON-NLS-1$
		return store.getString(PreferenceConstants.APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW);
	}	
}
