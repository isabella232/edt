if ( !egl.javascript )
{
	egl.javascript = {};
}

/* Generated from 'MathContext.nrx' 8 Sep 2000 11:07:48 [v2.00] */
/* Options: Binary Comments Crossref Format Java Logo Strictargs Strictcase Trace2 Verbose3 */
//--package com.ibm.icu.math;

/* ------------------------------------------------------------------ */
/* MathContext -- Math context settings                               */
/* ------------------------------------------------------------------ */
/*                                                                    */
/*   The MathContext object encapsulates the settings used by the     */
/*   BigDecimal class; it could also be used by other arithmetics.    */
/* ------------------------------------------------------------------ */
/* Notes:                                                             */
/*                                                                    */
/* 1. The properties are checked for validity on construction, so     */
/*    the BigDecimal class may assume that they are correct.          */
/* ------------------------------------------------------------------ */
/* Author:    Mike Cowlishaw                                          */
/* 1997.09.03 Initial version (edited from netrexx.lang.RexxSet)      */
/* 1997.09.12 Add lostDigits property                                 */
/* 1998.05.02 Make the class immutable and final; drop set methods    */
/* 1998.06.05 Add Round (rounding modes) property                     */
/* 1998.06.25 Rename from DecimalContext; allow digits=0              */
/* 1998.10.12 change to com.ibm.icu.math package                          */
/* 1999.02.06 add javadoc comments                                    */
/* 1999.03.05 simplify; changes from discussion with J. Bloch         */
/* 1999.03.13 1.00 release to IBM Centre for Java Technology          */
/* 1999.07.10 1.04 flag serialization unused                          */
/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
/* ------------------------------------------------------------------ */


/* JavaScript conversion (c) 2003 STZ-IDA and PTV AG, Karlsruhe, Germany */



/**
 * The <code>MathContext</code> immutable class encapsulates the
 * settings understood by the operator methods of the {@link BigDecimal}
 * class (and potentially other classes).  Operator methods are those
 * that effect an operation on a number or a pair of numbers.
 * <p>
 * The settings, which are not base-dependent, comprise:
 * <ol>
 * <li><code>digits</code>:
 * the number of digits (precision) to be used for an operation
 * <li><code>form</code>:
 * the form of any exponent that results from the operation
 * <li><code>lostDigits</code>:
 * whether checking for lost digits is enabled
 * <li><code>roundingMode</code>:
 * the algorithm to be used for rounding.
 * </ol>
 * <p>
 * When provided, a <code>MathContext</code> object supplies the
 * settings for an operation directly.
 * <p>
 * When <code>MathContext.DEFAULT</code> is provided for a
 * <code>MathContext</code> parameter then the default settings are used
 * (<code>9, SCIENTIFIC, false, ROUND_HALF_UP</code>).
 * <p>
 * In the <code>BigDecimal</code> class, all methods which accept a
 * <code>MathContext</code> object defaults) also have a version of the
 * method which does not accept a MathContext parameter.  These versions
 * carry out unlimited precision fixed point arithmetic (as though the
 * settings were (<code>0, PLAIN, false, ROUND_HALF_UP</code>).
 * <p>
 * The instance variables are shared with default access (so they are
 * directly accessible to the <code>BigDecimal</code> class), but must
 * never be changed.
 * <p>
 * The rounding mode constants have the same names and values as the
 * constants of the same name in <code>java.math.BigDecimal</code>, to
 * maintain compatibility with earlier versions of
 * <code>BigDecimal</code>.
 *
 * @see     BigDecimal
 * @author  Mike Cowlishaw
 * @stable ICU 2.0
 */

//--public final class MathContext implements java.io.Serializable{
 //--private static final java.lang.String $0="MathContext.nrx";

 /* ----- Constructors ----- */
 
 /**
  * Constructs a new <code>MathContext</code> with a specified
  * precision.
  * The other settings are set to the default values
  * (see {@link #DEFAULT}).
  *
  * An <code>IllegalArgumentException</code> is thrown if the
  * <code>setdigits</code> parameter is out of range
  * (&lt;0 or &gt;999999999).
  *
  * @param setdigits     The <code>int</code> digits setting
  *                      for this <code>MathContext</code>.
  * @throws IllegalArgumentException parameter out of range.
  * @stable ICU 2.0
  */
 
 //--public MathContext(int setdigits){
 //-- this(setdigits,DEFAULT_FORM,DEFAULT_LOSTDIGITS,DEFAULT_ROUNDINGMODE);
 //-- return;}

 
 /**
  * Constructs a new <code>MathContext</code> with a specified
  * precision and form.
  * The other settings are set to the default values
  * (see {@link #DEFAULT}).
  *
  * An <code>IllegalArgumentException</code> is thrown if the
  * <code>setdigits</code> parameter is out of range
  * (&lt;0 or &gt;999999999), or if the value given for the
  * <code>setform</code> parameter is not one of the appropriate
  * constants.
  *
  * @param setdigits     The <code>int</code> digits setting
  *                      for this <code>MathContext</code>.
  * @param setform       The <code>int</code> form setting
  *                      for this <code>MathContext</code>.
  * @throws IllegalArgumentException parameter out of range.
  * @stable ICU 2.0
  */
 
 //--public MathContext(int setdigits,int setform){
 //-- this(setdigits,setform,DEFAULT_LOSTDIGITS,DEFAULT_ROUNDINGMODE);
 //-- return;}

 /**
  * Constructs a new <code>MathContext</code> with a specified
  * precision, form, and lostDigits setting.
  * The roundingMode setting is set to its default value
  * (see {@link #DEFAULT}).
  *
  * An <code>IllegalArgumentException</code> is thrown if the
  * <code>setdigits</code> parameter is out of range
  * (&lt;0 or &gt;999999999), or if the value given for the
  * <code>setform</code> parameter is not one of the appropriate
  * constants.
  *
  * @param setdigits     The <code>int</code> digits setting
  *                      for this <code>MathContext</code>.
  * @param setform       The <code>int</code> form setting
  *                      for this <code>MathContext</code>.
  * @param setlostdigits The <code>boolean</code> lostDigits
  *                      setting for this <code>MathContext</code>.
  * @throws IllegalArgumentException parameter out of range.
  * @stable ICU 2.0
  */
 
 //--public MathContext(int setdigits,int setform,boolean setlostdigits){
 //-- this(setdigits,setform,setlostdigits,DEFAULT_ROUNDINGMODE);
 //-- return;}

 /**
  * Constructs a new <code>MathContext</code> with a specified
  * precision, form, lostDigits, and roundingMode setting.
  *
  * An <code>IllegalArgumentException</code> is thrown if the
  * <code>setdigits</code> parameter is out of range
  * (&lt;0 or &gt;999999999), or if the value given for the
  * <code>setform</code> or <code>setroundingmode</code> parameters is
  * not one of the appropriate constants.
  *
  * @param setdigits       The <code>int</code> digits setting
  *                        for this <code>MathContext</code>.
  * @param setform         The <code>int</code> form setting
  *                        for this <code>MathContext</code>.
  * @param setlostdigits   The <code>boolean</code> lostDigits
  *                        setting for this <code>MathContext</code>.
  * @param setroundingmode The <code>int</code> roundingMode setting
  *                        for this <code>MathContext</code>.
  * @throws IllegalArgumentException parameter out of range.
  * @stable ICU 2.0
  */
 
 //--public MathContext(int setdigits,int setform,boolean setlostdigits,int setroundingmode){super();
 egl.javascript.MathContext = function cons() {
  //-- members
  this.digits = 0;
  this.form = 0; // values for this must fit in a byte
  this.lostDigits = false;
  this.roundingMode = 0;

  //-- overloaded ctor
  var setform = this.DEFAULT_FORM;
  var setlostdigits = this.DEFAULT_LOSTDIGITS;
  var setroundingmode = this.DEFAULT_ROUNDINGMODE;
  if (arguments.length == 4)
   {
    setform = arguments[1];
    setlostdigits = arguments[2];
    setroundingmode = arguments[3];
   }
  else if (arguments.length == 3)
   {
    setform = arguments[1];
    setlostdigits = arguments[2];
   }
  else if (arguments.length == 2)
   {
    setform = arguments[1];
   }
  else if (arguments.length != 1)
   {
    throw egl.createRuntimeException( "CRRUI2111E", [arguments.length] );
   }
  var setdigits = arguments[0];
  
  
  // set values, after checking
  if (setdigits!=this.DEFAULT_DIGITS) 
   {
    if (setdigits<this.MIN_DIGITS) 
     throw egl.createRuntimeException( "CRRUI2112E", [ setdigits ]);
    if (setdigits>this.MAX_DIGITS) 
     throw egl.createRuntimeException( "CRRUI2113E", [ setdigits ]);
   }
  {/*select*/
  if (setform==this.SCIENTIFIC)
   ; // [most common]
  else if (setform==this.ENGINEERING)
   ;
  else if (setform==this.PLAIN)
   ;
  else{
   throw egl.createRuntimeException( "CRRUI2114E", [ setform ]);
  }
  }
  if ((!(this.isValidRound(setroundingmode)))) 
     throw egl.createRuntimeException( "CRRUI2115E", [ setroundingmode ]);
  this.digits=setdigits;
  this.form=setform;
  this.lostDigits=setlostdigits; // [no bad value possible]
  this.roundingMode=setroundingmode;
  return;}


 //-- methods
  /**
  * Returns the digits setting.
  * This value is always non-negative.
  *
  * @return an <code>int</code> which is the value of the digits
  *         setting
  * @stable ICU 2.0
  */
 
 //--public int getDigits(){
egl.javascript.MathContext.prototype.getDigits = function() {
  return this.digits;
  };

/**
 * Returns the form setting.
 * This will be one of
 * {@link #ENGINEERING},
 * {@link #PLAIN}, or
 * {@link #SCIENTIFIC}.
 *
 * @return an <code>int</code> which is the value of the form setting
 * @stable ICU 2.0
 */

//--public int getForm(){
egl.javascript.MathContext.prototype.getForm = function() {
 return this.form;
 };
 
 /**
  * Returns the lostDigits setting.
  * This will be either <code>true</code> (enabled) or
  * <code>false</code> (disabled).
  *
  * @return a <code>boolean</code> which is the value of the lostDigits
  *           setting
  * @stable ICU 2.0
  */
 
 //--public boolean getLostDigits(){
 egl.javascript.MathContext.prototype.getLostDigits = function() {
  return this.lostDigits;
  };

  /**
   * Returns the roundingMode setting.
   * This will be one of
   * {@link  #ROUND_CEILING},
   * {@link  #ROUND_DOWN},
   * {@link  #ROUND_FLOOR},
   * {@link  #ROUND_HALF_DOWN},
   * {@link  #ROUND_HALF_EVEN},
   * {@link  #ROUND_HALF_UP},
   * {@link  #ROUND_UNNECESSARY}, or
   * {@link  #ROUND_UP}.
   *
   * @return an <code>int</code> which is the value of the roundingMode
   *         setting
   * @stable ICU 2.0
   */
  
  //--public int getRoundingMode(){
  egl.javascript.MathContext.prototype.getRoundingMode = function() {
   return this.roundingMode;
   };
 
   /** Returns the <code>MathContext</code> as a readable string.
    * The <code>String</code> returned represents the settings of the
    * <code>MathContext</code> object as four blank-delimited words
    * separated by a single blank and with no leading or trailing blanks,
    * as follows:
    * <ol>
    * <li>
    * <code>digits=</code>, immediately followed by
    * the value of the digits setting as a numeric word.
    * <li>
    * <code>form=</code>, immediately followed by
    * the value of the form setting as an uppercase word
    * (one of <code>SCIENTIFIC</code>, <code>PLAIN</code>, or
    * <code>ENGINEERING</code>).
    * <li>
    * <code>lostDigits=</code>, immediately followed by
    * the value of the lostDigits setting
    * (<code>1</code> if enabled, <code>0</code> if disabled).
    * <li>
    * <code>roundingMode=</code>, immediately followed by
    * the value of the roundingMode setting as a word.
    * This word will be the same as the name of the corresponding public
    * constant.
    * </ol>
    * <p>
    * For example:
    * <br><code>
    * digits=9 form=SCIENTIFIC lostDigits=0 roundingMode=ROUND_HALF_UP
    * </code>
    * <p>
    * Additional words may be appended to the result of
    * <code>toString</code> in the future if more properties are added
    * to the class.
    *
    * @return a <code>String</code> representing the context settings.
    * @stable ICU 2.0
    */
   
   //--public java.lang.String toString(){
egl.javascript.MathContext.prototype.toString = function() {
    //--java.lang.String formstr=null;
    var formstr=null;
    //--int r=0;
    var r=0;
    //--java.lang.String roundword=null;
    var roundword=null;
    {/*select*/
    if (this.form==this.SCIENTIFIC)
     formstr="SCIENTIFIC";
    else if (this.form==this.ENGINEERING)
     formstr="ENGINEERING";
    else{
     formstr="PLAIN";/* form=PLAIN */
    }
    }
    {var $1=this.ROUNDS.length;r=0;r:for(;$1>0;$1--,r++){
     if (this.roundingMode==this.ROUNDS[r]) 
      {
       roundword=this.ROUNDWORDS[r];
       break r;
      }
     }
    }/*r*/
    return "digits="+this.digits+" "+"form="+formstr+" "+"lostDigits="+(this.lostDigits?"1":"0")+" "+"roundingMode="+roundword;
};

/* <sgml> Test whether round is valid. </sgml> */
// This could be made shared for use by BigDecimal for setScale.

//--private static boolean isValidRound(int testround){
egl.javascript.MathContext.prototype.isValidRound = function(testround) {
 //--int r=0;
 var r=0;
 {var $2=this.ROUNDS.length;r=0;r:for(;$2>0;$2--,r++){
  if (testround==this.ROUNDS[r]) 
   return true;
  }
 }/*r*/
 return false;
 }


 
 /* ----- Properties ----- */
 /* properties public constant */
 /**
  * Plain (fixed point) notation, without any exponent.
  * Used as a setting to control the form of the result of a
  * <code>BigDecimal</code> operation.
  * A zero result in plain form may have a decimal part of one or
  * more zeros.
  *
  * @see #ENGINEERING
  * @see #SCIENTIFIC
  * @stable ICU 2.0
  */
 //--public static final int PLAIN=0; // [no exponent]
 egl.javascript.MathContext.prototype.PLAIN=0; // [no exponent]
 
 /**
  * Standard floating point notation (with scientific exponential
  * format, where there is one digit before any decimal point).
  * Used as a setting to control the form of the result of a
  * <code>BigDecimal</code> operation.
  * A zero result in plain form may have a decimal part of one or
  * more zeros.
  *
  * @see #ENGINEERING
  * @see #PLAIN
  * @stable ICU 2.0
  */
 //--public static final int SCIENTIFIC=1; // 1 digit before .
 egl.javascript.MathContext.prototype.SCIENTIFIC=1; // 1 digit before .
 
 /**
  * Standard floating point notation (with engineering exponential
  * format, where the power of ten is a multiple of 3).
  * Used as a setting to control the form of the result of a
  * <code>BigDecimal</code> operation.
  * A zero result in plain form may have a decimal part of one or
  * more zeros.
  *
  * @see #PLAIN
  * @see #SCIENTIFIC
  * @stable ICU 2.0
  */
 //--public static final int ENGINEERING=2; // 1-3 digits before .
 egl.javascript.MathContext.prototype.ENGINEERING=2; // 1-3 digits before .
 
 // The rounding modes match the original BigDecimal class values
 /**
  * Rounding mode to round to a more positive number.
  * Used as a setting to control the rounding mode used during a
  * <code>BigDecimal</code> operation.
  * <p>
  * If any of the discarded digits are non-zero then the result
  * should be rounded towards the next more positive digit.
  * @stable ICU 2.0
  */
 //--public static final int ROUND_CEILING=2;
 egl.javascript.MathContext.prototype.ROUND_CEILING=2;
 
 /**
  * Rounding mode to round towards zero.
  * Used as a setting to control the rounding mode used during a
  * <code>BigDecimal</code> operation.
  * <p>
  * All discarded digits are ignored (truncated).  The result is
  * neither incremented nor decremented.
  * @stable ICU 2.0
  */
 //--public static final int ROUND_DOWN=1;
 egl.javascript.MathContext.prototype.ROUND_DOWN=1;
 
 /**
  * Rounding mode to round to a more negative number.
  * Used as a setting to control the rounding mode used during a
  * <code>BigDecimal</code> operation.
  * <p>
  * If any of the discarded digits are non-zero then the result
  * should be rounded towards the next more negative digit.
  * @stable ICU 2.0
  */
 //--public static final int ROUND_FLOOR=3;
 egl.javascript.MathContext.prototype.ROUND_FLOOR=3;
 
 /**
  * Rounding mode to round to nearest neighbor, where an equidistant
  * value is rounded down.
  * Used as a setting to control the rounding mode used during a
  * <code>BigDecimal</code> operation.
  * <p>
  * If the discarded digits represent greater than half (0.5 times)
  * the value of a one in the next position then the result should be
  * rounded up (away from zero).  Otherwise the discarded digits are
  * ignored.
  * @stable ICU 2.0
  */
 //--public static final int ROUND_HALF_DOWN=5;
 egl.javascript.MathContext.prototype.ROUND_HALF_DOWN=5;
 
 /**
  * Rounding mode to round to nearest neighbor, where an equidistant
  * value is rounded to the nearest even neighbor.
  * Used as a setting to control the rounding mode used during a
  * <code>BigDecimal</code> operation.
  * <p>
  * If the discarded digits represent greater than half (0.5 times)
  * the value of a one in the next position then the result should be
  * rounded up (away from zero).  If they represent less than half,
  * then the result should be rounded down.
  * <p>
  * Otherwise (they represent exactly half) the result is rounded
  * down if its rightmost digit is even, or rounded up if its
  * rightmost digit is odd (to make an even digit).
  * @stable ICU 2.0
  */
 //--public static final int ROUND_HALF_EVEN=6;
 egl.javascript.MathContext.prototype.ROUND_HALF_EVEN=6;
 
 /**
  * Rounding mode to round to nearest neighbor, where an equidistant
  * value is rounded up.
  * Used as a setting to control the rounding mode used during a
  * <code>BigDecimal</code> operation.
  * <p>
  * If the discarded digits represent greater than or equal to half
  * (0.5 times) the value of a one in the next position then the result
  * should be rounded up (away from zero).  Otherwise the discarded
  * digits are ignored.
  * @stable ICU 2.0
  */
 //--public static final int ROUND_HALF_UP=4;
 egl.javascript.MathContext.prototype.ROUND_HALF_UP=4;
 
 /**
  * Rounding mode to assert that no rounding is necessary.
  * Used as a setting to control the rounding mode used during a
  * <code>BigDecimal</code> operation.
  * <p>
  * Rounding (potential loss of information) is not permitted.
  * If any of the discarded digits are non-zero then an
  * <code>ArithmeticException</code> should be thrown.
  * @stable ICU 2.0
  */
 //--public static final int ROUND_UNNECESSARY=7;
 egl.javascript.MathContext.prototype.ROUND_UNNECESSARY=7;
 
 /**
  * Rounding mode to round away from zero.
  * Used as a setting to control the rounding mode used during a
  * <code>BigDecimal</code> operation.
  * <p>
  * If any of the discarded digits are non-zero then the result will
  * be rounded up (away from zero).
  * @stable ICU 2.0
  */
 //--public static final int ROUND_UP=0;
 egl.javascript.MathContext.prototype.ROUND_UP=0;
 
 
 /* properties shared */
 /**
  * The number of digits (precision) to be used for an operation.
  * A value of 0 indicates that unlimited precision (as many digits
  * as are required) will be used.
  * <p>
  * The {@link BigDecimal} operator methods use this value to
  * determine the precision of results.
  * Note that leading zeros (in the integer part of a number) are
  * never significant.
  * <p>
  * <code>digits</code> will always be non-negative.
  *
  * @serial
  */
 //--int digits;
 
 /**
  * The form of results from an operation.
  * <p>
  * The {@link BigDecimal} operator methods use this value to
  * determine the form of results, in particular whether and how
  * exponential notation should be used.
  *
  * @see #ENGINEERING
  * @see #PLAIN
  * @see #SCIENTIFIC
  * @serial
  */
 //--int form; // values for this must fit in a byte
 
 /**
  * Controls whether lost digits checking is enabled for an
  * operation.
  * Set to <code>true</code> to enable checking, or
  * to <code>false</code> to disable checking.
  * <p>
  * When enabled, the {@link BigDecimal} operator methods check
  * the precision of their operand or operands, and throw an
  * <code>ArithmeticException</code> if an operand is more precise
  * than the digits setting (that is, digits would be lost).
  * When disabled, operands are rounded to the specified digits.
  *
  * @serial
  */
 //--boolean lostDigits;
 
 /**
  * The rounding algorithm to be used for an operation.
  * <p>
  * The {@link BigDecimal} operator methods use this value to
  * determine the algorithm to be used when non-zero digits have to
  * be discarded in order to reduce the precision of a result.
  * The value must be one of the public constants whose name starts
  * with <code>ROUND_</code>.
  *
  * @see #ROUND_CEILING
  * @see #ROUND_DOWN
  * @see #ROUND_FLOOR
  * @see #ROUND_HALF_DOWN
  * @see #ROUND_HALF_EVEN
  * @see #ROUND_HALF_UP
  * @see #ROUND_UNNECESSARY
  * @see #ROUND_UP
  * @serial
  */
 //--int roundingMode;
 
 /* properties private constant */
 // default settings
 //--private static final int DEFAULT_FORM=SCIENTIFIC;
 //--private static final int DEFAULT_DIGITS=9;
 //--private static final boolean DEFAULT_LOSTDIGITS=false;
 //--private static final int DEFAULT_ROUNDINGMODE=ROUND_HALF_UP;
 egl.javascript.MathContext.prototype.DEFAULT_FORM=egl.javascript.MathContext.prototype.SCIENTIFIC;
 egl.javascript.MathContext.prototype.DEFAULT_DIGITS=9;
 egl.javascript.MathContext.prototype.DEFAULT_LOSTDIGITS=false;
 egl.javascript.MathContext.prototype.DEFAULT_ROUNDINGMODE=egl.javascript.MathContext.prototype.ROUND_HALF_DOWN;
 
 /* properties private constant */
 
 //--private static final int MIN_DIGITS=0; // smallest value for DIGITS.
 //--private static final int MAX_DIGITS=999999999; // largest value for DIGITS.  If increased,
 egl.javascript.MathContext.prototype.MIN_DIGITS=0; // smallest value for DIGITS.
 egl.javascript.MathContext.prototype.MAX_DIGITS=999999999; // largest value for DIGITS.  If increased,
 // the BigDecimal class may need update.
 // list of valid rounding mode values, most common two first
 //--private static final int ROUNDS[]=new int[]{ROUND_HALF_UP,ROUND_UNNECESSARY,ROUND_CEILING,ROUND_DOWN,ROUND_FLOOR,ROUND_HALF_DOWN,ROUND_HALF_EVEN,ROUND_UP};
 egl.javascript.MathContext.prototype.ROUNDS=new Array(egl.javascript.MathContext.prototype.ROUND_HALF_UP,egl.javascript.MathContext.prototype.ROUND_UNNECESSARY,egl.javascript.MathContext.prototype.ROUND_CEILING,egl.javascript.MathContext.prototype.ROUND_DOWN,egl.javascript.MathContext.prototype.ROUND_FLOOR,egl.javascript.MathContext.prototype.ROUND_HALF_DOWN,egl.javascript.MathContext.prototype.ROUND_HALF_EVEN,egl.javascript.MathContext.prototype.ROUND_UP);
 
 
 //--private static final java.lang.String ROUNDWORDS[]=new java.lang.String[]{"ROUND_HALF_UP","ROUND_UNNECESSARY","ROUND_CEILING","ROUND_DOWN","ROUND_FLOOR","ROUND_HALF_DOWN","ROUND_HALF_EVEN","ROUND_UP"}; // matching names of the ROUNDS values
 egl.javascript.MathContext.prototype.ROUNDWORDS=new Array("ROUND_HALF_UP","ROUND_UNNECESSARY","ROUND_CEILING","ROUND_DOWN","ROUND_FLOOR","ROUND_HALF_DOWN","ROUND_HALF_EVEN","ROUND_UP"); // matching names of the ROUNDS values
 
 
 
 
 /* properties private constant unused */
 
 // Serialization version
 //--private static final long serialVersionUID=7163376998892515376L;
 
 /* properties public constant */
 /**
  * A <code>MathContext</code> object initialized to the default
  * settings for general-purpose arithmetic.  That is,
  * <code>digits=9 form=SCIENTIFIC lostDigits=false
  * roundingMode=ROUND_HALF_UP</code>.
  *
  * @see #SCIENTIFIC
  * @see #ROUND_HALF_UP
  * @stable ICU 2.0
  */
 //--public static final com.ibm.icu.math.MathContext DEFAULT=new com.ibm.icu.math.MathContext(DEFAULT_DIGITS,DEFAULT_FORM,DEFAULT_LOSTDIGITS,DEFAULT_ROUNDINGMODE);
 egl.javascript.MathContext.prototype.DEFAULT=new egl.javascript.MathContext(egl.javascript.MathContext.prototype.DEFAULT_DIGITS,egl.javascript.MathContext.prototype.DEFAULT_FORM,egl.javascript.MathContext.prototype.DEFAULT_LOSTDIGITS,egl.javascript.MathContext.prototype.DEFAULT_ROUNDINGMODE);

 
 
 
