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
package org.eclipse.edt.compiler.core.ast;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * @author demurray
 */
public class Primitive implements Serializable {

	public static final int ANY_PRIMITIVE = 0;
	public static final Primitive ANY = new Primitive(0, IEGLConstants.KEYWORD_ANY);
	
	public static final int BIGINT_PRIMITIVE = 1;
	public static final Primitive BIGINT = new Primitive(1, IEGLConstants.KEYWORD_BIGINT, 18);
	
	public static final int BIN_PRIMITIVE = 2;
	public static final Primitive BIN = new Primitive(2, IEGLConstants.KEYWORD_BIN);
	
	public static final int BOOLEAN_PRIMITIVE = 3;
	public static final Primitive BOOLEAN = new Primitive(3, IEGLConstants.KEYWORD_BOOLEAN, 1);
	
	public static final int CHAR_PRIMITIVE = 4;
	public static final Primitive CHAR = new Primitive(4, IEGLConstants.KEYWORD_CHAR);
	
	public static final int DBCHAR_PRIMITIVE = 5;
	public static final Primitive DBCHAR = new Primitive(5, IEGLConstants.KEYWORD_DBCHAR);
	
	public static final int DBCHARLIT_PRIMITIVE = 6;
	public static final Primitive DBCHARLIT = new Primitive(6, IEGLConstants.KEYWORD_DBCHAR);
	
	public static final int DECIMAL_PRIMITIVE = 7;
	public static final Primitive DECIMAL = new Primitive(7, IEGLConstants.KEYWORD_DECIMAL);
	
	public static final int FLOAT_PRIMITIVE = 8;
	public static final Primitive FLOAT = new Primitive(8, IEGLConstants.KEYWORD_FLOAT, 8);
	
	public static final int HEX_PRIMITIVE = 9;
	public static final Primitive HEX = new Primitive(9, IEGLConstants.KEYWORD_HEX);
	
	public static final int INT_PRIMITIVE = 10;
	public static final Primitive INT = new Primitive(10, IEGLConstants.KEYWORD_INT, 9);
	
	public static final int MBCHAR_PRIMITIVE = 11;
	public static final Primitive MBCHAR = new Primitive(11, IEGLConstants.KEYWORD_MBCHAR);
	
	public static final int MONEY_PRIMITIVE = 12;
	public static final Primitive MONEY = new Primitive(12, IEGLConstants.KEYWORD_MONEY, 16, 2);
	
	public static final int NUM_PRIMITIVE = 13;
	public static final Primitive NUM = new Primitive(13, IEGLConstants.KEYWORD_NUM);
	
	public static final int NUMBER_PRIMITIVE = 14;
	public static final Primitive NUMBER = new Primitive(14, IEGLConstants.KEYWORD_NUMBER);
	
	public static final int NUMC_PRIMITIVE = 15;
	public static final Primitive NUMC = new Primitive(15, IEGLConstants.KEYWORD_NUMC);
	
	public static final int PACF_PRIMITIVE = 16;
	public static final Primitive PACF = new Primitive(16, IEGLConstants.KEYWORD_PACF);
	
	public static final int SMALLFLOAT_PRIMITIVE = 17;
	public static final Primitive SMALLFLOAT = new Primitive(17, IEGLConstants.KEYWORD_SMALLFLOAT, 4);
	
	public static final int SMALLINT_PRIMITIVE = 18;
	public static final Primitive SMALLINT = new Primitive(18, IEGLConstants.KEYWORD_SMALLINT, 4);
	
	public static final int STRING_PRIMITIVE = 19;
	public static final Primitive STRING = new Primitive(19, IEGLConstants.KEYWORD_STRING);
	
	public static final int UNICODE_PRIMITIVE = 20;
	public static final Primitive UNICODE = new Primitive(20, IEGLConstants.KEYWORD_UNICODE);
	
	public static final int BLOB_PRIMITIVE = 21;
	public static final Primitive BLOB = new Primitive(21, IEGLConstants.KEYWORD_BLOB);
	
	public static final int CLOB_PRIMITIVE = 22;
	public static final Primitive CLOB = new Primitive(22, IEGLConstants.KEYWORD_CLOB);
	
	public static final int DATE_PRIMITIVE = 23;
	public static final Primitive DATE = new Primitive(23, IEGLConstants.KEYWORD_DATE, 8);
	
	public static final int MONTHSPAN_INTERVAL_PRIMITIVE = 24;
	public static final Primitive MONTHSPAN_INTERVAL = new Primitive(24, IEGLConstants.KEYWORD_INTERVAL, 7);
	
	public static final int SECONDSPAN_INTERVAL_PRIMITIVE = 25;
	public static final Primitive SECONDSPAN_INTERVAL = new Primitive(25, IEGLConstants.KEYWORD_INTERVAL, 7);
	
	public static final int INTERVAL_PRIMITIVE = 26;
	public static final Primitive INTERVAL = new Primitive(26, IEGLConstants.KEYWORD_INTERVAL, 7);
	
	public static final int TIME_PRIMITIVE = 27;
	public static final Primitive TIME = new Primitive(27, IEGLConstants.KEYWORD_TIME, 6);
	
	public static final int TIMESTAMP_PRIMITIVE = 28;
	public static final Primitive TIMESTAMP = new Primitive(28, IEGLConstants.KEYWORD_TIMESTAMP, 14);
	
	private static Primitive[] types;
	
	static {
		List primitiveList = new ArrayList();
		
		primitiveList.add(ANY);
		primitiveList.add(BIGINT);
		primitiveList.add(BIN);
		primitiveList.add(BOOLEAN);
		primitiveList.add(CHAR);
		primitiveList.add(DBCHAR);
		primitiveList.add(DBCHARLIT);
		primitiveList.add(DECIMAL);
		primitiveList.add(FLOAT);
		primitiveList.add(HEX);
		primitiveList.add(INT);
		primitiveList.add(MBCHAR);
		primitiveList.add(MONEY);
		primitiveList.add(NUM);
		primitiveList.add(NUMBER);
		primitiveList.add(NUMC);
		primitiveList.add(PACF);
		primitiveList.add(SMALLFLOAT);
		primitiveList.add(SMALLINT);
		primitiveList.add(STRING);
		primitiveList.add(UNICODE);
		primitiveList.add(BLOB);
		primitiveList.add(CLOB);
		primitiveList.add(DATE);
		primitiveList.add(MONTHSPAN_INTERVAL);
		primitiveList.add(SECONDSPAN_INTERVAL);
		primitiveList.add(INTERVAL);
		primitiveList.add(TIME);
		primitiveList.add(TIMESTAMP);

		types = (Primitive[])primitiveList.toArray(new Primitive[primitiveList.size()]);
	}
	
    private int type;
    private String name;
    private int defaultLength;
    private boolean hasDefaultLength;
    private int defaultDecimals;
    private boolean hasDefaultDecimals;

    // From old EGLPrimitive. Still need this kind of support?
    //private byte intervalPrecision, startCode, endCode;

    private Primitive(int type, String name) {
        this.type = type;
        this.name = name;
    }
    
    private Primitive(int type, String name, int defaultLength) {
        this(type, name);
        this.defaultLength = defaultLength;
        this.hasDefaultLength = true;
    }

    private Primitive(int type, String name, int defaultLength, int defaultDecimals) {
        this(type, name, defaultLength);
        this.defaultDecimals = defaultDecimals;
        this.hasDefaultDecimals = true;
    }
    
    private Object readResolve() {
        return getPrimitive(name);
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private boolean isOfType(String type) {
        if (getName().equalsIgnoreCase(type.trim())) {
            return true;
        }
        return false;
    }

    public static Primitive getPrimitive(String type) {
        for (int i = 0; i < types.length; i++) {
            if (types[i].isOfType(type)) {
                return types[i];
            }
        }

        return null;
    }
    
    public boolean hasDefaultLength() {
        return hasDefaultLength;
    }

    public int getDefaultLength() {
        if (hasDefaultLength) {
            return defaultLength;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public boolean hasDefaultDecimals() {
        return hasDefaultDecimals;
    }

    public int getDefaultDecimals() {
        if (hasDefaultDecimals) {
            return defaultDecimals;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static boolean isIntegerType(Primitive prim) {
        return prim == SMALLINT || prim == INT || prim == BIGINT;
    }

    public static boolean isFloatType(Primitive prim) {
        return prim == SMALLFLOAT || prim == FLOAT;
    }

    public static boolean isNumericType(Primitive prim) {
        return prim == BIGINT || prim == BIN || prim == DECIMAL
                || prim == INT || prim == NUM || prim == NUMBER
                || prim == NUMC || prim == PACF || prim == SMALLINT
                || prim == FLOAT || prim == SMALLFLOAT
                || prim == MONEY;
    }

    public static boolean isStringType(Primitive prim) {
        return prim == CHAR || prim == DBCHAR || prim == DBCHARLIT
                || prim == HEX || prim == MBCHAR || prim == UNICODE
                || prim == STRING;
    }

    public static boolean isDateTimeType(Primitive prim) {
        return prim.getType() == DATE.getType() || prim.getType() == TIME.getType()
                || prim.getType() == TIMESTAMP.getType()
                || prim.getType() == MONTHSPAN_INTERVAL.getType()
                || prim.getType() == SECONDSPAN_INTERVAL.getType()
                || prim.getType() == INTERVAL.getType();
    }
    
    public static boolean isLargeObjectType(Primitive prim) {
        return prim.getType() == BLOB.getType() || prim.getType() == CLOB.getType();
    }
    
    public static boolean isLooseType(Primitive prim,int length){
        if (prim == Primitive.NUMBER) {
            return true;
        }
        if ((prim == Primitive.CHAR ||
        	 prim == Primitive.DBCHAR ||
        	 prim == Primitive.HEX ||
			 prim == Primitive.DECIMAL ||
			 prim == Primitive.UNICODE ||
			 prim == Primitive.MBCHAR )
                && length == 0) {
            return true;
        }
        return false;
    }
    
}
