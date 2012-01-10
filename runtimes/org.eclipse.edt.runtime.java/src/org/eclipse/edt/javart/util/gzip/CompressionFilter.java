/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.edt.javart.resources.Trace;


public class CompressionFilter implements Filter {

	private static final String ENABLE_COMPRESSION = "enable_compression";
	private static final String COMPRESSION_THRESHOLD = "compression_threshold";
	private static final String TRACE_LEVEL = "trace_level";
	private static final String TRACE_DEV = "trace_dev";
	private static final String TRACE_FILE = "trace_file";
	
    private FilterConfig config = null;
    
    private boolean enableCompression = true;
    private int compressionThreshold = 1024 * 20; // 20K; 
	private Trace tracer;

    public void init(FilterConfig filterConfig) {
        config = filterConfig;
        String tempValue = config.getInitParameter( ENABLE_COMPRESSION );
        enableCompression = ( "true".equalsIgnoreCase( tempValue ) );
        tempValue = config.getInitParameter( COMPRESSION_THRESHOLD );
        compressionThreshold = ( tempValue != null ? Integer.parseInt( tempValue ) : compressionThreshold );
		
        String traceLevel = config.getInitParameter( TRACE_LEVEL );
		if ( traceLevel == null ) {
			tracer = new Trace( "0", null, null ); // don't print Trace
		} else {
			String traceDev = config.getInitParameter( TRACE_DEV );
			String traceFile = config.getInitParameter( TRACE_FILE );
			tracer = new Trace( traceLevel, traceDev, traceFile );
		}
    }

    public void destroy() {
        this.config = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (enableCompression && request instanceof HttpServletRequest && response instanceof HttpServletResponse && isClientGzipEnabled((HttpServletRequest)request)) {
            CompressionServletResponseWrapper wrappedResponse =
                    new CompressionServletResponseWrapper((HttpServletResponse) response, tracer);
            wrappedResponse.setCompressionThreshold(compressionThreshold);
            try {
                chain.doFilter(request, wrappedResponse);
            } finally {
                wrappedResponse.finishResponse();
            }
        } else {
            chain.doFilter(request, response);
        }
    }


    public void setFilterConfig(FilterConfig filterConfig) {
        init(filterConfig);
    }


    public FilterConfig getFilterConfig() {
        return config;
    }
    
    private boolean isClientGzipEnabled(HttpServletRequest req) {
        String clientEncoding = req.getHeader("Accept-Encoding");
        return ((clientEncoding != null) && (clientEncoding.indexOf("gzip") != -1));
    }

}

