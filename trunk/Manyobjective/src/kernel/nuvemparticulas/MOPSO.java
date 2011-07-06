package kernel.nuvemparticulas;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import problema.Problema;
import pareto.FronteiraPareto;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.AlgoritmoAprendizado;


/**
 * Classe que implementa o algoritmo da Otimiza��o por nuvem de part�culas multi-objetivo.
 * @author Andr� B. de Carvalho
 *
 */
public abstract class MOPSO extends AlgoritmoAprendizado{

	//Arraylist que cont�m as particulas da execu��o do algoritmo
	public ArrayList<Particula> populacao = null;
	
	//Arraylist que representa o reposit�rio com as solu��es n�o dominadas
	//public ArrayList<Particula> repositorio = null;
	
	private String[] maxmim = null;
	
	
	Problema problema = null;
	
	
	public MOPSO(int n, Problema prob, int g, int a, int t, double s, String[] maxmim, String tRank, double ocupacao){
		super(n,prob,g, a,t, tRank, ocupacao);
		populacao = new ArrayList<Particula>();
		//repositorio = new ArrayList<Particula>();
		pareto = new FronteiraPareto(s, maxmim,rank, ocupacao);
		if(rank)
			metodoRank.setPareto(pareto);
		this.maxmim = maxmim;
		problema = prob;
	}
	
	/**
	 * M�todo que limpa as listas do algoritmo e o prepara para uma nova execu��o.
	 *
	 */
	public void reiniciarExecucao(){
		populacao = new ArrayList<Particula>();
		pareto = new FronteiraPareto(pareto.S, maxmim, pareto.rank, pareto.limite_ocupacao);
		problema.avaliacoes =0; 
	}
	
	/**
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public abstract ArrayList<Solucao> executar();
	
	public abstract void escolherLideres();
	
	
	/**
	 * M�todo que inicia a popula��o de part�culas. 
	 */
	public void inicializarPopulacao(){
		
		for(int i = 0; i<tamanhoPopulacao; i++){
			Particula particula = new Particula();
			//Contador utilizada para a cria��o da regra n�o ficar presa no la�o
			int cont = 0;
			do{
				SolucaoNumerica s = new SolucaoNumerica(n, problema.m);
				s.iniciarSolucaoAleatoria();
				particula.iniciarParticulaAleatoriamente(problema, s);
				problema.calcularObjetivos(s);
				cont++;
			}while(populacao.contains(particula) && (cont<20));
			//Avaliando os objetivos da particula;
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);	
		}
		if(rank)
			rankParticula(populacao);
	}
	
	/**
	 * M�todo que preenche o respositorio com as solu��o n�o dominadas
	 *
	 */
	public void atualizarRepositorio(){
		
		//calcularCrowdingDistanceParticula(populacao);
		
		//contarParticulasLimitesRaio(populacao);
				
		/*try{
			imprimirParticulas(populacao);
		} catch (IOException ex) {ex.printStackTrace();}*/
		
		//ComparetorCrowdedOperatorParticula comp = new ComparetorCrowdedOperatorParticula();
		
		//ComparetorOcupacaoParticula comp = new ComparetorOcupacaoParticula();
		
		//Collections.sort(populacao, comp);
		
		//definirS(populacao);
		
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula =  iter.next();
			if(!pareto.fronteira.contains(particula.solucao)){
				if(!rank)
					particula.solucao.numDominacao = pareto.add((Solucao)particula.solucao.clone());
				else
					pareto.addRank((Solucao)particula.solucao.clone());
				
			}
		}	
	}
	
	public void definirS(ArrayList<Particula> particulas){
		double maiorCD = 0;
		double max = 0.35;
		double min = 0.45;
		
		/*for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula =  iterator.next();
			double cd = particula.solucao.ocupacao;
			if(cd!=Double.MAX_VALUE){
				maiorCD = particula.solucao.ocupacao; 
				break;
			}
		}*/
		
		maiorCD = particulas.get(particulas.size()-1).solucao.ocupacao;
		double menorCD = particulas.get(0).solucao.ocupacao;
		double denominador = maiorCD - menorCD;
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula =  iterator.next();
			double cd = particula.solucao.ocupacao;
			if(cd == Double.MAX_VALUE)
				particula.solucao.S = max;
			else{
				double parte1 = (max-min)*(maiorCD-cd);
				double parte2 = 0;
				if(denominador != 0)
					parte2 = parte1/denominador;
				double S = -1.0*parte2 + max;
				particula.solucao.S = S;
				//System.out.println(particula.solucao.ocupacao + " - " +  S);
			}
		}
		//System.out.println();
	}
	
	public void calcularCrowdingDistanceParticula(ArrayList<Particula> particulas){
		
		ArrayList<Solucao> solucoes = new ArrayList<Solucao>();
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula =  iterator.next();
			particula.solucao.crowdDistance = 0;
			solucoes.add(particula.solucao);
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
	
	public void contarParticulasLimitesRaio(ArrayList<Particula> particulas){

		for(int k = 0; k<particulas.size(); k++){
			Solucao solucao = ((Particula)particulas.get(k)).solucao;
			solucao.ocupacao = 0;

			for(int i = 0; i<particulas.size(); i++){
				Solucao solucao_i = ((Particula)particulas.get(i)).solucao;
				//Se não é a solução corrente
				if(i != k){
					
					double dist = distanciaEuclidiana(solucao.objetivos, solucao_i.objetivos);
					if(dist<limite_ocupacao)
						solucao.ocupacao++;
				}			
			}
		}

	}
	
	public void imprimirParticulas(ArrayList<Particula> particulas) throws IOException{
		PrintStream ps = new PrintStream("fronteira.txt");
		for (Iterator iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula = (Particula) iterator.next();
			for(int i = 0; i<problema.m;i++){
				ps.print(particula.solucao.objetivos[i] + "\t");
			}
			ps.println();
		}
		
	}
	
}
