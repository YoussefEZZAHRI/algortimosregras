package problema;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import pareto.FronteiraPareto;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa o problema DTLZ2
 * @author andre
 *
 */

public class DTLZ7 extends Problema {
	
		/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ7(int m){
		super(m);
		
		this.r = false;
		
		
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g7 = g7(solucao.xm);
		for(int i = 0; i<m-1; i++){
			solucao.objetivos[i] = solucao.getVariavel(i);
		}
		
		double h = h(solucao.objetivos, g7);
		
		solucao.objetivos[m-1] = (1+g7)*h;
		
		
	   
		
		avaliacoes++;
		return solucao.objetivos;
	}
	
	public double[] calcularObjetivos2(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		
		for(int i = 0; i<m; i++)
			solucao.objetivos[i] = 0;
		
		
		int numberOfVariables_ = solucao.getVariaveis().length;
		int numberOfObjectives_ = m;

	    double [] x = new double[numberOfVariables_];
	    double [] f = new double[numberOfObjectives_];
	    int k = numberOfVariables_ - numberOfObjectives_ + 1;
	            
	    for (int i = 0; i < numberOfVariables_; i++)
	      x[i] = solucao.getVariavel(i);
	        
	    //Calculate g
	    double g = 0.0;
	    for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
	      g += x[i] ;
	        
	    g = 1 + (9.0 * g) / k;
	    //<-
	                
	    //Calculate the value of f1,f2,f3,...,fM-1 (take acount of vectors start at 0)
	    for (int i = 0; i < numberOfObjectives_-1; i++)
	      f[i] = x[i];
	    //<-
	        
	    //->Calculate fM
	    double h = 0.0;
	    for (int i = 0; i < numberOfObjectives_ -1; i++)
	      h += (f[i]/(1.0 + g))*(1 + Math.sin(3.0 * Math.PI * f[i]));
	       
	    h = numberOfObjectives_ - h;
	        
	    f[numberOfObjectives_-1] = (1 + g) * h;
	    //<-
	        
	    //-> Setting up the value of the objetives
	    for (int i = 0; i < numberOfObjectives_; i++)
	      solucao.objetivos[i] = f[i];
	    
	    return solucao.objetivos;
		
	}
	
	public double h(double[] objetivos, double g){
		double soma = 0;
		for(int i = 0; i<m-1; i++){
			double fi = objetivos[i];
			double sen = Math.sin(3*Math.PI*fi);
			double parte1 = fi/(1+g);
			double parte2 = 1+sen;
			soma+=parte1*parte2;
		}
		
		return m - soma;
	}
	
	public  ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		FronteiraPareto pareto = new FronteiraPareto(s, maxmim, r);
		                         
				
		while(melhores.size()<numSol){
			SolucaoNumerica melhor = new SolucaoNumerica(n, m);

			for (int i = m-1; i <n; i++) {
				melhor.setVariavel(i, 0);
			}

			for (int i = 0; i < m-1; i++) {
				double newVal = rand.nextDouble();
				melhor.setVariavel(i, newVal);
			}
			
			calcularObjetivos(melhor);
			
			if(!pareto.fronteira.contains(melhor))
				pareto.add(melhor);
			
			melhores.add(melhor);
			
			
		}
		
		ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		for (Iterator<Solucao> iterator = pareto.fronteira.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			saida.add(solucaoNumerica);
		}
		
			
		return saida;	
	}
	
	public  ArrayList<SolucaoNumerica> obterFronteiraIncremental(int n, int numSol){
				
		//Indicies que indicam que variaves serão geradas incrementalmente para a geracao da fronteira
		//O padrao dos problemas DTLZ eh entre 0 e m-2
		int inicio = 0;
		int fim = m-2;
		
		FronteiraPareto pareto = new FronteiraPareto(s, maxmim, r);
		
		SolucaoNumerica solucaoBase = new SolucaoNumerica(n, m);
		
		varVez = fim;
		
		for (int i = 0; i < solucaoBase.getVariaveis().length; i++) {
			solucaoBase.setVariavel(i, 0);
		}
		
		boolean haSolucao = true;
		
		while(haSolucao){
			
			SolucaoNumerica melhor = (SolucaoNumerica) solucaoBase.clone();
			
			for (int i = m-1; i <n; i++) {
				melhor.setVariavel(i, 0);
			}
					
			calcularObjetivos(melhor);
							
			if(!pareto.fronteira.contains(melhor))
				pareto.add(melhor);
					
			haSolucao = getProximaSolucao(solucaoBase, inicio, fim);
							
		}

		ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		for (Iterator<Solucao> iterator = pareto.fronteira.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			saida.add(solucaoNumerica);
		}
		
		return saida;	
	}
		
	
	public static void main(String[] args) {
		
		int m = 2;
		int numSol = 500;
		int k = 10;
		int n = m + k - 1;
		
		int decimalPlace = 5;
		DTLZ7 dtlz7 = new DTLZ7(m);
		
		dtlz7.inc = 0.001;
		
		//dtlz7.obterFronteira2(n, numSol);
		
		
		ArrayList<SolucaoNumerica> f = dtlz7.obterFronteiraIncremental(n, numSol);
		ComparetorObjetivo comp = new ComparetorObjetivo(0);
		Collections.sort(f,comp);
		
		try{
			PrintStream ps = new PrintStream("fronteira_dtlz7" + m);
			PrintStream psSol = new PrintStream("solucoes_dtlz7" + m);
			for (Iterator<SolucaoNumerica> iterator = f.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
						.next();
			
				
				for(int i = 0; i<m; i++){
					BigDecimal bd = new BigDecimal(solucaoNumerica.objetivos[i]);     
					bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
					ps.print( bd+ " ");
				}
				ps.println();
				
				for(int i = 0; i<solucaoNumerica.getVariaveis().length; i++){
					psSol.print(solucaoNumerica.getVariavel(i) + " ");
				}
				
				psSol.println();
				
			}
		} catch (IOException ex){ex.printStackTrace();}
		
	}

}
