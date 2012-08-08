/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.edt.compiler.TypeValidator;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.IntervalType;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;

public class DefaultTypeValidator implements TypeValidator {
	
	@Override
	public void validateType(org.eclipse.edt.compiler.core.ast.Type type, Type typeBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (typeBinding instanceof FixedPrecisionType) {
			// Length and decimals must be non-negative, 0-32, and decimals must be <= length.
			int len = ((FixedPrecisionType)typeBinding).getLength();
			int decimals = ((FixedPrecisionType)typeBinding).getDecimals();
			
			if (len < 0 || len > 32) {
				problemRequestor.acceptProblem(type, IProblemRequestor.INVALID_LENGTH_FOR_PARAMETERIZED_TYPE,
						new String[] {Integer.toString(len), typeBinding.getClassifier().getTypeSignature(), "(1..32)"});
			}
			
			// Don't report too many errors at once
			if (decimals < 0 || decimals > 32) {
				problemRequestor.acceptProblem(type, IProblemRequestor.INVALID_DECIMALS,
						new String[] {Integer.toString(decimals), typeBinding.getClassifier().getTypeSignature(), "(1..32)"});
			}
			else if (decimals > len) {
				problemRequestor.acceptProblem(type, IProblemRequestor.DECIMALS_GREATER_THAN_LENGTH,
						new String[] {Integer.toString(decimals), typeBinding.getClassifier().getTypeSignature(), Integer.toString(len)});
			}
		}
		else if (typeBinding instanceof SequenceType) {
			// Length must be non-negative
			int len = ((SequenceType)typeBinding).getLength();
			if (len < 0) {
				problemRequestor.acceptProblem(type, IProblemRequestor.NEGATIVE_LENGTH_INVALID,
						new String[] {Integer.toString(len), typeBinding.getClassifier().getTypeSignature()});
			}
		}
		else if (typeBinding instanceof TimestampType) {
			DateTimePattern dtPat = new DateTimePattern(((TimestampType)typeBinding).getPattern());
	  		if(!dtPat.isValidTimeStampPattern()) {
	  			Integer[] errors = dtPat.getErrorMessageNumbers();
	  			for( int i = 0; i < errors.length; i++ ) {
	  				problemRequestor.acceptProblem(type,
	  						errors[i].intValue(),
							new String[] {((TimestampType)typeBinding).getPattern()});
	  						
	  			}
	  		}
		}
		else if (typeBinding instanceof IntervalType) {
			DateTimePattern dtPat = new DateTimePattern(((TimestampType)typeBinding).getPattern());
	  		if(!dtPat.isValidIntervalPattern()) {
	  			Integer[] errors = dtPat.getErrorMessageNumbers();
	  			for( int i = 0; i < errors.length; i++ ) {
	  				problemRequestor.acceptProblem(type,
	  						errors[i].intValue(),
							new String[] {((TimestampType)typeBinding).getPattern()});
	  						
	  			}
	  		}
		}
	}
	
	public static class DateTimePattern {
		private String[] components;
		private boolean isValidPattern;
		private Set<Integer> errorMessageNumbers;
		private String text;
		
		static final List<Character> VALID_CHARS = new ArrayList<Character>(Arrays.asList(new Character[] {				
			'y',
			'M',
			'd',
			'H',
			'm',
			's',
			'f',
		}));
		
		private static Map<Character, Integer> maxOccurences = new HashMap<Character, Integer>();
		static {
			maxOccurences.put('y', 4);
			maxOccurences.put('M', 2);
			maxOccurences.put('d', 2);
			maxOccurences.put('H', 2);
			maxOccurences.put('m', 2);
			maxOccurences.put('s', 2);
			maxOccurences.put('f', 6);
		}
		
		public DateTimePattern(String atext) {
			this.isValidPattern = true;
			this.errorMessageNumbers = new TreeSet<Integer>();
			this.text = atext;
			if (this.text.startsWith("\"") && this.text.endsWith("\"")) {
				this.text = atext.substring(1,atext.length()-1);
			}
			if (text.length() == 0) {
				isValidPattern = false;
				errorMessageNumbers.add(IProblemRequestor.DATETIME_PATTERN_EMPTY);
				return;
			}
			
			StringBuffer component = new StringBuffer();
			List componentsList = new ArrayList();
			char currentChar = text.charAt( 0 );
			for (int i = 0; i < text.length() && isValidPattern; i++) {
				char ch = text.charAt(i);
				if (!VALID_CHARS.contains(ch)) {
					isValidPattern = false;
					errorMessageNumbers.add(IProblemRequestor.DATETIME_PATTERN_HAS_INVALID_CHARACTER);
				}
				else {
					if (currentChar != ch) {
						componentsList.add(component.toString());
						component = new StringBuffer();
						currentChar = ch;
					}
					component.append( ch );
				}
			}
			if (component.toString().length() != 0) {
				componentsList.add( component.toString() );
			}
			components = (String[]) componentsList.toArray( new String[0] );
		}
		
		private void checkOrder() {
			char currentChar = components[0].charAt( 0 );
			int currentIndex = VALID_CHARS.indexOf( currentChar );
			
			for( int i = 1; i < components.length && isValidPattern; i++ ) {
				char nextChar = components[i].charAt( 0 );
				int nextIndex = VALID_CHARS.indexOf( nextChar );
				if( nextChar != currentChar ) {
					if( nextChar != VALID_CHARS.get( currentIndex + 1 ) ) {
						isValidPattern = false;
						errorMessageNumbers.add(nextIndex < currentIndex
								? IProblemRequestor.DATETIME_PATTERN_OUT_OF_ORDER
								: IProblemRequestor.DATETIME_PATTERN_MISSING_INTERMEDIATE_FIELD);
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
					isValid = components[i].length() <= maxOccurences.get(currentChar); 
				}
				
				if(!isValid) {
					isValidPattern = false;
					if(i == 0 && firstFieldAllowedLength != -1) {
						errorMessageNumbers.add(IProblemRequestor.DATETIME_PATTERN_FIRST_INTERVAL_FIELD_TOO_LONG);						
					}
					else {
						errorMessageNumbers.add(occurenceMessages[VALID_CHARS.indexOf(currentChar)]);
					}
				}
			}
		}
		
		private void checkIntervalSpan() {
			boolean spanValid = true;
			char firstChar = components[0].charAt( 0 );
			char lastChar = components[ components.length-1 ].charAt( 0 );
			if( firstChar == 'y' ) {
				spanValid = lastChar == 'M' ? components.length == 2 : lastChar == 'y';
			}
			else if( firstChar == 'M' ) {
				spanValid = components.length == 1;
			}
			if( !spanValid ) {
				isValidPattern = false;
				errorMessageNumbers.add(IProblemRequestor.DATETIME_PATTERN_INVALID_INTERVAL_SPAN);
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
			return (Integer[]) errorMessageNumbers.toArray(new Integer[errorMessageNumbers.size()]);
		}
	}
}
