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
package org.eclipse.edt.mof.eglx.rui.validation.annotation;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.eglx.rui.messages.RUIResourceKeys;


/**
 * @author svihovec
 */
public class DateFormatAnnotationValueValidator implements IValueValidationRule {
	private static Set<Character> invalidDateFormatChars = new TreeSet();
	static {
		invalidDateFormatChars.add( 'Y' );
	}
	
	public void validate(Node errorNode, Node target, Annotation annotation, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotation.getValue() instanceof String) {
			String value = (String)annotation.getValue();
			try {
				char[] dateFormatPropertyValue = value.toCharArray();
				boolean validating = true;
				Set<Character> markedChars = new TreeSet();
									
				for( int i = 0; i < dateFormatPropertyValue.length; i++ ) {
					char currentChar = dateFormatPropertyValue[i];
					if( currentChar == '\'' ) {
						validating = !validating;
					}
					else if( validating &&
							 invalidDateFormatChars.contains( currentChar ) &&
							 !markedChars.contains( currentChar ) ) {
						markedChars.add( currentChar );
						problemRequestor.acceptProblem(errorNode,
								RUIResourceKeys.PROPERTY_INVALID_CHARACTER_IN_DATEFORMAT,
								IMarker.SEVERITY_ERROR,
								new String[] {IEGLConstants.PROPERTY_DATEFORMAT, "'" + currentChar + "'"},
								RUIResourceKeys.getResourceBundleForKeys());
					}
				}			
			}
			catch( Exception e ) {
			}
		}
	}
}
