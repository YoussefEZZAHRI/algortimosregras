package kernel.nuvemparticulas;


import java.util.ArrayList;
import java.util.Iterator;

import problema.Problema;
import pareto.FronteiraPareto;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import kernel.AlgoritmoAprendizado;


/**
 * Classe que implementa o algoritmo da Otimiza��o por nuvem de part�culas multi-objetivo.
 * @author Andr� B. de Carvalho
 *
 */
public abstract class MOPSO extends AlgoritmoAprendizado{

	//Arraylist que cont�m as particulas da execu��o do algoritmo
	public ArrayList<Particula> populacao = null;
	
	//Arraylist que representa o reposit�rio com as solu��es n�o dominadas
	//public ArrayList<Particula> repositorio = null;
	
	private String[] maxmim = null;
	
	
	Problema problema = null;
	
	
	public MOPSO(int n, Problema prob, int g, int a, int t, double s, String[] maxmim, String tRank){
		super(n,prob,g, a,t, tRank);
		populacao = new ArrayList<Particula>();
		//repositorio = new ArrayList<Particula>();
		pareto = new FronteiraPareto(s, maxmim,rank);
		if(rank)
			metodoRank.setPareto(pareto);
		this.maxmim = maxmim;
		problema = prob;
	}
	
	/**
	 * M�todo que limpa as listas do algoritmo e o prepara para uma nova execu��o.
	 *
	 */
	public void reiniciarExecucao(){
		populacao = new ArrayList<Particula>();
		pareto = new FronteiraPareto(pareto.S, maxmim, pareto.rank);
		problema.avaliacoes =0; 
	}
	
	/**
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public abstract ArrayList<Solucao> executar();
	
	public abstract void escolherLideres();
	
	
	/**
	 * M�todo que inicia a popula��o de part�culas. 
	 */
	public void inicializarPopulacao(){
		
		for(int i = 0; i<tamanhoPopulacao; i++){
			Particula particula = new Particula();
			//Contador utilizada para a cria��o da regra n�o ficar presa no la�o
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
			rankParticula(populacao);
	}
	
	/**
	 * M�todo que preenche o respositorio com as solu��o n�o dominadas
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
	
	public void liderOposto(){
		
		
	}
	
}
