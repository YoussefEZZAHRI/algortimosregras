package kernel.nuvemparticulas;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import problema.Problema;
import pareto.FronteiraPareto;
import rank.AverageRank;
import rank.BalancedRank;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.AlgoritmoAprendizado;
import kernel.nuvemparticulas.lider.EscolherIdeal;
import kernel.nuvemparticulas.lider.EscolherLider;
import kernel.nuvemparticulas.lider.EscolherMetodoSigma;
import kernel.nuvemparticulas.lider.EscolherOposto;
import kernel.nuvemparticulas.lider.EscolherTorneioBinario;
import kernel.nuvemparticulas.lider.EscolherAleatorio;
import kernel.nuvemparticulas.lider.EscolherWSum;
import kernel.nuvemparticulas.lider.EscolherNWSum;





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
	
	public double S_MAX = 0.5;
	
	public EscolherLider escolherLider = null;
	
	
	
	/**
	 * 	
	 * @param n - numero de variaveis
	 * @param prob - problema
	 * @param g - numero de geracoes
	 * @param a - numero de avaliacoes
	 * @param t - tamanho da populacao
	 * @param s - valor do s do metodo do sato
	 * @param maxmim - string q indica se cada objetivo eh de maximizacao ou minimizacao
	 * @param tRank - 
	 * @param smax
	 * @param el
	 * @param eps
	 * @param tRepositorio
	 * @param tPoda
	 */
	public MOPSO(int n, Problema prob, int g, int a, int t, double s, String[] maxmim, String tRank, double smax, String el, double eps, int tRepositorio, String tPoda){
		super(n,prob,g, a,t, tRank, eps, tRepositorio, tPoda);
		populacao = new ArrayList<Particula>();
		//repositorio = new ArrayList<Particula>();
		pareto = new FronteiraPareto(s, maxmim,rank, eps, problema, tamanhoRepositorio, filter);
		S_MAX = smax;
		if(rank)
			metodoRank.setPareto(pareto);
		this.maxmim = maxmim;
		problema = prob;
		
		setMetodoEscolhaLider(el);
		
		if(filter.equals("ar"))
			metodoRank = new AverageRank(problema.m);
		if(filter.equals("br"))
			metodoRank = new BalancedRank(problema.m);
		if(filter.equals("ag"))
			pareto.partesGrid = 25;
		
		if(filter.equals("eaps") || filter.equals("eapp")){
			if(eps > 1 || eps <= 0){
			System.err.println("Tipo de arquivo escolhido: " + filter);
			System.err.println("Deve ser escolhido um valor de Epsilon entre 0 e 1");
			System.exit(0);
			}
		}
			
		
	}
	
	/**
	 * M�todo que limpa as listas do algoritmo e o prepara para uma nova execu��o.
	 *
	 */
	public void reiniciarExecucao(){
		populacao = new ArrayList<Particula>();
		pareto = new FronteiraPareto(pareto.S, maxmim, pareto.rank, pareto.eps, problema, pareto.archiveSize, pareto.filter);
		problema.avaliacoes =0; 
	}
	
	/**
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public abstract ArrayList<Solucao> executar();
	
	/*public void escolherLideres(){
		escolherLider.escolherLideres(populacao, pareto.getFronteira());
	}*/
	
	
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
		
						
		/*try{
			imprimirParticulas(populacao);
		} catch (IOException ex) {ex.printStackTrace();}*/
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula =  iter.next();
			if(!pareto.getFronteira().contains(particula.solucao)){
				if(!rank){
					particula.solucao.numDominacao = pareto.add((Solucao)particula.solucao.clone());	
				}
				else
					pareto.addRank((Solucao)particula.solucao.clone());
			}
		}	
		
		
		/*try{
			imprimirFronteira(pareto.getFronteira(), 0, "temp");
		} catch(IOException ex){ex.printStackTrace();}*/
	}
	
	/**
	 * Metodo que efetua a poda das solucoes do repositorio de acordo com o metodo definido pelo parametro tipoPoda:
	 * crowd = poda pelo CrowdedOperator = distancia de crowding
	 * AR = poda que calcula o ranking AR e poda pelo valor de AR
	 * BR = poda que calcula o ranking AR e poda pelo valor de BR
	 * ex_id2 = poda que seleciona as solucoes mais proximas dos extremos e da solucao ideal
	 * ex_id = poda que seleciona as solucoes mais proximas dos extremos e da solucao mais proxima da ideal
	 * eucli = poda que utiliza a menor distancia euclidiana de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * sigma = poda que utiliza a menor distancia euclidiana do vetor sigma de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * tcheb = poda que utiliza a menor distancia de tchebycheff de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * rand = aleatorio
	 * p-ideal = oda que utiliza a menor distancia euclidiana de cada solucao em relacao a solucao ideal
	 *  p-pr_id = oda que utiliza a menor distancia euclidiana de cada solucao em relacao a solucao mais proxiama ideal
	 *  p-ag = Poda que adiciona a soluções num grid adaptativo
	 */
	/*public void filter(){
		if(rank)
			pareto.podarLideresCrowdedOperator(tamanhoRepositorio);
		else{
			//Poda somente de  acordo com a distancia de Crowding 
			if(filter.equals("pcrowd"))
				pareto.podarLideresCrowdedOperator(tamanhoRepositorio);
			//Calcula o ranking AR e poda de acordo com o AR, caso haja empate usa a distancia de crowding
			if(filter.equals("par")){				
				rankear(pareto.getFronteira());
				pareto.podarLideresCrowdedOperator(tamanhoRepositorio);
			}
			//Calcula o ranking BR e poda de acordo com o BR, caso haja empate usa a distancia de crowding
			if(filter.equals("pbr")){
				rankear(pareto.getFronteira());
				pareto.podarLideresCrowdedOperator(tamanhoRepositorio);
			}
			
			if(filter.equals("pideal")){
				Solucao ideal = obterSolucoesExtremasIdeais(pareto.getFronteira(), false).get(problema.m).get(0);
				pareto.podarLideresIdeal(tamanhoRepositorio, ideal);
			}
			
			if(filter.equals("ppr_id")){
				Solucao ideal = obterSolucoesExtremasIdeais(pareto.getFronteira(), true).get(problema.m).get(0);
				pareto.podarLideresExtremosIdeal(tamanhoRepositorio, problema.m, ideal);
			}

			if(filter.equals("pex_id2")){
				Solucao ideal = obterSolucoesExtremasIdeais(pareto.getFronteira(), false).get(problema.m).get(0);
				pareto.podarLideresExtremosIdeal(tamanhoRepositorio, problema.m, ideal);
			}

			if(filter.equals("pex_id")){
				Solucao ideal = obterSolucoesExtremasIdeais(pareto.getFronteira(), true).get(problema.m).get(0);
				pareto.podarLideresExtremosIdeal(tamanhoRepositorio, problema.m, ideal);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("peucli")){
				ArrayList<ArrayList<Solucao>> extremos = obterSolucoesExtremasIdeais(pareto.getFronteira(), true);
				definirDistanciasSolucoesProximasIdeais(extremos, pareto.getFronteira(), "euclidiana");
				pareto.podarLideresDistancia(tamanhoRepositorio);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("psigma")){
				ArrayList<ArrayList<Solucao>> extremos = obterSolucoesExtremasIdeais(pareto.getFronteira(), true);
				definirDistanciasSolucoesProximasIdeais(extremos, pareto.getFronteira(), "sigma");
				pareto.podarLideresDistancia(tamanhoRepositorio);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("ptcheb")){
				ArrayList<ArrayList<Solucao>> extremos = obterSolucoesExtremasIdeais(pareto.getFronteira(), true);
				definirDistanciasSolucoesProximasIdeais(extremos, pareto.getFronteira(), "tcheb");
				pareto.podarLideresDistancia(tamanhoRepositorio);
			}
			if(filter.equals("prand"))
				pareto.podarLideresAleatorio(tamanhoRepositorio);
			if(filter.equals("pag"))
				pareto.podarAdaptiveGrid(tamanhoRepositorio, problema.m, partesGrid);
		}
			
		
	}*/
	
	/**
	 * Metodo que define o parametro S do metodo CDAS automaticamente para cada solucao.
	 * Solcuoes mais no extremo sao definidas com o valor S_MAX para evitar que elas sejam dominadas
	 * O valor de S vai diminuindo quando chega as solucoes mais ao centro
	 * @param particulas
	 */
	public void definirSExtremos(ArrayList<Particula> particulas){
		double max = pareto.S;
		double min = S_MAX;
		
		double maiorDiff = 0;
		double menorDiff = Double.MAX_VALUE;
		
		//Obtem quais solucoes estao mais nos extremos
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Solucao solucao = ((Particula) iterator.next()).solucao; 
			solucao.setDiferenca();
			if(solucao.diff > maiorDiff){
				//System.out.println("Maior: " + solucao);
				maiorDiff = solucao.diff;
			}
			if(solucao.diff < menorDiff){
				//System.out.println("Menor: " + solucao);
				menorDiff = solucao.diff;
			}
			
		}
		
		double denominador = maiorDiff - menorDiff;
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			
			//Define o S para cada solucao, de acordo com sua proximidade dos extremos
			Solucao solucao = ((Particula) iterator.next()).solucao; 
			double diff = solucao.diff;

			double parte1 = (max-min)*(maiorDiff-diff);
			double parte2 = 0;
			if(denominador != 0)
				parte2 = parte1/denominador;
			double S = -1.0*parte2 + max;
			solucao.S = S;
			//System.out.println(solucao.diff + " - " +  S);
			
		}
	//	System.out.println();
	}
	
	/**
	 * Metodo que defineo valor de S automaticamente, com S maior para solcoes em areas povadas e menor para solcuoes em areas menos povadas 
	 * @param particulas
	 */
	public void definirS(ArrayList<Particula> particulas){
		double maiorCD = 0;
		double max = pareto.S;
		double min = S_MAX;
		
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
	
	/**
	 * Calcula a distancia de Crowding para um conjunto de particulas
	 * @param particulas
	 */
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
	
	/**
	 * Para cada particula, conta quantas as particulas estao dentro do raio de valor limite_ocupacao
	 * @param particulas
	 */
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
	
	public void setMetodoEscolhaLider(String escolha){
		escolherLider = new EscolherTorneioBinario();
		if(escolha.equals("tb"))
			escolherLider = new EscolherTorneioBinario();
		if(escolha.equals("sigma"))
			escolherLider = new EscolherMetodoSigma();
		if(escolha.equals("ideal"))
			escolherLider = new EscolherIdeal(problema.m);
		if(escolha.equals("op"))
			escolherLider = new EscolherOposto();
		if(escolha.equals("rdm"))
			escolherLider = new EscolherAleatorio();
		if(escolha.equals("WSum"))
			escolherLider = new EscolherWSum();
		if(escolha.equals("NWSum"))
			escolherLider = new EscolherNWSum();
		
		
			
	}
	
	public void imprimirParticulas(ArrayList<Particula> particulas) throws IOException{
		PrintStream ps = new PrintStream("fronteiras/particulas.txt");
		for (Iterator<Particula> iterator = particulas.iterator(); iterator.hasNext();) {
			Particula particula = (Particula) iterator.next();
			for(int i = 0; i<problema.m;i++){
				ps.print(particula.solucao.objetivos[i] + "\t");
			}
			ps.println();
		}
		
	}
	public void populacaoNoRepositorio(){
		int contador1 = 0;
		int contador2 = 0;
		int contador3 = 0;
		
		for (Iterator<Particula> iterator = populacao.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica)iterator.next().solucao;
		
		if(pareto.getFronteira().contains(solucao))
				contador2++;
		if(pareto.contemSolucaoVariacao(solucao, 0.001))
			contador1++;
		if(pareto.contemSolucaoVariacaoEspacobusca(solucao, 0.001))
			contador3++;
							
		}
		System.out.println(pareto.getFronteira().size() + " - " +  contador2 + " - " + contador1);
	}
	
}
