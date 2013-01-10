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
package org.eclipse.edt.ide.core.internal.errors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author winghong
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FileReaderUtil {
	public static String readFile(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));

			StringBuffer buffer = new StringBuffer();
			
			// Print the first line
			String line = reader.readLine();
			if(line != null) {
				buffer.append(line);
				
				// Print subsequent lines
				while (true) {
					line = reader.readLine();
					if (line == null)
						break;

					buffer.append("\r\n");
					buffer.append(line);
				}
			}
			reader.close();

			return buffer.toString();
		} catch (FileNotFoundException e) {
			System.err.println("Testcase not found");
		} catch (IOException e) {
			System.err.println("Error reading testcase file");
		}

		return null;
	}
}
