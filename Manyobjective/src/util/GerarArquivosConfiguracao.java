package util;

import java.io.IOException;
import java.io.PrintStream;

public class GerarArquivosConfiguracao {
	
	
	
	public static void gerarArquivos(int m, String ind) throws IOException{
		
		

		String problema  = "dtlz2";

		//String[] algs = {"0.25", "0.30", "0.35", "0.40", "0.45", "0.50", "0.55", "0.60", "0.65", "0.70", "0.75"};
		String[] algs = {"0.5"};
		String metodo = "smopso";
		String objetivos = "-";
		String exec = "30";
		String g = "100";
		String a = "-1";
		String p = "250";
		String r = "250";
		String rank = "false";
		String poda = "spea2";
		String[] eps = {"0.1","0.05", "0.025", "0.01", "0.005", "0.0025", "0.001", "0.0005", "0.00025", "0.0001"  };
		int k = 10;
		String lider = "tb";
		String  direxec = "/home/andre/doutorado/experimentos/poda/";
				
		
		if(!ind.equals("")){
			algs = new String[1];
			algs[0] = "0.5";
		}
		
		if(poda.equals("eaps") || poda.equals("eapp")){
			for (int i = 0; i < eps.length; i++) {
				String e = eps[i];
				String arquivo = "";
				if(ind.equals(""))
					arquivo = "arquivos/principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ e  + "_" +  lider+ "_" + poda + ".txt";
				PrintStream ps = new PrintStream(arquivo);

				ps.println("algoritmo = " + metodo);
				ps.println("problema = " + problema);
				ps.println("m = " + m);
				ps.println("k = " + k);
				ps.println("max_min = " + objetivos);
				ps.println("geracoes = " + g);
				ps.println("numeroavaliacoes = " + a);
				ps.println("populacao = " + p);
				ps.println("repositorio = " + r);		
				ps.println("numexec = " + exec);
				ps.println("S = 0.5");
				ps.println("eps = " + e);
				ps.println("rank = false");
				ps.println("poda = " + poda);				
				ps.println("lider = " + lider);			
				ps.println("direxec =  " + direxec);
			}
		
		} else

			for (int i = 0; i < algs.length; i++) {
				String s = algs[i];
				String arquivo = "";
				if(ind.equals(""))
					arquivo = "arquivos/principal_" + metodo + "_"+ problema.toUpperCase() + "_" + m + "_"+s + "_" +  lider +"_"+ ind+ ".txt";
				else
					arquivo = "arquivos/principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_" + ind+ ".txt";
				if(!rank.equals("false"))
					arquivo = "arquivos/principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ s  + "_"+lider + "_" + rank + ".txt";
				if(!poda.equals("false"))
					arquivo = "arquivos/principal_" + metodo + "_"+problema.toUpperCase() + "_" + m +"_"+ s  + "_" +  lider+ "_" + poda + ".txt";
				PrintStream ps = new PrintStream(arquivo);

				ps.println("algoritmo = " + metodo);
				ps.println("problema = " + problema);
				ps.println("m = " + m);
				ps.println("k = " + k);
				ps.println("max_min = " + objetivos);
				ps.println("geracoes = " + g);
				ps.println("numeroavaliacoes = " + a);
				ps.println("populacao = " + p);
				ps.println("repositorio = " + r);		
				ps.println("numexec = " + exec);
				ps.println("S = " + s);
				ps.println("rank = " + rank);
				ps.println("poda = " + poda);				
				ps.println("lider = " + lider);			
				ps.println("direxec =  " + direxec);
			}

		 
	}
	
	public static void main(String[] args) {

		int[] ms = {2,3,5,10,15, 20};

		String ind = "";

		try{
			for (int i = 0; i < ms.length; i++) {
				gerarArquivos(ms[i],ind);
			}
			

		} catch (Exception e) {
		}
	}

}
