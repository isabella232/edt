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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.Signature;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.compiler.tools.IRUtils;

import com.ibm.icu.util.StringTokenizer;

/**
 * Provides convenient utility methods to other types in this package.
 */
public class Util {

	private static final String ARGUMENTS_DELIMITER = "#"; //$NON-NLS-1$
	private static final String EMPTY_ARGUMENT = "   "; //$NON-NLS-1$

	public interface Comparable {
		/**
		 * Returns 0 if this and c are equal, >0 if this is greater than c,
		 * or <0 if this is less than c.
		 */
		int compareTo(Comparable c);
	}

	public interface Comparer {
		/**
		 * Returns 0 if a and b are equal, >0 if a is greater than b,
		 * or <0 if a is less than b.
		 */
		int compare(Object a, Object b);
	}
	
	public interface Displayable {
		String displayString(Object o);
	}
	
	public static final String[] fgEmptyStringArray = new String[0];


	/* Bundle containing messages */
	protected static ResourceBundle bundle;
	private final static String bundleName = "org.eclipse.edt.ide.core.internal.model.EGLModelResources"; //$NON-NLS-1$

	public final static char[] SUFFIX_egl = ".egl".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGL = ".EGL".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_buildstate = ".bs".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_BUILDSTATE = ".BS".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_eglbld = ".eglbld".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGLBLD = ".EGLBLD".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_jar = ".jar".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_JAR = ".JAR".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_zip = ".zip".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_ZIP = ".ZIP".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_eglar = ".eglar".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGLAR = ".EGLAR".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_mofar = ".mofar".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_MOFAR = ".MOFAR".toCharArray(); //$NON-NLS-1$
	static {
		relocalize();
	}
	
	/**
	 * Lookup the message with the given ID in this catalog and bind its
	 * substitution locations with the given strings.
	 */
//	public static String bind(String id, String binding1, String binding2) {
//		return bind(id, new String[] {binding1, binding2});
//	}

	/**
	 * Combines two hash codes to make a new one.
	 */
	public static int combineHashCodes(int hashCode1, int hashCode2) {
		return hashCode1 * 17 + hashCode2;
	}
	
	/**
	 * Compares two byte arrays.  
	 * Returns <0 if a byte in a is less than the corresponding byte in b, or if a is shorter, or if a is null.
	 * Returns >0 if a byte in a is greater than the corresponding byte in b, or if a is longer, or if b is null.
	 * Returns 0 if they are equal or both null.
	 */
	public static int compare(byte[] a, byte[] b) {
		if (a == b)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		int len = Math.min(a.length, b.length);
		for (int i = 0; i < len; ++i) {
			int diff = a[i] - b[i];
			if (diff != 0)
				return diff;
		}
		if (a.length > len)
			return 1;
		if (b.length > len)
			return -1;
		return 0;
	}

	/**
	 * Compares two char arrays lexicographically. 
	 * The comparison is based on the Unicode value of each character in
	 * the char arrays. 
	 * @return  the value <code>0</code> if a is equal to
	 *          b; a value less than <code>0</code> if a
	 *          is lexicographically less than b; and a
	 *          value greater than <code>0</code> if a is
	 *          lexicographically greater than b.
	 */
	public static int compare(char[] v1, char[] v2) {
		int len1 = v1.length;
		int len2 = v2.length;
		int n = Math.min(len1, len2);
		int i = 0;
		while (n-- != 0) {
			if (v1[i] != v2[i]) {
				return v1[i] - v2[i];
			}
			++i;
		}
		return len1 - len2;
	}

	/**
	 * Concatenate two strings with a char in between.
	 * @see #concat(String, String)
	 */
	public static String concat(String s1, char c, String s2) {
		if (s1 == null) s1 = "null"; //$NON-NLS-1$
		if (s2 == null) s2 = "null"; //$NON-NLS-1$
		int l1 = s1.length();
		int l2 = s2.length();
		char[] buf = new char[l1 + 1 + l2];
		s1.getChars(0, l1, buf, 0);
		buf[l1] = c;
		s2.getChars(0, l2, buf, l1 + 1);
		return new String(buf);
	}
	/**
	 * Returns the concatenation of the given array parts using the given separator between each part.
	 * <br>
	 * <br>
	 * For example:<br>
	 * <ol>
	 * <li><pre>
	 *    array = {"a", "b"}
	 *    separator = '.'
	 *    => result = "a.b"
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    array = {}
	 *    separator = '.'
	 *    => result = ""
	 * </pre></li>
	 * </ol>
	 *
	 * @param array the given array
	 * @param separator the given separator
	 * @return the concatenation of the given array parts using the given separator between each part
	 */
	public static final String concatWith(String[] array, char separator) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = array.length; i < length; i++) {
			buffer.append(array[i]);
			if (i < length - 1)
				buffer.append(separator);
		}
		return buffer.toString();
	}
	/**
	 * Concatenate two strings.
	 * Much faster than using +, which:
	 * 		- creates a StringBuffer,
	 * 		- which is synchronized,
	 * 		- of default size, so the resulting char array is
	 *        often larger than needed.
	 * This implementation creates an extra char array, since the
	 * String constructor copies its argument, but there's no way around this.
	 */
	public static String concat(String s1, String s2) {
		if (s1 == null) s1 = "null"; //$NON-NLS-1$
		if (s2 == null) s2 = "null"; //$NON-NLS-1$
		int l1 = s1.length();
		int l2 = s2.length();
		char[] buf = new char[l1 + l2];
		s1.getChars(0, l1, buf, 0);
		s2.getChars(0, l2, buf, l1);
		return new String(buf);
	}

	/**
	 * Concatenate three strings.
	 * @see #concat(String, String)
	 */
	public static String concat(String s1, String s2, String s3) {
		if (s1 == null) s1 = "null"; //$NON-NLS-1$
		if (s2 == null) s2 = "null"; //$NON-NLS-1$
		if (s3 == null) s3 = "null"; //$NON-NLS-1$
		int l1 = s1.length();
		int l2 = s2.length();
		int l3 = s3.length();
		char[] buf = new char[l1 + l2 + l3];
		s1.getChars(0, l1, buf, 0);
		s2.getChars(0, l2, buf, l1);
		s3.getChars(0, l3, buf, l1 + l2);
		return new String(buf);
	}
	
	/**
	 * Converts a type signature from the IBinaryType representation to the DC representation.
	 */
	public static String convertTypeSignature(char[] sig) {
		return new String(sig).replace('/', '.');
	}

	/**
	 * Returns true iff str.toLowerCase().endsWith(end.toLowerCase())
	 * implementation is not creating extra strings.
	 */
	public final static boolean endsWithIgnoreCase(String str, String end) {
		
		int strLength = str == null ? 0 : str.length();
		int endLength = end == null ? 0 : end.length();
		
		// return false if the string is smaller than the end.
		if(endLength > strLength)
			return false;
			
		// return false if any character of the end are
		// not the same in lower case.
		for(int i = 1 ; i <= endLength; i++){
			if(Character.toLowerCase(end.charAt(endLength - i)) != Character.toLowerCase(str.charAt(strLength - i)))
				return false;
		}
		
		return true;
	}

	/**
	 * Compares two arrays using equals() on the elements.
	 * Either or both arrays may be null.
	 * Returns true if both are null.
	 * Returns false if only one is null.
	 * If both are arrays, returns true iff they have the same length and
	 * all elements are equal.
	 */
	public static boolean equalArraysOrNull(int[] a, int[] b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		int len = a.length;
		if (len != b.length)
			return false;
		for (int i = 0; i < len; ++i) {
			if (a[i] != b[i])
				return false;
		}
		return true;
	}

	/**
	 * Compares two arrays using equals() on the elements.
	 * Either or both arrays may be null.
	 * Returns true if both are null.
	 * Returns false if only one is null.
	 * If both are arrays, returns true iff they have the same length and
	 * all elements compare true with equals.
	 */
	public static boolean equalArraysOrNull(Object[] a, Object[] b) {
		if (a == b)	return true;
		if (a == null || b == null) return false;

		int len = a.length;
		if (len != b.length) return false;
		for (int i = 0; i < len; ++i) {
			if (a[i] == null) {
				if (b[i] != null) return false;
			} else {
				if (!a[i].equals(b[i])) return false;
			}
		}
		return true;
	}
	
	/**
	 * Compares two String arrays using equals() on the elements.
	 * The arrays are first sorted.
	 * Either or both arrays may be null.
	 * Returns true if both are null.
	 * Returns false if only one is null.
	 * If both are arrays, returns true iff they have the same length and
	 * iff, after sorting both arrays, all elements compare true with equals.
	 * The original arrays are left untouched.
	 */
	public static boolean equalArraysOrNullSortFirst(String[] a, String[] b) {
		if (a == b)	return true;
		if (a == null || b == null) return false;
		int len = a.length;
		if (len != b.length) return false;
		if (len >= 2) {  // only need to sort if more than two items
			a = sortCopy(a);
			b = sortCopy(b);
		}
		for (int i = 0; i < len; ++i) {
			if (!a[i].equals(b[i])) return false;
		}
		return true;
	}
	
	/**
	 * Compares two arrays using equals() on the elements.
	 * The arrays are first sorted.
	 * Either or both arrays may be null.
	 * Returns true if both are null.
	 * Returns false if only one is null.
	 * If both are arrays, returns true iff they have the same length and
	 * iff, after sorting both arrays, all elements compare true with equals.
	 * The original arrays are left untouched.
	 */
	public static boolean equalArraysOrNullSortFirst(Comparable[] a, Comparable[] b) {
		if (a == b)	return true;
		if (a == null || b == null) return false;
		int len = a.length;
		if (len != b.length) return false;
		if (len >= 2) {  // only need to sort if more than two items
			a = sortCopy(a);
			b = sortCopy(b);
		}
		for (int i = 0; i < len; ++i) {
			if (!a[i].equals(b[i])) return false;
		}
		return true;
	}
	
	/**
	 * Compares two objects using equals().
	 * Either or both array may be null.
	 * Returns true if both are null.
	 * Returns false if only one is null.
	 * Otherwise, return the result of comparing with equals().
	 */
	public static boolean equalOrNull(Object a, Object b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}
	
	/**
	 * Given a qualified name, extract the last component.
	 * If the input is not qualified, the same string is answered.
	 */
	public static String extractLastName(String qualifiedName) {
		int i = qualifiedName.lastIndexOf('.');
		if (i == -1) return qualifiedName;
		return qualifiedName.substring(i+1);
	}
	
	/**
	 * Finds the first line separator used by the given text.
	 *
	 * @return </code>"\n"</code> or </code>"\r"</code> or  </code>"\r\n"</code>,
	 *			or <code>null</code> if none found
	 */
	public static String findLineSeparator(char[] text) {
		// find the first line separator
		int length = text.length;
		if (length > 0) {
			char nextChar = text[0];
			for (int i = 0; i < length; i++) {
				char currentChar = nextChar;
				nextChar = i < length-1 ? text[i+1] : ' ';
				switch (currentChar) {
					case '\n': return "\n"; //$NON-NLS-1$
					case '\r': return nextChar == '\n' ? "\r\n" : "\r"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		// not found
		return null;
	}
	
	/**
	 * Returns the given file's contents as a byte array.
	 */
	public static byte[] getResourceContentsAsByteArray(IFile file) throws EGLModelException {
		InputStream stream= null;
		try {
			stream = new BufferedInputStream(file.getContents(true));
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
		try {
			return org.eclipse.edt.ide.core.internal.model.util.Util.getInputStreamAsByteArray(stream, -1);
		} catch (IOException e) {
			throw new EGLModelException(e, IEGLModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * Returns the given file's contents as a character array.
	 */
	public static char[] getResourceContentsAsCharArray(IFile file) throws EGLModelException {
		try {
			String encoding = file.getCharset();
			if(encoding == null)	//get the default encoding setting
				encoding = EGLCore.create(file.getProject()).getOption(EGLCore.CORE_ENCODING, true);
			
			return getResourceContentsAsCharArray(file, encoding);
		} catch (CoreException e) {
			throw new EGLModelException(e);
		}
		
	}

	public static char[] getResourceContentsAsCharArray(IFile file, String encoding) throws EGLModelException {
		InputStream stream= null;
		try {
			stream = new BufferedInputStream(file.getContents(true));
		} catch (CoreException e) {
			throw new EGLModelException(e, IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
		}
		try {
			return org.eclipse.edt.ide.core.internal.model.util.Util.getInputStreamAsCharArray(stream, -1, encoding);
		} catch (IOException e) {
			throw new EGLModelException(e, IEGLModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * Returns a trimmed version the simples names returned by Signature.
	 */
	public static String[] getTrimmedSimpleNames(String name) {
		String[] result = Signature.getSimpleNames(name);
		if (result == null) return null;
		for (int i = 0, length = result.length; i < length; i++) {
			result[i] = result[i].trim();
		}
		return result;
	}
	/**
	 * Returns true iff str.toLowerCase().endsWith(".eglar" or ".mofar")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isEGLARFileName(String name) {
		if (name == null) {
			return false;
		}
		
		return IRUtils.matchesFileName(name, SUFFIX_eglar, SUFFIX_EGLAR)
				|| IRUtils.matchesFileName(name, SUFFIX_mofar, SUFFIX_MOFAR);
	}
	
	public static boolean isValidMofPackage(String fullPath) {
		if(fullPath != null && fullPath.contains("eglgen")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns true iff str.toLowerCase().endsWith(".jar" or ".zip")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isArchiveFileName(String name) {
		int nameLength = name == null ? 0 : name.length();
		int suffixLength = SUFFIX_JAR.length;
		if (nameLength < suffixLength) return false;

		int i, offset;
		for ( i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name.charAt(offset + i);
			if (c != SUFFIX_jar[i] && c != SUFFIX_JAR[i]) break;
		}
		if (i == suffixLength) return true;		
		for ( i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name.charAt(offset + i);
			if (c != SUFFIX_zip[i] && c != SUFFIX_ZIP[i]) return false;
		}
		return true;
	}
	/**
	 * Returns true iff str.toLowerCase().endsWith(".eglbld")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isEGLBLDFileName(String name) {
		int nameLength = name == null ? 0 : name.length();
		int suffixLength = SUFFIX_EGLBLD.length;
		if (nameLength < suffixLength) return false;

		for (int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name.charAt(offset + i);
			if (c != SUFFIX_eglbld[i] && c != SUFFIX_EGLBLD[i]) return false;
		}
		return true;		
	}

	/**
	 * Returns true iff str.toLowerCase().endsWith(".egl")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isEGLFileName(String name) {
		int nameLength = name == null ? 0 : name.length();
		int suffixLength = SUFFIX_EGL.length;
		if (nameLength < suffixLength) return false;

		for (int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name.charAt(offset + i);
			if (c != SUFFIX_egl[i] && c != SUFFIX_EGL[i]) return false;
		}
		return true;		
	}

	/**
	 * Returns true iff str.toLowerCase().endsWith(".bs")
	 * implementation is not creating extra strings.
	 */
	public final static boolean isBuildStateFileName(String name) {
		int nameLength = name == null ? 0 : name.length();
		int suffixLength = SUFFIX_BUILDSTATE.length;
		if (nameLength < suffixLength) return false;

		for (int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name.charAt(offset + i);
			if (c != SUFFIX_buildstate[i] && c != SUFFIX_BUILDSTATE[i]) return false;
		}
		return true;		
	}
	
	/*
	 * Returns whether the given java element is exluded from its root's classpath.
	 */
	public static final boolean isExcluded(IEGLElement element) {
		int elementType = element.getElementType();
		switch (elementType) {
			case IEGLElement.PACKAGE_FRAGMENT:
				PackageFragmentRoot root = (PackageFragmentRoot)element.getAncestor(IEGLElement.PACKAGE_FRAGMENT_ROOT);
				IResource resource = element.getResource();
				return resource != null && Util.isExcluded(resource, root.fullExclusionPatternChars());
			case IEGLElement.EGL_FILE:
				root = (PackageFragmentRoot)element.getAncestor(IEGLElement.PACKAGE_FRAGMENT_ROOT);
				resource = element.getResource();
				if (resource != null && Util.isExcluded(resource, root.fullExclusionPatternChars()))
					return true;
				return isExcluded(element.getParent());
			default:
				IEGLElement cu = element.getAncestor(IEGLElement.EGL_FILE);
				return cu != null && isExcluded(cu);
		}
	}
	/*
	 * Returns whether the given resource path matches one of the exclusion
	 * patterns.
	 * 
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IPath resourcePath, char[][] exclusionPatterns) {
		if (exclusionPatterns == null) return false;
		char[] path = resourcePath.toString().toCharArray();
		for (int i = 0, length = exclusionPatterns.length; i < length; i++)
			if (CharOperation.pathMatch(exclusionPatterns[i], path, true, '/'))
				return true;
		return false;
	}	
	
	/*
	 * Returns whether the given resource matches one of the exclusion patterns.
	 * 
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IResource resource, char[][] exclusionPatterns) {
		IPath path = resource.getFullPath();
		// ensure that folders are only excluded if all of their children are excluded
		if (resource.getType() == IResource.FOLDER)
			path = path.append("*"); //$NON-NLS-1$
		return isExcluded(path, exclusionPatterns);
	}

	
	/**
	 * Validate the given compilation unit name.
	 * A compilation unit name must obey the following rules:
	 * <ul>
	 * <li> it must not be null
	 * <li> it must include the <code>".java"</code> suffix
	 * <li> its prefix must be a valid identifier
	 * </ul>
	 * </p>
	 * @param name the name of a compilation unit
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a compilation unit name, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static boolean isValidEGLFileName(String name) {
		return EGLConventions.validateEGLFileName(name).getSeverity() != IStatus.ERROR;
	}

	
	/**
	 * Validate the given .class file name.
	 * A .class file name must obey the following rules:
	 * <ul>
	 * <li> it must not be null
	 * <li> it must include the <code>".class"</code> suffix
	 * <li> its prefix must be a valid identifier
	 * </ul>
	 * </p>
	 * @param name the name of a .class file
	 * @return a status object with code <code>IStatus.OK</code> if
	 *		the given name is valid as a .class file name, otherwise a status 
	 *		object indicating what is wrong with the name
	 */
	public static boolean isValidClassFileName(String name) {
		// TODO handle class files generated from egl resources
		return false;
		//return EGLConventions.validateClassFileName(name).getSeverity() != IStatus.ERROR;
	}

	/**
	 * Returns true if the given folder name is valid for a package,
	 * false if it is not.
	 */
	public static boolean isValidFolderNameForPackage(String folderName) {
		return EGLConventions.validateIdentifier(folderName).getSeverity() != IStatus.ERROR;
	}	

	/*
	 * Add a log entry
	 */
	public static void log(Throwable e, String message) {
		Throwable nestedException;
		if (e instanceof EGLModelException 
				&& (nestedException = ((EGLModelException)e).getException()) != null) {
			e = nestedException;
		}
		IStatus status= new Status(
			IStatus.ERROR, 
			EDTCoreIDEPlugin.getPlugin().getDescriptor().getUniqueIdentifier(), 
			IStatus.ERROR, 
			message, 
			e); 
		EDTCoreIDEPlugin.getPlugin().getLog().log(status);
	}	
	
	/**
	 * Sort the objects in the given collection using the given sort order.
	 */
	private static void quickSort(Object[] sortedCollection, int left, int right, int[] sortOrder) {
		int original_left = left;
		int original_right = right;
		int mid = sortOrder[ (left + right) / 2];
		do {
			while (sortOrder[left] < mid) {
				left++;
			}
			while (mid < sortOrder[right]) {
				right--;
			}
			if (left <= right) {
				Object tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				int tmp2 = sortOrder[left];
				sortOrder[left] = sortOrder[right];
				sortOrder[right] = tmp2;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right, sortOrder);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right, sortOrder);
		}
	}
	/**
	 * Sort the objects in the given collection using the given comparer.
	 */
	private static void quickSort(Object[] sortedCollection, int left, int right, Comparer comparer) {
		int original_left = left;
		int original_right = right;
		Object mid = sortedCollection[ (left + right) / 2];
		do {
			while (comparer.compare(sortedCollection[left], mid) < 0) {
				left++;
			}
			while (comparer.compare(mid, sortedCollection[right]) < 0) {
				right--;
			}
			if (left <= right) {
				Object tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right, comparer);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right, comparer);
		}
	}

	/**
	 * Sort the strings in the given collection.
	 */
	private static void quickSort(String[] sortedCollection, int left, int right) {
		int original_left = left;
		int original_right = right;
		String mid = sortedCollection[ (left + right) / 2];
		do {
			while (sortedCollection[left].compareTo(mid) < 0) {
				left++;
			}
			while (mid.compareTo(sortedCollection[right]) < 0) {
				right--;
			}
			if (left <= right) {
				String tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right);
		}
	}

	/**
	 * Converts the given relative path into a package name.
	 * Returns null if the path is not a valid package name.
	 */
	public static String packageName(IPath pkgPath) {
		StringBuffer pkgName = new StringBuffer(IPackageFragment.DEFAULT_PACKAGE_NAME);
		for (int j = 0, max = pkgPath.segmentCount(); j < max; j++) {
			String segment = pkgPath.segment(j);
			if (!isValidFolderNameForPackage(segment)) {
				return null;
			}
			pkgName.append(segment);
			if (j < pkgPath.segmentCount() - 1) {
				pkgName.append("." ); //$NON-NLS-1$
			}
		}
		return pkgName.toString();
	}

	/**
	 * Sort the comparable objects in the given collection.
	 */
	private static void quickSort(Comparable[] sortedCollection, int left, int right) {
		int original_left = left;
		int original_right = right;
		Comparable mid = sortedCollection[ (left + right) / 2];
		do {
			while (sortedCollection[left].compareTo(mid) < 0) {
				left++;
			}
			while (mid.compareTo(sortedCollection[right]) < 0) {
				right--;
			}
			if (left <= right) {
				Comparable tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSort(sortedCollection, original_left, right);
		}
		if (left < original_right) {
			quickSort(sortedCollection, left, original_right);
		}
	}

	/**
	 * Sort the strings in the given collection in reverse alphabetical order.
	 */
	private static void quickSortReverse(String[] sortedCollection, int left, int right) {
		int original_left = left;
		int original_right = right;
		String mid = sortedCollection[ (left + right) / 2];
		do {
			while (sortedCollection[left].compareTo(mid) > 0) {
				left++;
			}
			while (mid.compareTo(sortedCollection[right]) > 0) {
				right--;
			}
			if (left <= right) {
				String tmp = sortedCollection[left];
				sortedCollection[left] = sortedCollection[right];
				sortedCollection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right) {
			quickSortReverse(sortedCollection, original_left, right);
		}
		if (left < original_right) {
			quickSortReverse(sortedCollection, left, original_right);
		}
	}

	/**
	 * Sorts an array of objects in place, using the sort order given for each item.
	 */
	public static void sort(Object[] objects, int[] sortOrder) {
		if (objects.length > 1)
			quickSort(objects, 0, objects.length - 1, sortOrder);
	}

	/**
	 * Sorts an array of objects in place.
	 * The given comparer compares pairs of items.
	 */
	public static void sort(Object[] objects, Comparer comparer) {
		if (objects.length > 1)
			quickSort(objects, 0, objects.length - 1, comparer);
	}

	/**
	 * Sorts an array of strings in place using quicksort.
	 */
	public static void sort(String[] strings) {
		if (strings.length > 1)
			quickSort(strings, 0, strings.length - 1);
	}

	/**
	 * Sorts an array of Comparable objects in place.
	 */
	public static void sort(Comparable[] objects) {
		if (objects.length > 1)
			quickSort(objects, 0, objects.length - 1);
	}

	/**
	 * Sorts an array of Strings, returning a new array
	 * with the sorted items.  The original array is left untouched.
	 */
	public static Object[] sortCopy(Object[] objects, Comparer comparer) {
		int len = objects.length;
		Object[] copy = new Object[len];
		System.arraycopy(objects, 0, copy, 0, len);
		sort(copy, comparer);
		return copy;
	}

	/**
	 * Sorts an array of Strings, returning a new array
	 * with the sorted items.  The original array is left untouched.
	 */
	public static String[] sortCopy(String[] objects) {
		int len = objects.length;
		String[] copy = new String[len];
		System.arraycopy(objects, 0, copy, 0, len);
		sort(copy);
		return copy;
	}

	/**
	 * Sorts an array of Comparable objects, returning a new array
	 * with the sorted items.  The original array is left untouched.
	 */
	public static Comparable[] sortCopy(Comparable[] objects) {
		int len = objects.length;
		Comparable[] copy = new Comparable[len];
		System.arraycopy(objects, 0, copy, 0, len);
		sort(copy);
		return copy;
	}

	/**
	 * Sorts an array of strings in place using quicksort
	 * in reverse alphabetical order.
	 */
	public static void sortReverseOrder(String[] strings) {
		if (strings.length > 1)
			quickSortReverse(strings, 0, strings.length - 1);
	}

	/**
	 * Converts a String[] to char[][].
	 */
	public static char[][] toCharArrays(String[] a) {
		int len = a.length;
		char[][] result = new char[len][];
		for (int i = 0; i < len; ++i) {
			result[i] = toChars(a[i]);
		}
		return result;
	}

	/**
	 * Converts a String to char[].
	 */
	public static char[] toChars(String s) {
		int len = s.length();
		char[] chars = new char[len];
		s.getChars(0, len, chars, 0);
		return chars;
	}

	/**
	 * Converts a String to char[][], where segments are separate by '.'.
	 */
	public static char[][] toCompoundChars(String s) {
		int len = s.length();
		if (len == 0) {
			return CharOperation.NO_CHAR_CHAR;
		}
		int segCount = 1;
		for (int off = s.indexOf('.'); off != -1; off = s.indexOf('.', off + 1)) {
			++segCount;
		}
		char[][] segs = new char[segCount][];
		int start = 0;
		for (int i = 0; i < segCount; ++i) {
			int dot = s.indexOf('.', start);
			int end = (dot == -1 ? s.length() : dot);
			segs[i] = new char[end - start];
			s.getChars(start, end, segs[i], 0);
			start = end + 1;
		}
		return segs;
	}

	/**
	 * Converts a char[][] to String, where segments are separated by '.'.
	 */
	public static String toString(char[][] c) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, max = c.length; i < max; ++i) {
			if (i != 0) sb.append('.');
			sb.append(c[i]);
		}
		return sb.toString();
	}

	/**
	 * Converts a char[][] and a char[] to String, where segments are separated by '.'.
	 */
	public static String toString(char[][] c, char[] d) {
		if (c == null) return new String(d);
		StringBuffer sb = new StringBuffer();
		for (int i = 0, max = c.length; i < max; ++i) {
			sb.append(c[i]);
			sb.append('.');
		}
		sb.append(d);
		return sb.toString();
	}

	/**
	 * Converts a char[] to String.
	 */
	public static String toString(char[] c) {
		return new String(c);
	}

	/**
	 * Converts an array of Objects into String.
	 */
	public static String toString(Object[] objects) {
		return toString(objects, 
			new Displayable(){ 
				public String displayString(Object o) { 
					if (o == null) return "null"; //$NON-NLS-1$
					return o.toString(); 
				}
			});
	}

	/**
	 * Converts an array of Objects into String.
	 */
	public static String toString(Object[] objects, Displayable renderer) {
		if (objects == null) return ""; //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer(10);
		for (int i = 0; i < objects.length; i++){
			if (i > 0) buffer.append(", "); //$NON-NLS-1$
			buffer.append(renderer.displayString(objects[i]));
		}
		return buffer.toString();
	}
	

	/**
	 * Creates a NLS catalog for the given locale.
	 */
	public static void relocalize() {
		try {
			bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
		} catch(MissingResourceException e) {
			System.out.println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + Locale.getDefault()); //$NON-NLS-1$//$NON-NLS-2$
			throw e;
		}
	}
	
	/**
	 * Put all the arguments in one String.
	 */
	public static String getProblemArgumentsForMarker(String[] arguments){
		StringBuffer args = new StringBuffer(10);
		
		args.append(arguments.length);
		args.append(':');
		
			
		for (int j = 0; j < arguments.length; j++) {
			if(j != 0)
				args.append(ARGUMENTS_DELIMITER);
			
			if(arguments[j].length() == 0) {
				args.append(EMPTY_ARGUMENT);
			} else {			
				args.append(arguments[j]);
			}
		}
		
		return args.toString();
	}
	
	/**
	 * Separate all the arguments of a String made by getProblemArgumentsForMarker
	 */
	public static String[] getProblemArgumentsFromMarker(String argumentsString){
		if (argumentsString == null) return null;
		int index = argumentsString.indexOf(':');
		if(index == -1)
			return null;
		
		int length = argumentsString.length();
		int numberOfArg;
		try{
			numberOfArg = Integer.parseInt(argumentsString.substring(0 , index));
		} catch (NumberFormatException e) {
			return null;
		}
		argumentsString = argumentsString.substring(index + 1, length);
		
		String[] args = new String[length];
		int count = 0;
		
		StringTokenizer tokenizer = new StringTokenizer(argumentsString, ARGUMENTS_DELIMITER);
		while(tokenizer.hasMoreTokens()) {
			String argument = tokenizer.nextToken();
			if(argument.equals(EMPTY_ARGUMENT))
				argument = "";  //$NON-NLS-1$
			args[count++] = argument;
		}
		
		if(count != numberOfArg)
			return null;
		
		System.arraycopy(args, 0, args = new String[count], 0, count);
		return args;
	}
	
	/*
	 * Converts the given URI to a local file. Use the existing file if the uri is on the local file system.
	 * Otherwise fetch it.
	 * Returns null if unable to fetch it.
	 */
	public static File toLocalFile(URI uri, IProgressMonitor monitor) throws CoreException {
		IFileStore fileStore = EFS.getStore(uri);
		File localFile = fileStore.toLocalFile(EFS.NONE, monitor);
		if (localFile ==null)
			// non local file system
			localFile= fileStore.toLocalFile(EFS.CACHE, monitor);
		return localFile;
	}

	/**
	 * Returns the contents of the given zip entry as a byte array.
	 * @throws IOException if a problem occured reading the zip entry.
	 */
	public static byte[] getZipEntryByteContent(ZipEntry ze, ZipFile zip)
		throws IOException {

		InputStream stream = null;
		try {
			InputStream inputStream = zip.getInputStream(ze);
			if (inputStream == null) throw new IOException("Invalid zip entry name : " + ze.getName()); //$NON-NLS-1$
			stream = new BufferedInputStream(inputStream);
			return getInputStreamAsByteArray(stream, (int) ze.getSize());
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	/**
	 * Returns the given input stream's contents as a byte array.
	 * If a length is specified (ie. if length != -1), only length bytes
	 * are returned. Otherwise all bytes in the stream are returned.
	 * Note this doesn't close the stream.
	 * @throws IOException if a problem occured reading the stream.
	 */
	public static byte[] getInputStreamAsByteArray(InputStream stream, int length)
			throws IOException {
		final int DEFAULT_READING_SIZE = 8192;
		byte[] contents;
		if (length == -1) {
			contents = new byte[0];
			int contentsLength = 0;
			int amountRead = -1;
			do {
				int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE);  // read at least 8K

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(
						contents,
						0,
						contents = new byte[contentsLength + amountRequested],
						0,
						contentsLength);
				}

				// read as many bytes as possible
				amountRead = stream.read(contents, contentsLength, amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1);

			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(
					contents,
					0,
					contents = new byte[contentsLength],
					0,
					contentsLength);
			}
		} else {
			contents = new byte[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length)) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual read size.
				len += readSize;
				readSize = stream.read(contents, len, length - len);
			}
		}

		return contents;
	}	
	
	/**
	 * Return a new array which is the split of the given string using the given divider. The given end
	 * is exclusive and the given start is inclusive.
	 * <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li><pre>
	 *    divider = 'b'
	 *    string = "abbaba"
	 *    start = 2
	 *    end = 5
	 *    result => { "", "a", "" }
	 * </pre>
	 * </li>
	 * </ol>
	 *
	 * @param divider the given divider
	 * @param string the given string
	 * @param start the given starting index
	 * @param end the given ending index
	 * @return a new array which is the split of the given string using the given divider
	 * @throws ArrayIndexOutOfBoundsException if start is lower than 0 or end is greater than the array length
	 */
	public static final String[] splitOn(
		char divider,
		String string,
		int start,
		int end) {
		int length = string == null ? 0 : string.length();
		if (length == 0 || start > end)
			return new String[0];

		int wordCount = 1;
		for (int i = start; i < end; i++)
			if (string.charAt(i) == divider)
				wordCount++;
		String[] split = new String[wordCount];
		int last = start, currentWord = 0;
		for (int i = start; i < end; i++) {
			if (string.charAt(i) == divider) {
				split[currentWord++] = string.substring(last, i);
				last = i + 1;
			}
		}
		split[currentWord] = string.substring(last, end);
		return split;
	}
	/**
	 * Compares two arrays using equals() on the elements.
	 * Neither can be null. Only the first len elements are compared.
	 * Return false if either array is shorter than len.
	 */
	public static boolean equalArrays(Object[] a, Object[] b, int len) {
		if (a == b)	return true;
		if (a.length < len || b.length < len) return false;
		for (int i = 0; i < len; ++i) {
			if (a[i] == null) {
				if (b[i] != null) return false;
			} else {
				if (!a[i].equals(b[i])) return false;
			}
		}
		return true;
	}	
	
    //Given an IEGLPathEntry for a binary project, return the project name
	public static String getExternalProjectName(IEGLPathEntry entry) { 	
    	//For now, the path will look something like this: "C:\targetPlatform\project.eglar"
    	String fname = entry.getPath().lastSegment();
    	int index = fname.lastIndexOf(".");
    	if (index > -1) {
    		fname = fname.substring(0, index);
    	}
    	return fname;
    }
}
