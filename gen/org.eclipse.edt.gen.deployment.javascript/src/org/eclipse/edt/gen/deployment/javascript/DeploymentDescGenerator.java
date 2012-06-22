/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.deployment.javascript;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.edt.gen.deployment.util.CommonUtilities;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.javart.resources.egldd.DedicatedBinding;
import org.eclipse.edt.javart.resources.egldd.RestBinding;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class DeploymentDescGenerator{


	private TabbedWriter writer;
	
	public byte[] generateBindFile( DeploymentDesc deploymentDesc )
	{
		StringWriter stringWriter = new StringWriter();
		writer = new TabbedWriter(stringWriter);
		genBindJSFile(deploymentDesc);

		byte[] result;
		try {
			result = stringWriter.toString().getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException uee) {
			result = stringWriter.toString().getBytes();
		}
		return result;
	}
	
	private void genBindJSFile( DeploymentDesc deploymentDesc ) 
	{
		String eglddName = deploymentDesc.getName().toLowerCase();
		writer.print("egl[\"eze$$BindFile_");
		writer.print(eglddName);
		writer.println("\"] = function() {");
		writer.print("var bindFile = new egl.eglx.services.BindFile(\"");
		writer.print(eglddName);
		writer.println("\");");
		writer.println("var binding;");
		List restBindings = deploymentDesc.getRestBindings();
		for (int i = 0; i < restBindings.size(); i++)
		{
			RestBinding restBinding = (RestBinding) restBindings.get(i);
			if (restBinding.isEnableGeneration())
			{
				writer.println("binding = new egl.eglx.services.RestBinding(");
				writer.pushIndent();
				writer.println("/*name                   */ \"" + restBinding.getName().toLowerCase() + "\",");
				writer.println("/*baseURI                */ \"" + restBinding.getUri() + "\",");
				writer.print("/*sessionCookieId        */ ");
				if (restBinding.getSessionCookieId() != null)
				{
					writer.println("\"" + restBinding.getSessionCookieId() + "\"");
				}
				else
				{
					writer.println("null");
				}			
				writer.popIndent();
				writer.println(");");
				writer.println("bindFile.bindings.push(binding);");
			}
		}
		List dedicatedBindings = deploymentDesc.getDedicatedBindings();
		for (int i = 0; i < dedicatedBindings.size(); i++)
		{
			DedicatedBinding dedicatedBinding = (DedicatedBinding) dedicatedBindings.get(i);
			writer.println("binding = new egl.eglx.services.DedicatedBinding(");
			writer.pushIndent();
			writer.println("/*name                   */ \"" + dedicatedBinding.getName().toLowerCase() + "\"");
			writer.popIndent();
			writer.println(");");
			writer.println("bindFile.bindings.push(binding);");
		}
		for (String include : deploymentDesc.getIncludes())
		{
			writer.println("bindFile.includes.push(\"" + CommonUtilities.toIncludeDDName( include ) + "\");");
		}	
		writer.println("return bindFile;");
		writer.println("};");
	}

}
