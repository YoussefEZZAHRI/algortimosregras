package problema;

import pareto.FronteiraPareto;
import solucao.Solucao;

/**
 * Classe que representa o problema DTLZ2
 * @author andre
 *
 */

public class DTLZ5 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ5(int m){
		this.m = m;
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double[] calcularObjetivos(Solucao solucao) {
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g2(solucao.xm);
		double pi_2 = Math.PI/2.0;
		
		double f0 = (1+g);
		
		for(int i = 0; i<m-1; i++){
			double fi0 = fi(solucao.variaveis[0],g);
			f0 *= Math.cos(fi0*pi_2);
		}
	   
		solucao.objetivos[0] = f0;
		for(int i = 1; i<(m); i++){
			//System.out.print("f(" + i + "): ");
			double fxi = (1+g);
			double fiI = fi(solucao.variaveis[i],g);
			int j = 1;
			for(j = 0; j<(m-1-i);j++){
				fxi*=(Math.cos(fiI*pi_2));
				//System.out.print("Cos " + j  + " ");
			}
			fxi *= (Math.sin(fiI*pi_2));
			//System.out.print("Sen " + j  + " ");
			//System.out.println();
			solucao.objetivos[i] = fxi;
		}
		
		return solucao.objetivos;
	}
	
	public double fi(double xi, double g){
		double temp1 = Math.PI/(4*(1+g));
		double temp2 = 1+(2*g*xi);
		return temp1*temp2;
		
	}
	

	
	public static void main(String[] args) {
		
		int m = 2;
		FronteiraPareto pareto = new FronteiraPareto(0.25, false);
		
		DTLZ5 dtlz2 = new DTLZ5(m);
		for(int i = 0; i<2; i++){
			Solucao sol = new Solucao(5, m);
			sol.iniciarSolucaoAleatoria();
			dtlz2.calcularObjetivos(sol);
			System.out.println(sol);
			pareto.add(sol);
		}
		System.out.println("Fronteira:");
		System.out.println(pareto);
		
		
		
	}

}
