package kernel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYSeries;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;

public class CurvaROC extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jPanel = null;
	
	private double[][] dadosXY;
	SortedSet<Double> limiares;

	/**
	 * This is the default constructor
	 */
	public CurvaROC(double[] x, double[] y, String titulo, SortedSet<Double> l) {
		super();
		dadosXY = new double[3][y.length];
		dadosXY[0] = x;
		dadosXY[1] = y;
		limiares = l;
		
		// Colocando a lista de limiares no conjunto como informativo...
		double temp[][] = new double[1][y.length];
		int i = 0;
		for (Iterator iterator = limiares.iterator(); iterator.hasNext();) {
			Double limiar = (Double) iterator.next();
			temp[0][i] = limiar;
			i++;
		}
		dadosXY[2] = temp[0];
		
		jPanel = createPanel(titulo);  
		initialize();
	
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("Curva ROC");
		this.addWindowListener(new CurvaROCWindowAdapters(this));
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}
	
	public JFreeChart createChart(String titulo){
		DefaultXYZDataset xyzDataSet = new DefaultXYZDataset();
		xyzDataSet.addSeries("(TP,FP,L)", dadosXY);
		JFreeChart chart = ChartFactory.createScatterPlot(titulo, "TP", "FP", xyzDataSet, PlotOrientation.HORIZONTAL, false, true, false);
		//JFreeChart chart = ChartFactory.createBubbleChart(titulo, "TP", "FP", xyzDataSet, PlotOrientation.HORIZONTAL, false, true, false);
		
		
		XYItemRenderer renderer = (XYItemRenderer) chart.getXYPlot().getRenderer();
		renderer.setBaseToolTipGenerator(new StandardXYZToolTipGenerator() );
        //renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator( "{0}({1}) ", NumberFormat.getNumberInstance()));
		
		
		chart.setBorderPaint(Color.WHITE);
		return chart;
	}
	
	public  JPanel createPanel(String titulo) {  
		JFreeChart chart = createChart(titulo);  
		return new ChartPanel(chart);  
	}  
	

}

class CurvaROCWindowAdapters extends java.awt.event.WindowAdapter
{
	
	public JFrame janela = null;
	
	public CurvaROCWindowAdapters(JFrame j){
		janela = j;
	}
	public void windowClosing(java.awt.event.WindowEvent e) {
		janela.dispose();
	}	

}
