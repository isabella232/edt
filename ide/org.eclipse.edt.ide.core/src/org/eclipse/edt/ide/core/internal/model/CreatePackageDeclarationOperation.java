/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;


/**
 * <p>This operation adds/replaces a package declaration in an existing compilation unit.
 * If the compilation unit already includes the specified package declaration,
 * it is not generated (it does not generate duplicates).
 *
 * <p>Required Attributes:<ul>
 *  <li>Compilation unit element
 *  <li>Package name
 * </ul>
 */
public class CreatePackageDeclarationOperation /* extends CreateElementInEGLFileOperation */ {
	/**
	 * The name of the package declaration being created
	 */
	protected String fName = null;
/**
 * When executed, this operation will add a package declaration to the given compilation unit.
 */
//public CreatePackageDeclarationOperation(String name, IEGLFile parentElement) {
//	super(parentElement);
//	fName= name;
//}
/**
 * @see CreateTypeMemberOperation#generateElementDOM
 */
//protected IDOMNode generateElementDOM() throws EGLModelException {
//	IEGLElement[] children = getEGLFile().getChildren();
//	//look for an existing package declaration
//	for (int i = 0; i < children.length; i++) {
//		if (children[i].getElementType() ==  IEGLElement.PACKAGE_DECLARATION) {
//			IPackageDeclaration pck = (IPackageDeclaration) children[i];
//			IDOMPackage pack = (IDOMPackage) ((EGLElement)pck).findNode(fCUDOM);
//			if (!pack.getName().equals(fName)) {
//				 // get the insertion position before setting the name, as this makes it a detailed node
//				 // thus the start position is always 0
//				DOMNode node = (DOMNode)pack;
//				fInsertionPosition = node.getStartPosition();
//				fReplacementLength = node.getEndPosition() - fInsertionPosition + 1;
//				pack.setName(fName);
//				fCreatedElement = (com.ibm.etools.egl.internal.model.internal.core.jdom.DOMNode)pack;
//			} else {
//				//equivalent package declaration already exists
//				fCreationOccurred= false;
//			}
//			
//			return null;
//		}
//	}
//	IDOMPackage pack = (new DOMFactory()).createPackage();
//	pack.setName(fName);
//	return pack;
//}
///**
// * Creates and returns the handle for the element this operation created.
// */
//protected IEGLElement generateResultHandle() {
//	return getEGLFile().getPackageDeclaration(fName);
//}
///**
// * @see CreateElementInCUOperation#getMainTaskName()
// */
//public String getMainTaskName(){
//	return Util.bind("operation.createPackageProgress"); //$NON-NLS-1$
//}
///**
// * Sets the correct position for new package declaration:<ul>
// * <li> before the first import
// * <li> if no imports, before the first type
// * <li> if no type - first thing in the CU
// * <li> 
// */
//protected void initializeDefaultPosition() {
//	try {
//		IEGLFile cu = getEGLFile();
//		IImportDeclaration[] imports = cu.getImports();
//		if (imports.length > 0) {
//			createBefore(imports[0]);
//			return;
//		}
//		IPart[] types = cu.getParts();
//		if (types.length > 0) {
//			createBefore(types[0]);
//			return;
//		}
//	} catch (EGLModelException npe) {
//	}
//}
///**
// * Possible failures: <ul>
// *  <li>NO_ELEMENTS_TO_PROCESS - no compilation unit was supplied to the operation 
// *  <li>INVALID_NAME - a name supplied to the operation was not a valid
// * 		package declaration name.
// * </ul>
// * @see IEGLModelStatus
// * @see EGLConventions
// */
//public IEGLModelStatus verify() {
//	IEGLModelStatus status = super.verify();
//	if (!status.isOK()) {
//		return status;
//	}
//	if (EGLConventions.validatePackageName(fName).getSeverity() == IStatus.ERROR) {
//		return new EGLModelStatus(IEGLModelStatusConstants.INVALID_NAME, fName);
//	}
//	return EGLModelStatus.VERIFIED_OK;
//}
}
