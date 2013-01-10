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
package org.eclipse.edt.tests.validation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.eclipse.edt.compiler.internal.core.builder.ConsoleOutProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;

public class ProblemCollectingProblemRequestor extends DefaultProblemRequestor {
	
	public static boolean PRINT_ERRORS = false;
	
	public static class Problem {
		public int startOffset, endOffset, severity;
		public int problemKind;
		public String[] inserts;
		public ResourceBundle bundle;
		
		Problem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
			this.startOffset = startOffset;
			this.endOffset = endOffset;
			this.severity = severity;
			this.problemKind = problemKind;
			this.inserts = inserts;
			this.bundle = bundle;
		}
		
		public Problem(int problemKind, String[] inserts, ResourceBundle bundle) {
			this(0, 0, 0, problemKind, inserts, bundle);
		}
		
		public Problem(int problemKind, ResourceBundle bundle) {
			this(0, 0, 0, problemKind, null, bundle);
		}
		
		public String getErrorMessage() {
			return getMessageFromBundle(problemKind, inserts, bundle);
		}
		
		public String toString() {			
			return getErrorMessage() + ", startOffset = " + startOffset + ", endOffset = " + endOffset + ", severity = " + severity;
		}
		
		public boolean equals(Object obj) {
			if(!(obj instanceof Problem)) return false;
			Problem other = (Problem) obj;
			if(problemKind != other.problemKind) return false;
			if(inserts != null && other.inserts != null && !elementsSame(inserts, other.inserts)) return false;			
			return true;			
		}
		
		private static boolean elementsSame(String[] arr1, String[] arr2) {
			if(arr1.length != arr2.length) {
				return false;
			}
			for(int i = 0; i < arr1.length; i++) {
				if(arr1[i] == null) {
					if(arr2[i] != null) {
						return false;
					}
				}
				else if(!arr1[i].equals(arr2[i])) {
					return false;
				}
			}
			return true;
		}
	}
	
	private List problems = new ArrayList();
	Problem[] expectedProblems;
	
	public ProblemCollectingProblemRequestor(Problem[] expectedProblems) {
		this.expectedProblems = expectedProblems;		
	}
	
	public ProblemCollectingProblemRequestor() {
		this.expectedProblems = new Problem[0];
	}
	
	public List getProblems() {
		return problems;
	}
	
	@Override
    public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
 		if (severity == IMarker.SEVERITY_ERROR) {
 			setHasError(true);
 		}
 		
 		Problem problem = new Problem(startOffset, endOffset, severity, problemKind, inserts, bundle);
		problems.add(problem);
    	
    	if(PRINT_ERRORS) {
    		ConsoleOutProblemRequestor.getInstance().acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
    	}
    }
    
    public void assertExpectedMatchesProduced() {
    	TestCase.assertEquals("Expected # of problems mismatch;", expectedProblems.length, problems.size());
    	List problemsCopy = new ArrayList(problems);
    	for(int i = 0; i < expectedProblems.length; i++) {
    		int index = problemsCopy.indexOf(expectedProblems[i]);
    		if(index == -1) {
    			TestCase.fail("Expected Problem " + (i+1) + " (" + expectedProblems[i] + ") not found.");
    		}
    		problemsCopy.remove(index);
    	}
    }
}
