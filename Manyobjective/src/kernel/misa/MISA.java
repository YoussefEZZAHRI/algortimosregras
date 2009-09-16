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
import kernel.Avaliacao;

public class MISA extends AlgoritmoAprendizado {

	
	public ArrayList<Solucao> populacao = null;
	public AdaptiveGrid populacaoSecundaria = null;
	public ArrayList<Solucao> clones = null;
	
	//N�mero de execu��es
	public int geracoes;
	//Tamanho inicial da popula��o
	public int tamanhoPopulacao;
	public int totalClonagem;
	
	public final int taxaClonagem = 6;
	
	public final int partesGrid = 4;
	
	public MISA(int n, Problema prob, int g, int t, double s, boolean mod){
		super(n,prob,g,t);
		pareto = new FronteiraPareto(s,mod);
		totalClonagem = taxaClonagem * tamanhoPopulacao;
	}
	
	@Override
	public ArrayList<Solucao> executar() {
		
		iniciarPopulacao();
		
		for(int g = 0; g<geracoes; g++){
			encontrarSolucoesNaoDominadas(populacao, pareto);
			ArrayList<Solucao> melhores = obterMelhoresAnticorpos(pareto, 0.05);
			for (Iterator<Solucao> iterator = melhores.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				solucao.aceita = populacaoSecundaria.add(solucao);
			}
			clones = populacaoSecundaria.getAll();
			clonarMelhoresAnticorpos(clones);
			
			//rankearSolucoes(clones);
			//ComparetorRank  comp = new ComparetorRank();
			//Collections.sort(solucoes, comp);
			mutacao(clones);
			populacao.addAll(clones);
			FronteiraPareto paretoTemp = new FronteiraPareto(pareto.S, pareto.modificar);
			encontrarSolucoesNaoDominadas(populacao, paretoTemp);
			reduzirPopulacao(populacao, paretoTemp);
		}
		
		
		
		return null;
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
	
	public void reduzirPopulacao(ArrayList<Solucao> populacaoFinal, FronteiraPareto paretoTemp){
		if(paretoTemp.fronteira.size()<tamanhoPopulacao){
			ArrayList<Solucao> temp = obterMelhoresAnticorpos(paretoTemp, 1.0);
			populacaoFinal.clear();
			populacaoFinal.addAll(temp);
		}
		else{
			ArrayList<Solucao> solucoesFinais = paretoTemp.fronteira;
			rankearSolucoes(solucoesFinais);
			ComparetorRank  comp = new ComparetorRank();
			Collections.sort(solucoesFinais, comp);
			populacaoFinal.clear();
			for(int i = 0; i<tamanhoPopulacao; i++)
				populacaoFinal.add(solucoesFinais.get(i));	
		}
	}
	
	public ArrayList<Solucao> obterMelhoresAnticorpos(FronteiraPareto paretoAtual, double tamanhoMelhores){
		ArrayList<Solucao> melhores = new ArrayList<Solucao>();
		melhores.addAll(paretoAtual.fronteira);
		int maxMelhores = (int)(tamanhoMelhores*tamanhoPopulacao);
		//Caso o n�mero das melhore solu��es seja menor q 5% da popula��o deve-se preencher os array das melhores solu��es
		if(melhores.size()< maxMelhores){
			ArrayList<Solucao> dominadas = new ArrayList<Solucao>();
			for (Iterator<Solucao> iterator = populacao.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				if(!melhores.contains(solucao)){
					dominadas.add(solucao);
				}
			}
			
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
		
		MISA misa = new MISA(n, prob, g, t, 0.25, false);
		
		ArrayList<Solucao> fronteira =  misa.executar();
		
		Avaliacao aval = new Avaliacao(fronteira, m);
		aval.avaliar();
	}
	

}
