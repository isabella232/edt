/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.document.utils;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;

public class SettingsBlockLocator extends DefaultASTVisitor {

	private SettingsBlock block;
		
	public boolean visit(Handler handler){
		List contents = handler.getContents();
		for (Iterator iter = contents.iterator(); iter.hasNext();) {
			Node element = (Node) iter.next();
			element.accept(new DefaultASTVisitor(){
				public boolean visit(SettingsBlock settingsBlock) {
					block = settingsBlock;
					return false;
				};
			});
			if(block != null){
				break;
			}
		}
		return false;
	}
	
	public SettingsBlock getSettingsBlock(){
		return block;
	}
}
