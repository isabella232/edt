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
package org.eclipse.edt.ide.core.internal.search.matching;


/*
 * A name environment based on the classpath of a EGL project.
 */
public class EGLSearchableNameEnvironment /* implements INameEnvironment */{
// TODO Uncomment and fix the code	
//	EGLPathLocation[] locations;
	
//public EGLSearchNameEnvironment(IEGLProject javaProject) {
//	try {
//		computeEGLPathLocations(javaProject.getProject().getWorkspace().getRoot(), (EGLProject) javaProject);
//	} catch(CoreException e) {
//		this.locations = new EGLPathLocation[0];
//	}
//}
//
//public void cleanup() {
//	for (int i = 0, length = this.locations.length; i < length; i++) {
//		this.locations[i].cleanup();
//	}
//}
//
//private void computeEGLPathLocations(
//	IWorkspaceRoot workspaceRoot,
//	EGLProject javaProject) throws CoreException {
//
//	String encoding = null;
//	IPackageFragmentRoot[] roots = javaProject.getAllPackageFragmentRoots();
//	int length = roots.length;
//	EGLPathLocation[] locations = new EGLPathLocation[length];
//	EGLModelManager manager = EGLModelManager.getEGLModelManager();
//	for (int i = 0; i < length; i++) {
//		IPackageFragmentRoot root = roots[i];
//		IPath path = root.getPath();
//		if (root.isArchive()) {
//			ZipFile zipFile = manager.getZipFile(path);
//			locations[i] = new EGLPathJar(zipFile);
//		} else {
//			Object target = EGLModel.getTarget(workspaceRoot, path, false);
//			if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
//				if (encoding == null) {
//					encoding = javaProject.getOption(EGLCore.CORE_ENCODING, true);
//				}
//				locations[i] = new EGLPathSourceDirectory((IContainer)target, encoding);
//			} else {
//				locations[i] = EGLPathLocation.forBinaryFolder((IContainer) target, false);
//			}
//		}
//	}
//	this.locations = locations;
//}
//
//private NameEnvironmentAnswer findPart(String qualifiedTypeName, char[] typeName) {
//	String 
//		sourceFileName = null, qSourceFileName = null, 
//		qPackageName = null;
//	for (int i = 0, length = this.locations.length; i < length; i++) {
//		EGLPathLocation location = this.locations[i];
//		NameEnvironmentAnswer answer;
//		if (location instanceof EGLPathSourceDirectory) {
//			if (sourceFileName == null) {
//				qSourceFileName = qualifiedTypeName + ".egl"; //$NON-NLS-1$
//				sourceFileName = qSourceFileName;
//				qPackageName =  ""; //$NON-NLS-1$
//				if (qualifiedTypeName.length() > typeName.length) {
//					int typeNameStart = qSourceFileName.length() - typeName.length - 5; // size of ".java"
//					qPackageName =  qSourceFileName.substring(0, typeNameStart - 1);
//					sourceFileName = qSourceFileName.substring(typeNameStart);
//				}
//			}
//			answer = location.findClass(
//				sourceFileName,
//				qPackageName,
//				qSourceFileName);
//		} else {
//			if (binaryFileName == null) {
//				qBinaryFileName = qualifiedTypeName + ".class"; //$NON-NLS-1$
//				binaryFileName = qBinaryFileName;
//				qPackageName =  ""; //$NON-NLS-1$
//				if (qualifiedTypeName.length() > typeName.length) {
//					int typeNameStart = qBinaryFileName.length() - typeName.length - 6; // size of ".class"
//					qPackageName =  qBinaryFileName.substring(0, typeNameStart - 1);
//					binaryFileName = qBinaryFileName.substring(typeNameStart);
//				}
//			}
//			answer = 
//				location.findClass(
//					binaryFileName, 
//					qPackageName, 
//					qBinaryFileName);
//		}
//		if (answer != null) return answer;
//	}
//	return null;
//}
//
//public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
//	if (typeName != null)
//		return findClass(
//			new String(CharOperation.concatWith(packageName, typeName, '/')),
//			typeName);
//	return null;
//}
//
//public NameEnvironmentAnswer findType(char[][] compoundName) {
//	if (compoundName != null)
//		return findClass(
//			new String(CharOperation.concatWith(compoundName, '/')),
//			compoundName[compoundName.length - 1]);
//	return null;
//}
//
//public boolean isPackage(char[][] compoundName, char[] packageName) {
//	return isPackage(new String(CharOperation.concatWith(compoundName, packageName, '/')));
//}
//
//public boolean isPackage(String qualifiedPackageName) {
//	for (int i = 0, length = this.locations.length; i < length; i++)
//		if (this.locations[i].isPackage(qualifiedPackageName))
//			return true;
//	return false;
//}

}
