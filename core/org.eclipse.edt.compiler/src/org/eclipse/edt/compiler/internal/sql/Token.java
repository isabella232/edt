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
package org.eclipse.edt.compiler.internal.sql;


import java.io.Serializable;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLPrimeToken;

/**
 * Insert the type's description here.
 * Creation date: (6/12/2001 2:07:02 PM)
 * @author: Paul R. Harmon
 */
public abstract class Token implements Serializable {
	public java.lang.String string;
	private int startOffset;
	private int endOffset;
	/**
	 * Token constructor comment.
	 */
	public Token() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/12/2001 3:10:31 PM)
	 * @param string java.lang.String
	 */
	public Token(String string) {
		super();
		this.string = string;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/13/2001 2:23:34 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.sql.Token
	 * @param string java.lang.String
	 * @param type int
	 * @param tokenizer org.eclipse.edt.compiler.internal.compiler.sql.StatmentTokenizer
	 */
	public static Token createToken(EGLPrimeToken token, int type, EGLSQLGenerationTokens tokenizer, EGLPrimeToken prevToken) {
		return createToken(token, type, tokenizer, prevToken, 0,0);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/13/2001 2:23:34 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.sql.Token
	 * @param string java.lang.String
	 * @param type int
	 * @param tokenizer org.eclipse.edt.compiler.internal.compiler.sql.StatmentTokenizer
	 */
	public static Token createToken(EGLPrimeToken token, int type, EGLSQLGenerationTokens tokenizer, EGLPrimeToken prevToken, int startOff, int endOff) {

		String string = token.getText(); 
		IDataBinding sqlIOObject = tokenizer.getSQLIOObject();
		boolean convertToken = tokenizer.isConvertNameTokenToTableNameToken();

		Token newToken = null;
		switch (type) {
			case EGLSQLGenerationTokens.TYPE_STRING :
				{
					newToken = new StringToken(token);
					break;
				}
			case EGLSQLGenerationTokens.TYPE_SELECT_NAME :
				{
					newToken = new SelectNameToken(token.getText(), token.getOffset());
					
					break;
				}				
			case EGLSQLGenerationTokens.TYPE_WHERE_CURRENT_OF :
				{
					newToken = new WhereCurrentOfToken(token); 
					break;
				}				
			case EGLSQLGenerationTokens.TYPE_INPUT :
				{
//					if (convertToken && (tableName(string, sqlIOObject))) {
//						newToken = new TableNameHostVariableToken(string, sqlIOObject);
//						if (prevToken != null
//							&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
//							((ItemNameToken)(newToken)).setFollowsLike();
//						tokenizer.setConvertNameTokenToTableNameToken(false);
//					} else {
						newToken = new InputHostVariableToken(string, sqlIOObject);
						if (prevToken != null
							&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
							((ItemNameToken)(newToken)).setFollowsLike();						
//					}
					break;
				}
			case EGLSQLGenerationTokens.TYPE_OUTPUT :
				{
//					if (convertToken && (tableName(string, sqlIOObject))) {
//						newToken = new TableNameHostVariableToken(string, sqlIOObject);
//						if (prevToken != null
//							&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
//							((ItemNameToken)(newToken)).setFollowsLike();						
//						tokenizer.setConvertNameTokenToTableNameToken(false);
//					} else {
						newToken = new OutputHostVariableToken(string, sqlIOObject);
						if (prevToken != null
							&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
							((ItemNameToken)(newToken)).setFollowsLike();						
//					}
					break;
				}
			case EGLSQLGenerationTokens.TYPE_TABLE :
				{
					newToken = new TableNameHostVariableToken(string, sqlIOObject);
					if (prevToken != null
						&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
						((ItemNameToken)(newToken)).setFollowsLike();
					break;
				}
			case EGLSQLGenerationTokens.TYPE_QUOTED_STRING :
				{
					newToken = new QuotedStringToken(string);
					break;
				}
			default :
				{

				}
		}
		if (startOff != 0 && endOff != 0) {
			newToken.setStartOffset(startOff);
			newToken.setEndOffset(endOff);
		}
		return newToken;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/13/2001 2:23:34 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.sql.Token
	 * @param string java.lang.String
	 * @param type int
	 * @param tokenizer org.eclipse.edt.compiler.internal.compiler.sql.StatmentTokenizer
	 */
	public static Token createToken(String string, int type, EGLSQLGenerationTokens tokenizer, EGLPrimeToken prevToken) {
		return createToken(string, type, tokenizer, prevToken,0,0);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/13/2001 2:23:34 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.sql.Token
	 * @param string java.lang.String
	 * @param type int
	 * @param tokenizer org.eclipse.edt.compiler.internal.compiler.sql.StatmentTokenizer
	 */
	public static Token createToken(String string, int type, EGLSQLGenerationTokens tokenizer, EGLPrimeToken prevToken, int startOff, int endOff) {

		IDataBinding sqlIOObject = tokenizer.getSQLIOObject();
		boolean convertToken = tokenizer.isConvertNameTokenToTableNameToken();

		Token newToken = null;
		switch (type) {
			case EGLSQLGenerationTokens.TYPE_STRING :
				{
					newToken = new StringToken(string);
					break;
				}
			case EGLSQLGenerationTokens.TYPE_SELECT_NAME :
				{
					newToken = new SelectNameToken(string, 0);
					break;
				}				
			case EGLSQLGenerationTokens.TYPE_WHERE_CURRENT_OF :
				{
					newToken = new WhereCurrentOfToken(string);
					break;
				}				
			case EGLSQLGenerationTokens.TYPE_INPUT :
				{
//					if (convertToken && (tableName(string, sqlIOObject))) {
//						newToken = new TableNameHostVariableToken(string, sqlIOObject);
//						if (prevToken != null
//							&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
//							((ItemNameToken)(newToken)).setFollowsLike();						
//						tokenizer.setConvertNameTokenToTableNameToken(false);
//					} else {
						newToken = new InputHostVariableToken(string, sqlIOObject);
						if (prevToken != null
							&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
							((ItemNameToken)(newToken)).setFollowsLike();						
//					}
					break;
				}
			case EGLSQLGenerationTokens.TYPE_OUTPUT :
				{
//					if (convertToken && (tableName(string, sqlIOObject))) {
//						newToken = new TableNameHostVariableToken(string, sqlIOObject);
//						if (prevToken != null
//							&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
//							((ItemNameToken)(newToken)).setFollowsLike();						
//						tokenizer.setConvertNameTokenToTableNameToken(false);
//					} else {
						newToken = new OutputHostVariableToken(string, sqlIOObject);
						if (prevToken != null
							&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
							((ItemNameToken)(newToken)).setFollowsLike();						
//					}
					break;
				}
			case EGLSQLGenerationTokens.TYPE_TABLE :
				{
					newToken = new TableNameHostVariableToken(string, sqlIOObject);
					if (prevToken != null
						&& prevToken.getText().equalsIgnoreCase("like") )  //$NON-NLS-1$
						((ItemNameToken)(newToken)).setFollowsLike();
					break;
				}
			case EGLSQLGenerationTokens.TYPE_QUOTED_STRING :
				{
					newToken = new QuotedStringToken(string);
					break;
				}
			default :
				{

				}
		}
		if (startOff != 0 && endOff != 0) {
			newToken.setStartOffset(startOff);
			newToken.setEndOffset(endOff);
		}
		return newToken;
	}	
	/**
	 * Insert the method's description here.
	 * 
	 * @return boolean
	 */
	public boolean isHostVariableToken() {
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/2001 12:12:24 PM)
	 * @return boolean
	 */
	public boolean isInputHostVariableToken() {
		return false;
	}
	/**
	 * Insert the method's description here.
	 * 
	 * @return boolean
	 */
	public boolean isNonQuotedStringToken() {
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/2001 12:12:24 PM)
	 * @return boolean
	 */
	public boolean isOutputHostVariableToken() {
		return false;
	}
	/**
	 * Insert the method's description here.
	 * 
	 * @return boolean
	 */
	public boolean isQuotedStringToken() {
		return false;
	}
	public boolean isSelectNameToken() {
		return false; 
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/2001 12:12:24 PM)
	 * @return boolean
	 */
	public boolean isStringToken() {
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/2001 12:12:24 PM)
	 * @return boolean
	 */
	public boolean isTableNameHostVariableToken() {
		return false;
	}
	public boolean isWhereCurrentOfToken() {
		return false;
	}	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/2001 3:00:52 PM)
	 * @return boolean
	 */
	public static boolean tableName(String string, IDataBinding sqlIOObject) {
		if (sqlIOObject != null && sqlIOObject != IBinding.NOT_FOUND_BINDING) {
		    IAnnotationBinding annotationBinding = getField(sqlIOObject.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord"), IEGLConstants.PROPERTY_TABLENAMES);
		    if (annotationBinding == null) {
		        return false;
		    }
		    
		    String[][] value = (String[][])annotationBinding.getValue();
		    if (value == null) {
		        return false;
		    }
		    for (int i = 0; i < value.length; i++) {
                if (string.equalsIgnoreCase(value[i][0])) {
                    return true;
                }
            }
		}

		return false;
	}
	
	public abstract String getSQLString();
	
	public int getStartOffset() {
		return startOffset;
	}
	
	public int getEndOffset() {
		return endOffset;
	}
	
	public void setStartOffset(int startOff) {
		startOffset= startOff;
	}
	
	public void setEndOffset(int endOff) {
		endOffset= endOff;
	}	
	
	public boolean isItemNameToken() {
		return false;
	}
	
	private static String stripQuotes(String aString) {
	    return aString.substring(1, aString.length() - 1);
	}
	private static boolean isQuotedTableName(String aString) {
	    if (aString == null || aString.length() < 5) {
	        return false;
	    }
	    if (!aString.startsWith("\"")) {
	        return false;
	    }
	    if (!aString.endsWith("\"")) {
	        return false;
	    }
	    aString = aString.substring(1, aString.length() - 1);
	    boolean colonFound = false;
	    boolean atFound = false;
	    int atIndex = -1;
	    int end = aString.length();
	    for (int i = 0; i < end; i++) {
	        char curChar = aString.charAt(i);
            if (':' == curChar) {
                if (colonFound || i == 0 || i == end-1 || (atIndex == i-1)) {
                    return false;
                }
                colonFound = true;
                continue;
                
            }
            if ('@' == curChar) {
                if (colonFound || atFound || i == 0) {
                    return false;
                }
                atFound = true;
                atIndex = i;
                continue;
            }
            if ((curChar >= '0' && curChar <= '9') || (curChar >= 'A' && curChar <= 'Z') ) {
                
            }
            else {
                return false;
            }
            
         }
	    	
	    
	    return colonFound;

	} 
    public java.lang.String getString() {
        return string;
    }
    
    private static IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
    	IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
}
