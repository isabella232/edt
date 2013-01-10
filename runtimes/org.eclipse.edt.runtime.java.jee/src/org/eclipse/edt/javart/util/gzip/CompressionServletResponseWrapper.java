/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.util.gzip;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.eclipse.edt.javart.resources.Trace;

public class CompressionServletResponseWrapper extends HttpServletResponseWrapper {

    protected HttpServletResponse origResponse = null;
    protected ServletOutputStream stream = null;
    protected PrintWriter writer = null;
    protected int threshold = 0;
    protected String contentType = null;
    protected Trace tracer;
    
    protected static final String WRITER_CALLED_MSG = "getWriter() has already been called for this response";
    protected static final String OUTPUTSTREAM_CALLED_MSG = "getOutputStream() has already been called for this response";

    public CompressionServletResponseWrapper(HttpServletResponse response, Trace tracer) {
        super(response);
        this.origResponse = response;
        this.tracer = tracer;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        origResponse.setContentType(contentType);
    }

    public void setCompressionThreshold(int threshold) {
        this.threshold = threshold;
    }

    public ServletOutputStream createOutputStream() throws IOException {
        CompressionServletOutputStream stream = new CompressionServletOutputStream(origResponse, tracer);
        stream.setBuffer(threshold);
        return stream;
    }

    public void finishResponse() {
        try {
            if (writer != null) {
                writer.close();
            } else {
                if (stream != null)
                    stream.close();
            }
        } catch (IOException e) {
        }
    }

    public void flushBuffer() throws IOException {
        ((CompressionServletOutputStream) stream).flush();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null)
            throw new IllegalStateException( WRITER_CALLED_MSG );

        if (stream == null)
            stream = createOutputStream();
        return (stream);

    }

    public PrintWriter getWriter() throws IOException {
        if (writer != null)
            return (writer);

        if (stream != null)
            throw new IllegalStateException( OUTPUTSTREAM_CALLED_MSG );

        stream = createOutputStream();
        String charEnc = origResponse.getCharacterEncoding();
        if (charEnc != null) {
            writer = new PrintWriter(new OutputStreamWriter(stream, charEnc));
        } else {
            writer = new PrintWriter(stream);
        }

        return (writer);
    }

    public void setContentLength(int length) {
    }

}
