package problema;

import java.util.ArrayList;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.DLOAD;

import pareto.FronteiraPareto;
import solucao.Solucao;
import solucao.SolucaoNumerica;

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
		super(m);
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems" utilizado a equacao 8 como g
	 */
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g1(solucao.xm);
		double pi_2 = Math.PI/2.0;
		//System.out.print("f(0): ");
		double f0 = (1+g)*Math.cos(solucao.getVariavel(0)*pi_2);
		//System.out.print("Cos 0 ");
		for(int i = 1; i<m-1; i++){
			f0 *= Math.cos(solucao.getVariavel(i)*pi_2);
			//System.out.print("Cos " + i  + " ");
		}
	   solucao.objetivos[0] = f0;
		for(int i = 1; i<(m); i++){
			//System.out.print("f(" + i + "): ");
			double fxi = (1+g);
			int j = 1;
			for(j = 0; j<(m-1-i);j++){
				fxi*=(Math.cos(solucao.getVariavel(j)*pi_2));
				//System.out.print("Cos " + j  + " ");
			}
			fxi *= (Math.sin(solucao.getVariavel(j)*pi_2));
			//System.out.print("Sen " + j  + " ");
			//System.out.println();
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
				somaParcial += melhor.objetivos[i]*melhor.objetivos[i];
			}
			if(somaParcial==1){
				melhores.add(melhor);
			}
		}
		
		return melhores;
	}
	

	
	public static void main(String[] args) {
		
		int m = 3;

		DTLZ3 dtlz3 = new DTLZ3(m);
		ArrayList<SolucaoNumerica> melhores =  dtlz3.obterFronteira(11, 250);
		dtlz3.imprimirVetoresScilab(melhores);
		
		
		
	}

}
