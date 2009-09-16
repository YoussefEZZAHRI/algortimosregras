package principal;

import indicadores.GD;
import indicadores.Hipervolume;
import indicadores.PontoFronteira;
import indicadores.Spread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import kernel.AlgoritmoAprendizado;
import kernel.Avaliacao;
import kernel.genetic.NSGA2;
import kernel.misa.MISA;
import kernel.nuvemparticulas.SMOPSO;
import kernel.nuvemparticulas.SigmaMOPSO;

import problema.DTLZ1;
import problema.DTLZ2;
import problema.DTLZ3;
import problema.DTLZ4;
import problema.DTLZ5;
import problema.DTLZ6;
import problema.Problema;
import solucao.ComparetorObjetivo;
import solucao.Solucao;

public class Principal {
	
	public String alg = null;
	public String prob = null;
	
	public Problema problema;
	
	public AlgoritmoAprendizado algoritmo = null;
	
	public int geracoes;
	public int populacao;
	public int numExec;
	public int m;
	public int n;
	
	public boolean modificar;
	public double S;
	
	public double maioresObjetivos[];
	
	public double limitesHiper[];
	public String[] maxmimObjetivos;
	
	public static void main(String[] args) {	
		Principal principal = new Principal();
		try{			
			principal.carregarArquivoConf(args[0]);
			principal.setProblema();
			if(principal.alg.equals("sigma"))
				principal.algoritmo = new SigmaMOPSO(principal.n, principal.problema, principal.geracoes, principal.populacao, principal.S, principal.modificar);
			if(principal.alg.equals("smopso"))
				principal.algoritmo = new SMOPSO(principal.n, principal.problema, principal.geracoes, principal.populacao, principal.S, principal.modificar);
			if(principal.alg.equals("misa"))
				principal.algoritmo = new MISA(principal.n, principal.problema, principal.geracoes, principal.populacao, principal.S, principal.modificar);
			if(principal.alg.equals("nsga2"))
				principal.algoritmo = new NSGA2(principal.n, principal.problema, principal.geracoes, principal.populacao, principal.S, principal.modificar);
			principal.executar();
			
			
		} catch (Exception ex) {ex.printStackTrace();}
	}

	private  void executar()
			throws IOException {
		System.out.println(this);
		
		String id = alg + prob + "_" + m;
		
		String idS = "normal";
		if(modificar)
			idS = S + "";
		
		String caminhoDir = System.getProperty("user.dir") + "/resultados/" + alg + "/" +prob + "/" + m + "/" + idS + "/" ;
		//String caminhoDir = "e:/Andre/testes/resultados/" + principal.alg + "/" + principal.prob + "/" ;
		File dir = new File(caminhoDir);
		dir.mkdirs();
		
		
		
		String arquivoExec = caminhoDir + id+ idS +"_texec.txt";
		
		PrintStream psTempo = new PrintStream(arquivoExec);
		
		
		//PrintStream psMedidasGeral = new PrintStream(caminhoDir +id+ "_" + idS + "_medidas.txt");
		PrintStream psSolucaoGeral = new PrintStream(caminhoDir +id+  idS +"_solucoes.txt");
		PrintStream psFronteiraGeral = new PrintStream(caminhoDir+id+ idS+"_fronteira.txt");
		
		maioresObjetivos = new double[m];
		
		
		ArrayList<ArrayList<PontoFronteira>> fronteiras = new ArrayList<ArrayList<PontoFronteira>>();
		
		for(int i = 0; i<numExec; i++){
			
			String caminhoDirExec = caminhoDir+i+"/";
			dir = new File(caminhoDirExec);
			dir.mkdirs();

			PrintStream psSolucaoExec = new PrintStream(caminhoDirExec+id+"_"+i+"_solucoes.txt");
			PrintStream psFronteiraExec = new PrintStream(caminhoDirExec+id+"_fronteira.txt");
			
			psTempo.print(i +":\t" + Calendar.getInstance().getTimeInMillis() + "\t");
			ArrayList<Solucao> solucoes =  algoritmo.executar();
			psTempo.print(Calendar.getInstance().getTimeInMillis()  + "\n");
			
			for (int j = 0; j < maioresObjetivos.length; j++) {
				maioresObjetivos[j] = 0;
			}
			
			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				
				for(int j = 0; j<m; j++){
					if(solucao.objetivos[j]>maioresObjetivos[j])
						maioresObjetivos[j] =Math.ceil(solucao.objetivos[j]);
				}
			}
			
			ArrayList<PontoFronteira> fronteira = new ArrayList<PontoFronteira>();
			
			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = iterator.next();
				PontoFronteira pf = new PontoFronteira(solucao.objetivos);
				fronteira.add(pf);
			}
			fronteiras.add(fronteira);
			
			gerarSaida(solucoes, psSolucaoGeral,  psFronteiraGeral, psSolucaoExec, psFronteiraExec);
			psSolucaoGeral.println();
			psFronteiraGeral.println();
			System.out.println();
		}
		
		
		System.out.print("\n" + "Melhores Objetivos: ");
		for (int j = 0; j < maioresObjetivos.length; j++) {
			System.out.print(maioresObjetivos[j] + "\t");
			
		}
		System.out.println();
	
		Hipervolume hiper = new Hipervolume(m, caminhoDir, id+idS, limitesHiper);
		hiper.preencherObjetivosMaxMin(maxmimObjetivos);
		if(m<4)
			hiper.calcularIndicadorArray(fronteiras);
		
		Spread spread = new Spread(m, caminhoDir, id+idS);
		spread.preencherObjetivosMaxMin(maxmimObjetivos);
		spread.calcularIndicadorArray(fronteiras);
	
		double[] o = new double[m];
		for (int j = 0; j < o.length; j++) {
			o[j] = 0;
		}
		
		PontoFronteira melhorPonto = new PontoFronteira(o);
		ArrayList<PontoFronteira> pftrue= new ArrayList<PontoFronteira>();
		pftrue.add(melhorPonto);
		
		GD gd = new GD(m, caminhoDir, id+idS, pftrue);
		gd.preencherObjetivosMaxMin(maxmimObjetivos);
		gd.calcularIndicadorArray(fronteiras);
		//gd.calcularIndicadorArquivo(caminhoDir+id+ idS+"_fronteira.txt");
		
		
		

	}
	
	public void gerarSaida(ArrayList<Solucao> fronteira, PrintStream solGeral, PrintStream psFronteiraGeral, PrintStream solExecucao, PrintStream psFronteiraExec){
		
		
		for (Iterator<Solucao> iterator = fronteira.iterator(); iterator.hasNext();) {
			Solucao solucao = (Solucao) iterator.next();
			solGeral.println(solucao);
			solExecucao.println(solucao);
			for(int i = 0; i<m; i++){				
				psFronteiraExec.print(new Double( solucao.objetivos[i]).toString().replace('.', ',')+ "\t");
				psFronteiraGeral.print(new Double(solucao.objetivos[i]).toString().replace('.', ',') + " ");	
			}
			psFronteiraExec.println();
			psFronteiraGeral.println();
		}
	}
	
	public void setProblema(){
		prob = prob.toUpperCase();
		if(prob.equals("DTLZ1"))
			problema = new DTLZ1(m);
		if(prob.equals("DTLZ2"))
			problema = new DTLZ2(m);
		if(prob.equals("DTLZ3"))
			problema = new DTLZ3(m);
		if(prob.equals("DTLZ4"))
			problema = new DTLZ4(m);
		if(prob.equals("DTLZ5"))
			problema = new DTLZ5(m);
		if(prob.equals("DTLZ6"))
			problema = new DTLZ6(m);
	}
	
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
			if(tag.equals("algoritmo"))
				alg = valor;
			
			if(tag.equals("geracoes")){
				geracoes = new Integer(valor).intValue();
			}
			if(tag.equals("populacao"))
				populacao = new Integer(valor).intValue();
			if(tag.equals("numexec"))
				numExec = new Integer(valor).intValue();
			if(tag.equals("m"))
				m = new Integer(valor).intValue();
			if(tag.equals("n"))
				n = new Integer(valor).intValue();
			
			if(tag.equals("problema")){
				prob = valor;
			}
			
			if(tag.equals("s"))
				S = new Double(valor).doubleValue();
			if(tag.equals("modificar")){
				if(valor.equals("true"))
					modificar = true;
				else
					modificar = false;
			}
			
			if(tag.equals("limites_objetivos")){
				setLimitesHiper(valor);
			}
			if(tag.equals("objetivos")){
				maxmimObjetivos = valor.split(" ");
			}
		}
		
	}
	
	
	public void setLimitesHiper(String valor){
		limitesHiper = new double[m];
		String valores[] = valor.split(" ");
		for (int i = 0; i < valores.length; i++) {
			limitesHiper[i] = new Double(valores[i]);
		}
		
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("Algoritmo: " + alg + "\n");
		buff.append("Problema: " + prob + "\n");
		buff.append("m: " + m + "\n");
		buff.append("n: " + n + "\n");
		buff.append("Geracoes: " + geracoes + "\n");
		buff.append("Populacao: " + populacao + "\n");
		buff.append("S: " + S + "\n");
		buff.append("Modificar: " + modificar + "\n");
		return buff.toString();
	}

}
