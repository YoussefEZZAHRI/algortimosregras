package kernel;



import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;

import pareto.FronteiraPareto;
import problema.Problema;
import solucao.ComparetorObjetivo;
import solucao.ComparetorRank;
import solucao.Solucao;

public abstract class AlgoritmoAprendizado {
	
	//Número de variáveis da solução
	public int n;
	public Problema problema = null;
	
	//Número de execuções do mopso-n
	public int geracoes;
	//Tamanho inicial da população
	public int tamanhoPopulacao;
	
	public FronteiraPareto pareto = null;
	
	public final double PROB_MUT_COD;
	
	
	private final double MAX_MUT = 0.5;
	//Flag que indica se algum metodo de rankeamento many-objetivo sera utilizado
	public boolean rank = false;
	
	
	public AlgoritmoAprendizado(int n, Problema p, int g, int t){
		this.n = n;
		problema = p;
		geracoes = g;
		tamanhoPopulacao = t;
		PROB_MUT_COD = 1.0/(double)n;
	}
	
	public abstract ArrayList<Solucao> executar();
	
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	public void calcularCrowdingDistance(ArrayList<Solucao> solucoes){
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			solucao.crowdDistance = 0;
		}
		
		for(int m = 0; m<problema.m; m++){
			ComparetorObjetivo comp = new ComparetorObjetivo(m);
			Collections.sort(solucoes, comp);
			Solucao sol1 = solucoes.get(0);
			Solucao solN = solucoes.get(solucoes.size()-1);
			sol1.crowdDistance = Double.MAX_VALUE;
			solN.crowdDistance = Double.MAX_VALUE;
			for(int i = 1; i<solucoes.size()-1; i++){
				Solucao sol = solucoes.get(i);
				Solucao solProx = solucoes.get(i+1);
				Solucao solAnt = solucoes.get(i-1);
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
	
	public void averageRank(ArrayList<Solucao> solucoes){
		int[][][] A = new int[problema.m][solucoes.size()][solucoes.size()];
		for(int k = 0; k<problema.m; k++){
			for(int i = 0; i<solucoes.size()-1; i++){
				Solucao solucaoi = solucoes.get(i);
				for(int j = i+1; j<solucoes.size(); j++){
					Solucao solucaoj = solucoes.get(j);
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
			Solucao solucaoi = solucoes.get(i);
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
	public void encontrarSolucoesNaoDominadas(ArrayList<Solucao> solucoes, FronteiraPareto pareto){
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao solucao =  iter.next();
			pareto.add(solucao);
		}
	}
	

	
	
}
