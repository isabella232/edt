/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.eunit.internal.chart;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.DataPointComponent;
import org.eclipse.birt.chart.model.attribute.DataPointComponentType;
import org.eclipse.birt.chart.model.attribute.impl.DataPointComponentImpl;
import org.eclipse.birt.chart.model.attribute.impl.JavaNumberFormatSpecifierImpl;
import org.eclipse.birt.chart.model.attribute.impl.StringFormatSpecifierImpl;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;

public class Pie
{

	public static final Chart createPie( TextDataSet categoryValues, NumberDataSet seriesOneValues )
	{
		ChartWithoutAxes cwoaPie = ChartWithoutAxesImpl.create( );
		cwoaPie.setDimension( ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL );
		cwoaPie.setType( "Pie Chart" ); //$NON-NLS-1$	
		cwoaPie.setSubType( "Standard Pie Chart" ); //$NON-NLS-1$
		
		// Plot
		cwoaPie.setSeriesThickness( 10 );

		// Legend
		Legend lg = cwoaPie.getLegend( );
		lg.getOutline( ).setVisible( true );

		// Title
		cwoaPie.getTitle( ).getLabel( ).getCaption( ).setValue( "Test Result Statistics Chart" );//$NON-NLS-1$


		// Base Series
		Series seCategory = SeriesImpl.create( );
		seCategory.setDataSet( categoryValues );

		SeriesDefinition sd = SeriesDefinitionImpl.create( );
		cwoaPie.getSeriesDefinitions( ).add( sd );
		sd.getSeriesPalette( ).shift( 0 );
		sd.getSeries( ).add( seCategory );

		// Orthogonal Series
		DataPointComponent dpc = DataPointComponentImpl.create(DataPointComponentType.ORTHOGONAL_VALUE_LITERAL, JavaNumberFormatSpecifierImpl.create("###,###"));
		DataPointComponent dpca = DataPointComponentImpl.create(DataPointComponentType.PERCENTILE_ORTHOGONAL_VALUE_LITERAL, JavaNumberFormatSpecifierImpl.create("##.##%"));
		DataPointComponent dpcb = DataPointComponentImpl.create(DataPointComponentType.BASE_VALUE_LITERAL, StringFormatSpecifierImpl.create(""));
				
		PieSeries sePie = (PieSeries) PieSeriesImpl.create( );
		sePie.setDataSet( seriesOneValues );
		sePie.setSeriesIdentifier( "Test Result Count" );//$NON-NLS-1$ 
		sePie.setExplosion( 5 );
				
		sePie.getDataPoint().getComponents().clear();
		sePie.getDataPoint().getComponents().add(dpc);
		sePie.getDataPoint().getComponents().add(dpcb);
		sePie.getDataPoint().getComponents().add(dpca);		
		sePie.getLabel().setVisible(true);
		
		SeriesDefinition sdCity = SeriesDefinitionImpl.create( );		
		sd.getSeriesDefinitions( ).add( sdCity );
		sdCity.getSeries( ).add( sePie );

		return cwoaPie;
	}

}
