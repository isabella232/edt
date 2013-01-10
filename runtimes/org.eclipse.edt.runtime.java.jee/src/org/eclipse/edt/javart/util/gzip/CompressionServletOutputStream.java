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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.edt.javart.resources.Trace;

public class CompressionServletOutputStream extends ServletOutputStream {

    protected int compressionThreshold = 0;
    protected byte[] buffer = null;
    protected int bufferCount = 0;
    protected GZIPOutputStream gzipstream = null;
    protected boolean closed = false;
    protected int length = -1;
    protected HttpServletResponse response = null;
    protected ServletOutputStream output = null;
    private ByteArrayOutputStream byteStream = null;
    
    protected int beforeCompress;
    protected int afterCompress;
    protected Trace tracer;
    
    protected static final String CLOSED_STREAM_MSG = "This output stream has already been closed";
    protected static final String CANNOT_WRITE_MSG = "Cannot write to a closed output stream";

    public CompressionServletOutputStream(HttpServletResponse response, Trace tracer) throws IOException {
        super();
        closed = false;
        this.response = response;
        this.output = response.getOutputStream();
        this.tracer = tracer;
    }

    protected void setBuffer(int threshold) {
        compressionThreshold = threshold;
        buffer = new byte[compressionThreshold];
    }

    public void close() throws IOException {

        if (closed)
            throw new IOException( CLOSED_STREAM_MSG );

        if (gzipstream != null) {
            flushToGZip();
            gzipstream.close();
            gzipstream = null;
            if ( tracer.traceIsOn() ) {
    	        afterCompress += byteStream.size();
    	        byteStream.writeTo( output );
    	        byteStream.close();
    	        tracer.put( "Befor Compress: " + beforeCompress + "   After Compress: " + afterCompress );
            }
        } else {
            if (bufferCount > 0) {
                output.write(buffer, 0, bufferCount);
                bufferCount = 0;
            }
        }

        output.close();
        closed = true;
    }

    public void flush() throws IOException {
        if (closed) {
            throw new IOException("");
        }

        if (gzipstream != null) {
            gzipstream.flush();
        }
    }

    public void flushToGZip() throws IOException {
       if (bufferCount > 0) {
            writeToGZip(buffer, 0, bufferCount);
            bufferCount = 0;
        }
    }

    public void write(int b) throws IOException {
        if (closed)
            throw new IOException( CLOSED_STREAM_MSG );

        if (bufferCount >= buffer.length) {
            flushToGZip();
        }

        buffer[bufferCount++] = (byte) b;
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }


    public void write(byte b[], int off, int len) throws IOException {
        if (closed)
            throw new IOException( CANNOT_WRITE_MSG );
        if (len == 0)
            return;

        if (len <= (buffer.length - bufferCount)) {
            System.arraycopy(b, off, buffer, bufferCount, len);
            bufferCount += len;
            return;
        }
        flushToGZip();

        if (len <= (buffer.length - bufferCount)) {
            System.arraycopy(b, off, buffer, bufferCount, len);
            bufferCount += len;
            return;
        }
        writeToGZip(b, off, len);
    }

    public void writeToGZip(byte b[], int off, int len) throws IOException {
        if (gzipstream == null) {
            response.addHeader("Content-Encoding", "gzip");
            if ( tracer.traceIsOn() ) {
	            byteStream = new ByteArrayOutputStream();
	            gzipstream = new GZIPOutputStream(byteStream);
            } else {
            	gzipstream = new GZIPOutputStream(output);
            }
        }
        gzipstream.write(b, off, len);

        if ( tracer.traceIsOn() ) {
	        beforeCompress += len;
        }
    }

}
