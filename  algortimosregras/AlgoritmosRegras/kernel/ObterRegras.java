package kernel;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import objetivo.Especificidade;
import objetivo.FuncaoObjetivo;
import objetivo.Laplace;
import objetivo.Novidade;
import objetivo.Sensitividade;

import votacao.Votacao;
import votacao.VotacaoConfidence;
import votacao.VotacaoConfidenceLaplace;
import votacao.VotacaoConfidenceLaplaceOrdenacao;
import votacao.VotacaoSimples;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import regra.Atributo;
import regra.AtributoNominal;
import regra.AtributoNumerico;
import regra.Regra;

/**
 * Classe abstrata que representa um algoritmo de obtenção de regras.
 * Classe principal que contém os métodos e atributos necessários para obtenção das regras. 
 * @author André B. de Carvalho
 *
 */
public abstract class ObterRegras {
	
	//Arraylist que contém as regras encontradas pelos algoritmos de obtenção de regras.
	protected ArrayList<Regra> regras = null;
	//Obejto do framework weka que contém os dados de treinamento
	public Instances dados = null;
	
	//Objetos que contém as fronteiras de pareto para as classes positivas ou negativas
	public FronteiraPareto paretoPos = null;
	public FronteiraPareto paretoNeg = null;
	
	//Matriz de confusão que é calculada para cada execução
	public MatrizConfusao confusao;
	
	//Arquivo de log
	public PrintStream psLog = null;
	
	//Objeto que cotem todas as informacoes os dados do experimento
	public DadosExperimentos dadosExperimento = null;
	
	//Objeto que ira conter o metodod e votacao passado como parametro da execucao (Padrão Statement )
	public Votacao metodoVotacao = null;
	
	//String que contém o nome da classe positiva da execução (Ex: positive ou true )
	public  String classePositiva;
	//String que contém o nome da classe negativa da execução (Ex: negative ou false )
	public  String classeNegativa;
	
	//Contéma  informação do fold corrente
	public int numFold;
	
	//Variável que indica se vai haver ou não impressão na tela e do log do andamento da execução
	public boolean verbose;
	
	//Matriz que contém o cálculo do coeficiente de Jaccard de cada regra encontrada
	//Utilizado no método para a redução do número de regras. Não implementado
	public double[][] coeficienteJaccard = null;
	
	//Array que contém informações necessárias para o cálculo do coeficiente de Jaccard
	public int[][] distribuicaoAtributos = null;

	
	//Arraylist que contém todos os objetivos da busca
	public ArrayList<FuncaoObjetivo> objetivos = null;
	
	/**
	 * Método abstrato que deve ser implementado por todo algoritmo de obtenção de regras a partir 
	 * de uma base de dados previamente carregada.
	 * @param cPositiva Indice da classe positiva
	 * @param cNegativa Indice da classe negativa
	 * @return As regras encontradas
	 * @throws Exception
	 */	
	public abstract ArrayList<Regra> obterRegras(int cPositiva, int cNegativa) throws Exception;
	
	public ObterRegras(String[] objs){
		addObjetivos(objs);
		
	}
	
	/**
	 * Método que carrega o arquivo do tipo ARFF num objeto do tipo Instances 
	 * @param arquivoARFF Caminho do arquivo ARFF
	 * @param indicePositiva Indice que indica qual eh a classe positiva
	 * @param indiceNegativa Indice que indica qual eh a classe negativa
	 * @throws Exception Execção lançada se há erro no acesso ao arquivo
	 */
	public void carregarInstancias(String arquivoARFF, int indicePositiva, int indiceNegativa)  throws Exception{
		Reader reader = new FileReader(arquivoARFF);
		dados = new Instances(reader);
		dados.setClassIndex(dados.numAttributes()-1);
		Attribute classe = dados.classAttribute();
		classePositiva = classe.value(indicePositiva);
		classeNegativa = classe.value(indiceNegativa);
	}
	
	/**
	 * Método que preencher a matriz de contingencia para todas as regras passadas como parametro
	 * atraves das instancias passadas como parametros
	 * @param regras Regras a serem preenchidas as matrizes de contigencias
	 * @param dados Instancias utilizadas no prenchimendo da matriz
	 */
	public void preencherMatrizContigencia(ArrayList<Regra> regras, Instances dados){
		
		for (Iterator iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			regra.matrizContigencia.limparMatrizContingencia();
			//Método que limpa a matriz de contingencia antes de preenche-la
			//regra.matrizContigencia.limparMatrizContingencia();
			for(int i = 0; i<dados.numInstances(); i++){
				Instance instancia = dados.instance(i);
				regra.compararRegraContigencia(instancia);
			}
			regra.calcularValoresObjetivos();	
		}
	}
	
	/**
	 * Método que preenche a matriz de continegência da regra passada como parametro
	 * @param regra Regra a ser preenchida a matriz de contigencia
	 * @param dados Instancias utilizadas no prenchimendo da matriz
	 */
	public void preencherMatrizContigencia(Regra regra, Instances dados){
		regra.matrizContigencia.limparMatrizContingencia();
		for(int i = 0; i<dados.numInstances(); i++){
			Instance instancia = dados.instance(i);
			regra.compararRegraContigencia(instancia);
		}
		regra.calcularValoresObjetivos();
	
}
	
	/**
	 * Método que percorre os dados da base e retorna o valor maximo e o valor minimo 
	 * presentes na base para o atributo passabdo como parametro.
	 * @param att Tipo do atributo a ser calculado os valores maximo e minimo
	 * @return Array contendo os valores maximo e minimo
	 */
	public double[] obterMaximoMinimoAtributo(Attribute att){
		Instance exemplo1 = dados.firstInstance();
		double max = exemplo1.value(att);
		double min = exemplo1.value(att);
		for(int i = 1; i<dados.numInstances(); i++){
			Instance exemplo = dados.instance(i);
			double temp = exemplo.value(att);
			if(temp<min)
				min = temp;
			if(temp>max)
				max = temp;
		}
		double[] retorno = new double[2];
		retorno[0] = min;
		retorno[1] = max;
		return retorno;
		
	}
	
	
	/**
	 * Calcula a porcentagem da classe majoritário dos dados passados como parâmetro
	 * @param dados Dados para o cálcula da classe majoritária
	 * @return Vetor contendo o índice e a porcentagem da classe majoritária
	 */
	public double[] obterClasseMajoritaria(Instances dados){
		int positivo = 0;
		int negativo = 0;
		double classeMajoritaria = 0;
		double porcentagemClasseMajoritaria = 0;
		for(int i = 0; i<dados.numInstances(); i++){
			Instance temp = dados.instance(i);
			if(temp.classValue() == temp.classAttribute().indexOfValue(classePositiva)){
				positivo++;
			} else {
				negativo++;
			}
		}
		
		if(positivo>negativo){
			classeMajoritaria = dados.classAttribute().indexOfValue(classePositiva);
			porcentagemClasseMajoritaria = (double)positivo/(double)(positivo + negativo);
		} else {
			classeMajoritaria = dados.classAttribute().indexOfValue(classeNegativa);
			porcentagemClasseMajoritaria = (double)negativo/(double)(positivo + negativo);
			
		}
		
		double[] retorno = new double[2];
		retorno[0] = classeMajoritaria;
		retorno[1] = porcentagemClasseMajoritaria;
		return retorno;
		
	}
	
	/**
	 * Calcula a AUC a partir de um conjunto de dados passado como parâmetro
	 * @param dadosTeste Dados de teste para o cálculo da AUC
	 * @return O valor da AUC das regras para os dados passados como parâmetro
	 * @throws IOException
	 */
	public double obterAUC(Instances dadosTeste) throws IOException{
		
				
		double[][] d = construirROC(dadosTeste,regras);
		
		
		/*CurvaROC positive = new CurvaROC(d[0], d[1], "Curva ROC - Positive");
		CurvaROC negative = new CurvaROC(d[2], d[3], "Curva ROC - Negative");
		positive.setVisible(true);
		positive.pack();
		negative.setVisible(true);
		negative.pack();*/
		
		//Calcular Area da classe positiva
		double area = calcularArea(d[1], d[0]);
		
	
		return area;
				
	}
	
	/**
	 * Método que cálcula a área da curva ROC pelo método os trapézios
	 * @param vetorX Vetor que contém as coordenadas dos pontos no eixo X
	 * @param vetorY Vetor que contém as coordenadas dos pontos no eixo Y 
	 * @return
	 */
	public double calcularArea(double[] vetorX, double[] vetorY){
	
		double h;
		double area = 0;
		for(int i = 0  ; i<vetorX.length-1; i++){

			h = Math.abs(vetorX[i]-vetorX[i+1]);
			
			if(h>0){
				double temp = ((vetorY[i+1]+vetorY[i])/2.0) * h;
				area+= temp;
			}
			
		}
		
		return area;
	}

	
	/**
	 * Metodo que construi a curva ROC atraves da instancia de teste e das regras passadas como parametro.
	 * Método implementado a partir do artigo: ROC Graphs: Notes and Practical Considerations for Researchers
	 * @param dadosTeste Instancias de teste
	 * @param regras Regras
	 * @return Array de double contendo os valores dos pontos da curva ROC para a classe positiva e negativa. 
	 * Indice 0 valores True Postive da classe positiva
	 * Indice 1 valores False Postive da classe positiva
	 * Indice 2 valores True Negative da classe negativa
	 * Indice 3 valores False Negative da classe negativa
	 */
	public double[][] construirROC(Instances dadosTeste, ArrayList<Regra> regras){
		
		ArrayList<InstanceVotacao> votos = new ArrayList<InstanceVotacao>();
		SortedSet<Double> limiares = new TreeSet<Double>();
		for(int i = 0; i<dadosTeste.numInstances(); i++){
			Instance exemplo = dadosTeste.instance(i); 
			double voto = metodoVotacao.votacao(regras, exemplo, classePositiva);
			InstanceVotacao temp= new InstanceVotacao(exemplo, voto);
			votos.add(temp);
			limiares.add(new Double(voto));
		}
		
		int i = 0;
		for (Iterator iter = votos.iterator(); iter.hasNext(); i++) {
			InstanceVotacao element = (InstanceVotacao) iter.next();
			element.codigo = i;
			
		}
		Collections.sort(votos);
	
		double[][] classesSugeridasPositivos = new double[limiares.size()][dadosTeste.numInstances()];
		
		i = 0;
		for (Iterator iter = limiares.iterator(); iter.hasNext(); i++) {
			Double limiar = (Double) iter.next();
			int j = 0;
			for (Iterator iterator = votos.iterator(); iterator.hasNext(); j++) {
				InstanceVotacao temp = (InstanceVotacao) iterator.next();
				if(temp.votacao<limiar.doubleValue()){
					classesSugeridasPositivos[i][j] = temp.exemplo.classAttribute().indexOfValue(classeNegativa);
					
				} else{
					classesSugeridasPositivos[i][j] = temp.exemplo.classAttribute().indexOfValue(classePositiva);
					
				}
			}
		}
		
		double[] tpr = new double[classesSugeridasPositivos.length];
		double[] fpr = new double[classesSugeridasPositivos.length];
		
		
		for(int k = 0; k<classesSugeridasPositivos.length; k++){
			int j = 0;
			int tp = 0;
			int fp = 0;
		
			int p = 0;
			int n = 0;
			
			for (Iterator iter = votos.iterator(); iter.hasNext(); j++) {
				InstanceVotacao element = (InstanceVotacao) iter.next();
				double classeReal = element.exemplo.classValue();
				
				double classeSugeridaPositiva = classesSugeridasPositivos[k][j];
				if(classeSugeridaPositiva == element.exemplo.classAttribute().indexOfValue(classePositiva)){
				
					if(classeSugeridaPositiva == classeReal){
						tp++;
					} else {
						fp++;
					}
				}
				
									
				if(classeReal == element.exemplo.classAttribute().indexOfValue(classePositiva))
					p++;
				else 
					n++;
			}
			
			
			if(p!=0){
				tpr[k] = (double)tp/(double)p;
				
			} else {
				tpr[k] = 0;
			}
			if(n!=0){
				fpr[k] = (double)fp/(double)n;	
			} else{
				fpr[k] = 0;	
			}
			
		}
		
		double[][] result = new double[2][tpr.length];
		result[0] = tpr;
		result[1] = fpr;
		return result;

	}

	/**
	 * Método que preenche a matriz de confusão para a classe passada em parâmetro
	 * @param confusao Matriz de confusao a ser preenchida
	 * @param dadosTeste Dados de testes
	 * @param regras Regras do modelo
	 */
	public void preencherMatrizConfusao(MatrizConfusao confusao, Instances dadosTeste, ArrayList<Regra> regras){
		double votacao = 0;
		Instance temp = null;
		for(int i = 0; i<dadosTeste.numInstances(); i++){
			temp = dadosTeste.instance(i);
			votacao = metodoVotacao.votacao(regras, temp, classePositiva);
			//Se a votacao for 0 o elemento é assinalado como pertencente da classe majoritaria
			if(votacao == 0){
				double[] classeMajor = obterClasseMajoritaria(dadosTeste);
				if(classeMajor[0] == dadosTeste.classAttribute().indexOfValue(classePositiva))
					votacao = 1;
				else
					votacao = -1;
			}
			//Exemplo votado como positivo
			if(votacao>0){
				//Se o valor real do exemplo for positivo
				if(temp.classValue() == dadosTeste.classAttribute().indexOfValue(classePositiva)){
					confusao.tp++;
				} else
					confusao.fp++;
			} else {
				if(votacao<0){
//					Se o valor real do exemplo for negativo
					if(temp.classValue() == dadosTeste.classAttribute().indexOfValue(classeNegativa)){
						confusao.tn++;
					} else {
						confusao.fn++;
					}
				} 
			}
		}
	}
	
	/**
	 * Método que recebe como parâmetro um conjunto de regras e retira deste conjunto as regras 
	 * cujo o erro e maior que a porcentagem da class majortaria
	 * @param regras Regras de entrada
	 * @param porcentageClasseMajoritaria Procentagem da classe majoritária
	 * @return Regras filtradas
	 */
	public ArrayList<Regra> retirarRegrasRuins(ArrayList<Regra> regras, double porcentageClasseMajoritaria){
		ArrayList<Regra> novasRegras = new ArrayList<Regra>();
		for (Iterator iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			double erro = regra.getErro();
			if(erro<porcentageClasseMajoritaria)
				novasRegras.add(regra);
		}
		return novasRegras;
	}
	
	/**
	 * Método que percorre a base e verifica quantos exemplos não foram cobertos pelas regras geradas.
	 * @param dadosTeste
	 */
	public void contarExemplosNaoCobertos(Instances dados){
		int cobertos = 0;
		for(int i = 0; i<dados.numInstances();i++){
			Instance exemplo = dados.instance(i);
			int x = 0;
			for (Iterator iter = regras.iterator(); iter.hasNext();) {
				Regra r = (Regra) iter.next();
				boolean b = r.compararCorpo(exemplo.toDoubleArray());
				if(b){
					cobertos++;
					x = 1;
					break;
				} 	
			}
			if(x==0)
				System.out.println("Aquii");
		}
		
		System.out.println("Cobertos: " + cobertos );
		System.out.println("Não cobertos: " + (dados.numInstances() - cobertos) );
		
	}
	
	/**
	 * Método que apaga todas as listas e prepara o algoritmo para próxima execução
	 *
	 */
	public void apagarListas(){
		regras.clear();
		if(paretoPos!=null)
			paretoPos.getRegras().clear();
		if(paretoNeg !=null)
			paretoNeg.getRegras().clear();
		dados = null;
	}
	
	/**
	 * Método que calcula a média dos valores do objetivos para todas as regras da fronteira
	 * @return
	 */
	public double[] obterMediaValoresObjetivos(){
		double[] somaValores = new double[objetivos.size()];
		for (Iterator iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			double valores[] = regra.getValoresObjetivos();
			for (int i = 0; i < valores.length; i++) {
				somaValores[i] += valores[i];
			}
		}
		
		for (int i = 0; i < somaValores.length; i++) {
			somaValores[i] = somaValores[i]/regras.size();
		}
		
		return somaValores;
	}
	
	
	/**
	 * Método que recebe como paramentros as informacoes dos atributos e gera uma regra aleatoria
	 * @deprecated Please now use gerarRegraAleatoriaProporcional()
     * @see gerarRegraAleatoriaProporcional()
	 * @param atributos Enumeration contendo os atributos
	 * @param classAttribute Objeto do tipo Attribute referente a classe
	 * @param numAtributos Número total de atributos
	 * @param classe String contendo o nome da classe da regra que sera gerada
	 * @return Nova regra gerada
	 */
	public Regra gerarRegraAleatoria(Enumeration atributos, Attribute classAttribute, int numAtributos, int classe){
		Regra regra = new Regra(objetivos);
		regra.corpo = new Atributo[numAtributos-1];
				
		int i = 0;
		while(atributos.hasMoreElements()){
			Attribute att = (Attribute)atributos.nextElement();
			if(att.isNominal())
				regra.corpo[i] = new AtributoNominal(true, att, i);
			else{
				double[] limites = obterMaximoMinimoAtributo(att);
				regra.corpo[i] = new AtributoNumerico(true, att, i, limites[0], limites[1]);
			}
			++i;
		}
		
		double temp = Math.random();
		int n = (int)((temp*100) % regra.corpo.length);
		if(n == 0)
			n = 1;
		
		preencherRegraAleatoria(n, regra);
		
		while(regra.isEmpty()){
			preencherRegraAleatoria(n, regra);
		}
		
		if(classe == 0)
			regra.cabeca = classAttribute.indexOfValue(classePositiva);
		else
			regra.cabeca = classAttribute.indexOfValue(classeNegativa);
		regra.classe = classAttribute;
		regra.getNumAtributosNaoVazios();
		return regra;
	}
	
	/**
	 * Método que gera regras iniciais aleatórias de acordo com a distribuição dos valores na base
	 * @param atributos Informações sobre os atributos da base
	 * @param classAttribute Atributo classe
	 * @param numAtributos Número de atributos
	 * @param classe Classe da regra gerada
	 * @return A regra com seus valores iniciais gerados
	 */
	public Regra gerarRegraAleatoriaProporcional(Enumeration atributos, Attribute classAttribute, int numAtributos, int classe){
		Regra regra = new Regra(objetivos);
		regra.corpo = new Atributo[numAtributos-1];
		
		preencherDistribuicaoValores();
		
		int i = 0;
		while(atributos.hasMoreElements()){
			Attribute att = (Attribute)atributos.nextElement();
			if(att.isNominal())
				regra.corpo[i] = new AtributoNominal(true, att, i);
			else{
				double[] limites = obterMaximoMinimoAtributo(att);
				regra.corpo[i] = new AtributoNumerico(true, att, i, limites[0], limites[1]);
			}
			++i;
		}
		
		
		preencherRegraAleatoriaProporcional(regra);
		
		while(regra.isEmpty()){
			preencherRegraAleatoriaProporcional(regra);
		}
		
		if(classe == 0)
			regra.cabeca = classAttribute.indexOfValue(classePositiva);
		else
			regra.cabeca = classAttribute.indexOfValue(classeNegativa);
		regra.classe = classAttribute;
		regra.getNumAtributosNaoVazios();
		return regra;
	}
	
	/**
	 * Preenche n posicoes da regra aleatoriamente
	 * @param n Numero de posicoes a serem preenchidas
	 * @param regra Regra a ser preenchida
	 */
	public void preencherRegraAleatoria(int n, Regra regra){
		for(int i = 0; i<n; i++){
			double temp = Math.random();
			int pos = (int)((temp*100) % regra.corpo.length);
			Atributo atributo = regra.corpo[pos];
			if(atributo.isNominal())
				regra.preencherAtributoNominalAleatorio(pos, atributo);
			else{
				regra.preencherAtributoNumericoAleatorio(pos, atributo);
			}
		}
	}
	
	/**
	 * Método que preenche todos os atributos da regra aleatoriamente, proporcional à distribuição dos
	 * valores na base.
	 * @param regra Regra a ser preenchida
	 */
	public void preencherRegraAleatoriaProporcional(Regra regra){
		int num = 0;
		for(int i = 0; i<regra.corpo.length; i++){
			Atributo atributo = regra.corpo[i];
			if(atributo.isNominal())
				regra.preencherAtributoNominalAleatorioProporcional(i, atributo, distribuicaoAtributos[num++]);
			else{
				regra.preencherAtributoNumericoAleatorio(i, atributo);
			}
		}
	}
	
/**
 * Método principal que executa o algoritmo passado como parametro
 * @param nomeBase Base de dados da execução
 * @param caminhoBase Caminho da base de dados
 * @param nomeMetodo Nome método que será executado
 * @param cPositiva Índice da classe postiva da base
 * @param cNegativa Índice da classe postiva da base
 * @param numFolds Número de folds da base
 * @param numExec Número de execuções
 * @param dirResultado Nome do diretório onde o resultado será guardado
 * @param AUC Boolean que indica se só irá ocorrer a etapa de avaliação das regras geradas
 * @param verbose Indica se haverá a impressão do andamento da execução ou não
 * @param votacao Contém o nome do métodod e votaçaõ utilizado (simples, confidence, laplace, ordenacao)
 * @param selecao Indica se as regras finais vão ser somente as regras que votaram na etapa de teste
 * @throws Exception Lança execuçãoc caso haja algum erro nos arquivos de entrada ou saída.
 */
	public void executarFolds(String nomeBase, String caminhoBase, String nomeMetodo, int cPositiva, int cNegativa, int numFolds, int numExec, String dirResultado, boolean AUC, boolean verbose, String votacao, boolean selecao/*, String[] objetivos*/)  throws Exception{
		dadosExperimento = new DadosExperimentos();
		dadosExperimento.nomeBase = nomeBase;
		dadosExperimento.metodo = nomeMetodo;
		String caminhoDir = System.getProperty("user.dir");
		
		//Arquivo de redundância que salva as informações das execuções durante o processo. 
		//Evita perder as informações se houver um problema na execução.
		String caminhoTemp = caminhoDir + "/resultados/" +nomeMetodo +"/" + dirResultado + "/"; 
		File dir = new File(caminhoTemp);
		dir.mkdirs();
		String arquivoTemp = caminhoTemp + "temp_" +nomeBase+ ".txt";
		PrintStream psTemp = new PrintStream(arquivoTemp);
		
		for(int j = 0; j<numExec; j++){
			
			String arquivoLog = caminhoDir + "/resultados/" +nomeMetodo +"/" + dirResultado + "/" + nomeBase + "" + j +"/" +nomeBase + "" + j + ".log";			
			String diretorio = caminhoDir +  "/resultados/" +nomeMetodo +"/" + dirResultado + "/" +nomeBase + "" +j+"/";
			dir = new File(diretorio);
			dir.mkdirs();
			psLog = new PrintStream(arquivoLog);
			
		
			
			System.out.println("Inicio: " + Calendar.getInstance().getTime());
			System.out.println("Base: " + nomeBase);
			System.out.println("Caminho da Base: " + caminhoBase);
			System.out.println("Execucao: " + j);
			psLog.println("Inicio: " + Calendar.getInstance().getTime());
			psLog.println("Base: " + nomeBase);
			psLog.println("Caminho da Base: " + caminhoBase);
			psLog.println("Inicio da execucao");
			
			
			this.verbose = verbose;
			
			setVotacao(votacao);
			
			
			ArrayList<Regra> regrasFinais = new ArrayList<Regra>();
			for(int i = 0; i<numFolds; i++){
				System.out.println("Fold: "+ i);
				numFold = i;
				String arquivoTreinamento = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_data.arff";
				System.out.println("Base de Treinamento: "+ arquivoTreinamento);
				psLog.println("Base de Treinamento: "+ arquivoTreinamento);
				carregarInstancias(arquivoTreinamento, cPositiva, cNegativa);
				obterRegras(cPositiva, cNegativa);
				
				
				System.out.println("Numero de Instancias: " + dados.numInstances());
				psLog.println("Numero de Instancias: " + dados.numInstances());
				System.out.println("Numero de Regras: " + regras.size());
				psLog.println("Numero de Regras: " + regras.size());

				String arquivoTeste = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_test.arff";
				
				if(AUC){
					if(regras.isEmpty()){
						System.out.println("Nenhuma regra encontrada");
						psLog.println("Nenhuma regra encontrada");
					}
					else{
						
						Reader reader = new FileReader(arquivoTeste);
						Instances dadosTeste = new Instances(reader);
						dadosTeste.setClassIndex(dadosTeste.numAttributes()-1);
						System.out.println("Numero de Instancias de Teste: " + dadosTeste.numInstances());
						psLog.println("Numero de Instancias de Teste: " + dadosTeste.numInstances());
						
						
						confusao = new MatrizConfusao();
						preencherMatrizConfusao(confusao, dadosTeste, regras);		

						
						double a = obterAUC(dadosTeste);
						
						System.out.println("AUC: " + a);
						psLog.println("AUC: " + a);
						
						DadosExecucao fold = new DadosExecucao(nomeBase, i, j, a, regras.size(), confusao);					
						
						dadosExperimento.addFold(fold);
						psTemp.println(fold);
						
						String arquivoRegras = "resultados/" + nomeMetodo + "/" + dirResultado + "/" + nomeBase+ "" + j +"/regras_" + nomeBase + "" + j + "_" + i + ".txt";
						PrintStream psRegras = new PrintStream(arquivoRegras);
						
						//Grava as regras num arquivo texto
						for (Iterator iter = regras.iterator(); iter.hasNext();) {
							Regra regra = (Regra) iter.next();
							if(!selecao){
								psRegras.println(regra);
								if(!regrasFinais.contains(regra)){
									regrasFinais.add(regra);
								}
							} else{
								if(regra.votou){
									psRegras.println(regra);
									if(!regrasFinais.contains(regra))
										regrasFinais.add(regra);
								}
							}
						}
					}
				} else{
					for (Iterator iter = regras.iterator(); iter.hasNext();) {
						Regra regra = (Regra) iter.next();
						regrasFinais.add(regra);
					}
				}
				System.out.println();
				psLog.println();
				apagarListas();
			}
			
			String arquivoRegrasFinais = "resultados/" + nomeMetodo + "/" + dirResultado + "/" + nomeBase + "" + j +"/regras_" + nomeBase + "" + j + ".txt";
			PrintStream psRegras = new PrintStream(arquivoRegrasFinais);
			for (Iterator iter = regrasFinais.iterator(); iter.hasNext();) {
				Regra regra = (Regra) iter.next();
				psRegras.println(regra);
			}
			System.out.println("Fim: " + Calendar.getInstance().getTime());
			psLog.println("Fim: " + Calendar.getInstance().getTime());
		}
		
		if(AUC){
			String diretorio = caminhoDir +  "/resultados/" + nomeMetodo +"/" + nomeBase +"/";
			String arquivoResult = diretorio + "/resultado_" + nomeBase +".txt";
			System.out.println("Resultado gerado em: " + arquivoResult);
			
			PrintStream ps = new PrintStream(arquivoResult);
			dadosExperimento.calcularMediaAreasPrecisaoNumRegras();
			dadosExperimento.calcularDesvioPadrao();
			
			System.out.println(dadosExperimento);			
			ps.println(dadosExperimento);
		
		
			dadosExperimento.gerarArquivosMedidas(diretorio,nomeMetodo+"_"+ nomeBase + "_medidas.txt",nomeMetodo+"_"+ nomeBase + "_comandos.txt", nomeMetodo+"_"+ nomeBase + "_confusao.txt");
		}
	}

private void setVotacao(String votacao) {
	if(votacao.equals("confidence"))
		this.metodoVotacao = new VotacaoConfidence();
	else{
		if(votacao.equals("ordenacao"))
			this.metodoVotacao = new VotacaoConfidenceLaplaceOrdenacao();
		else{
			if(votacao.equals("laplace"))
				this.metodoVotacao = new VotacaoConfidenceLaplace();
			else{
				this.metodoVotacao = new VotacaoSimples();
			}
		}
		
	}
}
	
	/**
	 * Uma só execução
	 * @deprecated Please now use executarFolds(String, String, String, int, int ,int, String, String, boolean, boolean, String, boolean, String[])
     * @see executarFolds(String, String, String, int, int ,int, String, String, boolean, boolean, String, boolean, String[])
	 * @param nomeBase
	 * @param caminhoBase
	 * @param nomeMetodo
	 * @param cPositiva
	 * @param cNegativa
	 * @param numFolds
	 * @param verbose
	 * @param votacao
	 * @throws Exception
	 */
	public void executarFolds(String nomeBase, String caminhoBase, String nomeMetodo, int cPositiva, int cNegativa, int numFolds, boolean verbose, String votacao, boolean selecao)  throws Exception{
		String caminhoDir = System.getProperty("user.dir");
		String arquivoLog = caminhoDir + "/resultados/" +nomeMetodo +"/" +  nomeBase  +"/" +nomeBase  + ".log";
		String diretorio = caminhoDir +  "/resultados/" +nomeMetodo +"/" + nomeBase +"/";
		File dir = new File(diretorio);
		dir.mkdirs();
		psLog = new PrintStream(arquivoLog);
		System.out.println("Inicio: " + Calendar.getInstance().getTime());
		System.out.println("Base: " + nomeBase);
		System.out.println("Caminho da Base: " + caminhoBase);
		System.out.println("Inicio da execucao");
		psLog.println("Inicio: " + Calendar.getInstance().getTime());
		psLog.println("Base: " + nomeBase);
		psLog.println("Caminho da Base: " + caminhoBase);
		psLog.println("Inicio da execucao");
		dadosExperimento = new DadosExperimentos();
		dadosExperimento.nomeBase = nomeBase;
		
		this.verbose = verbose;
		
		if(votacao.equals("confidence"))
			this.metodoVotacao = new VotacaoConfidence();
		else{
			if(votacao.equals("ordenacao"))
				this.metodoVotacao = new VotacaoConfidenceLaplaceOrdenacao();
			else{
				if(votacao.equals("laplace"))
					this.metodoVotacao = new VotacaoConfidenceLaplace();
				else{
					this.metodoVotacao = new VotacaoSimples();
				}
			}
			
		}
		
		
		ArrayList<Regra> regrasFinais = new ArrayList<Regra>();
		for(int i = 0; i<numFolds; i++){
			System.out.println("Fold: "+ i);	
			numFold = i;
			String arquivoTreinamento = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_data.arff";
			System.out.println("Base de Treinamento: "+ arquivoTreinamento);
			psLog.println("Base de Treinamento: "+ arquivoTreinamento);
			carregarInstancias(arquivoTreinamento, cPositiva, cNegativa);
			obterRegras(cPositiva, cNegativa);	
			String arquivoTeste = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_test.arff";
			
			if(regras.isEmpty()){
				System.out.println("Nenhuma regra encontrada");
			    psLog.println("Nenhuma regra encontrada");
			}
			else{

				Reader reader = new FileReader(arquivoTeste);
				Instances dadosTeste = new Instances(reader);
				dadosTeste.setClassIndex(dadosTeste.numAttributes()-1);
				System.out.println("Numero de Instancias de Teste: " + dadosTeste.numInstances());
				psLog.println("Numero de Instancias de Teste: " + dadosTeste.numInstances());
				
				double a = obterAUC(dadosTeste);
				
				
				System.out.println("AUC: " + a);
				psLog.println("AUC: " + a);
				DadosExecucao fold = new DadosExecucao(nomeBase, i, 0, a, regras.size(), confusao);					
				
				dadosExperimento.addFold(fold);

				
				String arquivoRegras = "resultados/" + nomeMetodo + "/" + nomeBase+ "/regras_" + nomeBase  + "_" + i + ".txt";
				PrintStream psRegras = new PrintStream(arquivoRegras);
				
				for (Iterator iter = regras.iterator(); iter.hasNext();) {
					Regra regra = (Regra) iter.next();
					if(!selecao){
						psRegras.println(regra);
						if(!regrasFinais.contains(regra)){
							regrasFinais.add(regra);
						}
					} else{
						if(regra.votou){
							psRegras.println(regra);
							if(!regrasFinais.contains(regra))
								regrasFinais.add(regra);
						}
					}
				}
			
				
				
			}
			System.out.println();
			psLog.println();
			apagarListas();
		}
		
		String arquivoResult = "resultados/" + nomeMetodo + "/" + nomeBase  +"/resultado_" + nomeBase  +".txt";
		System.out.println("Resultado gerado em: " + arquivoResult);
		
		PrintStream ps = new PrintStream(arquivoResult);
		dadosExperimento.calcularMediaAreasPrecisaoNumRegras();
		dadosExperimento.calcularDesvioPadrao();
		
		System.out.println(dadosExperimento);			
		ps.println(dadosExperimento);
		
		String arquivoRegrasFinais = "resultados/" + nomeMetodo + "/" + nomeBase  +"/regras_" + nomeBase  + ".txt";
		PrintStream psRegras = new PrintStream(arquivoRegrasFinais);
	    for (Iterator iter = regrasFinais.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			psRegras.println(regra);
		}
	    System.out.println("Fim: " + Calendar.getInstance().getTime());
	    psLog.println("Fim: " + Calendar.getInstance().getTime());
	}
	
	
	/**
	 * Método que divide a execução dos em folds em diversas partições, junta todas as regras geradas e testa no arquivo de test.
	 * Já esta divindo o base de treinamento em partes iguais, executando o algoritmo para todas as partições, juntando as regras
	 * deixando apenas as regras não dominadas. Falta a parte do cálculo da AUC no arquivo de teste.
	 * @param nomeBase
	 * @param caminhoBase
	 * @param nomeMetodo
	 * @param cPositiva
	 * @param cNegativa
	 * @param numFolds
	 * @param indice
	 * @param dirResultado
	 * @param AUC
	 * @param numParticoes
	 * @throws Exception
	 */
	
	public void executarParalelo(String nomeBase, String caminhoBase, String nomeMetodo, int cPositiva, int cNegativa, int numFolds, int numExec, String dirResultado, boolean AUC , boolean verbose, String votacao, boolean selecao, int numParticoes)  throws Exception{
		
		
		dadosExperimento = new DadosExperimentos();
		dadosExperimento.nomeBase = nomeBase;
		dadosExperimento.metodo = nomeMetodo;
		String caminhoDir = System.getProperty("user.dir");
		
		//Arquivo de redundância que salva as informações das execuções durante o processo. 
		//Evita perder as informações se houver um problema na execução.
		String caminhoTemp = caminhoDir + "/resultados/" +nomeMetodo +"/" + dirResultado + "/"; 
		File dir = new File(caminhoTemp);
		dir.mkdirs();
		String arquivoTemp = caminhoTemp + "temp_" +nomeBase+ ".txt";
		PrintStream psTemp = new PrintStream(arquivoTemp);
		
		for(int j = 0; j<numExec; j++){
			
			String arquivoLog = caminhoDir + "/resultados/" +nomeMetodo +"/" + dirResultado + "/" + nomeBase + "" + j +"/" +nomeBase + "" + j + ".log";			
			String diretorio = caminhoDir +  "/resultados/" +nomeMetodo +"/" + dirResultado + "/" +nomeBase + "" +j+"/";
			dir = new File(diretorio);
			dir.mkdirs();
			psLog = new PrintStream(arquivoLog);


			System.out.println("Inicio: " + Calendar.getInstance().getTime());
			System.out.println("Base: " + nomeBase);
			System.out.println("Caminho da Base: " + caminhoBase);
			System.out.println("Execucao: " + j);
			psLog.println("Inicio: " + Calendar.getInstance().getTime());
			psLog.println("Base: " + nomeBase);
			psLog.println("Caminho da Base: " + caminhoBase);
			psLog.println("Inicio da execucao");


			this.verbose = verbose;

			setVotacao(votacao);


			//ArrayList<Regra> regrasFinais = new ArrayList<Regra>();

			ArrayList<Regra> regrasParticoes =new ArrayList<Regra>();

			
			for(int i = 0; i<numFolds; i++){
				System.out.println("Fold: "+ j+ "-"+ i);
				numFold = i;
				String arquivoTreinamento = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_data.arff";
				System.out.println("Base de Treinamento: "+ arquivoTreinamento);
				psLog.println("Base de Treinamento: "+ arquivoTreinamento);
				carregarInstancias(arquivoTreinamento, cPositiva, cNegativa);
				int tamParticao = dados.numInstances()/numParticoes;
				Instances dadosTreinamentoTotal = new Instances(dados);
				for (int n = 0; n < numParticoes; n++) {
					System.out.println("Partição: " + n);
					int inicio = n*tamParticao;
					int fim = 0;
					if(n+1<numParticoes)
						fim = (n+1)*tamParticao;
					else
						fim = dadosTreinamentoTotal.numInstances();
				
					dados = new Instances(dadosTreinamentoTotal,0);
					for(int z = inicio; z<fim; z++){
						dados.add(dadosTreinamentoTotal.instance(z));
					}

					obterRegras(cPositiva, cNegativa);
					System.out.println("Numero de Instancias: " + dados.numInstances());
					psLog.println("Numero de Instancias: " + dados.numInstances());
					System.out.println("Numero de Regras Partição: " + regras.size());
					psLog.println("Numero de Regras Partição: " + regras.size());
					regrasParticoes.addAll(regras);
					apagarListas();
				}
				
				//Recalcular os objetivos para a base de dados original 
				//preencherMatrizContigencia(regrasParticoes, dadosTreinamentoTotal);
				
				//eliminarRegrasDominadas(regrasParticoes);
				regras.addAll(regrasParticoes);
				regrasParticoes.clear();
				
				System.out.println("\nNumero de Regras Total: " + regras.size());
				psLog.println("\nNumero de Regras Total: " + regras.size());
				

				String arquivoTeste = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_test.arff";
				
				if(AUC){
					if(regras.isEmpty()){
						System.out.println("Nenhuma regra encontrada");
						psLog.println("Nenhuma regra encontrada");
					}
					else{
						
						Reader reader = new FileReader(arquivoTeste);
						Instances dadosTeste = new Instances(reader);
						dadosTeste.setClassIndex(dadosTeste.numAttributes()-1);
						System.out.println("Numero de Instancias de Teste: " + dadosTeste.numInstances());
						psLog.println("Numero de Instancias de Teste: " + dadosTeste.numInstances());
						
						confusao = new MatrizConfusao();
						preencherMatrizConfusao(confusao, dadosTeste, regras);		
						
						double a = obterAUC(dadosTeste);
						
						System.out.println("AUC: " + a);
						psLog.println("AUC: " + a);
						
						DadosExecucao fold = new DadosExecucao(nomeBase, i, j, a, regras.size(), confusao);					
						
						dadosExperimento.addFold(fold);
						psTemp.println(fold);
						
						String arquivoRegras = "resultados/" + nomeMetodo + "/" + dirResultado + "/" + nomeBase+ "" + j +"/regras_" + nomeBase + "" + j + "_" + i + ".txt";
						PrintStream psRegras = new PrintStream(arquivoRegras);
						
						//Grava as regras num arquivo texto
						for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
							Regra regra = (Regra) iter.next();
							if(!selecao)
								psRegras.println(regra);
							 else{
								if(regra.votou)
									psRegras.println(regra);
							}
						}
					}
				} 
				
				System.out.println();
				psLog.println();
				apagarListas();
			}

			System.out.println("Fim: " + Calendar.getInstance().getTime());
			psLog.println("Fim: " + Calendar.getInstance().getTime());
		}
		
		if(AUC){
			String diretorio = caminhoDir +  "/resultados/" + nomeMetodo +"/" + nomeBase +"/";
			String arquivoResult = diretorio + "/resultado_" + nomeBase +".txt";
			System.out.println("Resultado gerado em: " + arquivoResult);
			
			PrintStream ps = new PrintStream(arquivoResult);
			dadosExperimento.calcularMediaAreasPrecisaoNumRegras();
			dadosExperimento.calcularDesvioPadrao();
			
			System.out.println(dadosExperimento);			
			ps.println(dadosExperimento);
		
			dadosExperimento.gerarArquivosMedidas(diretorio,nomeMetodo+"_"+ nomeBase + "_medidas.txt",nomeMetodo+"_"+ nomeBase + "_comandos.txt", nomeMetodo+"_"+ nomeBase + "_confusao.txt");
		}
	}
	
	/**
	 * Método que recebe um conjunto de regras como parâmetro, escolhe as regras não dominadas
	 * e preenche o atributo regras, que são as regras finais geradas pelo algoritmo.
	 * @param regrasTemp Regras à serem refinadas
	 */
	public void eliminarRegrasDominadas(ArrayList<Regra> regrasTemp){
		
		paretoPos = new FronteiraPareto();
		paretoNeg = new FronteiraPareto();
		
		for (Iterator iter = regrasTemp.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			paretoPos.add(regra, classePositiva);
			paretoNeg.add(regra, classeNegativa);
		}
		
		regras.addAll(paretoPos.getRegras());
		regras.addAll(paretoNeg.getRegras());
	}
	
	/**
	 * * @deprecated Método ainda não terminado.
	 * Método que preenche uma matriz com o coeficient de Jaccard de todas as regras
	 * Só deve ser executado quando o array de regras estiver preenchido
	 *
	 */
	public void calcularCoeficienteJaccard(){
	   if(regras.size()>0){
		   coeficienteJaccard = new double[regras.size()][regras.size()];
		   int i = 0;
		   int j = 0;
		   for (Iterator iter = regras.iterator(); iter.hasNext();) {
				Regra r1 = (Regra) iter.next();
				for (Iterator iterator = regras.iterator(); iterator.hasNext();) {
					Regra r2 = (Regra) iterator.next();
					double[] similaridade = medidadeSimilaridade(r1, r2);
					//Calcula do coeficiente de Jaccard
					coeficienteJaccard[i][j] = similaridade[0]/similaridade[1]; 
				}
				i++;
			}
		   
	   }
	}
	

	/**
	 * * @deprecated Método ainda não terminado
	 * Medida de similiradade entre duas regras utilizad no cálculo do coeficiente de Jaccard
	 * @param r1 Regra 1
	 * @param r2 Regra 2
	 * @return Valor da medida de similaridade e o total de exemplos cobertos pelas duas regras
	 */
	public double[] medidadeSimilaridade(Regra r1, Regra r2){
		int uniao = 0;
		int interseccao = 0;
		int r1menosr2 = 0;
		int r2menosr1 = 0;
		
		for(int i =0; i<dados.numInstances(); i++){
			Instance exemplo = dados.instance(i);
			if(r1.cobreCorretamente(exemplo)){
				if(r2.cobreCorretamente(exemplo))
					interseccao++;
				else
					r1menosr2++;
			} else{
				if(r2.cobreCorretamente(exemplo))
					r2menosr1++;
			}
		}
		
		uniao = interseccao + r1menosr2 + r2menosr1;
		
		double similaridade = r1menosr2 + r2menosr1;
		
		double retorno[] = new double[2];
		retorno[0] = similaridade;
		retorno[1] = uniao;
		return retorno;
	}
	
	/**
	 * Método que recebe como parâmetros os objetivos da busca e os instância
	 * @param objetivos Objetivos da busca
	 */
	public void addObjetivos(String[] objs){
		objetivos = new ArrayList<FuncaoObjetivo>();
		if(objs!=null){
			for (int i = 0; i < objs.length; i++) {
				String objetivo = objs[i].toLowerCase();
				if(objetivo.equals("sens")){
					Sensitividade sens = new Sensitividade();
					objetivos.add(sens);
				} else{
					if(objetivo.equals("spec")){
						Especificidade spec = new Especificidade();
						objetivos.add(spec);
					} else{
						if(objetivo.equals("nov")){
							Novidade nov = new Novidade();
							objetivos.add(nov);
						} else{
							if(objetivo.equals("lap")){
								Laplace lap = new Laplace();
								objetivos.add(lap);
							}
						}	
					}	
				}		
			}
		}
		//Caso os objetivos passados como parâmetro não existam é definido como default Sens e Spec
		if(objetivos.size()==0){
			Sensitividade sens = new Sensitividade();
			objetivos.add(sens);
			Especificidade spec = new Especificidade();
			objetivos.add(spec);
		}
	}

	
	
	/**
	 * Método que percorre a base de dados e para cada atributo nominal preenche a distribuição dos valores
	 */
	public void preencherDistribuicaoValores(){
		//Maior número de valores para um atributo na base
		int maxValues = 0;
		int num = 0;
		Enumeration atributos = dados.enumerateAttributes();
		while(atributos.hasMoreElements()){
			Attribute att = (Attribute) atributos.nextElement();
			if(att.isNominal()){
				num++;
				if(att.numValues()>maxValues)
					maxValues = att.numValues();
			}
			
		}
		
		distribuicaoAtributos = new int[num][maxValues];
		
		for(int i = 0; i<dados.numInstances();i++){
			Instance exemplo = dados.instance(i);
			int indice = 0;
			for(int j = 0; j<exemplo.numAttributes(); j++){
				Attribute att = exemplo.attribute(j);
				if(att.isNominal() && att!=dados.classAttribute()){
					double temp = exemplo.value(j);
					if(temp!=Double.NaN) {
						distribuicaoAtributos[indice++][(int) temp]++;
					}
				}
			}
		}
		
	}
	
	/**
	 * * @deprecated Resultado ruim!.
	 * Método que percorre todas as regras e verifica quais regras são iguais nos atributos numéricos
	 * e possuem atributos numéricos que se cruzam.
	 */
	public void mergeRegrasCruzam(ArrayList<Regra> regrasCruzam){
		
		for(int i = 0; i<regrasCruzam.size(); i++){
			Regra r1 = regrasCruzam.get(i);
			for(int j = 0 ; j<regrasCruzam.size();j++){
				Regra r2 = regrasCruzam.get(j);
				boolean m = r1.merge(r2);
				if(m)
					regrasCruzam.remove(r2);
			}
		}
		
	}
	
	/**
	 * Método que preenche o conjunto de regras a partir de um arquivo com as regras com todos os valores
	 * dos atributos separados por virgula 
	 * @param arquivoRegras Arquivo com as regras com todos os atributos separados por virgula
	 * @param dadosOriginais Objeto com as informacoes sobre os atributos da base de dados.
	 */
	/*public void preencherRegrasArquivo(String arquivoRegras, Instances dadosOriginais){
		
		Enumeration<Attribute> atributos = dadosOriginais.enumerateAttributes();
		
		int i = 0;
		while(atributos.hasMoreElements()){
			Attribute att = (Attribute)atributos.nextElement();
			if(att.isNominal())
				regra.corpo[i] = new AtributoNominal(true, att, i);
			else{
				double[] limites = obterMaximoMinimoAtributo(att);
				regra.corpo[i] = new AtributoNumerico(true, att, i, limites[0], limites[1]);
			}
			++i;
		}
		
	}*/



}
