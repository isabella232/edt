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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author svihovec
 *
 */
public class DateFormatAnnotationValueValidator implements IValueValidationRule {
	private static Set invalidDateFormatChars = new TreeSet();
	static {
		invalidDateFormatChars.add( new Character( 'Y' ) );
	}
	
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotationBinding.getValue() != null && annotationBinding.getValue() != IBinding.NOT_FOUND_BINDING) {
			if (annotationBinding.getValue() instanceof String){
				String value = (String)annotationBinding.getValue();
				try {
					char[] dateFormatPropertyValue = value.toCharArray();
					boolean validating = true;
					Set markedChars = new TreeSet();
										
					for( int i = 0; i < dateFormatPropertyValue.length; i++ ) {
						char currentChar = dateFormatPropertyValue[i];
						if( currentChar == '\'' ) {
							validating = !validating;
						}
						else if( validating &&
								 invalidDateFormatChars.contains( new Character( currentChar ) ) &&
								 !markedChars.contains( new Character( currentChar ) ) ) {
							markedChars.add( new Character( currentChar ) );
							problemRequestor.acceptProblem(errorNode,
									IProblemRequestor.PROPERTY_INVALID_CHARACTER_IN_DATEFORMAT,
									new String[] {IEGLConstants.PROPERTY_DATEFORMAT,
									"'" + currentChar + "'"});
								

						}
					}			
				}
				catch( Exception e ) {			
				}
			}


		}
	}
}
