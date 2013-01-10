/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.errors;

public class SyntaxErrorRecoverer {
	private ParseStack stack;
	private TokenStream tokenStream;
	
	private AbstractRecoverer[] recoverers = new AbstractRecoverer[] {
		new UnexpectedTerminalRecoverer(),
		new MissingTerminalRecoverer(),
		new InvalidTerminalRecoverer(),
		new MissingNonTerminalRecoverer(),
		new InvalidPhraseRecoverer()
	};
	
	public SyntaxErrorRecoverer(ParseStack stack, TokenStream tokenStream) {
		this.stack = stack;
		this.tokenStream = tokenStream;
	}
	
	public AbstractRecoverer recover() {
		int bestRecovery = selectRecovery();
		recoverers[bestRecovery].recover(stack, tokenStream);
		
		return recoverers[bestRecovery];
	}
	
	private int selectRecovery() {
		for (int i = 0; i < recoverers.length; i++) {
			if(recoverers[i].recoverDistance(stack, tokenStream) >= AbstractRecoverer.SUCCESS_DISTANCE) {
				return i;
			}
		}
		
		return -1;
	}
}
