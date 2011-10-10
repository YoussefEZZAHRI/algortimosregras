package pareto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import problema.Problema;
import kernel.AlgoritmoAprendizado;
import solucao.ComparetorCrowdedOperator;
import solucao.ComparetorDistancia;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class FronteiraPareto {
	
	private ArrayList<Solucao> front = null;
	
	//public ArrayList<Particula> fronteiraNuvem = null;
	
	public double S;
	
	public double limite_ocupacao;
	
	public boolean rank;
	
	public double[] objetivosMaxMin = null;
	
	public String[] maxmim = null;
	
	public AdaptiveGrid grid = null;

	public double fator;
	
	public double eps;
	
	public String filter;
	
	public int archiveSize;
	
	public Problema problema;
	
	public int partesGrid;
	
	/*public FronteiraPareto(double s){
		fronteira = new ArrayList<Solucao>();
		fronteiraNuvem = new ArrayList<Particula>();
		S = s;
		
	}*/
	
	public FronteiraPareto(double s, String[] maxmim, boolean r, double e, Problema prob, int tArquivo, String poda){
		front = new ArrayList<Solucao>();
		//fronteiraNuvem = new ArrayList<Particula>();
		S = s;
		rank= r;
		this.maxmim = maxmim;
		
		eps = e;
		
		problema = prob;
		
		archiveSize = tArquivo;
		
		filter = poda;
		
		preencherObjetivosMaxMin(maxmim);
	}
	

	/**
	 * Metodo que define para cada objetivo se ele e de maximizacao ou minimizacao
	 * @param maxmim
	 */
	public void preencherObjetivosMaxMin(String[] maxmim){
		objetivosMaxMin = new double[maxmim.length];
		for (int i = 0; i < maxmim.length; i++) {
			if(maxmim[i].equals("+"))
				objetivosMaxMin[i] = 1;
			else
				objetivosMaxMin[i] = -1;
		}
	}
	
	public void setFronteira(ArrayList<Solucao> temp){
		front.clear();
		for (Iterator<Solucao> iter = temp.iterator(); iter.hasNext();) {
			Solucao s = (Solucao) iter.next();
			front.add(s);
			
		}
	}
	
	
	
	public void apagarFronteira(){
		front.clear();
	}
	
	
	
	/**
	 * Metodo que adiciona um nova solucao na fronteira de pareto - Arquivador precise
	 * Some Multiobjective Optimizers are Better than Others - Corne and Knowles
	 * @param Solcao solucaos a ser adicionada
	 * @return Valor double que indica por quantas solucoes o elemento eh dominado 
	 */
	@SuppressWarnings("unchecked")
	public double add(Solucao solucao){
		solucao.numDominacao = 0;
		solucao.numDominadas = 0;
		if(front.size()==0){
			front.add(solucao);
			return solucao.numDominacao;
		}

		if(eps==0){

			int comp;

			ArrayList<SolucaoNumerica> cloneFronteira = (ArrayList<SolucaoNumerica>)front.clone();

			double[] novosObjetivosSolucao = new double[solucao.objetivos.length];

			if(S!=0.5){
				novosObjetivosSolucao = modificacaoDominanciaParetoCDAS(solucao.objetivos, S);
			} else{
				//novosObjetivosSolucao  = modificacaoDominanciaParetoEqualizar(solucao.objetivos, fator);
				novosObjetivosSolucao  = solucao.objetivos;
				//System.out.println();
			}

			for (Iterator<SolucaoNumerica> iter = cloneFronteira.iterator(); iter.hasNext();) {
				SolucaoNumerica temp = (SolucaoNumerica) iter.next();

				double[] novosObjetivosTemp = new double[temp.objetivos.length];

				if(S!=0.5){				
					novosObjetivosTemp = modificacaoDominanciaParetoCDAS(temp.objetivos, S);
				} else
					novosObjetivosTemp = temp.objetivos;				
				//novosObjetivosTemp = modificacaoDominanciaParetoEqualizar(temp.objetivos, fator);

				comp = compareObjectiveVector(novosObjetivosSolucao, novosObjetivosTemp);



				if(comp == -1){
					solucao.numDominacao++;
					//	System.out.println("dominada por: " +temp.indice);
				}
				if(comp == 1){
					front.remove(temp);
					solucao.numDominadas++;
					//System.out.println("domina: " + temp.indice);
				}

			}
			if(solucao.numDominacao == 0){
				if(solucao.numDominadas>0)
					front.add(solucao);
				else{
					if(front.size()==archiveSize)
						filter(solucao);
					else
						front.add(solucao);
				}

			}			
		} else{
			if(filter.equals("eapp"))
				return adpativeEpsApprox(solucao);
			else
			if(filter.equals("eaps"))
				return adpativeEpsParetoSet(solucao);
		}
		return solucao.numDominacao;
	}
	
	/**
	 * Epsilon dominance algorithm (Algorithm 1)- Combining Convergence and Diversity in Evolutionary Multi-Objective Optimization
	 * Laumanns, Thiele, Deb , 2002
	 * @param solution Candidate solution to be added to the archive
	 * @return Number of solutions that dominate the candidate
	 */
	public double adpativeEpsApprox(Solucao solution){
		int comp, comp2;
		
		ArrayList<Solucao> dominated = new ArrayList<Solucao>();

		for (Iterator<Solucao> iter = front.iterator(); iter.hasNext();) {
			SolucaoNumerica solution_archive = (SolucaoNumerica) iter.next();

			double[] newObjSolArchive = new double[solution_archive.objetivos.length];

			
			newObjSolArchive = modificacaoDominanciaParetoEpsilon(solution_archive.objetivos, eps);				
			

			comp = compareObjectiveVector(solution.objetivos, newObjSolArchive);
			comp2 = compareObjectiveVector(solution.objetivos, solution_archive.objetivos);

			if(comp == -1){
				solution.numDominacao++;
				//	System.out.println("dominada por: " +temp.indice);
			}
			if(comp2 == 1){
				dominated.add(solution_archive);
				solution.numDominadas++;
				//System.out.println("domina: " + temp.indice);
			}
		}
		
		//If the candidate is not eps-dominated by any other f' in the archive
		if(solution.numDominacao == 0){
			front.add(solution);
			//If any f' is dominated by the candidate, it is removed from the front
			for (Iterator<Solucao> iterator = dominated.iterator(); iterator.hasNext();) {
				Solucao dominada = (Solucao) iterator.next();
				front.remove(dominada);				
			}

		}				
		return solution.numDominacao;
	}
	
	public double adpativeEpsParetoSet(Solucao solution){

		int comp, comp2;
		ArrayList<Solucao> box_dominated = new ArrayList<Solucao>();
		ArrayList<Solucao> objective_dominated = new ArrayList<Solucao>();

		double[] box = box(solution); 
		for (Iterator<Solucao> iter = front.iterator(); iter.hasNext();) {
			SolucaoNumerica solution_archive = (SolucaoNumerica) iter.next();
			double box_archive[] = box(solution);
			comp = compareObjectiveVector(box, box_archive);
			if(comp == 1){
				box_dominated.add(solution_archive);
				solution.numDominadas++;
			}

			comp2 = compareObjectiveVector(solution.objetivos, solution_archive.objetivos);

			//If box(f') == box(f) and f dominates f'
			if(AlgoritmoAprendizado.vector_equality(box, box_archive) && comp2 == 1){
				objective_dominated.add(solution_archive);
			} 

			//If box(f') == box(f) or box(f') dominates box(f)
			if(AlgoritmoAprendizado.vector_equality(box, box_archive) || comp == -1){
				solution.numDominacao++;
			}
		}

		if(box_dominated.size()>0){
			// A' = A U f / D
			front.add(solution);
			for (Iterator<Solucao> iterator = box_dominated.iterator(); iterator.hasNext();) {
				Solucao dominated = (Solucao) iterator.next();
				front.remove(dominated);				
			}
		} else{	
			//If box(f') == box(f) and f dominates f'
			if(objective_dominated.size()>0){
				// A' = A u f /f'
				front.add(solution);
				for (Iterator<Solucao> iterator = objective_dominated.iterator(); iterator.hasNext();) {
					Solucao dominated = (Solucao) iterator.next();
					front.remove(dominated);				
				}
			} else{
				//If don't exist f' | box(f') == box(f) or box(f') dominates box(f)
				if(solution.numDominacao == 0)
					//A' = A U f
					front.add(solution);
			}
		}
		
		return solution.numDominacao;

	}
	
	public double[] box(Solucao solution){
		double[] box = new double[solution.objetivos.length];
		for (int i = 0; i < solution.objetivos.length; i++) {
			double num = solution.objetivos[i];
			box[i] = AlgoritmoAprendizado.log2(num)/(AlgoritmoAprendizado.log2(1+eps));
		}
		return box;
	}
	
	
	
	
	/*
	public double add2(Solucao solucao){
		solucao.numDominacao = 0;
		solucao.numDominadas = 0;
		if(fronteira.size()==0){
			fronteira.add(solucao);
			return solucao.numDominacao;
		}
		
		int comp;
		
		ArrayList<SolucaoNumerica> cloneFronteira = (ArrayList<SolucaoNumerica>)fronteira.clone();
		
		double[] novosObjetivosSolucao = new double[solucao.objetivos.length];

		if(S!=0.5){
			novosObjetivosSolucao = modificacaoDominanciaParetoCDAS(solucao.objetivos, S);
		} else{
			//novosObjetivosSolucao  = modificacaoDominanciaParetoEqualizar(solucao.objetivos, fator);
			novosObjetivosSolucao  = modificacaoDominanciaParetoEpsilon(solucao.objetivos, eps);
			//novosObjetivosSolucao  = solucao.objetivos;
			//System.out.println();
		}
		
		int k = 0;
		for (Iterator<SolucaoNumerica> iter = cloneFronteira.iterator(); iter.hasNext(); k++) {
			SolucaoNumerica temp = (SolucaoNumerica) iter.next();
			
			double[] novosObjetivosTemp = new double[temp.objetivos.length];
			
			if(S!=0.5){
				
					novosObjetivosTemp = modificacaoDominanciaParetoCDAS(temp.objetivos, S);
			} else
				//novosObjetivosTemp = temp.objetivos;
				novosObjetivosTemp = modificacaoDominanciaParetoEpsilon(temp.objetivos, eps);
				//novosObjetivosTemp = modificacaoDominanciaParetoEqualizar(temp.objetivos, fator);
			
			comp = compararMedidas(novosObjetivosSolucao, novosObjetivosTemp);
			
			
			
			if(comp == -1){
				solucao.numDominacao++;
			//	System.out.println("dominada por: " +temp.indice);
			}
			if(comp == 1){
				fronteira.remove(temp);
				solucao.numDominadas++;
				//System.out.println("domina: " + temp.indice);
			}
			
		}
		if(solucao.numDominacao == 0)
			fronteira.add(solucao);
							
		return solucao.numDominacao;
		
	}*/
	
	/**
	 * Metodo que efetua a poda das solucoes do repositorio de acordo com o metodo definido pelo parametro tipoPoda:
	 * crowd = poda pelo CrowdedOperator = distancia de crowding
	 * AR = poda que calcula o ranking AR e poda pelo valor de AR
	 * BR = poda que calcula o ranking AR e poda pelo valor de BR
	 * pr_id = poda que seleciona as solucoes mais proximas dos extremos e da solucao ideal
	 * ex_id = poda que seleciona as solucoes mais proximas dos extremos e da solucao mais proxima da ideal
	 * eucli = poda que utiliza a menor distancia euclidiana de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * sigma = poda que utiliza a menor distancia euclidiana do vetor sigma de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * tcheb = poda que utiliza a menor distancia de tchebycheff de cada solucao em relacao aos extremos ou da solucoa mais proxiama ideal
	 * rand = aleatorio
	 * p-ideal = oda que utiliza a menor distancia euclidiana de cada solucao em relacao a solucao ideal
	 *  p-pr_id = oda que utiliza a menor distancia euclidiana de cada solucao em relacao a solucao mais proxiama ideal
	 *  p-ag = Poda que adiciona a soluções num grid adaptativo
	 *  pdom = a solucao nao dominada nao entra no arquivo, somente entram solucoes que dominem alguma outra
	 *  pub = unbound, todas as solucoes entram
	 */
	public void filter(Solucao nova_solucao){
		if(rank)
			podarLideresCrowdedOperator(nova_solucao);
		else{
			//Poda somente de  acordo com a distancia de Crowding ou AR+CD ou BR+CD 
			if(filter.equals("crowd") || filter.equals("ar") || filter.equals("br"))
				podarLideresCrowdedOperator(nova_solucao);
			//Calcula o ranking AR e poda de acordo com o AR, caso haja empate usa a distancia de crowding
			
			
			if(filter.equals("ideal")){
				Solucao ideal = AlgoritmoAprendizado.obterSolucoesExtremasIdeais(getFronteira(), false, problema).get(problema.m).get(0);
				podarLideresIdeal(nova_solucao, ideal);
			}
			
			if(filter.equals("pr_id")){
				Solucao ideal = AlgoritmoAprendizado.obterSolucoesExtremasIdeais(getFronteira(), true, problema).get(problema.m).get(0);
				podarLideresExtremosIdeal(nova_solucao, ideal,  problema.m, archiveSize);
			}

			if(filter.equals("ex_id")){
				Solucao ideal = AlgoritmoAprendizado.obterSolucoesExtremasIdeais(getFronteira(), false, problema).get(problema.m).get(0);
				podarLideresExtremosIdeal(nova_solucao, ideal,  problema.m, archiveSize);
				
			}

			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("eucli")){
				ArrayList<ArrayList<Solucao>> extremos =AlgoritmoAprendizado. obterSolucoesExtremasIdeais(getFronteira(), true, problema);
				AlgoritmoAprendizado.definirDistanciasSolucoesProximasIdeais(extremos, getFronteira(), "euclidiana", problema);
				podarLideresDistancia(nova_solucao);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("sigma")){
				ArrayList<ArrayList<Solucao>> extremos =AlgoritmoAprendizado. obterSolucoesExtremasIdeais(getFronteira(), true, problema);
				AlgoritmoAprendizado.definirDistanciasSolucoesProximasIdeais(extremos, getFronteira(), "sigma", problema);
				podarLideresDistancia(nova_solucao);
			}
			//Usa a menor distancia em relacao aos extremos e a solucao mais proxima do ideal
			if(filter.equals("tcheb")){
				ArrayList<ArrayList<Solucao>> extremos =AlgoritmoAprendizado. obterSolucoesExtremasIdeais(getFronteira(), true, problema);
				AlgoritmoAprendizado.definirDistanciasSolucoesProximasIdeais(extremos, getFronteira(), "tcheb", problema);
				podarLideresDistancia(nova_solucao);
			}
			//Random selection
			if(filter.equals("rand"))
				podarLideresAleatorio(nova_solucao);
			//The solutions are selected through the Adpative Grid scheme
			if(filter.equals("ag"))
				podarAdaptiveGrid(nova_solucao);
			//Unbound, every non-dominated solutions enters
			if(filter.equals("ub"))
				front.add(nova_solucao);
			//Only enter in the archive solutions that dominate any other
			if(filter.equals("dom"))
				;
		}
	}
	

	public void addRank(Solucao solucao){
		if(front.size()==0){
			front.add(solucao);
		}
		
		for (Iterator<Solucao> iter = front.iterator(); iter.hasNext();) {
			Solucao temp = (Solucao) iter.next();
			if(solucao.rank< temp.rank){
				front.add(solucao);
				break;
			}
		}
		
	}

	
	public void podarLideresCrowdedOperator(Solucao nova_solucao){
		
		
		front.add(nova_solucao);
		
		AlgoritmoAprendizado.calcularCrowdingDistance(front, problema.m);
		
		ComparetorCrowdedOperator comp = new ComparetorCrowdedOperator();
		Collections.sort(front, comp);
		front.remove(front.remove(front.size()-1));
		
	}
	
	public void podarLideresAleatorio(Solucao nova_solucao){
		
		front.add(nova_solucao);
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		double num = rand.nextDouble();
		int indice = (int) (Math.round(num*front.size())) % front.size();
		
		front.remove(indice);
			
			
		
	}
	
	/**
	 * Metodo que deixa que poda o repositorio em tamanhoRepositorio, com as solucoes com menor distancia
	 * A distancia pode ser calculada atraves de diferentes metodos
	 */
	public void podarLideresDistancia(Solucao nova_solucao){
		front.add(nova_solucao);
		ComparetorDistancia comp = new ComparetorDistancia();
		Collections.sort(front, comp);
		front.remove(front.remove(front.size()-1));
	}

	
	
	/**
	 * Seleciona as solucoes mais proximas ao extremo e mais proximas a solucao ideal, em partes iguais
	 * @param tamanhoRepositorio
	 * @param m
	 * @param ideal
	 */
	public void podarLideresIdeal(Solucao nova_solucao, Solucao ideal){
		
		front.add(nova_solucao);
		//Para cada solucao calcula sua distancia em relacao a solucao ideal
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(ideal.objetivos, solucao.objetivos);
			//Arredonda a distancia para 4 casas decimais para que a distancia de crowding seja utilizada para diferenciar as solucoes proximas a ideal
			BigDecimal b = new BigDecimal(solucao.menorDistancia);		 
			solucao.menorDistancia = (b.setScale(4, BigDecimal.ROUND_UP)).doubleValue();
		}

		//Ordena as solucoes em relacao a distancia do idal
		ComparetorDistancia comp = new ComparetorDistancia();
		Collections.sort(front, comp);
		front.remove(front.remove(front.size()-1));
		

	}
	
	public void podarAdaptiveGrid(Solucao nova_solucao){	
		grid = new AdaptiveGrid(problema.m, partesGrid, archiveSize);
		front.add(nova_solucao);
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			grid.add(solucao);
		}

		front.clear();
		front.addAll(grid.getAll());

	}
	
	/**
	 * Seleciona as solucoes mais proximas ao extremo e mais proximas a solucao ideal, em partes iguais
	 * @param tamanhoRepositorio
	 * @param m
	 * @param ideal
	 */
	public void podarLideresExtremosIdeal(Solucao nova_solucao, Solucao ideal, int m, int tamanhoRepositorio){
		front.add(nova_solucao);
		ArrayList<Solucao> solucoes = getFronteira();
		//Se o numero de solucoes eh maior que o tamanho definido para o repositorio

		//Calcula a proporcao de solucoes selecionadas para cada extremo e para o ideal
		double proporcao = 1.0/(m+1);
		int num_sol = (int)(tamanhoRepositorio*proporcao);

		ArrayList<Solucao> selecionadas = new ArrayList<Solucao>();

		//Percorre todos os objetivo obtende as solucoes com menores valores (nos extremos)
		for(int i = 0; i< m; i++){
			int contador = 0;
			//Ordena as solcoes de acordo com o objetivo i
			ComparetorObjetivo comp = new ComparetorObjetivo(i);
			Collections.sort(solucoes, comp);
			int j = 0;
			//Preenche a lista "selecionadas" com as menore solucoes por objetivo. Evita colocar solucoes repetidas
			while(contador<num_sol){
				Solucao solucao = solucoes.get(j++);
				if(!selecionadas.contains(solucao)){
					selecionadas.add(solucao);
					contador++;
				}
			}
		}

		//Para cada solucao calcula sua distancia em relacao a solucao ideal
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(ideal.objetivos, solucao.objetivos);				
		}

		//Ordena as solucoes em relacao a distancia do idal
		ComparetorDistancia comp = new ComparetorDistancia();
		Collections.sort(solucoes, comp);

		int contador = 0;
		int j = 0;
		//Preenche o resto das solucoes selecionadas
		int tamanho = tamanhoRepositorio - selecionadas.size();
		while(contador<tamanho){
			Solucao solucao = solucoes.get(j++);
			if(!selecionadas.contains(solucao)){
				selecionadas.add(solucao);
				contador++;
			}
		}
		setFronteira(selecionadas);
	}
		
	
	public ArrayList<Solucao> getFronteira(){
		return front;
	}
	
	public String toString(){
		return front.toString();
	}
	/**
	 * M�todo que verifica se uma solucao domina a outra
	 * @param sol1 Solucao que sera comparada com as regras pertencentes a fronteira de pareto
	 * @param sol2 Solucao pertencente a fronteira de pareto
	 * @return -1 Se sol1 for dominada, 0 se a sol1 nao domina nem eh dominada, 1 sol1 domina sol2 
	 */
	public int compareObjectiveVector(double[] sol1, double[] sol2){
		//Contador que marca quantos valores da solucao 1 sao maiores que os da solucao 2
		//Se cont for igual ao tamanho dos elementos da solucao 1 entao a solucao 2 eh dominada pela sol1
		//Se cont for igual a 0 a sol2 domina a sol1
		//Se cont for maior do que 0 e menor que o tamanho ela nao domina e nem eh dominada
		int cont = 0; 
		int cont2 = sol1.length;
		for (int i = 0; i < sol1.length; i++) {
			
			/*BigDecimal sol1Big = new BigDecimal(sol1[i]);
			BigDecimal sol2Big = new BigDecimal(sol2[i]);
			
			
			sol1Big = sol1Big.setScale(4,BigDecimal.ROUND_HALF_UP);
			sol2Big = sol2Big.setScale(4,BigDecimal.ROUND_HALF_UP);
			
			
						
			double sol1_i = new Double(sol1Big.toString());
			double sol2_i = new Double(sol2Big.toString());*/
			
			double sol1_i = sol1[i];
			double sol2_i = sol2[i];
			
			
			/*if((sol1_i - 0.001) <= 0)
				sol1_i = 0;
			
			if((sol2_i - 0.001) <= 0)
				sol2_i = 0;*/
			
			
			if(sol1_i*objetivosMaxMin[i]>sol2_i*objetivosMaxMin[i]){
				++cont;
			} else {
				if(sol1_i==sol2_i){
					--cont2;
				}
			}
		}
		if(cont == 0){	
			if(cont2 == 0)
				return 0;
			else
				return -1;
		}
		else{
			if(cont>0 && cont<cont2)
				return 0;
			else return 1;
		}
	}
	
	/*public int compararMedidas(Solucao sol1, Solucao sol2){
		if(sol1.rank>sol2.rank)
			return -1;
		else{
			if(sol1.rank<sol2.rank)
				return 1;
				else
					return 0;
		}
	}*/
	
	/**
	 * Modificacao da fronteira de pareto pelo metodo CDAS
	 */
	public double[] modificacaoDominanciaParetoCDAS(double[] fx, double S){
		double r = r(fx);
		double[] retorno = new double[fx.length];
		for (int i = 0; i < fx.length; i++) {
			retorno[i] = modificacaoCDASValor(fx[i], r, S);
		}
		
		return retorno;
	}
	
	/**
	 * Modificao da da dominancia de Pareto proposta por Sato
	 * Derivacao do Sen do Wi atraves do Cos
	 * @param fix Valor original da funcao de objetivo de indice i
	 * @param r Norma do vetor de objetivos
	 * @param si Paremetro da modificacao da dominacia (Varia entre 0.25 e 0.75)
	 * @return
	 */
	public double modificacaoCDASValor(double fix, double r, double si){
		double cosWi = fix/r;
		double cosWi2 = cosWi*cosWi;
		double senWi = Math.sqrt(1-cosWi2);
		double senSiPi = Math.sin(si * Math.PI);
		double cosSiPi = Math.cos(si * Math.PI);
		//Formula: r*sen(Wi+SiPi)/sen(SiPi)
		double numerador = r*((senWi*cosSiPi)+(cosWi*senSiPi));
		double novoFix = numerador/senSiPi;
		
		/*double diff = fix - novoFix;
		
		novoFix = fix + diff;*/
		
		return Math.max(novoFix, 0);
	}
	
	/**
	 * Método que modifica os valores dos objetivos de acordo com a diferença entre cada valor
	 * Visa deslocar os objetivos para o centro do espaco de objetivos
	 * Um valor de objetivo pequeno e muito deslocado se existe um valor grande.
	 * Um valor grande nao sofre deslocamente
	 * Solucoes com valores proximos sao privilegiadas
	 * 
	 * @param fx vetor objetivo
	 * @return vetor objetivo modificado
	 */
	public double[] modificacaoDominanciaParetoEqualizar(double[] fx, double fator){
		if(fator !=0){
			double[] retorno = new double[fx.length];
			double maiorValor = 0;
			double menorValor = Double.MAX_VALUE;
			
			//Procura o maior e o menor valor dos objetivos
			for (int i = 0; i < fx.length; i++) {
				double d = fx[i];
				if(d > maiorValor)
					maiorValor = d;
				if(d< menorValor)
					menorValor = d;
			}

			double diferenca = maiorValor - menorValor;
			//Modifica a cada objetivo de acordo com sua relação a diferenca
			for (int i = 0; i < fx.length; i++) {
				double d = fx[i];
				//Obtem a relacao entre a maior diferenca dos objetivos e o valor do objetivo i
				double relacao = diferenca/d;
				//Calcula a variacao do objetivo. Em funcao do valor do objetivo e a relacao com a diferenca
				//Quanto menor eh o objetivo em relacao a deferenca, maior sera o aumento
				double variacao = (Math.abs(1-relacao)*d)/fator;
				retorno[i] = d+variacao; 
			}

			return retorno;
		}
		else return fx;
	}
	
	/**
	 * Aplica a epsilon-dominance - Minimizacao (fx / (1+epsilon)) - Maximizacao ((1+eps) * fx)  
	 * @param fx
	 * @param epsilon
	 * @return
	 */
	public double[] modificacaoDominanciaParetoEpsilon(double[] fx, double epsilon){
		double[] retorno = new double[fx.length];
	
		for (int i = 0; i < fx.length; i++) {
			double novo_valor = 0;
			if(maxmim[i].equals("-"))
				novo_valor = fx[i]/(1+epsilon);
			else
				novo_valor = fx[i]*(1+epsilon);
			retorno[i] = novo_valor;
		}
		
		return retorno;
	}
	
	public double r(double[] objetivos){
		double soma = 0;
		for (int i = 0; i < objetivos.length; i++) {
			double fix = objetivos[i];
			soma+=fix*fix;
		}
		return Math.sqrt(soma);
	}
	
	/**
	 * M�todo que retorna de quantas solu��es a solu��o passada como par�metro � dominada
	 * @param solucao Solu��o a ser contada o numero de domina��o		
	 * @return Quantas solu��es a solu��o passada como parametro � dominada
	 */
	public double obterNumDomincao(Solucao solucao, ArrayList<Solucao> solucoes){
		
		int numDominacao = 0;
		
		int comp;
	
		double[] novosObjetivosSolucao =  modificacaoDominanciaParetoCDAS(solucao.objetivos, S);
	
		
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao temp = iter.next();
			
			double[] novosObjetivosTemp = modificacaoDominanciaParetoCDAS(temp.objetivos, S);
			
			comp = compareObjectiveVector(novosObjetivosSolucao, novosObjetivosTemp);
			if(comp == -1)
				numDominacao++;
		}
		
		return numDominacao;
	}
	
	public double obterNumDomincaoRank(Solucao solucao, ArrayList<Solucao> solucoes){
		
		int numDominacao = 0;
		
		int comp;
	
		
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao temp = iter.next();
			
			comp = compareObjectiveVector(solucao.combRank, temp.combRank);
			if(comp == -1)
				numDominacao++;
		}
		
		return numDominacao;
	}
	
	/**
	 * Verifica se a solucao tem os mesmos valores de objetivo (+ ou - var) que alguma solucao no repositorio
	 * @param solucao
	 * @param var
	 * @return
	 */
	public boolean contemSolucaoVariacao(Solucao solucao, double var){
		double[] limite_sup = new double[solucao.objetivos.length];
		double[] limite_inf = new double[solucao.objetivos.length];
		
		for (int i = 0; i < solucao.objetivos.length; i++) {
			limite_sup[i] = solucao.objetivos[i] + var; 
			limite_inf[i] =  Math.max(solucao.objetivos[i] - var, 0);;
		}
		
		boolean dentro = false;
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			Solucao sol_fronteira = (Solucao) iterator.next();
			for (int i = 0; i < sol_fronteira.objetivos.length && !dentro; i++) {
				double obj = sol_fronteira.objetivos[i];
				if(obj>=limite_inf[i] && obj<=limite_sup[i])
					dentro = true;
				else{
					dentro = false;
					break;
				}					
			}
			
		}
		
		return dentro;
	}
	
	/**
	 * Verifica se a solucao tem os mesmos valores de objetivo (+ ou - var) que alguma solucao no repositorio
	 * @param solucao
	 * @param var
	 * @return
	 */
	public boolean contemSolucaoVariacaoEspacobusca(SolucaoNumerica solucao, double var){
		double[] limite_sup = new double[solucao.getVariaveis().length];
		double[] limite_inf = new double[solucao.getVariaveis().length];
		
		for (int i = 0; i < solucao.getVariaveis().length; i++) {
			limite_sup[i] = solucao.getVariavel(i) + var; 
			limite_inf[i] =  Math.max(solucao.getVariavel(i) - var, 0);;
		}
		
		boolean dentro = false;
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			SolucaoNumerica sol_fronteira = (SolucaoNumerica) iterator.next();
			for (int i = 0; i < sol_fronteira.getVariaveis().length && !dentro; i++) {
				double obj = sol_fronteira.getVariavel(i);
				if(obj>=limite_inf[i] && obj<=limite_sup[i])
					dentro = true;
				else{
					dentro = false;
					break;
				}					
			}
			
		}
		
		return dentro;
	}

}
