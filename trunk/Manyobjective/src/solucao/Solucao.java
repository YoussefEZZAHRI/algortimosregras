package solucao;



/**
 * Classe que representa uma solu��o formada por um vetor de numeros reais
 * @author andre
 *
 */
public abstract class Solucao {
	
	
	public int n, m;
	
	public double[] objetivos;
	
	//Conta por qts solu��es a solu��o � dominada
	public double numDominacao;
		
	public double crowdDistance;
	
	public double rank = -1;
	public double balanceamentoRank = -1;
	
	
	/**
	 * Construtor da classe
	 * @param n Numero de variaveis
	 * @param m Numero de objetivos da busca
	 */
	public Solucao(int n, int m){
		this.n = n;
		this.m = m;
		
		objetivos = new double[m];
	
	}
	
	public abstract boolean  isNumerica();
	
	public abstract boolean  isBinaria();
	
	
	//Metodo que gera uma solucao aleatorio
	public abstract void iniciarSolucaoAleatoria();
		
	
	

}
