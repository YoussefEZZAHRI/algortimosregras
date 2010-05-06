package kernel.nuvemparticulas;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


import problema.DTLZ2;
import problema.Problema;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.Avaliacao;

/**
 * Classe que implementa o algoritmo da Otimização por nuvem de partículas multi-objetivo.
 * @author André B. de Carvalho
 *
 */
public class SMOPSO extends MOPSO{

	
	public final double INDICE_MUTACAO = 0.15;
	
	public int tamanhoRepositorio;
	
	
	
		
	public SMOPSO(int n, Problema prob, int g, int a, int t, double s, String[] maxmim, boolean r, int tr){
		super(n,prob,g,a,t,s, maxmim, r);
		tamanhoRepositorio = tr;	
		
		rank = r;
	}
		
	/**
	 * Método principal que executa as operaçoes do MOPSO
	 */
	public ArrayList<Solucao> executar(){
		
		String arquivoSaida = "medidas.txt";
		
		try{
		PrintStream psMedidas = new PrintStream(arquivoSaida);
		
		//Apaga todas as listas antes do inicio da execução
		reiniciarExecucao();
		//Inicia a populçao
		inicializarPopulacao();
		//Obtém as melhores partículas da população
		if(!rank)
			atualizarRepositorio();
		else
			iniciarRepositorioRank();	
		//Obtém os melhores globais para todas as partículas da população
		escolherLideres();
		
		escolherParticulasMutacao();
		//Inícia o laço evolutivo
		for(int i = 0; i<geracoes; i++){
			if(i%10 == 0)
				System.out.print(i + " ");
			lacoEvolutivo();
		}
		
		
		pareto.retornarFronteiraNuvem();
		}catch(IOException ex){ex.printStackTrace();}
		return pareto.getFronteira();
		
	}
	
	public ArrayList<Solucao> executarAvaliacoes(){
		
		String arquivoSaida = "medidas.txt";
	
		
		try{
		PrintStream psMedidas = new PrintStream(arquivoSaida);
		
		//Apaga todas as listas antes do inicio da execução
		reiniciarExecucao();
		//Inicia a populçao
		inicializarPopulacao();
		//Obtém as melhores partículas da população
		if(!rank)
			atualizarRepositorio();
		else
			iniciarRepositorioRank();
		//Obtém os melhores globais para todas as partículas da população
		escolherLideres();
		
		escolherParticulasMutacao();
		//Inícia o laço evolutivo
		while(problema.avaliacoes < numeroavalicoes){
			if(problema.avaliacoes%1000 == 0)
				System.out.print(problema.avaliacoes + " - " + numeroavalicoes + " ");
			lacoEvolutivo();
		}
		
		
		pareto.retornarFronteiraNuvem();
		}catch(IOException ex){ex.printStackTrace();}
		return pareto.getFronteira();
		
	}

	private void lacoEvolutivo() {
		//Itera sobre todas as partículas da população
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = (Particula) iter.next();
			//Calcula a nova velocidade
			particula.calcularNovaVelocidadeConstriction();
			//Calcula a nova posição
			particula.calcularNovaPosicao();
			if(particula.mutacao){
				mutacaoPolinomial(PROB_MUT_COD,particula.posicao);
				particula.mutacao = false;
			}
			
			particula.truncar();
			//Avalia a partícula
			problema.calcularObjetivos(particula.solucao);
			//Define o melhor local
			particula.escolherLocalBest(pareto);
		}
		if(rank)
			averageRankParticula(populacao);
		//Obtém as melhores particulas da população
		atualizarRepositorio();
		
					
		calcularCrowdingDistance(pareto.fronteira);
		
		pareto.podarLideresCrowdOperatorParticula(tamanhoRepositorio);
		
		//Recalcula a Crowding distance dos lideres
		calcularCrowdingDistance(pareto.fronteira);
		
		//Escolhe os novos melhores globais
		escolherLideres();
		
		escolherParticulasMutacao();
	}
	
	/**
	 * Método que escolhe para cada particula da populacao uma particula presente no repositorio
	 *
	 */
	public void escolherLideres(){
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			particula.escolherGlobalBestBinario(pareto.fronteiraNuvem);
		}
	}
	
	/**
	 * Método que define quais particulas da população sofrerão mutacao
	 * AS particulas que forem domindas por mais particulas serão escolhidas
	 */
	public void escolherParticulasMutacao(){
		ComparetorDominacaoParticula comp = new ComparetorDominacaoParticula();
		Collections.sort(populacao, comp);
		int numMutacao = (int)(populacao.size()*INDICE_MUTACAO);
		for(int i = 1; i<=numMutacao;i++){
			Particula part = (Particula)populacao.get(populacao.size()-i);
			part.mutacao = true;
		}
	}
	
	public void iniciarRepositorioRank(){
		ComparetorRankParticula comp = new ComparetorRankParticula();
		Collections.sort(populacao, comp);
		for(int i = 0; i<tamanhoRepositorio; i++){
			Particula particula = populacao.get(i);
			pareto.fronteiraNuvem.add((Particula)particula.clone());
		}
		
		pareto.retornarFronteiraNuvem();
		
	}
	
	public static void main(String[] args) {
		int m = 3;
		Problema prob = new DTLZ2(m);
		int n = 10;
		int g = 250;
		int t = 100;
		int a = -1;
		
		String[] mm = {"-","-","-"};
		for(int i = 0; i<5; i++){
			SMOPSO nuvem = new SMOPSO(n, prob, g, a, t, 0.25,  mm, false, t);
			ArrayList<Solucao> fronteira = nuvem.executar();
			for (Iterator<Solucao> iterator = nuvem.pareto.fronteira.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
				prob.calcularObjetivos(solucao);
				System.out.println(solucao);
				
			}
			//System.out.println();
			//Avaliacao aval = new Avaliacao(fronteira, m);
			//aval.avaliar();	
		}
		
		
		
	}
	

}
