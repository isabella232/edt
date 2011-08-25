package org.eclipse.edt.ide.eunit.ui.testresult;

import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.eunit.internal.chart.ChartPreview;
import org.eclipse.edt.ide.eunit.internal.chart.Pie;
import org.eclipse.edt.ide.eunit.ui.testresult.ResultSummaryBlock.ResultStatisticCnts;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


public class TestResultPkgNodeDetailsPage implements IDetailsPage {
	protected IManagedForm mform;
	protected int nColumnSpan = 3;
	ResultStatisticCnts statisticCnt;
	
	private static final String BIRT_ENGINE_PLUGIN_ID = "org.eclipse.birt.chart.engine";
	protected Color red;
	
	public TestResultPkgNodeDetailsPage(ResultStatisticCnts statistic){
		statisticCnt = statistic;
	}
	
	@Override
	public void commit(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		if(red != null)
			red.dispose();
		red = null;
	}

	@Override
	public void initialize(IManagedForm form) {
		this.mform = form;

	}

	protected Color getRed(){
		if(red == null)
			red = new Color(Display.getCurrent(), new RGB(255, 0, 0));	
		return red;
	}
	
	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setFormInput(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		// TODO Auto-generated method stub
		selection.toString();
	}

	@Override
	public void createContents(Composite parent) {
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 5;
		layout.bottomMargin = 2;
		parent.setLayout(layout);
		FormToolkit toolkit = mform.getToolkit();
		createTopNonExpandableSection(parent, toolkit);		
	}

	protected void createTopNonExpandableSection(Composite parent, FormToolkit toolkit) {
		Composite client = createDetailSection(parent, toolkit, Section.DESCRIPTION|Section.SHORT_TITLE_BAR, nColumnSpan);		
		createControlsInTopSection(toolkit, client);
	}
	
	protected Composite createDetailSection(Composite parent, FormToolkit toolkit, int sectionStyle, int columnSpan){
		return createSection(parent, toolkit, "", "", sectionStyle, columnSpan); //$NON-NLS-1$ //$NON-NLS-2$
	}	
	
	protected void createControlsInTopSection(FormToolkit toolkit, Composite parent) {
		createSpacer(toolkit, parent, nColumnSpan);
		createOneLabelPerLine(toolkit, parent, nColumnSpan, "testCnt: " + statisticCnt.getTestCnt());
		createOneLabelPerLine(toolkit, parent, nColumnSpan, "expCnt: " + statisticCnt.getExpectedCnt());
		createOneLabelPerLine(toolkit, parent, nColumnSpan,  "passedCnt: " + statisticCnt.getPassedCnt());
		createOneLabelPerLine(toolkit, parent, nColumnSpan,  "failedCnt: " + statisticCnt.getFailedCnt());
		createOneLabelPerLine(toolkit, parent, nColumnSpan,  "errCnt: " + statisticCnt.getErrCnt());
		createOneLabelPerLine(toolkit, parent, nColumnSpan,  "badCnt: " + statisticCnt.getBadCnt());
		createOneLabelPerLine(toolkit, parent, nColumnSpan,  "notRunCnt: " + statisticCnt.getNotRunCnt());
		
		if(statisticCnt.getTestCnt() != statisticCnt.getExpectedCnt()){
			String errMsg = "ERROR: Expected count [" + statisticCnt.getExpectedCnt() + "] is different from the actual test ran [" + statisticCnt.getTestCnt() + "]";
			createErrorLable(toolkit, parent, nColumnSpan, errMsg);
		}
		
		if(isBIRTPluginInstalled()) {
			createPieChart(parent);
		} else {
			String errMsg = "NOTE: You need birt chart engine to view pie chart, please install BIRT feature (version 2.6.2 and up)";
			createErrorLable(toolkit, parent, nColumnSpan, errMsg);
		}
	}

	private void createPieChart(Composite parent) {		
		parent.setLayout( new GridLayout( ) );
		parent.setLayoutData( new GridData( GridData.FILL_VERTICAL
				| GridData.FILL_HORIZONTAL ) );
		
		GridData gridData = new GridData( GridData.FILL_VERTICAL
				| GridData.HORIZONTAL_ALIGN_FILL );
		gridData.horizontalSpan = nColumnSpan;
		//gridData.verticalSpan = 5;
		parent.setLayoutData( gridData );

		Canvas paintCanvas = new Canvas( parent, SWT.BORDER );
		GridData gridData1 = new GridData( GridData.FILL_BOTH | GridData.FILL_VERTICAL
				| GridData.HORIZONTAL_ALIGN_FILL );
		gridData1.horizontalSpan = nColumnSpan;
		gridData1.verticalSpan = 100;

		paintCanvas.setLayoutData( gridData1);
		paintCanvas.setBackground( Display.getDefault( )
				.getSystemColor( SWT.COLOR_WHITE ) );
		
		ChartPreview preview = new ChartPreview( );
		paintCanvas.addPaintListener( preview );
		paintCanvas.addControlListener( preview );
		preview.setPreview( paintCanvas );	
		
		// Data Set
		TextDataSet categoryValues = TextDataSetImpl.create( new String[]{
				"passed", "failed", "err", "bad", "notRun"} );//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		NumberDataSet seriesOneValues = NumberDataSetImpl.create( new double[]{
				statisticCnt.getPassedCnt(), statisticCnt.getFailedCnt(), 
				statisticCnt.getErrCnt(), statisticCnt.getBadCnt(), statisticCnt.getNotRunCnt()
		} );
		
		
		preview.renderModel(Pie.createPie(categoryValues, seriesOneValues).copyInstance());
	}	
		
	protected Label createSpacer(FormToolkit toolkit, Composite parent, int span) {
		return createOneLabelPerLine(toolkit, parent, span, "");
	}	
	
	protected Label createOneLabelPerLine(FormToolkit toolkit, Composite parent, int span, String labelText) {
		Label spacer = toolkit.createLabel(parent, labelText); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.horizontalSpan = span;
		spacer.setLayoutData(gd);
		return spacer;
	}	
	
	protected Label createErrorLable(FormToolkit toolkit, Composite parent, int span, String labelText){
		Label label = createOneLabelPerLine(toolkit, parent, span, labelText);
		label.setForeground(getRed());
		label.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		return label;
	}

	protected Composite createSection(Composite parent, FormToolkit toolkit, String title,
			String desc, int SectionStyle, int numColumns) 
	{		
		Section section = toolkit.createSection(parent, SectionStyle);
		section.marginWidth = 10;
		section.setText(title);
		section.setDescription(desc);
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);		
		//toolkit.createCompositeSeparator(section);
		
		Composite client = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 2;
		layout.numColumns = numColumns;
		client.setLayout(layout);

		section.setClient(client);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				mform.getForm().reflow(false);
			}
		});
	
		return client;
	}	
	
	/**
	 * Check if the BIRT engine is there & also requires the right version.
	 * @return
	 */
	protected boolean isBIRTPluginInstalled() {
		State state = Platform.getPlatformAdmin().getState(false); // Have to call getState(false) to get an immutable state.
		BundleDescription bundle = state.getBundle(BIRT_ENGINE_PLUGIN_ID, null);
		boolean versionCheck = false;
		
		if(bundle != null) {				
			int majorV = bundle.getVersion().getMajor();
			int minorV = bundle.getVersion().getMinor();
			int microV = bundle.getVersion().getMicro();
			
			//check to see if version is 2.6.2 or up
			if(majorV > 2)
				versionCheck = true;
			else if(majorV == 2){
				if(minorV > 6)
					versionCheck = true;
				else if(minorV == 6){
					if(microV >= 2)
						versionCheck = true;					
				}
			}				
		}
		return (bundle != null) && (bundle.getResolvedRequires().length == bundle.getRequiredBundles().length) && versionCheck;
	}
}
