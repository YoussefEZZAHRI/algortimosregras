package kernel.misa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import pareto.FronteiraPareto;
import problema.DTLZ2;
import problema.Problema;

import solucao.ComparetorDominacao;
import solucao.ComparetorRank;
import solucao.Solucao;
import kernel.AlgoritmoAprendizado;


/**
 * Algoritmo MISA proposto por Coello em: Solving Multiobjective Optimization Problems Using
an Artificial Immune System
 * @author Andre
 *
 */
public class MISA extends AlgoritmoAprendizado {

	
	public ArrayList<Solucao> populacao = null;
	public AdaptiveGrid populacaoSecundaria = null;
	public ArrayList<Solucao> clones = null;
	
	//Total de elementos após a clonagem
	public int totalClonagem;
	
	//Taxa de clonagem
	public int taxaClonagem = 1;
	//Numero de divisoes do grid da populacaos secundaria
	public final int partesGrid;
	//Probabilidade da mutacao nao uniforme
	public double non_uniform_prob;
	public double decremento;
	
	/**
	 * 
	 * @param n Número de variaveis
	 * @param prob Problema
	 * @param g Número de gerações
	 * @param t Tamanho máximo da população
	 * @param s Valor do S do método do Sato
	 * @param tc Taxa de clonagem
	 * * @param pg Número de partes da divisão do grid da população secundária
	 */
	public MISA(int n, Problema prob, int g, int t, double s, int tc, int pg){
		super(n,prob,g,t);
		pareto = new FronteiraPareto(s);
		taxaClonagem = tc;
		totalClonagem = taxaClonagem * tamanhoPopulacao;
		partesGrid = pg;
		
		//Probabilidade inicial da mutacao não uniforme
		non_uniform_prob = 0.6;
		//Decremento da mutação não uniforme, variando do incial até 1/n
		double diff = non_uniform_prob - PROB_MUT_COD;
		decremento = diff/(double) t;
	}
	
	@Override
	
	public ArrayList<Solucao> executar() {
		
		//Inicio aleatório da população
		iniciarPopulacao();
		
		problema.avaliacoes = 0;
		
		
		//Laço evolutivo
		for(int g = 0; g<geracoes; g++){
			if(g%10 == 0)
				System.out.print(g + " ");
			//Seleção das melhores soluções
			encontrarSolucoesNaoDominadas(populacao, pareto);
			ArrayList<Solucao> melhores = obterMelhoresAnticorpos(pareto, populacao, 0.05);
			//Adiciona as melhores soluções do problema no grid da populacao secundaria
			for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				solucao.aceita = populacaoSecundaria.add(solucao);
			}
			//Obtém as soluções presentes no grid
			clones = populacaoSecundaria.getAll();
			
			//Colna os melhores anticorpos
			clonarMelhoresAnticorpos(clones);	
			//Aplica uma mutação em todos os clones
			mutacao(clones, PROB_MUT_COD);
			//Aplica uma mutação não uniform nos clones dominados
			//mutacaoSolucoesNaoTaoBoas(clones);			
			//Adiciona todos os clones na população atual
			populacao.addAll(clones);
			//Obtém os novos líderes da população
			FronteiraPareto paretoTemp = new FronteiraPareto(pareto.S);
			encontrarSolucoesNaoDominadas(populacao, paretoTemp);
			//Reduz a população com o tamanho passado como parametro
			reduzirPopulacao(populacao, paretoTemp);	
		}
		return populacao;
	}
	
	/*for (Iterator iterator = clones.iterator(); iterator.hasNext();) {
		Solucao solucao = (Solucao) iterator.next();
		for (int i = 0; i < solucao.variaveis.length; i++) {
			if(solucao.variaveis[i]<0)
				System.out.println();
		}	
	}*/
	
	/**
	 * Método que inicia a população aleatoriamente e inicia a população secundária como vazia
	 */
	public void iniciarPopulacao(){
		populacao = new ArrayList<Solucao>();
		for(int i = 0; i<tamanhoPopulacao; i++){
			Solucao s = new Solucao(n, problema.m);
			s.iniciarSolucaoAleatoria();
			populacao.add(s);
			problema.calcularObjetivos(s);
		}
		
		populacaoSecundaria = new AdaptiveGrid(problema.m, partesGrid);
	}
	

	/**
	 * Método que reduz a população para o tamanho máximo passado como parametro
	 * @param populacaoFinal População final da iteração
	 * @param paretoTemp Conjunto das melhore soluçõs encontradas até então
	 */
	public void reduzirPopulacao(ArrayList<Solucao> populacaoFinal, FronteiraPareto paretoTemp){
		//Se o número das melhores soluções é menor que o tamanho máximo, todas as soluções são adicionadas na população final 
		//e as melhores soluções (domindas por menos soluções) dominadas da população
		//Caso contrário somente as melhore soluções são adicionadas na população final
		if(paretoTemp.fronteira.size()<tamanhoPopulacao){
			ArrayList<Solucao> temp = obterMelhoresAnticorpos(paretoTemp, populacaoFinal, 1.0);
			populacaoFinal.clear();
			populacaoFinal.addAll(temp);
		}
		else{
			ArrayList<Solucao> solucoesFinais = paretoTemp.fronteira;
			//Escolhe as melhores soluções atraves do metodo AR 
			averageRank(solucoesFinais);
			ComparetorRank comp = new ComparetorRank();
			Collections.sort(solucoesFinais, comp);
			
			
			populacaoFinal.clear();
			for(int i = 0; i<tamanhoPopulacao; i++)
				populacaoFinal.add(solucoesFinais.get(i));	
		}
	}
	
	/**
	 * Método que obtém as melhores soluções. Caso o número de soluções seja menor que o valor porcentagemaMinima passado como parametro,
	 * as melhores dominadas soluções são adicionadas nas melhores.
	 * @param paretoAtual Soluçoes não dominadas
	 * @param porcentagemaMinima Porcentagem mínima de soluções que devem ser retornadas
	 * @return
	 */
	public ArrayList<Solucao> obterMelhoresAnticorpos(FronteiraPareto paretoAtual, ArrayList<Solucao> populacao,  double porcentagemaMinima){
		ArrayList<Solucao> melhores = new ArrayList<Solucao>();
		melhores.addAll(paretoAtual.fronteira);
		int maxMelhores = (int)(porcentagemaMinima*tamanhoPopulacao);
		//Caso o número das melhores soluções seja menor que a porcentamge tamanhoMelhores da população deve-se preencher os array das melhores soluções
		if(melhores.size()< maxMelhores){
			ArrayList<Solucao> dominadas = new ArrayList<Solucao>();
			for (Iterator<Solucao> iterator = populacao.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				if(!melhores.contains(solucao)){
					dominadas.add(solucao);
				}
			}
			
			//Ordena as soluções de acordo com o número de dominacao de cada solucao
			ComparetorDominacao comp = new ComparetorDominacao();
			Collections.sort(dominadas, comp);
			int resto = maxMelhores - melhores.size();
			for(int i = 0; i<resto; i++)
				melhores.add((Solucao) dominadas.get(i));	
		}
		return melhores;
	}
	
	/**
	 * Clona as melhores soluções da iteração
	 * @param clones Melhores solucoes da iteração
	 */
	public void clonarMelhoresAnticorpos(ArrayList<Solucao> clones){
		//Calcula o numero estimado de clones para cada elemento a ser colnado
		int numEstimadoClones = (taxaClonagem*populacao.size())/populacaoSecundaria.size();
		clones = new ArrayList<Solucao>();
		if(populacaoSecundaria.isFull())
			clonarFull(numEstimadoClones, populacaoSecundaria.getAll());
		else
			clonarNotFull(numEstimadoClones, populacaoSecundaria.getAll());
		
	}
	
	/**
	 * Clona as melhores soluções caso a população secundária esteja cheia
	 * @param numBase Número estimado de clones para cada solução
	 * @param melhores Soluções que serão clonadas
	 */
	public void clonarFull(int numBase, ArrayList<Solucao> melhores){
		for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			double fator = 1;
			if(!solucao.aceita)
				fator = 0;
			else{
				//Calcula quanto cada elemento vai ser clonado de acordo com a ocupação da célula em que o elemento pertence no grid
				double mediaOcupacao = populacaoSecundaria.obterMediaOcupacao();
				Integer celula = populacaoSecundaria.contains(solucao);				
				int crowdCelula = populacaoSecundaria.obterLotacao(celula, solucao);
				if(crowdCelula<mediaOcupacao)
					fator = 2;
				else
					fator = 0.5;
			}
			for(int j = 0; j<numBase*fator; j++){
				Solucao novaSolucao = (Solucao)solucao.clone();
				clones.add(novaSolucao);
			}
			
		}
		
	}
	
	/**
	 * Clona as melhores soluções caso a população secundária não esteja cheia
	 * @param numBase Número estimado de clones para cada solução
	 * @param melhores Soluções que serão clonadas
	 */
	public void clonarNotFull(int numBase, ArrayList<Solucao> melhores){
		double[][] distancias = new double[melhores.size()][melhores.size()];
		double[] mediasIndividuais = new double[melhores.size()];
		double mediaDistancias = 0;
		double numDist = 0;
		for(int i = 0; i< melhores.size()-1;i++){
			Solucao solucao1 = melhores.get(i);
			for(int j = i+1; j<melhores.size(); j++){
				Solucao solucao2 = melhores.get(j);
				distancias[i][j] = distanciaEuclidiana(solucao1.objetivos, solucao2.objetivos);
				mediaDistancias+= distancias[i][j];
				mediasIndividuais[i] += distancias[i][j];
				mediasIndividuais[j] += distancias[i][j];
				numDist++;
			}
		}
		
		mediaDistancias = mediaDistancias/numDist;
		for (int i = 0; i < mediasIndividuais.length; i++) {
			mediasIndividuais[i] = mediasIndividuais[i]/(mediasIndividuais.length-1);
		}
		
		int densidadeAbaixo = 0;
		int densidadeAcima = 0;
		
		for(int i = 0; i< mediasIndividuais.length;i++){
			if(mediasIndividuais[i]<mediaDistancias)
				densidadeAbaixo++;
			else
				densidadeAcima++;
		}
		
		int i = 0;
		for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			double fator = 1;
			//Se a solução pertence à parte de baixo
			if(mediasIndividuais[i]<mediaDistancias){
				//Se a parte de baixo é mais densa então reduz o número de clones em 50%
				if(densidadeAbaixo>=densidadeAcima)
					fator = 0.5;
				else
					//Caso contrário aumenta o número em 50%
					fator = 1.5;
			} else {
				//Se a solução pertence à parte de cima
				if(mediasIndividuais[i]>mediaDistancias)
					//Se a parte de cima é mais densa reduz em 50%, caso contrário aumenta em 50%
					if(densidadeAbaixo<densidadeAcima)
						fator = 0.5;
					else
						fator = 1.5;
			}
			i++;
			for(int j = 0; j<numBase*fator; j++){
				Solucao novaSolucao = (Solucao)solucao.clone();
				clones.add(novaSolucao);
			}	
		}
	}
	
	/**
	 * Aplica uma mutação polinomial com probabilidade prob em todos os clones
	 * @param solucoes
	 */
	public void mutacao(ArrayList<Solucao> solucoes, double prob){
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();

			mutacaoPolinomial(prob, solucao.variaveis);
			problema.calcularObjetivos(solucao);		

		}
	}
	
	public void mutacaoSolucoesNaoTaoBoas(ArrayList<Solucao> clones){
		
		FronteiraPareto clonesNaoDominados = new FronteiraPareto(pareto.S);
		encontrarSolucoesNaoDominadas(clones, clonesNaoDominados);
		ArrayList<Solucao> melhores = clonesNaoDominados.getFronteira();
		
		ArrayList<Solucao> dominadas = new ArrayList<Solucao>();
		for (Iterator<Solucao> iterator = clones.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			if(!melhores.contains(solucao)){
				dominadas.add(solucao);
			}
		}
		
		mutacao(dominadas, non_uniform_prob);
		non_uniform_prob -= decremento;
		
	}
	

	
	public static void main(String[] args) {
		int n = 10;
		int m = 3;
		Problema prob = new DTLZ2(m);
		
		int g = 50;
		int t = 100;
		
		MISA misa = new MISA(n, prob, g, t, 0.25, 7, 25);
		
		misa.executar();
		
		
	}
	

}
