package principal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import nuvemparticulas.NuvemParticulas;
import kernel.ObterRegras;
import apriori.ObterRegrasApriori;

public class Principal {
	
	public String metodo = null;
	public String caminhoBase = null;
	public String nomeBase = null;
	
	public int numExec = 1;
	public int numFolds = 1;
	public int classePositiva = 0;
	public int classeNegativa = 1;
	public boolean auc = true;
	public boolean verbose = true;
	
	public String votacao = "confidence";
	public boolean selecaoVotacao = false;
	public boolean fronteiraApriori = true;
	
	public int geracoes = 50;
	public int populacao = 500;
	
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
			if(args.length==1){
				principal.carregarArquivoConf(args[0]);
				if(principal.metodo.equals("pso"))
					principal.algoritmo = new NuvemParticulas(principal.geracoes,principal.populacao, principal.objetivos);
				else
					principal.algoritmo = new ObterRegrasApriori(principal.numRegras, principal.confianca, principal.minSuporte, principal.maxSuporte, principal.delta, principal.fronteiraApriori, principal.objetivos);					
			}	
			else{
				principal.metodo = args[0];
				principal.nomeBase = args[1];
				principal.caminhoBase = args[2];
				principal.numFolds = 10;
				principal.classePositiva = 0;
				principal.classeNegativa = 1;
				principal.numExec = new Integer(args[5]).intValue();
				principal.auc = true;
				if(principal.metodo.equals("apriori")){
					principal.numRegras = new Integer(args[3]).intValue();
					principal.algoritmo = new ObterRegrasApriori(principal.numRegras,principal.confianca, principal.minSuporte, principal.maxSuporte, principal.delta, principal.fronteiraApriori, principal.objetivos);
				} else{
					if(principal.metodo.equals("pso")){
						principal.geracoes = new Integer(args[3]).intValue();
						principal.populacao = new Integer(args[4]).intValue();
						principal.algoritmo = new NuvemParticulas(principal.geracoes,principal.populacao, principal.objetivos);
					}
				}
			}		
			
			if(!principal.nomeBase.equals("all")){
				/*NuvemParticulas nuvem = (NuvemParticulas) principal.algoritmo;
				 if(principal.metodo.equals("pso_apriori"))
				 prinnuvem.iniciarRepositorios(nomeBase, caminhoBase,10, 0.2, 0.4, new Integer(args[6]).intValue());
				 */
				
				principal.algoritmo.executarFolds(principal.nomeBase, principal.caminhoBase, principal.metodo,principal.classePositiva,principal.classeNegativa,principal.numFolds,principal.numExec, principal.nomeBase, principal.auc, principal.verbose, principal.votacao,principal.selecaoVotacao);
				
			}
			//Executa mais de uma base por vez
			else{
				Reader reader = new FileReader("bases.txt");
				BufferedReader buff = new BufferedReader(reader);
				String[] bases = buff.readLine().split(",");
				
				for (int i = 0; i < bases.length; i++) {
					String nBase = bases[i];
					
					principal.algoritmo.executarFolds(nBase, principal.caminhoBase, principal.metodo,principal.classePositiva,principal.classeNegativa,principal.numFolds,principal.numExec, nBase, principal.auc, principal.verbose, principal.votacao, principal.selecaoVotacao);
					
				}
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
	 * vebose = Imprime ou nao o andamento das geracoes.
	 * votacao = escolhe o método de votacao das regras. simples, confidence, laplace, ordenacao
	 * selecaoVotacao = retorna somente as regras que votaram na escolha da AUC.
	 * fronteiraApriori = flag que define se as regras geradas pelo apriori serão reduzidas somente 
	 * à fronteira de pareto.
	 * objetivos = Objetivos da busca separados por ";" (spec,sens,nov,lap)
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
			if(tag.equals("numexec"))
				numExec = new Integer(valor).intValue();
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
			
			
			
		}
		
	}
	


}
