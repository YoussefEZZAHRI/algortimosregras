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
import solucao.SolucaoBinaria;
import solucao.SolucaoNumerica;
import solucao.Solucao;

import kernel.AlgoritmoAprendizado;
import kernel.Avaliacao;
import kernel.misa.AdaptiveGrid;
import kernel.nuvemparticulas.Particula;

public class NSGA2 extends AlgoritmoAprendizado {
	
	public ArrayList<Solucao> populacao;
	public ArrayList<Solucao> offspring;
	
	private ComparetorRank compRank = new ComparetorRank();
	private ComparetorCrowdedOperator compCrwd = new ComparetorCrowdedOperator();
	
	public String tipoSolucao = null;
	
	
	
	public NSGA2(int n, Problema prob, int g, int a, int t, double s, String ts){
		super(n,prob,g, a,t);
		
		pareto = new FronteiraPareto(s);
		problema = prob;
		
		tipoSolucao = ts;

	}

	@Override
	public ArrayList<Solucao> executar() {
		
		populacao = new ArrayList<Solucao>();
		offspring = new ArrayList<Solucao>();
		
		iniciarPopulacao();
		atribuirRanking(populacao);
		//fastNonDominatedSort(populacao);
		
		gerarOffsping(populacao, compRank);
		ArrayList<Solucao> populacaoCombinada = new ArrayList<Solucao>();
		
		for(int g = 0; g<geracoes; g++){
			if(g%10 == 0)
				System.out.print(g + " ");
			lacoEvolutivo(populacaoCombinada);
		}
		
		return populacao;
	}
	
	
	public ArrayList<Solucao> executarAvaliacoes() {
		
		populacao = new ArrayList<Solucao>();
		offspring = new ArrayList<Solucao>();
		
		iniciarPopulacao();
		atribuirRanking(populacao);
		
		problema.avaliacoes = 0;
		//fastNonDominatedSort(populacao);
		
		gerarOffsping(populacao, compRank);
		ArrayList<Solucao> populacaoCombinada = new ArrayList<Solucao>();
		
		while(problema.avaliacoes < numeroavalicoes){
			if(problema.avaliacoes%1000 == 0)
				System.out.print(problema.avaliacoes + " - " + numeroavalicoes + " ");
			lacoEvolutivo(populacaoCombinada);
		}
		
		return populacao;
	}

	private void lacoEvolutivo(ArrayList<Solucao> populacaoCombinada) {
		populacaoCombinada.addAll(populacao);
		populacaoCombinada.addAll(offspring);
		atribuirRanking(populacaoCombinada);
		//fastNonDominatedSort(populacaoCombinada);
		calcularCrowdingDistance(populacaoCombinada);
		Collections.sort(populacaoCombinada, compCrwd);
		populacao.clear();
		for(int i = 0; i<tamanhoPopulacao; i++){
			Solucao solucao = populacaoCombinada.get(i);
			populacao.add(solucao);
		}
		calcularCrowdingDistance(populacao);
		gerarOffsping(populacao, compCrwd);
		populacaoCombinada.clear();
	}
	
	public void gerarOffsping(ArrayList<Solucao> solucoes, Comparator<Solucao> comp){
		offspring.clear();
		for(int i = 0; i<tamanhoPopulacao; i++){
			Solucao pai1 = escolherPaiBinaryTournament(solucoes, comp); 
			Solucao pai2 = escolherPaiBinaryTournament(solucoes, comp);
			Solucao filho = recombinacao(pai1, pai2);
			mutacao(PROB_MUT_COD, filho);
			if(filho.isNumerica())
				((SolucaoNumerica)filho).truncar();
			problema.calcularObjetivos(filho);
			offspring.add(filho);
		}
	}
	
	public void iniciarPopulacao(){
		populacao = new ArrayList<Solucao>();
		for(int i = 0; i<tamanhoPopulacao; i++){
			Solucao s = null;
			if(tipoSolucao.equals("numerica"))
				s = new SolucaoNumerica(n, problema.m);
			else
				s = new SolucaoBinaria(n, problema.m);
			s.iniciarSolucaoAleatoria();
			problema.calcularObjetivos(s);
			populacao.add(s);
		}
	}
	

	public void atribuirRanking(ArrayList<Solucao> solucoes){
		
		ArrayList<Solucao> atual = new ArrayList<Solucao>();
		ArrayList<Solucao> proxima = new ArrayList<Solucao>();
		
		atual.addAll(solucoes);
		
		int rank = 0;
		
		while(atual.size()>0){
			for (Iterator<Solucao> iter = atual.iterator(); iter.hasNext();) {
				Solucao solucao = iter.next();
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
	
	public void fastNonDominatedSort(ArrayList<Solucao> solucoes){
		ArrayList<Solucao> dominadas = new ArrayList<Solucao>();
		ArrayList<Solucao> naoDominadas = new ArrayList<Solucao>();
		int rank = 0;
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao p =  iterator.next();
			p.numDominacao = 0;
			for (Iterator<Solucao> iterator2 = solucoes.iterator(); iterator2.hasNext();) {
				Solucao q =  iterator2.next();
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
			ArrayList<Solucao> H = new ArrayList<Solucao>();
			for (Iterator<Solucao> iterator = naoDominadas.iterator(); iterator.hasNext();) {
				rank++;
				Solucao p =  iterator.next();
				for (Iterator<Solucao> iterator2 = dominadas.iterator(); iterator2.hasNext();) {
					Solucao q =  iterator2.next();
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
	
	public Solucao recombinacao(Solucao solucao1, Solucao solucao2){
		Solucao novaSolucao = null;
		if(solucao1.isNumerica())
			novaSolucao = recombinacaoNumerica((SolucaoNumerica)solucao1, (SolucaoNumerica)solucao2);
		else
			novaSolucao = recombinacaoBinaria((SolucaoBinaria) solucao1, (SolucaoBinaria) solucao2);
		return novaSolucao;
	}
	
	public SolucaoBinaria recombinacaoBinaria(SolucaoBinaria solucao1, SolucaoBinaria solucao2){
		int fator = (int)Math.ceil(Math.log10(n));
		int pontoJuncao = (int)((Math.random()*fator) % n);
		SolucaoBinaria novaSolucao = (SolucaoBinaria)solucao1.clone();
		
		
		for (int i = pontoJuncao; i < solucao2.n; i++) {
			novaSolucao.setVariavel(i,  solucao2.getVariavel(i));
		}
		
		return novaSolucao;
	}

	
	public SolucaoNumerica recombinacaoNumerica(SolucaoNumerica solucao1, SolucaoNumerica solucao2){
		int fator = (int)Math.ceil(Math.log10(n));
		int pontoJuncao = (int)((Math.random()*fator) % n);
		SolucaoNumerica novaSolucao = (SolucaoNumerica)solucao1.clone();
		for (int i = pontoJuncao; i < solucao2.n; i++) {
			novaSolucao.setVariavel(i,  solucao2.getVariavel(i));
		}
		return novaSolucao;
	}
	
	public Solucao escolherPaiBinaryTournament(ArrayList<Solucao> solucoes, Comparator<Solucao> comp){
		int ordem = (int)Math.ceil(Math.log10(solucoes.size()));
		int indice1 = (int)(Math.random()*(Math.pow(10, ordem))%solucoes.size());
		int indice2 = (int)(Math.random()*(Math.pow(10, ordem))%solucoes.size());
		Solucao solucao1 = solucoes.get(indice1);
		Solucao solucao2 = solucoes.get(indice2);
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
