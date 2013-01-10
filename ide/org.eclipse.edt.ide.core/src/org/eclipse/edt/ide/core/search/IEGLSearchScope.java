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
package org.eclipse.edt.ide.core.search;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.IEGLElement;

/**
 * An <code>IEGLSearchScope</code> defines where search result should be found by a
 * <code>SearchEngine</code>. Clients must pass an instance of this interface
 * to the <code>search(...)</code> methods. Such an instance can be created using the
 * following factory methods on <code>SearchEngine</code>: <code>createHierarchyScope(IType)</code>,
 * <code>createEGLSearchScope(IResource[])</code>, <code>createWorkspaceScope()</code>, or
 * clients may choose to implement this interface.
 */
public interface IEGLSearchScope {
/**
 * Checks whether the resource at the given path is enclosed by this scope.
 *
 * @param resourcePath if the resource is contained in
 * a JAR file, the path is composed of 2 paths separated
 * by <code>JAR_FILE_ENTRY_SEPARATOR</code>: the first path is the full OS path 
 * to the JAR (if it is an external JAR), or the workspace relative <code>IPath</code>
 * to the JAR (if it is an internal JAR), 
 * the second path is the path to the resource inside the JAR.
 * @return whether the resource is enclosed by this scope
 */
public boolean encloses(String resourcePath);
/**
 * Checks whether this scope encloses the given element.
 *
 * @param element the given element
 * @return <code>true</code> if the element is in this scope
 */
public boolean encloses(IEGLElement element);
/**
 * Returns the paths to the enclosing projects for this search scope.
 * <ul>
 * <li> If the path is a project path, this is the full path of the project
 *       (see <code>IResource.getFullPath()</code>).
 *        For example, /MyProject
 * </li>
 * </ul>
 * 
 * @return an array of paths to the enclosing projects and JARS.
 */
IPath[] enclosingProjects();

}
