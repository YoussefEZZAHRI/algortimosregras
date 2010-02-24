package problema;

import java.util.ArrayList;
import java.util.Random;

import pareto.FronteiraPareto;
import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa o problema DTLZ2
 * @author andre
 *
 */

public class DTLZ6 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ6(int m){
		super(m);
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g5(solucao.xm);
		double pi_2 = Math.PI/2.0;
		
		double f0 = (1+g);
		
		for(int i = 0; i<m-1; i++){
			double fi0 = fi(solucao.getVariavel(0),g);
			f0 *= Math.cos(fi0*pi_2);
		}
	   
		solucao.objetivos[0] = f0;
		for(int i = 1; i<(m); i++){
			//System.out.print("f(" + i + "): ");
			double fxi = (1+g);
			double fiI = fi(solucao.getVariavel(i),g);
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
		avaliacoes++;
		return solucao.objetivos;
	}
	
	public double fi(double xi, double g){
		double temp1 = Math.PI/(4*(1+g));
		double temp2 = 1+(2*g*xi);
		return temp1*temp2;
		
	}
	
	public  ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		while(melhores.size()<numSol){
			SolucaoNumerica melhor = new SolucaoNumerica(n, m);

			for (int i = m-1; i <n; i++) {
				melhor.setVariavel(i, 0);
			}

			for (int i = 0; i < m-1; i++) {
				double newVal = rand.nextDouble();
				melhor.setVariavel(i, newVal);
			}

			
			calcularObjetivos(melhor);
			melhores.add(melhor);
			
		}
		
		return melhores;	
	}
	
	public static void main(String[] args) {
		
		int m = 2;
		FronteiraPareto pareto = new FronteiraPareto(0.25);
		
		DTLZ6 dtlz2 = new DTLZ6(m);
		for(int i = 0; i<2; i++){
			SolucaoNumerica sol = new SolucaoNumerica(5, m);
			sol.iniciarSolucaoAleatoria();
			dtlz2.calcularObjetivos(sol);
			System.out.println(sol);
			pareto.add(sol);
		}
		System.out.println("Fronteira:");
		System.out.println(pareto);
		
		
		
	}

}
