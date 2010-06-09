package principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import kernel.ObterRegras;
import nuvemparticulas.NuvemParticulas;
import apriori.ObterRegrasApriori;

public class Principal {
	
	public String metodo = null;
	public String caminhoBase = null;
	public String nomeBase = null;
	
	public int numExec = 0;
	public int numFolds = 0;
	public int classePositiva = 0;
	public int classeNegativa = 1;
	public boolean auc;
	public boolean combinacao;
	public int numParticoes = 1;
	
	public boolean verbose = false;
	public String votacao = "ordenacao";
	public boolean fronteiraApriori = true;
	public boolean selecaoVotacao = false;
	
	public boolean medoid = false;
	
	public int geracoes = 0;
	public int populacao = 0;
	public int numclasses = 0;
	
	public int numRegras = 0;
	public double minSuporte = 0;
	public double maxSuporte = 1.0;
	public double confianca = 0;
	public double delta = 0.05;
	
	public String[] objetivos = null;
	
	ObterRegras algoritmo = null;
	
	public static void main(String[] args) {	
		Principal principal = new Principal();
		try{			
			principal.carregarArquivoConf(args[0]);
			if(principal.metodo.equals("pso"))
				principal.algoritmo = new NuvemParticulas(principal.geracoes,principal.populacao, principal.objetivos, principal.combinacao);
			else
				principal.algoritmo = new ObterRegrasApriori(principal.numRegras, principal.confianca, principal.minSuporte, principal.maxSuporte, principal.delta, principal.fronteiraApriori, principal.objetivos);					

			if(principal.medoid)
				principal.algoritmo.executarMedoid(principal.nomeBase, principal.caminhoBase, principal.metodo,principal.numclasses,principal.classePositiva,principal.classeNegativa,principal.numFolds,principal.numExec, principal.nomeBase, principal.auc, principal.verbose, principal.votacao,principal.selecaoVotacao);
			else{
				if(principal.numParticoes == 1)
					principal.algoritmo.executarFolds(principal.nomeBase, principal.caminhoBase, principal.metodo,principal.numclasses,principal.classePositiva,principal.classeNegativa,principal.numFolds,principal.numExec, principal.nomeBase, principal.auc, principal.verbose, principal.votacao,principal.selecaoVotacao);
				else
					principal.algoritmo.executarParaleloBagging(principal.nomeBase, principal.caminhoBase, principal.metodo, principal.numclasses,principal.classePositiva,principal.classeNegativa,principal.numFolds,principal.numExec, principal.nomeBase, principal.auc, principal.verbose, principal.votacao,principal.selecaoVotacao, principal.numParticoes);
			}

		} catch (Exception ex) {ex.printStackTrace();}
	}
	
	/**
	 * Método que carregar as informações da execução a partir de um arquivo de configuração
	 * nomebase = Base a ser executada
	 * caminhoBase = Caminho do diretorio da base
	 * metodo = Método que será a base será executada (pso, apriori)
	 * classePositiva = Índice da classe positiva (0 ou 1)  
	 * classeNegativa = Índice da classe positiva (1 ou 0)
	 * numClasses = Número de classes da base
	 * numFolds = Número de folds da base de dados
	 * numExec = Número de execuções do algoritmo
	 * Parametros da nuvem de particulas
	 * geracoes = Número de iterações do problema
	 * populacao = Número inicial de partículas
	 * Parametros do apriori
	 * numRegras = Número máximo de regras gerado pelo algoritmo
	 * confianca = Confianca mínima
	 * minSuporte = Suporte mínimo
	 * maxSuporte = Suporte máximo
	 * delta = Taxa de variação do suporte
	 * auc = Executa ou não o cálculo da AUC
	 * @param nomeArquivo
	 * @throws IOException
	 */
	public void carregarArquivoConf(String nomeArquivo)throws IOException{
		Reader reader = new FileReader(nomeArquivo);
		BufferedReader buff = new BufferedReader(reader);
		while(buff.ready()){
			String linhaString = buff.readLine(); 
			String linha[] = linhaString.split("=");
			if(linha.length!=2){
				System.err.println("Erro no arquivo de configuração! Linha: " + linhaString);
				System.exit(0);
			}
			String tag = linha[0].trim().toLowerCase();
			String valor = linha[1].trim();
			if(tag.equals("nomebase"))
				nomeBase = valor;
			if(tag.equals("caminhobase"))
				caminhoBase = valor;
			if(tag.equals("metodo"))
				metodo = valor;
			if(tag.equals("classepostiva"))
				classePositiva = new Integer(valor).intValue();
			if(tag.equals("classenegativa"))
				classeNegativa = new Integer(valor).intValue();
			if(tag.equals("geracoes"))
				geracoes = new Integer(valor).intValue();
			if(tag.equals("populacao"))
				populacao = new Integer(valor).intValue();
			if(tag.equals("numfolds"))
				numFolds = new Integer(valor).intValue();
			if(tag.equals("numparticoes"))
				numParticoes = new Integer(valor).intValue();
			if(tag.equals("numexec"))
				numExec = new Integer(valor).intValue();
			if(tag.equals("numclasses"))
				numclasses = new Integer(valor).intValue();
			if(tag.equals("numregras"))
				numRegras = new Integer(valor).intValue();
			if(tag.equals("confianca"))
				confianca = new Double(valor).doubleValue();
			if(tag.equals("minsuporte"))
				minSuporte = new Double(valor).doubleValue();
			if(tag.equals("maxSuporte"))
				maxSuporte = new Double(valor).doubleValue();
			if(tag.equals("delta"))
				delta = new Double(valor).doubleValue();
			if(tag.equals("auc")){
				if(valor.equals("sim"))
					auc = true;
				else
					auc = false;
			}
			
			if(tag.equals("verbose")){
				if(valor.equals("sim"))
					verbose = true;
				else
					verbose = false;
			}
			if(tag.equals("votacao")){
				votacao = valor;
			}
			
			if(tag.equals("selecao")){
				if(valor.equals("sim"))
					selecaoVotacao = true;
				else
					selecaoVotacao = false;
			}
			
			if(tag.equals("fronteiraapriori")){
				if(valor.equals("sim"))
					fronteiraApriori = true;
				else
					fronteiraApriori = false;
			}
			
			if(tag.equals("objetivos")){
				objetivos = valor.split(";");
			}
			
			if(tag.equals("medoid")){
				if(valor.equals("sim"))
					medoid = true;
				else
					medoid = false;
			}
			
			if(tag.equals("combinacao")){
				if(valor.equals("sim"))
					combinacao = true;
				else
					combinacao = false;
			}

			
		}
		
	}
	


}
