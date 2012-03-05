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
package org.eclipse.edt.gen.javascript.templates;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class ExternalTypeTemplate extends JavaScriptTemplate {

	public void preGenClassBody(ExternalType part, Context ctx) {}

	public void genPart(ExternalType part, Context ctx, TabbedWriter out) {
		ctx.invoke(genAMDHeader, part, ctx, out);
		getExternalJSCotent(part, ctx, out);
		out.println("});");
	}
	
	private void getExternalJSCotent(ExternalType part, Context ctx, TabbedWriter out){
		String paths = (String) ctx.getParameter(Constants.parameter_projectPaths);
		String[] allPaths = paths.split(",");
		String filePath = getExternalJSPath(part).replaceAll("\\\\", "/");
		for (int i = 0; i < allPaths.length; i++) {
			File file = new File(allPaths[i] + "/" + Constants.WEB_CONTENT_FOLDER_NAME + "/" + filePath + ".js");
			byte[] bytes = null;
			if(file.exists()){
				DataInputStream is = null;
				try {
					is = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
					bytes = new byte[is.available()];
					is.readFully(bytes);
					String content = new String(bytes);
					out.print(content);
					break;
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
				finally{
					if(is != null){
						try {
							is.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}
	}

	public void genClassBody(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(ExternalType part, Context ctx, TabbedWriter out) {}

	public void genAccessor(ExternalType part, Context ctx, TabbedWriter out) {
		ctx.invoke(genPartName, part, ctx, out);
	}

	public void genRuntimeTypeName(ExternalType part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		if (ctx.mapsToNativeType(part))
			out.print(ctx.getNativeImplementationMapping(part));
		else{
			Annotation annotation = part.getAnnotation(Constants.Annotation_JavaScriptObject);
			if(annotation != null){
				String packageName = (String) annotation.getValue(InternUtil.intern("relativePath"));
				if(packageName != null && !(packageName.isEmpty())){
					packageName = packageName.replace('/', '.');
					packageName = packageName.replace('\\', '.');
					packageName += ".";
				}else{
					packageName = "";
				}
				String partName = (String) annotation.getValue(InternUtil.intern("externalName"));
				if(partName == null || partName.isEmpty()){
					partName = part.getName();
				}
				out.print(eglnamespace + packageName.toLowerCase() + partName);
			}
		}
	}

	public void genQualifier(ExternalType part, Context ctx, TabbedWriter out, NamedElement arg) {
		// out.print("this.");
	}
	
	public Boolean supportsConversion(ExternalType part, Context ctx) {
		return Boolean.FALSE;
	}
	
	public void genModuleName(ExternalType part, StringBuilder buf) {
		Annotation annot = part.getAnnotation( "eglx.javascript.JavaScriptObject" );
		String fullName = "";
		if (annot != null) {
			String pkg = (String)annot.getValue( "relativePath" );
			String name = (String)annot.getValue( "externalName" );
			
			if (pkg == null || pkg.isEmpty()) {
				pkg = part.getPackageName().replace( '.', '/' );
			}
			if (name == null || name.isEmpty()) {
				name = part.getName();
			}
			if (pkg.length() > 0) {
				fullName = JavaScriptAliaser.packageNameAlias(pkg.split("[/]"), '/');
				if (fullName.charAt(fullName.length() - 1) != '/') {
					fullName += "/" ;
				}
			}
			fullName += JavaScriptAliaser.getAliasForExternalType(JavaScriptAliaser.getAlias(name));
		}
		buf.append("\"" + fullName + "\"");
	}

	private String getExternalJSPath(ExternalType part) {
		Annotation annot = part.getAnnotation( "eglx.javascript.JavaScriptObject" );
		String fullName = "";
		if (annot != null) {
			String pkg = (String)annot.getValue( "relativePath" );
			String name = (String)annot.getValue( "externalName" );
			
			if (pkg == null || pkg.isEmpty()) {
				pkg = part.getPackageName().replace( '.', '/' );
			}
			if (name == null || name.isEmpty()) {
				name = part.getName();
			}
			if (pkg.length() > 0) {
				fullName = pkg;
				if (pkg.charAt(pkg.length() - 1) != '/') {
					fullName += "/" ;
				}
			}
			fullName += name;
		}
		return fullName;
	}
}
