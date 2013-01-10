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
package org.eclipse.edt.compiler.internal.egl2mof;

import org.eclipse.edt.compiler.Context;
import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.TypeStore;


public class Egl2Mof extends Egl2MofExpression {

	private static class SyntaxErrorRequestor implements ISyntaxErrorRequestor {

		boolean error;

		public void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset) {

			error = true;
		}

		public void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void incorrectTerminal(int terminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void keywordAsName(int terminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void invalidCharacterInHexLiteral(int startOffset, int endOffset) {
			error = true;
		}

		public void invalidEscapeSequence(int startOffset, int endOffset) {
			error = true;
		}

		public void missingEndForPart(int startOffset, int endOffset) {
			error = true;
		}

		public void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void missingPreviousTerminal(int terminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void missingScopeCloser(int terminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void missingTerminal(int terminalType, int startOffset, int endOffset) {
			error = true;
		}

		public void panicPhrase(int startOffset, int endOffset) {
			error = true;
		}

		public void tooManyErrors() {
			error = true;
		}

		public void unclosedBlockComment(int startOffset, int endOffset) {
			error = true;
		}

		public void unclosedDLI(int startOffset, int endOffset) {
			error = true;
		}

		public void unclosedSQL(int startOffset, int endOffset) {
			error = true;
		}

		public void unclosedSQLCondition(int startOffset, int endOffset) {
			error = true;
		}

		public void unclosedString(int startOffset, int endOffset) {
			error = true;
		}

		public void unexpectedPhrase(int startOffset, int endOffset) {
			error = true;
		}

		public void unexpectedPreviousTerminal(int startOffset, int endOffset) {
			error = true;
		}

		public void unexpectedTerminal(int startOffset, int endOffset) {
			error = true;
		}

		public void whitespaceInDLI(int startOffset, int endOffset) {
		}

		public void whitespaceInSQL(int startOffset, int endOffset) {
		}

		public void whitespaceInSQLCondition(int startOffset, int endOffset) {
		}

		public boolean isError() {
			return error;
		}

	}
			
 	public Egl2Mof(IEnvironment env) {
		super(env);
	}

 	public Egl2Mof(IEnvironment env, TypeStore store) {
		this(env);
		env.setDefaultSerializeStore(Type.EGL_KeyScheme, store);
	}
	
	public EObject convert(org.eclipse.edt.compiler.core.ast.Part part, Context context, IProblemRequestor problemRequestor) {
		this.context = context;
		Environment.pushEnv(env);
		EObject mofPart = null;
		try {
			part.accept(this);
			mofPart = (EObject)stack.pop();
			// If the EClassifier has name different than the original Part
			// due to conflicts with EGL keywords for instance, save
			// the result at the original typeSignature as well
			if (mofPart instanceof EClassifier) {
				EClassifier ePart = (EClassifier)mofPart;
				org.eclipse.edt.mof.egl.Part binding = (org.eclipse.edt.mof.egl.Part)part.getName().resolveType();
				if (!ePart.getName().equalsIgnoreCase(binding.getName())) {
					env.save(binding.getMofSerializationKey(), ePart, false);
				}
			}
			
			if (problemRequestor != null && part != null && mofPart instanceof Classifier) {
				Classifier irPart = (Classifier) mofPart;
				irPart.setHasCompileErrors(hasCompileErrors(problemRequestor, part));
			}
		}
		finally {
			Environment.popEnv();
		}
		return mofPart;
	}
	
	private boolean hasCompileErrors(IProblemRequestor problemRequestor, Part node) {
		if (problemRequestor != null && problemRequestor.hasError()) {
			return true;
		}

		SyntaxErrorRequestor req = new SyntaxErrorRequestor();
		node.accept(req);

		return req.error;
	}

		
}
