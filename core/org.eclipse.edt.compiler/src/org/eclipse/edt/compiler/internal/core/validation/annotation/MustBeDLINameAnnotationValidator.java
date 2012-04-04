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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.ValueValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class MustBeDLINameAnnotationValidator extends ValueValidationAnnotationTypeBinding {
	
	public static ValueValidationAnnotationTypeBinding INSTANCE = new MustBeDLINameAnnotationValidator();
	
	private MustBeDLINameAnnotationValidator() {
		super(InternUtil.internCaseSensitive("MustBeDLINameAnnotationValidator"));
	}
	
	public void validate(final Node errorNode, Node target, IAnnotationBinding annotationBinding, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		final String name = annotationBinding.getValue().toString();
		checkDLIName(name, new DLINameProblemRequestor() {
			
			public void nameTooLong() {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.DLI_NAME_TOO_LONG,
					new String[] {name});
			}
			
			public void badFirstCharacter() {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.DLI_NAME_BAD_FIRST_CHAR,
					new String[] {name});
			}
			
			public void badCharacter() {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.DLI_NAME_BAD_CHAR,
					new String[] {name});
			}
		});
	}
	
	public static boolean isValidDLIName( String name ) {
		final boolean[] result = new boolean[] {true};
		checkDLIName(name, new DLINameProblemRequestor() {
			public void nameTooLong() { result[0] = false; }
			public void badFirstCharacter() { result[0] = false; }
			public void badCharacter() { result[0] = false; }
		});
		return result[0];
	}
	
	public static interface DLINameProblemRequestor {
		void nameTooLong();
		void badFirstCharacter();
		void badCharacter();
	}
	
	public static void checkDLIName(String name, DLINameProblemRequestor problemRequestor ) {
		if( name.length() > 8 ) {
			problemRequestor.nameTooLong();
		}
		if( name.length() != 0 ) {
			char ch = name.charAt( 0 );
			if( !Character.isLetter( ch ) && ch != '@' && ch != '$' && ch != '#' ) {
				problemRequestor.badFirstCharacter();
			}
			if( name.length() != 1 ) {
				for( int i = 1; i < name.length(); i++ ) {
					ch = name.charAt( i );
					if( !Character.isLetter( ch ) && !Character.isDigit( ch ) && ch != '@' && ch != '$' && ch != '#' ) {
						problemRequestor.badCharacter();
					}					
				}
			}
		}
	}	
}
