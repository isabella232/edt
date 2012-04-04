/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.compiler.internal.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * <code>Encoder</code> encodes strings to make them difficult to memorize.
 * For example, this can be used to encode passwords in a file so that someone looking over your shoulder
 * won't be able to deteremine the password. This is not a substitute for using proper access control for
 * the file, or encryption.
 */

public class Encoder {

    /** the usage string */
    public final static String USAGE =
        "Usage: Encoder [-action encode|decode] [-key <key-name>] [-in <input-file-name>] [-out <output-file-name>]"; //$NON-NLS-1$

    /** the encode action */
    public final static String ENCODE_ACTION = "encode"; //$NON-NLS-1$

    /** the decode action */
    public final static String DECODE_ACTION = "decode"; //$NON-NLS-1$

    /** the default action is "encode" */
    public final static String DEFAULT_ACTION = ENCODE_ACTION;

    /** the default property key value to encode is "password" */
    public final static String DEFAULT_KEY = "password"; //$NON-NLS-1$

    /** the option to specify an action name */
    public final static String ACTION_OPTION = "-action"; //$NON-NLS-1$

    /** the option to specify a key name */
    public final static String KEY_OPTION = "-key"; //$NON-NLS-1$

    /** the option to specify an input file */
    public final static String INPUT_OPTION = "-in"; //$NON-NLS-1$

    /** the option to specify an output file */
    public final static String OUTPUT_OPTION = "-out"; //$NON-NLS-1$

    /** the output file name or null for standard output */
    private String outputFileName;

    /** the input file name or null for standard input */
    private String inputFileName;

    /** the key name */
    private String keyName = DEFAULT_KEY;

    /** the action */
    private String action = DEFAULT_ACTION;

    /** the properties list */
    private Properties properties;

    /** the prefix for encoded text - DO NOT change this value without talking to gen/runtime folks.
     	We want to keep this prefix insynch!!! **/
    private final static String ENCODED_PREFIX = "crypto:"; //$NON-NLS-1$
    //needed to migrate old encoding to new encoding
    private final static String OLD_ENCODED_PREFIX = "encoded:"; //$NON-NLS-1$

    /** the character encoding */
    private final static String CHARACTER_ENCODING = "utf-8"; //$NON-NLS-1$
/**
 * Decodes an encoded text string.
 *
 * @return the decoded text string
 * @param encodedText the encoded text string to decode
 * @exception IllegalArgumentException if the input text is not encoded correctly
 */
public static String decode(String encodedText)
    throws IllegalArgumentException {
	return encodedText;
//    if (!isEncoded(encodedText)) {
//
//        throw new IllegalArgumentException("Text is not encoded."); //$NON-NLS-1$
//    }
//
//    //If string was encoded using the old encoding, need to decode using old encoding for migration purposes.
//    if (encodedText.startsWith(OLD_ENCODED_PREFIX)) {
//        byte[] data8 = EGLOldDecoder.decode(encodedText.substring(OLD_ENCODED_PREFIX.length()));
//        int length8 = data8.length;
//        if (length8 % 8 != 0) {
//
//    	    throw new IllegalArgumentException("Encoded text has wrong length."); //$NON-NLS-1$
//        }
//        
//        int length = length8 / 8;
//        byte[] data = new byte[length];
//        int[] mask = {1, 2, 4, 8, 16, 32, 64, 128};
//        for (int i = 0; i < length; i++) {
//    	    for (int j = 0; j < 8; j++) {
//
//    		    int k = j * length + i;
//    		    byte value = data8[k];
//    		    value ^= k;
//    		    if ((value & mask[j]) != value) {
//
//    			    throw new IllegalArgumentException("Encoded text has illegal value."); //$NON-NLS-1$
//    		    }
//    		    data[i] += value;
//    	    }
//        }
//        
//        String clearText;
//        try {
//            clearText = new String(data, CHARACTER_ENCODING);
//        } catch (UnsupportedEncodingException uee) {
//
//            throw new IllegalArgumentException(uee.getLocalizedMessage());
//        }
//        return clearText;
//    }
//    //Use new encryption
//    else {
//        String result = new TeaEncrypter().decrypt(encodedText.substring(ENCODED_PREFIX.length()));
//        return result;
//    }
}
/**
 * Encodes a clear text string.
 *
 * @return the encoded text string
 * @param clearText the clear text string to encode
 * @exception IllegalArgumentException if the input text is already encoded
 */
public static String encode(String clearText) throws IllegalArgumentException {
	return clearText;
//    if (isEncoded(clearText)) {
//
//        throw new IllegalArgumentException("Text is already encoded."); //$NON-NLS-1$
//    }
//
//    /**
//     * Don't encode an empty or null string 
//     */
//    if (clearText == null || clearText.length() == 0) {
//    	return clearText;
//    }
//    
//    String base64 = new TeaEncrypter().encrypt(clearText);
//
//    return ENCODED_PREFIX + base64;
}
public String getAction() {
	return action;
}
public String getInputFileName() {
	return inputFileName;
}
public String getKeyName() {
	return keyName;
}
public String getOutputFileName() {
	return outputFileName;
}
public Properties getProperties() {
	return properties;
}
/**
 * Tests if a text string is encoded.
 *
 * @return true if the text is encoded, false otherwise
 * @param text the string to test
 */
public static boolean isEncoded(String text) {
	
	return text !=  null && (text.startsWith(ENCODED_PREFIX)|| text.startsWith(OLD_ENCODED_PREFIX));
}
/**
 * Loads the properties.
 *
 * @exception IOException if the input stream cannot be read
 * @exception FileNotFoundException if the input file does not exist
 */
public void load() throws IOException, FileNotFoundException {

    InputStream in;
    String inputFileName = getInputFileName();
    if (inputFileName == null) {

        in = System.in;

    } else {

        in = new FileInputStream(inputFileName);
    }

    Properties properties = new Properties();
    properties.load(in);

    in.close();

    setProperties(properties);
}
/**
 * Encodes or decodes a value in a properties file.
 * The usage is:
 * <pre>
 * Encoder [-action encode|decode] [-key key-name] [-in input-file-name] [-out output-file-name]
 * </pre>
 * All arguments are optional.
 * If the action is not specified then "encode" is used.
 * If the key name is not specified then "password" is used.
 * If the input file is not specified then standard input is used.
 * If the output file is not specified then standard output is used.
 * The options may appear in any order and may be repeated.
 * The last value specified is used.
 *
 * @param args the string array of arguments
 */
public static void main(String[] args) {

    Encoder encoder = new Encoder();

    try {
    encoder.parseArgs(args);
    } catch (Exception e) {

	    System.err.println(e.getLocalizedMessage());
	    System.err.println(USAGE);

	    return;
    }
    
    try {

        encoder.load();
        encoder.update();
        encoder.store();

    } catch (Exception e) {

        e.printStackTrace(System.err);
    }
}
/**
 * Parses the command line arguments.
 *
 * @param args the string array of command line arguments
 */
public void parseArgs(String[] args) {

    int n = args.length;
    if (n % 2 != 0) {

        throw new IllegalArgumentException("Arguments must be in option-value pairs."); //$NON-NLS-1$
    }

    for (int i = 0; i < n; i += 2) {

        String option = args[i];
        String value = args[i + 1];

        if (option.equals(ACTION_OPTION)) {

            setAction(value);
        } else
            if (option.equals(KEY_OPTION)) {

                setKeyName(value);
            } else
                if (option.equals(INPUT_OPTION)) {

                    setInputFileName(value);
                } else
                    if (option.equals(OUTPUT_OPTION)) {

                        setOutputFileName(value);
                    } else {

                        throw new IllegalArgumentException("Unknown option: " + option); //$NON-NLS-1$
                    }
    }
}
public void setAction(String newAction) {
	action = newAction;
}
public void setInputFileName(String newInputFileName) {
	inputFileName = newInputFileName;
}
public void setKeyName(String newKeyName) {
	keyName = newKeyName;
}
public void setOutputFileName(String newOutputFileName) {
	outputFileName = newOutputFileName;
}
public void setProperties(Properties newProperties) {
	properties = newProperties;
}
/**
 * Stores the properties.
 *
 * @exception IOException if the properties list cannot be written to the output stream
 * @exception FileNotFoundException if the output file does not exist
 */
public void store() throws IOException, FileNotFoundException {

    String outputFileName = getOutputFileName();
    OutputStream out;
    if (outputFileName == null) {

        out = System.out;
    } else {

        out = new FileOutputStream(outputFileName);
    }

    Properties properties = getProperties();
    properties.store(out, null);

    out.close();
}
/**
 * Updates the properties list. 
 * If the key value is missing or empty then no action is taken.
 * Otherwise, if the key value is encoded and the action is decode, then the key value is decoded,
  * of if the key value in not encode and te action is encode, then the key value is encoded.
 */
public void update() {

    Properties properties = getProperties();
    String keyName = getKeyName();
    String keyValue = properties.getProperty(this.keyName);

    if (keyValue == null || keyValue.length() == 0)
        return;

    String action = getAction();
    if (isEncoded(keyValue)) {

        if (action.equals(DECODE_ACTION)) {

            keyValue = decode(keyValue);
        }
    } else {

        if (action.equals(ENCODE_ACTION)) {

            keyValue = encode(keyValue);
        }
    }

    properties.setProperty(keyName, keyValue);
}
}

