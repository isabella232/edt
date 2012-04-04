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
package org.eclipse.edt.ide.rui.visualeditor.internal.outline;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetManager;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


/**
 * Class for serializing widgets to/from a byte array
 */
public class WidgetTransfer extends ByteArrayTransfer {
	private static final String TYPE_NAME = "egl-rui-widget-transfer-format";
	private static final int TYPEID = registerType(TYPE_NAME);
	private WidgetManager manager;

	public WidgetTransfer( WidgetManager manager ) {
		this.manager = manager;
	}

	/*
	 * Method declared on Transfer.
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/*
	 * Method declared on Transfer.
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/*
	 * Method declared on Transfer.
	 */
	protected void javaToNative(Object object, TransferData transferData) {
		byte[] bytes = toByteArray((WidgetPart[]) object);
		if (bytes != null)
			super.javaToNative(bytes, transferData);
	}

	/*
	 * Method declared on Transfer.
	 */
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}
	
	protected WidgetPart[] fromByteArray(byte[] bytes) {
		DataInputStream in = new DataInputStream( new ByteArrayInputStream(bytes) );

		try {
			int n = in.readInt();
			WidgetPart[] widgets = new WidgetPart[n];
			for (int i = 0; i < n; i++) {
				WidgetPart widget = readWidget(null, in);
				if (widget == null) {
					return null;
				}
				widgets[i] = widget;
			}
			return widgets;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Reads and returns a single widget from the given stream.
	 */
	private WidgetPart readWidget(WidgetPart parent, DataInputStream dataIn)
			throws IOException {
		int iOffset = dataIn.readInt();
		int iLength = dataIn.readInt();
		WidgetPart newParent = manager.getWidget( iOffset, iLength );
		return newParent;
	}

	/**
	 * Transfer data is an array of widgets. Serialized version is: (int)
	 * number of widgets , widgets 1 (StatementOffset(), StatementLength()) widget 2 ... repeat for
	 * each subsequent widget see writeWidget for the (Widget) format.
	 */
	protected byte[] toByteArray(WidgetPart[] widgets) {

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);

		byte[] bytes = null;

		try {
			/* write number of markers */
			out.writeInt(widgets.length);

			/* write markers */
			for (int i = 0; i < widgets.length; i++) {
				writeWidget((WidgetPart) widgets[i], out);
			}
			out.close();
			bytes = byteOut.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * Writes the given widget to the stream.
	 */
	private void writeWidget(WidgetPart widget, DataOutputStream dataOut)
			throws IOException {
		dataOut.writeInt( widget.getStatementOffset() );
		dataOut.writeInt( widget.getStatementLength() );
	}
}
