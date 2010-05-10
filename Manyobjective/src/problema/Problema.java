package problema;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import solucao.ComparetorObjetivo;
import solucao.ComparetorRank;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public abstract class Problema {
	
	public int m;
	public int avaliacoes;
	
	public String problema;
	
	private static double[] joelho = null;
	
	private static double[] lambda = null;
	
	public Problema(int m){
		this.m = m;
		avaliacoes = 0;
	}
	
	public abstract double[] calcularObjetivos(Solucao solucao);
	
	public abstract ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol);
	
	/**
	 * Equacao 8 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double g1(double[] xm){
		
		double soma = 0;
		for (int i = 0; i < xm.length; i++) {
			double xi = xm[i];
			
			
			double temp1 = Math.pow((xi-0.5),2);
			double temp2 = Math.cos(20*Math.PI*(xi-0.5));
			double temp3 = temp1 - temp2;
			soma+=temp3;
		}
		
		int moduloXm = xm.length;
		return 100*(moduloXm + soma);
	}
	
	/*public double g12(Solucao sol){
		double g = 0;
	    for (int i = sol.n - sol.k + 1; i <= sol.n; i++)
	    {
		g += Math.pow(sol.variaveis[i-1]-0.5,2) - Math.cos(20 * Math.PI * (sol.variaveis[i-1]-0.5));
	    }
	    g = 100 * (sol.k + g);
	    
	    return g;

	}*/
	
	
	/**
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double g2(double[] xm){
		double soma = 0;
		for(int i = 0; i<xm.length; i++){
			soma += (xm[i] - 0.5)*(xm[i] - 0.5);
		}
		return soma;
	}
	
	/**
	 * 
	 * Equacao 6.24 do artigo "Scalable Test Problems for Evolutionary Multiobjective Optimization"
	 */
	public double g5(double[] xm){
		double soma = 0;
		for (int i = 0; i < xm.length; i++) {
			soma+= Math.pow(xm[i], 0.1);
			
		}
		
		return soma;
	}
	
	public void imprimirVetoresScilab(ArrayList<SolucaoNumerica> melhores){
		StringBuffer comandoX = new StringBuffer();
		StringBuffer comandoY = new StringBuffer();
		StringBuffer comandoZ = new StringBuffer();
		
		comandoX.append("x = [\n");
		comandoY.append("y = [\n");
		comandoZ.append("z = [\n");
		for (Iterator<SolucaoNumerica> iterator = melhores.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			
			comandoX.append(solucao.objetivos[0]+  "\n");
			comandoY.append(solucao.objetivos[1]+  "\n");
			comandoZ.append(solucao.objetivos[2]+  "\n");
			
			
		}
		
		comandoX.append("];\n");
		comandoY.append("];\n");
		comandoZ.append("];\n");
		
		System.out.println(comandoX);
		System.out.println();
		System.out.println(comandoY);
		System.out.println();
		System.out.println(comandoZ);
		
		
	}
	
	
	public void imprimirFronteirar(int n, int m, int numSol) throws IOException{
		
		ArrayList<SolucaoNumerica> fronteira =  obterFronteira(n, numSol);
		
		String arqFronteira = problema +"_" + m +"_fronteira.txt";
		
		PrintStream psFronteira = new PrintStream(arqFronteira);
		
		for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			for(int i = 0; i<m; i++){
				psFronteira.print(new Double( solucao.objetivos[i]).toString().replace('.', ',')+ " ");
			}
			psFronteira.println();
		}
	}
	
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	/**
	 * Método que obtem o joelho da uma fronteira de Pareto real
	 * Inicia também um array de double lambda contendo o intervalo de valores para cada objetivo das solucoes na fronteira de pareto
	 * O método busca o ponto médio para cada dimensao do espaco de objetivos. O joelho é o ponto mais próximo deste ponto médio. 
	 * @param n
	 * @return
	 */
	public double[] getJoelho(int n){
		
		//O calculo do joelho e lambda só é executaod uma vez
		if(joelho ==null){
			
			
			joelho = new double[m];
			lambda = new double[m];
			
			//Número de solucoes na fronteira
			int numSol = 100000;
			//Obtém a fronteira de pareto real para o problema
			ArrayList<SolucaoNumerica> fronteiraReal = obterFronteira(n, numSol);

			double maxValorObjetivo ,minValorObjetivo; 
			
			//Ponto médio da fronteira real
			double[] pontoCentral = new double[m];
			
			
			//Per corre todos as dimensoes buscando o ponto médio
			for (int i = 0; i < m; i++) {
				ComparetorObjetivo comp = new ComparetorObjetivo(i);
				Collections.sort(fronteiraReal, comp);
				//Busca os valores máximo e mínimo para o objetivo i na fronteira real
				minValorObjetivo = ((SolucaoNumerica)fronteiraReal.get(0)).objetivos[i];
				maxValorObjetivo = ((SolucaoNumerica)fronteiraReal.get(fronteiraReal.size()-1)).objetivos[i];
				//Calcula o intervalo para o objetivo
				lambda[i] = (maxValorObjetivo - minValorObjetivo);
				//Calcula o ponto médio para o objetivo
				pontoCentral[i] = (maxValorObjetivo - minValorObjetivo)/2.0;
			}

			double menorDistancia = Double.MAX_VALUE;
			int indiceMenorDistancia = -1;

			int i = 0;
			//Busca o ponto da fronteira real mais próximo do ponto médio
			for (Iterator<SolucaoNumerica> iterator = fronteiraReal.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();

				double dist = distanciaEuclidiana(pontoCentral, solucaoNumerica.objetivos);
				if(dist < menorDistancia){
					menorDistancia = dist;
					indiceMenorDistancia = i;
				}
				i++;
			}



			SolucaoNumerica j = (SolucaoNumerica) (fronteiraReal.get(indiceMenorDistancia));

			joelho = new double[m];

			for (int k = 0; k < m; k++) {
				joelho[k] = j.objetivos[k];
			}


		}
		return joelho;
		}
	
	public double[] getLambda(int n){
		if(lambda == null)
			getJoelho(n);
		return lambda;
	}
	
	public ArrayList<SolucaoNumerica> obterSolucoesExtremas(int n, int s) {
		ArrayList<SolucaoNumerica> retorno = new ArrayList<SolucaoNumerica>();
		//Número de solucoes na fronteira
		int numSol = 100000;
		//Obtém a fronteira de pareto real para o problema
		ArrayList<SolucaoNumerica> fronteiraReal = obterFronteira(n, numSol);
		for(int i = 0; i<m; i++){
			ComparetorObjetivo comp = new ComparetorObjetivo(i);
			Collections.sort(fronteiraReal, comp);
			for(int j = 1; j<=s; j++){
				retorno.add(fronteiraReal.get(j));
			}
		}
		
		getJoelho(n);
		 	
		
		for(int i = 0; i<numSol; i++){
			Solucao temp = fronteiraReal.get(i);
			double dist = distanciaEuclidiana(temp.objetivos, joelho);
			temp.rank = dist;
		}
		
		ComparetorRank comp = new ComparetorRank();
		Collections.sort(fronteiraReal, comp);
		for(int j = 1; j<=s; j++){
			retorno.add(fronteiraReal.get(j));
		}
		return retorno;
		
		
	}
	
	


}
