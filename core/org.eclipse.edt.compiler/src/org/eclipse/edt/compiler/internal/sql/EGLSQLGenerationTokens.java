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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.EGLSQLKeywordHandler;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLPrimeToken;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLSQLClauseTree;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLSQLParser;


/**
 * @author dollar
 *
 * Convert the SQL tokens as parsed into the format that is
 * needed by the generators
 */
public class EGLSQLGenerationTokens implements Serializable {

	private List tokens = null;

	private boolean concatenateStringTokens = true;
	private boolean convertNameTokenToTableNameToken = false;
	private IDataBinding sqlIOObject;
	private transient EGLSQLParser parser;
    String[] tableVariableNames;
	private boolean convertTableNameVariables = false;

	public final static int TYPE_INPUT = 1;
	public final static int TYPE_OUTPUT = 2;
	public final static int TYPE_STRING = 3;
	public final static int TYPE_TABLE = 4;
	public final static int TYPE_QUOTED_STRING = 5;
	public final static int TYPE_WHERE_CURRENT_OF = 6;
	public final static int TYPE_SELECT_NAME = 7;

	public final static int STATE_CLEAR = 0;
	public final static int STATE_ABOUTTOREADMULTIPLEOUTPUT = 1;
	public final static int STATE_ABOUTTOREADMULTIPLETABLE = 2;
	public final static int STATE_ABOUTTOREADSINGLETABLE = 3;
	public final static int STATE_DELETE = 4;
	public final static int STATE_INSERT = 5;
	public final static int STATE_MULTIPLEINPUT = 6;
	public final static int STATE_MULTIPLEOUTPUT = 7;
	public final static int STATE_MULTIPLETABLE = 8;
	public final static int STATE_SINGLETABLE = 9;
	
	public int state = 0;
	
	private Set tableHostVarSet;

	/**
	 * Constructor
	*/
	public EGLSQLGenerationTokens() {
		super();
	}

	/**
		 * Constructor
		*/
	public EGLSQLGenerationTokens(EGLSQLParser parser) {
		this(parser, null, false);
	}

	/**
	 * Constructor
	 */
	public EGLSQLGenerationTokens(EGLSQLParser parser, IDataBinding sqlIOObject) {
		this(parser, sqlIOObject, false);
		//FRIEDA - assuming concatenation for now
		//FRIEDA - Do I need a get all tokens that runs all clauses? 
	}

	/**
	* Constructor
	*/
	public EGLSQLGenerationTokens(EGLSQLParser parser, String[] tableVariableNames) {
		this(parser, null, false);
		this.tableVariableNames = tableVariableNames;
	}

	/**
	 * Constructor
	 */
	public EGLSQLGenerationTokens(EGLSQLParser parser, IDataBinding sqlIOObject, boolean convertNames) {
		this();
		this.parser = parser;
		this.sqlIOObject = sqlIOObject;
		this.convertNameTokenToTableNameToken = convertNames;
	}

	public Token[] getAllTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getAllTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getSelectTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getSelectTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getIntoTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getIntoTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getCallTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getCallTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getColumnsTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getColumnsTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getFromTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getFromTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getValuesTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getValuesTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getUpdateTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getUpdateTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}
	
	public Token[] getDeleteTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getDeleteTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getSetTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getSetTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getHavingTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getHavingTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getWhereTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getWhereTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getWhereCurrentOfTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getWhereCurrentOfTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getOrderByTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getOrderByTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getGroupByTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getGroupByTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getInsertIntoTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getInsertIntoTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getForUpdateOfTokens() {
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getForUpdateOfTokens();
		state = STATE_CLEAR;
		return getTokens(primeTokens);
	}

	public Token[] getSelectNameTokens() {
		tokens = new ArrayList();
		if (parser.hasErrors())
			return null;
		EGLSQLClauseTree primeTokens = parser.getSelectTokens();
		if (primeTokens == null)
			return null;
		state = STATE_CLEAR;
		int start = 0;
		for (int ii = start; ii < primeTokens.size(); ii++) {
			if (primeTokens.getToken(ii).getType() == EGLPrimeToken.SELECT) {
				checkState(primeTokens.getToken(ii).getText());
				addToken(Token.createToken(primeTokens.getToken(ii), TYPE_STRING, this, null));
			} else if (primeTokens.getToken(ii).getType() == EGLPrimeToken.COMMA) {
				addToken(Token.createToken(primeTokens.getToken(ii), TYPE_STRING, this, null));
			} else if (ii > 0 && primeTokens.getToken(ii - 1).getType() == EGLPrimeToken.SELECT &&
					(primeTokens.getToken(ii).getText().equalsIgnoreCase(IEGLConstants.SQLKEYWORD_DISTINCT) ||
				     primeTokens.getToken(ii).getText().equalsIgnoreCase(IEGLConstants.SQLKEYWORD_ALL))) {
				addToken(Token.createToken(primeTokens.getToken(ii), TYPE_STRING, this, null));
			} else {
				checkState(primeTokens.getToken(ii).getText());
				// just deal with anything else as a delimited name 
				Token newToken = Token.createToken(primeTokens.getToken(ii), TYPE_SELECT_NAME, this, null);
				addSelectNameToken(newToken);
			}
		}
		return (Token[]) tokens.toArray(new Token[tokens.size()]);
	}
	/**
	 * @return org.eclipse.edt.compiler.internal.compiler.sql.Token
	 */
	private Token[] getTokens(EGLSQLClauseTree primeTokens) {

		EGLPrimeToken prevToken = null;
		EGLPrimeToken prevKeywordToken = null;
		tokens = new ArrayList();
		tableHostVarSet = null;
		Set sqlKeywords = EGLSQLKeywordHandler.getSQLKeywordNamesToLowerCaseAsSet();
        if (tableVariableNames==null && sqlIOObject != null && sqlIOObject != IBinding.NOT_FOUND_BINDING) {
            IAnnotationBinding annotationBinding = getField(sqlIOObject.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord"), IEGLConstants.PROPERTY_TABLENAMEVARIABLES);
            if (annotationBinding != null) {
                if (annotationBinding.getValue() != null) {
                    String[][] value = (String[][]) annotationBinding.getValue();
                    tableVariableNames = new String[value.length];
                    for (int i = 0; i < value.length; i++) {
                        tableVariableNames[i] = value[i][0];
                    }
                }
            }
        }    
		if (primeTokens == null) {
			return null;
		} else {
			int start = 0;

			for (int ii = start; ii < primeTokens.size(); ii++) {
				if (((EGLPrimeToken) (primeTokens.getToken(ii))).getType() == EGLPrimeToken.SQLCOMMENT) { //skip comments
				} else if (
					((EGLPrimeToken) (primeTokens.getToken(ii))).getType() == EGLPrimeToken.HOST_VAR_COLON) {					
					//process the name that follows, make it all one token
					checkState(primeTokens.getToken(ii).getText());
					StringBuffer hostVar = new StringBuffer(primeTokens.getToken(ii).getText());
					int startOffset = primeTokens.getToken(ii).getOffset();
					int endOffset = 0;
					ii++;
					while (ii < primeTokens.size() && primeTokens.getToken(ii).isHostVar()) {
						if ("-".equals(primeTokens.getToken(ii).getText())) {
							hostVar.append(" - ");
							endOffset = primeTokens.getToken(ii).getOffset() + 2;
						}
						else {
							hostVar.append(primeTokens.getToken(ii).getText());
							endOffset = primeTokens.getToken(ii).getOffset() + primeTokens.getToken(ii).getText().length() - 1;
						}
						ii++;
					}
					ii = ii - 1; // account for nested looping and both incrementing the controller
					addToken(Token.createToken(hostVar.toString(), getType(), this, prevToken, startOffset, endOffset));
				} else if (primeTokens.getToken(ii).isDelimiter()) //delimiter
					{
					addToken(
						Token.createToken(
							primeTokens.getToken(ii),
							TYPE_STRING,
							this,
							prevToken,
							primeTokens.getToken(ii).getOffset(),
							primeTokens.getToken(ii).getOffset() + primeTokens.getToken(ii).getText().length() - 1));
				} else if (primeTokens.getToken(ii).isWhereCurrentOf()) { // process all tokens for the where current of clause
					// putting them all in one token	
					// need to createToken. then append to it
					// then addToken so rsi gets set correctly
					checkState(primeTokens.getToken(ii).getText());
					Token whereCurrentOf = Token.createToken(primeTokens.getToken(ii).getText(), TYPE_WHERE_CURRENT_OF, this, prevToken);
					whereCurrentOf.setStartOffset(primeTokens.getToken(ii).getOffset());
					ii++;
					while (ii < primeTokens.size() && primeTokens.getToken(ii).isWhereCurrentOf()) {
						((WhereCurrentOfToken) (whereCurrentOf)).append(new StringToken(primeTokens.getToken(ii)));
						ii++;
					}
					ii = ii - 1; // account for nested looping and both incrementing the controller
					whereCurrentOf.setEndOffset(primeTokens.getToken(ii).getOffset() + primeTokens.getToken(ii).getText().length() - 1);
					addToken(whereCurrentOf);
				} else {
					checkState(primeTokens.getToken(ii).getText());
					if (prevKeywordToken != null)
						if (prevKeywordToken.getText().equalsIgnoreCase(IEGLConstants.SQLKEYWORD_UPDATE)
							|| prevKeywordToken.getText().equalsIgnoreCase(IEGLConstants.SQLKEYWORD_INTO)
							|| prevKeywordToken.getText().equalsIgnoreCase(IEGLConstants.SQLKEYWORD_JOIN)
							|| prevKeywordToken.getText().equalsIgnoreCase(IEGLConstants.SQLKEYWORD_FROM))
							convertTableNameVariables = true;
                    if (convertTableNameVariables && tableVariableNames != null) {
						// on a clause that could have table name variables
						if (identifierMatchesTableNameVariable(primeTokens, ii)) {
							// could be multiple tokens
							StringBuffer hostVar = new StringBuffer(primeTokens.getToken(ii).getText());
							int startOffset = primeTokens.getToken(ii).getOffset();
							int endOffset = 0;
							ii++;
							while (ii < primeTokens.size() && primeTokens.getToken(ii).isHostVar()) {
								hostVar.append(primeTokens.getToken(ii).getText());
								endOffset = primeTokens.getToken(ii).getOffset() + primeTokens.getToken(ii).getText().length() - 1;
								ii++;
							}
							ii = ii - 1; // account for nested looping and both incrementing the controller
							addToken(
								Token.createToken(
									hostVar.toString(),
									EGLSQLGenerationTokens.TYPE_TABLE,
									this,
									prevToken,
									startOffset,
									endOffset));
						} else // just deal with anything else like it is a string	 
							addToken(
								Token.createToken(
									primeTokens.getToken(ii),
									getType(),
									this,
									prevToken,
									primeTokens.getToken(ii).getOffset(),
									primeTokens.getToken(ii).getOffset() + primeTokens.getToken(ii).getText().length() - 1));
					} else // just deal with anything else like it is a string	 
						addToken(
							Token.createToken(
								primeTokens.getToken(ii),
								getType(),
								this,
								prevToken,
								primeTokens.getToken(ii).getOffset(),
								primeTokens.getToken(ii).getOffset() + primeTokens.getToken(ii).getText().length() - 1));
					convertTableNameVariables = false;
				}
				prevToken = primeTokens.getToken(ii);
				if (sqlKeywords.contains(primeTokens.getToken(ii).getText().toLowerCase())) {
					prevKeywordToken = primeTokens.getToken(ii);
				}
			}
			return (Token[]) tokens.toArray(new Token[tokens.size()]);
		}
	}


	public boolean identifierMatchesTableNameVariable(EGLSQLClauseTree primeTokens, int index) {
		int startIndex = index;
		EGLPrimeToken currentToken = null;
		// first decide how many tokens make up the name that I am matching
		// to the sqlIOObject
		EGLSQLClauseTree tempTokens = new EGLSQLClauseTree();
		tempTokens.addToken(primeTokens.getToken(index));
		index++;
		if (index < primeTokens.size())
			currentToken = primeTokens.getToken(index);
		else
			currentToken = null;

		// This loop control looks wierd but is effective.  The problem is that if
		// I just loop while things are touching, then I have to special case the
		// situation where they had whitespace between the item and the '['.
		// Then if I find that things are touching the ']', I need to drop back
		// into the loop (which means repeating the code again.  I'd rather make
		// the loop control a little wierd.

		while ((currentToken != null) && (currentToken.getType() != EGLPrimeToken.R_PAREN) && (currentToken.getType() != EGLPrimeToken.COMMA) && (currentToken.getType() != EGLPrimeToken.EQUAL) && (currentToken.getType() != EGLPrimeToken.NOT_EQUAL) && (currentToken.getType() != EGLPrimeToken.GREATER) && (currentToken.getType() != EGLPrimeToken.GREATER_EQUAL) && (currentToken.getType() != EGLPrimeToken.LESS) && (currentToken.getType() != EGLPrimeToken.LESS_EQUAL) && (currentToken.getType() != EGLPrimeToken.BANG) && (currentToken.getType() != EGLPrimeToken.PLUS) && (currentToken.getType() != EGLPrimeToken.SPLAT) && (currentToken.getType() != EGLPrimeToken.MINUS) && (currentToken.getType() != EGLPrimeToken.PERCENT) && (currentToken.getType() != EGLPrimeToken.AND) && (currentToken.getType() != EGLPrimeToken.OR) && (currentToken.getType() != EGLPrimeToken.SQL_AND) && (currentToken.getType() != EGLPrimeToken.SQL_OR) && !(currentToken.getText().equals("/")) //$NON-NLS-1$
		&& ((currentToken.getType() == EGLPrimeToken.L_SQUARE) || // start of subscripts   OR
		 (((EGLPrimeToken) (tempTokens.getLastToken())).getOffset() // previous token
		+ ((EGLPrimeToken) (tempTokens.getLastToken())).getText().length() == // 'touches'
		 ((EGLPrimeToken) (currentToken)).getOffset())) // thisToken
		) {
			if (currentToken.getType() == EGLPrimeToken.L_SQUARE)
				// start of subscripts, take them all
				{
				while (currentToken != null && currentToken.getType() != EGLPrimeToken.R_SQUARE) /* just in case they left out the ']' */ {
					tempTokens.addToken(currentToken);
					index++;
					if (index < primeTokens.size())
						currentToken = primeTokens.getToken(index);
					else
						currentToken = null;
				}
				if (currentToken.getType() == EGLPrimeToken.R_SQUARE)
					tempTokens.addToken(currentToken);
			} else {
				tempTokens.addToken(currentToken);
				index++;
				if (index < primeTokens.size())
					currentToken = primeTokens.getToken(index);
				else
					currentToken = null;

			}
		}

		String variable = tempTokens.toStringNoWhiteSpace();	
		if (tableVariableNames == null) {
			return false;
		}
		String currentVariable = null;
		for (int jj = 0; jj < tableVariableNames.length; jj++) {
			currentVariable =  tableVariableNames[jj];
			if (variable.equalsIgnoreCase(currentVariable)) {
				// found name that matches so turn it into a host variable
				for (int kk = 0; kk < tempTokens.size(); kk++) {
					primeTokens.getToken(kk + startIndex).setHostVar();
				}
				return true;
			}
		}
		return false;

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 12:53:14 PM)
	 * @return org.eclipse.edt.compiler.internal.compiler.parts.SQLIOObject
	 */
	public IDataBinding getSQLIOObject() {
		return sqlIOObject;
	}

	/**
	 * @return boolean
	 */
	public boolean isConvertNameTokenToTableNameToken() {
		return convertNameTokenToTableNameToken;
	}
	/**
	 * @param newConvertNameTokenToTableNameToken boolean
	 */
	public void setConvertNameTokenToTableNameToken(boolean newConvertNameTokenToTableNameToken) {
		convertNameTokenToTableNameToken = newConvertNameTokenToTableNameToken;
	}
	/**
	 */
	public void addToken(Token token) {
		StringToken prevToken;
		if (concatenateStringTokens) {
			if (shouldConcatenateToken(token)) {
				prevToken = (StringToken) tokens.get(tokens.size() - 1);
				prevToken.append((StringToken) token);
			} else {
				tokens.add(token);
				return;
			}
		} else {
			// Don't add any string tokens at the beginning if all the string is all blanks.	
			if (!(tokens.size() == 0 && token.isStringToken() && token.string.trim().length() == 0))
				tokens.add(token);
		}
	}

	/**
	 */
	public void addSelectNameToken(Token token) {
		SelectNameToken prevToken;
		if (shouldConcatenateSelectNameToken(token)) {
			prevToken = (SelectNameToken) tokens.get(tokens.size() - 1);
			prevToken.append((SelectNameToken) token);
		} else {
			tokens.add(token);
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/13/2001 4:39:07 PM)
	 * @param string java.lang.String
	 */
	private void checkState(String string) {

		switch (state) {
			case STATE_ABOUTTOREADMULTIPLEOUTPUT :
				{
					stateAboutToReadMultipleOutput(string);
					break;
				}
			case STATE_MULTIPLEOUTPUT :
				{
					stateMultipleOutput(string);
					break;
				}
			case STATE_MULTIPLEINPUT :
				{
					stateMultipleInput(string);
					break;
				}
			case STATE_SINGLETABLE :
				{
					stateSingleTable(string);
					break;
				}
			case STATE_MULTIPLETABLE :
				{
					stateMultipleTable(string);
					break;
				}
			case STATE_INSERT :
				{
					stateInsert(string);
					break;
				}
			case STATE_DELETE :
				{
					stateDelete(string);
					break;
				}
			case STATE_ABOUTTOREADSINGLETABLE :
				{
					stateAboutToReadSingleTable(string);
					break;
				}
			case STATE_ABOUTTOREADMULTIPLETABLE :
				{
					stateAboutToReadMultipleTable(string);
					break;
				}
			case STATE_CLEAR :
				{
					stateClear(string);
					break;
				}
			default :
				{

				}
		}
	}
	/**
	 * @return java.lang.String
	 */
	private static String getHostVariableIdentifier() {
		return ":"; //$NON-NLS-1$
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 2:40:44 PM)
	 * @return int
	 */
	private int getType() {
		switch (state) {
			case STATE_MULTIPLEOUTPUT :
				{
					return TYPE_OUTPUT;
				}
			case STATE_MULTIPLEINPUT :
				{
					return TYPE_INPUT;
				}
			case STATE_SINGLETABLE :
				{
					return TYPE_TABLE;
				}
			case STATE_MULTIPLETABLE :
				{
					return TYPE_INPUT;
				}
			default :
				{
					return TYPE_STRING;
				}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateAboutToReadMultipleOutput(String string) {
		if (string.startsWith(getHostVariableIdentifier())) {
			state = STATE_MULTIPLEOUTPUT;
			return;
		} else {
			state = STATE_CLEAR;
			stateClear(string);
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateAboutToReadMultipleTable(String string) {
		if (string.startsWith(getHostVariableIdentifier())) {
			state = STATE_MULTIPLETABLE;
			return;
		}
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_WHERE)
			|| string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_HAVING)
			|| string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_GROUP)) {
			state = STATE_CLEAR;
			return;
		}

		return;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateAboutToReadSingleTable(String string) {
		if (string.startsWith(getHostVariableIdentifier())) {
			state = STATE_SINGLETABLE;
			return;
		} else {
			state = STATE_CLEAR;
			stateClear(string);
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateClear(String string) {
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_FROM)) {
			state = STATE_ABOUTTOREADMULTIPLETABLE;
			return;
		}
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_UPDATE)) {
			state = STATE_ABOUTTOREADSINGLETABLE;
			return;
		}
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_INTO)) {
			state = STATE_ABOUTTOREADMULTIPLEOUTPUT;
			return;
		}
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_INSERT)) {
			state = STATE_INSERT;
			return;
		}
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_CALL)) {
			state = STATE_ABOUTTOREADMULTIPLEOUTPUT;
			return;
		}
		if (string.startsWith(getHostVariableIdentifier())) {
			state = STATE_MULTIPLEINPUT;
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateDelete(String string) {
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_FROM)) {
			state = STATE_ABOUTTOREADSINGLETABLE;
			return;
		} else {
			state = STATE_CLEAR;
			stateClear(string);
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateInsert(String string) {
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_INTO)) {
			state = STATE_ABOUTTOREADSINGLETABLE;
			return;
		} else {
			state = STATE_CLEAR;
			stateClear(string);
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateMultipleInput(String string) {
		if (string.startsWith(getHostVariableIdentifier())) {
			return;
		} else {
			state = STATE_CLEAR;
			stateClear(string);
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateMultipleOutput(String string) {
		if (string.startsWith(getHostVariableIdentifier())) {
			return;
		} else {
			state = STATE_CLEAR;
			stateClear(string);
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateMultipleTable(String string) {
		if (string.startsWith(getHostVariableIdentifier())) {
			return;
		}
		if (string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_WHERE)
			|| string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_HAVING)
			|| string.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_GROUP)) {
			state = STATE_CLEAR;
			return;
		} else {
			state = STATE_ABOUTTOREADMULTIPLETABLE;
			return;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/15/2001 4:26:38 PM)
	 * @param string java.lang.String
	 */
	private void stateSingleTable(String string) {
		state = STATE_CLEAR;
		stateClear(string);
		return;
	}

	private boolean shouldConcatenateToken(Token token) {

		Token prevToken;
		// Cannot concatenate if there is nothing to concatenate with
		if (tokens.size() < 1) {
			return false;
		}
		// Token must be a string token to concatenate
		if (!token.isStringToken()) {
			return false;
		}
		// The previous token must be a string token to concatenate
		if (!(prevToken = ((Token) tokens.get(tokens.size() - 1))).isStringToken()) {
			return false;
		}

		return true;
	}

	private boolean shouldConcatenateSelectNameToken(Token token) {

		Token prevToken;
		// Cannot concatenate if there is nothing to concatenate with
		if (tokens.size() < 1) {
			return false;
		}
		// Token must be a string token to concatenate
		if (!token.isSelectNameToken()) {
			return false;
		}
		// The previous token must be a string token to concatenate
		if (!(prevToken = ((Token) tokens.get(tokens.size() - 1))).isSelectNameToken()) {
			return false;
		}

		return true;
	}
	
	private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
		IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}

}
