package solucao;

import sun.java2d.pipe.SolidTextRenderer;

/**
 * Classe que representa uma solução formada por um vetor de numeros reais
 * @author andre
 *
 */
public class Solucao {
	
	//Array contendo as variaves da solucao
	private double[] variaveis;
	
	//Variaveis utilizadas para o cálculo dos objetivos dos problemas DTLZ
	public double[] xm;
	public int k, n, m;
	
	public double[] objetivos;
	
	//Conta por qts soluções a solução é dominada
	public double numDominacao;
	
	//Variável que marca se a solução foi aceita na inserção da população secundária do MISA
	public boolean aceita;
		
	public double crowdDistance;
	
	public double rank = -1;
	
	//Limites que definem o espaço de busca dos atributos da partícula
	public double[] limitesPosicaoInferior;
	public double[] limitesPosicaoSuperior;
	
	
	/**
	 * Construtor da classe
	 * @param n Numero de variaveis
	 * @param m Numero de objetivos da busca
	 */
	public Solucao(int n, int m){
		this.n = n;
		this.m = m;
		variaveis = new double[n];
		//Calcula o valor k, que eh o numero de variaveis nao utilizadas nas funcoes objetivos
		k = n - m +1;
		//Array que contem o resto das variaveis nao utilizadas nas funcoes objetivos
		xm = new double[k];
		objetivos = new double[m];
		
		limitesPosicaoInferior = new double[variaveis.length];
		limitesPosicaoSuperior = new double[variaveis.length];
		
		setLimites();
		
	}
	
	/**
	 * Método que seta of limites superiores e inferiores para cada posicao do vetor velocidade
	 */
	public void setLimites(){
		for (int i = 0; i < limitesPosicaoInferior.length; i++) {
			
			limitesPosicaoInferior[i] = 0;
			limitesPosicaoSuperior[i] = 1;
		}
	}
	
	
	//Metodo que gera uma solucao aleatorio
	public void iniciarSolucaoAleatoria(){
		//Random rand = new Random();
		//rand.setSeed(System.currentTimeMillis());
		for(int i = 0; i<variaveis.length; i++){
			//double xi = rand.nextDouble();
			double xi = Math.random();
			variaveis[i] = xi;
		}
		
		for(int i = 0; i<k; i++){
			xm[i] = variaveis[m+i-1]; 
		}
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		//buff.append("\nVariaveis: ");
		for (int i = 0; i < variaveis.length; i++) {
			buff.append(new Double(variaveis[i]).toString().replace('.', ',') + "\t");
			//buff.append(variaveis[i] + "\t");
		}
		/*buff.append("\t");
		if(objetivos.length>0){
			for (int i = 0; i < objetivos.length; i++) {
				//buff.append(new Double(variaveis[i]).toString().replace('.', ',') + "\n");
				buff.append(objetivos[i] + "\t");
			}	
		}*/
		
		//if(rank!=-1)
		//	buff.append("\n" + rank + "\t");
		
		//buff.append("\n" + crowdDistance + "\t");
		
		return buff.toString();
	}
	
	public Object clone(){
		Solucao novaSolucao = new Solucao(n, m);
		for(int i = 0; i<variaveis.length; i++){
			novaSolucao.variaveis[i] = variaveis[i];
		}
		
		for(int i = 0; i<k; i++){
			novaSolucao.xm[i] = xm[i]; 
		}
		for(int i = 0; i<m; i++){
			novaSolucao.objetivos[i] = objetivos[i];
		}
		
		novaSolucao.aceita = aceita;
		novaSolucao.rank = rank;
		novaSolucao.crowdDistance = crowdDistance;
		novaSolucao.numDominacao = numDominacao;
				
		novaSolucao.limitesPosicaoInferior = new double[limitesPosicaoInferior.length];
		novaSolucao.limitesPosicaoSuperior = new double[limitesPosicaoSuperior.length];
		
		for (int i = 0; i < limitesPosicaoInferior.length; i++) {
			novaSolucao.limitesPosicaoInferior[i] = limitesPosicaoInferior[i];
			novaSolucao.limitesPosicaoSuperior[i] = limitesPosicaoSuperior[i];
			
		}
		
		return novaSolucao;
	}
	
	public boolean equals(Object o){
		Solucao sol = (Solucao) o;
		if(n!=sol.n || m!=sol.m || k!=sol.k)
			return false;
		for (int i = 0; i < variaveis.length; i++) {
			if(sol.variaveis[i]!=variaveis[i])
				return false;
		}
		return true;
		
	}
	
	public boolean truncar() {
		
		boolean over_limits = false;
		int k = 0;
		//Checa se algum valor da posicao excedeu o limites(numero negativo)
		for (int i = 0; i < variaveis.length; i++) {
			if(variaveis[i]<limitesPosicaoInferior[i]){
				double x = limitesPosicaoSuperior[i] + variaveis[i];
				variaveis[i] = Math.max(limitesPosicaoInferior[i], x);
				over_limits = true;
			}
			if(variaveis[i]>limitesPosicaoSuperior[i]){
				variaveis[i] = variaveis[i] % limitesPosicaoSuperior[i];
				over_limits = true;
			}
			
			if(i>=(m-1))
				xm[k++] = variaveis[i];
		}
		return over_limits;
	}
	
	public void setVariavel(int i, double valor){
		
		
	
		variaveis[i] = valor;
		if(i>=(m-1))
			xm[i-(m-1)] = variaveis[i];
	}
	
	public double getVariavel(int i){
		return variaveis[i];
	}
	
	public double[] getVariaveis(){
		return variaveis;
	}

}
