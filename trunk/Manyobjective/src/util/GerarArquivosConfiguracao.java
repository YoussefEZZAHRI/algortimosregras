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
		String poda = "p-crowd";
		int k = 10;
		String lider = "torneio";
		String  direxec = "/home/andre/doutorado/experimentos/poda/";
				
		
		if(!ind.equals("")){
			algs = new String[1];
			algs[0] = "0.25";
		}
		
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

		int[] ms = {2, 3, 5, 10};

		String ind = "";

		try{
			for (int i = 0; i < ms.length; i++) {
				gerarArquivos(ms[i],ind);
			}
			

		} catch (Exception e) {
		}
	}

}
