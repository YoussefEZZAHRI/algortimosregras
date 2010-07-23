package kernel.nuvemparticulas;


import java.util.ArrayList;
import java.util.Iterator;


import problema.DTLZ2;
import problema.Problema;
import solucao.Solucao;
import solucao.SolucaoNumerica;


/**
 * Classe que implementa o algoritmo da Otimização por nuvem de partículas multi-objetivo.
 * @author André B. de Carvalho
 *
 */
public class SigmaMOPSO extends MOPSO{


	
	public SigmaMOPSO(int n, Problema prob, int g, int a, int t, double s, String[] maxmim, String tRank){
		super(n,prob,g, a,t, s, maxmim,tRank);

	}
	

	/**
	 * Método principal que executa as operaçoes do MOPSO
	 */
	public ArrayList<Solucao> executar(){
		//Apaga todas as listas antes do inicio da execução
		reiniciarExecucao();
		//Inicia a populçao
		inicializarPopulacao();
		//Obtém as melhores partículas da população
		atualizarRepositorio();		
		//Obtém os melhores globais para todas as partículas da população
		escolherLideres();
		//Inícia o laço evolutivo
		for(int i = 0; i<geracoes; i++){
			if(i%10 == 0)
				System.out.print(i + " - " + geracoes + " ");
			lacoEvolutivo();
		}
		
		
		pareto.retornarFronteiraNuvem();
		return pareto.getFronteira();
	}
	
	/**
	 * Método principal que executa as operaçoes do MOPSO
	 */
	public ArrayList<Solucao> executarAvaliacoes(){
		//Apaga todas as listas antes do inicio da execução
		reiniciarExecucao();
		//Inicia a populçao
		inicializarPopulacao();
		//Obtém as melhores partículas da população
		atualizarRepositorio();		
		//Obtém os melhores globais para todas as partículas da população
		escolherLideres();
		while(problema.avaliacoes < numeroavalicoes){
			if(problema.avaliacoes%1000 == 0)
				System.out.print(problema.avaliacoes + " - " + numeroavalicoes + " ");
			lacoEvolutivo();
		}
		
	
		pareto.retornarFronteiraNuvem();
		return pareto.getFronteira();
	}


	private void lacoEvolutivo() {
		//Itera sobre todas as partículas da população
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = (Particula) iter.next();
			//Calcula a nova velocidade
			particula.calcularNovaVelocidade();
			//Calcula a nova posição
			particula.calcularNovaPosicao();
			//Turbulência na posição da partícula
			mutacao(0.05, particula.posicao);
			particula.truncar();
			//Avalia a partícula
			problema.calcularObjetivos(particula.solucao);
			//Define o melhor local
			particula.escolherLocalBest(pareto);
		}
		//Obtém as melhores particulas da população
		atualizarRepositorio();
		//Escolhe os novos melhores globais
		escolherLideres();
	}
	
	/**
	 * Método que escolhe para cada particula da populacao uma particula presente no repositorio
	 *
	 */
	public void escolherLideres(){
		for (Iterator<Particula> iter = pareto.fronteiraNuvem.iterator(); iter.hasNext();) {
			Particula partRepositorio =  iter.next();
			partRepositorio.calcularSigmaVector();
		}
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			particula.calcularSigmaVector();
			particula.escolherGlobalBestSigma(pareto.fronteiraNuvem);
		}
	}
	
	
	public static void main(String[] args) {
		int m = 3;
		Problema prob = new DTLZ2(m);
		int n = 5;
		int g = 50;
		int t = 100;
		int a = -1;
		String[] mm = {"-","-","-"};
		for(int i = 0; i<5; i++){
			SigmaMOPSO nuvem = new SigmaMOPSO(n, prob, g, a, t, 0.25, mm, "false");
			nuvem.executar();
			for (Iterator<Solucao> iterator = nuvem.pareto.fronteira.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
				prob.calcularObjetivos(solucao);
				//System.out.println(solucao);
				
			}
			//System.out.println();
			//Avaliacao aval = new Avaliacao(fronteira, m);
			//aval.avaliar();	
		}
		
		
		
	}
	

}
