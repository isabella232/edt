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

import org.eclipse.edt.compiler.core.ast.Primitive;

public class PrimitiveTypeBindingImpl extends PrimitiveTypeBinding {

	Primitive prim;
    int length;
    int decimals;
    String pattern;
    transient int bytes = -1;
    
    protected PrimitiveTypeBindingImpl(PrimitiveSpec primSpec) {
		super(primSpec.toString());
		this.prim = primSpec.prim;
		this.length = primSpec.length;
		this.decimals = primSpec.decimals;
		this.pattern = primSpec.pattern;
	}
    
    private PrimitiveTypeBindingImpl(PrimitiveTypeBindingImpl old) {
    	super(old.caseSensitiveInternedName);

    	prim = old.prim;
        length = old.length;
        decimals = old.decimals;
        pattern = old.pattern;
        bytes = old.bytes;
}
    
    private Object readResolve() {
        if(pattern == null) {
            return getInstance(prim, length, decimals);
        }
        else {
            return getInstance(prim, pattern);
        }
    }
	
	public Primitive getPrimitive() {
		return prim;
	}

	public int getLength() {
		return length;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public int getDecimals() {
		return decimals;
	}
	
	public String getTimeStampOrIntervalPattern() {
		return pattern;
	}

	public int getKind() {
		return PRIMITIVE_TYPE_BINDING;
	}

	public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public ITypeBinding copyTypeBinding() {
		return this;
	}
	
	public int getBytes() {
		if(bytes == -1) {
			bytes = length;
			
			switch (prim.getType()) {
				case Primitive.SMALLINT_PRIMITIVE :
					bytes = 2;
					break;
				case Primitive.INT_PRIMITIVE :
					bytes = 4;
					break;
				case Primitive.BIGINT_PRIMITIVE :
					bytes = 8;
					break;
				case Primitive.SMALLFLOAT_PRIMITIVE :
					bytes = 4;
					break;
				case Primitive.FLOAT_PRIMITIVE :
					bytes = 8;
					break;
				case Primitive.BIN_PRIMITIVE :
					if (bytes == 4)
						bytes = 2;
					else if (bytes == 9)
						bytes = 4;
					else if (bytes == 18)
						bytes = 8;
					break;
				case Primitive.MONEY_PRIMITIVE :
				case Primitive.DECIMAL_PRIMITIVE :
				case Primitive.PACF_PRIMITIVE :
					bytes = Math.round((bytes + 1) / 2f);
					break;
				case Primitive.UNICODE_PRIMITIVE :
				case Primitive.STRING_PRIMITIVE :
				case Primitive.DBCHAR_PRIMITIVE :				
					// unicode and dbchars are double the size, so bytes *= 2
					bytes *= 2;
					break;
				case Primitive.HEX_PRIMITIVE :
					// hex is half the size, so bytes /= 2
					bytes /= 2;
					break;
				case Primitive.TIMESTAMP_PRIMITIVE :
					if(pattern != null) {
						TimestampOrIntervalSpec spec = TimestampOrIntervalSpec.createTimestampSpec(pattern);
						bytes = spec.endCode - spec.startCode + spec.intervalPrecision;
					}
					break;
				case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE:
				case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE:
					if(pattern != null) {
						TimestampOrIntervalSpec spec = TimestampOrIntervalSpec.createIntervalSpec(pattern);
						bytes = spec.endCode - spec.startCode + spec.intervalPrecision + 1;
					}
					break;

				default :
					break;
			}
		}
		return bytes;
	}
	
	public boolean isDynamic() {
		return prim == Primitive.ANY;
	}
	
    public ITypeBinding getBaseType() {
        return this;
    }
    
    private static class TimestampOrIntervalSpec {
    	byte intervalPrecision, startCode, endCode;
    	
        /**
         * The time unit for the YEAR field in a binary qualifier.
         *  
         */
        public static final byte TU_YEAR = 0;

        /**
         * The time unit for the MONTH field in a binary qualifier.
         *  
         */
        public static final byte TU_MONTH = 2;

        /**
         * The time unit for the DAY field in a binary qualifier.
         *  
         */
        public static final byte TU_DAY = 4;

        /**
         * The time unit for the HOUR field in a binary qualifier.
         *  
         */
        public static final byte TU_HOUR = 6;

        /**
         * The time unit for the MINUTE field in a binary qualifier.
         *  
         */
        public static final byte TU_MINUTE = 8;

        /**
         * The time unit for the SECOND field in a binary qualifier.
         *  
         */
        public static final byte TU_SECOND = 10;

        /**
         * The time unit for the FRACTION(1) field in a binary qualifier. The
         * fraction scale is equal to 1.
         *  
         */
        public static final byte TU_F1 = 11;

        /**
         * The time unit for the FRACTION(2) field in a binary qualifier. The
         * fraction scale is equal to 2.
         *  
         */
        public static final byte TU_F2 = 12;

        /**
         * The time unit for the FRACTION(3) field in a binary qualifier. The
         * fraction scale is equal to 3.
         *  
         */
        public static final byte TU_F3 = 13;

        /**
         * The time unit for the FRACTION(4) field in a binary qualifier. The
         * fraction scale is equal to 4.
         *  
         */
        public static final byte TU_F4 = 14;

        /**
         * The time unit for the FRACTION(5) field in a binary qualifier. The
         * fraction scale is equal to 5.
         *  
         */
        public static final byte TU_F5 = 15;

        /**
         * The time unit for the FRACTION(5) field in a binary qualifier. The
         * fraction scale is equal to 5.
         *  
         */
        public static final byte TU_F6 = 16;
        
        /*
         * intervalPrecision::= n, 2 < n <= 9, (default for year is 4 instead of 2)
         */
        private static int intervalPrecision(String pattern) {
            int count = 1;
            char prevCh = pattern.charAt(0);
            for (int i = 1; i < pattern.length(); i++) {
                if (pattern.charAt(i) == prevCh) {
                    prevCh = pattern.charAt(i); //nop
                    count++;
                } else
                    break;
            }
            return count;
        }

        /*
         * secondScale::= n, 0 < n <= 6
         */
        private static int secondScale(String pattern) {
            int count = 1;
            char prevCh = pattern.charAt(pattern.length() - 1);
            for (int i = pattern.length() - 2; i >= 0; i--) {
                if (pattern.charAt(i) == prevCh) {
                    prevCh = pattern.charAt(i); //nop
                    count++;
                } else
                    break;
            }
            return count;
        }

        private static boolean isMonth(String pattern, String originalPattern, boolean bln) {
            int count;
            if (bln) {
                count = intervalPrecision(pattern);
                if (pattern.length() == count) {
                    for (int i = 0; i < pattern.length(); i++)
                        if (originalPattern.charAt(i) != 'M')
                            return false;
                    return true;
                } else {
                    return pattern.charAt(count) == 'd';
                }
            } else {
                count = secondScale(pattern);
                if (pattern.length() == count) {
                    for (int i = 0; i < pattern.length(); i++)
                        if (originalPattern.charAt(i) != 'M')
                            return false;
                    return true;
                } else {
                    return pattern.charAt(pattern.length() - count - 1) == 'y';
                }
            }
        }

        private static boolean isMinute(String pattern, String originalPattern, boolean bln) {
            int count;
            if (bln) {
                count = intervalPrecision(pattern);
                if (pattern.length() == count) {
                    for (int i = 0; i < pattern.length(); i++)
                        if (originalPattern.charAt(i) != 'm')
                            return false;
                    return true;
                } else {
                    return pattern.charAt(count) == 's';
                }
            } else {
                count = secondScale(pattern);
                if (pattern.length() == count) {
                    for (int i = 0; i < pattern.length(); i++)
                        if (originalPattern.charAt(i) != 'm')
                            return false;
                    return true;
                } else {
                    return pattern.charAt(pattern.length() - count - 1) == 'h';
                }
            }
        }

		public static TimestampOrIntervalSpec createIntervalSpec(String mixedCasePattern) {
	        byte intervalPrecision = (byte) 4;
	        byte startCode = TU_YEAR;
	        byte endCode = TU_MONTH;

	        if (mixedCasePattern != null) {

	            String pattern = mixedCasePattern.toLowerCase();

	            if (pattern.startsWith("yy")) {
	            	intervalPrecision = (byte) intervalPrecision(pattern);
	                startCode = TU_YEAR;
	            } else if (pattern.startsWith("mm") && isMonth(pattern, mixedCasePattern, true)) {
	            	intervalPrecision = (byte) intervalPrecision(pattern);
	                startCode = TU_MONTH;
	            } else if (pattern.startsWith("dd")) {
	            	intervalPrecision = (byte) intervalPrecision(pattern);
	                startCode = TU_DAY;
	            } else if (pattern.startsWith("hh")) {
	            	intervalPrecision = (byte) intervalPrecision(pattern);
	                startCode = TU_HOUR;
	            } else if (pattern.startsWith("mm") && isMinute(pattern, mixedCasePattern, true)) {
	            	intervalPrecision = (byte) intervalPrecision(pattern);
	                startCode = TU_MINUTE;
	            } else if (pattern.startsWith("ss")) {
	            	intervalPrecision = (byte) intervalPrecision(pattern);
	                startCode = TU_SECOND;
	            }

	            if (pattern.endsWith("yy"))
	                endCode = TU_YEAR;
	            else if (pattern.endsWith("mm") && isMonth(pattern, mixedCasePattern, false))
	                endCode = TU_MONTH;
	            else if (pattern.endsWith("dd"))
	                endCode = TU_DAY;
	            else if (pattern.endsWith("hh"))
	                endCode = TU_HOUR;
	            else if (pattern.endsWith("mm") && isMinute(pattern, mixedCasePattern, false))
	                endCode = TU_MINUTE;
	            else if (pattern.endsWith("ss"))
	                endCode = TU_SECOND;
	            else if (pattern.endsWith("f")) {
	                switch (secondScale(pattern)) {
	                case 1:
	                    endCode = TU_F1;
	                    break;
	                case 2:
	                    endCode = TU_F2;
	                    break;
	                case 3:
	                    endCode = TU_F3;
	                    break;
	                case 4:
	                    endCode = TU_F4;
	                    break;
	                case 5:
	                    endCode = TU_F5;
	                    break;
	                case 6:
	                    endCode = TU_F6;
	                    break;
	                }
	            }
	        }
	        
	        TimestampOrIntervalSpec result = new TimestampOrIntervalSpec();
			result.startCode = startCode;
			result.endCode = endCode;
			result.intervalPrecision = intervalPrecision;
			return result;
	    }
		
		public static TimestampOrIntervalSpec createTimestampSpec(String mixedCasePattern) {
			byte length = (byte) 4;
	        byte startCode = TU_YEAR;
	        byte endCode = TU_SECOND;

	        if (mixedCasePattern != null) {
	            String pattern = mixedCasePattern.toLowerCase();

	            if (pattern.startsWith("yy")) {
	                length = (byte) 4;
	                startCode = TU_YEAR;
	            } else if (pattern.startsWith("mm") && isMonth(pattern, mixedCasePattern, true)) {
	                length = (byte) 2;
	                startCode = TU_MONTH;
	            } else if (pattern.startsWith("dd")) {
	                length = (byte) 2;
	                startCode = TU_DAY;
	            } else if (pattern.startsWith("hh")) {
	                length = (byte) 2;
	                startCode = TU_HOUR;
	            } else if (pattern.startsWith("mm") && isMinute(pattern, mixedCasePattern, true)) {
	                length = (byte) 2;
	                startCode = TU_MINUTE;
	            } else if (pattern.startsWith("ss")) {
	                length = (byte) 2;
	                startCode = TU_SECOND;
	            }

	            if (pattern.endsWith("yy"))
	                endCode = TU_YEAR;
	            else if (pattern.endsWith("mm") && isMonth(pattern, mixedCasePattern, false))
	                endCode = TU_MONTH;
	            else if (pattern.endsWith("dd"))
	                endCode = TU_DAY;
	            else if (pattern.endsWith("hh"))
	                endCode = TU_HOUR;
	            else if (pattern.endsWith("mm") && isMinute(pattern, mixedCasePattern, false))
	                endCode = TU_MINUTE;
	            else if (pattern.endsWith("ss"))
	                endCode = TU_SECOND;
	            else if (pattern.endsWith("f")) {
	                switch (secondScale(pattern)) {
	                case 1:
	                    endCode = TU_F1;
	                    break;
	                case 2:
	                    endCode = TU_F2;
	                    break;
	                case 3:
	                    endCode = TU_F3;
	                    break;
	                case 4:
	                    endCode = TU_F4;
	                    break;
	                case 5:
	                    endCode = TU_F5;
	                    break;
	                case 6:
	                    endCode = TU_F6;
	                    break;
	                }
	            }
	        }
	        
	        TimestampOrIntervalSpec result = new TimestampOrIntervalSpec();
			result.startCode = startCode;
			result.endCode = endCode;
			result.intervalPrecision = length;
			return result;
		}
    }    
    
	@Override
	public ITypeBinding primGetNullableInstance() {
		PrimitiveTypeBindingImpl nullable = new PrimitiveTypeBindingImpl(this);
		nullable.setNullable(true);
		return nullable;
	}
    
}
