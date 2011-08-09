package pareto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import kernel.AlgoritmoAprendizado;
import kernel.nuvemparticulas.Particula;
import solucao.ComparetorCrowdedOperator;
import solucao.ComparetorDistancia;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class FronteiraPareto {
	
	private ArrayList<Solucao> fronteira = null;
	
	//public ArrayList<Particula> fronteiraNuvem = null;
	
	public double S;
	
	public double limite_ocupacao;
	
	public boolean rank;
	
	public double[] objetivosMaxMin = null;
	
	public String[] maxmim = null;
	

	public double fator;
	
	/*public FronteiraPareto(double s){
		fronteira = new ArrayList<Solucao>();
		fronteiraNuvem = new ArrayList<Particula>();
		S = s;
		
	}*/
	
	public FronteiraPareto(double s, String[] maxmim, boolean r, double ocupacao, double f){
		fronteira = new ArrayList<Solucao>();
		//fronteiraNuvem = new ArrayList<Particula>();
		S = s;
		rank= r;
		limite_ocupacao = ocupacao;
		fator = f;
		this.maxmim = maxmim;
		
		preencherObjetivosMaxMin(maxmim);
	}
	
	/**
	 * M�todo que define para cada objetivo se ele � de maximiza��o ou minimiza��o
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
		fronteira.clear();
		for (Iterator<Solucao> iter = temp.iterator(); iter.hasNext();) {
			Solucao s = (Solucao) iter.next();
			fronteira.add(s);
			
		}
	}
	
	
	
	public void apagarFronteira(){
		fronteira.clear();
	}
	
	
	
	/**
	 * M�todo que adiciona um nova solu��o na fronteira de pareto
	 * @param regra Regra a ser adicionada
	 * @return Valor booleano que especifica se o elemento foi inserido ou nao na fronteira 
	 */
	@SuppressWarnings("unchecked")
	public double add(Solucao solucao){
		//S� adiciona na fronteira caso a regra seja da classe passada como parametro
		solucao.numDominacao = 0;
		if(fronteira.size()==0){
			fronteira.add(solucao);
			return solucao.numDominacao;
		}
		
		int comp;
		
		ArrayList<SolucaoNumerica> cloneFronteira = (ArrayList<SolucaoNumerica>)fronteira.clone();
		
		double[] novosObjetivosSolucao = new double[solucao.objetivos.length];
		
		double eps = 0.01;

		double r = 0;
		if(S!=0.5){
		r = r(solucao.objetivos);
		for (int i = 0; i < solucao.objetivos.length; i++) {
			novosObjetivosSolucao[i] = modificacaoDominanciaParetoCDAS(solucao.objetivos[i], r, S);
		}
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
				r = r(temp.objetivos);
				for (int i = 0; i < temp.objetivos.length; i++) {
					novosObjetivosTemp[i] = modificacaoDominanciaParetoCDAS(temp.objetivos[i], r, S);
				}
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
				//System.out.println("domina: " + temp.indice);
			}
			
		}
		if(solucao.numDominacao == 0){
			fronteira.add(solucao);	
		}
		
		return solucao.numDominacao;
		
	}
	
	
	/**
	 * M�todo que adiciona um nova solu��o na fronteira de pareto
	 * @param regra Regra a ser adicionada
	 * @return Valor booleano que especifica se o elemento foi inserido ou nao na fronteira 
	 */
	@SuppressWarnings("unchecked")
	public double add2(Solucao solucao){
		//S� adiciona na fronteira caso a regra seja da classe passada como parametro
		solucao.numDominacao = 0;
		if(fronteira.size()==0){
			fronteira.add(solucao);
			return solucao.numDominacao;
		}
		
		int comp;
		
		ArrayList<SolucaoNumerica> cloneFronteira = (ArrayList<SolucaoNumerica>)fronteira.clone();
		
		
		
		double[] novosObjetivosSolucao = new double[solucao.objetivos.length];
		
		double r = 0;
		r = r(solucao.objetivos);
		for (int i = 0; i < solucao.objetivos.length; i++) {
			novosObjetivosSolucao[i] = modificacaoDominanciaParetoCDAS(solucao.objetivos[i], r, solucao.S);
		}
	
		
		for (Iterator<SolucaoNumerica> iter = cloneFronteira.iterator(); iter.hasNext();) {
			SolucaoNumerica temp = (SolucaoNumerica) iter.next();
			
			double[] novosObjetivosTemp = new double[temp.objetivos.length];
			
			
			r = r(temp.objetivos);
			for (int i = 0; i < temp.objetivos.length; i++) {
				novosObjetivosTemp[i] = modificacaoDominanciaParetoCDAS(temp.objetivos[i], r, solucao.S);
			}
			
			comp = compararMedidas(novosObjetivosSolucao, novosObjetivosTemp);
			
			
			
			if(comp == -1){
				solucao.numDominacao++;
				//System.out.println("dominada por: " +temp.indice);
			}
			if(comp == 1){
				fronteira.remove(temp);
				//System.out.println("domina: " + temp.indice);
			}
			
			if(limite_ocupacao!=0)
			if(comp == 0){
				double dist = AlgoritmoAprendizado.distanciaEuclidiana(solucao.objetivos, temp.objetivos);
				if(dist<limite_ocupacao){
					//Remocao da solucao no mesmo quadrado, mas menos em uma regiao menos povoada
					if(solucao.crowdDistance < temp.crowdDistance)
						fronteira.remove(temp);
					else{
						solucao.numDominacao++;
					}
				}
			}
			
		}
		if(solucao.numDominacao == 0){
			fronteira.add(solucao);	
		}
		
		return solucao.numDominacao;
		
	}


	public void addRank(Solucao solucao){
		if(fronteira.size()==0){
			fronteira.add(solucao);
		}
		
		for (Iterator<Solucao> iter = fronteira.iterator(); iter.hasNext();) {
			Solucao temp = (Solucao) iter.next();
			if(solucao.rank< temp.rank){
				fronteira.add(solucao);
				break;
			}
		}
		
	}

	
	public void podarLideresCrowdedOperator(int tamanhoRepositorio){
		if(tamanhoRepositorio<fronteira.size()){
			ComparetorCrowdedOperator comp = new ComparetorCrowdedOperator();
			//ComparetorCrowdedOperator comp = new ComparetorCrowdedOperator();
			Collections.sort(fronteira, comp);
			int diferenca = fronteira.size() - tamanhoRepositorio; 
			for(int i = 0; i<diferenca; i++)
				fronteira.remove(fronteira.remove(fronteira.size()-1));
		}
	}
	
	public void podarLideresAleatorio(int tamanhoRepositorio){
		if(tamanhoRepositorio<fronteira.size()){
			
			Collections.shuffle(fronteira);
			int diferenca = fronteira.size() - tamanhoRepositorio; 
			for(int i = 0; i<diferenca; i++)
				fronteira.remove(fronteira.remove(fronteira.size()-1));
			
			
		}
	}
	
	/**
	 * Metodo que deixa que poda o repositorio em tamanhoRepositorio, com as solucoes com menor distancia
	 * A distancia pode ser calculada atraves de diferentes metodos
	 * @param tamanhoRepositorio
	 */
	public void podarLideresDistancia(int tamanhoRepositorio){
		if(tamanhoRepositorio<fronteira.size()){
			ComparetorDistancia comp = new ComparetorDistancia();
			//ComparetorCrowdedOperator comp = new ComparetorCrowdedOperator();
			Collections.sort(fronteira, comp);
			int diferenca = fronteira.size() - tamanhoRepositorio; 
			for(int i = 0; i<diferenca; i++)
				fronteira.remove(fronteira.remove(fronteira.size()-1));
		}
	}
	
	
	
	/**
	 * Seleciona as solucoes mais proximas ao extremo e mais proximas a solucao ideal, em partes iguais
	 * @param tamanhoRepositorio
	 * @param m
	 * @param ideal
	 */
	public void podarLideresIdeal(int tamanhoRepositorio , Solucao ideal){
		ArrayList<Solucao> solucoes = getFronteira();
		//Se o numero de solucoes eh maior que o tamanho definido para o repositorio
		if(solucoes.size()> tamanhoRepositorio){
			//Para cada solucao calcula sua distancia em relacao a solucao ideal
			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = iterator.next();
				solucao.menorDistancia = AlgoritmoAprendizado.distanciaEuclidiana(ideal.objetivos, solucao.objetivos);				
			}
			
			//Ordena as solucoes em relacao a distancia do idal
			ComparetorDistancia comp = new ComparetorDistancia();
			Collections.sort(solucoes, comp);
			int diferenca = fronteira.size() - tamanhoRepositorio; 
			for(int i = 0; i<diferenca; i++)
				fronteira.remove(fronteira.remove(fronteira.size()-1));
		}

	}
	
	/**
	 * Seleciona as solucoes mais proximas ao extremo e mais proximas a solucao ideal, em partes iguais
	 * @param tamanhoRepositorio
	 * @param m
	 * @param ideal
	 */
	public void podarLideresExtremosIdeal(int tamanhoRepositorio, int m, Solucao ideal){
		ArrayList<Solucao> solucoes = getFronteira();
		//Se o numero de solucoes eh maior que o tamanho definido para o repositorio
		if(solucoes.size()> tamanhoRepositorio){
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

	}
		
	
	public ArrayList<Solucao> getFronteira(){
		return fronteira;
	}
	
	public String toString(){
		return fronteira.toString();
	}
	/**
	 * M�todo que verifica se uma solucao domina a outra
	 * @param sol1 Solucao que sera comparada com as regras pertencentes a fronteira de pareto
	 * @param sol2 Solucao pertencente a fronteira de pareto
	 * @return -1 Se sol1 for dominada, 0 se a sol1 nao domina nem eh dominada, 1 sol1 domina sol2 
	 */
	public int compararMedidas(double[] sol1, double[] sol2){
		//Contador que marca quantos valores da solucao 1 sao maiores que os da solucao 2
		//Se cont for igual ao tamanho dos elementos da solucao 1 entao a solucao 2 eh dominada pela sol1
		//Se cont for igual a 0 a sol2 domina a sol1
		//Se cont for maior do que 0 e menor que o tamanho ela nao domina e nem eh dominada
		int cont = 0; 
		int cont2 = sol1.length;
		for (int i = 0; i < sol1.length; i++) {
			
			/*BigDecimal sol1Big = new BigDecimal(sol1[i]);
			BigDecimal sol2Big = new BigDecimal(sol2[i]);
			
			
			sol1Big = sol1Big.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
			sol2Big = sol2Big.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
			
			
						
			double sol1_i = new Double(sol1Big.toString());
			double sol2_i = new Double(sol2Big.toString());*/
			
			double sol1_i = sol1[i];
			double sol2_i = sol2[i];
			
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
	 * Modifica��o da modifica��o da domin�ncia de Pareto proposta por Sato
	 * Deriva��o do Sen do Wi atrav�s do Cos
	 * @param fix Valor original da fun��o de objetivo de �ndice i
	 * @param r Norma do vetor de objetivos
	 * @param si Par�metro da modifica��o da dominacia (Varia entre 0 e 1)
	 * @return
	 */
	public double modificacaoDominanciaParetoCDAS(double fix, double r, double si){
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
	 * Aplica a epsilon-dominance - (fx - epsilon) 
	 * @param fx
	 * @param epsilon
	 * @return
	 */
	public double[] modificacaoDominanciaParetoEpsilon(double[] fx, double epsilon){
		double[] retorno = new double[fx.length];
	
		for (int i = 0; i < fx.length; i++) {
			double d = fx[i];
			double novo_valor = d-epsilon;
			retorno[i] = Math.max(0, novo_valor);
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
	
		double[] novosObjetivosSolucao = new double[solucao.objetivos.length];
		
		double r = r(solucao.objetivos);
		for (int i = 0; i < solucao.objetivos.length; i++) {
			novosObjetivosSolucao[i] = modificacaoDominanciaParetoCDAS(solucao.objetivos[i], r, S);
		}
		
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao temp = iter.next();
			
			double[] novosObjetivosTemp = new double[temp.objetivos.length];

			r = r(temp.objetivos);
			for (int i = 0; i < temp.objetivos.length; i++) {
				novosObjetivosTemp[i] = modificacaoDominanciaParetoCDAS(temp.objetivos[i], r, S);
			}

			
			comp = compararMedidas(novosObjetivosSolucao, novosObjetivosTemp);
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
			
			
			
			comp = compararMedidas(solucao.combRank, temp.combRank);
			if(comp == -1)
				numDominacao++;
		}
		
		return numDominacao;
	}
	
	public void imprimir(){

		
		int j = 0;
		for (Iterator<Solucao> iterator = fronteira.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			System.out.print(j + "\t");
			for (int i = 0; i < solucao.objetivos.length; i++) {
				System.out.print(new Double(solucao.objetivos[i]).toString().replace('.', ',') + "\t");
			}
			System.out.print("\t");
			
					
			double[] novosObjetivosSolucao = new double[solucao.objetivos.length];

			double r = r(solucao.objetivos);
			for (int i = 0; i < solucao.objetivos.length; i++) {
				novosObjetivosSolucao[i] = modificacaoDominanciaParetoCDAS(solucao.objetivos[i], r, S);
			}


			System.out.print(j++ + "\t");
			for (int i = 0; i < novosObjetivosSolucao.length; i++) {
				System.out.print(new Double(novosObjetivosSolucao[i]).toString().replace('.', ',') + "\t");

			}
			
			System.out.println();
		}
		
		System.out.println();
	}
	
	public void imprimir(ArrayList<Particula> pop){

		
		int j = 0;
		for (Iterator<Particula> iterator = pop.iterator(); iterator.hasNext();) {
			Particula particula = (Particula) iterator.next();
			System.out.print(j + "\t");
			for (int i = 0; i < particula.solucao.objetivos.length; i++) {
				System.out.print(new Double(particula.solucao.objetivos[i]).toString().replace('.', ',') + "\t");
			}
			System.out.print("\t");
		

			SolucaoNumerica solucao = particula.solucao;
			double[] novosObjetivosSolucao = new double[solucao.objetivos.length];

			double r = r(solucao.objetivos);
			for (int i = 0; i < solucao.objetivos.length; i++) {
				novosObjetivosSolucao[i] = modificacaoDominanciaParetoCDAS(solucao.objetivos[i], r, S);
			}
			
			
			
			System.out.print(j++ + "\t");
			for (int i = 0; i < novosObjetivosSolucao.length; i++) {
				System.out.print(new Double(novosObjetivosSolucao[i]).toString().replace('.', ',') + "\t");

			}
			
			System.out.println();
		}
		
		System.out.println();
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
		for (Iterator<Solucao> iterator = fronteira.iterator(); iterator.hasNext();) {
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
		for (Iterator<Solucao> iterator = fronteira.iterator(); iterator.hasNext();) {
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
