package kernel.nuvemparticulas;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import problema.Problema;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;


/**
 * Classe que implementa o algoritmo da Otimiza��o por nuvem de part�culas multi-objetivo.
 * @author Andr� B. de Carvalho
 *
 */
public class SMOPSO extends MOPSO{

	
	public final double INDICE_MUTACAO = 0.15;
	
	
	
	
	//PrintStream psSol;
	
		
	public SMOPSO(int n, Problema prob, int g, int a, int t, double s, String[] maxmim, int tamRep, String tRank, double ocupacao, double fator, double smax, String tPoda){
		super(n,prob,g,a,t,s, maxmim, tRank, ocupacao, fator, smax, tPoda);
		tamanhoRepositorio = tamRep;	
			
		/*try{
			psSol = new PrintStream("solucoes_" + pareto.S);
		}catch(IOException ex){ex.printStackTrace();}*/
	}
		
	/**
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public ArrayList<Solucao> executar(){
		
//		teste();
		//Apaga todas as listas antes do inicio da execucao
		reiniciarExecucao();
		
		//iniciarPopulacaoTeste();
		//rankParticula(populacao);
		
		//Inicia a populcaao
		inicializarPopulacao();
		
		//Obtem as melhores partaculas da populacao
					
		if(!rank)
			atualizarRepositorio();
		else
			iniciarRepositorioRank();	
	
		
		calcularCrowdingDistance(pareto.getFronteira());
		//Obtem os melhores globais para todas as particulas da populacao
		escolherLider.escolherLideres(populacao, pareto.getFronteira());
		
		
		escolherParticulasMutacao();
		//Inicia o laco evolutivo
		for(int i = 0; i<geracoes; i++){
			if(i%10 == 0)
				System.out.print(i + " ");
			if(i % 100 ==0)
				System.out.println();
			lacoEvolutivo(i);
		}
		
		//removerGranularRaio(pareto.getFronteira());
		calcularCrowdingDistance(pareto.getFronteira());
		
		efetuarPoda();
		
		return pareto.getFronteira();
		
	}
	
	public ArrayList<Solucao> executarAvaliacoes(){

		//Apaga todas as listas antes do inicio da execucao
		reiniciarExecucao();
		//Inicia a populcao
		inicializarPopulacao();
		//Obtem as melhores particulas da populacao
		if(!rank)
			atualizarRepositorio();
		else
			iniciarRepositorioRank();
		
		//calcularCrowdingDistance(pareto.fronteira);
		//Obtem os melhores globais para todas as particulas da populacao
		escolherLider.escolherLideres(populacao, pareto.getFronteira());

		escolherParticulasMutacao();
		//Inicia o laco evolutivo
		while(problema.avaliacoes < numeroavalicoes){
			if(problema.avaliacoes%5000 == 0)
				System.out.print(problema.avaliacoes + " ");
			lacoEvolutivo(problema.avaliacoes);
		}

		//removerGranularRaio(pareto.getFronteira());
		calcularCrowdingDistance(pareto.getFronteira());
		efetuarPoda();

		return pareto.getFronteira();
		
	}

	private void lacoEvolutivo(int i) {
		
		//Itera sobre todas as particulas da populacao
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = (Particula) iter.next();
			//Calcula a nova velocidade
			particula.calcularNovaVelocidadeConstriction();
			//Calcula a nova posicao
			particula.calcularNovaPosicao();
			if(particula.mutacao){
				mutacaoPolinomial(PROB_MUT_COD,particula.posicao);
				particula.mutacao = false;
			}
			
			particula.truncar();
			//Avalia a particula
			problema.calcularObjetivos(particula.solucao);
			//Define o melhor local
			particula.escolherLocalBest(pareto);
		}		
		
		if(rank)
			rankParticula(populacao);
		//Obtem as melhores particulas da populacao
		atualizarRepositorio();
		
		//System.out.println(pareto.getFronteira().size());
		//removerGranularLimites(pareto.getFronteira());
		// System.out.println(" -  " + pareto.getFronteira().size());
		
		
		calcularCrowdingDistance(pareto.getFronteira());
				
		//System.out.print (pareto.getFronteira().size()  + " - ");
		efetuarPoda();
		//System.out.println(pareto.getFronteira().size());

		//Recalcula a Crowding distance dos lideres
		if(pareto.getFronteira().size()==tamanhoRepositorio)
			calcularCrowdingDistance(pareto.getFronteira());
		
		//Escolhe os novos melhores globais
		escolherLider.escolherLideres(populacao, pareto.getFronteira());
		
		escolherParticulasMutacao();
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
	

	

	public void iniciarRepositorioRank(){
		ComparetorRankParticula comp = new ComparetorRankParticula();
		Collections.sort(populacao, comp);
		for(int i = 0; i<(tamanhoRepositorio); i++){
			Particula particula = populacao.get(i);
			pareto.getFronteira().add((Solucao)particula.solucao.clone());
		}
		
		
	}
	
	public void iniciarPopulacaoTeste(){
		int sl = 2;
		ArrayList<SolucaoNumerica> temp =  problema.obterSolucoesExtremas(n, sl);
		
		for (Iterator<SolucaoNumerica> iterator = temp.iterator(); iterator.hasNext();) {
			Particula particula = new Particula();
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			particula.iniciarParticulaAleatoriamente(problema, solucaoNumerica);
			problema.calcularObjetivos(solucaoNumerica);
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);
				
		}
		
		for(int i = 0; i<3; i++){
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
	
	public void iniciarPopulacaoTeste2(){
		
		
		ArrayList<SolucaoNumerica> solucoes =  problema.obterFronteira(tamanhoRepositorio, 250);
		ComparetorObjetivo comp = new ComparetorObjetivo(0);
		Collections.sort(solucoes, comp);
		
		
		
		for (Iterator<SolucaoNumerica> iterator = solucoes.iterator(); iterator.hasNext();) {
			Particula particula = new Particula();
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			particula.iniciarParticulaAleatoriamente(problema, solucaoNumerica);
			problema.calcularObjetivos(solucaoNumerica);
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);
				
		}
		
	}
	
	public void teste(){
		
		iniciarPopulacaoTeste2();
		
		
		
		definirSExtremos(populacao);
		System.out.println();
		
		
		int k = 0;
		for (Iterator<Particula> iterator = populacao.iterator(); iterator.hasNext(); k++) {
			Solucao solucao = iterator.next().solucao; 
			solucao.indice = k;
		}
		
		for (Iterator<Particula> iterator = populacao.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next().solucao;
			System.out.println("Solucao: " + solucao.indice);
			double dom  = pareto.add2(solucao);
			if(dom ==0)
				System.out.print("");
				
		}
		try{
			imprimirFronteira(pareto.getFronteira(), 0, "temp");
			} catch(IOException ex){ex.printStackTrace();}
	}
	

	
		
		

}
