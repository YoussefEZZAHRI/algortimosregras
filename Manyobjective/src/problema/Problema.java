package problema;

import indicadores.PontoFronteira;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import solucao.ComparetorObjetivo;
import solucao.ComparetorObjetivoPF;
import solucao.ComparetorRank;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public abstract class Problema {
	
	public int m;
	public int n;
	
	
	public int avaliacoes;
	
	public String problema;
	
	public  double[] joelho = null;
	
	public  double[] lambda = null;
	
	public double inc;
	public int varVez;
	
	public double s;
	
	public String[] maxmim;
	
	public boolean r;

	
	public Problema(int m){
			
		this.m = m;
		avaliacoes = 0;
		
		this.s  = 0.5;
		this.maxmim = new String[m];
		for (int k = 0; k < maxmim.length; k++) {
			maxmim[k] = "-";
		}
		this.r = false;
		
	}
	
	public Problema(int m, double inc){
		
		this.m = m;
		avaliacoes = 0;
		
		this.inc = inc;
		
	}
	
	public abstract double[] calcularObjetivos(Solucao solucao);
	
	public abstract ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol);
	
	/**
	 * Método que gera a próxima solução através do incremento passado no construtor
	 * @param solucaoBase Solução base para a geração da nova solução
	 * @param varVez2 Varia
	 * @param inicio
	 * @param fim
	 */
	public boolean getProximaSolucao(SolucaoNumerica solucaoBase, int inicio, int fim){
		
		/*int decimalPlace = 7;
		
		BigDecimal incBig = new BigDecimal(this.inc);
		BigDecimal valVarVezBig = new BigDecimal(solucaoBase.getVariavel(varVez));
		
		valVarVezBig = valVarVezBig.add(incBig);
		valVarVezBig = valVarVezBig.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);*/
		
		
		Double valVarVez = new Double(solucaoBase.getVariavel(varVez));

		valVarVez=valVarVez+inc;
		
		//Double valVarVez = new Double(valVarVezBig.toString());

		if(valVarVez >1){
			while(valVarVez>=1){
				valVarVez = 0.0;
				solucaoBase.setVariavel(varVez, valVarVez);
				varVez--;
				if(varVez < inicio)
					return false;
				valVarVez = solucaoBase.getVariavel(varVez);
			}
		}

		
		if(varVez!=fim){
			/*valVarVezBig = new BigDecimal(valVarVez);
			valVarVezBig = valVarVezBig.add(incBig);
			valVarVezBig = valVarVezBig.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
			valVarVez = new Double(valVarVezBig.toString());*/
			valVarVez=valVarVez+inc;
		}
			valVarVez = Math.min(1.0, valVarVez);
		solucaoBase.setVariavel(varVez, valVarVez);
		varVez = fim;
		

		return true;

	}
	
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
	
public double g7(double[] xm){
		
		double soma = 0;
		for (int i = 0; i < xm.length; i++) {
			soma+= xm[i];
		}
		double fator = 9.0/xm.length;
		
		return 1 + fator*soma;
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
	 * M�todo que obtem o joelho da uma fronteira de Pareto real
	 * Inicia tamb�m um array de double lambda contendo o intervalo de valores para cada objetivo das solucoes na fronteira de pareto
	 * O m�todo busca o ponto m�dio para cada dimensao do espaco de objetivos. O joelho � o ponto mais pr�ximo deste ponto m�dio. 
	 * @param n
	 * @return
	 */
	public double[] getJoelho(int n, ArrayList<PontoFronteira> fronteiraReal){
		
			joelho = new double[m];
			lambda = new double[m];
			
			if(fronteiraReal ==null){
				//N�mero de solucoes na fronteira
				int numSol = 10000;
				//Obt�m a fronteira de pareto real para o problema
				ArrayList<SolucaoNumerica> solucoes = obterFronteira(n, numSol);
				
				fronteiraReal = new ArrayList<PontoFronteira>();
				for (Iterator<SolucaoNumerica> iterator = solucoes.iterator(); iterator
						.hasNext();) {
					SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
							.next();
					PontoFronteira pf = new PontoFronteira(solucaoNumerica.objetivos);
					fronteiraReal.add(pf);
					
				} 
			}

			double maxValorObjetivo ,minValorObjetivo; 
			
			//Ponto m�dio da fronteira real
			double[] pontoCentral = new double[m];
			
			
			//Percorre todos as dimensoes buscando o ponto m�dio
			for (int i = 0; i < m; i++) {
				ComparetorObjetivoPF comp = new ComparetorObjetivoPF(i);
				Collections.sort(fronteiraReal, comp);
				//Busca os valores m�ximo e m�nimo para o objetivo i na fronteira real
				minValorObjetivo = (fronteiraReal.get(0)).objetivos[i];
				maxValorObjetivo = (fronteiraReal.get(fronteiraReal.size()-1)).objetivos[i];
				//Calcula o intervalo para o objetivo
				lambda[i] = (maxValorObjetivo - minValorObjetivo);
				//Calcula o ponto m�dio para o objetivo
				pontoCentral[i] = ((maxValorObjetivo - minValorObjetivo)/2.0) + minValorObjetivo;
			}

			double menorDistancia = Double.MAX_VALUE;
			int indiceMenorDistancia = -1;

			int i = 0;
			//Busca o ponto da fronteira real mais pr�ximo do ponto m�dio
			for (Iterator<PontoFronteira> iterator = fronteiraReal.iterator(); iterator.hasNext();) {
				PontoFronteira pf = iterator.next();

				double dist = distanciaEuclidiana(pontoCentral, pf.objetivos);
				if(dist < menorDistancia){
					menorDistancia = dist;
					indiceMenorDistancia = i;
				}
				i++;
			}



			PontoFronteira j = (fronteiraReal.get(indiceMenorDistancia));

			joelho = new double[m];

			for (int k = 0; k < m; k++) {
				joelho[k] = j.objetivos[k];
			}


		return joelho;
		}
	
	public double[] getLambda(int n, ArrayList<PontoFronteira> fronteiraReal){
		if(lambda == null)
			getJoelho(n, fronteiraReal);
		return lambda;
	}
	
		
	public double[] obterLimites(ArrayList<SolucaoNumerica> fronteiraReal){
		double limites[] = new double[m];
		for (int i = 0; i < limites.length; i++) {
			limites[i] = Double.NEGATIVE_INFINITY;
		}
		for (Iterator<SolucaoNumerica> iterator = fronteiraReal.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			for (int i = 0; i < m; i++) {
				if(solucaoNumerica.objetivos[i]>limites[i])
					limites[i] = solucaoNumerica.objetivos[i];
			}
		}
		
		return limites;
	}
	
	public void normalizarFronteira(double[] limites, ArrayList<SolucaoNumerica> fronteira){
		for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			for(int i = 0; i<m; i++){
				solucaoNumerica.objetivos[i] = solucaoNumerica.objetivos[i]/limites[i];
			}
		}
		
	}
	
	public ArrayList<SolucaoNumerica> obterSolucoesExtremas(int n, int s) {
		ArrayList<SolucaoNumerica> retorno = new ArrayList<SolucaoNumerica>();
		//N�mero de solucoes na fronteira
		int numSol = 10000;
		//Obt�m a fronteira de pareto real para o problema
		ArrayList<SolucaoNumerica> fronteiraReal = obterFronteira(n, numSol);
		for(int i = 0; i<m; i++){
			ComparetorObjetivo comp = new ComparetorObjetivo(i);
			Collections.sort(fronteiraReal, comp);
			for(int j = 1; j<=s; j++){
				retorno.add(fronteiraReal.get(j));
			}
		}
		
		getJoelho(n, null);
		 	
		
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
