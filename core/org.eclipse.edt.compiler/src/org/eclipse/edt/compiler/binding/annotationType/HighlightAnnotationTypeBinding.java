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
package org.eclipse.edt.compiler.binding.annotationType;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.HighlightKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;



class HighlightAnnotationTypeBinding extends EnumerationValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("highlight");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static HighlightAnnotationTypeBinding INSTANCE = new HighlightAnnotationTypeBinding();

	private HighlightAnnotationTypeBinding() {
		super(caseSensitiveName, HighlightKind.TYPE);
	}
	
	public static HighlightAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
	    if (binding.isOpenUIStatementBinding()) {
	        return true;
	    }
		return takesFormFieldAnnotations(binding);
	}
	
	public Object getDefaultValue() {
		return HighlightKind.NOHIGHLIGHT;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}

	public boolean supportsElementOverride() {
        return true;
    }
	

}
