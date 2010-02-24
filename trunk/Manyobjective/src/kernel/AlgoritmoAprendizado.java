package kernel;



import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;

import pareto.FronteiraPareto;
import problema.Problema;
import solucao.ComparetorObjetivo;
import solucao.ComparetorRank;
import solucao.SolucaoNumerica;

public abstract class AlgoritmoAprendizado {
	
	//Número de variáveis da solução
	public int n;
	public Problema problema = null;
	
	//Número de execuções do mopso-n
	public int geracoes;
	//Tamanho inicial da população
	public int tamanhoPopulacao;
	
	public int numeroavalicoes;
	
	public FronteiraPareto pareto = null;
	
	public final double PROB_MUT_COD;
	
	
	private final double MAX_MUT = 0.5;
	//Flag que indica se algum metodo de rankeamento many-objetivo sera utilizado
	public boolean rank = false;
	
	
	public AlgoritmoAprendizado(int n, Problema p, int g, int avaliacoes, int t){
		this.n = n;
		problema = p;
		geracoes = g;
		tamanhoPopulacao = t;
		PROB_MUT_COD = 1.0/(double)n;
		
		numeroavalicoes = avaliacoes;
	}
	
	public abstract ArrayList<SolucaoNumerica> executar();
	
	public abstract ArrayList<SolucaoNumerica> executarAvaliacoes();
	
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	public void calcularCrowdingDistance(ArrayList<SolucaoNumerica> solucoes){
		for (Iterator<SolucaoNumerica> iterator = solucoes.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			solucao.crowdDistance = 0;
		}
		
		for(int m = 0; m<problema.m; m++){
			ComparetorObjetivo comp = new ComparetorObjetivo(m);
			Collections.sort(solucoes, comp);
			SolucaoNumerica sol1 = solucoes.get(0);
			SolucaoNumerica solN = solucoes.get(solucoes.size()-1);
			sol1.crowdDistance = Double.MAX_VALUE;
			solN.crowdDistance = Double.MAX_VALUE;
			for(int i = 1; i<solucoes.size()-1; i++){
				SolucaoNumerica sol = solucoes.get(i);
				SolucaoNumerica solProx = solucoes.get(i+1);
				SolucaoNumerica solAnt = solucoes.get(i-1);
				sol.crowdDistance += solProx.objetivos[m] - solAnt.objetivos[m];
			}
		}
		
	}
	
	/**
	 * Mutação probabilística
	 * @param prob_mutacao Probabilidade de efetuar a mutação em uma posição
	 * @param vetor1 Vetor que irá sofre a mutação
	 */
	public void mutacaoPolinomial(double prob_mutacao, double[] vetor1){
		for (int i = 0; i < vetor1.length; i++) {
			double pos = vetor1[i];
			double prob = Math.random();
			double delta;
			if(prob<prob_mutacao){
				double u = Math.random();
				if(u<0.5){
					delta = Math.pow(2*u, 1.0/(vetor1.length+1)) - 1;
				} else{
					delta = 1- Math.pow(2*(1-u), 1.0/(vetor1.length+1));
				}
				
			} else
				delta = 0;
			vetor1[i] = Math.max(0,pos + delta*MAX_MUT); 
		}
	}
	
	/**
	 * Mutação probabilística
	 * @param prob_mutacao Probabilidade de efetuar a mutação em uma posição
	 * @param 
	 */
	public void mutacaoPolinomial(double prob_mutacao, SolucaoNumerica solucao){
		for (int i = 0; i < solucao.n; i++) {
			double pos = solucao.getVariavel(i);
			double prob = Math.random();
			double delta;
			if(prob<prob_mutacao){
				double u = Math.random();
				if(u<0.5){
					delta = Math.pow(2*u, 1.0/(solucao.n+1)) - 1;
				} else{
					delta = 1- Math.pow(2*(1-u), 1.0/(solucao.n+1));
				}
				
			} else
				delta = 0;
			solucao.setVariavel(i, pos + delta*MAX_MUT); 
		}
	}
	
	/**
	 * Método que executa a mutação simples, pos = pos + random(0,1)*pos
	 * @param prob_mutacao Probabilidade de efetuar a mutação em uma posição
	 * @param vetor1 Vetor que irá sofre a mutação
	 */
	public void mutacao(double prob_mutacao, double[] vetor1){
		for (int i = 0; i < vetor1.length; i++) {
			double pos = vetor1[i];
			double prob = Math.random();
			if(prob<prob_mutacao){
				pos += (Math.random() % pos);
				vetor1[i] = pos;
			}
		}	
	}
	
	public void averageRank(ArrayList<SolucaoNumerica> solucoes){
		int[][][] A = new int[problema.m][solucoes.size()][solucoes.size()];
		for(int k = 0; k<problema.m; k++){
			for(int i = 0; i<solucoes.size()-1; i++){
				SolucaoNumerica solucaoi = solucoes.get(i);
				for(int j = i+1; j<solucoes.size(); j++){
					SolucaoNumerica solucaoj = solucoes.get(j);
					if(solucaoi.objetivos[k]<solucaoj.objetivos[k]){
						A[k][i][j] = 1;
						A[k][j][i] = -1;
					} else {
						if(solucaoi.objetivos[k]>solucaoj.objetivos[k]){
							A[k][i][j] = -1;
							A[k][j][i] = 1;
						} else {
							A[k][i][j] = 0;
							A[k][j][i] = 0;
						}
					}
				}
			}
		}
		
		for(int i = 0; i<solucoes.size(); i++){
			SolucaoNumerica solucaoi = solucoes.get(i);
			for(int k = 0; k<problema.m; k++){
				for(int j = 0; j<solucoes.size(); j++){
					if(i!=j){
						solucaoi.rank+=((solucoes.size()+1)-A[k][i][j]);
					}
				}
			}
		}
	}
	
	/**
	 * Método que busca as soluções não dominadas da população atual
	 * @return Soluções não dominadas da população
	 */
	public void encontrarSolucoesNaoDominadas(ArrayList<SolucaoNumerica> solucoes, FronteiraPareto pareto){
		for (Iterator<SolucaoNumerica> iter = solucoes.iterator(); iter.hasNext();) {
			SolucaoNumerica solucao =  iter.next();
			pareto.add(solucao);
		}
	}
	

	
	
}
