package kernel;



import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;

import kernel.nuvemparticulas.Particula;

import pareto.FronteiraPareto;
import problema.Problema;
import rank.AverageRank;
import rank.BalancedDominationRank;
import rank.BalancedRank;
import rank.BalancedRankObj;
import rank.CombinacaoRank;
import rank.GB;
import rank.MaximumRank;
import rank.Rank;
import rank.RankDominancia;
import rank.SumWeightedGlobalRatios;
import rank.SumWeightedRatios;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoBinaria;
import solucao.SolucaoNumerica;

public abstract class AlgoritmoAprendizado {
	
	//N�mero de vari�veis da solu��o
	public int n;
	public Problema problema = null;
	
	//N�mero de execu��es do mopso-n
	public int geracoes;
	//Tamanho inicial da popula��o
	public int tamanhoPopulacao;
	
	public int numeroavalicoes;
	
	public FronteiraPareto pareto = null;
	
	public final double PROB_MUT_COD;
	
	
	private final double MAX_MUT = 0.5;
	//Flag que indica se algum metodo de rankeamento many-objetivo sera utilizado
	public boolean rank = false;
	public String tipoRank;
	
	public Rank metodoRank = null;
	
	
	
	public AlgoritmoAprendizado(int n, Problema p, int g, int avaliacoes, int t, String tRank){
		this.n = n;
		problema = p;
		geracoes = g;
		tamanhoPopulacao = t;
		PROB_MUT_COD = 1.0/(double)n;
		
		numeroavalicoes = avaliacoes;
		tipoRank = tRank;
		iniciarMetodoRank();
		
	}
	
	public abstract ArrayList<Solucao> executar();
	
	public abstract ArrayList<Solucao> executarAvaliacoes();
	
	
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	public void calcularCrowdingDistance(ArrayList<Solucao> solucoes){
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao =  iterator.next();
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
	 * Muta��o probabil�stica
	 * @param prob_mutacao Probabilidade de efetuar a muta��o em uma posi��o
	 * @param vetor1 Vetor que ir� sofre a muta��o
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
	
	public void mutacao(double prob_mutacao, Solucao solucao){
		if(solucao.isNumerica())
			mutacaoPolinomialNumerica(prob_mutacao, (SolucaoNumerica)solucao);
		else
			((SolucaoBinaria) solucao).mutacaoSimples(prob_mutacao);
	}
	
	/**
	 * Muta��o probabil�stica
	 * @param prob_mutacao Probabilidade de efetuar a muta��o em uma posi��o
	 * @param 
	 */
	public void mutacaoPolinomialNumerica(double prob_mutacao, SolucaoNumerica solucao){
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
	 * M�todo que executa a muta��o simples, pos = pos + random(0,1)*pos
	 * @param prob_mutacao Probabilidade de efetuar a muta��o em uma posi��o
	 * @param vetor1 Vetor que ir� sofre a muta��o
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
	
	public void rankParticula(ArrayList<Particula> particulas){
		ArrayList<Solucao> solucoes = new ArrayList<Solucao>();
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula = (Particula) iterator.next();
			solucoes.add(particula.solucao);
		}
		
		
		rankear(solucoes);
		solucoes.clear();
		solucoes = null;
	}
	
	public void iniciarMetodoRank(){
		rank = true;
		
		String[] rs = tipoRank.split("_");
		
		ArrayList<Rank> ranks = new ArrayList<Rank>();
		StringBuffer nomeTemp = new StringBuffer();
		
		for (int i = 0; i < rs.length; i++) {
			String rankTemp = rs[i];
			nomeTemp.append(rankTemp);
			if(rankTemp.equals("ar"))
				metodoRank = new AverageRank(problema.m);
			else{
				if(rankTemp.equals("mr"))
					metodoRank = new MaximumRank(problema.m);
				else{
					if(rankTemp.equals("br"))
						metodoRank = new BalancedRank(problema.m);
					else{
						if(rankTemp.equals("bdr"))
							metodoRank = new BalancedDominationRank(problema.m);
						else{
							if(rankTemp.equals("bro"))
								metodoRank = new BalancedRankObj(problema.m);
							else{
								if(rankTemp.equals("sr"))
									metodoRank = new SumWeightedRatios(problema.m);
								else{
									if(rankTemp.equals("sgr"))
										metodoRank = new SumWeightedGlobalRatios(problema.m);
									else{
										if(rankTemp.equals("nsga"))
											metodoRank = new RankDominancia(problema.m);
										else{
											if(rankTemp.equals("gb"))
												metodoRank = new GB(problema.m, "-");
											else
												rank = false;
										}
									}
								}
							}
						}

					}
				}
			}
			
			ranks.add(metodoRank);
		}
		
		if(rs.length>1){
			metodoRank = new CombinacaoRank(problema.m, ranks);
			tipoRank = "comb_"+ nomeTemp;
		}
	}
	
	public void rankear(ArrayList<Solucao> solucoes){
		metodoRank.rankear(solucoes, -1);
		//if(tipoRank.equals("ar") ||tipoRank.equals("ar2"))
		/*	averageRank(solucoes);
			int i = 0;
			System.out.print("AR: \t");
			for (Iterator iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				System.out.println(solucao.rank + "\t");
				
			}
			System.out.println();
			maximumAverageRank(solucoes);
			i = 0;
			System.out.print("MR: \t");
			for (Iterator iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				System.out.println(solucao.rank + "\t");
				
			}
			System.out.println();
			weightedAverageRank(solucoes);
			i = 0;
			System.out.print("WR: \t");
			for (Iterator iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				System.out.println(solucao.rank + "\t");
				
			}
			System.out.println();
			System.out.print("SR: \t");
			sumWeightedRatios(solucoes);
			i = 0;
			for (Iterator iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				System.out.println(solucao.rank + "\t");
				
			}*/
		/*if(tipoRank.equals("war") || tipoRank.equals("war2"))
			weightedAverageRank(solucoes);
		if(tipoRank.equals("mar") || tipoRank.equals("mar2"))
			weightedAverageRank(solucoes);*/
	}
	


	
	/**
	 * M�todo que busca as solu��es n�o dominadas da popula��o atual
	 * @return Solu��es n�o dominadas da popula��o
	 */
	public void encontrarSolucoesNaoDominadas(ArrayList<Solucao> solucoes, FronteiraPareto pareto){
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao solucao =  iter.next();
			pareto.add(solucao);
		}
	}
	

	
	
}
