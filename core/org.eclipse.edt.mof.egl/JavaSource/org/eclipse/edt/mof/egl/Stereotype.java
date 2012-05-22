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
package org.eclipse.edt.mof.egl;

/**
 * This class is the superclass for all instances of <code>StereotypeType</code>.
 * Stereotype values are meta-data that are associated to Classifier
 * elements to provide extra information beyond the information provided within
 * the given classifier declaration itself. 
 * 
 * For instance the following record declaration adds an instance of the
 * Annotation <code>egl.io.sql.SQLRecord</code> to the record declaration
 * @example record Customer type SQLRecord {tableNames=["CUSTTBL"]}
 * 
 * @version 8.0.0
 *
 */

public interface Stereotype extends Annotation {
}
