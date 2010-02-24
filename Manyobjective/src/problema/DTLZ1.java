package problema;


import java.util.ArrayList;
import java.util.Random;

import solucao.Solucao;
import solucao.SolucaoNumerica;

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
		super(m);
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 7 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g1(solucao.xm);
		//System.out.println(g);

		for(int i = 0; i<m; i++){
			double fxi = 0.5*(1+g);
			int j;
			for(j = 0; j<(m-1-i);j++){
				fxi*=solucao.getVariavel(j);
			}
			if(j!=m-1){
			  fxi *= (1-solucao.getVariavel(j));
			}
			solucao.objetivos[i] = fxi;
		}
		
		avaliacoes++;
		return solucao.objetivos;
	}
	
		
		
	public  ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		while(melhores.size()<numSol){
			SolucaoNumerica melhor = new SolucaoNumerica(n, m);

			for (int i = m-1; i <n; i++) {
				melhor.setVariavel(i, 0.5);
			}

			for (int i = 0; i < m-1; i++) {
				double newVal = rand.nextDouble();
				melhor.setVariavel(i, newVal);
			}

			double somaParcial = 0;
			calcularObjetivos(melhor);

			for (int i = 0; i < melhor.m; i++) {
				somaParcial += melhor.objetivos[i];
			}
			if(somaParcial==0.5){
				melhores.add(melhor);	
			}
			
		}
			
		return melhores;
	}
	
	public static void main(String[] args) {

		int m = 3;

		DTLZ1 dtlz1 = new DTLZ1(m);
		dtlz1.obterFronteira(11, 250);
	}
	

}
