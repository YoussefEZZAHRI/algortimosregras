package kernel.nuvemparticulas;


import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


import problema.DTLZ2;
import problema.Problema;
import solucao.Solucao;
import kernel.Avaliacao;

/**
 * Classe que implementa o algoritmo da Otimização por nuvem de partículas multi-objetivo.
 * @author André B. de Carvalho
 *
 */
public class SMOPSO extends MOPSO{

	
	public final double INDICE_MUTACAO = 0.15;
	
	public int tamanhoRepositorio;
	
	public boolean rank = true;
		
	public SMOPSO(int n, Problema prob, int g, int t, double s, boolean mod, boolean r){
		super(n,prob,g,t,s,mod);
		tamanhoRepositorio = tamanhoPopulacao;
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
		atualizarRepositorio();		
		//Obtém os melhores globais para todas as partículas da população
		escolherLideres();
		
		escolherParticulasMutacao();
		//Inícia o laço evolutivo
		for(int i = 0; i<geracoes; i++){
			if(i%10 == 0)
				System.out.print(i + " ");
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
				particula.escolherLocalBest();
			}
 			//Obtém as melhores particulas da população
			atualizarRepositorio();
			
			if(rank)
				rankearSolucoes(pareto.fronteira);
			
			calcularCrowdingDistance(pareto.fronteira);
			
			Avaliacao aval = new Avaliacao(pareto.fronteira, problema.m);
			double[] medidas = aval.avaliar();
			
			//if(i %10 == 0)
			//	psMedidas.println(i + "\t" + new Double(medidas[0]).toString().replace('.', ',') + "\t" + new Double(medidas[1]).toString().replace('.', ',') + "\t" + new Double(medidas[2]).toString().replace('.', ',') + "\t" + new Double(medidas[3]).toString().replace('.', ','));
			
			//pareto.podarLideresCrowd(tamanhoRepositorio);
			pareto.podarLideresCrowdOperatorParticula(tamanhoRepositorio);
			//Recalcula a Crowding distance dos lideres
			calcularCrowdingDistance(pareto.fronteira);
			
			//Escolhe os novos melhores globais
			escolherLideres();
			
			escolherParticulasMutacao();
		}
		
		
		pareto.retornarFronteiraNuvem();
		}catch(IOException ex){ex.printStackTrace();}
		return pareto.getFronteira();
		
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
	
	public static void main(String[] args) {
		int m = 3;
		Problema prob = new DTLZ2(m);
		int n = 10;
		int g = 250;
		int t = 100;
		for(int i = 0; i<5; i++){
			SMOPSO nuvem = new SMOPSO(n, prob, g, t, 0.25, false, true);
			ArrayList<Solucao> fronteira = nuvem.executar();
			for (Iterator<Solucao> iterator = nuvem.pareto.fronteira.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				prob.calcularObjetivos(solucao);
				System.out.println(solucao);
				
			}
			//System.out.println();
			Avaliacao aval = new Avaliacao(fronteira, m);
			aval.avaliar();	
		}
		
		
		
	}
	

}
