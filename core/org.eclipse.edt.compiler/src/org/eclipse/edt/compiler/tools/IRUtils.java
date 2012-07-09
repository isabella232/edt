/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.tools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class IRUtils {

	public final static char[] SUFFIX_eglxml = ".eglxml".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGLXML = ".EGLXML".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_eglbin = ".eglbin".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGLBIN = ".EGLBIN".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_mofxml = ".mofxml".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_MOFXML = ".MOFXML".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_mofbin = ".mofbin".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_MOFBIN = ".MOFBIN".toCharArray(); //$NON-NLS-1$
	
	/**
	 * Returns true iff str.toLowerCase().endsWith(".eglxml" or ".eglbin" or ".mofxml" or ".mofbin")
	 * implementation is not creating extra strings.
	 */	
	public static boolean isEGLIRFileName(String name) {
		if (name == null) {
			return false;
		}
		return IRUtils.matchesFileName(name, IRUtils.SUFFIX_eglxml, IRUtils.SUFFIX_EGLXML)
				|| IRUtils.matchesFileName(name, IRUtils.SUFFIX_eglbin, IRUtils.SUFFIX_EGLBIN)
				|| IRUtils.matchesFileName(name, IRUtils.SUFFIX_mofxml, IRUtils.SUFFIX_MOFXML)
				|| IRUtils.matchesFileName(name, IRUtils.SUFFIX_mofbin, IRUtils.SUFFIX_MOFBIN);
	}

	public final static boolean matchesFileName(String name, char[] lowercaseExtension, char[] uppercaseExtension) {
		int nameLength = name.length();
		int suffixLength = lowercaseExtension.length;
		if (nameLength < suffixLength)
			return false;
		for (int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++) {
			char c = name.charAt(offset + i);
			if (c != lowercaseExtension[i] && c != uppercaseExtension[i])
				return false;
		}
		return true;
	}

	//FIXME should be in the extension plugin
	public static boolean isIBMi(CallStatement call){
		if(call.getUsing() != null){
			ITypeBinding binding = call.getUsing().resolveTypeBinding();
			return isType(binding, InternUtil.intern(new String[]{"eglx", "jtopen"}), InternUtil.intern("IBMiConnection"), new ArrayList<ITypeBinding>());
		}
		else{
			Expression exp = call.getInvocationTarget();
			IDataBinding binding = exp.resolveDataBinding();
			//only service can have a Service type qualifier with no using clause
			return Binding.isValidBinding(binding) && 
					!isFunctionServiceQualified(exp, binding) &&
					binding.getAnnotation(new String[]{"eglx", "jtopen","annotations"}, "IBMiProgram") != null;
		}
	}
	
	//FIXME should be in the extension plugin
	public static boolean isService(CallStatement call){
		if(call.getUsing() != null){
			ITypeBinding binding = call.getUsing().resolveTypeBinding();
			return isType(binding, InternUtil.intern(new String[]{"eglx", "http"}), InternUtil.intern("IHttp"), new ArrayList<ITypeBinding>());
		}
		else{
			Expression exp = call.getInvocationTarget();
			IDataBinding binding = exp.resolveDataBinding();
			return Binding.isValidBinding(binding) &&
					(binding.getAnnotation(new String[]{"eglx", "rest"}, "Rest") != null ||
					binding.getAnnotation(new String[]{"eglx", "rest"}, "EGLService") != null ||
					isFunctionServiceQualified(exp, binding));
		}
	}
	
	private static boolean isFunctionServiceQualified(	Expression exp, IDataBinding binding){
		return !(exp instanceof FieldAccess && ((FieldAccess)exp).getPrimary() instanceof ThisExpression) &&
				Binding.isValidBinding(binding.getDeclaringPart()) && 
				binding.getDeclaringPart().getKind() == ITypeBinding.SERVICE_BINDING;
	}
	private static boolean isType(ITypeBinding type, String[] testTypePackage, String testTypeName, List<ITypeBinding> seenTypes) {
		
		if(!Binding.isValidBinding(type)){
			return false;
		}
		if (seenTypes.contains(type)) {
			return false;
		}
		seenTypes.add(type);
		
		if (Binding.isValidBinding(type) && ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
			if (type.getName() == testTypeName && type.getPackageName() == testTypePackage) {
				return true;
			}
		}

		List<ITypeBinding> interfaces = getImplementedInterfaces(type);
		if (interfaces != null) {
			for(ITypeBinding imp : interfaces){
				if (isType(imp, testTypePackage, testTypeName, seenTypes)) {
					return true;
				}
			}
		}
		

		return false;
	}
	private static List<ITypeBinding> getImplementedInterfaces(ITypeBinding type) {
		
		if (Binding.isValidBinding(type) && ITypeBinding.HANDLER_BINDING == type.getKind()) {
			HandlerBinding hand = (HandlerBinding)type;
			return hand.getImplementedInterfaces();
		}

		if (Binding.isValidBinding(type) && ITypeBinding.INTERFACE_BINDING == type.getKind()) {
			InterfaceBinding inter = (InterfaceBinding)type;
			return inter.getExtendedTypes();
		}

		if (Binding.isValidBinding(type) && ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
			ExternalTypeBinding et = (ExternalTypeBinding)type;
			return et.getExtendedTypes();
		}

		return null;
	}

}
