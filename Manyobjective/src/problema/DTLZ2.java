package problema;

import java.util.ArrayList;
import java.util.Random;

import pareto.FronteiraPareto;
import solucao.Solucao;

/**
 * Classe que representa o problema DTLZ2
 * @author andre
 *
 */

public class DTLZ2 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ2(int m){
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
	
	public ArrayList<Solucao> obterFronteira(int n, int numSol){
		ArrayList<Solucao> melhores = new ArrayList<Solucao>();
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		while(melhores.size()<numSol){
			Solucao melhor = new Solucao(n, m);

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
	
	public boolean validarSolucaoFronteira(Solucao s){
		double soma = 0;
		for (int i = 0; i < s.objetivos.length; i++) {
			soma += s.objetivos[i];
		}
		
		if(soma  == 1)
			return true;
		else
			return false;
	}
	

	
	public static void main(String[] args) {
		
		int m = 3;
		DTLZ2 dtlz2 = new DTLZ2(m);
		dtlz2.obterFronteira(12, 250);
		
		/*FronteiraPareto pareto = new FronteiraPareto(0.25, false);
		
		DTLZ2 dtlz2 = new DTLZ2(m);
		for(int i = 0; i<2; i++){
			Solucao sol = new Solucao(5, m);
			sol.iniciarSolucaoAleatoria();
			dtlz2.calcularObjetivos(sol);
			System.out.println(sol);
			pareto.add(sol);
		}
		System.out.println("Fronteira:");
		System.out.println(pareto);
		*/
		
		
	}

}
