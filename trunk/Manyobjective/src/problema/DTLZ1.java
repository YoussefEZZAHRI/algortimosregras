package problema;


import solucao.Solucao;

/**
 * Classe que representa o problema DTLZ1
 * @author andre
 *
 */

public class DTLZ1 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ1(int m){
		this.m = m;
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 7 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double[] calcularObjetivos(Solucao solucao) {
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g1(solucao.xm);
		//System.out.println(g);

		for(int i = 0; i<m; i++){
			double fxi = 0.5*(1+g);
			int j;
			for(j = 0; j<(m-1-i);j++){
				fxi*=solucao.variaveis[j];
			}
			if(j!=m-1){
			  fxi *= (1-solucao.variaveis[j]);
			}
			solucao.objetivos[i] = fxi;
		}
		
		return solucao.objetivos;
	}
	
		public static void main(String[] args) {
		
		int m = 2;
		
		DTLZ1 dtlz2 = new DTLZ1(m);
		for(int i = 0; i<1; i++){
			Solucao sol = new Solucao(5, m);
			sol.iniciarSolucaoAleatoria();
			dtlz2.calcularObjetivos(sol);
			
		}
		
		
	
	}

}
