/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.engine;

import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.ComposeGenNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.gen.GenNode;


public class RepeatResolver implements IGenVariableResolver {

	public void resolve(GenNode genNode) {
		if(genNode instanceof ComposeGenNode){
			ComposeGenNode composeGenNode = (ComposeGenNode)genNode;
			String template = composeGenNode.getTemplate();
			if(template.contains(IGenVariableResolver.StartRepeat) && template.contains(IGenVariableResolver.EndRepeat)){
				String oldRepeatString = template.substring(template.indexOf(IGenVariableResolver.StartRepeat), template.indexOf(IGenVariableResolver.EndRepeat)+IGenVariableResolver.EndRepeat.length()).trim();
				String newRepeatString = oldRepeatString.replace(IGenVariableResolver.StartRepeat, "");
				newRepeatString = newRepeatString.replace(IGenVariableResolver.EndRepeat, "").trim();
				
				StringBuffer sbRepeat = new StringBuffer("\t\t\t");
				List<GenNode> children = composeGenNode.getChildren();
				for(int i=0; i<children.size(); i++){
					GenNode child = children.get(i);
					String oldTemplate = child.getTemplate();
					child.setTemplate(newRepeatString);
					GenVariableResolverManager.getInstance().resolve(child);
					sbRepeat.append(child.getTemplate()).append(",").append("\n\t\t\t");
					child.setTemplate(oldTemplate);
				}
				String repeat = sbRepeat.toString();
				if(children.isEmpty()){
					repeat = "";
				}else{
					repeat = repeat.substring(0, repeat.lastIndexOf(","));
				}
				template = template.replace(oldRepeatString, repeat);
				composeGenNode.setTemplate(template);
			}
			
		}
	}

}
