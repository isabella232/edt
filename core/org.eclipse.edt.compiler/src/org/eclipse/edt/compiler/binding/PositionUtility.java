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
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.IndexOrientationKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
public class PositionUtility {
    
    IDataBinding dataBinding;
    int arraySize;
    private ICompilerOptions compilerOptions;
	private Scope currentScope;

    /**
     * 
     */
    public PositionUtility(IDataBinding dataBinding, int arraySize, ICompilerOptions compilerOptions, Scope currentScope) {
        super();
        this.dataBinding = dataBinding;
        this.arraySize = arraySize;
        this.compilerOptions = compilerOptions;
        this.currentScope = currentScope;
    }
    
    public void checkPosition() {
    	ITypeBinding positionType = currentScope.findPackage(InternUtil.intern("eglx")).resolvePackage(InternUtil.intern("ui")).resolveType(InternUtil.intern("Position"));
        if(IBinding.NOT_FOUND_BINDING == positionType) {
        	return;
        }
        
        if (arraySize > 0) {
            
            EnumerationDataBinding orientation = getOrientation();
            int columns = getColumns();
            int linesBetweenRows = getLinesBetweenRows();
            int spacesBetweenColums = getSpacesBetweenColumns();
            int fieldLen = getFieldLen();
            int[] position = getPosition();
            
            //Invoking dataBinding.addAnnotation in the for loop below makes the calls
            //to getAnnotation within the loop take progressively longer. It's more
            //efficient to store the new bindings in a local list and add them all to
            //dataBinding at once.
            List newAnnotations = new ArrayList();
            
            for (int i = 0; i < arraySize; i++) {
                IAnnotationBinding annotation = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "Position", i+1);
                if (annotation == null || !annotation.isForElement()) {
                    Integer[] newPos = calculateLogicalPosition(i, orientation, columns, linesBetweenRows, spacesBetweenColums, position, fieldLen);                    
					AnnotationBindingForElement posAnnotation = new AnnotationBindingForElement(InternUtil.internCaseSensitive("Position"), dataBinding.getDeclaringPart(), new AnnotationTypeBindingImpl((FlexibleRecordBinding) positionType, dataBinding.getDeclaringPart()), i+1);
                    posAnnotation.setValue(newPos, null, null, compilerOptions, false);
                    posAnnotation.setCalculated(true);
                    newAnnotations.add(posAnnotation);
                }
            }
            
            dataBinding.addAnnotations(newAnnotations);
        }
    }
    
   private EnumerationDataBinding getOrientation() {
        EnumerationDataBinding orientation = IndexOrientationKind.DOWN;
        IAnnotationBinding annotation = dataBinding.getAnnotation(new String[] {"egl", "ui", "text"}, "IndexOrientation");
        if (annotation != null) {
            if (annotation.getValue() != null) {
                orientation = (EnumerationDataBinding) annotation.getValue();
            }
        }
        return orientation;
    }

   private int getColumns() {
       int columns = 1;
       IAnnotationBinding annotation = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "Columns");
       if (annotation != null) {
           if (annotation.getValue() != null && annotation.getValue() instanceof Integer) {
               columns = ((Integer) annotation.getValue()).intValue();
           }
       }
       
       if (columns < 1) {
    	   return 1;
       }
       return columns;
   }

   private int getLinesBetweenRows() {
       int lines = 0;
       IAnnotationBinding annotation = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "LinesBetweenRows");
       if (annotation != null) {
           if (annotation.getValue() != null && annotation.getValue() instanceof Integer) {
               lines = ((Integer) annotation.getValue()).intValue();
           }
       }
       return lines;
   }
   
   private int getSpacesBetweenColumns() {
       int spaces = 1;
       IAnnotationBinding annotation = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "SpacesBetweenColumns");
       if (annotation != null) {
    	   if (annotation.getValue() != null && annotation.getValue() instanceof Integer) {
               spaces = ((Integer) annotation.getValue()).intValue();
           }
       }
       return spaces;
   }

   private int getFieldLen() {
       int len = 1;
       IAnnotationBinding annotation = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "FieldLen");
       if (annotation != null) {
    	   if (annotation.getValue() != null && annotation.getValue() instanceof Integer) {
               len = ((Integer) annotation.getValue()).intValue();
           }
       }
       return len;
   }
   
   private int[] getPosition() {
       Integer[] position = new Integer[] {new Integer(1), new Integer(1)};
       IAnnotationBinding annotation = dataBinding.getAnnotation(new String[] {"egl", "ui"}, "Position");
       if (annotation != null) {
    	   if (annotation.getValue() != null && annotation.getValue() instanceof Integer[]) {
               Integer[] pos = (Integer[])annotation.getValue();
               if (pos.length == 2) {
                   position = pos;
               }
           }
       }
       
       int[] result = new int[2];
       result[0] = position[0].intValue();
       result[1] = position[1].intValue();
       return result;
   }

    
	private Integer[] calculateLogicalPosition(int index, EnumerationDataBinding orientation, int columns, int linesBetweenRows, int spacesBetweenColumns, int[] initialElementPosition, int fieldLen) {
		boolean indexOrientationIsDown = (IEGLConstants.MNEMONIC_DOWN.equalsIgnoreCase(orientation.getName()));
		int[] elementPosition = new int[2];

		if (indexOrientationIsDown) {
			// Instead of having a seperate "rows" property for use with indexOrientation=down, Paul Hoffman has
			// decided that rows should be calculated as follows
		    int rows = arraySize / columns;
		    if ((arraySize % columns) > 0) {
				rows = rows + 1;
		    }
			elementPosition[0] = initialElementPosition[0] + (index % rows) * (linesBetweenRows + 1); //row
			elementPosition[1] = initialElementPosition[1] + (index / rows) * (spacesBetweenColumns + fieldLen);
		} else {
			elementPosition[0] = initialElementPosition[0] + (index / columns) * (linesBetweenRows + 1); //row
			elementPosition[1] = initialElementPosition[1] + (index % columns) * (spacesBetweenColumns + fieldLen);
		}

		Integer[] result = new Integer[2];
		result[0] = new Integer(elementPosition[0]);
		result[1] = new Integer(elementPosition[1]);
		return result;
	}

    

}
