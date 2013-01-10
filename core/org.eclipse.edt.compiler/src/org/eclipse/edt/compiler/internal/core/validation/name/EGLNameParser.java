/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.name;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author dollar 
 *
 * Parse a name, collecting messages and breaking
 * it into the respective parts.
 */
public class EGLNameParser {

	private Reader inputReader;
	private EGLNameLexer lexer;
	private IProblemRequestor problemRequestor;
	private EGLNameToken currentToken;
	private EGLNameAndSubscripts nameAndSubscripts;
	private Stack state = new Stack();
	private String nameState = "NAME";
	private String subScriptState = "SUBSCRIPT";
	private String subStringState = "SUBSTRING";
	private String subScriptNameState = "SubscriptName";
	private String subStringNameState = "SubstringName";
	private boolean alreadyConsumed = false;
	private boolean hadSubstringState = false;
	private String inputString = null;
	private boolean parsingFileName = false;

	public EGLNameParser(String input, boolean normalWhiteSpaceChecking, boolean filename, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		inputString = input;
		this.problemRequestor = problemRequestor;
		parsingFileName = filename;
		inputReader = new StringReader(input);
		lexer = new EGLNameLexer(inputReader, 0, parsingFileName, problemRequestor, compilerOptions);
		currentToken = null;
		nameAndSubscripts = new EGLNameAndSubscripts();

		try {
			//	//  System.out.println("Starting input = " + input);
				parseName();
				
				
		} catch (Exception iox) {
			// end of file
		}
	}

	/**
	  * Parse the name held by inputReader into tokens, placing them in a 
	  * token list. 
	  *
	  */
	private void parseName() throws IOException {
		currentToken = lexer.getNextToken(parsingFileName);
		state.push(nameState);
		while (currentToken != null) 
		{
			alreadyConsumed = false;
			switch (currentToken.getType()) {
				case (EGLNameToken.IDENTIFIER) :
				{
					updateState();
				//	//  System.out.println("State = " + (String)state.peek() + " Good ident = " + currentToken.toString() );
					if (((String)state.peek()).equalsIgnoreCase(nameState) ) {
						ArrayList names = nameAndSubscripts.getNames();
						if(names.isEmpty()) {
							nameAndSubscripts.addName(currentToken);
						}
						else {
							EGLNameToken lastName = (EGLNameToken) names.get(names.size()-1);
							if(lastName.getText().endsWith(",")) {
								names.remove(lastName);
								names.add(new EGLNameToken(lastName.getType(), lastName.getText() + currentToken.getText(), lastName.getOffset(), lastName.getLine(), currentToken.getColumn()));
							}
							else {
								nameAndSubscripts.addName(currentToken);
							}
						}
					}
					else if (((String)state.peek()).equalsIgnoreCase(subScriptNameState)) {
						nameAndSubscripts.addSubscript(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subStringNameState)) {
					 	nameAndSubscripts.addSubstring(currentToken);
					}					
					break;
				}
				case (EGLNameToken.REAL_NUMBER) :
				case (EGLNameToken.FLOAT_NUMBER) :
				{
					updateState();
				//	//  System.out.println("State = " + (String)state.peek() + " real number = " + currentToken.toString() );
					//error will be put out in the Name Validator
					if (((String)state.peek()).equalsIgnoreCase(nameState) ) {
						nameAndSubscripts.addName(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subScriptState)) {
						nameAndSubscripts.addSubscript(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subScriptNameState)) {
						nameAndSubscripts.addSubscript(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subStringState)) {
					 	nameAndSubscripts.addSubstring(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subStringNameState)) {
					 	nameAndSubscripts.addSubstring(currentToken);
					}
					break;
				}				
				case (EGLNameToken.QUOTED_STRING) :
				{
					updateState();

					//  System.out.println("State = " + (String)state.peek() + " integer = " + currentToken.toString() );
					if (((String)state.peek()).equalsIgnoreCase(nameState) ) {
						// error will be put out in name validator
						nameAndSubscripts.addName(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subScriptNameState)) {
						nameAndSubscripts.addSubscript(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subStringNameState)) {
					 	nameAndSubscripts.addSubstring(currentToken);
					}
					break;
				}
				case (EGLNameToken.INTEGER) :
				{
					updateState();

					//  System.out.println("State = " + (String)state.peek() + " integer = " + currentToken.toString() );
					if (((String)state.peek()).equalsIgnoreCase(nameState) ) {
						// error will be put out in name validator
						nameAndSubscripts.addName(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subScriptNameState)) {
						nameAndSubscripts.addSubscript(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subStringNameState)) {
					 	nameAndSubscripts.addSubstring(currentToken);
					}
					break;
				}
				case (EGLNameToken.COMMENT) :
				{
					//  System.out.println("State = " + (String)state.peek() + " comment = " + currentToken.toString() );
					//FRIEDA - error if we want to restrict where comments can be 				
					break;
				}				
				case (EGLNameToken.L_SQUARE) :
				{
					if (((String)state.peek()).equalsIgnoreCase(subScriptState)||
					    ((String)state.peek()).equalsIgnoreCase(subScriptNameState) ||
						((String)state.peek()).equalsIgnoreCase(subStringState) ||
						((String)state.peek()).equalsIgnoreCase(subStringNameState)) {
					 	// error - nested subscripts
						problemRequestor.acceptProblem(
							currentToken.getOffset(),      
							currentToken.getOffset()+1,
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.INVALID_SUBSCRIPT_NESTING,
							new String[] { inputString });
					}
					updateState();
					//  System.out.println("State = " + (String)state.peek() + " left bracket = " + currentToken.toString() );
 					
					if (!(alreadyConsumed)) {
						// errors issued elsewhere
						nameAndSubscripts.newSubscript();
					}
	
					break;
				}								
				case (EGLNameToken.R_SQUARE) :
				{
					if ((((String)state.peek()).equalsIgnoreCase(subScriptState) ||
						((String)state.peek()).equalsIgnoreCase(subStringState)	) ) {
//						 //error - empty brackets
						problemRequestor.acceptProblem(
							currentToken.getOffset(),      
							currentToken.getOffset()+1,
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.P_FOUND_EMPTY_BRACKETS,
							new String[] { inputString });	
					}
					if ( ((String)state.peek()).equalsIgnoreCase(nameState))  {
						 	// error - right bracket when not in subscript or substing one
						problemRequestor.acceptProblem(
							currentToken.getOffset(),      
							currentToken.getOffset()+1,
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.P_FOUND_RBRACKET_WRONG,
							new String[] { inputString });	
						}
					updateState();
					//  System.out.println("State = " + (String)state.peek() + " right bracket = " + currentToken.toString() );
					break;
				}				
				case (EGLNameToken.DOT) :
				{
					updateState();
					//  System.out.println("State = " + (String)state.peek() + " dot = " + currentToken.toString() );
 					//if name begins with dot, error is issued from name validator
					
					if (((String)state.peek()).equalsIgnoreCase(nameState) ) {
						nameAndSubscripts.addName(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subScriptState) ||
							 ((String)state.peek()).equalsIgnoreCase(subScriptNameState)) {
						nameAndSubscripts.addSubscript(currentToken);
					}
					else if (((String)state.peek()).equalsIgnoreCase(subStringState) ||
							((String)state.peek()).equalsIgnoreCase(subStringNameState)) {
					 	nameAndSubscripts.addSubstring(currentToken);
					}
					break;
				}
				case (EGLNameToken.COLON) :
				{
					if (state.peek().equals(subScriptState) ||  // name[:
							 state.peek().equals(subStringState) ) {  // name[: shouldn't happen
						//  System.out.println("Missing from location");
					 	// error - right bracket when not in subscript or substing one
						problemRequestor.acceptProblem(
							currentToken.getOffset(),      
							currentToken.getOffset()+1,
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.P_MISSING_FROM_SUBSTRING,
							new String[] { inputString });
					}
					else if (state.peek().equals(subStringNameState) ) { //name[name:name: or [name::
						//  System.out.println("too many substrings");
					 	// error - right bracket when not in subscript or substing one
						problemRequestor.acceptProblem(
							currentToken.getOffset(),      
							currentToken.getOffset()+1,
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.P_TOO_MANY_SUBSTRINGS,
							new String[] { inputString });
					}

					// Need to remove from subscript list and put in substring list 

					updateState();
					//  System.out.println("State = " + (String)state.peek() + " colon = " + currentToken.toString() );
					if (((String)state.peek()).equalsIgnoreCase(nameState) ) {
						nameAndSubscripts.addName(currentToken);
						//if name begins with dot, error is issued from name validator
					}
					else if (!(alreadyConsumed) ) {
						nameAndSubscripts.newSubstring();
						nameAndSubscripts.copySubscriptToSubstring();				
						nameAndSubscripts.newSubstring();
					}
					break;
				}				
				case (EGLNameToken.COMMA) :
				{
					//comma anywhere in a name error issued from name validator
					if (((String)state.peek()).equalsIgnoreCase(nameState) ) {
						ArrayList names = nameAndSubscripts.getNames();
						EGLNameToken lastName = (EGLNameToken) names.get(names.size()-1);
						names.remove(lastName);
						names.add(new EGLNameToken(lastName.getType(), lastName.getText() + ",", lastName.getOffset(), lastName.getLine(), lastName.getColumn()+1));
					}
					else {
						updateState();
						//  System.out.println("State = " + (String)state.peek() + " comma = " + currentToken.toString() );
						nameAndSubscripts.newSubscript();
					}
					break;
				}								
								
				case (EGLNameToken.UNKNOWN_EGL) :
				{
					//  System.out.println("State = " + (String)state.peek() + " invalid token = " + currentToken.toString() );
					problemRequestor.acceptProblem(
						currentToken.getOffset(),      
						currentToken.getOffset() + currentToken.getText().length(),
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.P_UNRECOGNIZED_TOKEN, 
						new String[] { currentToken.getText(), inputString });
					break;
				}

				default :
				{
					//  System.out.println("Taking default -  " + currentToken.toString()  );
				}
			}
			if (!(alreadyConsumed)) {
				currentToken = lexer.getNextToken(parsingFileName);
			}
		}			

		if ( state.isEmpty() ||
			!(state.peek().equals(nameState)) ) {
			// error - didn't terminate name properly (subscript or substring not closed
			//  System.out.println("Didn't terminate properly.");
			problemRequestor.acceptProblem(
				inputString.lastIndexOf('['),      
				inputString.lastIndexOf('['),
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.P_SUBSCRIPT_OR_SUBSTRING_NOT_CLOSED,
				new String[] { inputString });
		}
			
		return ;
	}
	/**
	  * Parse the name held by inputReader into tokens, placing them in a 
	  * token list. 
	  *
	  */
	private void updateState()  throws IOException  {
 
		if (hadSubstringState) {
			//error - can't have anything after substring
			problemRequestor.acceptProblem(
				currentToken.getOffset(),      
				currentToken.getOffset() + currentToken.getText().length()+1,
				IMarker.SEVERITY_ERROR,
				IProblemRequestor.P_SUBSTRING_NOT_LAST,
				new String[] { currentToken.getText(), inputString });
			//  System.out.println("Found token after substring. Token = " + currentToken.toString());
			consumeRest();
			return;
		}
		switch (currentToken.getType()) {
			case (EGLNameToken.IDENTIFIER) :
			case (EGLNameToken.REAL_NUMBER) :
			case (EGLNameToken.DOT) : 
			case (EGLNameToken.COMMA) :
			{
				if (state.isEmpty())
					state.push(nameState);
				else if (state.peek().equals(subScriptState))
					state.push(subScriptNameState);
				else if (state.peek().equals(subStringState))
					state.push(subStringNameState);
				break;
			}
			
			case (EGLNameToken.INTEGER) :	
			case (EGLNameToken.QUOTED_STRING) :	
			{
				if (state.isEmpty()) {
					//error will be issued from the name validator
					//  System.out.println("State error - found int with empty stack " );
					state.push(nameState);
				}
				else if (state.peek().equals(subScriptState))
					state.push(subScriptNameState);
				else if (state.peek().equals(subStringState))
					state.push(subStringNameState);
				break;
			}
			
					
			case (EGLNameToken.L_SQUARE) :
			{
				if (state.isEmpty()){
					//error will be issued from the name validator
					//  System.out.println("State error - found [ with empty stack " );
					alreadyConsumed = consumeBadSubscript();
				}
				else if ( !(state.peek().equals(nameState)) ) {
					//nested subscript error - will be issued by parseName 
					//  System.out.println("State error - found [ when not in main name (nested)" );
					alreadyConsumed = consumeBadSubscript();
				}
				else
					state.push(subScriptState);
				break;
			}								
			case (EGLNameToken.R_SQUARE) :
			{
				if (state.isEmpty()){
					//error issued by parseName
					//  System.out.println("State error - found ] with empty stack " );				
				}
				else if (state.peek().equals(subScriptState) ) {
						//error issued by parseName
					//  System.out.println("State error - found ] in subscriptState " );
					state.pop();
				}
				else if (state.peek().equals(subStringState) ) {
						//error issued by ParseName
					//  System.out.println("State error - found ] in substringState " );
					state.pop();
				}
				else if (state.peek().equals(nameState)) {
						//error issued by ParseName
					//  System.out.println("State error - found ] in nameState " );
				}	
				else {
					String currentState = (String)state.pop();
					if (currentState.equals(subStringNameState))
						hadSubstringState = true;
					while (currentState.equals(subScriptNameState) ||
							currentState.equals(subStringNameState) ){
						currentState = (String)state.pop();
					}
				}
				break;
			}				

			case (EGLNameToken.COLON) :
			{
				if (state.isEmpty()){
					//error issued from nameValidator
					//  System.out.println("State error - found : with empty stack " );
					state.push(nameState);
				}
				else if (state.peek().equals(subScriptState) ||  // name[:
						 state.peek().equals(subStringState) ) {  // name[: shouldn't happen
					// error issued from parseName 
					//  System.out.println("Missing from location");
					state.push(subStringNameState);
				}
				else if (state.peek().equals(subStringNameState) ) { //name[name:name: or [name::
					// error issued from parseName
					//  System.out.println("too many substrings");
					consumeRest();
				}
				else if (state.peek().equals(nameState)) { //name:
						//error issued from nameValidator
					//  System.out.println("State error - found : in error location " );
					state.push(nameState);
				}
				else if (state.peek().equals(subScriptNameState)){ // change to substringNameState
					String currentState = (String)state.pop();
					while (currentState.equals(subScriptNameState)){
						currentState = (String)state.pop();
					}
					state.push(subStringState);
					state.push(subStringNameState);
				}
				break;
			}				


			case (EGLNameToken.COMMENT) :				
			default :
			{
				if (state.isEmpty()) {
					state.push(nameState);
				}
				//else no state update to make
				break;
			}
		}			

		return ;
	}
	
	public boolean consumeBadSubscript() throws IOException {
		int bracketCount = 1;
		currentToken = lexer.getNextToken(parsingFileName);		
		while (currentToken != null &&
				bracketCount >0 ) 
		{
			switch (currentToken.getType()) {
				case (EGLNameToken.L_SQUARE) :
				{  
					bracketCount = bracketCount + 1;
				}
				case (EGLNameToken.R_SQUARE) :
				{
					bracketCount = bracketCount - 1;
					
				}
			}
			//  System.out.println("Consuming Token = " + currentToken.toString() );
			currentToken = lexer.getNextToken(parsingFileName);				
			
		}
		if (bracketCount != 0) {
			//error issued in parseName - ran out of input before found matching brackets
			//  System.out.println("unmatched left bracket"); 
		}
		return true;
	}

	public void consumeRest() throws IOException {
	
		while (currentToken != null  ) 
		{
			//  System.out.println("Consuming Token = " + currentToken.toString() );
			currentToken = lexer.getNextToken(parsingFileName);				
		}
	}	
	
	public EGLNameAndSubscripts getNamesAndSubscripts() {
		return nameAndSubscripts;
	}
	
	public ArrayList getNames() {
		return nameAndSubscripts.getNames();
	}
	
	public ArrayList getSubscripts() {
		return nameAndSubscripts.getSubscripts();
	}	
	
	public ArrayList getFirstSubscript() {
		return ((ArrayList)(nameAndSubscripts.getSubscripts().get(0)));
	}
	
	public ArrayList getSubstrings() {
		return nameAndSubscripts.getSubstrings();
	}
	
	public ArrayList getFirstSubstring() {
		return ((ArrayList)(nameAndSubscripts.getSubstrings().get(0)));
	}
}

