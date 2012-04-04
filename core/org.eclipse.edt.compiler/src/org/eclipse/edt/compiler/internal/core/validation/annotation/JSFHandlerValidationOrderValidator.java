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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class JSFHandlerValidationOrderValidator implements IPartContentAnnotationValidationRule {

	public void validate(Node errorNode, Node target, Map allAnnotations, IProblemRequestor problemRequestor) {
		final TreeMap validationOrderAnnotationsMap = new TreeMap();		
		
		Map validationOrderAnnotations = (Map)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATIONORDER));
		
		if(validationOrderAnnotations != null){
			for (Iterator iter = validationOrderAnnotations.keySet().iterator(); iter.hasNext();) {
				IAnnotationBinding validationOrderAnnotation = (IAnnotationBinding) iter.next();
				
				ArrayList annotations = (ArrayList)validationOrderAnnotationsMap.get(validationOrderAnnotation.getValue());
				
				if(annotations == null){
					annotations = new ArrayList();
					validationOrderAnnotationsMap.put(validationOrderAnnotation.getValue(), annotations);
				}
				annotations.add(validationOrderAnnotations.get(validationOrderAnnotation));
			}
				
			// loop over list looking for errors
			validateDuplicateValues(validationOrderAnnotationsMap, problemRequestor);
		}
	}

	private void validateDuplicateValues(Map validationOrderAnnotations, IProblemRequestor problemRequestor) {
		for (Iterator iter = validationOrderAnnotations.keySet().iterator(); iter.hasNext();) {
			Integer nextValue = (Integer) iter.next();
			ArrayList expressions = (ArrayList) validationOrderAnnotations.get(nextValue);
			
			if(expressions.size() > 1){
				for(Iterator exprIter = expressions.iterator(); exprIter.hasNext();) {
					Object[] nextExpr = (Object[]) exprIter.next();
					problemRequestor.acceptProblem(
			            (Node) nextExpr[0],
						IProblemRequestor.DUPLICATE_VALIDATION_ORDER_VALUES_FOUND,
						new String[] {
			            	((IDataBinding) nextExpr[1]).getCaseSensitiveName(),
							nextValue.toString()});
				}
			}
		}		
	}
}
