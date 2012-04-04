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

import java.util.ArrayList;

/**
 * @author dollar
 *
 */
public class EGLSQLClauseTree {

		ArrayList tokens;
		EGLPrimeToken clauseKeyword = null;
		EGLPrimeToken secondKeyword = null;
		EGLPrimeToken thirdKeyword = null;
	
	public EGLSQLClauseTree( )
	{
		this.tokens =  new ArrayList();
		this.clauseKeyword = null;
		this.secondKeyword = null;
		this.thirdKeyword = null; 
	} 		
	public EGLSQLClauseTree(EGLPrimeToken keyword)
	{
		this.tokens =  new ArrayList();
		this.clauseKeyword = keyword;
		this.secondKeyword = null;
		this.thirdKeyword = null;
	} 	
	public EGLSQLClauseTree(EGLPrimeToken keyword, EGLPrimeToken secondKeyword)
	{
		this.tokens =  new ArrayList();
		this.clauseKeyword = keyword;
		this.secondKeyword = secondKeyword;
		this.thirdKeyword = null;
	} 
	public EGLSQLClauseTree(EGLPrimeToken keyword, EGLPrimeToken secondKeyword, EGLPrimeToken thirdKeyword)
	{
		this.tokens =  new ArrayList();
		this.clauseKeyword = keyword;
		this.secondKeyword = secondKeyword;
		this.thirdKeyword = thirdKeyword;
	} 
	public void addToken(EGLPrimeToken newToken) 
	{
		tokens.add(newToken);
	}	
	public void concat(EGLSQLClauseTree tree) 
	{
		tokens.addAll(tree.tokens);
	}	
	public EGLPrimeToken getClauseKeyword()
	{
		return clauseKeyword;	
	}
	public EGLPrimeToken getSecondKeyword()
	{
		return secondKeyword;	
	}	
	public EGLPrimeToken getThirdKeyword()
	{
		return thirdKeyword;	
	}	
	public EGLPrimeToken getToken(int index) 
	{
		return ((EGLPrimeToken)(tokens.get(index)));
	}	
	public int getClauseType()
	{
		if (clauseKeyword == null)
			return EGLPrimeToken.DEFAULT_SELECT;
		else return clauseKeyword.getType();	
	}	
	public EGLPrimeToken getFirstToken()
	{
		if ( tokens.isEmpty() )
			return null;
		else	
			return ((EGLPrimeToken)(tokens.get(0)));
	}
	public EGLPrimeToken getLastToken()
	{
		if ( tokens.isEmpty() )
			return null;
		else	
			return ((EGLPrimeToken)(tokens.get(tokens.size()-1)));
	}
	public void setClauseKeyword(EGLPrimeToken keyword)
	{
		clauseKeyword = keyword;
	}
	public void setSecondKeyword(EGLPrimeToken secondKeyword)
	{
		this.secondKeyword = secondKeyword;
	}	
	public void setThirdKeyword(EGLPrimeToken thirdKeyword)
	{
		this.thirdKeyword = thirdKeyword;
	}	

	public int size(){
		return tokens.size();
	}

	public String toString() {
	
		int betweenTokens = 0; 
		int CRLFpairs = 0;
		int atEndOfPrevious = 0;
		int atBeginningOfCurrent = 0;
	
		StringBuffer newStr = new StringBuffer(""); //$NON-NLS-1$
	
		if ( tokens.size() > 0 )
		{
			//leading blanks before first token
			addBlanks(newStr, ((EGLPrimeToken)(tokens.get(0))).getColumn() -1  );
			//add first token
			newStr.append(((EGLPrimeToken)(tokens.get(0))).getText());	
		}
	
		//now loop to handle rest of tokens
		for (int ii= 1; ii<tokens.size(); ii++)
		{
			betweenTokens = ((EGLPrimeToken)(tokens.get(ii))).getOffset()
							- ((EGLPrimeToken)(tokens.get(ii-1))).getOffset()
							- ((EGLPrimeToken)(tokens.get(ii-1))).getText().length();
			if ( betweenTokens >0 )
			{
				CRLFpairs = ((EGLPrimeToken)(tokens.get(ii))).getLine()
							 - ((EGLPrimeToken)(tokens.get(ii-1))).getLine();
				if ( CRLFpairs > 0 ) // tokens are on different lines
				{
					atEndOfPrevious = betweenTokens 
									   - (2 * CRLFpairs)
									   - ((EGLPrimeToken)(tokens.get(ii))).getColumn()	             				
									   + 1;
					atBeginningOfCurrent = betweenTokens
											- (2 * CRLFpairs)
											- atEndOfPrevious;				   
				}
				else // tokens are on the same line
				{
					atEndOfPrevious = betweenTokens; 
					atBeginningOfCurrent = 0;
				}							
			}			
			else
			{
				CRLFpairs = 0;
				atEndOfPrevious = 0;
				atBeginningOfCurrent = 0;
			}		   
			// add blanks at end of previous token
			addBlanks(newStr, atEndOfPrevious);
			// add pairs of CR/LF
			addCRLF(newStr, CRLFpairs); 
			//leading blanks before token
			addBlanks(newStr, atBeginningOfCurrent );
			//add token
			newStr.append(((EGLPrimeToken)(tokens.get(ii))).getText());		
		}		

		return newStr.toString();
	}

	public void addBlanks(StringBuffer str, int count ) {
	
		if (count > 0)
			for (int ii = 1; ii<=count; ii++)
			{
				str.append(" "); //$NON-NLS-1$
			}		
	}
	public void addCRLF(StringBuffer str, int count ) {
	
		if (count >0)
			for (int ii = 1; ii<=count; ii++)
			{
				str.append("\r\n"); //$NON-NLS-1$
			}		
	}

	public String toStringNoComments() {
	
		int betweenTokens = 0; 
		int CRLFpairs = 0;
		int atEndOfPrevious = 0;
		int atBeginningOfCurrent = 0;
	
		StringBuffer newStr = new StringBuffer(""); //$NON-NLS-1$
		EGLPrimeToken prevToken = null;
		int ii = 0;

		while (ii<tokens.size())
		{
			// find next non comment token to put on string
			while (ii<tokens.size() 
				&& ((EGLPrimeToken)(tokens.get(ii))).getType() == EGLPrimeToken.SQLCOMMENT  ){
					ii++; 
			}
			if (ii < tokens.size()) {
				if (prevToken == null){
					//leading blanks before first token
					addBlanks(newStr, ((EGLPrimeToken)(tokens.get(ii))).getColumn() -1  );
					//add first token
					newStr.append(((EGLPrimeToken)(tokens.get(ii))).getText());
					prevToken = ((EGLPrimeToken)(tokens.get(ii)));								
				}
				else {
					betweenTokens = ((EGLPrimeToken)(tokens.get(ii))).getOffset()
								- prevToken.getOffset()
								- prevToken.getText().length();
					if ( betweenTokens >0 )
					{
						CRLFpairs = ((EGLPrimeToken)(tokens.get(ii))).getLine()
									 - prevToken.getLine();
						if ( CRLFpairs > 0 ) // tokens are on different lines
						{
							atEndOfPrevious = betweenTokens 
											   - (2 * CRLFpairs)
											   - ((EGLPrimeToken)(tokens.get(ii))).getColumn()	             				
											   + 1;
							atBeginningOfCurrent = betweenTokens
													- (2 * CRLFpairs)
													- atEndOfPrevious;				   
						}
						else // tokens are on the same line
						{
							atEndOfPrevious = betweenTokens; 
							atBeginningOfCurrent = 0;
						}							
					}			
					else
					{
						CRLFpairs = 0;
						atEndOfPrevious = 0;
						atBeginningOfCurrent = 0;
					}		   
					// add blanks at end of previous token
					addBlanks(newStr, atEndOfPrevious);
					// add pairs of CR/LF
					addCRLF(newStr, CRLFpairs); 
					//leading blanks before token
					addBlanks(newStr, atBeginningOfCurrent );
					//add token
					newStr.append(((EGLPrimeToken)(tokens.get(ii))).getText());	
					prevToken = ((EGLPrimeToken)(tokens.get(ii)));
				}
			}
			ii++;	
		}		

		return newStr.toString();
	}
	
	public String toStringNoWhiteSpace() {
	
		StringBuffer newStr = new StringBuffer(""); //$NON-NLS-1$
	
		for (int ii = 0; ii<tokens.size(); ii++)
		{
			//add token
			newStr.append(((EGLPrimeToken)(tokens.get(ii))).getText());		
		}		

		return newStr.toString();
	}
	

	}
