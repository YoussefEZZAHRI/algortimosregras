package kernel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import java.awt.GridBagLayout;

public class CurvaROC extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jPanel = null;
	
	private double[][] dadosXY;

	/**
	 * This is the default constructor
	 */
	public CurvaROC(double[] x, double[] y, String titulo) {
		super();
		dadosXY = new double[2][y.length];
		dadosXY[0] = x;
		dadosXY[1] = y;
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
		DefaultXYDataset xyDataSet= new DefaultXYDataset();
		Double d = new Double(1);
		xyDataSet.addSeries(d, dadosXY);
		
		JFreeChart chart = ChartFactory.createScatterPlot(titulo, "TP", "FP", xyDataSet, 
				PlotOrientation.HORIZONTAL, false, false, false);
				
		chart.setBorderPaint(Color.WHITE);
		return chart;
	}
	
	public  JPanel createPanel(String titulo) {  
		JFreeChart chart = createChart(titulo);  
		return new ChartPanel(chart);  
	}  
	

}

class CurvaROCWindowAdapters extends java.awt.event.WindowAdapter{
	
	public JFrame janela = null;
	
	public CurvaROCWindowAdapters(JFrame j){
		janela = j;
	}
	public void windowClosing(java.awt.event.WindowEvent e) {
		janela.dispose();
	}

	
	
}
