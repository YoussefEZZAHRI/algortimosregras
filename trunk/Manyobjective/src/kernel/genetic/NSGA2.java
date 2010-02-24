package kernel.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import pareto.FronteiraPareto;
import problema.Problema;

import solucao.ComparetorCrowdedOperator;
import solucao.ComparetorDominacao;
import solucao.ComparetorRank;
import solucao.SolucaoNumerica;
import sun.java2d.pipe.SolidTextRenderer;
import kernel.AlgoritmoAprendizado;
import kernel.Avaliacao;
import kernel.misa.AdaptiveGrid;
import kernel.nuvemparticulas.Particula;

public class NSGA2 extends AlgoritmoAprendizado {
	
	public ArrayList<SolucaoNumerica> populacao;
	public ArrayList<SolucaoNumerica> offspring;
	
	private ComparetorRank compRank = new ComparetorRank();
	private ComparetorCrowdedOperator compCrwd = new ComparetorCrowdedOperator();
	
	
	
	public NSGA2(int n, Problema prob, int g, int a, int t, double s){
		super(n,prob,g, a,t);
		
		pareto = new FronteiraPareto(s);
		problema = prob;

	}

	@Override
	public ArrayList<SolucaoNumerica> executar() {
		
		populacao = new ArrayList<SolucaoNumerica>();
		offspring = new ArrayList<SolucaoNumerica>();
		
		iniciarPopulacao();
		atribuirRanking(populacao);
		//fastNonDominatedSort(populacao);
		
		gerarOffsping(populacao, compRank);
		ArrayList<SolucaoNumerica> populacaoCombinada = new ArrayList<SolucaoNumerica>();
		
		for(int g = 0; g<geracoes; g++){
			
			lacoEvolutivo(populacaoCombinada);
		}
		
		return populacao;
	}
	
	
	public ArrayList<SolucaoNumerica> executarAvaliacoes() {
		
		populacao = new ArrayList<SolucaoNumerica>();
		offspring = new ArrayList<SolucaoNumerica>();
		
		iniciarPopulacao();
		atribuirRanking(populacao);
		
		problema.avaliacoes = 0;
		//fastNonDominatedSort(populacao);
		
		gerarOffsping(populacao, compRank);
		ArrayList<SolucaoNumerica> populacaoCombinada = new ArrayList<SolucaoNumerica>();
		
		while(problema.avaliacoes < numeroavalicoes){
			if(problema.avaliacoes%1000 == 0)
				System.out.print(problema.avaliacoes + " - " + numeroavalicoes + " ");
			lacoEvolutivo(populacaoCombinada);
		}
		
		return populacao;
	}

	private void lacoEvolutivo(ArrayList<SolucaoNumerica> populacaoCombinada) {
		populacaoCombinada.addAll(populacao);
		populacaoCombinada.addAll(offspring);
		atribuirRanking(populacaoCombinada);
		//fastNonDominatedSort(populacaoCombinada);
		calcularCrowdingDistance(populacaoCombinada);
		Collections.sort(populacaoCombinada, compCrwd);
		populacao.clear();
		for(int i = 0; i<tamanhoPopulacao; i++){
			SolucaoNumerica solucao = populacaoCombinada.get(i);
			solucao.truncar();
			populacao.add(solucao);
		}
		calcularCrowdingDistance(populacao);
		gerarOffsping(populacao, compCrwd);
		populacaoCombinada.clear();
	}
	
	public void gerarOffsping(ArrayList<SolucaoNumerica> solucoes, Comparator<SolucaoNumerica> comp){
		offspring.clear();
		int fator = (int)Math.ceil(Math.log10(solucoes.size()));
		for(int i = 0; i<tamanhoPopulacao; i++){
			SolucaoNumerica pai1 = escolherPaiBinaryTournament(solucoes, comp); 
			SolucaoNumerica pai2 = escolherPaiBinaryTournament(solucoes, comp);
			SolucaoNumerica filho = recombinacao(pai1, pai2);
			mutacaoPolinomial(PROB_MUT_COD, filho);
			filho.truncar();
			problema.calcularObjetivos(filho);
			offspring.add(filho);
		}
	}
	
	public void iniciarPopulacao(){
		populacao = new ArrayList<SolucaoNumerica>();
		for(int i = 0; i<tamanhoPopulacao; i++){
			SolucaoNumerica s = new SolucaoNumerica(n, problema.m);
			s.iniciarSolucaoAleatoria();
			problema.calcularObjetivos(s);
			populacao.add(s);
		}
	}
	

	public void atribuirRanking(ArrayList<SolucaoNumerica> solucoes){
		
		ArrayList<SolucaoNumerica> atual = new ArrayList<SolucaoNumerica>();
		ArrayList<SolucaoNumerica> proxima = new ArrayList<SolucaoNumerica>();
		
		atual.addAll(solucoes);
		
		int rank = 0;
		
		while(atual.size()>0){
			for (Iterator<SolucaoNumerica> iter = atual.iterator(); iter.hasNext();) {
				SolucaoNumerica solucao = iter.next();
				solucao.numDominacao = pareto.obterNumDomincao(solucao, atual);
				if(solucao.numDominacao == 0){
					solucao.rank = rank;
				} else
					proxima.add(solucao);
			}
			atual.clear();
			atual.addAll(proxima);
			proxima.clear();
			rank++;
		}
	}
	
	public void fastNonDominatedSort(ArrayList<SolucaoNumerica> solucoes){
		ArrayList<SolucaoNumerica> dominadas = new ArrayList<SolucaoNumerica>();
		ArrayList<SolucaoNumerica> naoDominadas = new ArrayList<SolucaoNumerica>();
		int rank = 0;
		for (Iterator iterator = solucoes.iterator(); iterator.hasNext();) {
			SolucaoNumerica p = (SolucaoNumerica) iterator.next();
			p.numDominacao = 0;
			for (Iterator iterator2 = solucoes.iterator(); iterator2.hasNext();) {
				SolucaoNumerica q = (SolucaoNumerica) iterator2.next();
				int comp = pareto.compararMedidas(p.objetivos, q.objetivos);
				if(comp == 1)
					dominadas.add(q);
				else
					if(comp == -1)
						p.numDominacao++;
			}
			if(p.numDominacao == 0){
				p.rank = rank;
				naoDominadas.add(p);
			}
		}
		
		
		while(naoDominadas.size()>0){
			ArrayList<SolucaoNumerica> H = new ArrayList<SolucaoNumerica>();
			for (Iterator iterator = naoDominadas.iterator(); iterator.hasNext();) {
				rank++;
				SolucaoNumerica p = (SolucaoNumerica) iterator.next();
				for (Iterator iterator2 = dominadas.iterator(); iterator2.hasNext();) {
					SolucaoNumerica q = (SolucaoNumerica) iterator2.next();
					q.numDominacao--;
					if(q.numDominacao == 0){
						H.add(q);
						q.rank = rank;
					}
				}
			}
			naoDominadas.clear();
			naoDominadas.addAll(H);
		}
	}
	
	public SolucaoNumerica recombinacao(SolucaoNumerica solucao1, SolucaoNumerica solucao2){
		int fator = (int)Math.ceil(Math.log10(n));
		int pontoJuncao = (int)((Math.random()*fator) % n);
		SolucaoNumerica novaSolucao = (SolucaoNumerica)solucao1.clone();
		for (int i = pontoJuncao; i < solucao2.n; i++) {
			novaSolucao.setVariavel(i,  solucao2.getVariavel(i));
		}
		return novaSolucao;
	}
	
	public SolucaoNumerica escolherPaiBinaryTournament(ArrayList<SolucaoNumerica> solucoes, Comparator<SolucaoNumerica> comp){
		int ordem = (int)Math.ceil(Math.log10(solucoes.size()));
		int indice1 = (int)(Math.random()*(Math.pow(10, ordem))%solucoes.size());
		int indice2 = (int)(Math.random()*(Math.pow(10, ordem))%solucoes.size());
		SolucaoNumerica solucao1 = solucoes.get(indice1);
		SolucaoNumerica solucao2 = solucoes.get(indice2);
		int result  = comp.compare(solucao1, solucao2);
		if(result == -1)
			return solucao1;
		if(result == 1)
			return solucao2;
		
		//Caso as solucoes nao se dominem a escolha eh aleatoria
		int indice = (int)(Math.random()*10%2);
		if(indice ==0)
			return solucao1;
		else
			return solucao2;
	}
	


}
