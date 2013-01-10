/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
/******************************************************************************/
/*---------------------------------------------------------------------------------*/
/* Change Activity                                                                 */
/*                                                                                 */
/* ------------------------------------------------------------------------------- */
/*  Flag  Reason        Rls Date        Description of changes                     */
/*  ----  -----------   --- ------ ---  ------------------------------------------ */
/*  $bd1  59501         801 131010 RHB  Set the orientation of Formatter Preview   */
/* 									    to LTR for Arabic locale                   */
/***********************************************************************************/

package org.eclipse.edt.ide.ui.internal.formatting.ui;

import java.util.Locale;
import java.util.Map;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.editor.EGLSourceViewer;
import org.eclipse.edt.ide.ui.internal.editor.EGLSourceViewerConfiguration;
import org.eclipse.edt.ide.ui.internal.editor.TextTools;
import org.eclipse.edt.ide.ui.internal.formatting.CodeFormatterConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.MarginPainter;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.formatter.FormattingContext;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatterExtension;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

public class EGLPreview {

	private String fPreviewText ;

	private final class EGLSourcePreviewerUpdater {
	    
	    final IPropertyChangeListener fontListener= new IPropertyChangeListener() {
	        public void propertyChange(PropertyChangeEvent event) {
	            if (event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_TEXT_FONT)) {
					final Font font= JFaceResources.getFont(EDTUIPreferenceConstants.EDITOR_TEXT_FONT);
					fSourceViewer.getTextWidget().setFont(font);
					if (fMarginPainter != null) {
						fMarginPainter.initialize();
					}
				}
			}
		};
		
	    final IPropertyChangeListener propertyListener= new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
//				if (fViewerConfiguration.affectsTextPresentation(event)) {
//					fViewerConfiguration.handlePropertyChangeEvent(event);
					fSourceViewer.invalidateTextPresentation();
//				}
			}
		};
		
		
		public EGLSourcePreviewerUpdater() {
			
		    JFaceResources.getFontRegistry().addListener(fontListener);
		    fPreferenceStore.addPropertyChangeListener(propertyListener);
			
			fSourceViewer.getTextWidget().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					JFaceResources.getFontRegistry().removeListener(fontListener);
					fPreferenceStore.removePropertyChangeListener(propertyListener);
				}
			});
		}
	}
	
	protected final EGLSourceViewerConfiguration fViewerConfiguration;	
	protected final Document fPreviewDocument;
	protected final SourceViewer fSourceViewer;
	protected final IPreferenceStore fPreferenceStore;
	
	protected final MarginPainter fMarginPainter;
	private Map fPreferenceSetting ;
	
	private int fTabSize = 0;
	private TextTools ftools;
	
	public EGLPreview(Composite parent, Map preferenceSetting){
		fPreferenceSetting = preferenceSetting;
		ftools = new TextTools(EDTUIPlugin.getDefault().getPreferenceStore());
		fPreviewDocument = new Document();
		
		ftools.setupEGLDocumentPartitioner(fPreviewDocument);
		IPreferenceStore[] chain = {EDTUIPlugin.getDefault().getPreferenceStore()};
		fPreferenceStore = new ChainedPreferenceStore(chain);
		
		//@bd1a Start
		int styles = SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
		if(Locale.getDefault().toString().toLowerCase().indexOf("ar") != -1) {
    		styles |= SWT.LEFT_TO_RIGHT;    		
		}
		//@bd1a End
		fSourceViewer = new EGLSourceViewer(parent, null, null, false, styles); //@bd1c
		fViewerConfiguration = new EGLSourceViewerConfiguration(ftools);
		fSourceViewer.configure(fViewerConfiguration);
		fSourceViewer.getTextWidget().setFont(JFaceResources.getFont(EDTUIPreferenceConstants.EDITOR_TEXT_FONT));

		fMarginPainter = new MarginPainter(fSourceViewer);
		final RGB rgb = PreferenceConverter.getColor(fPreferenceStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR);
		
		fMarginPainter.setMarginRulerColor(ftools.getEGLColorProvider().getColorForRGB(rgb));
		fSourceViewer.addPainter(fMarginPainter);
		
		new EGLSourcePreviewerUpdater();
		fSourceViewer.setDocument(fPreviewDocument);		
	}
	
	public Control getControl(){
		return fSourceViewer.getControl();
	}
	
	public void update(){
		// update the print margin		
		final int lineWidth= CodeFormatterConstants.getIntPreferenceSetting(fPreferenceSetting, CodeFormatterConstants.FORMATTER_PREF_WRAP_MAX_LEN);
		fMarginPainter.setMarginRulerColumn(lineWidth);
		
		//update the tab size
		final int tabSize = CodeFormatterConstants.getIntPreferenceSetting(fPreferenceSetting, CodeFormatterConstants.FORMATTER_PREF_INDENT_SIZE); 
		if (tabSize != fTabSize) fSourceViewer.getTextWidget().setTabs(tabSize);
		fTabSize= tabSize;
		
		final StyledText widget= (StyledText)fSourceViewer.getControl();
		final int height= widget.getClientArea().height;
		final int top0= widget.getTopPixel();
		
		final int totalPixels0= getHeightOfAllLines(widget);
		final int topPixelRange0= totalPixels0 > height ? totalPixels0 - height : 0;
		
		widget.setRedraw(false);
		doFormatPreview();
		fSourceViewer.setSelection(null);
		
		final int totalPixels1= getHeightOfAllLines(widget);
		final int topPixelRange1= totalPixels1 > height ? totalPixels1 - height : 0;

		final int top1= topPixelRange0 > 0 ? (int)(topPixelRange1 * top0 / (double)topPixelRange0) : 0;
		widget.setTopPixel(top1);
		widget.setRedraw(true);		
	}
	
    protected void doFormatPreview() {
        fPreviewDocument.set(fPreviewText);
		
		fSourceViewer.setRedraw(false);

		final IFormattingContext context = new FormattingContext();
		try{
			final IContentFormatter formatter =	fViewerConfiguration.getContentFormatter(fSourceViewer);
			if (formatter instanceof IContentFormatterExtension) {
				final IContentFormatterExtension extension = (IContentFormatterExtension) formatter;
				context.setProperty(FormattingContextProperties.CONTEXT_PREFERENCES, fPreferenceSetting);
				context.setProperty(FormattingContextProperties.CONTEXT_DOCUMENT, Boolean.valueOf(true));
				extension.format(fPreviewDocument, context);
			}
		}
		finally{
			fSourceViewer.setRedraw(true);
		}
			
    }	
	
	private int getHeightOfAllLines(StyledText styledText) {
		int height= 0;
		int lineCount= styledText.getLineCount();
		for (int i= 0; i < lineCount; i++)
			height= height + styledText.getLineHeight(styledText.getOffsetAtLine(i));
		return height;
	}
	
	private static int getPositiveIntValue(String string, int defaultValue) {
	    try {
	        int i= Integer.parseInt(string);
	        if (i >= 0) {
	            return i;
	        }
	    } catch (NumberFormatException e) {
	    }
	    return defaultValue;
	}		
	
	
	public void setPreviewText(String previewText){
		fPreviewText = previewText;
		update();
	}

	public String getDefaultLineDelimiter() {
		return TextUtilities.getDefaultLineDelimiter(fPreviewDocument);
	}
	
	public void dispose(){
		if(ftools != null)
			ftools.dispose();
	}
}
