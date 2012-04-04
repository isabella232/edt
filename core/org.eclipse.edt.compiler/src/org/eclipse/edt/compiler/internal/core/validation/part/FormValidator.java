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
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class FormValidator extends AbstractASTVisitor {
	
	private static final String[] EGLUITEXT = new String[] {"egl", "ui", "text"};
	private static final String[] EGLUI = new String[] {"egl", "ui"};
	
	private static class FloatingAreaInfo {
		int leftMargin, topMargin;
		int[] size;
		boolean isDoubleByte;
		
		FloatingAreaInfo(int leftMargin, int topMargin, int[] size) {
			this.leftMargin = leftMargin;
			this.topMargin = topMargin;
			this.size = size;
		}
		
		boolean formOfSizeFits(int[] formSize) {
			return formSize[0] + topMargin <= size[0] &&
			       formSize[1] + leftMargin <= size[1];
		}
	}
	private static interface IFieldInfo {
		boolean isAttribute();
		boolean isPrintField();
		boolean isTextField();
		
		boolean canExtendBeyondFormBoundary();
		boolean canAlwaysWrap();
		boolean isCursor();
		Integer getValidationOrder();
		
		Node getNodeForErrors();
		int getFieldLength();
		int[] getFieldPosition();
		String getCanonicalFieldName();
		FormFieldBinding getFieldBinding();
		
		void setNodeForErrors(Node nodeForErrors);
		void setFieldLength(int fieldLength);
		void setFieldPosition(int[] fieldPosition);
		void setCanonicalFieldName(String canonicalFieldName);
		void setFieldBinding(FormFieldBinding fieldBinding);
		
		void setIndex(int index);
		int getIndex();
		boolean isCursorOnSpecificIndex();
		
		
	}
	
	private static class AttributeFieldInfo implements IFieldInfo {
		private IFieldInfo fieldInfo;

		public AttributeFieldInfo(IFieldInfo fieldInfo) {
			super();
			this.fieldInfo = fieldInfo;
		}

		public boolean canAlwaysWrap() {
			return fieldInfo.canAlwaysWrap();
		}

		public boolean canExtendBeyondFormBoundary() {
			return fieldInfo.canExtendBeyondFormBoundary();
		}

		public String getCanonicalFieldName() {
			return fieldInfo.getCanonicalFieldName();
		}

		public FormFieldBinding getFieldBinding() {
			return fieldInfo.getFieldBinding();
		}

		public int getFieldLength() {
			return fieldInfo.getFieldLength();
		}

		public int[] getFieldPosition() {
			return fieldInfo.getFieldPosition();
		}

		public Node getNodeForErrors() {
			return fieldInfo.getNodeForErrors();
		}

		public Integer getValidationOrder() {
			return fieldInfo.getValidationOrder();
		}

		public boolean isAttribute() {
			return true;
		}

		public boolean isCursor() {
			return fieldInfo.isCursor();
		}

		public void setCanonicalFieldName(String canonicalFieldName) {
			fieldInfo.setCanonicalFieldName(canonicalFieldName);
		}

		public void setFieldBinding(FormFieldBinding fieldBinding) {
			fieldInfo.setFieldBinding(fieldBinding);
		}

		public void setFieldLength(int fieldLength) {
			fieldInfo.setFieldLength(fieldLength);
		}

		public void setFieldPosition(int[] fieldPosition) {
			fieldInfo.setFieldPosition(fieldPosition);
		}

		public void setNodeForErrors(Node nodeForErrors) {
			fieldInfo.setNodeForErrors(nodeForErrors);
		}
		
		public boolean equals(Object obj) {
			return fieldInfo.equals(obj);
		}

		public boolean isPrintField() {
			return false;
		}

		public boolean isTextField() {
			return false;
		}

		public int getIndex() {
			return fieldInfo.getIndex();
		}

		public void setIndex(int index) {
			fieldInfo.setIndex(index);
		}

		public boolean isCursorOnSpecificIndex() {
			return fieldInfo.isCursorOnSpecificIndex();
		}

		
	}
	
	
	private static abstract class FieldInfo implements IFieldInfo{

		private Node nodeForErrors;
		private int fieldLength;
		private int[] fieldPosition;
		private String canonicalFieldName;
		private FormFieldBinding fieldBinding;
		private int index;
				
		
		public boolean isCursorOnSpecificIndex() {
			if (getIndex() < 1 || !isCursor()) {
				return false;
			}
			
			return getFieldBinding().getAnnotation(EGLUITEXT, "Cursor", getIndex()) != getFieldBinding().getAnnotation(EGLUITEXT, "Cursor");
			
		}
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public Node getNodeForErrors() {
			return nodeForErrors;
		}
		public int getFieldLength() {
			return fieldLength;
		}
		public int[] getFieldPosition() {
			return fieldPosition;
		}
		public String getCanonicalFieldName() {
			return canonicalFieldName;
		}
		public FormFieldBinding getFieldBinding() {
			return fieldBinding;
		}
		public void setNodeForErrors(Node nodeForErrors) {
			this.nodeForErrors = nodeForErrors;
		}
		public void setFieldLength(int fieldLength) {
			this.fieldLength = fieldLength;
		}
		public void setFieldPosition(int[] fieldPosition) {
			this.fieldPosition = fieldPosition;
		}
		public void setCanonicalFieldName(String canonicalFieldName) {
			this.canonicalFieldName = canonicalFieldName;
		}
		public void setFieldBinding(FormFieldBinding fieldBinding) {
			this.fieldBinding = fieldBinding;
		}
		
		
		public boolean equals(Object obj) {
			if(obj==this) return true;
			if(obj instanceof IFieldInfo) {
				IFieldInfo other = (IFieldInfo) obj;
				return(other.getNodeForErrors() == getNodeForErrors() &&
					   other.getFieldLength() == getFieldLength() &&
					   other.getFieldPosition()[0] == getFieldPosition()[0] &&
					   other.getFieldPosition()[1] == getFieldPosition()[1] &&
					   other.getCanonicalFieldName().equals(getCanonicalFieldName()));
			}
			return false;
		}
		
		public int hashCode() {
			int result = 17;
			result = 37*result + nodeForErrors.hashCode();
			result = 37*result + fieldLength;
			result = 37*result + fieldPosition[0];
			result = 37*result + fieldPosition[1];
			result = 37*result + canonicalFieldName.hashCode();
			return result;
		}
		
		protected abstract IFieldInfo getNewInstance();
		
		IFieldInfo copy() {
			IFieldInfo result = getNewInstance();
			result.setNodeForErrors(nodeForErrors);
			result.setFieldLength(fieldLength);
			result.setFieldPosition(new int[] {fieldPosition[0], fieldPosition[1]});
			result.setCanonicalFieldName(canonicalFieldName);
			return result;
		}
		public boolean isAttribute() {
			return false;
		}
		public boolean isTextField() {
			return false;
		}
		public boolean isPrintField() {
			return false;
		}
	}

	private static abstract class TextFormFieldInfo extends FieldInfo {
		
		public boolean canExtendBeyondFormBoundary() {			
			return false;
		}
		
		public boolean isCursor() {
			
			IAnnotationBinding aBinding;

			if (getIndex() > 1) {
				aBinding = getFieldBinding().getAnnotation(EGLUITEXT, "Cursor", getIndex());
				if (aBinding == getFieldBinding().getAnnotation(EGLUITEXT, "Cursor")) {
					aBinding = null;
				}
			}
			else {
				if (getIndex() == 1) {
					aBinding = getFieldBinding().getAnnotation(EGLUITEXT, "Cursor", getIndex());
				}
				else {
					aBinding = getFieldBinding().getAnnotation(EGLUITEXT, "Cursor");
				}
			}
			
			if(aBinding != null) {
				return Boolean.YES == aBinding.getValue();
			}
			return false;
		}
		
		public Integer getValidationOrder() {
			IAnnotationBinding aBinding = getFieldBinding().getAnnotation(EGLUI, "ValidationOrder");
			if(aBinding != null) {
				try {
					return new Integer(aBinding.getValue().toString());
				}
				catch(NumberFormatException e) {
					return null;
				}
			}
			return null;			
		}
		public boolean isTextField() {
			return true;
		}
	}
	
	private static abstract class PrintFormFieldInfo extends FieldInfo {
		public boolean canExtendBeyondFormBoundary() {
			return true;
		}
		
		public boolean isCursor() {			
			return false;
		}
		
		public Integer getValidationOrder() {
			return null;			
		}
		
		public boolean isPrintField() {
			return true;
		}
	}
	
	private static class VariableTextFormFieldInfo extends TextFormFieldInfo {
		protected IFieldInfo getNewInstance() {
			return new VariableTextFormFieldInfo();
		}
		
		public boolean canAlwaysWrap() {
			return false;
		}
	}
	
	private static class ConstantTextFormFieldInfo extends TextFormFieldInfo {
		protected IFieldInfo getNewInstance() {
			return new ConstantTextFormFieldInfo();
		}
		
		public boolean canAlwaysWrap() {
			return true;
		}
	}
	
	private static class VariablePrintFormFieldInfo extends PrintFormFieldInfo {
		protected IFieldInfo getNewInstance() {
			return new VariablePrintFormFieldInfo();
		}
		
		public boolean canAlwaysWrap() {
			return false;
		}
	}
	
	private static class ConstantPrintFormFieldInfo extends PrintFormFieldInfo {
		protected IFieldInfo getNewInstance() {
			return new ConstantPrintFormFieldInfo();
		}
		
		public boolean canAlwaysWrap() {
			return true;
		}
	}
	
	private static class FieldInfoPair {
		IFieldInfo firstInfo;
		IFieldInfo secondInfo;
		
		FieldInfoPair(IFieldInfo firstInfo, IFieldInfo secondInfo) {
			this.firstInfo = firstInfo;
			this.secondInfo = secondInfo;
		}
		
		public boolean equals(Object obj) {
			if(obj==this) return true;
			if(obj instanceof FieldInfoPair) {
				FieldInfoPair other = (FieldInfoPair) obj;
				return(other.firstInfo.equals(firstInfo) &&
					   other.secondInfo.equals(secondInfo));
			}
			return false;
		}
		
		public int hashCode() {
			int result = 17;
			result = 37*result + firstInfo.hashCode();
			result = 37*result + secondInfo.hashCode();
			return result;
		}	
	}
	
	private static interface IFormGridProblemAcceptor {
		void fieldsOverlap(IFieldInfo fieldOnGrid, IFieldInfo fieldBeingAdded);
		void fieldOutsideForm(IFieldInfo fieldInfo);
		void fieldExtendsOutsideForm(IFieldInfo fieldInfo);
		void formSizeIsInvalid();
		void formPositionIsInvalid();
	}
	
	private static class FormGridProblemCollector implements IFormGridProblemAcceptor {
		List overlappingFieldProblems = new ArrayList();
		List fieldOutsideFormProblems = new ArrayList();
		List fieldExtendingOutsideFormProblems = new ArrayList();
		boolean formSizeIsInvalid = false;
		boolean formPositionInvalid = false;
		boolean hasProblem = false;
		
		public void fieldsOverlap(IFieldInfo fieldOnGrid, IFieldInfo fieldBeingAdded) {
			FieldInfoPair fInfoPair = new FieldInfoPair(fieldOnGrid, fieldBeingAdded);
			if(!overlappingFieldProblems.contains(fInfoPair)) {
				overlappingFieldProblems.add(fInfoPair);
			}
			hasProblem = true;
		}
		
		public void fieldOutsideForm(IFieldInfo fieldInfo) {
			if(!fieldOutsideFormProblems.contains(fieldInfo)) {
				fieldOutsideFormProblems.add(fieldInfo);
			}
			hasProblem = true;
		}
		
		public void fieldExtendsOutsideForm(IFieldInfo fieldInfo) {
			if(!fieldExtendingOutsideFormProblems.contains(fieldInfo)) {
				fieldExtendingOutsideFormProblems.add(fieldInfo);
			}
			hasProblem = true;
		}
		
		public void formPositionIsInvalid() {
			formPositionInvalid = true;
			hasProblem = true;
		}
		
		public void formSizeIsInvalid() {
			formSizeIsInvalid = true;
			hasProblem = true;
		}
	}
	
	private static class FormGrid {
		private IFieldInfo[][] grid;
		
		boolean horizontalWrappingIsAllowed = false;
		boolean verticalWrappingIsAllowed = false;
		
		FormGrid(int[] formSize) {
			grid = new IFieldInfo[formSize[0]][formSize[1]];
		}
		
		//void applyAttributeByte(FieldInfo fieldInfo, IFormGridProblemAcceptor problemAcceptor, int[] formPosition, int[] formSize) {
		void applyAttributeByte(IFieldInfo fieldInfo, IFormGridProblemAcceptor problemAcceptor, int[] formPosition) {
			int zeroBasedRow = fieldInfo.getFieldPosition()[0]-1;
			int zeroBasedCol = fieldInfo.getFieldPosition()[1]-1;
			
			if(zeroBasedRow < 0 || zeroBasedRow >= grid.length ||
			   zeroBasedCol < 0 || zeroBasedCol >= grid[0].length) {
				return;
			}			
			
			int attributeRow = zeroBasedRow;
			int attributeColumn = zeroBasedCol - 1;
			if (attributeColumn == -1)
			{
				if(formPosition != null && formPosition[1] != 1) {
					return;
				}
//				attributeColumn = formPosition[1] - 1 + formSize[1] - 1;
//				attributeRow--;
//				if (attributeRow < 0)
//				{
//					attributeRow = formPosition[0] - 1 + formSize[0] - 1;
//				}
				
				attributeColumn = grid[0].length-1;
				attributeRow--;
				if (attributeRow == -1)
				{
					if(formPosition != null && formPosition[0] != 1) {
						return;
					}
					attributeRow = grid.length-1;
				}
			}
			
			if(attributeRow < 0 || attributeRow >= grid.length ||
			   attributeColumn < 0 || attributeColumn >= grid[0].length) {
				return;
			}
			
			IFieldInfo fieldAtPosition = grid[attributeRow][attributeColumn];
			AttributeFieldInfo afi = new AttributeFieldInfo(fieldInfo);
			if(fieldAtPosition == null) {
					grid[attributeRow][attributeColumn] = afi;
			}
			else {
				problemAcceptor.fieldsOverlap(fieldAtPosition, afi);
			}
		}
		
		//void applyField(FieldInfo fieldInfo, IFormGridProblemAcceptor problemAcceptor, int[] formPosition, int[] formSize) {
		void applyField(IFieldInfo fieldInfo, IFormGridProblemAcceptor problemAcceptor) {
			int zeroBasedRow = fieldInfo.getFieldPosition()[0]-1;
			int zeroBasedCol = fieldInfo.getFieldPosition()[1]-1;
			
			if(zeroBasedRow < 0 || zeroBasedCol < 0) {
				//already error somewhere
				return;
			}
			
			for(int i = 0; i < fieldInfo.getFieldLength(); i++) {
				if(zeroBasedRow >= grid.length || zeroBasedCol >= grid[0].length) {
					problemAcceptor.fieldOutsideForm(fieldInfo);
					return;
				}
				
				IFieldInfo fieldAtPosition = grid[zeroBasedRow][zeroBasedCol];
				if(fieldAtPosition == null) {
					grid[zeroBasedRow][zeroBasedCol] = fieldInfo;
				}
				else {
					problemAcceptor.fieldsOverlap(fieldAtPosition, fieldInfo);
				}
				
				zeroBasedCol += 1;
				
				if(i != fieldInfo.getFieldLength()-1) {				
					if(zeroBasedCol == grid[0].length) {
						if(fieldInfo.canExtendBeyondFormBoundary()) {
							return;
						}
						
						if(horizontalWrappingIsAllowed || fieldInfo.canAlwaysWrap()) {
							zeroBasedCol = 0;
							zeroBasedRow += 1;
							
							if(zeroBasedRow == grid.length) {
								if(verticalWrappingIsAllowed || fieldInfo.canAlwaysWrap()) {
									zeroBasedRow = 0;
								}
								else {
									problemAcceptor.fieldExtendsOutsideForm(fieldInfo);
									return;
								}
							}						
						}
						else {
							problemAcceptor.fieldExtendsOutsideForm(fieldInfo);
							return;
						}
					}
				}
			}
		}
	}
	
	private abstract class FormGridValidator {
		abstract void applyFieldToGrid(IFieldInfo fieldInfo);
		
		abstract void addFloatingArea(FloatingAreaInfo floatingAreaInfo);
		abstract void useDefaultFloatingArea();		
		abstract void addScreenSize(int[] screenSize);		
		abstract void reportFormContextProblems();
		abstract void reportFormGroupContextProblems();
		
		protected void requestOverlappingFieldsProblem(IFieldInfo fieldOnGrid, IFieldInfo fieldBeingAdded) {
			int sev = IMarker.SEVERITY_ERROR;
			if((fieldOnGrid.isAttribute() && fieldBeingAdded.isPrintField()) || (fieldOnGrid.isPrintField() && fieldBeingAdded.isAttribute()) ) {
				sev = IMarker.SEVERITY_WARNING;
			}
			
			problemRequestor.acceptProblem(
				fieldBeingAdded.getNodeForErrors(),
				IProblemRequestor.INVALID_FORM_FIELD_OVERLAPPING,
				sev,
				new String[] {
					canonicalFormName,
					fieldOnGrid.getCanonicalFieldName(),
					fieldBeingAdded.getCanonicalFieldName()
				});
		}
		
		protected void requestFieldOutsideFormProblem(IFieldInfo fieldInfo) {
			problemRequestor.acceptProblem(
				fieldInfo.getNodeForErrors(),
				IProblemRequestor.INVALID_FORM_FIELD_OVERFLOWING,
				new String[] {
					canonicalFormName,
					fieldInfo.getCanonicalFieldName()
				});
		}
		
		protected void requestFieldExtendsOutsideFormProblem(IFieldInfo fieldInfo) {
			problemRequestor.acceptProblem(
				fieldInfo.getNodeForErrors(),
				IProblemRequestor.INVALID_FORM_FIELD_WRAPPING,
				new String[] {
					canonicalFormName,
					fieldInfo.getCanonicalFieldName()
				});
		}
	}
	
	private abstract class PrintFormGridValidator extends FormGridValidator implements IFormGridProblemAcceptor {
		private FormGrid formGrid;
		private List overlappingFieldsProblems = new ArrayList();
		private List fieldOutsideFormProblems = new ArrayList();
		
		public PrintFormGridValidator() {
			formGrid = new FormGrid(formSize);
		}
		
		void applyFieldToGrid(IFieldInfo fieldInfo) {
			formGrid.applyField(fieldInfo, this);
			formGrid.applyAttributeByte(fieldInfo, this, formPosition);
		}
		
		public void fieldsOverlap(IFieldInfo fieldOnGrid, IFieldInfo fieldBeingAdded) {
			FieldInfoPair fInfoPair = new FieldInfoPair(fieldOnGrid, fieldBeingAdded);
			if(!overlappingFieldsProblems.contains(fInfoPair)) {
				overlappingFieldsProblems.add(fInfoPair);
			}
			else {
				if (!fieldOnGrid.isAttribute() && !fieldBeingAdded.isAttribute()) {
					//Replace a possible warning with an error
					overlappingFieldsProblems.remove(fInfoPair);
					overlappingFieldsProblems.add(fInfoPair);
				}
			}
		}
		
		public void fieldOutsideForm(IFieldInfo fieldInfo) {
			if(!fieldOutsideFormProblems.contains(fieldInfo)) {
				fieldOutsideFormProblems.add(fieldInfo);
			}
		}
		
		public void fieldExtendsOutsideForm(IFieldInfo fieldInfo) {
			throw new RuntimeException("This should not happen.");
		}
		
		public void formPositionIsInvalid() {
		}
		
		public void formSizeIsInvalid() {
		}
		
		void reportFormContextProblems() {
			for(Iterator iter = overlappingFieldsProblems.iterator(); iter.hasNext();) {
				FieldInfoPair nextProblem = (FieldInfoPair) iter.next();
				requestOverlappingFieldsProblem(nextProblem.firstInfo, nextProblem.secondInfo);
			}
			for(Iterator iter = fieldOutsideFormProblems.iterator(); iter.hasNext();) {
				IFieldInfo nextProblem = (IFieldInfo) iter.next();
				requestFieldOutsideFormProblem(nextProblem);
			}
		}
		
		void reportFormGroupContextProblems() {
		}
		
		void addFloatingArea(FloatingAreaInfo floatingAreaInfo) {
		}
		
		void addScreenSize(int[] screenSize) {
			if(formSize[0] > screenSize[0] || formSize[1] > screenSize[1]) {
				problemRequestor.acceptProblem(
					getFormSizeNode(),
					IProblemRequestor.INVALID_FORM_SIZE_PROPERTY_VALUE,
					new String[] {IEGLConstants.PROPERTY_FORMSIZE, canonicalFormName});
			}
			else {
				primAddScreenSize(screenSize);
			}
		}
		
		protected void primAddScreenSize(int[] screenSize) {
		}

		void useDefaultFloatingArea() {
		}
	}
	
	private class FixedPrintFormGridValidator extends PrintFormGridValidator {
		protected void primAddScreenSize(int[] screenSize) {
			Node positionNode = getPositionNode();
			if(formPosition[0] > screenSize[0] || formPosition[1] > screenSize[1]) {
				problemRequestor.acceptProblem(
					positionNode,
					IProblemRequestor.INVALID_FORM_SIZE_PROPERTY_VALUE,
					new String[] {IEGLConstants.PROPERTY_POSITION, canonicalFormName});
			}
			else if(formPosition[0]+formSize[0]-1 > screenSize[0] ||
					formPosition[1]+formSize[1]-1 > screenSize[1]) {
				problemRequestor.acceptProblem(
					getFormSizeNode(),
					IProblemRequestor.INVALID_FORM_SIZE_PROPERTY_VALUE,
					new String[] {IEGLConstants.PROPERTY_FORMSIZE, canonicalFormName});
			}
		}
	}
	
	private class FloatingPrintFormGridValidator extends PrintFormGridValidator {
		private boolean isFormSizeProblem = false;
		private java.lang.Boolean formHasDBCharField; 
		
		void addFloatingArea(FloatingAreaInfo floatingAreaInfo) {
			if(!isFormSizeProblem) {
				if(!floatingAreaInfo.formOfSizeFits(formSize)) {
					isFormSizeProblem = true;
				}				
			}
		}
		
		void useDefaultFloatingArea() {
			FloatingAreaInfo floatingAreaInfo = null;
			
			if(getHasDBCharField()) {
				floatingAreaInfo = new FloatingAreaInfo(0, 0, new int[] {255, 158});
			}
			else {
				floatingAreaInfo = new FloatingAreaInfo(0, 0, new int[] {255, 132});
			}
			addFloatingArea(floatingAreaInfo);
		}
		
		void reportFormGroupContextProblems() {
			super.reportFormGroupContextProblems();
			if(isFormSizeProblem) {
				problemRequestor.acceptProblem(
					getFormSizeNode(),
					IProblemRequestor.INVALID_FORM_SIZE_PROPERTY_VALUE,
					new String[] {IEGLConstants.PROPERTY_FORMSIZE, canonicalFormName});
			}
		}
		
		private boolean getHasDBCharField() {
			if(formHasDBCharField == null) {
				formHasDBCharField = new java.lang.Boolean(hasDBCharField(formBinding));
			}
			return formHasDBCharField.booleanValue();
		}
	}
	
	private abstract class TextFormGridValidator extends FormGridValidator {
		private final List defaultScreenSizes = Arrays.asList(new int[][] {
			new int[] {12,80}, new int[] {24,80},
			new int[] {32,80}, new int[] {43,80},
			new int[] {27,132}	
		});
		
		private int[] getMatchingDefaultScreenSize(int[] formSize) {
			for(Iterator iter = defaultScreenSizes.iterator(); iter.hasNext();) {
				int[] nextScreenSize= (int[]) iter.next();
				if(nextScreenSize[0] == formSize[0] && nextScreenSize[1] == formSize[1]) {
					return nextScreenSize;
				}
			}
			return null;
		}
		
		protected Map formGridsToProblemCollectors = new HashMap();
		protected Map formGridsToScreenSizes = new HashMap();
		protected boolean usingDefaultScreenSizes = false;
		private FormGrid firstFormGrid;
		protected Set screenSizesWhichFit = new HashSet();
		
		TextFormGridValidator() {
		}
		
		void addScreenSize(int[] screenSize) {
			primAddScreenSize(screenSize);
		}
		
		protected FormGrid primAddScreenSize(int[] screenSize) {
			FormGrid newFormGrid = new FormGrid(formSize);
			newFormGrid.horizontalWrappingIsAllowed = screenSize[1] == formSize[1];
			newFormGrid.verticalWrappingIsAllowed = screenSize[0] == formSize[0];
			
			formGridsToProblemCollectors.put(newFormGrid, new FormGridProblemCollector());
			formGridsToScreenSizes.put(newFormGrid, screenSize);
			
			if(firstFormGrid == null) {
				firstFormGrid = newFormGrid;
			}
			
			return newFormGrid;
		}
		
		protected abstract int[] getFormPosition(int[] screenSize);
		
		private void verifyFormGridsToProblemCollectorsNotEmpty() {
			if(formGridsToProblemCollectors.isEmpty()) {
				int[] matchingDefaultScreenSize = getMatchingDefaultScreenSize(formSize);
				if(matchingDefaultScreenSize == null) {
					usingDefaultScreenSizes = true;
					for(Iterator iter = defaultScreenSizes.iterator(); iter.hasNext();) {
						primAddScreenSize((int[]) iter.next());
					}
				}
				else {
					primAddScreenSize(matchingDefaultScreenSize);
				}
			}
		}
		
		void applyFieldToGrid(IFieldInfo fieldInfo) {
			verifyFormGridsToProblemCollectorsNotEmpty();
			
			for(Iterator iter = formGridsToProblemCollectors.keySet().iterator(); iter.hasNext();) {
				FormGrid nextFormGrid = (FormGrid) iter.next();
				IFormGridProblemAcceptor problemAcceptor = (IFormGridProblemAcceptor) formGridsToProblemCollectors.get(nextFormGrid); 
				nextFormGrid.applyAttributeByte(fieldInfo, problemAcceptor, formPosition);
				nextFormGrid.applyField(fieldInfo, problemAcceptor);
			}
		}
		
		protected void reportProblems(IFormGridProblemAcceptor problemAcceptor) {
			verifyFormGridsToProblemCollectorsNotEmpty();
			if(usingDefaultScreenSizes) {
				//The fields have to 'fit' for one of the default screen sizes. If they do,
				//we do not issue error messages for the default screen sizes that the fields
				//do not fit
				for(Iterator iter = formGridsToProblemCollectors.keySet().iterator(); iter.hasNext();) {
					FormGrid nextFormGrid = (FormGrid) iter.next();
					FormGridProblemCollector nextProblemCollector = (FormGridProblemCollector) formGridsToProblemCollectors.get(nextFormGrid);
				
					if(nextProblemCollector.hasProblem == false) {
						screenSizesWhichFit.add(new IntAry((int[]) formGridsToScreenSizes.get(nextFormGrid)));
						return;
					}
				}
				
				//Remove everything from the problems map except for the first grid and its
				//problems, and issue them
				Object firstFormGridsProblems = formGridsToProblemCollectors.get(firstFormGrid);
				formGridsToProblemCollectors.clear();
				formGridsToProblemCollectors.put(firstFormGrid, firstFormGridsProblems);
			}
			
			boolean firstPass = true;
			boolean issuedError = false;
			for(Iterator iter = formGridsToProblemCollectors.keySet().iterator(); iter.hasNext() && !issuedError;) {
				FormGrid nextFormGrid = (FormGrid) iter.next();
				FormGridProblemCollector problemCollector = (FormGridProblemCollector) formGridsToProblemCollectors.get(nextFormGrid);
				if(firstPass) {
					for(Iterator iter2 = problemCollector.fieldOutsideFormProblems.iterator(); iter2.hasNext();) {
						IFieldInfo nextProblem = (IFieldInfo) iter2.next();
						problemAcceptor.fieldOutsideForm(nextProblem);
						issuedError = true;
					}						
					firstPass = false;
				}
				if(!problemCollector.overlappingFieldProblems.isEmpty()) {
					for(Iterator iter2 = problemCollector.overlappingFieldProblems.iterator(); iter2.hasNext();) {
						FieldInfoPair nextProblem = (FieldInfoPair) iter2.next();
						problemAcceptor.fieldsOverlap(nextProblem.firstInfo, nextProblem.secondInfo);
						issuedError = true;
					}
				}
				if(!problemCollector.fieldExtendingOutsideFormProblems.isEmpty()) {
					for(Iterator iter2 = problemCollector.fieldExtendingOutsideFormProblems.iterator(); iter2.hasNext();) {
						IFieldInfo nextProblem = (IFieldInfo) iter2.next();
						problemAcceptor.fieldExtendsOutsideForm(nextProblem);
						issuedError = true;
					}
				}
				if(problemCollector.formSizeIsInvalid) {
					problemAcceptor.formSizeIsInvalid();
					issuedError = true;
				}
				if(problemCollector.formPositionInvalid) {
					problemAcceptor.formPositionIsInvalid();
					issuedError = true;
				}
				
				if(!issuedError) {
					screenSizesWhichFit.add(new IntAry((int[]) formGridsToScreenSizes.get(nextFormGrid)));
				}
			}
		}
		
		void addFloatingArea(FloatingAreaInfo floatingAreaInfo) {
		}
		
		void useDefaultFloatingArea() {
		}
	}
	
	private class FixedTextFormGridValidator extends TextFormGridValidator {
		protected int[] getFormPosition(int[] screenSize) {
			return formPosition;
		}
		
		protected FormGrid primAddScreenSize(int[] screenSize) {
			FormGrid newFormGrid = super.primAddScreenSize(screenSize);
			IFormGridProblemAcceptor problemAcceptor = (IFormGridProblemAcceptor) formGridsToProblemCollectors.get(newFormGrid);
			
			if(formSize[0] > screenSize[0] || formSize[1] > screenSize[1]) {
				problemAcceptor.formSizeIsInvalid();
			}
			else if(formPosition[0] > screenSize[0] || formPosition[1] > screenSize[1]) {
				problemAcceptor.formPositionIsInvalid();
			}
			else if(formPosition[0]+formSize[0]-1 > screenSize[0] ||
					formPosition[1]+formSize[1]-1 > screenSize[1]) {
				problemAcceptor.formSizeIsInvalid();
			}
			
			return newFormGrid;
		}
		
		void reportFormContextProblems() {
			reportProblems(new IFormGridProblemAcceptor() {
				public void fieldExtendsOutsideForm(IFieldInfo fieldInfo) {
					requestFieldExtendsOutsideFormProblem(fieldInfo);
				}
				public void fieldsOverlap(IFieldInfo fieldOnGrid, IFieldInfo fieldBeingAdded) {
					requestOverlappingFieldsProblem(fieldOnGrid, fieldBeingAdded);
				}
				public void fieldOutsideForm(IFieldInfo fieldInfo) {
					requestFieldOutsideFormProblem(fieldInfo);
				}
				public void formPositionIsInvalid() {
					problemRequestor.acceptProblem(
						getPositionNode(),
						IProblemRequestor.INVALID_FORM_POSITION_PROPERTY_VALUE,
						new String[] {IEGLConstants.PROPERTY_POSITION, canonicalFormName});
				}
				public void formSizeIsInvalid() {
					problemRequestor.acceptProblem(
						getFormSizeNode(),
						IProblemRequestor.INVALID_FORM_SIZE_PROPERTY_VALUE,
						new String[] {IEGLConstants.PROPERTY_FORMSIZE, canonicalFormName});
				}
			});
		}
		
		void reportFormGroupContextProblems() {
		}
	}
	
	private class FloatingTextFormGridValidator extends TextFormGridValidator {
		Map screenSizesToPositions = new HashMap();
		
		protected int[] getFormPosition(int[] screenSize) {
			int[] result = (int[]) screenSizesToPositions.get(screenSize);
			if(result == null) {
				result = new int[] {1,1};
			}
			return result;
		}
		
		protected FormGrid primAddScreenSize(int[] screenSize) {
			FormGrid newFormGrid = super.primAddScreenSize(screenSize);
			IFormGridProblemAcceptor problemAcceptor = (IFormGridProblemAcceptor) formGridsToProblemCollectors.get(newFormGrid);
			
			if(formSize[0] > screenSize[0] || formSize[1] > screenSize[1]) {
				problemAcceptor.formSizeIsInvalid();
			}
			
			return newFormGrid;
		}
		
		void addFloatingArea(FloatingAreaInfo floatingAreaInfo) {
			super.addFloatingArea(floatingAreaInfo);

			screenSizesToPositions.put(
				new IntAry(floatingAreaInfo.size),
				new int[] {floatingAreaInfo.topMargin+1,
						   floatingAreaInfo.leftMargin+1});
			
			if(!floatingAreaInfo.formOfSizeFits(formSize)) {
				for(Iterator iter = formGridsToProblemCollectors.keySet().iterator(); iter.hasNext();) {
					Object nextFormGrid = iter.next();
					int[] nextScreenSize = (int[]) formGridsToScreenSizes.get(nextFormGrid);
					if(nextScreenSize != null &&
					   nextScreenSize[0] == floatingAreaInfo.size[0] &&
					   nextScreenSize[1] == floatingAreaInfo.size[1]) {
						((IFormGridProblemAcceptor) formGridsToProblemCollectors.get(nextFormGrid)).formSizeIsInvalid();						
					}
				}
			}
		}
		
		void reportFormContextProblems() {			
			reportProblems(new IFormGridProblemAcceptor() {
				public void fieldExtendsOutsideForm(IFieldInfo fieldInfo) {
				}
				public void fieldsOverlap(IFieldInfo fieldOnGrid, IFieldInfo fieldBeingAdded) {
				}
				public void fieldOutsideForm(IFieldInfo fieldInfo) {
					requestFieldOutsideFormProblem(fieldInfo);
				}
				public void formPositionIsInvalid() {
				}
				public void formSizeIsInvalid() {
				}
			});
		}
		
		void reportFormGroupContextProblems() {
			reportProblems(new IFormGridProblemAcceptor() {
				public void fieldExtendsOutsideForm(IFieldInfo fieldInfo) {
					requestFieldExtendsOutsideFormProblem(fieldInfo);
				}
				public void fieldsOverlap(IFieldInfo fieldOnGrid, IFieldInfo fieldBeingAdded) {
					requestOverlappingFieldsProblem(fieldOnGrid, fieldBeingAdded);
				}
				public void fieldOutsideForm(IFieldInfo fieldInfo) {
				}
				public void formPositionIsInvalid() {
				}
				public void formSizeIsInvalid() {
					problemRequestor.acceptProblem(
						getFormSizeNode(),
						IProblemRequestor.INVALID_FORM_SIZE_PROPERTY_VALUE,
						new String[] {IEGLConstants.PROPERTY_FORMSIZE, canonicalFormName});
				}
			});
		}
	}
	
	private static class IntAry {
		int[] intAry;
		
		IntAry(int[] intAry) {
			this.intAry = intAry;
		}
		
		public boolean equals(Object obj) {
			if(obj instanceof IntAry) {
				IntAry other = (IntAry) obj;
				if(intAry.length == other.intAry.length) {
					for(int i = 0; i < intAry.length; i++) {
						if(intAry[i] != other.intAry[i]) {
							return false;
						}
					}
				}
				return true;
			}
			return false;
		}
		
		public int hashCode() {
			int result = 17;
			for(int i = 0; i < intAry.length; i++) {
				result = 37*result + intAry[i];
			}
			return result;
		}
	}
	
	protected IProblemRequestor problemRequestor;
	private FormBinding formBinding;
	private String canonicalFormName;
	private FormGroupBinding enclosingFormGroupBinding;
	//To be used as a starting point to find nodes when reporting errors
	private Node topLevelNode;
	private FormGridValidator formGridValidator;
	
	private boolean isTextForm;
	private boolean isPrintForm;
	private int[] formSize;
	private int[] formPosition;
	
	private IFieldInfo cursorField;
	private List validationOrders = new ArrayList();
    private ICompilerOptions compilerOptions;
	
	public FormValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this(problemRequestor, null, compilerOptions);
	}
	
	public FormValidator(IProblemRequestor problemRequestor, FormGroupBinding enclosingFormGroupBinding, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.enclosingFormGroupBinding = enclosingFormGroupBinding;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(TopLevelForm topLevelForm) {
		topLevelNode = topLevelForm;
		formBinding = (FormBinding) topLevelForm.getName().resolveBinding();
		canonicalFormName = topLevelForm.getName().getCanonicalName();
				
		if(formBinding != null) {
			new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(topLevelForm);
			
			initializeData();		
			if(!checkForm(topLevelForm.getName())) {
				//return false;
			}
		}
		return true;
	}
	
	public void endVisit(TopLevelForm topLevelForm) {
		if(formGridValidator != null) {
			formGridValidator.reportFormContextProblems();
		}
		issueFinalErrors(topLevelForm.getName());
	}
	
	public boolean visit(NestedForm nestedForm) {
		topLevelNode = nestedForm;
		formBinding = (FormBinding) nestedForm.getName().resolveBinding();
		canonicalFormName = nestedForm.getName().getCanonicalName();
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(nestedForm);
		
		initializeData();		
		if(!checkForm(nestedForm.getName())) {
			return false;
		}
		addFloatingAreas();		
		return true;
	}
	
	public void endVisit(NestedForm nestedForm) {
		if(formGridValidator != null) {
			formGridValidator.reportFormContextProblems();
			formGridValidator.reportFormGroupContextProblems();
		}
		issueFinalErrors(nestedForm.getName());
	}
	
	private void issueFinalErrors(Node formNameNode) {
		checkValidationOrderValues(formNameNode);
	}
	
	private void checkValidationOrderValues(Node formNameNode) {
		if(!validationOrders.isEmpty()) {
			Collections.sort(validationOrders);
			boolean isValid = true;
			boolean firstPass = true;
			int previousOrder = -1;
			for(Iterator iter = validationOrders.iterator(); iter.hasNext() && isValid;) {
				int currentOrder = ((Integer) iter.next()).intValue();
				if(firstPass) {
					isValid = currentOrder >= 1;
					firstPass = false;
				}
				else {
					isValid = currentOrder >= previousOrder+1;
				}
				previousOrder = currentOrder;
			}
			
			if(!isValid) {
				problemRequestor.acceptProblem(
					formNameNode,
					IProblemRequestor.INVALID_FORM_VALIDATIONORDER_PROPERTY_SPECIFICATION,
					new String[] {canonicalFormName});
			}
		}
	}
	
	/**
	 * Traverses use statements declared inside of form groups; making only those
	 * checks which can only be made from within the context of a form group. All
	 * problems reported beyond this entry point must be hung on the name node(s)
	 * of the use statement since there is no Form AST on which to hang them.
	 */
	public boolean visit(UseStatement useStatement) {
		for(Iterator iter = useStatement.getNames().iterator(); iter.hasNext();) {
			Name nextName = (Name) iter.next();
			canonicalFormName = nextName.getCanonicalName();
			IBinding binding = nextName.resolveBinding();
			if(binding != null && binding != IBinding.NOT_FOUND_BINDING &&
			   binding.isTypeBinding() && ITypeBinding.FORM_BINDING == ((ITypeBinding) binding).getKind()) {
				
				topLevelNode = nextName;
				formBinding = (FormBinding) binding;
				initializeData();				
				addFloatingAreas();
				
				if(formGridValidator != null) {
					for(Iterator iter2 = formBinding.getFields().iterator(); iter2.hasNext();) {
						IFieldInfo[] fieldInfos = createFieldInfos((FormFieldBinding) iter2.next(), nextName);
						for(int i = 0; i < fieldInfos.length; i++) {
							formGridValidator.applyFieldToGrid(fieldInfos[i]);
						}					
					}
				}
				
				if(formGridValidator != null) {
					formGridValidator.reportFormGroupContextProblems();
				}
			}
		}
		return false;
	}
	
	public boolean visit(VariableFormField variableFormField) {
		EGLNameValidator.validate(variableFormField.getName(), EGLNameValidator.IDENTIFIER, problemRequestor, compilerOptions);
		IFieldInfo[] fInfos = createFieldInfos(variableFormField);
		checkFields(fInfos);
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(variableFormField);
		
		if(fInfos.length != 0) {
			Integer validationOrder = fInfos[0].getValidationOrder();
			if(validationOrder != null) {
				validationOrders.add(validationOrder);
			}
			
			for (int i = 0; i < fInfos.length; i++) {
				if(fInfos[i].isCursor()) {
					if(cursorField == null) {
						cursorField = fInfos[i];
					}
					else {
						
						int sev;
						if (cursorField.isCursorOnSpecificIndex() || fInfos[i].isCursorOnSpecificIndex()) {
							sev = IMarker.SEVERITY_WARNING;			
							if (cursorField.isCursorOnSpecificIndex()) {
								cursorField = fInfos[i];
							}
						}
						else {
							sev = IMarker.SEVERITY_ERROR;			
						}
						
						problemRequestor.acceptProblem(
							fInfos[0].getNodeForErrors(),
							IProblemRequestor.INVALID_FORM_CURSOR_PROPERTY_SPECIFICATION,
							sev,
							new String[] {canonicalFormName});
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean visit(ConstantFormField constantFormField) {
		IFieldInfo fInfo = createFieldInfo(constantFormField);
		checkField(fInfo);
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(constantFormField);
		
		if(fInfo.isCursor()) {
			if(cursorField == null) {
				cursorField = fInfo;
			}
			else {
				
				int sev;
				if (cursorField.isCursorOnSpecificIndex()) {
					sev = IMarker.SEVERITY_WARNING;				}
				else {
					sev = IMarker.SEVERITY_ERROR;
				}
				problemRequestor.acceptProblem(
					fInfo.getNodeForErrors(),
					IProblemRequestor.INVALID_FORM_CURSOR_PROPERTY_SPECIFICATION,
					sev,
					new String[] {canonicalFormName});
			}
		}
		
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
	
	/**
	 * Should be invoked when the formBinding class field is not null.
	 */
	private void initializeData() {
		isPrintForm = formBinding.getAnnotation(EGLUITEXT, "PrintForm") != null;
		isTextForm = formBinding.getAnnotation(EGLUITEXT, "TextForm") != null;
		
		IAnnotationBinding aBinding = formBinding.getAnnotation(EGLUITEXT, "FormSize");
		if(aBinding != null && aBinding.getValue() instanceof Integer[]) {
			Integer[] formSizeValue = (Integer[]) aBinding.getValue();
			if(formSizeValue != null && formSizeValue.length == 2) {
				formSize = new int[] {formSizeValue[0].intValue(), formSizeValue[1].intValue()};
				if(formSize[0] < 1 || formSize[1] < 1) {
					formSize = null;
				}
			}
		}
		
		aBinding = formBinding.getAnnotation(EGLUI, "Position");
		if(aBinding != null) {
			Integer[] positionValue = (Integer[]) aBinding.getValue();
			if(positionValue != null && positionValue.length == 2) {
				formPosition = new int[] {positionValue[0].intValue(), positionValue[1].intValue()};
			}
		}
		
		if(formSize != null) {
			if(isTextForm) {
				if(formPosition == null) {
					formGridValidator = new FloatingTextFormGridValidator();
				}
				else {
					formGridValidator = new FixedTextFormGridValidator();
				}
				
				aBinding = getField(formBinding.getAnnotation(formBinding.getSubType()), IEGLConstants.PROPERTY_SCREENSIZES);
				if(aBinding != null) {
					Integer[][] screenSizesValue = (Integer[][]) aBinding.getValue();
					if(screenSizesValue != null) {
						for(int i = 0; i < screenSizesValue.length; i++) {
							if(screenSizesValue[i].length == 2) {
								int[] screenSize = new int[] {screenSizesValue[i][0].intValue(), screenSizesValue[i][1].intValue()};
								if(screenSize[0] > 0 && screenSize[1] > 0) {
									formGridValidator.addScreenSize(screenSize);
								}
							}
						}
					}
				}
			}
			else if(isPrintForm) {
				if(formPosition == null) {
					formGridValidator = new FloatingPrintFormGridValidator();
				}
				else {
					formGridValidator = new FixedPrintFormGridValidator();
				}
				formGridValidator.addScreenSize(new int[] {255, 158});
			}
		}
	}
	
	private void addFloatingAreas() {
		if(formGridValidator != null) {
			if(isPrintForm) {
				IAnnotationBinding aBinding = enclosingFormGroupBinding.getAnnotation(EGLUITEXT, "PrintFloatingAreas");
				if(aBinding != null && aBinding.getValue() instanceof IAnnotationBinding[]) {
					IAnnotationBinding[] printFloatingAreas = (IAnnotationBinding[]) aBinding.getValue();
					if(printFloatingAreas.length == 0) {
						formGridValidator.useDefaultFloatingArea();
					}
					else {
						for(int i = 0; i < printFloatingAreas.length; i++) {
							formGridValidator.addFloatingArea(createFloatingAreaInfo(printFloatingAreas[i], false));
						}
					}
				}
			}
			else if(isTextForm) {
				IAnnotationBinding aBinding = enclosingFormGroupBinding.getAnnotation(EGLUITEXT, "ScreenFloatingAreas");
				if(aBinding != null && aBinding.getValue() instanceof IAnnotationBinding[]) {
					IAnnotationBinding[] screenFloatingAreas = (IAnnotationBinding[]) aBinding.getValue();
					if(screenFloatingAreas.length == 0) {
						formGridValidator.useDefaultFloatingArea();
					}
					else {
						for(int i = 0; i < screenFloatingAreas.length; i++) {
							formGridValidator.addFloatingArea(createFloatingAreaInfo(screenFloatingAreas[i], true));
						}
					}
				}
			}
		}
	}
	
	private boolean checkForm(Name formNameNode) {
		EGLNameValidator.validate(formNameNode, EGLNameValidator.IDENTIFIER, problemRequestor, compilerOptions);
		
		if(formSize == null) {
			problemRequestor.acceptProblem(
				formNameNode,
				IProblemRequestor.INVALID_FORM_SIZE_PROPERTY_NOT_SPECIFIED,
				new String[] {canonicalFormName});
			return false;
		}
				
		return true;
	}
	
	private boolean checkFields(IFieldInfo[] fInfos) {
		boolean result = true;
		for(int i = 0; i < fInfos.length; i++) {
			if(!checkField(fInfos[i])) {
				result = false;
			}
		}
		
		return result;
	}
	
	private boolean checkField(IFieldInfo fInfo) {
		if(formGridValidator != null) {
			formGridValidator.applyFieldToGrid(fInfo);
		}
		return true;
	}
	
	private FloatingAreaInfo createFloatingAreaInfo(IAnnotationBinding floatingAreaBinding, boolean isScreenFloatingArea) {
		if(isScreenFloatingArea) {
			IAnnotationBinding aBinding = getField(floatingAreaBinding, IEGLConstants.PROPERTY_SCREENSIZE);
			if(aBinding != null) {
				Integer[] screenSizeValue = (Integer[]) aBinding.getValue();
				if(screenSizeValue != null && screenSizeValue.length == 2) {
					FloatingAreaInfo result = new FloatingAreaInfo(
						getIntValue(floatingAreaBinding, IEGLConstants.PROPERTY_LEFTMARGIN),
						getIntValue(floatingAreaBinding, IEGLConstants.PROPERTY_TOPMARGIN),
						new int[] {screenSizeValue[0].intValue(), screenSizeValue[1].intValue()});
					return result;
				}
			}
		}
		else {
			IAnnotationBinding aBinding = getField(floatingAreaBinding, IEGLConstants.PROPERTY_PAGESIZE);
			if(aBinding != null) {
				Integer[] pageSizeValue = (Integer[]) aBinding.getValue();
				if(pageSizeValue != null && pageSizeValue.length == 2) {
					FloatingAreaInfo result = new FloatingAreaInfo(
							getIntValue(floatingAreaBinding, IEGLConstants.PROPERTY_LEFTMARGIN),
							getIntValue(floatingAreaBinding, IEGLConstants.PROPERTY_TOPMARGIN),
						new int[] {pageSizeValue[0].intValue(), pageSizeValue[1].intValue()});
					aBinding = getField(floatingAreaBinding, IEGLConstants.PROPERTY_DEVICETYPE);
					if(aBinding != null && aBinding.getValue() instanceof IDataBinding && IEGLConstants.MNEMONIC_DOUBLEBYTE.equalsIgnoreCase(((IDataBinding) aBinding.getValue()).getName())) {
						result.isDoubleByte = true;
					}
					return result;
				}
			}
		}
		return null;
	}
	
	private int getIntValue(IAnnotationBinding annotationContainer, String fieldName) {
		IAnnotationBinding aBinding = getField(annotationContainer, fieldName);
		return ((Integer) aBinding.getValue()).intValue();
	}
	
	private Node getPositionNode() {
		return getAnnotationNode(InternUtil.intern("Position"));
	}
	
	private Node getFormSizeNode() {
		return getAnnotationNode(InternUtil.intern("FormSize"));
	}
	
	private Node getAnnotationNode(final String annotationName) {	
		final Node[] result = new Node[] {topLevelNode};
		topLevelNode.accept(new DefaultASTVisitor() {
			public boolean visit(NestedForm nestedForm) {
				return true;
			}
			public boolean visit(TopLevelForm topLevelForm) {
				return true;
			}
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			public boolean visit(Assignment assignment) {
				assignment.getLeftHandSide().accept(new DefaultASTVisitor() {
					public boolean visit(SimpleName simpleName) {
						if(simpleName.getIdentifier() == annotationName) {
							result[0] = simpleName;
						}
						return false;
					}
				});
				return false;
			}			
		});
		return result[0];
	}
	
	private IFieldInfo[] createFieldInfos(VariableFormField varFormField) {
		FormFieldBinding fBinding = (FormFieldBinding) varFormField.getName().resolveBinding();
		return fBinding == null ? new IFieldInfo[0] : createFieldInfos(fBinding, varFormField.getName());
	}
	
	private IFieldInfo[] createFieldInfos(FormFieldBinding fBinding, Node nodeForErrors) {
		if(fBinding.isMultiplyOccuring()) {
			int occurs = fBinding.getOccurs();
			IAnnotationBinding aBinding = fBinding.getAnnotation(EGLUI, "FieldLen");
			IFieldInfo[] result = new IFieldInfo[0];
			if(aBinding != null) {
				int fieldLength = ((Integer) aBinding.getValue()).intValue();
				result = new IFieldInfo[occurs];
				for(int i = 0; i < occurs; i++) {
					if(isTextForm) {
						result[i] = new VariableTextFormFieldInfo();
					}
					else {
						result[i] = new VariablePrintFormFieldInfo();
					}
					result[i].setNodeForErrors(nodeForErrors);
					result[i].setFieldLength(fieldLength);
					Integer[] positionValue = (Integer[]) fBinding.getAnnotation(EGLUI, "Position", i+1).getValue();
					result[i].setFieldPosition(new int[] {positionValue[0].intValue(), positionValue[1].intValue()});
					result[i].setCanonicalFieldName(fBinding.getName() + "[" + Integer.toString(i+1) + "]");
					result[i].setFieldBinding(fBinding);
					result[i].setIndex(i + 1);
				}
			}
			return result;
		}
		else {
			IFieldInfo result = null;
			if(isTextForm) {
				if(fBinding.isConstant()) {
					result = new ConstantTextFormFieldInfo();
				}
				else {
					result = new VariableTextFormFieldInfo();
				}
			}
			else {
				if(fBinding.isConstant()) {
					result = new ConstantPrintFormFieldInfo();
				}
				else {
					result = new VariablePrintFormFieldInfo();
				}
			}
			result.setNodeForErrors(nodeForErrors);
			IAnnotationBinding aBinding = fBinding.getAnnotation(EGLUI, "FieldLen");
			if(aBinding == null || aBinding.getValue() == null) {
				if(fBinding.isConstant()) {
					aBinding = fBinding.getAnnotation(EGLUI, "Value");
					if(aBinding == null) {
						result.setFieldLength(0);
					}
					else {
						Object valueValue = aBinding.getValue();
						if(valueValue instanceof String) {
							result.setFieldLength(lengthWithoutEscapeChars((String) valueValue));
						}
						else if(valueValue instanceof Integer) {
							result.setFieldLength(((Integer) valueValue).toString().length());
						}				
					}
				}
				else {
					return new IFieldInfo[0];
				}
			}
			else {
				try {
					result.setFieldLength(Integer.parseInt(aBinding.getValue().toString()));
				}
				catch (NumberFormatException e) {
					//TODO: issue error?
				}
			}
			result.setFieldPosition(getPosition(fBinding));
			result.setCanonicalFieldName(fBinding.getName());
			result.setFieldBinding(fBinding);
			
			return new IFieldInfo[] {result};
		}
	}
	
	private IFieldInfo createFieldInfo(ConstantFormField constFormField) {
		FormFieldBinding fBinding = constFormField.resolveBinding();
		return createFieldInfos(fBinding, constFormField)[0];
	}
	
	private int[] getPosition(IBinding binding) {	
		IAnnotationBinding aBinding = binding.getAnnotation(EGLUI, "Position");
		Integer[] positionValue = null;
		if(aBinding == null || !(aBinding.getValue() instanceof Integer[])) {
			positionValue = new Integer[] {new Integer(1), new Integer(1)};
		}
		else {
			positionValue = (Integer[]) aBinding.getValue();
			if(positionValue == null || positionValue.length != 2) {
				positionValue = new Integer[] {new Integer(1), new Integer(1)};
			}
		}
		return new int[] {positionValue[0].intValue(), positionValue[1].intValue()};
	}
	
	private static int lengthWithoutEscapeChars(String input) {
		String tempString = input;
		int len = input.length();
		if (tempString == "\\")
			return 1;
		// now account for escape characters if there are any
		int slashIndex = tempString.indexOf('\\');
		while (slashIndex != -1) {
			if (slashIndex == input.length()-1) {// slash is next to last character
				slashIndex = -1;
				len = len -1;
			}
			else {
				tempString = tempString.substring(slashIndex+2);
				slashIndex = tempString.indexOf('\\');
				len = len -1;
			}
		}
		return len;	
	}
	
	private static boolean hasDBCharField(FormBinding formBinding) {
		for(Iterator iter = formBinding.getFields().iterator(); iter.hasNext();) {
			ITypeBinding nextType = ((IDataBinding) iter.next()).getType();
			if(nextType != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == nextType.getKind()) {
				Primitive nextPrim = ((PrimitiveTypeBinding) nextType).getPrimitive();
				if(nextPrim == Primitive.DBCHAR) {
					return true;
				}
			}
		}
		return false;
	}
	
	private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
		IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
}
