package archive;

import java.util.ArrayList;

import kernel.AlgoritmoAprendizado;

import solucao.Solucao;

public class HyperPlaneReferenceArchive extends PreciseArchiver {
	
	public int archiveSize = 0;

	public HyperPlaneReferenceArchive(int archiveSize) {
		ID = "hyper";
		this.archiveSize = archiveSize;
	}
	
	
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		front.add(new_solution);
	}
	
	/**
	 * Removes from the archive the solutions in the most crowded regions of the hyperplane
	 * @param front
	 */
	public void filterHyperplane(ArrayList<Solucao> front, ArrayList<ArrayList<Solucao>> extremes, int m){
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
	
	

}
