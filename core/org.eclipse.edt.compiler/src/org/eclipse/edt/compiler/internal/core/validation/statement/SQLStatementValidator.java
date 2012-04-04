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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.ArrayList;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLPrimeToken;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLSQLClauseTree;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLSQLParser;



/**
 * @author Jason Peterson
 */
public class SQLStatementValidator {

	public static void checkAddClauses(WithInlineSQLClause withInlineSQLClause, EGLSQLParser parser, IProblemRequestor problemRequestor) {
		ArrayList clauseKeywords = parser.getSqlClauseKeywordsUsed();

		//See if required clauses are there first
		if (!clauseKeywords.contains(IEGLConstants.SQLKEYWORD_INSERT)) {
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.REQUIRED_SQL_CLAUSE_MISSING,
				new String[] {IEGLConstants.KEYWORD_ADD.toUpperCase(), IEGLConstants.SQLKEYWORD_INSERT.toUpperCase() + " " + //$NON-NLS-1$
				IEGLConstants.SQLKEYWORD_INTO.toUpperCase() });
		}
		if (!clauseKeywords.contains("columns")) { //$NON-NLS-1$
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.REQUIRED_SQL_COLUMNS_CLAUSE_MISSING,
				new String[] {IEGLConstants.KEYWORD_ADD.toUpperCase()});
		}
		if (!clauseKeywords.contains(IEGLConstants.SQLKEYWORD_VALUES)) {
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.REQUIRED_SQL_CLAUSE_MISSING,
				new String[] {IEGLConstants.KEYWORD_ADD.toUpperCase(), IEGLConstants.SQLKEYWORD_VALUES.toUpperCase()});
		}
		// now check for order of clauses extra clauses, dupe clauses
		checkAddClauseOrder(withInlineSQLClause, problemRequestor, clauseKeywords);
		checkAddExtraDupeClauses(withInlineSQLClause, parser, problemRequestor);	
	}

	public static int getPartitionEndOffset(WithInlineSQLClause withInlineSQLClause) {
		return getPartitionStartOffset(withInlineSQLClause) + withInlineSQLClause.getSqlStmt().getLength();
	}

	public static int getPartitionStartOffset(WithInlineSQLClause withInlineSQLClause) {
		return withInlineSQLClause.getSqlStmt().getOffset();
	}
	
	public static int getStatementStartOffset(WithInlineSQLClause withInlineSQLClause) {
		return withInlineSQLClause.getSqlStmt().getValueOffset();
	}

	protected static void checkAddExtraDupeClauses(WithInlineSQLClause withInlineSQLClause, 
			EGLSQLParser parser, IProblemRequestor problemRequestor) {
		
		ArrayList allClauses = parser.getSqlClauses(); // array of EGLSQLClauseTree objects
		EGLSQLClauseTree clause = parser.getInsertIntoTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		clause = parser.getColumnsTokens();
		if (clause != null) {
			allClauses.remove(clause);
		}
		clause = parser.getValuesTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause); 
		}
		//anything that is left is either a duplicate or is invalid for the Add statement
		for (int ii = 0; ii < allClauses.size(); ii++) {
			EGLPrimeToken clauseKeyword = ((EGLSQLClauseTree) allClauses.get(ii)).getClauseKeyword();
			if (startsWithParen(allClauses, ii, clauseKeyword)) {
				continue;
			}
			if (clauseKeyword.getType() == EGLPrimeToken.INSERT || clauseKeyword.getType() == EGLPrimeToken.VALUES) {
				problemRequestor.acceptProblem(
					getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
					getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
					IMarker.SEVERITY_ERROR,
					IProblemRequestor.SQL_CLAUSES_DUPLICATED,
					new String[] {IEGLConstants.KEYWORD_ADD.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});
			}
			else { 
				problemRequestor.acceptProblem(
					getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
					getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
					IMarker.SEVERITY_ERROR,
					IProblemRequestor.SQL_CLAUSE_UNSUPPORTED,
					new String[] {IEGLConstants.KEYWORD_ADD.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});		
			}
		}		 
	}

	protected static void checkAddClauseOrder(WithInlineSQLClause withInlineSQLClause, 
			IProblemRequestor problemRequestor, ArrayList clauseKeywords) {
		
		// check for order
		// INSERT INTO must be first
		if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_INSERT) > -1 
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_VALUES) > -1 
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_INSERT) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_VALUES)) {
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
				new String[] {IEGLConstants.KEYWORD_ADD.toUpperCase(), IEGLConstants.SQLKEYWORD_INSERT.toUpperCase() + " " + //$NON-NLS-1$
				IEGLConstants.SQLKEYWORD_INTO.toUpperCase(), IEGLConstants.SQLKEYWORD_VALUES.toUpperCase()});
		}

		// COLUMNS must be second after INSERT INTO, before VALUES
		if (clauseKeywords.indexOf("columns") > -1 //$NON-NLS-1$
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_VALUES) > -1 
				&& clauseKeywords.indexOf("columns") > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_VALUES)) {	//$NON-NLS-1$
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.SQL_COLUMNS_CLAUSES_OUT_OF_ORDER_2,
				new String[] {IEGLConstants.KEYWORD_ADD.toUpperCase(), IEGLConstants.SQLKEYWORD_VALUES.toUpperCase()});
		}	 
	}
	
	public static void checkEmptyClause(WithInlineSQLClause withInlineSQLClause, EGLSQLClauseTree clause, EGLSQLParser parser, IProblemRequestor problemRequestor) {
		int keywords = 1; // number of keywords for clause
		if (clause.getThirdKeyword() != null)
			keywords = 3;
		else if (clause.getSecondKeyword() != null)
			keywords = 2;
		if (clause.size() == keywords) { // #tokens = # keywords	      
			int token = clause.size() - 1;
			if (keywords == 2) {
				token--;
			}
			if (keywords == 3) {
				token -= 2;
			}
			problemRequestor.acceptProblem(
				getStatementStartOffset(withInlineSQLClause) + clause.getClauseKeyword().getOffset(), 
				getStatementStartOffset(withInlineSQLClause) + clause.getClauseKeyword().getOffset() + clause.getToken(token).getText().length(),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.CLAUSE_CANT_BE_EMPTY,
				new String[] {getKeyword(clause)});		
		}
		if (clause.getClauseKeyword().getType() == EGLPrimeToken.WHERE && clause.size() > 2 && clause.getToken(1).getType() == EGLPrimeToken.HOST_VAR_COLON) {
			EGLSQLClauseTree whereClause = parser.getClauseWOCommentsAndHostVariables(clause);
			if (whereClause.size() == 1) {
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clause.getToken(1).getOffset(),
						getStatementStartOffset(withInlineSQLClause) + clause.getLastToken().getOffset()
								+ clause.getLastToken().getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.HOST_VARIABLE_CANT_BE_WHERE_CLAUSE);
			}
		}
	
	}
	
	protected static String getKeyword(EGLSQLClauseTree tree) {
		StringBuffer keywordText = new StringBuffer();
		keywordText.append(tree.getClauseKeyword().getText().toUpperCase());
		if (tree.getSecondKeyword() != null) {
			keywordText.append(" " + tree.getSecondKeyword().getText().toUpperCase()); //$NON-NLS-1$
			if (tree.getThirdKeyword() != null) {
				keywordText.append(" " + tree.getThirdKeyword().getText().toUpperCase()); //$NON-NLS-1$
			}
		}
		return keywordText.toString();
	}

	public static void checkReplaceClauses(WithInlineSQLClause withInlineSQLClause, EGLSQLParser parser, IProblemRequestor problemRequestor) {
		ArrayList clauseKeywords = parser.getSqlClauseKeywordsUsed();

		//See if required clauses are there first
		if (!clauseKeywords.contains(IEGLConstants.SQLKEYWORD_UPDATE)) {
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
					IMarker.SEVERITY_ERROR,
					IProblemRequestor.REQUIRED_SQL_CLAUSE_MISSING,
					new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.SQLKEYWORD_UPDATE.toUpperCase()});
		}
		if (!clauseKeywords.contains(IEGLConstants.SQLKEYWORD_SET)) {
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
					IMarker.SEVERITY_ERROR,
					IProblemRequestor.REQUIRED_SQL_CLAUSE_MISSING,
					new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.SQLKEYWORD_SET.toUpperCase()});
		}
		// now check for order of clauses extra clauses, dupe clauses
		checkReplaceClauseOrder(withInlineSQLClause, parser, problemRequestor, clauseKeywords);
		checkReplaceExtraDupeClauses(withInlineSQLClause, parser, problemRequestor);	
	}

	protected static void checkReplaceExtraDupeClauses(WithInlineSQLClause withInlineSQLClause, 
			EGLSQLParser parser, IProblemRequestor problemRequestor) {
		
		ArrayList allClauses = parser.getSqlClauses(); // array of EGLSQLClauseTree objects
		EGLSQLClauseTree clause = parser.getUpdateTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		clause = parser.getSetTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		EGLSQLClauseTree whereCurrentClause = parser.getWhereCurrentOfTokens();
		if (whereCurrentClause != null) {
			checkEmptyClause(withInlineSQLClause, whereCurrentClause, parser, problemRequestor);
			allClauses.remove(whereCurrentClause);
		}
		EGLSQLClauseTree whereClause = parser.getWhereTokens();
		if (whereClause != null) {
			checkEmptyClause(withInlineSQLClause, whereClause, parser, problemRequestor);	
		}	
		if (whereCurrentClause == null) {
			allClauses.remove(whereClause);
		}
		//anything that is left is either a duplicate or is invalid for the Replace statement
		for (int ii = 0; ii < allClauses.size(); ii++) {
			EGLPrimeToken clauseKeyword = ((EGLSQLClauseTree) allClauses.get(ii)).getClauseKeyword();
			if (startsWithParen(allClauses, ii, clauseKeyword)) {
				continue;
			}
			if (clauseKeyword.getType() == EGLPrimeToken.UPDATE
					|| clauseKeyword.getType() == EGLPrimeToken.SET
					|| (parser.getWhereCurrentOfTokens() != null)
					|| (parser.getWhereTokens() != null)) {
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSES_DUPLICATED,
						new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});	
			}
			else { 
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSE_UNSUPPORTED,
						new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});		
			}
		}
	}

	protected static void checkReplaceClauseOrder(WithInlineSQLClause withInlineSQLClause, 
			EGLSQLParser parser, IProblemRequestor problemRequestor, ArrayList clauseKeywords) {
		
		// check for order
		// update must be before everything else
		if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_UPDATE) > -1
			&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SET) > -1
			&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_UPDATE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SET)) {
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
					IMarker.SEVERITY_ERROR,
					IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
					new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), 
						IEGLConstants.SQLKEYWORD_UPDATE.toUpperCase(),
						IEGLConstants.SQLKEYWORD_SET.toUpperCase()});	
		}
		// set must be before where current of
		if (parser.getWhereCurrentOfTokens() != null) {
			if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SET) > -1
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SET) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
				problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
						new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.SQLKEYWORD_SET.toUpperCase(), 
						IEGLConstants.SQLKEYWORD_WHERE.toUpperCase() + " " + //$NON-NLS-1$
						IEGLConstants.SQLKEYWORD_CURRENT.toUpperCase() + " " + //$NON-NLS-1$
						IEGLConstants.SQLKEYWORD_OF.toUpperCase()});
			}
			if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_UPDATE) > -1
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_UPDATE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
				problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
						new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.SQLKEYWORD_UPDATE.toUpperCase(), 
						IEGLConstants.SQLKEYWORD_WHERE.toUpperCase() + " " + //$NON-NLS-1$
						IEGLConstants.SQLKEYWORD_CURRENT.toUpperCase() + " " + //$NON-NLS-1$
						IEGLConstants.SQLKEYWORD_OF.toUpperCase()});
			}
		}
		//set must be before where
		else if (parser.getWhereTokens() != null) {
			if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SET) > -1
					&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
					&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SET) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
					problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
							new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.SQLKEYWORD_SET.toUpperCase(), 
							IEGLConstants.SQLKEYWORD_WHERE.toUpperCase()});
			}
			if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_UPDATE) > -1
					&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
					&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_UPDATE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
					problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
							new String[] {IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.SQLKEYWORD_UPDATE.toUpperCase(), 
							IEGLConstants.SQLKEYWORD_WHERE.toUpperCase()});
			}
		}
	}

	public static void checkGetAndOpenClauses(WithInlineSQLClause withInlineSQLClause, EGLSQLParser parser, 
			IProblemRequestor problemRequestor, String stmtType, boolean arrayIO) {
		
		ArrayList clauseKeywords = parser.getSqlClauseKeywordsUsed();
		if (clauseKeywords.contains(IEGLConstants.SQLKEYWORD_CALL) && (stmtType.equals(IEGLConstants.KEYWORD_OPEN) || arrayIO )) {
			checkGetAndOpenCallExtraDupeClauses(withInlineSQLClause, parser, problemRequestor);
		} else {
			//See if required clauses are there first
			if (!clauseKeywords.contains(IEGLConstants.SQLKEYWORD_SELECT)) {
				problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.REQUIRED_SQL_CLAUSE_MISSING,
						new String[] {stmtType.toUpperCase(), IEGLConstants.SQLKEYWORD_SELECT.toUpperCase()});
			}
			if (!clauseKeywords.contains(IEGLConstants.SQLKEYWORD_FROM)) {
				problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.REQUIRED_SQL_CLAUSE_MISSING,
						new String[] {stmtType.toUpperCase(), IEGLConstants.SQLKEYWORD_FROM.toUpperCase()});
			}
			// now check for order of clauses extra clauses, dupe clauses
			checkGetAndOpenClauseOrder(withInlineSQLClause, problemRequestor, clauseKeywords, stmtType);
			checkGetAndOpenExtraDupeClauses(withInlineSQLClause, parser, problemRequestor, stmtType);
		}
	}

	protected static void checkGetAndOpenCallExtraDupeClauses(WithInlineSQLClause withInlineSQLClause, EGLSQLParser parser, IProblemRequestor problemRequestor) {
		ArrayList allClauses = (ArrayList) parser.getSqlClauses().clone(); // array of EGLSQLClauseTree objects
		EGLSQLClauseTree clause = parser.getCallTokens();
		if (clause != null) {
			allClauses.remove(clause);
		}
		//anything that is left is either a duplicate or is invalid for the Get by key statement
		for (int ii = 0; ii < allClauses.size(); ii++) {
			EGLPrimeToken clauseKeyword = ((EGLSQLClauseTree) allClauses.get(ii)).getClauseKeyword();
			if (startsWithParen(allClauses, ii, clauseKeyword)) {
				continue;
			}
			if (clauseKeyword.getType() == EGLPrimeToken.CALL) {
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSES_DUPLICATED,
						new String[] {IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.SQLKEYWORD_CALL.toUpperCase()});	
			} else if (clauseKeyword.getType() == EGLPrimeToken.SELECT
					|| clauseKeyword.getType() == EGLPrimeToken.FROM
					|| clauseKeyword.getType() == EGLPrimeToken.FOR		
					|| clauseKeyword.getType() == EGLPrimeToken.WHERE
					|| clauseKeyword.getType() == EGLPrimeToken.GROUP			
					|| clauseKeyword.getType() == EGLPrimeToken.HAVING
					|| clauseKeyword.getType() == EGLPrimeToken.ORDER) {
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.CANT_BE_USED_WITH_CALL,
						new String[] {IEGLConstants.KEYWORD_OPEN.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});	
			} else {
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSE_UNSUPPORTED,
						new String[] {IEGLConstants.KEYWORD_OPEN.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});
			}
		}	
	}

	private static boolean startsWithParen(ArrayList allClauses, int ii, EGLPrimeToken clauseKeyword) {
		if (clauseKeyword.getType() == EGLPrimeToken.EXECUTE) {
			EGLSQLClauseTree executeSQL = ((EGLSQLClauseTree) allClauses.get(ii));
			if (executeSQL.size() == 1 && executeSQL.getToken(0).getType() == EGLPrimeToken.L_PAREN) {
				return true;
			}
		}
		return false;
	}

	protected static void checkGetAndOpenExtraDupeClauses(WithInlineSQLClause withInlineSQLClause, EGLSQLParser parser, 
			IProblemRequestor problemRequestor, String stmtType) {
		
		ArrayList allClauses = (ArrayList) parser.getSqlClauses().clone(); // array of EGLSQLClauseTree objects
		EGLSQLClauseTree clause = parser.getSelectTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		clause = parser.getFromTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		clause = parser.getForUpdateOfTokens();
		if (clause != null) {
			if (clause.getThirdKeyword() != null) {
				checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			}
			allClauses.remove(clause);
		}
		clause = parser.getWhereTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		clause = parser.getGroupByTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		clause = parser.getHavingTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		clause = parser.getOrderByTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		//anything that is left is either a duplicate or is invalid for the Get by key statement
		for (int ii = 0; ii < allClauses.size(); ii++) {
			EGLPrimeToken clauseKeyword = ((EGLSQLClauseTree) allClauses.get(ii)).getClauseKeyword();
			if (startsWithParen(allClauses, ii, clauseKeyword)) {
				continue;
			}
			if (clauseKeyword.getType() == EGLPrimeToken.SELECT
					|| clauseKeyword.getType() == EGLPrimeToken.FROM
					|| clauseKeyword.getType() == EGLPrimeToken.FOR
					|| clauseKeyword.getType() == EGLPrimeToken.WHERE
					|| clauseKeyword.getType() == EGLPrimeToken.GROUP
					|| clauseKeyword.getType() == EGLPrimeToken.HAVING
					|| clauseKeyword.getType() == EGLPrimeToken.ORDER) {
				if (!parser.hasUnion()) {
					problemRequestor.acceptProblem(
							getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
							getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.SQL_CLAUSES_DUPLICATED,
							new String[] {stmtType.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});
				}
			}
			else { // unsupported clause type for Get by key
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSE_UNSUPPORTED,
						new String[] {stmtType.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});
			}
		}
	}

	protected static void checkGetAndOpenClauseOrder(WithInlineSQLClause withInlineSQLClause, IProblemRequestor problemRequestor, 
			ArrayList clauseKeywords, String stmtType) {
		 
		// check for order
        // select must be before everything else
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_SELECT.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_FROM.toUpperCase()});
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_SELECT.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_WHERE.toUpperCase()});
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_SELECT.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_ORDER.toUpperCase() + " " + //$NON-NLS-1$
        				IEGLConstants.SQLKEYWORD_BY.toUpperCase() });
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_GROUP) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_GROUP)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_SELECT.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_GROUP.toUpperCase() + " " + //$NON-NLS-1$
                        IEGLConstants.SQLKEYWORD_BY.toUpperCase() });
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_HAVING) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_SELECT) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_HAVING)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_SELECT.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_HAVING.toUpperCase()});
        }
        // From must be after select but before anything else
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_FROM.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_ORDER.toUpperCase() + " " + //$NON-NLS-1$
        				IEGLConstants.SQLKEYWORD_BY.toUpperCase() });
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_FROM.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_WHERE.toUpperCase()});
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_GROUP) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_GROUP)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_FROM.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_GROUP.toUpperCase() + " " + //$NON-NLS-1$
                        IEGLConstants.SQLKEYWORD_BY.toUpperCase()});
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_HAVING) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_HAVING)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_FROM.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_HAVING.toUpperCase()});
        }
        // WHERE (if present) must be after FROM and must be before
        // FOR UPDATE OF, GROUP BY, HAVING, or ORDER BY
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_WHERE.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_ORDER.toUpperCase() + " " + //$NON-NLS-1$
        				IEGLConstants.SQLKEYWORD_BY.toUpperCase() });
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_GROUP) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_GROUP)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_WHERE.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_GROUP.toUpperCase() + " " + //$NON-NLS-1$
                        IEGLConstants.SQLKEYWORD_BY.toUpperCase() });
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_HAVING) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_HAVING)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_WHERE.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_HAVING.toUpperCase()});
        }
        // GROUP BY and HAVING (if present) must be after WHERE and must
        // be before ORDER BY
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_GROUP) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_GROUP) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        		  		IEGLConstants.SQLKEYWORD_GROUP.toUpperCase() + " " + //$NON-NLS-1$
        		  		IEGLConstants.SQLKEYWORD_BY.toUpperCase(), 
        		  		IEGLConstants.SQLKEYWORD_ORDER.toUpperCase() + " " + //$NON-NLS-1$
        		  		IEGLConstants.SQLKEYWORD_BY.toUpperCase() });
        }
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_HAVING) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER) > -1
                && clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_HAVING) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_ORDER)) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
    				new String[] {stmtType.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_HAVING.toUpperCase(), 
        				IEGLConstants.SQLKEYWORD_ORDER.toUpperCase() + " " + //$NON-NLS-1$
        				IEGLConstants.SQLKEYWORD_BY.toUpperCase() });
        }
        // FOR UPDATE must be last
        if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FOR) > -1 
        		&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FOR) < clauseKeywords.size() - 1) {
        	problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
    				IMarker.SEVERITY_ERROR,
    				IProblemRequestor.FOR_UPDATE_MUST_BE_LAST,
    				new String[] {stmtType.toUpperCase()});
        }
	}

	public static void checkForUpdateClause(WithInlineSQLClause withInlineSQLClause, ArrayList clauseKeywords, 
			IProblemRequestor problemRequestor, String stmtType) {
		
		if (!clauseKeywords.contains(IEGLConstants.SQLKEYWORD_FOR)) {
	            String insertOne = stmtType.toUpperCase() + " " //$NON-NLS-1$  
	                    + IEGLConstants.KEYWORD_FORUPDATE.toUpperCase();
	            String insertTwo = IEGLConstants.SQLKEYWORD_FOR.toUpperCase() + " " //$NON-NLS-1$
	                    + IEGLConstants.SQLKEYWORD_UPDATE.toUpperCase() 
	            	    + " " + IEGLConstants.SQLKEYWORD_OR + " " //$NON-NLS-1$ //$NON-NLS-2$
	            	    + IEGLConstants.SQLKEYWORD_FOR.toUpperCase() + " " //$NON-NLS-1$
	                    + IEGLConstants.SQLKEYWORD_UPDATE.toUpperCase() + " "	//$NON-NLS-1$
	                    + IEGLConstants.SQLKEYWORD_OF.toUpperCase();
	            problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
	    				IMarker.SEVERITY_ERROR,
	    				IProblemRequestor.REQUIRED_SQL_CLAUSE_MISSING,
	    				new String[] {insertOne, insertTwo});
		}
	}

	public static void checkDeleteClauses(WithInlineSQLClause withInlineSQLClause, EGLSQLParser parser, IProblemRequestor problemRequestor) {
		ArrayList clauseKeywords = parser.getSqlClauseKeywordsUsed();

		//See if required clauses are there first
		if (!clauseKeywords.contains(IEGLConstants.SQLKEYWORD_DELETE)) {
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
					IMarker.SEVERITY_ERROR,
					IProblemRequestor.REQUIRED_SQL_CLAUSE_MISSING,
					new String[] {IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.SQLKEYWORD_DELETE.toUpperCase()});
		}
		// now check for order of clauses extra clauses, dupe clauses
		checkDeleteClauseOrder(withInlineSQLClause, parser, problemRequestor, clauseKeywords);
		checkDeleteExtraDupeClauses(withInlineSQLClause, parser, problemRequestor);	
	}

	private static void checkDeleteExtraDupeClauses(WithInlineSQLClause withInlineSQLClause, EGLSQLParser parser, 
			IProblemRequestor problemRequestor) {
		
		ArrayList allClauses = parser.getSqlClauses(); // array of EGLSQLClauseTree objects
		EGLSQLClauseTree clause = parser.getDeleteTokens();
		if (clause != null) {
			allClauses.remove(clause);
		}
		clause = parser.getFromTokens();
		if (clause != null) {
			checkEmptyClause(withInlineSQLClause, clause, parser, problemRequestor);
			allClauses.remove(clause);
		}
		EGLSQLClauseTree whereCurrentClause = parser.getWhereCurrentOfTokens();
		if (whereCurrentClause != null) {
			checkEmptyClause(withInlineSQLClause, whereCurrentClause, parser, problemRequestor);
			allClauses.remove(whereCurrentClause);
		}
		EGLSQLClauseTree whereClause = parser.getWhereTokens();
		if (whereClause != null) {
			checkEmptyClause(withInlineSQLClause, whereClause, parser, problemRequestor);
		}	
		if (whereCurrentClause == null) {
			allClauses.remove(whereClause);
		}
		//anything that is left is either a duplicate or is invalid for the Delete statement
		for (int ii = 0; ii < allClauses.size(); ii++) {
			EGLPrimeToken clauseKeyword = ((EGLSQLClauseTree) allClauses.get(ii)).getClauseKeyword();
			if (startsWithParen(allClauses, ii, clauseKeyword)) {
				continue;
			}
			if (clauseKeyword.getType() == EGLPrimeToken.DELETE
					|| clauseKeyword.getType() == EGLPrimeToken.FROM
					|| (parser.getWhereCurrentOfTokens() != null)
					|| (parser.getWhereTokens() != null)) {
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSES_DUPLICATED,
						new String[] {IEGLConstants.KEYWORD_DELETE.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});	
			}
			else { 
				problemRequestor.acceptProblem(
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset(), 
						getStatementStartOffset(withInlineSQLClause) + clauseKeyword.getOffset() + clauseKeyword.getText().length(), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSE_UNSUPPORTED,
						new String[] {IEGLConstants.KEYWORD_DELETE.toUpperCase(), getKeyword((EGLSQLClauseTree) allClauses.get(ii))});		
			}
		}
	}

	private static void checkDeleteClauseOrder(WithInlineSQLClause withInlineSQLClause, 
			EGLSQLParser parser, IProblemRequestor problemRequestor, ArrayList clauseKeywords) {
		
		// check for order
		// Delete must be before everything else
		if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_DELETE) > -1
			&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > -1
			&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_DELETE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM)) {
			problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
					IMarker.SEVERITY_ERROR,
					IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
					new String[] {IEGLConstants.KEYWORD_DELETE.toUpperCase(), 
						IEGLConstants.SQLKEYWORD_DELETE.toUpperCase(),
						IEGLConstants.SQLKEYWORD_FROM.toUpperCase()});	
		}
		// from must be before where current of
		if (parser.getWhereCurrentOfTokens() != null) {
			if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > -1
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
				problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
						new String[] {IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.SQLKEYWORD_FROM.toUpperCase(), 
						IEGLConstants.SQLKEYWORD_WHERE.toUpperCase() + " " + //$NON-NLS-1$
						IEGLConstants.SQLKEYWORD_CURRENT.toUpperCase() + " " + //$NON-NLS-1$
						IEGLConstants.SQLKEYWORD_OF.toUpperCase()});
			}
			if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_DELETE) > -1
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
				&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_DELETE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
				problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
						new String[] {IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.SQLKEYWORD_DELETE.toUpperCase(), 
						IEGLConstants.SQLKEYWORD_WHERE.toUpperCase() + " " + //$NON-NLS-1$
						IEGLConstants.SQLKEYWORD_CURRENT.toUpperCase() + " " + //$NON-NLS-1$
						IEGLConstants.SQLKEYWORD_OF.toUpperCase()});
			}
		}
		//from must be before where
		else if (parser.getWhereTokens() != null) {
			if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > -1
					&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
					&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_FROM) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
					problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
							new String[] {IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.SQLKEYWORD_FROM.toUpperCase(), 
							IEGLConstants.SQLKEYWORD_WHERE.toUpperCase()});
			}
			if (clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_DELETE) > -1
					&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE) > -1
					&& clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_DELETE) > clauseKeywords.indexOf(IEGLConstants.SQLKEYWORD_WHERE)) {
					problemRequestor.acceptProblem(getPartitionStartOffset(withInlineSQLClause), getPartitionEndOffset(withInlineSQLClause), 
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.SQL_CLAUSES_OUT_OF_ORDER,
							new String[] {IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.SQLKEYWORD_DELETE.toUpperCase(), 
							IEGLConstants.SQLKEYWORD_WHERE.toUpperCase()});
			}
		}
	}

}
