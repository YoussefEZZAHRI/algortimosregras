package problema;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import kernel.AlgoritmoAprendizado;

import pareto.FronteiraPareto;
import solucao.ComparetorDistancia;
import solucao.Solucao;
import solucao.SolucaoNumerica;

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
		super(m);
		problema = "dtlz2";
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g2(solucao.xm);
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
		
		for (int i = 0; i < solucao.objetivos.length; i++) {
			if(solucao.objetivos[i] <0)
				System.out.println();
		}
		return solucao.objetivos;
	}
	
	public ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		double ocupacao = 0;
		double fator = 0;
		double eps = 0;
		
		FronteiraPareto pareto = new FronteiraPareto(s, maxmim, r, ocupacao, fator, eps);
		
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
				if(!pareto.getFronteira().contains(melhor))
					pareto.add(melhor);
				melhores.add(melhor);
			}
		}
		
		ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		for (Iterator<Solucao> iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			saida.add(solucaoNumerica);
		}
		
		return saida;
	}
	
	public boolean validarSolucaoFronteira(SolucaoNumerica s){
		double soma = 0;
		for (int i = 0; i < s.objetivos.length; i++) {
			soma += s.objetivos[i];
		}
		
		if(soma  == 1)
			return true;
		else
			return false;
	}
	
	public double obterDiferencaLimites(int n){
		
		int numSol = 10000;
		ArrayList<SolucaoNumerica> solucoes = obterFronteira(n, numSol);
		
		SolucaoNumerica extremos[] = new SolucaoNumerica[m];
		
		Solucao ideal = new SolucaoNumerica(n, m);
		
		for (int i = 0; i < ideal.objetivos.length; i++) {
			ideal.objetivos[i] = Double.POSITIVE_INFINITY;	
		}
		
		//Obtem a solucoes no extremo e calcula a solucao ideal
		for (Iterator<SolucaoNumerica> iter = solucoes.iterator(); iter.hasNext();) {
			SolucaoNumerica rep = iter.next();
			
		
			for(int j = 0; j<m;j++){
				
				
				if(rep.objetivos[j]<ideal.objetivos[j]){
					ideal.objetivos[j] = rep.objetivos[j];
					extremos[j] = rep;
				}
			}
			
			
		}	
		
		double menor = Double.MAX_VALUE;
		
		
		for (int i = 0; i < extremos.length-1; i++) {
			SolucaoNumerica sol1 = extremos[i];
			for (int j = i+1; j < extremos.length; j++){
				SolucaoNumerica sol2 = extremos[j];
				double distancia = AlgoritmoAprendizado.distanciaEuclidiana(sol1.objetivos, sol2.objetivos);
				if(distancia<menor)
					menor = distancia;
			}
		}
		
		return menor;
	}
	

	

	

	
	public static void main(String[] args) {
		
		int m = 25;
		int numSol = 10000;
		int k = 10;
		
		 DTLZ2 dtlz2 = new DTLZ2(m);
		 int n = m + k - 1;
		 
		 //System.out.println(dtlz2.obterDiferencaLimites(n));
		 
		 ArrayList<SolucaoNumerica> f = dtlz2.obterFronteira(n, numSol);
		 
		 ArrayList<Solucao> solucoes = new ArrayList<Solucao>();
		 		 
		 dtlz2.distanciaIdeal(f);
		 
		 solucoes.addAll(f);
		 
		 ComparetorDistancia comp = new ComparetorDistancia();
		 
		 Collections.sort(solucoes,comp);
		 
				 
		 
		 
		 
			
		//dtlz2.obterSolucoesExtremas(n, s);
				 
				
		try{
			PrintStream ps = new PrintStream("pareto/DTLZ2_" + m + "pareto.txt");
			for (Iterator<SolucaoNumerica> iterator = f.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
						.next();
				for(int i = 0; i<m; i++){
					ps.print(solucaoNumerica.objetivos[i] + "\t");
				}
				ps.println();
							

			}
		} catch (IOException ex){ex.printStackTrace();}
		
		
		/*try{
			dtlz2.imprimirFronteirar(n, m, numSol);
		} catch (IOException ex){ex.printStackTrace();}*/
		
		
		
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

	public void distanciaIdeal(ArrayList<SolucaoNumerica> solucoes){
		double[] ideal = new double[m];

		for (int i = 0; i < ideal.length; i++) {
			ideal[i] = Double.POSITIVE_INFINITY;	
		}

		//Obtem a solucoes no extremo e calcula a solucao ideal
		for (Iterator<SolucaoNumerica> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			double maiorValor = 0 ;
			double menorValor = Double.MAX_VALUE;
			for(int j = 0; j<m;j++){
				if(rep.objetivos[j] > maiorValor)
					maiorValor = rep.objetivos[j];
				if(rep.objetivos[j]<menorValor)
					menorValor = rep.objetivos[j];
				if(rep.objetivos[j]<=ideal[j]){
					if(rep.objetivos[j]<ideal[j])
					ideal[j] = rep.objetivos[j];
				}
			}
			rep.rank = maiorValor - menorValor;
		}
		
		for (Iterator<SolucaoNumerica> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			rep.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(rep.objetivos, ideal);
		}
		
	}

}
