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

package org.eclipse.edt.compiler.internal.core.validation.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.ReturnsDeclaration;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;







/**
 * @author cduval
 *
 */
public class PrimitiveTypeValidator {


	public PrimitiveTypeValidator() {
	}

 	static PrimitiveType passedPrimitiveType = null;
 	static int primitiveInt;
 	static String pattern;
 	static String ownerName = null;
 	static IProblemRequestor problemRequestor = null;
		
 	private static void initializeOwnerName (){
 		passedPrimitiveType.getParent().accept(new ParentAbstractASTVisitor());
 	}
 	
 	public static void validate(PrimitiveType passedType, IProblemRequestor pr,ICompilerOptions compilerOptions) {
		passedPrimitiveType = passedType;
		problemRequestor = pr;
		initializeOwnerName();
		primitiveInt = passedPrimitiveType.getPrimitive().getType();
		ITypeBinding binding =  passedPrimitiveType.resolveTypeBinding();
		pattern = null;
		if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			pattern = ((PrimitiveTypeBinding)binding).getTimeStampOrIntervalPattern();
		}

		if (  (primitiveInt == Primitive.SMALLINT_PRIMITIVE ||
			   primitiveInt == Primitive.INT_PRIMITIVE ||
			   primitiveInt == Primitive.BIGINT_PRIMITIVE ||
			   primitiveInt == Primitive.FLOAT_PRIMITIVE ||
			   primitiveInt == Primitive.SMALLFLOAT_PRIMITIVE ||
               primitiveInt == Primitive.NUMBER_PRIMITIVE ||
			   primitiveInt == Primitive.DATE_PRIMITIVE ||
			   primitiveInt == Primitive.TIME_PRIMITIVE ||			   
               primitiveInt == Primitive.ANY_PRIMITIVE)
			 && passedPrimitiveType.hasPrimLength() ) {
			problemRequestor.acceptProblem(passedPrimitiveType,
					IProblemRequestor.LENGTH_NOT_ALLOWED,
					new String[] {});
	
		}		

		
		//check and see if the primitive has logical chilren, which
		// changes some of the length validation rules
		if(!logicalChildrenCheckNeeded(passedPrimitiveType,primitiveInt, passedPrimitiveType.getPrimitive().getName().toUpperCase())) {
			//if a length is specified or should be specified then 
			// check that the length of the primitive used is valid
			checkLength(primitiveInt, passedPrimitiveType.getPrimitive().getName().toUpperCase(), passedType.hasPrimLength());
		}
		
		//to avoid returning too many error messages at once, validation 
		// proceeds as follows:
		if(checkDecimalSpecified(primitiveInt, passedPrimitiveType.getPrimitive().getName().toUpperCase())) {  //first check to make sure there are decimals
			if(checkDecimalRange()) {	  //then check to see if the range is valid
				checkDecimalLength();    //then check to see if the decimal length, when 
										// comapared to the primitive values length, is valid
			}
		}
	 		
	  checkTimestampOrIntervalPattern();
	  
	  if( primitiveInt == Primitive.INTERVAL_PRIMITIVE) {
		  if(pattern == null) {
			  problemRequestor.acceptProblem(
					  passedPrimitiveType,
					  IProblemRequestor.PRIMITIVE_TYPE_REQUIRES_DATETIME_PATTERN,
					  new String[] {ownerName, IEGLConstants.KEYWORD_INTERVAL});					 
		  }
	  }
	}
 	
	/**
	 * This method should -only- be called for Parameters in Functions.
	 *  This is where whatever is being validated is passed in.
	 * This is a specical version for parameters located in Functions.
	 * String parameters in Functions are not required to have a length, 
	 * @param passedPrimitive the EGLPrimitiveType that is being validated
	 */
	public static void validateParamInFunction(PrimitiveType passedType,IProblemRequestor pr,ICompilerOptions compilerOptions) {
		passedPrimitiveType = passedType;
		problemRequestor = pr;
		initializeOwnerName();
		primitiveInt = passedPrimitiveType.getPrimitive().getType();
		ITypeBinding binding =  passedPrimitiveType.resolveTypeBinding();
		pattern = null;
		if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			pattern = ((PrimitiveTypeBinding)binding).getTimeStampOrIntervalPattern();
		}
						
		if(!logicalChildrenCheckNeeded(passedPrimitiveType,primitiveInt, passedPrimitiveType.getPrimitive().getName().toUpperCase())) {
//			//this is unique to parameters in fuctions...
			if(isStringPrimitive(passedPrimitiveType)) {
				int primitiveLength = 0;
				if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
					primitiveLength = ((PrimitiveTypeBinding)binding).getLength();
				}
				//only validate the length if it != 0, as 0 is a valid length 
				// for String parameters in Functions
				if(primitiveLength != 0) {
					checkLength(primitiveInt, passedPrimitiveType.getPrimitive().getName().toUpperCase(), passedPrimitiveType.hasPrimLength());
				}
			}
			else {
				if (primitiveInt == Primitive.ANY_PRIMITIVE ||
                    primitiveInt == Primitive.NUMBER_PRIMITIVE) {
                    if (passedPrimitiveType.hasPrimLength()) {
                    	problemRequestor.acceptProblem(passedPrimitiveType,
                    			IProblemRequestor.LENGTH_NOT_ALLOWED ,
								new String[] {});
 
                    }
                }
                else 
                    checkLength(primitiveInt, passedPrimitiveType.getPrimitive().getName().toUpperCase(), passedPrimitiveType.hasPrimLength());
			}
		}
		
//		//to avoid returning too many error messages at once, validation 
//		// proceeds as follows:
		if(checkDecimalSpecified(primitiveInt, passedPrimitiveType.getPrimitive().getName().toUpperCase())) {  //first check to make sure there are decimals
			if(checkDecimalRange()) {	  //then check to see if the range is valid
				checkDecimalLength();    //then check to see if the decimal length, when 
										// comapared to the primitive values length, is valid
			}
		}
		
		checkTimestampOrIntervalPattern();
	}
	
	private static void checkTimestampOrIntervalPattern() {
		ITypeBinding binding =  passedPrimitiveType.resolveTypeBinding();
		String pattern = null;
		if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			pattern = ((PrimitiveTypeBinding)binding).getTimeStampOrIntervalPattern();
		}
		
		if( primitiveInt == Primitive.TIMESTAMP_PRIMITIVE ) {
		  	if( pattern != null ) {
		  		DateTimePattern dtPat = new DateTimePattern( pattern );
		  		if( !dtPat.isValidTimeStampPattern() ) {
		  			Integer[] errors = dtPat.getErrorMessageNumbers();
		  			for( int i = 0; i < errors.length; i++ ) {
		  				problemRequestor.acceptProblem(passedPrimitiveType,
		  						errors[i].intValue(),
								new String[] { pattern});
		  						
		  			}
		  		}
		  	}	  	 
		  }
		  
		  if( primitiveInt == Primitive.MONTHSPAN_INTERVAL_PRIMITIVE ||
			  primitiveInt == Primitive.SECONDSPAN_INTERVAL_PRIMITIVE) {
		  	if( pattern != null ) {
		  		DateTimePattern dtPat = new DateTimePattern( pattern );
		  		if( !dtPat.isValidIntervalPattern() ) {
		  			Integer[] errors = dtPat.getErrorMessageNumbers();
		  			for( int i = 0; i < errors.length; i++ ) {
		  				problemRequestor.acceptProblem(passedPrimitiveType,
		  						errors[i].intValue(),
								new String[] {pattern});
		  						
		  			}
		  		}
		  	}	  	 
		  }
	}
	
	protected static int getLogicalLengthInBytes(StructureItemBinding binding){
		int logicalLengthInBytes = 0;
		if (binding.getChildren().size() > 0){
			for (int i = 0; i < binding.getChildren().size();i++){
				StructureItemBinding childBinding = (StructureItemBinding)binding.getChildren().get(i);
				logicalLengthInBytes += getLogicalLengthInBytes(childBinding);
			}
		}else{
			logicalLengthInBytes = binding.getLengthInBytes();
		}
		return logicalLengthInBytes;
	}
/**
 * If there is no length defined for a Primitive, but it has logicalChildren
 * then the total length of its logicalChildren is the Primitives length.
 * If there are logicalChildren and no length is specified, then
 * validateLogicalChilrenLength is called, which is where the total
 * length calculated is validated against the PrimitiveType.
 * 
 * Before validateLogicalChildrenLength is called, the length to be validated
 *  is checked to ensure it is not an integer that is too large.
 * 
 * @param EGLPrimitiveType the EGLPrimitiveType being validated
 * @param int the integer corresponding to the PrimitiveType
 * @return true if there are logical children and no length is specified
 */		
	private static boolean logicalChildrenCheckNeeded(final PrimitiveType myPrimitive,final int primitiveInt,final String primitiveName) {
	   	BoolASTVisitor visitor = new BoolASTVisitor(){
			
  			
  			public boolean visit(StructureItem structureItem) {
  				if (StatementValidator.isValidBinding(structureItem.resolveBinding()) && structureItem.resolveBinding().isDataBinding() && 
  					((IDataBinding)structureItem.resolveBinding()).getKind() == IDataBinding.STRUCTURE_ITEM_BINDING){
	  				StructureItemBinding binding = (StructureItemBinding)structureItem.resolveBinding();
	  				if (StatementValidator.isValidBinding(binding)){
	  					if (binding.getChildren().size() > 0){
	  						if (!myPrimitive.hasPrimLength()){
	  							int logicalLengthInBytes = getLogicalLengthInBytes(binding);
	  							validateLogicalChildrenLength(primitiveInt, convertBytesToLength(primitiveInt, logicalLengthInBytes), primitiveName);
	  							retVal = true;
	  						}
	  					}
	  				}
  				}
 				return false;
 			}	  		
	  	};
	  	
		passedPrimitiveType.getParent().accept(visitor);
		return visitor.isValid();
	
	}	
	
	private static int convertBytesToLength(int primitiveInt, int lengthInBytes) {
		int lengthInDigits = 0;
		
		switch (primitiveInt) {
			case Primitive.BIN_PRIMITIVE:
				if(lengthInBytes == 2)
					lengthInDigits = 4;
				else if(lengthInBytes == 4)
					lengthInDigits = 9;
				else if(lengthInBytes == 8)
					lengthInDigits = 18;
				else
					lengthInDigits = -1;
				break;
			case Primitive.HEX_PRIMITIVE:
				lengthInDigits = lengthInBytes*2;
				break;
			case Primitive.UNICODE_PRIMITIVE:
			case Primitive.DBCHAR_PRIMITIVE:
				if(lengthInBytes < 2 || lengthInBytes % 2 != 0)
					lengthInDigits = -1;
				else
					lengthInDigits = lengthInBytes/2;
				break;
			case Primitive.DECIMAL_PRIMITIVE:
			case Primitive.PACF_PRIMITIVE:
			case Primitive.MONEY_PRIMITIVE:
				lengthInDigits = lengthInBytes*2 - 1;
				break;
			case Primitive.DATE_PRIMITIVE:
			case Primitive.TIME_PRIMITIVE:
			case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE:
			case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE:
			case Primitive.TIMESTAMP_PRIMITIVE:
			case Primitive.CHAR_PRIMITIVE:
			case Primitive.MBCHAR_PRIMITIVE:
			case Primitive.NUM_PRIMITIVE:
			case Primitive.NUMC_PRIMITIVE:
			default:
				lengthInDigits = lengthInBytes;
				break;
		}
		
		return lengthInDigits;
	}
	
/**
 * Validate the total length of the logical children, which is the
 * length of a primitive when it has logical children and no length is 
 * spefically specified. This is a seperate method from validateLength() so that
 * unique error messages may be issued.
 * 
 * @param int the integer corresponding to the PrimitiveType
 * @param int the integer corresponding to the logical length calculated
 */
	private static void validateLogicalChildrenLength(int primitiveInt,int primitiveLength,String primitiveName) throws ClassCastException {
		//In message 4404 below the first insert may be intentionally left blank if an error code is passed in for the primitiveLength
		
		switch (primitiveInt) {
			
			//bin can be 4,9, or 18
			case  Primitive.BIN_PRIMITIVE:
				if(primitiveLength != 4 && primitiveLength != 9 &&
				   primitiveLength != 18) {
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									IEGLConstants.BIN_STRING,                 // data type  
									BIN_RANGE }	);
				
				}
			break;
			
			//CHAR, MBCHAR must be between 1 and 32767
			case Primitive.CHAR_PRIMITIVE:
			case Primitive.MBCHAR_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > CHAR_MIX_MAX) {
					//CHAR/MBCHAR length error		
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									primitiveName,     // data type  
									CHAR_RANGE });
	
				}				
			break;

			
			//DBCHAR, UNICODE must be between 1 and 16383
			case Primitive.DBCHAR_PRIMITIVE:
			case Primitive.UNICODE_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > DBCHAR_UNICODE_MAX) {
					//DBCHAR/UNICODE length error
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN ,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									primitiveName,     // data type  
									DBCHAR_RANGE });
				}
			break;	
						
			//HEX must be an even number 2 to 65534		
			case  Primitive.HEX_PRIMITIVE:
				if(primitiveLength < HEX_MIN || primitiveLength > HEX_MAX 
					|| (primitiveLength%2 != 0)) {
					//HEX length error
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN ,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									IEGLConstants.HEX_STRING,            // data type  
									HEX_RANGE });
						
				}		
			break;
			
			//NUM and DECIMAL must be between 1 and 32
			case Primitive.NUM_PRIMITIVE:
			case Primitive.DECIMAL_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > DECIMAL_MAX) {
					//NUM/DECIMAL length error
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN ,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									primitiveName,     // data type  
									DECIMAL_RANGE });
	
				}
			break;

			//MONEY must be between 2 and 32
			case Primitive.MONEY_PRIMITIVE:
				if(primitiveLength < 2 || primitiveLength > DECIMAL_MAX) {
					//MONEY length error
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN ,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									primitiveName,     // data type  
									MONEY_RANGE });
				}
			break;
					
			//TIMESTAMP must be between 2 and 20
			case Primitive.TIMESTAMP_PRIMITIVE:
				if(primitiveLength < 2 || primitiveLength > TIMESTAMP_MAX) {
					//TIMESTAMP error
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN ,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									primitiveName,     // data type  
									TIMESTAMP_RANGE });
				}
			break;
					
			
			//INTERVAL must be between 3 and 28
			case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE:
			case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE:
				if(primitiveLength < 3 || primitiveLength > INTERVAL_MAX) {
					//INTERVAL length error
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN ,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									primitiveName,     // data type  
									INTERVAL_RANGE });
				}
			break;
							
			
			// NUMC, PACF must be between 1 and 18
			case Primitive.NUMC_PRIMITIVE:
			case Primitive.PACF_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > NUMERIC_MAX) {
					//NUMC/PACF length error
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_LOGICAL_CHILDREN ,
							new String[] { primitiveLength != -1 ? Integer.toString(primitiveLength) : "", //$NON-NLS-1$
									ownerName,
									primitiveName,     // data type  
									NUMC_RANGE });

				}
			break;

			//Float must be 8 
			case  Primitive.FLOAT_PRIMITIVE:
				if(passedPrimitiveType.hasPrimLength()){	
					if(primitiveLength != 8){ //FRIEDA check bytes verses digits
						//FLOAT length error
						problemRequestor.acceptProblem(passedPrimitiveType,
								IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_FIXED_LENGTH_WITH_LOGICAL_CHILDREN,
								new String[] { Integer.toString(primitiveLength),   // length
								ownerName,                  
								 IEGLConstants.FLOAT_STRING,            // data type  
								FLOAT_RANGE });
						
					}
				}
			break;
						
			//SmallFloat must be 8 
			case  Primitive.SMALLFLOAT_PRIMITIVE:
				if(passedPrimitiveType.hasPrimLength()){	
					if(primitiveLength != 4){ //FRIEDA check bytes verses digits
						//SmallFLOAT length error
						problemRequestor.acceptProblem(passedPrimitiveType,
								IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_FIXED_LENGTH_WITH_LOGICAL_CHILDREN,
								new String[] { Integer.toString(primitiveLength),   // length
								ownerName,                  
								 IEGLConstants.SMALLFLOAT_STRING,            // data type  
								SMALLFLOAT_RANGE });
					}
				}
			break;
						
						
			//INT must be 9 
			case  Primitive.INT_PRIMITIVE:
				if(passedPrimitiveType.hasPrimLength()){	
					if(primitiveLength != 9){
						//INT length error
						problemRequestor.acceptProblem(passedPrimitiveType,
								IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_FIXED_LENGTH_WITH_LOGICAL_CHILDREN,
								new String[] { Integer.toString(primitiveLength),   // length
								ownerName,                  
								 IEGLConstants.INT_STRING,            // data type  
								INT_RANGE });
						
					}
				}
			break;
			
			//BIGINT must be 18
			case  Primitive.BIGINT_PRIMITIVE:
				if(passedPrimitiveType.hasPrimLength()){	
					if(primitiveLength != 18 ){
						//BIGINT length error
						problemRequestor.acceptProblem(passedPrimitiveType,
								IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_FIXED_LENGTH_WITH_LOGICAL_CHILDREN,
								new String[] { Integer.toString(primitiveLength),   // length
								ownerName,                          
							IEGLConstants.BIGINT_STRING,         // data type  
							BIGINT_RANGE });
	
					}	
				}	
			break;
						
			//SMALLINT must be 4
			case Primitive.SMALLINT_PRIMITIVE:
		 	if(passedPrimitiveType.hasPrimLength()){	
				if(primitiveLength != 4) {
					//SMALLINT length error
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_CALCULATED_LENGTH_FOR_PRIMITIVE_TYPE_WITH_FIXED_LENGTH_WITH_LOGICAL_CHILDREN,
							new String[] { Integer.toString(primitiveLength),   // length
							ownerName,
							IEGLConstants.SMALLINT_STRING,            // data type  
							SMALLINT_RANGE });
	
				}
		 	}
			break;
		}	
	}
	

/**
 * Check and ensure the primitive type is of a valid length.
 * 
 * Integers that are out of range (too large) also produce error messages.
 * 
 * @param primitiveInt the integer value that maps to the Primitive Type
 * @return true if length is valid, else return false
 */		
	public static void checkLength(int primitiveInt, String primitiveName, boolean lengthDefined) {
		//get the int corresponding to the primitive type
		//int primitiveInt = myPrimitive.getPrimitive().getType();
		//get the length of the primitive
		try {
			int primitiveLength = 0;
			if(lengthDefined) {
				ITypeBinding binding =  passedPrimitiveType.resolveTypeBinding();
				if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
					primitiveLength = ((PrimitiveTypeBinding)binding).getLength();
				}
			}
		
  		  switch (primitiveInt) {
			
			//bin can be 4,9, or 18
			case  Primitive.BIN_PRIMITIVE:
				if(primitiveLength != 4 && primitiveLength != 9 &&
				   primitiveLength != 18) {					
					//BIN length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, BIN_RANGE, lengthDefined);					
				}
			break;
			
			//CHAR, MBCHAR must be between 1 and 32767
			case Primitive.CHAR_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > CHAR_MIX_MAX) {
					//CHAR length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, CHAR_RANGE, lengthDefined);					
				}
			break;
			case Primitive.MBCHAR_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > CHAR_MIX_MAX) {											
                    //MBCHAR length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, CHAR_RANGE, lengthDefined);
				}				
			break;

			
			//DBCHAR, UNICODE must be between 1 and 16383
			case Primitive.DBCHAR_PRIMITIVE:
			case Primitive.UNICODE_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > DBCHAR_UNICODE_MAX) {
					//DBCHAR/UNICODE length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, DBCHAR_RANGE, lengthDefined);
				}
			break;	
						
			//HEX must be an even number 2 to 65534		
			case  Primitive.HEX_PRIMITIVE:
				if(primitiveLength < HEX_MIN || primitiveLength > HEX_MAX
					|| (primitiveLength%2 != 0)) {
					//HEX length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, HEX_RANGE, lengthDefined);
				}		
			break;
			
			// NUMC, PACKFmust be between 1 and 18
			case Primitive.NUMC_PRIMITIVE:
			case Primitive.PACF_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > NUMERIC_MAX) {
					//NUM/NUMC/PACF/DECIMAL length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, NUMC_RANGE, lengthDefined);
				}
			break;
			
			//NUM must be between 1 and 32
			case Primitive.NUM_PRIMITIVE:
				if(primitiveLength < 1 || primitiveLength > DECIMAL_MAX) {
					//NUM/DECIMAL length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, DECIMAL_RANGE, lengthDefined);
				}
			break;
			//DECIMAL must be between 0 and 32
			case Primitive.DECIMAL_PRIMITIVE:
				if(primitiveLength < 0 || primitiveLength > DECIMAL_MAX) {
					//NUM/DECIMAL length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, DECIMAL_RANGE, lengthDefined);
				}
			break;

			//MONEY, if specified, must be between 2 and 32
			case Primitive.MONEY_PRIMITIVE:
				if(primitiveLength != 0 &&
					(primitiveLength < 2 || primitiveLength > DECIMAL_MAX)) {
					//MONEY length error
					issueWrongOrMissingLengthError(primitiveLength, primitiveName, MONEY_RANGE, lengthDefined);
				}
			break;
						
			//CLOB and BLOB must be between 1 and 2 gig
			case Primitive.CLOB_PRIMITIVE:
			case Primitive.BLOB_PRIMITIVE:

			break;
							 
			case Primitive.INT_PRIMITIVE:  //INT must be 9
			case Primitive.BIGINT_PRIMITIVE:	//BIGINT must be 18
			case Primitive.SMALLINT_PRIMITIVE:	//SMALLINT must be 4
			case Primitive.FLOAT_PRIMITIVE:  //FLOAT must be 8
			case Primitive.SMALLFLOAT_PRIMITIVE:	//SMALLFLOAT must be 4
			case Primitive.DATE_PRIMITIVE:  //DATE must be 8
			case Primitive.TIME_PRIMITIVE:  //TIME must be 6
			// no edit because there is already an edit that prevents a length  
			break;
		  }//close switch statement
		}//close try 
		
		//this same error is caught in checkDecimalLength, so the error
		// message is produced there and merely caught here.
		catch(NumberFormatException exception) {} 
	}
	

	private static void issueWrongOrMissingLengthError(int primitiveLength, String typeString, String rangeString, boolean lengthDefined) {
		if(lengthDefined) {
			problemRequestor.acceptProblem(
				passedPrimitiveType,
				IProblemRequestor.INVALID_LENGTH_FOR_PRIMITIVE_TYPE,
				new String[] {
					Integer.toString(primitiveLength),
					ownerName,
					typeString,  
					rangeString
				});
		}
		else {
			problemRequestor.acceptProblem(
				passedPrimitiveType,
				IProblemRequestor.MISSING_LENGTH_FOR_PRIMITIVE_TYPE,
				new String[] {
					typeString
				});
		}
	}

/**
 * First Decimal Check: 
 * 1. Decimals must not be specified for non-numeric primitive types. 
 *    If a decimal is specified as 0, do not produce an error message.
 *  Numeric primitivetypes are BIN, NUM, NUMC, PACK, and PACF.
 * 	@param primitiveInt the integer value that maps to the Primitive Type
 *  @return true if decimals specified for a valid type or if they were not
 * 			specifed, else false
 */

	public static boolean checkDecimalSpecified(int primitiveInt, String primitiveName) { 
		ITypeBinding binding =  passedPrimitiveType.resolveTypeBinding();
		if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			if (passedPrimitiveType.hasPrimDecimals() && ((PrimitiveTypeBinding)binding).getDecimals() != 0){
				//if there are decimals, it must be BIN, NUM, NUMC, DECIMAL (PACK),
				// or PACF	or Money		
				if(primitiveInt != Primitive.BIN_PRIMITIVE && 
				   primitiveInt != Primitive.DECIMAL_PRIMITIVE && 
				   primitiveInt != Primitive.NUM_PRIMITIVE &&
				   primitiveInt != Primitive.NUMC_PRIMITIVE &&
				   primitiveInt != Primitive.PACF_PRIMITIVE &&
				   primitiveInt != Primitive.MONEY_PRIMITIVE) {
						problemRequestor.acceptProblem(passedPrimitiveType,
								IProblemRequestor.NONNUMERIC_WITH_DECIMALS,
								new String [] {
								   ownerName,	
								   primitiveName});

				
					return false; //decimals were specified, but for an incorrect data type
					}
				return true; //decimals were specified for a valid data type

			}else {
				return true; //no decimals specified
			}
		}
		
		return true;
	}
	
/**
 * Second Decimal Check: 
 * 2. If length was not specified or specified incorrectly (ie not a number), the 
 * 	  decimals must be a number in the 0 to 18 range.
 *  This method is only run if checkDecimalsSpecified returns true, so the 
 *  primitive should have decimals if it reaches this point.
 * 
 * @return true if the decimal range is valid, false if otherwise
 */
	public static boolean checkDecimalRange() {
		ITypeBinding binding =  passedPrimitiveType.resolveTypeBinding();
		if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			int decimals = ((PrimitiveTypeBinding)binding).getDecimals();
			int primitiveInt = passedPrimitiveType.getPrimitive().getType();
			
			//if here then decimals were specified correctly, now check the range		
			
			//max decimals is 18
			if ( primitiveInt == Primitive.BIN_PRIMITIVE ||
				 primitiveInt == Primitive.NUMC_PRIMITIVE ||
				 primitiveInt == Primitive.PACF_PRIMITIVE ) {
				if(decimals < 0 || decimals > 18) {
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_DECIMALS,
							new String [] {
						 	String.valueOf(decimals),
						 	ownerName,
							NUMC_RANGE
						});
				return false;
				}
			}

			if ( primitiveInt == Primitive.DECIMAL_PRIMITIVE || 
				 primitiveInt == Primitive.NUM_PRIMITIVE ||
				 primitiveInt == Primitive.MONEY_PRIMITIVE) {		
				if(decimals < 0 || decimals > 32) {
					problemRequestor.acceptProblem(passedPrimitiveType,
							IProblemRequestor.INVALID_DECIMALS,
							new String [] {
						 	String.valueOf(decimals),
						 	ownerName,
							DECIMAL_RANGE
						});
			
				return false;
				}
			}
			return true;
		}
		return true;
	}
	
/**
* Third Decimal Check: 
* 3. Decimals must be less than or equal to the length, as long as
*    length is greater than 0.
* 
* Greater than maximum value integers errors are caught here. 
*/

	public static void checkDecimalLength() {
		try {
			ITypeBinding binding =  passedPrimitiveType.resolveTypeBinding();
			if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING ){
				int length = ((PrimitiveTypeBinding)binding).getLength();
				int decimals = ((PrimitiveTypeBinding)binding).getDecimals();			
			
			  if (length > 0 && (decimals > length) ) {
			  	problemRequestor.acceptProblem(passedPrimitiveType,
			  			IProblemRequestor.DECIMALS_GREATER_THAN_LENGTH,
						new String [] {
						   String.valueOf(decimals),
						   ownerName,
						   String.valueOf(length)});

			  }
			}
		}
		//this message and the other one like it in check length appear twice, 
		//but if one is removed a error is thrown,
		//so for now this catch will be the only one producing an error message.
		catch(NumberFormatException exception) {
			problemRequestor.acceptProblem(passedPrimitiveType,
					IProblemRequestor.INTEGER_TOO_LARGE,
					new String[] { 
					exception.getLocalizedMessage(),
					String.valueOf(Integer.MAX_VALUE)
				});
			
		}
		 
		//else it follows the rules
	}
 /**
  * Return true if the EGLPrimitiveType is a numeric primitive,
  * as in it holds digits, else false.
  * @param EGLPrimitiveType
  * @return true if numeric, else false.
  */
 public static boolean isNumericPrimitive(PrimitiveType myPrimitiveType) {
 	Primitive type =  myPrimitiveType.getPrimitive();
 	return Primitive.isNumericType(type);
 }
 
 /**
  * Return true if the EGLPrimitiveType is a String primitive,
  * as in it does not exclusively hold digits, else false.
  * @param EGLPrimitiveType
  * @return true if a String primitive, else false.
  */ 
 public static boolean isStringPrimitive(PrimitiveType myPrimitiveType) {
 	Primitive type =  myPrimitiveType.getPrimitive();
	return Primitive.isStringType(type);
 }
 
 /**
  * Return true if the EGLPrimitiveType is a Date/Time primitive,
  * as in it does not exclusively hold digits, else false.
  * @param EGLPrimitiveType
  * @return true if a String primitive, else false.
  */ 
 public static boolean isDateTimePrimitive(PrimitiveType myPrimitiveType) {
	Primitive type =  myPrimitiveType.getPrimitive();
	return Primitive.isDateTimeType(type);
 }
 
 /**
  * Return true if the EGLPrimitiveType is a String primitive,
  * as in it does not exclusively hold digits, else false.
  * @param EGLPrimitiveType
  * @return true if a String primitive, else false.
  */ 
 public static boolean isNonHexStringPrimitive(PrimitiveType myPrimitiveType) {
	int type =  myPrimitiveType.getPrimitive().getType();
//	
	switch(type) {
		case Primitive.CHAR_PRIMITIVE:
		case Primitive.DBCHAR_PRIMITIVE:
		case Primitive.MBCHAR_PRIMITIVE:
		case Primitive.UNICODE_PRIMITIVE:
			return true;		
	}	
	return false;
 } 
/**
 * Check if it is valid to use decimals in the given EGLPrimitiveType. 
 * @param EGLPrimitiveType
 * @return true if decimal use is valid, else false.
 */
 public static boolean isDecimalAllowed(PrimitiveType myPrimitiveType) {
	 //EGLPrimitives that allow decimals
	 int primitive = myPrimitiveType.getPrimitive().getType(); 
	 if(primitive == Primitive.BIN_PRIMITIVE ||
		primitive == Primitive.DECIMAL_PRIMITIVE||
		primitive == Primitive.NUM_PRIMITIVE||
		primitive == Primitive.NUMC_PRIMITIVE||
		primitive == Primitive.PACF_PRIMITIVE||
		primitive == Primitive.FLOAT_PRIMITIVE||
		primitive == Primitive.SMALLFLOAT_PRIMITIVE||
		primitive == Primitive.MONEY_PRIMITIVE
	 ) {
			
		 return true;   	
	 }		
		
	 return false;
 }

 
 
	public static class DateTimePattern {
		private String[] components = null;
		private boolean isValidPattern = true;
		private Set errorMessageNumbers = new TreeSet();
		String text;
		
		static final List VALID_CHARS = new ArrayList( Arrays.asList( new Character[] {				
			new Character( 'y' ),
			new Character( 'M' ),
			new Character( 'd' ),
			new Character( 'h' ),
			new Character( 'm' ),
			new Character( 's' ),
			new Character( 'f' ),
		} ) );
		
		private static Map maxOccurences = new HashMap();
		static {
			maxOccurences.put( new Character( 'y' ), new Integer( 4 ) );
			maxOccurences.put( new Character( 'M' ), new Integer( 2 ) );
			maxOccurences.put( new Character( 'd' ), new Integer( 2 ) );
			maxOccurences.put( new Character( 'h' ), new Integer( 2 ) );
			maxOccurences.put( new Character( 'm' ), new Integer( 2 ) );
			maxOccurences.put( new Character( 's' ), new Integer( 2 ) );
			maxOccurences.put( new Character( 'f' ), new Integer( 6 ) );
		}
		
		public DateTimePattern( String atext ) {
			this.text = atext;
			if (this.text.startsWith("\"") &&
				this.text.endsWith("\"")){
				this.text = atext.substring(1,atext.length()-1);
			}
			if( text.length() == 0 ) {
				isValidPattern = false;
				errorMessageNumbers.add( new Integer(IProblemRequestor.DATETIME_PATTERN_EMPTY ));
				return;
			}
			
			StringBuffer component = new StringBuffer();
			List componentsList = new ArrayList();
			char currentChar = text.charAt( 0 );
			for( int i = 0; i < text.length() && isValidPattern; i++ ) {
				char ch = text.charAt( i );
				if( !VALID_CHARS.contains( new Character( Character.toLowerCase( ch ) ) ) ) {
					isValidPattern = false;
					errorMessageNumbers.add(  new Integer(IProblemRequestor.DATETIME_PATTERN_HAS_INVALID_CHARACTER) );
				}
				else {
					if( Character.toLowerCase(currentChar) != Character.toLowerCase(ch) ) {
						componentsList.add( component.toString() );
						component = new StringBuffer();
						currentChar = ch;
					}
					component.append( ch );
				}
			}
			if( component.toString().length() != 0 ) {
				componentsList.add( component.toString() );
			}
			components = (String[]) componentsList.toArray( new String[0] );
		}
		
		private void checkOrder() {
			char currentChar = components[0].charAt( 0 );
			if( currentChar == 'm' && components.length > 1 && Character.toLowerCase( components[1].charAt(0) ) == 'd' ) {
				currentChar = 'M';
			}
			int currentIndex = VALID_CHARS.indexOf( new Character( currentChar == 'M' ? currentChar : Character.toLowerCase( currentChar ) ) );
			
			for( int i = 1; i < components.length && isValidPattern; i++ ) {
				char nextChar = components[i].charAt( 0 );
				if( nextChar == 'm' ) {
					if( Character.toLowerCase( currentChar ) == 'y' ) {
						nextChar = 'M';
					}
				}
				int nextIndex = VALID_CHARS.indexOf( new Character( nextChar == 'M' ? nextChar : Character.toLowerCase( nextChar ) ) );
				if( nextChar != currentChar ) {
					boolean nextCharValid = Character.toLowerCase( currentChar ) == 'y' ?
						Character.toLowerCase( nextChar ) == 'm' :
						Character.toLowerCase(nextChar) == Character.toLowerCase(((Character) VALID_CHARS.get( currentIndex + 1 )).charValue());
					
					if( !nextCharValid ) {
						isValidPattern = false;
						errorMessageNumbers.add( nextIndex < currentIndex ?
								 new Integer(IProblemRequestor.DATETIME_PATTERN_OUT_OF_ORDER) :
								 	 new Integer(IProblemRequestor.DATETIME_PATTERN_MISSING_INTERMEDIATE_FIELD) );
					}
				}
				currentChar = nextChar;
				currentIndex = nextIndex;
			}
		}
		
		private void checkOccurences( int firstFieldAllowedLength ) {
			int[] occurenceMessages = new int[] {
				IProblemRequestor.DATETIME_PATTERN_YEAR_FIELD_TOO_LONG,
				IProblemRequestor.DATETIME_PATTERN_MONTH_FIELD_TOO_LONG,
				IProblemRequestor.DATETIME_PATTERN_DAY_FIELD_TOO_LONG,
				IProblemRequestor.DATETIME_PATTERN_HOUR_FIELD_TOO_LONG,
				IProblemRequestor.DATETIME_PATTERN_MINUTES_FIELD_TOO_LONG,
				IProblemRequestor.DATETIME_PATTERN_SECONDS_FIELD_TOO_LONG,
				IProblemRequestor.DATETIME_PATTERN_SECOND_FRACTIONS_FIELD_TOO_LONG };
			
			for( int i = 0; i < components.length; i++ ) {
				char currentChar = components[i].charAt( 0 );
				boolean isValid = false;
				if(i == 0 && firstFieldAllowedLength != -1) {
					isValid = components[i].length() <= firstFieldAllowedLength;
				}
				else {
					if(Character.toLowerCase(currentChar) == 'f') {
						isValid = components[i].length() <= ((Integer) maxOccurences.get( new Character( Character.toLowerCase( currentChar ) ) )).intValue(); 
					}
					else {
						isValid = components[i].length() == ((Integer) maxOccurences.get( new Character( currentChar == 'M' ? currentChar : Character.toLowerCase( currentChar )  ) )).intValue();
					}
				}
				
				if(!isValid) {
					isValidPattern = false;
					char lookupChar;
					if(currentChar == 'M') {
						lookupChar = currentChar;
					}
					else {
						if(currentChar == 'm' && isMonthSpanInterval()) {
							lookupChar = 'M';
						}
						else {
							lookupChar = Character.toLowerCase(currentChar);
						}
					}
					if(i == 0 && firstFieldAllowedLength != -1) {
						errorMessageNumbers.add( new Integer(IProblemRequestor.DATETIME_PATTERN_FIRST_INTERVAL_FIELD_TOO_LONG));						
					}
					else {
						errorMessageNumbers.add( new Integer(occurenceMessages[VALID_CHARS.indexOf( new Character( lookupChar ) )] ));
					}
				}
			}
		}
		
		private void checkIntervalSpan() {
			boolean spanValid = true;
			char firstChar = Character.toLowerCase( components[0].charAt( 0 ) );
			char lastChar = Character.toLowerCase( components[ components.length-1 ].charAt( 0 ) );
			if( firstChar == 'y' ) {
				spanValid = lastChar == 'm' ? components.length == 2 : lastChar == 'y';
			}
			else if( firstChar == 'm' ) {
				spanValid = components.length == 1 ? true : components[1].charAt( 0 ) != 'd';
			}
			if( !spanValid ) {
				isValidPattern = false;
				errorMessageNumbers.add( new Integer(IProblemRequestor.DATETIME_PATTERN_INVALID_INTERVAL_SPAN ));
			}
		}
		
		public boolean isValidTimeStampPattern() {
			if( !isValidPattern ) return false;
			
			checkOrder();
			if( !isValidPattern ) return false;			
			
			checkOccurences( -1 );
			if( !isValidPattern ) return false;
			
			return true;
		}
		
		public boolean isValidIntervalPattern() {
			if( !isValidPattern ) return false;
			
			checkOrder();
			if( !isValidPattern ) return false;			
			
			checkOccurences( 9 );
			if( !isValidPattern ) return false;
			
			checkIntervalSpan();
			if( !isValidPattern ) return false;
			
			return true;
		}
		
		public Integer[] getErrorMessageNumbers() {
			return (Integer[]) errorMessageNumbers.toArray( new Integer[0] );
		}
		
		public String[] getComponents() {
			return components;
		}
		
		public int length() {
			int len = 0;
			for( int i = 0; i < components.length; i++ ) {
				len += components[ i ].length();
			}
			return len;
		}
		
		public boolean isMonthSpanInterval() {
			char firstChar = components[0].charAt( 0 );
			return Character.toLowerCase(firstChar) == 'y' ||
			       components.length == 1 && 'M' == firstChar;
		}
		
		public boolean isSecondSpanInterval() {
			return !isMonthSpanInterval();
		}
	}
		

 		public final static String BIN_RANGE = "(4, 9, 18)"; //$NON-NLS-1$
 		public final static String CHAR_RANGE = "(1..32767)"; //$NON-NLS-1$
 		public final static String DBCHAR_RANGE = "(1..16383)"; //$NON-NLS-1$
 		public final static String HEX_RANGE = "(2, 4,...,65534)"; //$NON-NLS-1$
 		public final static String MBCHAR_RANGE = "(1..32767)"; //$NON-NLS-1$
 		public final static String NUM_RANGE = "(1..32)"; //$NON-NLS-1$
 		public final static String NUMC_RANGE = "(1..18)"; //$NON-NLS-1$
 		public final static String PACF_RANGE = "(1, 3,...,17;18)"; //$NON-NLS-1$
 		public final static String DECIMAL_RANGE = "(1..32)"; //$NON-NLS-1$
 		public final static String UNICODE_RANGE = "(1..16383)"; //$NON-NLS-1$
 		public final static String MONEY_RANGE = "(2..32)"; //$NON-NLS-1$
 		public final static String TIMESTAMP_RANGE = "(2..20)"; //$NON-NLS-1$
 		public final static String INTERVAL_RANGE = "(3..28)"; //$NON-NLS-1$	
 		public final static String CLOB_RANGE = "(1..2 gigabytes)"; //$NON-NLS-1$ FRIEDA
 		public final static String BLOB_RANGE = "(1..2 gigabytes)"; //$NON-NLS-1$ FRIEDA	
 		
 		public final static String DATE_RANGE = "(8)"; //$NON-NLS-1$
 		public final static String TIME_RANGE = "(6)"; //$NON-NLS-1$		
 		public final static String FLOAT_RANGE = "(8)"; //$NON-NLS-1$
 		public final static String SMALLFLOAT_RANGE = "(4)"; //$NON-NLS-1$	
 		public final static String INT_RANGE = "(9)"; //$NON-NLS-1$
 		public final static String BIGINT_RANGE = "(18)"; //$NON-NLS-1$
 		public final static String SMALLINT_RANGE = "(4)"; //$NON-NLS-1$

 		public final static String[] LENGTH_RANGES =  //FRIEDA Order important????????
 			{ BIN_RANGE, CHAR_RANGE, DBCHAR_RANGE, HEX_RANGE, MBCHAR_RANGE, NUM_RANGE, NUMC_RANGE, PACF_RANGE, DECIMAL_RANGE, UNICODE_RANGE, INT_RANGE, BIGINT_RANGE, SMALLINT_RANGE };

 		public final static int NUMERIC_MAX = 18;
 		public final static int TIMESTAMP_MAX = 20;
 		public final static int INTERVAL_MAX = 28;
 		public final static int DECIMAL_MAX = 32;
 		public final static int CHAR_MIX_MAX = 32767;
 		public final static int DBCHAR_UNICODE_MAX = 16383;
 		public final static int HEX_MAX = 65534;
 		public final static int BLOB_CLOB_MAX = 2147483647; //technically should be 2,147,483,648
 		 						                            // but Java won't let me!

 		public final static int PRIMITIVE_TYPE_MIN = 1;
 		public final static int BIN_MIN = 4;
 		public final static int HEX_MIN = 2;

 		public final static int SQL_ITEM_BIN_MIN = 4;
 		public final static int SQL_ITEM_BIN_MAX = 9;
 		public final static String SQL_ITEM_BIN_RANGE = "(4, 9)"; //$NON-NLS-1$

		private static class BoolASTVisitor extends AbstractASTVisitor{
			boolean retVal = false;
			
			public BoolASTVisitor (){
			}
			public boolean isValid(){
				return retVal;
			}

		}		
		
		private static 	class ParentAbstractASTVisitor extends DefaultASTVisitor {
 			public boolean visit(ClassDataDeclaration classDataDeclaration) {
 				if (classDataDeclaration.getNames().size() > 0){
 					ownerName = ((Expression)classDataDeclaration.getNames().get(0)).getCanonicalString();
 				}
 				return false;
 			}
 			
 			public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
				if (functionDataDeclaration.getNames().size() > 0){
 					ownerName = ((Expression)functionDataDeclaration.getNames().get(0)).getCanonicalString();
 				}
 				return false;
 			}
 			
 			public boolean visit(FunctionParameter functionParameter) {
 				ownerName = functionParameter.getName().getCanonicalName();
 				return false;	
 			}
 			
 			public boolean visit(ProgramParameter programParameter) {
 				ownerName = programParameter.getName().getCanonicalName();
 				return false;
 			}
 			
 			public boolean visit(StructureItem structureItem) {
 				if (structureItem.isEmbedded()){
 					ownerName = IEGLConstants.KEYWORD_EMBED;
 				}else if (structureItem.isFiller()){
 					ownerName = "*";
 				}else {
 					ownerName = structureItem.getName().getCanonicalName();
 				}
 				return false;
 			}	
 			
 			public boolean visit(ReturnsDeclaration returnStatement) {
 				ownerName = 	IEGLConstants.KEYWORD_RETURN;
 				return false;
 			}	
 			
 			public boolean visit(ArrayType arrayType) {
 				arrayType.getParent().accept(new ParentAbstractASTVisitor());
 				return false;
 			}
 			
 			public boolean visit(IsAExpression isAExpression) {
 				ownerName = passedPrimitiveType.getCanonicalName();
				return false;
			}
 			
 			public boolean visit(AsExpression asExpression) {
 				ownerName = passedPrimitiveType.getCanonicalName();
 				return false;
 			}
 			
 			public boolean visit(org.eclipse.edt.compiler.core.ast.DataItem dataItem) {
 				ownerName = dataItem.getName().getCanonicalName();
 				return false;
 			}
 			
 			};	

 	 			
}
