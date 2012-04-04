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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.AmbiguousDataBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.OverloadedFunctionSet;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;


public class FunctionResolver {

	protected ICompilerOptions compilerOptions;

    private static interface IArgumentCompatibilityRules {
    	int getScoreForInOrOutParameter(int argNum, ITypeBinding argumentType, ITypeBinding parameterType, ICompilerOptions compilerOptions);
    	int getScoreForInOutParameter(int argNum, ITypeBinding argumentType, ITypeBinding parameterType, ICompilerOptions compilerOptions);
    }

    
    public FunctionResolver(ICompilerOptions compilerOptions) {
		super();
		this.compilerOptions = compilerOptions;
	}

	public IDataBinding findMatchingFunction(OverloadedFunctionSet functionSet, List functionArgumentTypes) {
    	return findMatchingFunction(functionSet, functionArgumentTypes, new boolean[functionArgumentTypes.size()]);
    }
	
	public IDataBinding findMatchingFunction(OverloadedFunctionSet functionSet, List functionArgumentTypes, boolean[] argIsLiteral) {
		return findMatchingFunction(functionSet, functionArgumentTypes, argIsLiteral, true);
	}
    
	public IDataBinding findMatchingFunction(OverloadedFunctionSet functionSet, List functionArgumentTypes, boolean returnFirstFunctionIfNoneFound) {
		return findMatchingFunction(functionSet, functionArgumentTypes, new boolean[functionArgumentTypes.size()], returnFirstFunctionIfNoneFound);
	}
	
	public IDataBinding findMatchingFunction(OverloadedFunctionSet functionSet, List functionArgumentTypes, final boolean[] argIsLiteral, boolean returnFirstFunctionIfNoneFound) {
    	functionSet = functionSet.trimFunctionsWithIdenticalSignatures(compilerOptions);
    	IDataBinding firstFunction = returnFirstFunctionIfNoneFound ?
    		(IDataBinding) functionSet.getNestedFunctionBindings().get(0) :
    		IBinding.NOT_FOUND_BINDING;
    		
    	List nestedFunctionBindings = new ArrayList();
    	nestedFunctionBindings.addAll(functionSet.getNestedFunctionBindings());
    	
    	/* 
    	 * Match the number of arguments in the function invocation to the
    	 * number of parameters for each function in the list.  If more than
    	 * one function remains go on to the next step.
    	 */
    	for(Iterator iter = nestedFunctionBindings.iterator(); iter.hasNext();) {
    		IDataBinding dBinding = (IDataBinding) iter.next();
    		IFunctionBinding fBinding = (IFunctionBinding) dBinding.getType();
    		
    		if(!validNumberOfArgs(fBinding, functionArgumentTypes.size())) {
    			iter.remove();
    		}
    	}
    	
    	if(nestedFunctionBindings.isEmpty()) {    	
    		return firstFunction;
    	}
    	else if(nestedFunctionBindings.size() == 1) {
    		return (IDataBinding) nestedFunctionBindings.get(0);
    	}
    	
    	/* 
    	 * Match each argument type against each parameter type of each function
    	 * left in the list.  If there is an exact match then return that function
    	 * otherwise go on to the next step.
    	 */
    	for(Iterator iter = nestedFunctionBindings.iterator(); iter.hasNext();) {
    		IDataBinding dBinding = (IDataBinding) iter.next();
    		IFunctionBinding fBinding = (IFunctionBinding) dBinding.getType();
    		boolean allArgsMatch = true;
    		
    		int limit = min(functionArgumentTypes.size(), fBinding.getParameters().size());
    		
    		for(int i = 0; i < limit; i++) {
    			
   			ITypeBinding argType = (ITypeBinding)functionArgumentTypes.get(i);
    			ITypeBinding parmType = ((IDataBinding) fBinding.getParameters().get(i)).getType(); 
    			if(argType == null || parmType == null || !TypeCompatibilityUtil.typesAreIdentical(argType, parmType, compilerOptions)) {
    				allArgsMatch = false;
    				break;
    			}    			
    		}
    		
    		if(allArgsMatch) {
    			return dBinding;
    		}
    	}
    	
    	IDataBinding bestFittingFunction = getBestFittingFunction(nestedFunctionBindings, functionArgumentTypes, new IArgumentCompatibilityRules() {
    		public int getScoreForInOrOutParameter(int argNum, ITypeBinding argumentType, ITypeBinding parameterType, ICompilerOptions compilerOptions) {
    			int valueWideningDistance = TypeCompatibilityUtil.valueWideningDistance(argumentType, parameterType, compilerOptions);
    			if(valueWideningDistance < 0 && argIsLiteral[argNum]) {
    				valueWideningDistance = TypeCompatibilityUtil.valueWideningDistance(parameterType, argumentType, compilerOptions);
    			}
				return valueWideningDistance;
    		}
    		
    		public int getScoreForInOutParameter(int argNum, ITypeBinding argumentType, ITypeBinding parameterType, ICompilerOptions compilerOptions) {
    			return argIsLiteral[argNum] ?
    				TypeCompatibilityUtil.valueWideningDistance(argumentType, parameterType, compilerOptions) :
    				TypeCompatibilityUtil.referenceWideningDistance(argumentType, parameterType, compilerOptions);
    		}
    	});
    	    	
    	if(bestFittingFunction != null) {
    		return bestFittingFunction;
    	}
    	
    	bestFittingFunction = getBestFittingFunction(nestedFunctionBindings, functionArgumentTypes, new IArgumentCompatibilityRules() {
    		public int getScoreForInOrOutParameter(int argNum, ITypeBinding argumentType, ITypeBinding parameterType, ICompilerOptions compilerOptions) {
    			//Use value narrowing distance -- pass arguments to valueWideningDistance() in different order
    			int valueWideningDistance = TypeCompatibilityUtil.valueWideningDistance(parameterType, argumentType, compilerOptions);
    			if(valueWideningDistance < 0 && argIsLiteral[argNum]) {
    				valueWideningDistance = TypeCompatibilityUtil.valueWideningDistance(argumentType, parameterType, compilerOptions);
    			}
    			return valueWideningDistance;
    		}
    		
    		public int getScoreForInOutParameter(int argNum, ITypeBinding argumentType, ITypeBinding parameterType, ICompilerOptions compilerOptions) {
    			if(TypeCompatibilityUtil.isReferenceCompatible(parameterType, argumentType, compilerOptions)) {
    				return 1;
    			}
    			else {
    				return argIsLiteral[argNum] ?
    	    			TypeCompatibilityUtil.valueWideningDistance(argumentType, parameterType, compilerOptions) :
    	    			TypeCompatibilityUtil.referenceWideningDistance(argumentType, parameterType, compilerOptions);
    			}
    		}
    	});
    	    	
    	if(bestFittingFunction != null) {
    		return bestFittingFunction;
    	}
    	
    	/*
    	 * We would normally have found the best fitting function by now (according to the algorithm in the
    	 * spec for function overloading). But there are strange cases for which we want to try one more time.
    	 * eg. startTransaction ( aFixedRec, "PR01", "TM01" ) ;
    	 *     The second argument is a string, but this invocation should resolve to the (any in, char(4) inout, char(4) in)
    	 *     version of the function in VGLib to be compaitible with past releases.
    	 */
      	bestFittingFunction = getBestFittingFunction(nestedFunctionBindings, functionArgumentTypes, new IArgumentCompatibilityRules() {
    		public int getScoreForInOrOutParameter(int argNum, ITypeBinding argumentType, ITypeBinding parameterType, ICompilerOptions compilerOptions) {
    			return TypeCompatibilityUtil.isMoveCompatible(parameterType, argumentType, null, compilerOptions) ? 1 : -1;
    		}
    		
    		public int getScoreForInOutParameter(int argNum, ITypeBinding argumentType, ITypeBinding parameterType, ICompilerOptions compilerOptions) {
    			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argumentType.getKind() && !argumentType.isNullable()) {
    				PrimitiveTypeBinding primitiveTypeBinding = ((PrimitiveTypeBinding) argumentType);
					Primitive prim = primitiveTypeBinding.getPrimitive();
    				if(Primitive.STRING == prim) {
    					argumentType = PrimitiveTypeBinding.getInstance(Primitive.CHAR, primitiveTypeBinding.getLength());
    				}
    				
    				if(TypeCompatibilityUtil.isReferenceCompatible(parameterType, argumentType, compilerOptions)) {
        				return 1;
        			}
        			else {
        				return TypeCompatibilityUtil.referenceWideningDistance(argumentType, parameterType, compilerOptions);
        			}
    			}
    			return -1;
    		}
    	});
      	
    	if(bestFittingFunction != null) {
    		return bestFittingFunction;
    	}
    	
    	firstFunction = returnFirstFunctionIfNoneFound && !nestedFunctionBindings.isEmpty() ?
    		(IDataBinding) nestedFunctionBindings.get(0) :
    		IBinding.NOT_FOUND_BINDING;
    	
    	if(nestedFunctionBindings.size() == 1) {
    		return (IDataBinding) nestedFunctionBindings.get(0);
    	}
    	else {
    		return firstFunction;
    	}
    }
    
    private boolean validNumberOfArgs(IFunctionBinding functionBinding, int numArgs) {
    	int[] validArgs = functionBinding.getValidNumbersOfArguments();
    	if (validArgs.length == 1) {
    		return validArgs[0] == numArgs;
    	}
    	
    	if (validArgs.length == 2 && validArgs[0] < 0) {
    		return numArgs >= validArgs[1];
    	}
    	
    	for (int i = 0; i < validArgs.length; i++) {
			if (validArgs[i] == numArgs) {
				return true;
			}
		}
    	return false;
    }
    
    private IDataBinding getBestFittingFunction(List nestedFunctionBindings, List functionArgumentTypes, IArgumentCompatibilityRules compatibilityRules) {
    	IDataBinding bestFittingFunction = null;
		int[] bestArgumentScore = null;
		for(Iterator iter = nestedFunctionBindings.iterator(); iter.hasNext();) {
			IDataBinding dBinding = (IDataBinding) iter.next();
			IFunctionBinding fBinding = (IFunctionBinding) dBinding.getType();
			int limit = min(fBinding.getParameters().size(), functionArgumentTypes.size());
			
			int[] currentArgumentScore = new int[limit];
			for(int i = 0; i < limit; i++) {
				
				ITypeBinding argType = (ITypeBinding)functionArgumentTypes.get(i);
				if(argType == null) {
					currentArgumentScore[i] = 0;
				}
				else {
					FunctionParameterBinding parmBinding = (FunctionParameterBinding) fBinding.getParameters().get(i); 
					ITypeBinding parmType = parmBinding.getType(); 
					currentArgumentScore[i] = parmBinding.isInput() || parmBinding.isOutput() ?
						compatibilityRules.getScoreForInOrOutParameter(i, argType, parmType, compilerOptions) :
						compatibilityRules.getScoreForInOutParameter(i, argType, parmType, compilerOptions);    				
				}
			}
			
			if(allElementsPostive(currentArgumentScore)) {
				if(bestArgumentScore == null) {
					bestArgumentScore = currentArgumentScore;
					bestFittingFunction = dBinding;
				}
				else {
					if(allElementsLessThan(currentArgumentScore, bestArgumentScore)) {
						bestArgumentScore = currentArgumentScore;
						bestFittingFunction = dBinding;
					}
					else if(allElementsEqual(currentArgumentScore, bestArgumentScore) && !iter.hasNext()) {
						return AmbiguousDataBinding.getInstance();
					}
				}
			}
		}
		return bestFittingFunction;
	}
    
    private int min(int int1, int int2) {
    	if (int1 < int2) {
    		return int1;
    	}
    	return int2;
    }
    
    private boolean allElementsLessThan(int[] srcArray, int[] targetAry) {
    	boolean allElementsEqual = true;
    	
    	int limit = min(srcArray.length, targetAry.length);
    	
    	for(int i = 0; i < limit; i++) {
    		
    		if(allElementsEqual && srcArray[i] != targetAry[i]) {
    			allElementsEqual = false;
    		}
			if(srcArray[i] > targetAry[i]) {
				return false;
			}
		}
    	return !allElementsEqual;
	}
    
    private boolean allElementsEqual(int[] srcArray, int[] targetAry) {

    	int limit = min(srcArray.length, targetAry.length);

    	for(int i = 0; i < limit; i++) {
			if(srcArray[i] != targetAry[i]) {
				return false;
			}
		}
		return true;
	}

	private boolean allElementsPostive(int[] elements) {
		for(int i = 0; i < elements.length; i++) {
			if(elements[i] < 0) {
				return false;
			}
		}
		return true;
	}

}
