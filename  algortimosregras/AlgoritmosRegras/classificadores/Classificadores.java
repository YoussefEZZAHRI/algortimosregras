package classificadores;

import java.io.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import kernel.DadosExperimentos;
import kernel.DadosExecucao;
import kernel.MatrizConfusao;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.NNge;
import weka.classifiers.trees.J48;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class Classificadores {
	
	
	public void treinarRede(Instances dados, Classifier classif) throws Exception{
		classif.buildClassifier(dados);
	}
	
	public double[] avaliarModelo(Instances dadosTeste, Classifier classif, int classIndex) throws Exception{
		
		Evaluation eval = new Evaluation(dadosTeste);
		eval.evaluateModel(classif,dadosTeste);
		
		//ThresholdCurve tc = new ThresholdCurve();
		// Instances result = tc.getCurve(eval.predictions(), classIndex);
		
		double auc = eval.areaUnderROC(classIndex);
		/*double fmeasure = eval.fMeasure(classIndex);
		double precision = eval.precision(classIndex);
		double recall = eval.recall(classIndex);
		*/
		
		
		//double accuracy = (TP+TN)/(TP+TN+FP+FN);
		
		double[] retorno = new double[5];
		retorno[0] = auc;
		retorno[1] = eval.numTruePositives(classIndex);
		retorno[2] = eval.numFalsePositives(classIndex);
		retorno[3] = eval.numTrueNegatives(classIndex);
		retorno[4] = eval.numFalseNegatives(classIndex);
		return retorno;
		

		
		
	}
	


	
/*
	private void gerarComando(String nomeBase, String metodo, ArrayList<Double> aucs) throws FileNotFoundException {
		PrintStream ps = new PrintStream(metodo+"_"+nomeBase+".txt");
		
		StringBuffer comando = new StringBuffer();
		comando.append(metodo);
		comando.append(nomeBase.substring(0,3) + "<-c(");
		for (Iterator iter = aucs.iterator(); iter.hasNext();) {
			Double auc = (Double) iter.next(); 
			ps.println(auc.toString().replace(".",","));
			comando.append(auc + ",");
		}
		
		comando.deleteCharAt(comando.length()-1);
		
		comando.append(")");
		
		ps.println();
		ps.println(comando);
	}
*/
	/**
	 * Método que executar um classificador passado como parâmetro e retorna a o valor da AUC de cada execucao para cada fold
	 * @param nomeBase Nome da base de dados
	 * @param caminhoBase Caminho da base de dados
	 * @param numFolds Número de folds da base de dados
	 * @param numExec Número de execução do algoritmo
	 * @param metodo Método que sera utilizado (rn, bayes, naive ou smo)
	 * @return Coleção com os valores de AUC das execuções
	 * @throws Exception
	 */
	private  void executarClassificacao(String nomeBase, String caminhoBase, int numFolds, int numExec, String metodo) throws Exception  {
		
		RBFKernel rbf = new RBFKernel();
		MultilayerPerceptron mlp = null;

		SMO smo = null;
		J48 j48 = null;
	
		
		
		
		Classifier classif = null;
		
		DadosExperimentos dadosBase = new DadosExperimentos();
		dadosBase.nomeBase = nomeBase;
		dadosBase.metodo = metodo;
		for(int j = 0; j<numExec;j++){
			
			if(metodo.equals("rn")){
				mlp = new MultilayerPerceptron();
				mlp.setRandomSeed(System.currentTimeMillis());
				classif = mlp;
			}
			if(metodo.equals("bayes")){
				classif = new BayesNet();
			}
			if(metodo.equals("naive")){
				classif =  new NaiveBayes();
			}
			if(metodo.equals("c45")){
				classif = new J48();
			}
			if(metodo.equals("c45np")){
				j48 = new J48();
				j48.setUnpruned(true);
				classif = j48;
			}
			if(metodo.equals("ripper")){
				classif = new JRip();
			}
			if(metodo.equals("nnge")){ 
				classif = new NNge();;
			}
			if(metodo.equals("smo")){
				smo = new SMO();
				smo.setKernel(rbf);
				SelectedTag tag = new SelectedTag(smo.FILTER_NONE, smo.TAGS_FILTER);
				smo.setFilterType(tag);
				smo.setRandomSeed((int)System.currentTimeMillis());
				classif = smo;
			}
			
			System.out.println("Execucao: " + j);
			for(int i = 0; i<numFolds; i++){
				System.out.println("Fold: " + i);
				String arquivoTreinamento = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_data.arff";
				System.out.println("Base de treinamento: " + arquivoTreinamento);
				
				Reader reader = new FileReader(arquivoTreinamento);
				Instances dados = new Instances(reader);
				dados.setClassIndex(dados.numAttributes()-1);
				System.out.println("Instancias de treinamento: " + dados.numInstances());
				
				treinarRede(dados, classif);
				
				String arquivoTeste = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_test.arff";
				reader = new FileReader(arquivoTeste);
				Instances dadosTeste = new Instances(reader);
				System.out.println("Base de teste: " + arquivoTeste);
				System.out.println("Instancias de teste: " + dadosTeste.numInstances());
				dadosTeste.setClassIndex(dadosTeste.numAttributes()-1);
				
				int classIndex = 0;
				double medidas[] = avaliarModelo(dadosTeste, classif,classIndex);
				
				MatrizConfusao confusao = new MatrizConfusao();
				confusao.tp = medidas[1];
				confusao.fp = medidas[2];
				confusao.tn = medidas[3];
				confusao.fn = medidas[4];
				
				DadosExecucao fold = new DadosExecucao(nomeBase, i, j, medidas[0], 0, confusao);
				
				dadosBase.addFold(fold);
				
				
			}
			
			String caminhoDir = System.getProperty("user.dir");
			
			String diretorio = caminhoDir +  "/resultados/" + metodo +"/" + nomeBase +"/";
			File dir = new File(diretorio);
			dir.mkdirs();
			
			 
			dadosBase.gerarArquivosMedidas(diretorio,metodo+"_"+ nomeBase + "_medidas.txt",metodo+"_"+ nomeBase + "_comandos.txt", metodo+"_"+ nomeBase + "_confusao.txt");
		}
		
	}
	
	public static void main(String[] args) {
		String nomeBase = args[0];
		String caminhoBase = args[1];
		
		int numFolds = new Integer(args[2]);
		int numExec = new Integer(args[3]);
		
		String metodo = args[4];
		
		/*String nomeBase = "kc1_class_defeito_numerico";
		String caminhoBase = "C:/Andre/bases/book/";
		
		int numFolds = 10;
		int numExec = 1;*/
		
		Classificadores c = new Classificadores();
		try{	
			c.executarClassificacao(nomeBase, caminhoBase, numFolds, numExec, metodo);

		} catch (Exception ex){ex.printStackTrace();}
		
		
			
	}
	
}
