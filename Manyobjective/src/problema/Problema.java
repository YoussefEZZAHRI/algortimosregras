package problema;

import solucao.Solucao;

public abstract class Problema {
	
	public int m;
	
	
	
	public abstract double[] calcularObjetivos(Solucao solucao);
	
	/**
	 * Equacao 8 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double g1(double[] xm){
		
		double soma = 0;
		for (int i = 0; i < xm.length; i++) {
			double xi = xm[i];
			
			
			double temp1 = Math.pow((xi-0.5),2);
			double temp2 = Math.cos(20*Math.PI*(xi-0.5));
			double temp3 = temp1 - temp2;
			soma+=temp3;
		}
		
		int moduloXm = xm.length;
		return 100*(moduloXm + soma);
	}
	
	/*public double g12(Solucao sol){
		double g = 0;
	    for (int i = sol.n - sol.k + 1; i <= sol.n; i++)
	    {
		g += Math.pow(sol.variaveis[i-1]-0.5,2) - Math.cos(20 * Math.PI * (sol.variaveis[i-1]-0.5));
	    }
	    g = 100 * (sol.k + g);
	    
	    return g;

	}*/
	
	
	/**
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double g2(double[] xm){
		double soma = 0;
		for(int i = 0; i<xm.length; i++){
			soma += (xm[i] - 0.5)*(xm[i] - 0.5);
		}
		return soma;
	}
	
	/**
	 * 
	 * Equacao 6.24 do artigo "Scalable Test Problems for Evolutionary Multiobjective Optimization"
	 */
	public double g5(double[] xm){
		double soma = 0;
		for (int i = 0; i < xm.length; i++) {
			soma+= Math.pow(xm[i], 0.1);
			
		}
		
		return soma;
	}
	
	


}
