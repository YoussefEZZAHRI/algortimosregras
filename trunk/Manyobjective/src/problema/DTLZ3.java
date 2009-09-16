package problema;

import pareto.FronteiraPareto;
import solucao.Solucao;

/**
 * Classe que representa o problema DTLZ3
 * @author andre
 *
 */

public class DTLZ3 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ3(int m){
		this.m = m;
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems" utilizado a equacao 8 como g
	 */
	public double[] calcularObjetivos(Solucao solucao) {
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g1(solucao.xm);
		double pi_2 = Math.PI/2.0;
		//System.out.print("f(0): ");
		double f0 = (1+g)*Math.cos(solucao.variaveis[0]*pi_2);
		//System.out.print("Cos 0 ");
		for(int i = 1; i<m-1; i++){
			f0 *= Math.cos(solucao.variaveis[i]*pi_2);
			//System.out.print("Cos " + i  + " ");
		}
	   solucao.objetivos[0] = f0;
		for(int i = 1; i<(m); i++){
			//System.out.print("f(" + i + "): ");
			double fxi = (1+g);
			int j = 1;
			for(j = 0; j<(m-1-i);j++){
				fxi*=(Math.cos(solucao.variaveis[j]*pi_2));
				//System.out.print("Cos " + j  + " ");
			}
			fxi *= (Math.sin(solucao.variaveis[j]*pi_2));
			//System.out.print("Sen " + j  + " ");
			//System.out.println();
			solucao.objetivos[i] = fxi;
		}
		
		return solucao.objetivos;
	}
	

	
	public static void main(String[] args) {
		
		int m = 2;
		FronteiraPareto pareto = new FronteiraPareto(0.25, false);
		
		DTLZ3 dtlz2 = new DTLZ3(m);
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
