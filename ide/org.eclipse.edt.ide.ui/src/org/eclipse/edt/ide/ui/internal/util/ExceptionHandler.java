/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class ExceptionHandler {
	
	public static Collection getFilteredSystemExceptions(AnnotationTypeManager annoTypeMgr) {
		Collection result = new ArrayList();
		
		if(null != annoTypeMgr){
			Collection exceptions = CapabilityFilterUtility.filterParts(annoTypeMgr.getSystemPackageAnnotations().values());
			for(Iterator iter = exceptions.iterator(); iter.hasNext();) {
				FlexibleRecordBinding rec = (FlexibleRecordBinding) iter.next();
				IPartSubTypeAnnotationTypeBinding subType = rec.getSubType();
				if (subType != null && subType.getName() == InternUtil.intern(IEGLConstants.RECORD_SUBTYPE_EXCEPTION))
					result.add(rec);
			}
			
		}

		return result;
	}
}
