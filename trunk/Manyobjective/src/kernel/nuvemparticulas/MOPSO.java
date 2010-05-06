package kernel.nuvemparticulas;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import problema.Problema;
import pareto.FronteiraPareto;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.AlgoritmoAprendizado;


/**
 * Classe que implementa o algoritmo da Otimização por nuvem de partículas multi-objetivo.
 * @author André B. de Carvalho
 *
 */
public abstract class MOPSO extends AlgoritmoAprendizado{

	//Arraylist que contém as particulas da execução do algoritmo
	public ArrayList<Particula> populacao = null;
	
	//Arraylist que representa o repositório com as soluções não dominadas
	//public ArrayList<Particula> repositorio = null;
	
	private String[] maxmim = null;
	
	
	Problema problema = null;
	
	
	public MOPSO(int n, Problema prob, int g, int a, int t, double s, String[] maxmim, boolean r){
		super(n,prob,g, a,t);
		populacao = new ArrayList<Particula>();
		//repositorio = new ArrayList<Particula>();
		pareto = new FronteiraPareto(s, maxmim,r);
		this.maxmim = maxmim;
		problema = prob;
	}
	
	/**
	 * Método que limpa as listas do algoritmo e o prepara para uma nova execução.
	 *
	 */
	public void reiniciarExecucao(){
		populacao = new ArrayList<Particula>();
		pareto = new FronteiraPareto(pareto.S, maxmim, pareto.rank);
		problema.avaliacoes =0; 
	}
	
	/**
	 * Método principal que executa as operaçoes do MOPSO
	 */
	public abstract ArrayList<Solucao> executar();
	
	public abstract void escolherLideres();
	
	
	/**
	 * Método que inicia a população de partículas. 
	 */
	public void inicializarPopulacao(){
		
		for(int i = 0; i<tamanhoPopulacao; i++){
			Particula particula = new Particula();
			//Contador utilizada para a criação da regra não ficar presa no laço
			int cont = 0;
			do{
				SolucaoNumerica s = new SolucaoNumerica(n, problema.m);
				s.iniciarSolucaoAleatoria();
				particula.iniciarParticulaAleatoriamente(problema, s);
				problema.calcularObjetivos(s);
				cont++;
			}while(populacao.contains(particula) && (cont<20));
			//Avaliando os objetivos da particula;
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);	
		}
		if(rank)
			averageRankParticula(populacao);
	}
	
	/**
	 * Método que preenche o respositorio com as solução não dominadas
	 *
	 */
	public void atualizarRepositorio(){
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula =  iter.next();
			if(!pareto.fronteiraNuvem.contains(particula)){
				if(!rank)
					particula.solucao.numDominacao = pareto.add((Particula)particula.clone());
				else
					pareto.addRank((Particula)particula.clone());
				
			}
		}	
		pareto.retornarFronteiraNuvem();
	}
	
}
