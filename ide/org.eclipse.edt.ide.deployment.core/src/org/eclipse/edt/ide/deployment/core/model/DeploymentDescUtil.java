/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.core.model;

import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.Parameter;

public class DeploymentDescUtil {
	private static final String indent1 = "    ";
	private static final String indent2 = indent1 + indent1;
	private static final String indent3 = indent2 + indent1;
	public static String convertToBindXML(DeploymentDesc desc){
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buf.append("<bindings>\n");
		for(Binding binding : desc.getBindings())
		{
			buf.append(toBindXML(binding));
		}
		buf.append("</bindings>\n");
		return buf.toString();

	}
	
	public static String toBindXML(Binding binding)
	{
		StringBuilder builder = new StringBuilder(indent1);
		builder.append("<binding name=\"");
		if(binding.getName() != null){
			builder.append(binding.getName());
		}
		builder.append("\"");
		if(binding.getType() != null){
			builder.append(" type=\"").append(binding.getType());
			builder.append("\"");
		}
		if(binding.getUri() != null){
			builder.append(" uri=\"").append(binding.getUri());
			builder.append("\"");
		}
		builder.append(" useURI=\"").append(String.valueOf((binding.isUseURI())));
		builder.append("\">\n");
		builder.append(indent2);
		builder.append("<parameters>\n");
		for(Parameter parameter : binding.getParameters())
		{
			builder.append(toBindXML(parameter));
		}
		builder.append(indent2);
		builder.append("</parameters>\n");
		builder.append(indent1);
		builder.append("</binding>\n");
		return builder.toString();
	}

	public static String toBindXML(Parameter parameter)
	{
		StringBuilder builder = new StringBuilder(indent3);
		builder.append("<parameter name=\"");
		if(parameter.getName() != null){
			builder.append(parameter.getName());
		}
		builder.append("\"");
		if(parameter.getType() != null){
			builder.append(" type=\"");
			builder.append(parameter.getType());
			builder.append("\"");
		}
		if(parameter.getValue() != null){
			builder.append(" value=\"").append(parameter.getValue());
			builder.append("\"");
		}
		builder.append("/>\n");
		return builder.toString();
	}

}
