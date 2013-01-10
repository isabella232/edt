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
package org.eclipse.edt.mof;

import java.util.List;

/**
 * All MOF classes are instances of <code>EClass</code> including EClass itself.  
 * An EClass embodies in MOF the Object Oriented concept of Class. EClasses contain
 * <code>EMember</code>s which are <code>EField</code>s and <code>EFunction</code>s.
 * The can also multiply inherit from other EClasses.
 * 
 * All EObject instances are created through the <code>EClass.newInstance()</code> methods.
 * If a static java class has been created to be able to manipulate a given EClass instance, 
 * the <code>newInstance()</code> methods will create an instance of that implementation class;
 * otherwise an instance of EObject is created instead.
 * @see EClassImpl.getClazz()
 *
 */
public interface EClass extends EClassifier, EMemberContainer {
	
	List<EField> getEFields();
	List<EFunction> getEFunctions();
	List<EClass> getSuperTypes();
	void addSuperTypes(List<EClass> superTypes);
	
	EField getEField(String name);
	EField getEField(int index);
	
	List<EField> getAllEFields();
	
	EObject newInstance();
	EObject newInstance(boolean init);
	EObject newInstance(boolean init, boolean useInitialValues);
	void initialize(EObject object);
	void initialize(EObject object, boolean useInitialValues);
	
	boolean isAbstract();
	void setIsAbstract(boolean value);
	
	boolean isInterface();
	void setIsInterface(boolean value);

	boolean isSubClassOf(EClass eClass);
	boolean isInstance(EObject obj);
}
