package kernel.nuvemparticulas;


import java.util.ArrayList;
import java.util.Iterator;


import problema.DTLZ2;
import problema.Problema;
import solucao.Solucao;
import solucao.SolucaoNumerica;


/**
 * Classe que implementa o algoritmo da Otimiza��o por nuvem de part�culas multi-objetivo.
 * @author Andr� B. de Carvalho
 *
 */
public class SigmaMOPSO extends MOPSO{


	
	public SigmaMOPSO(int n, Problema prob, int g, int a, int t, double s, String[] maxmim, String tRank, double ocupacao, double fator, double smax){
		super(n,prob,g, a,t, s, maxmim,tRank, ocupacao, fator, smax);

	}
	

	/**
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public ArrayList<Solucao> executar(){
		//Apaga todas as listas antes do inicio da execu��o
		reiniciarExecucao();
		//Inicia a popul�ao
		inicializarPopulacao();
		//Obt�m as melhores part�culas da popula��o
		atualizarRepositorio();		
		//Obt�m os melhores globais para todas as part�culas da popula��o
		escolherLideres();
		//In�cia o la�o evolutivo
		for(int i = 0; i<geracoes; i++){
			if(i%10 == 0)
				System.out.print(i + " - " + geracoes + " ");
			lacoEvolutivo();
		}
		
		return pareto.getFronteira();
	}
	
	/**
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public ArrayList<Solucao> executarAvaliacoes(){
		//Apaga todas as listas antes do inicio da execu��o
		reiniciarExecucao();
		//Inicia a popul�ao
		inicializarPopulacao();
		//Obt�m as melhores part�culas da popula��o
		atualizarRepositorio();		
		//Obt�m os melhores globais para todas as part�culas da popula��o
		escolherLideres();
		while(problema.avaliacoes < numeroavalicoes){
			if(problema.avaliacoes%10000 == 0)
				System.out.print(problema.avaliacoes + " ");
			if(problema.avaliacoes%100000 == 0)
				System.out.println();
			lacoEvolutivo();
		}
		
		return pareto.getFronteira();
	}


	private void lacoEvolutivo() {
		//Itera sobre todas as part�culas da popula��o
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = (Particula) iter.next();
			//Calcula a nova velocidade
			particula.calcularNovaVelocidade();
			//Calcula a nova posi��o
			particula.calcularNovaPosicao();
			//Turbul�ncia na posi��o da part�cula
			mutacao(0.05, particula.posicao);
			particula.truncar();
			//Avalia a part�cula
			problema.calcularObjetivos(particula.solucao);
			//Define o melhor local
			particula.escolherLocalBest(pareto);
		}
		//Obt�m as melhores particulas da popula��o
		atualizarRepositorio();
		//Escolhe os novos melhores globais
		escolherLideres();
	}
	
	/**
	 * M�todo que escolhe para cada particula da populacao uma particula presente no repositorio
	 *
	 */
	public void escolherLideres(){
		for (Iterator<Solucao> iter = pareto.getFronteira().iterator(); iter.hasNext();) {
			Solucao partRepositorio =  iter.next();
			partRepositorio.calcularSigmaVector();
		}
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			particula.solucao.calcularSigmaVector();
			particula.escolherGlobalBestSigma(pareto.getFronteira());
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
			SigmaMOPSO nuvem = new SigmaMOPSO(n, prob, g, a, t, 0.25, mm, "false",0, 0, 0.25);
			nuvem.executar();
			for (Iterator<Solucao> iterator = nuvem.pareto.getFronteira().iterator(); iterator.hasNext();) {
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
