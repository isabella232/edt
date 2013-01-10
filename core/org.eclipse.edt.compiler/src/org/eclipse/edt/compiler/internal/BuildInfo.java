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
 * @version 	1.0
 * @author
 */
public class BuildInfo {
  public static final String fgBuildLevel = "20020425_0301-Driver13"; //$NON-NLS-1$
  public static String level() { return fgBuildLevel; }
  public static String getWSABuildLevel() { return fgBuildLevel; }

}
