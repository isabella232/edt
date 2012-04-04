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
package org.eclipse.edt.compiler.internal.sqltokenizer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author dollar 
 * 
 * The statement is the keyword from the statement that contains the 
 * inline SQL or "ANY" when used for default select conditions or in
 * a situation where we don't really know which statement we are 
 * processing.
 *
 * Parse a string of SQL statement, collecting messages and breaking
 * it into the respective clauses.
 */
public class EGLSQLParser {

	private Reader inputReader;
	private EGLSQLLexer lexer;
	private ArrayList sqlClauses;
	private ArrayList reservedClauseKeywordsUsed;
	private ArrayList problems;
	private EGLPrimeToken currentToken;
	private EGLPrimeToken previousToken;
	private boolean hasUnion;

	public EGLSQLParser(String input, String stmtType, ICompilerOptions compilerOptions) {
		inputReader = new StringReader(input);
		lexer = new EGLSQLLexer(inputReader, compilerOptions);
		sqlClauses = new ArrayList();
		reservedClauseKeywordsUsed = new ArrayList();
		problems = new ArrayList();
		currentToken = null;
		hasUnion = false;
		try {
			if (stmtType == IEGLConstants.KEYWORD_EXECUTE )
			 	parseExecuteSQL();
			else	
				parseSQL();
		} catch (Exception iox) {
			iox.printStackTrace();
		}
		// add lexer problems to the parser problems.
		ArrayList lexerProbs = lexer.getProblems();
		for (int ii = 0; ii < lexerProbs.size(); ii++) {
			problems.add(lexerProbs.get(ii));
		}
	}
	
	private boolean isClauseStart(String clauseKeyword) {

		if (clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_SELECT)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_INTO)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_FROM)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.KEYWORD_WHERE)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_VALUES)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_DELETE)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_SET)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_HAVING)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_CALL)) {
			return true;
		}
		
		if (clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_UPDATE)) {
			
			if (previousToken != null && "keep".equalsIgnoreCase(previousToken.getText())) {
				if (lexer.lookAhead1 != null && "locks".equalsIgnoreCase(lexer.lookAhead1.getText())) {
					return false;
				}   
			}
			return true;
		}

		
		return false;
	}
	
	
	/**
	  * Parse the string held by inputReader into tokens, placing them in a 
	  * EGLSQLClauseTree by keyword until the end of the string is reached. 
	  * This does not assume that a complete SQL statement is provided but 
	  * does make some assumptions upon clauses because of what follows what.
	  * If a keyword that is known to EGL is not found to start the statement,
	  * it is assumed that we are on an execute statement and all tokens are 
	  * considered part of one clause. 
	  * 
	  * Note that where, group by, having and order by are always
	  * optional.  Also note that columns does not have a keyword
	  * but is instead set aside by a list of columns surrounded by
	  * parens.  The insert into clause is INSERT INTO tablename and
	  * optional label.  Immediately following that should be the
	  * opening paren for the columns clause.  The columns clause
	  * ends with the corresponding right paren.
	  *
	  * @return sqlClauses an ArrayList where each entry is a EGLSQLClauseTree.
	  */
	private void parseSQL() throws IOException {
		EGLSQLClauseTree currentClause = null;
		EGLSQLClauseTree commentClause = null;
		boolean alreadyConsumed = false;
		String clauseKeyword;  

		previousToken = null;	
		currentToken = lexer.getNextToken();
		while (currentToken != null) { 
			clauseKeyword = nextClauseKeyword(currentToken, lexer.lookAhead1, lexer.lookAhead2);
			if (isClauseStart(clauseKeyword)) {
				// single word clause keywords
				if (commentClause == null)
					currentClause = new EGLSQLClauseTree(currentToken);
				else {
					currentClause = commentClause;
					currentClause.setClauseKeyword(currentToken);
				}
				currentClause.addToken(currentToken);
				sqlClauses.add(currentClause);
				if (!(clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_INTO)) &&
					(!reservedClauseKeywordsUsed.contains(currentToken.getText().toLowerCase())) ){
					reservedClauseKeywordsUsed.add(currentToken.getText().toLowerCase());
				}
			}
			else if (clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_ORDER
												+ " " + IEGLConstants.SQLKEYWORD_BY) 		//$NON-NLS-1$
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_GROUP
												+ " " + IEGLConstants.SQLKEYWORD_BY)		//$NON-NLS-1$
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_INSERT
												+ " " + IEGLConstants.SQLKEYWORD_INTO)
				|| clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_FOR
												+ " " + IEGLConstants.SQLKEYWORD_UPDATE)) {	//$NON-NLS-1$
				// double word clause keywords
				// order by, group by or insert into			  	
				if (commentClause == null)
					currentClause = new EGLSQLClauseTree(currentToken, lexer.lookAhead1);
				else {
					currentClause = commentClause;
					currentClause.setClauseKeyword(currentToken);
					currentClause.setSecondKeyword(lexer.lookAhead1);
				}
				if (!reservedClauseKeywordsUsed.contains(currentToken.getText().toLowerCase())) {
					reservedClauseKeywordsUsed.add(currentToken.getText().toLowerCase());
				}
				currentClause.addToken(currentToken);
				currentClause.addToken(lexer.lookAhead1);
				sqlClauses.add(currentClause);
				previousToken = currentToken;
				currentToken = lexer.getNextToken();
				// just consume first token, second will be consumed later
			}	
			else if (clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_FOR
												+ " " + IEGLConstants.SQLKEYWORD_UPDATE //$NON-NLS-1$
												+ " " + IEGLConstants.SQLKEYWORD_OF) //$NON-NLS-1$
				|| 	clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_WHERE 
												+ " " + IEGLConstants.SQLKEYWORD_CURRENT //$NON-NLS-1$
												+ " " + IEGLConstants.SQLKEYWORD_OF)) {	//$NON-NLS-1$
				// triple word clause keywords
			    // for update of or where current of 	
				if (commentClause == null)
					currentClause =
						new EGLSQLClauseTree(currentToken, lexer.lookAhead1, lexer.lookAhead2);
				else {
					currentClause = commentClause;
					currentClause.setClauseKeyword(currentToken);
					currentClause.setSecondKeyword(lexer.lookAhead1);
					currentClause.setThirdKeyword(lexer.lookAhead2);
				}
				String temp = null;
				if (currentToken.getText().equalsIgnoreCase(IEGLConstants.SQLKEYWORD_WHERE) )
					temp = IEGLConstants.SQLKEYWORD_WHERE;
				else
					temp = IEGLConstants.SQLKEYWORD_UPDATE;
				if (!reservedClauseKeywordsUsed.contains(temp.toLowerCase())) {
					reservedClauseKeywordsUsed.add(temp.toLowerCase());
				}
				currentClause.addToken(currentToken);
				currentClause.addToken(lexer.lookAhead1);
				currentClause.addToken(lexer.lookAhead2);
				sqlClauses.add(currentClause);
				previousToken = currentToken;
				currentToken = lexer.getNextToken();
				previousToken = currentToken;
				currentToken = lexer.getNextToken(); 
				// just consume first two tokens, third will be consumed later
				if (clauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_WHERE 
													+ " " + IEGLConstants.SQLKEYWORD_CURRENT //$NON-NLS-1$
													+ " " + IEGLConstants.SQLKEYWORD_OF) )	 //$NON-NLS-1$
					currentToken.setWhereCurrentOf();		
			}		
			else { 
				// not the beginning of a particular clause 
				// If I don't have a keyword yet, everything is part of a single clause
				// I'm assuming that it is an execute statement
				// If I do have a keyword, just add this token to the list. 
				if (currentClause == null) {
					// no keyword yet 
					if (currentToken.getType() == EGLPrimeToken.SQLCOMMENT) {
						if (commentClause == null)
							commentClause = new EGLSQLClauseTree();
						commentClause.addToken(currentToken);
					} 
					else {
						currentClause = new EGLSQLClauseTree();
						currentClause.setClauseKeyword(new EGLPrimeToken(EGLPrimeToken.EXECUTE, "EXECUTE", 
								currentToken.getOffset(), currentToken.getLine(), currentToken.getColumn())); //$NON-NLS-1$
						currentClause.addToken(currentToken);
						sqlClauses.add(currentClause);	
					}
				} else {
					if (currentClause.getClauseType() == EGLPrimeToken.INSERT)
						if (currentToken.getType() == EGLPrimeToken.L_PAREN) {
							// end the insert into and start a columns clause
							currentClause = new EGLSQLClauseTree(new EGLPrimeToken(EGLPrimeToken.COLUMNS, "COLUMNS", 0, 0, 0)); //$NON-NLS-1$
							sqlClauses.add(currentClause);
						}
					switch (currentToken.getType()) {
						case (EGLPrimeToken.L_PAREN) : {
							// add the token to the current clause
							currentClause.addToken(currentToken);
							alreadyConsumed = matchSQLParens(currentClause);
							break;
						}
						case (EGLPrimeToken.HOST_VAR_COLON) : {
							//starting host variable name	
							//  make sure it is really a host variable
							// It could be an Informix 3 part table name
							// This is known only by whether the previous token
							// is an identifier that "touches" the colon on the left of the colon
							if (isReallyAHostVarColon()) {
								// add the token to the current clause
								currentClause.addToken(currentToken);
								List probs = getHostVarColumnName(currentClause);
								alreadyConsumed = true;
								if (probs != null){
									for (int ii = 0; ii < probs.size(); ii++) {
										problems.add(probs.get(ii));
									}	
								}
								break;
							}
							else  {
								currentToken = new EGLPrimeToken( EGLPrimeToken.COLON, ":", currentToken.getOffset(), 
										currentToken.getLine(), currentToken.getColumn() ); //$NON-NLS-1$
								currentClause.addToken(currentToken);
								break;
							}
						}
						default : {
							// add the token to the current clause
							currentClause.addToken(currentToken);
							if (currentToken.getType()== EGLPrimeToken.UNION) {
								setHasUnion(true);
								String currentClauseKeyword = nextClauseKeyword(lexer.lookAhead1, lexer.lookAhead2, lexer.lookAhead3);
								if (!reservedClauseKeywordsUsed.contains(currentToken.getText().toLowerCase())) {
									reservedClauseKeywordsUsed.add(currentToken.getText().toLowerCase());
								}
								currentToken = lexer.getNextToken();
								while (currentToken != null && !reachedUnionEnd(currentClause, currentClauseKeyword)) {
									switch (currentToken.getType()) {
										case (EGLPrimeToken.L_PAREN) : {
											currentClause.addToken(currentToken);
											alreadyConsumed = matchSQLParens(currentClause);
											break;
										}
										case (EGLPrimeToken.HOST_VAR_COLON) : {
											//make sure it is really a host variable
											// It could be an Informix 3 part table name
											// This is known only by whether the previous token
											// is an identifier that "touches" the colon on the left of the colon
											if (isReallyAHostVarColon()) {
												currentClause.addToken(currentToken);
												List probs = getHostVarColumnName(currentClause);
												alreadyConsumed = true;
												if (probs != null)
													for (int ii = 0; ii	< probs.size(); ii++) {
														problems.add(probs.get(ii));
													}
												break;
											}
											else {
												currentToken = new EGLPrimeToken( EGLPrimeToken.COLON, ":", currentToken.getOffset(), 
														currentToken.getLine(), currentToken.getColumn() ); //$NON-NLS-1$
												currentClause.addToken(currentToken);
												break;
											}
										}
										default : {
											currentClause.addToken(currentToken);
										}
									} // end switch
									if (alreadyConsumed)
										alreadyConsumed = false;
									else {
										previousToken = currentToken;
										currentToken = lexer.getNextToken();
									}
									currentClauseKeyword = nextClauseKeyword(currentToken, lexer.lookAhead1, lexer.lookAhead2); 
								} // end while
								alreadyConsumed = true;
							} // end union
						}
					}
				}
			}
			if (currentToken != null)
				if (alreadyConsumed)
					alreadyConsumed = false;
				else {
					previousToken = currentToken;
					currentToken = lexer.getNextToken();
				}
		}
		// make sure the wherecurrentof flag is set on all tokens in 
		// this clause
		EGLSQLClauseTree whereCurrent = getWhereCurrentOfTokens();
		if (whereCurrent != null){
			for(int kk = 0; kk <whereCurrent.tokens.size(); kk++){
				whereCurrent.getToken(kk).setWhereCurrentOf();			
			}			
		}
		return;
	}

	private boolean reachedUnionEnd(EGLSQLClauseTree currentClause, String currentClauseKeyword) {
				
		if (currentClauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_ORDER
	    									+ " " + IEGLConstants.SQLKEYWORD_BY)//$NON-NLS-1$
	        || currentClauseKeyword.equalsIgnoreCase(IEGLConstants.SQLKEYWORD_FOR
	    									+ " " + IEGLConstants.SQLKEYWORD_UPDATE //$NON-NLS-1$
            								+ " " + IEGLConstants.SQLKEYWORD_OF) )  { //$NON-NLS-1$
			return true;
		}	
		return false;
	}
	
	/**
	  * Parse the string held by inputReader into tokens, placing them in a 
	  * EGLSQLClauseTree until the end of the string is reached. 
	  * Individual keywords are not recognized because this is an Execute statement.
	  * This does not assume that a complete SQL statement is provided.
	  *
	  * @return sqlClauses an ArrayList where each entry is a EGLSQLClauseTree.
	  */
	private void parseExecuteSQL() throws IOException {
		EGLSQLClauseTree currentClause = null;
		EGLSQLClauseTree commentClause = null;
		boolean alreadyConsumed = false;
		previousToken = null;
		currentToken = lexer.getNextToken();
		while (currentToken != null) {
			// If I don't have a keyword yet, everything is part of a single clause
			// I'm assuming that it is an execute statement
			// If I do have a keyword, just add this token to the list. 
			if (currentClause == null) {
				// no keyword yet 
				if (currentToken.getType() == EGLPrimeToken.SQLCOMMENT) {
					if (commentClause == null)
						commentClause = new EGLSQLClauseTree();
					commentClause.addToken(currentToken);
				} 
				else {
					currentClause = new EGLSQLClauseTree();
					currentClause.setClauseKeyword(new EGLPrimeToken(EGLPrimeToken.EXECUTE, "EXECUTE", currentToken.getOffset(), 
							currentToken.getLine(), currentToken.getColumn())); //$NON-NLS-1$
					currentClause.addToken(currentToken);
					sqlClauses.add(currentClause);	
				}
			} 
			else {
				if (currentClause.getClauseType() == EGLPrimeToken.INSERT)
					if (currentToken.getType() == EGLPrimeToken.L_PAREN) {
						// end the insert into and start a columns clause
						currentClause = new EGLSQLClauseTree(new EGLPrimeToken(EGLPrimeToken.COLUMNS, "COLUMNS", 0, 0, 0)); //$NON-NLS-1$
						sqlClauses.add(currentClause);
					}

				switch (currentToken.getType()) {
					case (EGLPrimeToken.L_PAREN) : {
						// add the token to the current clause
						currentClause.addToken(currentToken);
						alreadyConsumed = matchSQLParens(currentClause);
						break;
					}
					case (EGLPrimeToken.HOST_VAR_COLON) : {
							// starting host variable name
 							//  make sure it is really a host variable
							// It could be an Informix 3 part table name
							// This is known only by whether the previous token
							// is an identifier that "touches" the colon on the left of the colon
							if (isReallyAHostVarColon()) {
								// add the token to the current clause
								currentClause.addToken(currentToken);
								List probs = getHostVarColumnName(currentClause);
								alreadyConsumed = true;
								if (probs != null){
									for (int ii = 0; ii < probs.size(); ii++) {
										problems.add(probs.get(ii));
									}	
								}
								break;
							}
							else  {
								currentToken = new EGLPrimeToken( EGLPrimeToken.COLON, ":", currentToken.getOffset(), 
										currentToken.getLine(), currentToken.getColumn() ); //$NON-NLS-1$
								// add the token to the current clause
								currentClause.addToken(currentToken);
								break;
							}
						}
					default : {
						// add the token to the current clause
						currentClause.addToken(currentToken);
					}
				}
			}
			if (currentToken != null)
				if (alreadyConsumed)
					alreadyConsumed = false;
				else {
					previousToken = currentToken;
					currentToken = lexer.getNextToken();
				}
		}			
		return;
	}

	private String nextClauseKeyword(EGLPrimeToken currentToken, EGLPrimeToken secondToken, EGLPrimeToken thirdToken  ) {
		StringBuffer keyword = new StringBuffer(""); //$NON-NLS-1$
		if (currentToken == null)
			return keyword.toString();
		else{
			keyword.append(currentToken.getText());
			if (currentToken.getType() == EGLPrimeToken.GROUP || currentToken.getType() == EGLPrimeToken.ORDER) {
				if (secondToken != null && secondToken.getType() == EGLPrimeToken.BY) {
					keyword.append(" " + secondToken.getText()); //$NON-NLS-1$
				}
			} else if (currentToken.getType() == EGLPrimeToken.INSERT) {
				if (secondToken != null && secondToken.getType() == EGLPrimeToken.INTO) {
					keyword.append(" " + secondToken.getText()); //$NON-NLS-1$
				}
			} else if (currentToken.getType() == EGLPrimeToken.FOR) {
				if (secondToken != null && secondToken.getType() == EGLPrimeToken.UPDATE) {
					keyword.append(" " + secondToken.getText()); //$NON-NLS-1$
					if (thirdToken != null && thirdToken.getType() == EGLPrimeToken.OF) {
						keyword.append(" " + thirdToken.getText()); //$NON-NLS-1$
					}
				}
			} else if (currentToken.getType() == EGLPrimeToken.WHERE)
				if (secondToken != null && secondToken.getType() == EGLPrimeToken.CURRENT && thirdToken != null
						&& thirdToken.getType() == EGLPrimeToken.OF) {
					keyword.append(" " + secondToken.getText()); //$NON-NLS-1$
					keyword.append(" " + thirdToken.getText()); //$NON-NLS-1$
				}										
		}
		return keyword.toString();
	}
				
	/**
	 * Handle matching left and right parens in an SQL clause. This is needed
	 * because the clauses can use parentheses to delineate groupings of tokens
	 * that compose a portion of another clause. Without matching up the parens,
	 * we would stumble over keywords and think they were beginning another
	 * clause, when in fact, it is just a part of the current clause.
	 * 
	 */
	public boolean matchSQLParens(EGLSQLClauseTree currentClause) throws IOException {
		int numberLparens;
		int numberRparens;
		Stack parensStack = new Stack();
		EGLPrimeToken paren = null;
		boolean consumed = false;

		numberLparens = 1;
		numberRparens = 0;
		parensStack.clear();
		parensStack.push(currentToken);

		while (numberLparens != numberRparens && currentToken != null) {
			if (consumed) // getHostVarColumnName could consume already
				consumed = false;
			else {
				previousToken = currentToken;
				currentToken = lexer.getNextToken();
			}
			if (currentToken != null)		
			switch (currentToken.getType()) {
				case (EGLPrimeToken.L_PAREN): {
					currentClause.addToken(currentToken);
					numberLparens++;
					parensStack.push(currentToken);
					break;
				}
				case (EGLPrimeToken.R_PAREN): {
					currentClause.addToken(currentToken);
					numberRparens++;
					parensStack.pop();
					break;
				}
				case (EGLPrimeToken.HOST_VAR_COLON): { 
					// starting host variable name
					// make sure it is really a host variable
					// It could be an Informix 3 part table name
					// This is known only by whether the previous token
					// is an identifier that "touches" the colon on the left of
					// the colon
					if (isReallyAHostVarColon()) {
						currentClause.addToken(currentToken);
						List probs = getHostVarColumnName(currentClause);
						consumed = true;
						if (probs != null) {
							for (int ii = 0; ii < probs.size(); ii++) {
								problems.add(probs.get(ii));
							}
						}
						break;
					} else {
						currentToken = new EGLPrimeToken(EGLPrimeToken.COLON,
								":", currentToken.getOffset(), currentToken.getLine(), currentToken.getColumn()); //$NON-NLS-1$
						currentClause.addToken(currentToken);
						break;
					}
				}
				default: {
					currentClause.addToken(currentToken);
				}
			}
		}
		while (!(parensStack.empty())) {
			paren = (EGLPrimeToken) (parensStack.pop());
			problems.add(new Problem(
					paren.getOffset(), 
					paren.getOffset() + paren.getText().length(), 
					IMarker.SEVERITY_ERROR, 
					IProblemRequestor.P_UNMATCHED_PARENS, 
					new String[0]));
		}
		return consumed;
	}

	/**
	 * Parse out a host variable name from the Sql text.
	 *  This is also used for SQLColumnNames
	 *
	 * Note that there are problems parsing out the host variables
	 * in SQLEXEC statements because there are some special
	 * conditions that prevent the direct use of dataItem() or
	 * standAloneDataItem().
	 * The reason is that dataItem() and standAloneDataItem
	 * both expect there to be no whitespace around the
	 * periods of the parts of a name and they expect each
	 * part of the name to be a valid Java identifier.
	 * There are two problems with this:
	 * 1.  It is valid to have :hostvariable .text1 .text2...
	 *     What this means is that we substitute a value for
	 *     the host variable and then SQL concatenates that
	 *     to .text1 and .text2 without any spaces.  The dot
	 *     can have spaces on either side of it and there
	 *     can be multiple pairs of dots and text.
	 * 2.  There are characters that SQL allows in the .text
	 *     that are not valid in Java identifiers.
	 *     Examples:  .@c  .$c ."a" .a"b" .#a
	 * Therefore, something like this ends up being valid:
	 * :hostvar .vguser .#user
	 * In addition, I can't just index for the first blank
	 * and assume that that is the hostvariable name and
	 * then send it to standAloneDataItem becuase it is
	 * valid to have blanks around each subscript name.
	 *
	 * So......
	 * The solution is to just tokenize the input, building
	 * up a list of tokens until I have found the right
	 * bracket of the subscripts or until I find a blank
	 * in the qualified item name.  This is considered the
	 * host variable name and it must meet our name rules,
	 * so send it to standAloneDataItem() to be validated.
	 * There is one additional trick here.  If the last
	 * thing I found was the right bracket and the next
	 * token is 'touching' the right bracket, go ahead and
	 * consider it part of the host variable so we get a msg
	 * about host variables needing to be surrounded by
	 * whitespace.
	 *
	 *
	 * @return List = list of problems found
	 *
	 *
	 */
	public List getHostVarColumnName(EGLSQLClauseTree currentClause) throws IOException {
		/* colon is already on the clause tree */
		previousToken = currentToken;
		currentToken = lexer.getNextToken();
		if  ((currentToken == null) || (currentToken.getType() != EGLPrimeToken.IDENTIFIER) ) {
			//FRIEDA error message - colon with nothing following
			return null;
		}
		/* my list of tokens that will be validated as part of the name */
		/* assume first token is part of the name, it better be....*/
		else 
			currentToken.setHostVar();
		
		EGLSQLClauseTree tempTokens = new EGLSQLClauseTree(currentToken);
		tempTokens.addToken(currentToken);
		currentClause.addToken(currentToken);
		previousToken = currentToken;
		currentToken = lexer.getNextToken();
		
		// This loop control looks wierd but is effective.  The problem is that if
		// I just loop while things are touching, then I have to special case the
		// situation where they had whitespace between the item and the '['.
		// Then if I find that things are touching the ']', I need to drop back
		// into the loop (which means repeating the code again.  I'd rather make
		// the loop control a little wierd.
		while ( (currentToken != null)
		&& (currentToken.getType() != EGLPrimeToken.R_PAREN) 
		&& (currentToken.getType() != EGLPrimeToken.COMMA) 
		&& (currentToken.getType() != EGLPrimeToken.EQUAL) 
		&& (currentToken.getType() != EGLPrimeToken.NOT_EQUAL) 
		&& (currentToken.getType() != EGLPrimeToken.GREATER) 
		&& (currentToken.getType() != EGLPrimeToken.GREATER_EQUAL) 
		&& (currentToken.getType() != EGLPrimeToken.LESS) 
		&& (currentToken.getType() != EGLPrimeToken.LESS_EQUAL) 
		&& (currentToken.getType() != EGLPrimeToken.BANG) 
		&& (currentToken.getType() != EGLPrimeToken.PLUS) 
		&& (currentToken.getType() != EGLPrimeToken.SPLAT) 
		&& (currentToken.getType() != EGLPrimeToken.MINUS) 
		&& (currentToken.getType() != EGLPrimeToken.PERCENT) 
		&& (currentToken.getType() != EGLPrimeToken.AND) 
		&& (currentToken.getType() != EGLPrimeToken.OR) 
		&& (currentToken.getType() != EGLPrimeToken.SQL_AND) 
		&& (currentToken.getType() != EGLPrimeToken.SQL_OR) 
		&& !(currentToken.getText().equals("/")) //$NON-NLS-1$
		&& ((currentToken.getType() == EGLPrimeToken.L_SQUARE) || // start of subscripts   OR
		 (((EGLPrimeToken) (tempTokens.getLastToken())).getOffset() // previous token
		+ ((EGLPrimeToken) (tempTokens.getLastToken())).getText().length() == // 'touches'
		 ((EGLPrimeToken) (currentToken)).getOffset())) // thisToken
		) {
			if (currentToken.getType() == EGLPrimeToken.L_SQUARE) {
				// start of subscripts, take them all
				while (currentToken != null && currentToken.getType() != EGLPrimeToken.R_SQUARE)
					 /* just in case they left out the ']' */ {
					currentToken.setHostVar();		
					tempTokens.addToken(currentToken);
					currentClause.addToken(currentToken);
					previousToken = currentToken;
					currentToken = lexer.getNextToken();
				}
				if (currentToken != null) { // include ] in the name
					currentToken.setHostVar();		
					tempTokens.addToken(currentToken);
					currentClause.addToken(currentToken);
					previousToken = currentToken;
					currentToken = lexer.getNextToken();				
				}
			} else {
				currentToken.setHostVar();
				tempTokens.addToken(currentToken);
				currentClause.addToken(currentToken);
				previousToken = currentToken;
				currentToken = lexer.getNextToken();
			}
		}
		return null;
	}

	// make sure it is really a host variable
	// It could be an Informix 3 part table name
	// This is known only by whether the previous token
	// is an identifier that "touches" the colon on the left of the colon
	private boolean isReallyAHostVarColon() {
		if (lexer.lookAhead1 != null && "=".equals(lexer.lookAhead1.getText())) {
			return false; //This was a ":="
		}
		
		if (previousToken.getType() == EGLPrimeToken.IDENTIFIER && 
				previousToken.getOffset() + previousToken.getText().length() == currentToken.getOffset())
			return false; // they are "touching"
		else 
			return true; // they aren't touching.
	}
	
	
	
	private String getSpecificClause(int type) {
		EGLSQLClauseTree aClause = null;
		for (int ii = 0; ii < sqlClauses.size(); ii++) {
			aClause = (EGLSQLClauseTree) (sqlClauses.get(ii));
			if (aClause.clauseKeyword.getType() == EGLPrimeToken.WHERE) {
				if (type == EGLPrimeToken.CURRENT) {
					if (aClause.secondKeyword != null && aClause.secondKeyword.getType() == EGLPrimeToken.CURRENT)
						return aClause.toString();					
				}
				if (type == EGLPrimeToken.WHERE) {
					if (aClause.secondKeyword == null) // not where current of
						return aClause.toString();					
				}				
			}	
			else if (aClause.clauseKeyword.getType() == type)
				return aClause.toString();
		}
		return null;
	}

	public String getAllClauses() {
		EGLSQLClauseTree aClause = null;
		EGLSQLClauseTree newClauseTree = new EGLSQLClauseTree();
		for (int ii = 0; ii < sqlClauses.size(); ii++) {
			aClause = (EGLSQLClauseTree) (sqlClauses.get(ii));
			newClauseTree.tokens.addAll(aClause.tokens);
		}
		return newClauseTree.toString();
	}
	
	public String getAllClausesWOCommentsAndHostVariables() {
		EGLSQLClauseTree aClause = null;
		EGLSQLClauseTree newClauseTree = new EGLSQLClauseTree();
		for (int ii = 0; ii < sqlClauses.size(); ii++) {
			aClause = (EGLSQLClauseTree) (sqlClauses.get(ii));
			addTokensHostVariableMarks(newClauseTree.tokens,aClause.tokens);
		}
		return newClauseTree.toString();
	} 
	
	public EGLSQLClauseTree getClauseWOCommentsAndHostVariables(EGLSQLClauseTree aClause) {
		EGLSQLClauseTree newClauseTree = new EGLSQLClauseTree();
		removeCommentsAndHostVariables(newClauseTree.tokens,aClause.tokens);
		return newClauseTree;
	}

	public void addTokensHostVariableMarks(ArrayList masterTokens, ArrayList clauseTokens){
		for (int ii=0; ii<clauseTokens.size(); ii++) {
			if ( ((EGLPrimeToken)(clauseTokens.get(ii))).isHostVar() 
					|| ((EGLPrimeToken)(clauseTokens.get(ii))).getType() == EGLPrimeToken.SQLCOMMENT) {
				// do nothing
				;
			} 
			else if ( ((EGLPrimeToken)(clauseTokens.get(ii))).getType() == EGLPrimeToken.HOST_VAR_COLON ) {
				EGLPrimeToken mark = new EGLPrimeToken( EGLPrimeToken.UNKNOWN_EGL, "?",  //$NON-NLS-1$
				((EGLPrimeToken)(clauseTokens.get(ii))).getOffset(), 
				((EGLPrimeToken)(clauseTokens.get(ii))).getLine(), 
				((EGLPrimeToken)(clauseTokens.get(ii))).getColumn() );
				 masterTokens.add(mark);
			}
			else
				masterTokens.add(clauseTokens.get(ii));
		}
	}
	
	public void removeCommentsAndHostVariables(ArrayList masterTokens, ArrayList clauseTokens){
		for (int ii=0; ii<clauseTokens.size(); ii++) {
			if ( ((EGLPrimeToken)(clauseTokens.get(ii))).isHostVar() 
				|| ((EGLPrimeToken)(clauseTokens.get(ii))).getType() == EGLPrimeToken.SQLCOMMENT
				|| ((EGLPrimeToken)(clauseTokens.get(ii))).getType() == EGLPrimeToken.HOST_VAR_COLON ) {
				;  // do nothing
			}
			else masterTokens.add(clauseTokens.get(ii));
		}
	}
	
	public String getSelectClause() {
		return getSpecificClause(EGLPrimeToken.SELECT);
	}

	public String getIntoClause() {
		return getSpecificClause(EGLPrimeToken.INTO);
	}

	public String getFromClause() {
		return getSpecificClause(EGLPrimeToken.FROM);
	}
	
	public String getCallClause() {
		return getSpecificClause(EGLPrimeToken.CALL);
	}
	
	public String getDeleteClause() {
		return getSpecificClause(EGLPrimeToken.DELETE);
	}

	public String getColumnsClause() {
		return getSpecificClause(EGLPrimeToken.COLUMNS);
	}

	public String getValuesClause() {
		return getSpecificClause(EGLPrimeToken.VALUES);
	}
	
	public String getUnionClause() {
		return getSpecificClause(EGLPrimeToken.UNION);
	}

	public String getUpdateClause() {
		return getSpecificClause(EGLPrimeToken.UPDATE);
	}

	public String getSetClause() {
		return getSpecificClause(EGLPrimeToken.SET);
	}

	public String getHavingClause() {
		return getSpecificClause(EGLPrimeToken.HAVING);
	}

	public String getWhereClause() {
		return getSpecificClause(EGLPrimeToken.WHERE);
	}

	public String getWhereCurrentOfClause() {
		return getSpecificClause(EGLPrimeToken.CURRENT);
	}

	public String getOrderByClause() {
		return getSpecificClause(EGLPrimeToken.ORDER);
	}

	public String getGroupByClause() {
		return getSpecificClause(EGLPrimeToken.GROUP);
	}

	public String getInsertIntoClause() {
		return getSpecificClause(EGLPrimeToken.INSERT);
	}

	public String getForUpdateOfClause() {
		return getSpecificClause(EGLPrimeToken.FOR);
	}

	private EGLSQLClauseTree getSpecificTokens(int type) {
		EGLSQLClauseTree aClause = null;
		for (int ii = 0; ii < sqlClauses.size(); ii++) {
			aClause = (EGLSQLClauseTree) (sqlClauses.get(ii));
			if (aClause.clauseKeyword.getType() == EGLPrimeToken.WHERE) {
				if (type == EGLPrimeToken.CURRENT) {
					if (aClause.secondKeyword != null && aClause.secondKeyword.getType() == EGLPrimeToken.CURRENT)
						return aClause;					
				}
				if (type == EGLPrimeToken.WHERE) {
					if (aClause.secondKeyword == null)// not where current of 
						return aClause;					
				}				
			}	
			else if (aClause.clauseKeyword.getType() == type)
				return aClause;
		}
		return null;
	}
	
	public EGLSQLClauseTree getAllTokens() {
		EGLSQLClauseTree aClause = null;
		EGLSQLClauseTree newClauseTree = new EGLSQLClauseTree();
		for (int ii = 0; ii < sqlClauses.size(); ii++) {
			aClause = (EGLSQLClauseTree) (sqlClauses.get(ii));	
			newClauseTree.tokens.addAll(aClause.tokens);
		}
		return newClauseTree;
	}

	public EGLSQLClauseTree getHostVariables() {
		EGLSQLClauseTree aClause = null;
		EGLSQLClauseTree hostVarsTree = new EGLSQLClauseTree();
		for (int ii = 0; ii < sqlClauses.size(); ii++) {
			aClause = (EGLSQLClauseTree) (sqlClauses.get(ii));
			addHostVariableTokens(hostVarsTree, aClause);
		}
		return hostVarsTree;
	}

	private void addHostVariableTokens(EGLSQLClauseTree hostVars, EGLSQLClauseTree primeTokens) {

		if (primeTokens == null){
			return;
		}
		else {
			int start = 0;
			for (int ii= start; ii < primeTokens.size(); ii++) {
				if ( ((EGLPrimeToken)(primeTokens.getToken(ii))).getType() == EGLPrimeToken.HOST_VAR_COLON ) {
					//process the name that follows, make it all one token
					EGLPrimeToken colon = (EGLPrimeToken)(primeTokens.getToken(ii));
					StringBuffer hostVar = new StringBuffer();
					ii++;
					while (ii <primeTokens.size() && primeTokens.getToken(ii).isHostVar()) {	
						hostVar.append(primeTokens.getToken(ii).getText());	
						ii++;						
					}
					ii= ii - 1;  // account for nested looping and both incrementing the controller
					hostVars.addToken(new EGLPrimeToken(EGLPrimeToken.IDENTIFIER,hostVar.toString(),colon.getOffset()+1, colon.getLine(), 
							colon.getColumn()+1) );
				}	
			}
			return; 
		}
	}
	
	public EGLSQLClauseTree getHostVariablesAsEntered(WithInlineSQLClause sqlClause) {
		EGLSQLClauseTree aClause = null;
		EGLSQLClauseTree hostVarsTree = new EGLSQLClauseTree();
		for (int ii = 0; ii < sqlClauses.size(); ii++) {
			aClause = (EGLSQLClauseTree) (sqlClauses.get(ii));
			addHostVariableTokensAsEntered(hostVarsTree, aClause, sqlClause);
		}
		return hostVarsTree;
	}

	/**
	 * add the host variable to the clause tree, maintaining whitespace as 
	 * entered by the user.
	 * @param hostVars
	 * @param primeTokens
	 */
	private void addHostVariableTokensAsEntered(EGLSQLClauseTree hostVars, EGLSQLClauseTree primeTokens, WithInlineSQLClause sqlClause) {
		
		if (primeTokens == null) {
			return;
		}
		else {	
			String sqlString = sqlClause.getSqlStmt().getValue();
			int start = 0;
			for (int ii= start; ii < primeTokens.size(); ii++) {
				if ( ((EGLPrimeToken)(primeTokens.getToken(ii))).getType() == EGLPrimeToken.HOST_VAR_COLON ) {
					//process the name that follows, make it all one token
					String hostVar = null;
					EGLPrimeToken firstToken = null;
					EGLPrimeToken lastToken = null;
					ii++;
					while (ii <primeTokens.size() && primeTokens.getToken(ii).isHostVar()) {	
						if (firstToken == null)
							firstToken = ((EGLPrimeToken)(primeTokens.getToken(ii)));
						lastToken = ((EGLPrimeToken)(primeTokens.getToken(ii)));
						ii++;
		
					}
					ii= ii - 1;  // account for nested looping and both incrementing the controller
					if (firstToken != null) {
						hostVar = sqlString.substring(firstToken.getOffset(), lastToken.getOffset() + lastToken.getText().length());
						hostVars.addToken(new EGLPrimeToken(EGLPrimeToken.IDENTIFIER,
															hostVar,
															firstToken.getOffset(), 
															firstToken.getLine(), 
															firstToken.getColumn()) );
					}
				}	
			}
			return; 
		}
	}
	
	public void addBlanks(StringBuffer str, int count ) {
		if (count > 0)
			for (int ii = 1; ii<=count; ii++) {
				str.append(" "); //$NON-NLS-1$
			}		
	}
	
	public void addCRLF(StringBuffer str, int count ) {
		if (count >0)
			for (int ii = 1; ii<=count; ii++) {
				str.append("\r\n"); //$NON-NLS-1$
			}		
	}

	
	public EGLSQLClauseTree getSelectTokens() {
		return getSpecificTokens(EGLPrimeToken.SELECT);
	}

	public EGLSQLClauseTree getIntoTokens() {
		return getSpecificTokens(EGLPrimeToken.INTO);
	}

	public EGLSQLClauseTree getFromTokens() {
		return getSpecificTokens(EGLPrimeToken.FROM);
	}

	public EGLSQLClauseTree getCallTokens() {
		return getSpecificTokens(EGLPrimeToken.CALL);
	}
	public EGLSQLClauseTree getColumnsTokens() {
		return getSpecificTokens(EGLPrimeToken.COLUMNS);
	}

	public EGLSQLClauseTree getValuesTokens() {
		return getSpecificTokens(EGLPrimeToken.VALUES);
	}

	public EGLSQLClauseTree getUpdateTokens() {
		return getSpecificTokens(EGLPrimeToken.UPDATE);
	}

	public EGLSQLClauseTree getSetTokens() {
		return getSpecificTokens(EGLPrimeToken.SET);
	}
	
	public EGLSQLClauseTree getDeleteTokens() {
		return getSpecificTokens(EGLPrimeToken.DELETE);
	}

	public EGLSQLClauseTree getHavingTokens() {
		return getSpecificTokens(EGLPrimeToken.HAVING);
	}

	public EGLSQLClauseTree getWhereTokens() {
		return getSpecificTokens(EGLPrimeToken.WHERE);
	}

	public EGLSQLClauseTree getWhereCurrentOfTokens() {
		return getSpecificTokens(EGLPrimeToken.CURRENT);
	}
	
	public String getWhereCurrentOfResultSetIdentifier() {
		EGLSQLClauseTree whereCurrent = getSpecificTokens(EGLPrimeToken.CURRENT);
		if (whereCurrent.tokens.size()>= 3)
			return ((EGLPrimeToken)(whereCurrent.tokens.get(3))).getText();
		else return null;
	}	

	public EGLSQLClauseTree getOrderByTokens() {
		return getSpecificTokens(EGLPrimeToken.ORDER);
	}

	public EGLSQLClauseTree getGroupByTokens() {
		return getSpecificTokens(EGLPrimeToken.GROUP);
	}

	public EGLSQLClauseTree getInsertIntoTokens() {
		return getSpecificTokens(EGLPrimeToken.INSERT);
	}

	public EGLSQLClauseTree getForUpdateOfTokens() {
		return getSpecificTokens(EGLPrimeToken.FOR);
	}

	public ArrayList getSqlClauses() {
		return sqlClauses;
	}
	
	public ArrayList getSqlClauseKeywordsUsed() {
		ArrayList clauseKeywords = new ArrayList();
		for ( int ii = 0; ii < sqlClauses.size(); ii++) {
			clauseKeywords.add(((EGLSQLClauseTree)(sqlClauses.get(ii))).getClauseKeyword().getText().toLowerCase() );
		}	
		return clauseKeywords;
	}

	public ArrayList getReservedClauseKeywordsUsed() {
		return reservedClauseKeywordsUsed;
	}


	public boolean hasErrors(){
		if (problems.size()>0)
			return true;
			else return false;
	}
	public ArrayList getErrors(){
		return problems;
	}
	public boolean hasUnion() {
		return hasUnion;
	}
	public void setHasUnion(boolean hasUnion) {
		this.hasUnion = hasUnion;
	}	

}
