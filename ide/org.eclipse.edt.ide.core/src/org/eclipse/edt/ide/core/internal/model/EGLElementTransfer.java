/*******************************************************************************
 * Copyright © 2004, 2013 IBM Corporation and others.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


/**
 * @author jqian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EGLElementTransfer extends ByteArrayTransfer {


	/**
	 * Singleton instance.
	 */
	private static final EGLElementTransfer fInstance= new EGLElementTransfer();

	// Create a unique ID to make sure that different Eclipse
	// applications use different "types" of <code>JavaElementTransfer</code>
	private static final String TYPE_NAME= "egl-element-transfer-format:" + System.currentTimeMillis() + ":" + fInstance.hashCode(); //$NON-NLS-2$//$NON-NLS-1$

	private static final int TYPEID= registerType(TYPE_NAME);
	
	/**
	 * 
	 */
	public EGLElementTransfer() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static EGLElementTransfer getInstance() {
		return fInstance;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
	protected void javaToNative(Object data, TransferData transferData) {
		if (!(data instanceof IEGLElement[]))
			return;

		IEGLElement[] eglElements= (IEGLElement[]) data;
		/*
		 * The element serialization format is:
		 *  (int) number of element
		 * Then, the following for each element:
		 *  (String) handle identifier
		 */

		try {
			ByteArrayOutputStream out= new ByteArrayOutputStream();
			DataOutputStream dataOut= new DataOutputStream(out);

			//write the number of elements
			dataOut.writeInt(eglElements.length);

			//write each element
			for (int i= 0; i < eglElements.length; i++) {
				writeEGLElement(dataOut, eglElements[i]);
			}

			//cleanup
			dataOut.close();
			out.close();
			byte[] bytes= out.toByteArray();
			super.javaToNative(bytes, transferData);
		} catch (IOException e) {
			//it's best to send nothing if there were problems
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
	 */
	protected Object nativeToJava(TransferData transferData) {
		/*
		 * The element serialization format is:
		 *  (int) number of element
		 * Then, the following for each element:
		 *  (String) handle identifier
		 */

		byte[] bytes= (byte[]) super.nativeToJava(transferData);
		if (bytes == null)
			return null;
		DataInputStream in= new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			int count= in.readInt();
			IEGLElement[] results= new IEGLElement[count];
			for (int i= 0; i < count; i++) {
				results[i]= readEGLElement(in);
			}
			return results;
		} catch (IOException e) {
			return null;
		}
	}

	private IEGLElement readEGLElement(DataInputStream dataIn) throws IOException {
		String handleIdentifier= dataIn.readUTF();
		return EGLCore.create(handleIdentifier);
	}

	private static void writeEGLElement(DataOutputStream dataOut, IEGLElement element) throws IOException {
		dataOut.writeUTF(element.getHandleIdentifier());
	}	
}
