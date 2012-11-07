package archive;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import kernel.AlgoritmoAprendizado;



import solucao.Solucao;

public class HyperPlaneReferenceArchive extends PreciseArchiver {
	
	public final static int ALL = 0;
	public final static int EDGE = 1;
	public final static int MIDDLE = 2;
	public final static int EXTREME = 3;
	
	public final static double MAX_OBJ_VALUE = 0.6;
	
	public int m;
	public double[][] reference_points = null;
	public int reference_index = -1;
	public double[] reference_point = null;

	public HyperPlaneReferenceArchive(int m, int region) {
		
		this.m = m;
		loadReferencePoints(m);
		
		if(region==ALL){
			selectRandomReferencePoint(reference_points);
			ID = "hyp_a";
		}
		if(region==EDGE){
			selectRandomReferencePoint(pointsOnEdge());
			ID = "hyp_ed";
		}
		if(region==MIDDLE){
			selectRandomReferencePoint(pointsOnMiddle());
			ID = "hyp_m";
		}
		
		if(region==EXTREME){
			int random_extreme = (int)(Math.random()*m);
			ID = "hyp_ex";
			if(m<8)
				selectRandomReferencePoint(pointsNearExtreme(random_extreme, 0.5));
			else
				selectRandomReferencePoint(pointsNearExtreme(random_extreme, 0.9));
		}
		
		
		
		
		System.out.println("Reference Index: " + reference_index);
		System.out.print("Reference Point:");
		
		
		for(int i = 0; i<m; i++){
			System.out.print("\t" + reference_point[i]);
		}
		System.out.println();
	}
	
	//Removes the solution with worst distance to the reference point
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {	
		
		front.add( new_solution);
		
		//For each solution on the front, it calculates the distance to the reference point
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(reference_point, solucao.objetivos);
			//Round up the distance
			BigDecimal b = new BigDecimal(solucao.menorDistancia);		 
			solucao.menorDistancia = (b.setScale(5, BigDecimal.ROUND_UP)).doubleValue();
		}

		double highDistanceValue = 0;
		int index = -1;
		for (int i = 0; i<front.size(); i++) {
			Solucao solucao = front.get(i);
			if(solucao.menorDistancia >= highDistanceValue){
				highDistanceValue = solucao.menorDistancia;
				index = i;
			}
		}
		try{
			front.remove(index);
		}	catch(ArrayIndexOutOfBoundsException ex){ex.printStackTrace();}

	}
	
	
	public void loadReferencePoints(int m){
		String reference_file = "ref/ref_"+m+".txt";
		try{
			int number_points = 0;
			BufferedReader reader = new BufferedReader(new FileReader(reference_file));
			
			while(reader.ready()){
				String line = reader.readLine();
				if(!line.equals(""))
					number_points++;
			}
			
			reader = new BufferedReader(new FileReader(reference_file));
			
			reference_points = new double[number_points][m];
			
			int j = 0;
			while(reader.ready()){
				String line = reader.readLine();
				if(!line.equals("")){
					double[] reference_point = reference_points[j++];
					String line_values[] = line.split("\t");
					for (int i = 0; i < line_values.length; i++) {
						if(line_values[i].length()>5)
							reference_point[i] = new Double(line_values[i].substring(0,5));	
						else
							reference_point[i] = new Double(line_values[i]);
					}
				}
					
			}
			
		} catch(IOException ex){ex.printStackTrace();}
	}
	
	public void selectRandomReferencePoint(double[][] reference_points){
		reference_index =  (int) (Math.random()*reference_points.length);
		reference_point = reference_points[reference_index];
	}
	
	/**
	 * Returns only the points that belongs to the edges of the hyperplane
	 * @return
	 */
	public double[][] pointsOnEdge(){
		ArrayList<double[]> edgePoints = new ArrayList<double[]>();
		for(int i = 0;i<reference_points.length;i++){
			double[] point = reference_points[i];
			int cont = 0;
			for(int j=0; j<point.length;j++){
				if(point[j]==0)
					cont++;
				if(cont>0)
					break;
			}
			
			if(cont > 0)
				edgePoints.add(point);
		}
		
		System.out.println(edgePoints.size());
		
		/*try{
		PrintStream edge = new PrintStream("edge.txt"); 
		for (Iterator iterator = edgePoints.iterator(); iterator.hasNext();) {
			double[] ds = (double[]) iterator.next();
			for(int i = 0; i< ds.length;i++)
				edge.print(ds[i] + "\t");
			edge.println();
		}
		
		}catch(IOException ex){ex.printStackTrace();}*/
		
		double edgePoints_double[][] = new double[edgePoints.size()][m];
		int i = 0;
		for (Iterator<double[]> iterator = edgePoints.iterator(); iterator.hasNext();) {
			double[] ds = iterator.next();
			edgePoints_double[i++] = ds;
		}
		return edgePoints_double;
	}
	
	/**
	 * Returns only the points in the middle of the Hyperplane.
	 * Points not on the edges and with no more than 0.6 of objective value
	 * @return
	 */
	public double[][] pointsOnMiddle(){
		ArrayList<double[]> middlePoints = new ArrayList<double[]>();
		for(int i = 0;i<reference_points.length;i++){
			double[] point = reference_points[i];
			int cont = 0;
			int cont2 = 0;
			for(int j=0; j<point.length;j++){
				if(point[j]==0)
					cont++;
				if(point[j]>MAX_OBJ_VALUE)
					cont2++;
				
				if(cont>0)
					break;
				if(cont2>0)
					break;
			}
			
			if(cont == 0 && cont2==0){
				middlePoints.add(point);
			}
		}
		
		System.out.println(middlePoints.size());
		
		/*try{
		PrintStream edge = new PrintStream("/home/andrebia/middle.txt"); 
		for (Iterator iterator = middlePoints.iterator(); iterator.hasNext();) {
			double[] ds = (double[]) iterator.next();
			for(int i = 0; i< ds.length;i++)
				edge.print(ds[i] + "\t");
			edge.println();
		}
		
		}catch(IOException ex){ex.printStackTrace();}*/
		
		double middlePoints_double[][] = new double[middlePoints.size()][m];
		int i = 0;
		for (Iterator<double[]> iterator = middlePoints.iterator(); iterator.hasNext();) {
			double[] ds = iterator.next();
			middlePoints_double[i++] = ds;
		}
		return middlePoints_double;
	}
	
	/**
	 * Return the points if max_dist from the extremen point of index "index"
	 * @param index Index of the extreme point
	 * @param max_dist maximum distance
	 * @return
	 */
	public double[][] pointsNearExtreme(int index, double max_dist ){
		
		double extremePoint[] = new double[m];
		
		extremePoint[index] = 1;
		
		ArrayList<double[]> points_near = new ArrayList<double[]>();
		
		for(int i = 0;i<reference_points.length;i++){
			double[] point = reference_points[i];
			double dist = AlgoritmoAprendizado.distanciaEuclidiana(point, extremePoint);
			if(dist<max_dist)
				points_near.add(point);
		}
		
		System.out.println(points_near.size());
		
		/*try{
			PrintStream edge = new PrintStream("/home/andrebia/near.txt"); 
			for (Iterator iterator = points_near.iterator(); iterator.hasNext();) {
				double[] ds = (double[]) iterator.next();
				for(int i = 0; i< ds.length;i++)
					edge.print(ds[i] + "\t");
				edge.println();
			}
		}catch(IOException ex){ex.printStackTrace();}*/
		double nearPoints_double[][] = new double[points_near.size()][m];
		int i = 0;
		for (Iterator<double[]> iterator = points_near.iterator(); iterator.hasNext();) {
			double[] ds = iterator.next();
			nearPoints_double[i++] = ds;
		}
		return nearPoints_double;
	}
	
	/**
	 * Removes from the archive the solutions in the most crowded regions of the hyperplane
	 * @param front
	 */
	/*public void filterHyperplane(ArrayList<Solucao> front, ArrayList<ArrayList<Solucao>> extremes, int m){
		//Checks if the archive has more solutions than the maximum allowed
		if(front.size()>archiveSize){
			double extremes_translated[][] = new double [m][m];
			Solucao ideal = extremes.get(m).get(0);
			
			for(int i = 0; i<extremes.size()-1; i++){
				ArrayList<Solucao> extremes_i = extremes.get(i);
				int random_index = (int) (Math.random() * extremes_i.size());
				Solucao extreme_selected = extremes_i.get(random_index);
				extremes_translated[i] = new double[m];
				for(int j = 0; j<m; j++){
					extremes_translated[i][j] = extreme_selected.objetivos[j] - ideal.objetivos[j];
				}
			}
			
			int p = 3;
			double[][] reference_points = AlgoritmoAprendizado.getReferencePointsHyperPlane(m, p, extremes_translated);
			System.out.println();
			
		}
	}
	*/
	

}
