package principal;

import indicadores.Dominance;
import indicadores.GD;
import indicadores.Hipervolume;
import indicadores.IGD;
import indicadores.Indicador;
import indicadores.PontoFronteira;
import indicadores.Spread;
import indicadores.Tchebycheff;

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
import problema.TestCaseSelection;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;

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
	
	public int repositorio;
	
	public int numeroavaliacoes=-1;
	
	
	public double S;
	
	public boolean rank;
	
	public String alg1;
	public String alg2;
	public String dirExec = "";
	public boolean dominance = false;
	public int num_sol_fronteira = 0;;
	
	public String indicador = "";
	
	public double maioresObjetivos[];
	
	public double limitesHiper[];
	public String[] maxmimObjetivos;
	public int maxobjhiper;
	
	public int taxaclonagem;
	public int partesgrid;
	
	public String programaes; 
	public int numeroCasosTeste;
	public String funcoesobjetivo;
	
	public String tipoSolucao;
	
	 
	public static void main(String[] args) {	
		Principal principal = new Principal();
		try{			
			principal.carregarArquivoConf(args[0]);
			principal.setProblema();
			if(principal.dominance){
				principal.executarDominance();
			} else {
				if(!principal.indicador.equals(""))
					principal.executarIndicador();
				else{
					if(principal.alg.equals("sigma"))
						principal.algoritmo = new SigmaMOPSO(principal.n, principal.problema, principal.geracoes, principal.numeroavaliacoes, principal.populacao, principal.S, principal.maxmimObjetivos, principal.rank);
					if(principal.alg.equals("smopso"))
						principal.algoritmo = new SMOPSO(principal.n, principal.problema, principal.geracoes, principal.numeroavaliacoes, principal.populacao, principal.S, principal.maxmimObjetivos, principal.rank, principal.repositorio);
					if(principal.alg.equals("misa"))
						principal.algoritmo = new MISA(principal.n, principal.problema, principal.geracoes, principal.numeroavaliacoes, principal.populacao, principal.S, principal.taxaclonagem, principal.partesgrid, principal.maxmimObjetivos, principal.rank);
					if(principal.alg.equals("nsga2"))
						principal.algoritmo = new NSGA2(principal.n, principal.problema, principal.geracoes, principal.numeroavaliacoes, principal.populacao, principal.S, principal.tipoSolucao, principal.maxmimObjetivos, principal.rank);
					
					
					principal.executar();
				
				}
			  }
			} catch (Exception ex) {ex.printStackTrace();}
	}
	
	private void executarDominance() throws IOException{
		
		if(alg1 == null || alg2 == null){
			System.err.println("Algoritmos para a comparação da dominancia não foram definido (Tags alg1 ou alg2)");
			System.exit(0);
		}
			
		
		String caminhoDir = System.getProperty("user.dir") + "/resultados/" + alg + "/" +prob + "/" + m + "/";
		File dir = new File(caminhoDir);
		dir.mkdirs();
		
		String idExec1 = alg + prob + "_" + m + alg1;
		String idExec2 = alg + prob + "_" + m + alg2;
		
		Dominance dominance = new Dominance(m, caminhoDir, idExec1, idExec2);
		dominance.preencherObjetivosMaxMin(maxmimObjetivos);
		String arquivo1 = caminhoDir + alg1 + "/" + idExec1  + "_fronteira.txt";
		String arquivo2 = caminhoDir + alg2 + "/" + idExec2  + "_fronteira.txt";
		
		dominance.calcularDominanceArquivo(arquivo1, arquivo2);
		
	}
	
	private void executarIndicador() throws IOException{
		
		if(alg1 == null){
			System.err.println("Algoritmo para a execucao do indicador não foi definido (Tags alg1)");
			System.exit(0);
		}
		
		
		String[] configuracoes = {"0.25", "0.3", "0.35", "0.4", "0.45", "0.5", "0.55", "0.6", "0.65", "0.7", "0.75"};
		if(!alg1.equals("all")){
			configuracoes = new String[1];
			configuracoes[0] = alg1;
		}
		
		for (int i = 0; i < configuracoes.length; i++) {
			alg1 = configuracoes[i];
			String diretorio = dirExec;
			if(diretorio.equals(""))
				diretorio = System.getProperty("user.dir");

			String caminhoDir = diretorio + "resultados/" + alg + "/" +prob + "/" + m + "/" + alg1 +"/";
			File dir = new File(caminhoDir);
			dir.mkdirs();

			String idExec = alg + prob + "_" + m + alg1;
			Indicador ind = null;
			if(indicador.equals("gd") || indicador.equals("igd")){
				if(num_sol_fronteira == 0)
					num_sol_fronteira = populacao;
				ArrayList<SolucaoNumerica> fronteira =  problema.obterFronteira(n, num_sol_fronteira);
				ArrayList<PontoFronteira> pftrue= new ArrayList<PontoFronteira>();

				for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
					SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
					PontoFronteira temp = new PontoFronteira(solucao.objetivos);
					pftrue.add(temp);
				}

				if(indicador.equals("gd"))
					ind = new GD(m, caminhoDir, idExec, pftrue);
				else
					ind = new IGD(m, caminhoDir, idExec, pftrue);
			}
			else{
				if(indicador.equals("hipervolume"))
					ind = new Hipervolume(m, caminhoDir, idExec, limitesHiper);
				else{
					if(indicador.equals("spread"))
						ind = new Spread(m, caminhoDir, idExec);
					else{
						if(indicador.equals("tchebycheff")){
							double[] j =  problema.getJoelho(n);
							double[] l = problema.getLambda(n);
							ind = new Tchebycheff(m, caminhoDir, idExec, j , l);
						} 				
					}
				}
			}

			if(ind!=null){
				ind.preencherObjetivosMaxMin(maxmimObjetivos);
				String arquivo1 = caminhoDir + "/" + idExec  + "_fronteira.txt";
				System.out.println("Indicador: " + ind.indicador);
				System.out.println("S = " + alg1);
				ind.calcularIndicadorArquivo(arquivo1);
			}
		}
	}

	private  void executar()
			throws IOException {
		System.out.println(this);
		String id = alg + prob + "_" + m;
		
		String caminhoDir = null;
		String arquivoExec = null;
		if(!rank){
			caminhoDir = System.getProperty("user.dir") + "/resultados/" + alg + "/" +prob + "/" + m + "/" + S + "/" ;
			arquivoExec = caminhoDir + id+ S +"_texec.txt";
		}else{
			caminhoDir = System.getProperty("user.dir") + "/resultados/" + alg + "/" +prob + "/" + m + "/" +S + "_rank/" ;
			arquivoExec = caminhoDir + id+ S +"_rank_texec.txt";
		}
		
		File dir = new File(caminhoDir);
		dir.mkdirs();
		PrintStream psTempo = null;
		PrintStream psSolucaoGeral = null;
		PrintStream psFronteiraGeral = null;
		if(!rank){
			psTempo = new PrintStream(arquivoExec);
			psSolucaoGeral = new PrintStream(caminhoDir +id+  S +"_solucoes.txt");
			psFronteiraGeral = new PrintStream(caminhoDir+id+ S+"_fronteira.txt");
		}else {

			psTempo = new PrintStream(arquivoExec);
			psSolucaoGeral = new PrintStream(caminhoDir +id+ S+ "_rank_solucoes.txt");
			psFronteiraGeral = new PrintStream(caminhoDir+id+ S+"_rank_fronteira.txt");
		}
		
		maioresObjetivos = new double[m];
		ArrayList<ArrayList<PontoFronteira>> fronteiras = new ArrayList<ArrayList<PontoFronteira>>();
		
		long tinicial, tfinal;
		
		
		for(int i = 0; i<numExec; i++){
			
			String caminhoDirExec = caminhoDir+i+"/";
			dir = new File(caminhoDirExec);
			dir.mkdirs();

			PrintStream psSolucaoExec = new PrintStream(caminhoDirExec+id+"_"+i+"_solucoes.txt");
			PrintStream psFronteiraExec = new PrintStream(caminhoDirExec+id+"_fronteira.txt");
			
			psTempo.print(i +":\t" + Calendar.getInstance().getTimeInMillis() + "\t");
			tinicial = Calendar.getInstance().getTimeInMillis();
			
			ArrayList<Solucao> solucoes = null;

			System.out.println("Execucao: " + i);
			if(numeroavaliacoes==-1)
				solucoes =  algoritmo.executar();
			else
				solucoes =  algoritmo.executarAvaliacoes();
			
			psTempo.print(Calendar.getInstance().getTimeInMillis()  + "\n");
			
			
			for (int j = 0; j < maioresObjetivos.length; j++) {
				maioresObjetivos[j] = 0;
			}
			
			ArrayList<PontoFronteira> fronteira = new ArrayList<PontoFronteira>();
			
			for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
				Solucao solucao = iterator.next();
				
				PontoFronteira pf = new PontoFronteira(solucao.objetivos);
				fronteira.add(pf);
				
				for(int j = 0; j<m; j++){
					if(solucao.objetivos[j]>maioresObjetivos[j])
						maioresObjetivos[j] =Math.ceil(solucao.objetivos[j]);
				}
			}
			
			
			fronteiras.add(fronteira);
			
			gerarSaida(solucoes, psSolucaoGeral,  psFronteiraGeral, psSolucaoExec, psFronteiraExec);
			psSolucaoGeral.println();
			psFronteiraGeral.println();
		
			System.out.println();
			System.out.println("Numero de avaliacoes: " + problema.avaliacoes);
			
			System.out.println("Piores Objetivos: ");
			for (int j = 0; j < maioresObjetivos.length; j++) {
				System.out.print(maioresObjetivos[j] + "\t");
				
			}
			System.out.println();
			tfinal = Calendar.getInstance().getTimeInMillis();
			
			System.out.println("Tempo Execucao: " + ((double)(tfinal - tinicial)/1000) + " (s)");
			System.out.println();
		}
		
		
		String idInd = null;
		if(!rank)
			idInd = id + S;
		else
			idInd = id + S + "_rank";
		
		Hipervolume hiper = new Hipervolume(m, caminhoDir, idInd, limitesHiper);
		hiper.preencherObjetivosMaxMin(maxmimObjetivos);
		if(m<=maxobjhiper)
			hiper.calcularIndicadorArray(fronteiras);
		
		Spread spread = new Spread(m, caminhoDir, idInd);
		spread.preencherObjetivosMaxMin(maxmimObjetivos);
		spread.calcularIndicadorArray(fronteiras);
	
		ArrayList<SolucaoNumerica> fronteira =  problema.obterFronteira(n, populacao);
		ArrayList<PontoFronteira> pftrue= new ArrayList<PontoFronteira>();
		
		if(fronteira!=null){
			for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator.next();
				PontoFronteira temp = new PontoFronteira(solucao.objetivos);
				pftrue.add(temp);
			}

			GD gd = new GD(m, caminhoDir, idInd, pftrue);
			gd.preencherObjetivosMaxMin(maxmimObjetivos);
			gd.calcularIndicadorArray(fronteiras);

			IGD igd = new IGD(m, caminhoDir, idInd, pftrue);
			igd.preencherObjetivosMaxMin(maxmimObjetivos);
			igd.calcularIndicadorArray(fronteiras);


			double[] j =  problema.getJoelho(n);
			double[] l = problema.getLambda(n);
			Tchebycheff tcheb = new Tchebycheff(m, caminhoDir, idInd, j , l);
			tcheb.preencherObjetivosMaxMin(maxmimObjetivos);
			tcheb.calcularTchebycheff(fronteiras);
		}
	}
	
	public void gerarSaida(ArrayList<Solucao> fronteira, PrintStream solGeral, PrintStream psFronteiraGeral, PrintStream solExecucao, PrintStream psFronteiraExec){
		
		
		for (Iterator<Solucao> iterator = fronteira.iterator(); iterator.hasNext();) {
			Solucao solucao = iterator.next();
			solGeral.println(solucao);
			solExecucao.println(solucao);
			for(int i = 0; i<m; i++){				
				psFronteiraExec.print(new Double( solucao.objetivos[i]).toString().replace('.', ',')+ " ");
				psFronteiraGeral.print(new Double(solucao.objetivos[i]).toString().replace('.', ',') + " ");	
			}
			psFronteiraExec.println();
			psFronteiraGeral.println();
		}
	}
	
	public void setProblema(){
		prob = prob.toUpperCase();
		if(prob.equals("TESTSELECTION")){
			problema = new TestCaseSelection(m, funcoesobjetivo, programaes, dirExec, numeroCasosTeste);
			tipoSolucao = "binaria";
			n = numeroCasosTeste;
		} else{
		tipoSolucao = "numerica";
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
	}
	
	public void carregarArquivoConf(String nomeArquivo)throws IOException{
		Reader reader = new FileReader(nomeArquivo);
		BufferedReader buff = new BufferedReader(reader);
		while(buff.ready()){
			String linhaString = buff.readLine();
			if(!linhaString.isEmpty()){
				String linha[] = linhaString.split("=");

				if(linha.length!=2){
					System.err.println("Erro no arquivo de configuração. Linha: " + linhaString);
					System.exit(0);
				}
				String tag = linha[0].trim().toLowerCase();
				String valor = linha[1].trim();
				if(tag.equals("algoritmo"))
					alg = valor;

				if(tag.equals("direxec"))
					dirExec = valor;

				if(tag.equals("programaes"))
					programaes = valor;

				if(tag.equals("funcoesobjetivo"))
					funcoesobjetivo = valor;

				if(tag.equals("numerocasosteste"))
					numeroCasosTeste = new Integer(valor).intValue();

				if(tag.equals("geracoes")){
					geracoes = new Integer(valor).intValue();
				}
				if(tag.equals("populacao"))
					populacao = new Integer(valor).intValue();
				if(tag.equals("repositorio"))
					repositorio = new Integer(valor).intValue();
				if(tag.equals("numexec"))
					numExec = new Integer(valor).intValue();
				if(tag.equals("m"))
					m = new Integer(valor).intValue();
				if(tag.equals("n"))
					n = new Integer(valor).intValue();

				if(tag.equals("num_sol_fronteira"))
					num_sol_fronteira = new Integer(valor).intValue();


				if(tag.equals("taxaclonagem"))
					taxaclonagem = new Integer(valor).intValue();

				if(tag.equals("maxobjhiper"))
					maxobjhiper = new Integer(valor).intValue();

				if(tag.equals("avaliacoes"))
					numeroavaliacoes = new Integer(valor).intValue();


				if(tag.equals("problema")){
					prob = valor;
				}

				if(tag.equals("dominance")){
					if(valor.equals("true"))
						dominance = true;
					else
						dominance = false;
				}

				if(tag.equals("indicador"))
					if(valor.equals("hipervolume") || valor.equals("gd") || valor.equals("spread") || valor.equals("igd") || valor.equals("tchebycheff")){
						indicador = valor;
					}

				if(tag.equals("alg1")){
					alg1 = valor;
				}

				if(tag.equals("alg2")){
					alg2 = valor;
				}

				if(tag.equals("s"))
					S = new Double(valor).doubleValue();
				if(tag.equals("partesgrid")){
					partesgrid = new Integer(valor).intValue();
				}

				if(tag.equals("limites_objetivos")){
					setLimitesHiper(valor);
				}
				if(tag.equals("objetivos")){
					maxmimObjetivos = valor.split(" ");
				}

				if(tag.equals("rank")){
					if(valor.equals("true"))
						rank = true;
					else
						rank = false;
				}
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
		buff.append("Avaliacoes: " + numeroavaliacoes + "\n");
		buff.append("Populacao: " + populacao + "\n");
		buff.append("S: " + S + "\n");
		return buff.toString();
	}

}
