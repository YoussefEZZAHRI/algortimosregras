package kernel.nuvemparticulas;

import java.util.ArrayList;
import java.util.Iterator;

import pareto.FronteiraPareto;
import problema.Problema;
import solucao.SolucaoNumerica;


/**
 * Classe que representa uma partícula do MOPSO-N
 * @author André B. de Carvalho
 *
 */
public class Particula {
	
	//Solução a qual a partícula representa
	public SolucaoNumerica solucao = null;
	//Velocidade atual da partícula
	public double[] velocidade = null;
	//Posição atual da partícula
	public double[] posicao = null;
	
	//Melhor local da partícula
	public double[] localBest = null;
	//Valores dos objestivos para o melhor local
	public double[] localBestObjetivos = null;
	//Melhor global da partícula
	public double[] globalBest = null;
	//Valor da distância sigma da partícula
	public double[] sigmaVector = null;
	
	//Coeficientes da equação do cálculo da velocidade
	public double phi1;
	public double phi2;
	
	public double c1;
	public double c2;
	
	//Valores que definem um limite máximo para a velocidade da partícula
	public double[] limitesMaxVelSup;
	public double[] limitesMaxVelInf;
	//Limites que definem o espaço de busca dos atributos da partícula
	public double[] limitesPosicaoInferior;
	public double[] limitesPosicaoSuperior;
	
	
	                        
	                        
	
	
	public double omega;
	//Limites da atualização dos coeficientes
	private final double MAX_PHI = 1;
	private final double MAX_OMEGA = 0.8;
	private final double REDUCAO_VELOCIDAE = 0.001;

	public boolean mutacao;
	
	Problema problema = null;
	
	public Particula(){
	}
	
	
	/**
	 * Método que inicializa a particula de forma aleatoria
	 * 
	 */
	public void iniciarParticulaAleatoriamente(Problema prob, SolucaoNumerica s){
		solucao = s;
		posicao = localBest = s.getVariaveis();
		velocidade = new double[posicao.length];
		inicializarVelocidadeAleatoria();
			
		//Obtendo os valores de phi aleatoriamente, com valor maximo MAX_PHI
		phi1 = (Math.random()*10) % MAX_PHI;
		phi2 = (Math.random()*10) % MAX_PHI;
		
		//Obtendo o valor de omega aleatoriamente, com valor maximo MAX_OMEGA
		omega = (Math.random()) % MAX_OMEGA;
		
		problema = prob;
		mutacao = false;
		
		limitesMaxVelInf = new double[velocidade.length];
		limitesMaxVelSup = new double[velocidade.length];
		
		limitesPosicaoInferior = new double[posicao.length];
		limitesPosicaoSuperior = new double[posicao.length];
		
		setLimites();
		
		c1 = c2 = 2.05;
		
	}
	
	/**
	 * Método que inicia a velocidade da partícula  de forma aleatória
	 *
	 */
	public void inicializarVelocidadeAleatoria(){
		for(int i = 0; i<velocidade.length; i++){
			//double xi = rand.nextDouble();
			double v = Math.random();
			velocidade[i] = v;
		}
	}
	
	/**
	 * Método que seta of limites superiores e inferiores para cada posicao do vetor velocidade
	 */
	public void setLimites(){
		for (int i = 0; i < limitesMaxVelSup.length; i++) {
			limitesMaxVelSup[i] = 5;
			limitesMaxVelInf[i] = -5;
			
			limitesPosicaoInferior[i] = 0;
			limitesPosicaoSuperior[i] = 1;
			
			
			
		}
		
		
	}
	
	
	/**
	 * Funcao que retorna o valor da phi1. Phi1 pode variar de acordo com o número da iteração
	 * @param iteracao Número da iteração da execução do algoritmo 
	 * @return Valor de phi1
	 */
	public double getPhi1(int iteracao){
		//Valor de phi1 aleatorio. Pode-se utilizar uma função que varia o valor de phi de acordo com aiteração
		return phi1 = (Math.random()*10) % MAX_PHI;
	}
		
	/**
	 * Funcao que retorna o valor da phi1. Phi2 pode variar de acordo com o número da iteração
	 * @param iteracao Número da iteração da execução do algoritmo 
	 * @return Valor de phi2
	 */
	public double getPhi2(int iteracao){
		//Valor de phi2 aleatorio. Pode-se utilizar uma função que varia o valor de phi de acordo com aiteração
		return phi2 = (Math.random()*10) % MAX_PHI;
	}
	
	/**
	 * Funcao que retorna o valor aleatorio de phi1 [0,1].  
	 * @return Valor de phi1
	 */
	public double getPhi1(){
		return phi1 = Math.random() % MAX_PHI;
	}
		
	/**
	 * Funcao que retorna o valor aleatorio de phi2 [0,1].  
	 * @return Valor de phi2
	 */	public double getPhi2(){
		return phi2 = Math.random() % MAX_PHI;
	}
	 
	 public double getC1(){
		 return c1 = 1.5 + Math.random();
	 }
	 
	 public double getC2(){
		 return c2 = 1.5 + Math.random();
	 }
	 
	 public double getFi(){
		 if(c1+c2>4)
			 return c1+c2;
		 else return 1;
	 }
	
	/**
	 * Atualiza o valor de omega aleatoriamente
	 * @return
	 */
	public double getOmega(){
		return omega = (Math.random()) % MAX_OMEGA;
	}
	
	
	/**
	 * Método que cálcula o vetor sigma de acordo com a fórmula proposta por Mostaghim
	 * @return Vetor sigma
	 */
	public double[] calcularSigmaVector(){
		double[] objetivos = solucao.objetivos;
		int tamVetor = (int) combinacao(objetivos.length, 2);
		sigmaVector = new double[tamVetor];
		int  cont = 0;
		for(int i = 0; i<objetivos.length-1; i++){
			for(int j = i+1; j<objetivos.length;j++){
				double obj1 = objetivos[i];
				double obj2 = objetivos[j];
				sigmaVector[cont++] =  calcularSigma(obj1, obj2);
			}
		}
		
		return sigmaVector;
		
	}
	
	/**
	 * Método que cálculo o valor sigma para dois objetivos
	 * @param f1 Objetivo 1
	 * @param f2 Objetivo 2
	 * @return Valor da função sigma
	 */
	public double calcularSigma(double f1, double f2){
		double valor = (f1*f1) - (f2*f2);
	
		double denominador = (f1*f1)+ (f2*f2);
		if(denominador!=0)
			return  valor/denominador;
		else
			return 0;
	}
	
	/**
	 * Cálcula a combinação de m, n a n.
	 * @param m 
	 * @param n
	 * @return Combinação (m n) 
	 */
	public double combinacao(int m, int n){
		if(n==m)
			return 1;
		else{
			double fatM = fatorial(m);
			double fatN = fatorial(n);
			double fatNM = fatorial(m-n);
			return (fatM)/(fatN*fatNM);
		}
	}
	
	/**
	 * Cálcula o fatorial de n
	 * @param n
	 * @return n!
	 */
	public double fatorial(int n){
		double fat = 1;
		for(int i = n;i>0;i--){
			fat*=i;
		}
		return fat;
	}
	
	/**
	 * Método que escolhe qual particula do repositorio sera escolhida como global best
	 * Escolhe a partícula probabilisticamente atraves de uma roleta com os valores da
	 * distância Euclidiana dos sigmaVector
	 * @param repositorio
	 * @return
	 */
	public double[] escolherGlobalBestSigma(ArrayList<Particula> repositorio){
		double melhorValor = Double.MAX_VALUE;
		Particula gbest = null; 
		//Calcula o valor da distancia euclidia dos sigmaVector de cada particula do repositorio
		//e escolhe a menor
		for (Iterator<Particula> iter = repositorio.iterator(); iter.hasNext();) {
			Particula rep = iter.next();
			double temp = distanciaEuclidiana(sigmaVector, rep.sigmaVector);
			if(temp<melhorValor){
				melhorValor = temp;
				gbest = rep;
			}
		}

		globalBest = gbest.posicao;
		
		return globalBest;
	}
	
	/**
	 * Método que escolhe qual particula do repositorio sera escolhida como global best
	 * Escolhe a partícula probabilisticamente atraves de uma roleta com os valores da
	 * distância Euclidiana dos sigmaVector
	 * @param repositorio
	 * @return
	 */
	public double[] escolherGlobalBestBinario(ArrayList<Particula> repositorio){
		int ordem = (int)Math.ceil(Math.log10(repositorio.size()));
		int indice1 = (int)(Math.random()*(Math.pow(10, ordem))%repositorio.size());
		int indice2 = (int)(Math.random()*(Math.pow(10, ordem))%repositorio.size());
		Particula particula1 = repositorio.get(indice1);
		Particula particula2 = repositorio.get(indice2);
		if(particula1.solucao.crowdDistance>particula2.solucao.crowdDistance)
			globalBest = particula1.posicao;
		else
			globalBest = particula2.posicao;
		
		return globalBest;
	}
	
	/**
	 * Cálculo da distânca Euclidiana entre dois vetores de mesmo tamanho
	 * @return Valor da distância Euclidiana entre os vetores
	 */
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for(int i = 0; i<vetor1.length; i++){
			double diferenca = Math.pow(vetor1[i]-vetor2[i],2);
			soma +=diferenca;
		}
		return Math.sqrt(soma);
	}
	
	/**
	 * Método que cálcula a nova velocidade da particula
	 * @param iter Número da iteração da execução do algoritmo
	 */
	public void calcularNovaVelocidade(){
		//omega*velocidade
		double[] parte1 = multiplicacao(getOmega(),velocidade);
		//phi1*(localBest - posicao);
		double[] parte2 = multiplicacao(c1*getPhi1(),soma(localBest, posicao, -1));
		//phi2*(globalBest - posicao);
		double[] parte3 = multiplicacao(c2*getPhi2(),soma(globalBest, posicao, -1));
		double[] parte4 = soma(parte2,parte3,1);
		double[] parte5 = soma(parte1,parte4,1);
		
		velocidade = parte5;
		
		
	}
	
	/**
	 * Método de cálculo da velocidade com limitação do valor da velocidade
	 * Utilizado no algoritmo SMOPSO
	 */
	public void calcularNovaVelocidadeConstriction(){
		
		//omega*velocidade
		double[] parte1 = multiplicacao(getOmega(),velocidade);
		//phi1*(localBest - posicao);
		double[] parte2 = multiplicacao(getC1()*getPhi1(),soma(localBest, posicao, -1));
		//phi2*(globalBest - posicao);
		double[] parte3 = multiplicacao(getC2()*getPhi2(),soma(globalBest, posicao, -1));
		double[] parte4 = soma(parte2,parte3,1);
		double[] parte5 = soma(parte1,parte4,1);
		
		double fi = getFi();
		double raiz = fi*fi - 4*fi;
		if(raiz<0)
			raiz = 0;
		double X = 2.0/(2-fi-Math.sqrt(raiz));
		
		velocidade = multiplicacao(X, parte5);
		
		for (int i = 0; i < velocidade.length; i++) {
			double deltai = (limitesMaxVelSup[i]-limitesMaxVelInf[i])/2;
			if(velocidade[i]>deltai)
				velocidade[i] = deltai;
			else{
				if(velocidade[i]<=(deltai*-1))
					velocidade[i] = deltai*-1;
			}
			
		}
		
		
	}
	
	/**
	 * Método que calcula a nova posição da partícula. Soma a posição à velocidade e limita o valor da velocidade
	 *
	 */
	public void calcularNovaPosicao(){
		posicao = soma(posicao, velocidade, 1);
		
		/*for (int i = 0; i < posicao.length; i++) {
			solucao.variaveis[i] = posicao[i];
		}*/
	}

	
	/**
	 * Método que trunca os valores da posição da partícula caso eles extrapolem os limites
	 */
	public void truncar() {
	
		for (int i = 0; i < posicao.length; i++) {
			solucao.setVariavel(i, posicao[i]);
		}
		
		boolean over_limits = solucao.truncar();
		
		for (int i = 0; i < posicao.length; i++) {
			 posicao[i] = solucao.getVariavel(i);
		}
		
		//Caso algum limite seja extrapolado, a velocidade eh reduzida em 0.001
		if(over_limits){
			for (int i = 0; i < velocidade.length; i++) {
				velocidade[i] = velocidade[i] * REDUCAO_VELOCIDAE;
				
			}
		}
	}

	/**
	 * Método que soma ou subtrai dois vetores
	 * @param vetor1 Primeiro vetor da soma
	 * @param vetor2 Segundo vetor da soma
	 * @param fator Fator que irá defini se será uma soma ou subtração dos vetores
	 * @return Vetor resultante
	 */
	public double[] soma(double[] vetor1, double[] vetor2, double fator){
		double soma = 0;
		double[] retorno = new double[vetor1.length];
		
		for (int i = 0; i < vetor1.length; i++) {
			soma = vetor1[i] +(fator*vetor2[i]);
			retorno[i] = soma;
		}
		return retorno;
	}
	
	/**
	 * Método que executa a multiplicação de um vetor por um escalar
	 * @param k Escalar da multiplicação
	 * @param vetor1 Vetor da multiplicação
	 * @return Vetor resultante
	 */
	public double[] multiplicacao(double k, double[] vetor){
		double mult = 0;
		double[] retorno = new double[vetor.length];
		for (int i = 0; i < vetor.length; i++) {
			mult = vetor[i] * k;
			retorno[i] = mult;
		}
		return retorno;
	}
	

	
	
	
	
	/**
	 * Método que verifica se os novos valores dos objetivos da partícula dominam o melhor local.
	 * Caso sim os novos objetivos são setados.
	 *
	 */
	public void escolherLocalBest(FronteiraPareto pareto){
		double[] objetivos = solucao.objetivos;
		
		int retorno = pareto.compararMedidas(objetivos,localBestObjetivos);
		if(retorno == 1 || retorno == 0){
			localBestObjetivos = objetivos;
			localBest = posicao;
		} 
	}
	
	/**
	 * Método que verifica se duas partículas são iguais.
	 * Utiliza o equal da Solucao.
	 */
	public boolean equals(Object o){
		Particula p = (Particula) o;
		return solucao.equals(p.solucao);
		
	}
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		str.append("Posicao: <");
		for(int i = 0; i< posicao.length; i++){
			str.append(posicao[i] + ", ");
		}
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		
		str.append("Velocidade: <");
		for(int i = 0; i< velocidade.length; i++){
			str.append(velocidade[i] + ", ");
		}
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		
		str.append("Local Best: <");
		for(int i = 0; i< localBest.length; i++){
			str.append(localBest[i] + ", ");
		}
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		if(globalBest!=null){
		str.append("Global Best: <");
		for(int i = 0; i< globalBest.length; i++){
			str.append(globalBest[i] + ", ");
		}
		}
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		
		str.append("Objetivos: <");
		for(int i = 0; i< solucao.objetivos.length; i++){
			str.append(solucao.objetivos[i] + ", ");
		}
		
		str.deleteCharAt(str.length()-1);
		str.deleteCharAt(str.length()-1);
		str.append(">\n");
		str.append("Rank: " + solucao.rank);
		
		return str.toString();
	}
	
	public Object clone(){
		Particula novaParticula = new Particula();
		novaParticula.posicao = new double[posicao.length];
		novaParticula.velocidade = new double[velocidade.length];
		if(localBest!=null)
			novaParticula.localBest = new double[localBest.length];
		if(localBestObjetivos!=null)
			novaParticula.localBestObjetivos = new double[localBestObjetivos.length];
		if(globalBest!=null)
			novaParticula.globalBest = new double[globalBest.length];
		
		
		for (int i = 0; i < novaParticula.posicao.length; i++) {
			novaParticula.posicao[i] = posicao[i];
			novaParticula.velocidade[i] = velocidade[i];
			if(localBest!=null)
				novaParticula.localBest[i] = localBest[i];
			if(globalBest!=null)
				novaParticula.globalBest[i] = globalBest[i];
		}
		
		if(localBestObjetivos!=null){
			for (int i = 0; i < localBestObjetivos.length; i++) {
				novaParticula.localBestObjetivos[i] = localBestObjetivos[i];
			}
		}
		
		novaParticula.solucao = (SolucaoNumerica)solucao.clone();
		problema.calcularObjetivos(solucao);
		
		return novaParticula;
	}
	
	public void atualizarSolucao(){
		for (int i = 0; i < posicao.length; i++) {
			solucao.setVariavel(i, posicao[i]);
		}
	}
	

}

