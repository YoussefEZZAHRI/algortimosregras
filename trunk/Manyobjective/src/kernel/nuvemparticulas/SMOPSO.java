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
 * Classe que implementa o algoritmo da Otimiza��o por nuvem de part�culas multi-objetivo.
 * @author Andr� B. de Carvalho
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
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public ArrayList<Solucao> executar(){
		
		String arquivoSaida = "medidas.txt";
		
		try{
		PrintStream psMedidas = new PrintStream(arquivoSaida);
		
		//Apaga todas as listas antes do inicio da execu��o
		reiniciarExecucao();
		//Inicia a popul�ao
		inicializarPopulacao();
		//Obt�m as melhores part�culas da popula��o
		atualizarRepositorio();		
		//Obt�m os melhores globais para todas as part�culas da popula��o
		escolherLideres();
		
		escolherParticulasMutacao();
		//In�cia o la�o evolutivo
		for(int i = 0; i<geracoes; i++){
			if(i%10 == 0)
				System.out.print(i + " ");
			//Itera sobre todas as part�culas da popula��o
 			for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
				Particula particula = (Particula) iter.next();
				//Calcula a nova velocidade
				particula.calcularNovaVelocidadeConstriction();
				//Calcula a nova posi��o
				particula.calcularNovaPosicao();
				if(particula.mutacao){
					mutacaoPolinomial(PROB_MUT_COD,particula.posicao);
					particula.mutacao = false;
				}
				
				particula.truncar();
				//Avalia a part�cula
				problema.calcularObjetivos(particula.solucao);
				//Define o melhor local
				particula.escolherLocalBest();
			}
 			//Obt�m as melhores particulas da popula��o
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
	 * M�todo que escolhe para cada particula da populacao uma particula presente no repositorio
	 *
	 */
	public void escolherLideres(){
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			particula.escolherGlobalBestBinario(pareto.fronteiraNuvem);
		}
	}
	
	/**
	 * M�todo que define quais particulas da popula��o sofrer�o mutacao
	 * AS particulas que forem domindas por mais particulas ser�o escolhidas
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
