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
	
	//Total de elementos ap�s a clonagem
	public int totalClonagem;
	
	//Taxa de clonagem
	public int taxaClonagem = 1;
	//Numero de divisoes do grid da populacaos secundaria
	public final int partesGrid;
	
	/**
	 * 
	 * @param n N�mero de variaveis
	 * @param prob Problema
	 * @param g N�mero de gera��es
	 * @param t Tamanho m�ximo da popula��o
	 * @param s Valor do S do m�todo do Sato
	 * @param tc Taxa de clonagem
	 * * @param pg N�mero de partes da divis�o do grid da popula��o secund�ria
	 */
	public MISA(int n, Problema prob, int g, int t, double s, int tc, int pg){
		super(n,prob,g,t);
		pareto = new FronteiraPareto(s);
		taxaClonagem = tc;
		totalClonagem = taxaClonagem * tamanhoPopulacao;
		partesGrid = pg;
	}
	
	@Override
	
	public ArrayList<Solucao> executar() {
		
		//Inicio aleat�rio da popula��o
		iniciarPopulacao();
		//La�o evolutivo
		for(int g = 0; g<geracoes; g++){
			if(g%10 == 0)
				System.out.print(g + " ");
			//Sele��o das melhores solu��es
			encontrarSolucoesNaoDominadas(populacao, pareto);
			ArrayList<Solucao> melhores = obterMelhoresAnticorpos(pareto, populacao, 0.05);
			//Adiciona as melhores solu��es do problema no grid da populacao secundaria
			for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				solucao.aceita = populacaoSecundaria.add(solucao);
			}
			//Obt�m
			clones = populacaoSecundaria.getAll();
			clonarMelhoresAnticorpos(clones);
			
			mutacao(clones);
			populacao.addAll(clones);
			FronteiraPareto paretoTemp = new FronteiraPareto(pareto.S);
			encontrarSolucoesNaoDominadas(populacao, paretoTemp);
			reduzirPopulacao(populacao, paretoTemp);
		}
		
		
		
		return populacao;
	}
	
	/**
	 * M�todo que inicia a popula��o aleatoriamente e inicia a popula��o secund�ria como vazia
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
	

	
	public void reduzirPopulacao(ArrayList<Solucao> populacaoFinal, FronteiraPareto paretoTemp){
		if(paretoTemp.fronteira.size()<tamanhoPopulacao){
			ArrayList<Solucao> temp = obterMelhoresAnticorpos(paretoTemp, populacaoFinal, 1.0);
			populacaoFinal.clear();
			populacaoFinal.addAll(temp);
		}
		else{
			ArrayList<Solucao> solucoesFinais = paretoTemp.fronteira;
			
			if(rank){
				averageRank(solucoesFinais);
				ComparetorRank comp = new ComparetorRank();
				Collections.sort(solucoesFinais, comp);
			}else{
				ComparetorDominacao comp = new ComparetorDominacao();
				Collections.sort(solucoesFinais, comp);
			}
			
			populacaoFinal.clear();
			for(int i = 0; i<tamanhoPopulacao; i++)
				populacaoFinal.add(solucoesFinais.get(i));	
		}
	}
	
	/**
	 * M�todo que obt�m as melhores solu��es. Caso o n�mero de solu��es seja menor que o valor porcentagemaMinima passado como parametro,
	 * as melhores dominadas solu��es s�o adicionadas nas melhores.
	 * @param paretoAtual Solu�oes n�o dominadas
	 * @param porcentagemaMinima Porcentagem m�nima de solu��es que devem ser retornadas
	 * @return
	 */
	public ArrayList<Solucao> obterMelhoresAnticorpos(FronteiraPareto paretoAtual, ArrayList<Solucao> populacao,  double porcentagemaMinima){
		ArrayList<Solucao> melhores = new ArrayList<Solucao>();
		melhores.addAll(paretoAtual.fronteira);
		int maxMelhores = (int)(porcentagemaMinima*tamanhoPopulacao);
		//Caso o n�mero das melhores solu��es seja menor que a porcentamge tamanhoMelhores da popula��o deve-se preencher os array das melhores solu��es
		if(melhores.size()< maxMelhores){
			ArrayList<Solucao> dominadas = new ArrayList<Solucao>();
			for (Iterator<Solucao> iterator = populacao.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				if(!melhores.contains(solucao)){
					dominadas.add(solucao);
				}
			}
			
			//Ordena as solu��es de acordo com o n�mero de dominacao de cada solucao
			ComparetorDominacao comp = new ComparetorDominacao();
			Collections.sort(dominadas, comp);
			int resto = maxMelhores - melhores.size();
			for(int i = 0; i<resto; i++)
				melhores.add((Solucao) dominadas.get(i));	
		}
		return melhores;
	}
	
	public void clonarMelhoresAnticorpos(ArrayList<Solucao> clones){
		int numEstimadoClones = (taxaClonagem*populacao.size())/populacaoSecundaria.size();
		clones = new ArrayList<Solucao>();
		if(populacaoSecundaria.isFull())
			clonarFull(numEstimadoClones, populacaoSecundaria.getAll());
		else
			clonarNotFull(numEstimadoClones, populacaoSecundaria.getAll());
		
	}
	
	public void clonarFull(int numBase, ArrayList<Solucao> melhores){
		for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			double fator = 1;
			if(!solucao.aceita)
				fator = 0;
			else{
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
			//Se a solu��o pertence � parte de baixo
			if(mediasIndividuais[i]<mediaDistancias){
				//Se a parte de baixo � mais densa ent�o reduz o n�mero de clones em 50%
				if(densidadeAbaixo>=densidadeAcima)
					fator = 0.5;
				else
					//Caso contr�rio aumenta o n�mero em 50%
					fator = 1.5;
			} else {
				//Se a solu��o pertence � parte de cima
				if(mediasIndividuais[i]>mediaDistancias)
					//Se a parte de cima � mais densa reduz em 50%, caso contr�rio aumenta em 50%
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
	
	public void mutacao(ArrayList<Solucao> solucoes){
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			int numAtt = (int)(Math.random()*100) % n;
			for(int i = 0; i<numAtt; i++){
				int att = (int)(Math.random()*100) % n;
				double val = solucao.variaveis[att];
				double inc = (Math.random()/10) % val;
				double probFator = Math.random();
				double fator = 1;
				if(probFator<0.5)
					fator = -1;
				solucao.variaveis[att] = val+(fator*inc);
			}
			problema.calcularObjetivos(solucao);			
		}
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
