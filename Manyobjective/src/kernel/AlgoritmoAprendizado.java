package kernel;



import java.io.IOException;
import java.io.PrintStream;
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
import rank.MaximumRank;
import rank.Rank;
import rank.RankDominancia;
import rank.SumWeightedGlobalRatios;
import rank.SumWeightedRatios;
import solucao.ComparetorDistancia;
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
	
	public double limite_ocupacao;
	
	
	
	
	private final double MAX_MUT = 0.5;
	//Flag que indica se algum metodo de rankeamento many-objetivo sera utilizado
	public boolean rank = false;
	public String tipoRank;
	
	public Rank metodoRank = null;
	
	
	
	public AlgoritmoAprendizado(int n, Problema p, int g, int avaliacoes, int t, String tRank, double ocupacao){
		this.n = n;
		problema = p;
		geracoes = g;
		tamanhoPopulacao = t;
		PROB_MUT_COD = 1.0/(double)n;
		
		numeroavalicoes = avaliacoes;
		tipoRank = tRank;
		iniciarMetodoRank();
		
		limite_ocupacao = ocupacao;
		
	}
	
	public abstract ArrayList<Solucao> executar();
	
	public abstract ArrayList<Solucao> executarAvaliacoes();
	
	
	public static double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	public static double distanciaTchebycheff(double[] z, double[] zEstrela, double[] lambda){
		double distancia = 0;
		
		for (int i = 0; i < z.length; i++) {
			distancia = Math.max(distancia, (1.0/lambda[i]) * Math.abs(zEstrela[i] - z[i]));
		}
		
		return distancia;
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
	 * Metodo que remove as solucoes que estao num mesmo cuboide com distancia limite_ocupacao
	 * @param solucoes Solucoes que serao refinadas
	 */
	public void removerGranular(ArrayList<Solucao> solucoes){
		//Calcula a distancia de crowding e o limite de ocupacao das solucoes. Um dos estimadores sera utilizado para podar as solucoes
		calcularCrowdingDistance(solucoes);
		//Conta para cada solucao, se existe alguma outra no mesmo raio de ocupacao
		contarSolucoesLimitesCuboide(solucoes);	
				
		ArrayList<Solucao> cloneSolucoes = (ArrayList<Solucao>)solucoes.clone();
		//Percorre todas as solucoes para remover as solucoes num mesmo cuboide		
		for(int k = 0; k<cloneSolucoes.size(); k++){
			Solucao solucao = cloneSolucoes.get(k);
			//Se a solucao possui alguma outra em seu raio de ocupacao, decide se retira ela ou não
			if(solucao.ocupacao!= 0){
				double[] limites = new double[problema.m*2];
				//Define os limites de ocupacao da solucao
				for(int i = 0;i<problema.m;i++){
					//Posicao final (2 * numero do objetivo) = valor do objetivo menos o  limite. (Limite inferior)  
					limites[2*i] = Math.max(solucao.objetivos[i] - limite_ocupacao, 0);
					//Posicao incial (2 * numero do objetivo + 1) = valor do objetivo mais limite. (Limite superior)
					limites[2*i + 1] = solucao.objetivos[i] + limite_ocupacao;
				}
				
				boolean removida = false;
				//Percorre todas as solucoes, para se ela foi removida ou se todas as solucoes foram percorridas
				for(int i = 0; i<cloneSolucoes.size() && !removida; i++){
					Solucao solucao_i = cloneSolucoes.get(i);
					boolean dentro = true;
					//Se não é a solução corrente
					if(i != k){
						for(int j = 0;j<problema.m && dentro;j++){
							double obj = solucao_i.objetivos[j];
							//Objetivo abaixo do limite inferior
							if(obj < limites[2*j])
								dentro = false;
							//Objetivo acima do limite superior
							if(obj > limites[2*j + 1])
								dentro = false;
						}
						if(dentro){
							//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
							if(solucao.crowdDistance < solucao_i.crowdDistance)
								solucoes.remove(solucao_i);
							else{
								solucoes.remove(solucao);
								removida = true;
							}
							/*try{
								imprimirFronteira(solucoes);
							}catch(IOException ex){ex.printStackTrace();}*/
						}
					}			
				}
			}
		}
	}
	
	/**
	 * Conta para cada solucao quantas solucoes estao dentro do limite definido por solucao.limiteOcupacao
	 * @param solucoes
	 */
	public void contarSolucoesLimitesCuboide(ArrayList<Solucao> solucoes){

		for(int k = 0; k<solucoes.size(); k++){
			Solucao solucao = solucoes.get(k);


			double[] limites = new double[problema.m*2];

			solucao.ocupacao = 0;

			for(int i = 0;i<problema.m;i++){
				//Posicao final (2 * numero do objetivo) = valor do objetivo menos o  limite. (Limite inferior)  
				limites[2*i] = Math.max(solucao.objetivos[i] - limite_ocupacao, 0);
				//Posicao incial (2 * numero do objetivo + 1) = valor do objetivo mais limite. (Limite superior)
				limites[2*i + 1] = solucao.objetivos[i] + limite_ocupacao;
			}

			for(int i = 0; i<solucoes.size(); i++){
				Solucao solucao_i = solucoes.get(i);
				boolean dentro = true;
				//Se não é a solução corrente
				if(i != k){
					for(int j = 0;j<problema.m && dentro;j++){
						double obj = solucao_i.objetivos[j];
						//Objetivo abaixo do limite inferior
						if(obj < limites[2*j])
							dentro = false;
						//Objetivo acima do limite superior
						if(obj > limites[2*j + 1])
							dentro = false;
					}
					if(dentro)
						solucao.ocupacao++;
				}			
			}
		}

	}
	
	public void removerGranularLimites(ArrayList<Solucao> solucoes){
		//Calcula a distancia de crowding e o limite de ocupacao das solucoes. Um dos estimadores sera utilizado para podar as solucoes
		calcularCrowdingDistance(solucoes);
		//Conta para cada solucao, se existe alguma outra no mesmo raio de ocupacao
		contarSolucoesLimites(solucoes);	
				
		ArrayList<Solucao> cloneSolucoes = (ArrayList<Solucao>)solucoes.clone();
		//Percorre todas as solucoes para remover as solucoes num mesmo cuboide		
		for(int k = 0; k<cloneSolucoes.size()-1; k++){
			Solucao solucao = cloneSolucoes.get(k);
			//Se a solucao possui alguma outra em seu raio de ocupacao, decide se retira ela ou não
			if(solucao.ocupacao!= 0){
				boolean removida = false;
				for(int j = 0;j<problema.m;j++){
					double limite_inf = solucao.objetivos[j] - limite_ocupacao; 
					double limite_sup = solucao.objetivos[j] + limite_ocupacao;
					//Percorre todas as solucoes, para se ela foi removida ou se todas as solucoes foram percorridas
					for(int i = k+1; i<cloneSolucoes.size() && !removida; i++){
						Solucao solucao_i = cloneSolucoes.get(i);
						boolean dentro = false;
						//Se não é a solução corrente
						
						double obj = solucao_i.objetivos[j];
						if(obj >= limite_inf && obj <= limite_sup){
							//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
							if(solucao.crowdDistance < solucao_i.crowdDistance)							
								solucoes.remove(solucao_i);
							else{
								solucoes.remove(solucao);
								removida = true;
							}
							/*try{
								imprimirFronteira(solucoes,0,"");
							}catch(IOException ex){ex.printStackTrace();}*/
						}
					}			
				}
			}
		}
	}
	
	
	public void contarSolucoesLimites(ArrayList<Solucao> solucoes){
		
		for(int k = 0; k<solucoes.size(); k++){
			Solucao solucao = solucoes.get(k);
			solucao.ocupacao = 0;
		}

		for(int k = 0; k<solucoes.size()-1; k++){
			Solucao solucao = solucoes.get(k);
			for(int j = 0;j<problema.m;j++){
				double limite_inf = Math.max(solucao.objetivos[j] - limite_ocupacao, 0); 
				double limite_sup = solucao.objetivos[j] + limite_ocupacao;
				for(int i = k+1; i<solucoes.size(); i++){
					Solucao solucao_i = solucoes.get(i);
					//Se não é a solução corrente
					double obj = solucao_i.objetivos[j];
					//Objetivo abaixo do limite inferior
					if(obj >= limite_inf && obj <= limite_sup){
						solucao.ocupacao++;
						solucao_i.ocupacao++;
					}

				}			
			}
		}

	}
	
	/**
	 * Metodo que remove as solucoes que estao num mesmo cuboide com distancia limite_ocupacao
	 * @param solucoes Solucoes que serao refinadas
	 */
	public void removerGranularRaio(ArrayList<Solucao> solucoes){
		//Calcula a distancia de crowding e o limite de ocupacao das solucoes. Um dos estimadores sera utilizado para podar as solucoes
		calcularCrowdingDistance(solucoes);
		//Conta para cada solucao, se existe alguma outra no mesmo raio de ocupacao
		contarSolucoesLimitesRaio(solucoes, limite_ocupacao);	
				
		ArrayList<Solucao> cloneSolucoes = (ArrayList<Solucao>)solucoes.clone();
		//Percorre todas as solucoes para remover as solucoes num mesmo cuboide		
		for(int k = 0; k<cloneSolucoes.size(); k++){
			Solucao solucao = cloneSolucoes.get(k);
			//Se a solucao possui alguma outra em seu raio de ocupacao, decide se retira ela ou não
			if(solucao.ocupacao!= 0){
				
				boolean removida = false;
				//Percorre todas as solucoes, para se ela foi removida ou se todas as solucoes foram percorridas
				for(int i = 0; i<cloneSolucoes.size() && !removida; i++){
					Solucao solucao_i = cloneSolucoes.get(i);
					
					if(i != k){
						
						double dist = distanciaEuclidiana(solucao.objetivos, solucao_i.objetivos);
						if(dist<limite_ocupacao){
							//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
							if(solucao.crowdDistance < solucao_i.crowdDistance)
							//if(solucao.ocupacao < solucao_i.ocupacao)
								solucoes.remove(solucao_i);
							else{
								solucoes.remove(solucao);
								removida = true;
							}
						}
						
					}			
				}
			}
		}
	}
	
	public ArrayList<Solucao> removerGranularRaio2(ArrayList<Solucao> solucoes, double limite_ocupacao){
		//Calcula a distancia de crowding e o limite de ocupacao das solucoes. Um dos estimadores sera utilizado para podar as solucoes
		calcularCrowdingDistance(solucoes);
		//Conta para cada solucao, se existe alguma outra no mesmo raio de ocupacao
		contarSolucoesLimitesRaio(solucoes, limite_ocupacao);	
				
		ArrayList<Solucao> retorno = (ArrayList<Solucao>)solucoes.clone();
		//Percorre todas as solucoes para remover as solucoes num mesmo cuboide		
		for(int k = 0; k<solucoes.size(); k++){
			Solucao solucao = solucoes.get(k);
			//Se a solucao possui alguma outra em seu raio de ocupacao, decide se retira ela ou não
			if(solucao.ocupacao!= 0){
				
				boolean removida = false;
				//Percorre todas as solucoes, para se ela foi removida ou se todas as solucoes foram percorridas
				for(int i = 0; i<solucoes.size() && !removida; i++){
					Solucao solucao_i = solucoes.get(i);
					
					if(i != k){
						
						double dist = distanciaEuclidiana(solucao.objetivos, solucao_i.objetivos);
						if(dist<limite_ocupacao){
							//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
							if(solucao.crowdDistance < solucao_i.crowdDistance)
							//if(solucao.ocupacao < solucao_i.ocupacao)
								retorno.remove(solucao_i);
							else{
								retorno.remove(solucao);
								removida = true;
							}
						}
						
					}			
				}
			}
		}
		
		return retorno;
	}
	
	public ArrayList<Solucao> removerCDAS(ArrayList<Solucao> solucoes, double S){
		FronteiraPareto pareto2 = new FronteiraPareto(S, pareto.maxmim, pareto.rank, 0.0, 0.0);
		
		for (Iterator iterator = solucoes.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			if(!pareto2.getFronteira().contains(solucao))
				pareto2.add((Solucao)solucao.clone());
		}
		
		return pareto2.getFronteira();
	}
	
		
	public void contarSolucoesLimitesRaio(ArrayList<Solucao> solucoes, double limite_ocupacao){

		for(int k = 0; k<solucoes.size(); k++){
			Solucao solucao = solucoes.get(k);
			solucao.ocupacao = 0;

			for(int i = 0; i<solucoes.size(); i++){
				Solucao solucao_i = solucoes.get(i);
				//Se não é a solução corrente
				if(i != k){
					
					double dist = distanciaEuclidiana(solucao.objetivos, solucao_i.objetivos);
					if(dist<limite_ocupacao)
						solucao.ocupacao++;
				}			
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
	 * Mutacao probabilistica
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
												;//metodoRank = new GB(problema.m, "-");
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
		if(metodoRank == null){
			AverageRank ar = new AverageRank(problema.m);
			BalancedRank br = new BalancedRank(problema.m);
			MaximumRank mr = new MaximumRank(problema.m);
			metodoRank = br;
		}
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
	
	public void imprimirFronteira(ArrayList<Solucao> solucoes, int j, String id) throws IOException{
		PrintStream ps = new PrintStream("fronteiras/fronteira_" + id + "_" +j+".txt");
		for (Iterator iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			for(int i = 0; i<problema.m;i++){
				ps.print(new Double (solucao.objetivos[i]).toString().replace('.', ',') + "\t");
			}
			ps.println();
		}
		
	}
	
	public void imprimirFronteira2(ArrayList<SolucaoNumerica> solucoes, int j, String id) throws IOException{
		PrintStream ps = new PrintStream("fronteiras/fronteira_" + id + "_" +j+".txt");
		for (Iterator iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			for(int i = 0; i<problema.m;i++){
				ps.print(new Double (solucao.objetivos[i]).toString().replace('.', ',') + "\t");
			}
			ps.println();
		}
		
	}
	
	
	/**
	 * Metodo que encontra as solucoes nos extremos dos objetivos e a solucao mais proxima a ideal
	 * @param solucoes
	 * @return Array com as solucoes nos extremos e a ideal
	 */
	public Solucao[] obterSolucoesIdeais(ArrayList<Solucao> solucoes){
		
		//double[][] retorno = new double[problema.m+1][problema.m];
		
		Solucao[] extremos = new Solucao[problema.m+1];
	
		
		Solucao ideal = new SolucaoNumerica(n, problema.m);
		
		for (int i = 0; i < ideal.objetivos.length; i++) {
			ideal.objetivos[i] = Double.POSITIVE_INFINITY;	
		}
		
		//Obtem a solucoes no extremo e calcula a solucao ideal
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			
			for(int j = 0; j<problema.m;j++){
				if(rep.objetivos[j]<ideal.objetivos[j]){
					ideal.objetivos[j] = rep.objetivos[j];
					extremos[j] = rep;
				}
			}
		}	
	
		extremos[problema.m] = ideal;
		
		//Busca a solucao mais proxima ideal
		/*double menorDist = Double.MAX_VALUE;
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao rep = iter.next();
			double distancia = distanciaEuclidiana(rep.objetivos, ideal.objetivos);
			if(distancia<menorDist){
				extremos[problema.m] = rep;
				menorDist = distancia;
			}
				
			
			
		}*/
		
		return extremos;		
	}
	
	/**
	 * Método que obtem para cada solucao a solucao do extremo em que ela esta mais perto
	 * @param extremos Solucoes no extremo e a ideal
	 * @param solucoes
	 */
	public void selecionarSolucoesProximasIdeais(Solucao[] extremos, ArrayList<Solucao> solucoes){
		
		double[] lambda = new double[problema.m];
		for (int i = 0; i < lambda.length; i++) {
			lambda[i] = 1;
		}
		
		for (int i = 0; i < extremos.length; i++) {
			extremos[i].calcularSigmaVector();
		}
		
		int[] contador = new int[problema.m+1];
		
		//Percorre todas as solucoes a atribui a cada uma a solucao extrema mais proxima
		//Pode ser utilizada a distancia euclidiana, de tchebycheff ou a distancia dos vetores sigmas
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.calcularSigmaVector();
			solucao.menorDistancia = Double.MAX_VALUE;
			for(int i =0; i<problema.m+1; i++){
				//double distancia = distanciaEuclidiana(extremos[i].objetivos, solucao.objetivos);
				double distancia = distanciaTchebycheff(extremos[i].objetivos, solucao.objetivos, lambda);
				//System.out.println(extremos[i].sigmaVector[0] + " - " + solucao.sigmaVector[0]);
				//double distancia = distanciaEuclidiana(extremos[i].sigmaVector, solucao.sigmaVector);
				if(distancia< solucao.menorDistancia){
					solucao.menorDistancia = distancia;
					solucao.guia = i;
				}
				//System.out.println();
				
			}
			contador[solucao.guia]++;
		}
		
		/*for (int i = 0; i < contador.length; i++) {
			System.out.print(contador[i] +  " ");
		}
		System.out.println();*/
		
		
		
	}
	
	public void selecionarSolucoesNosExtremos(FronteiraPareto pareto, int tamanhoRepositorio){
		ArrayList<Solucao> solucoes = pareto.getFronteira();
		if(solucoes.size()> tamanhoRepositorio){
			double proporcao = 1.0/(problema.m+1);
			int num_sol = (int)(tamanhoRepositorio*proporcao);

			ArrayList<Solucao> selecionadas = new ArrayList<Solucao>();

			for(int i = 0; i< problema.m; i++){
				int contador = 0;
				ComparetorObjetivo comp = new ComparetorObjetivo(i);
				Collections.sort(solucoes, comp);
				int j = 0;
				while(contador<num_sol){
					Solucao solucao = solucoes.get(j++);
					if(!selecionadas.contains(solucao)){
						selecionadas.add(solucao);
						contador++;
					}
				}
			}

			Solucao ideal = obterSolucoesIdeais(solucoes)[problema.m];

			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = iterator.next();
				solucao.menorDistancia = Double.MAX_VALUE;
				for(int i =0; i<problema.m+1; i++){
					double distancia = distanciaEuclidiana(ideal.objetivos, solucao.objetivos);
					solucao.menorDistancia = distancia;
					solucao.guia = i;
				}
			}

			ComparetorDistancia comp = new ComparetorDistancia();
			Collections.sort(solucoes, comp);

			int contador = 0;
			int j = 0;
			int tamanho = tamanhoRepositorio - selecionadas.size();
			while(contador<tamanho){
				Solucao solucao = solucoes.get(j++);
				if(!selecionadas.contains(solucao)){
					selecionadas.add(solucao);
					contador++;
				}
			}
			pareto.setFronteira(selecionadas);
		}

	}
	
	
	
	

	
	
}
