package util;

import java.io.IOException;
import java.io.PrintStream;

public class GerarArquivosConfiguracao {
	
	public static int k = 10;
	
	public static void gerarArquivos(int m, String ind) throws IOException{
		
		

		String problema  = "dtlz2";

		//String[] algs = {"0.25", "0.30", "0.35", "0.40", "0.45", "0.50", "0.55", "0.60", "0.65", "0.70", "0.75"};
		String[] algs = {"0.5"};
		String metodo = "smopso";
		String exec = "30";
		String g = "100";
		String a = "50000";
		String p = "250";
		String r = "250";
		String rank = "false";
		String poda = "p-rand";
		String maxobjhiper = "1";
		String  direxec = "/home/andre/doutorado/experimentos/poda/";
		
		int n = k+m-1;
		
		StringBuffer limitesObjetivos = new StringBuffer();
		StringBuffer objetivos = new StringBuffer();
		for(int i = 0; i<m; i++){
			limitesObjetivos.append("8 ");
			objetivos.append("- ");
		}
		
		if(!ind.equals("")){
			algs = new String[1];
			algs[0] = "0.25";
		}
		
		for (int i = 0; i < algs.length; i++) {
			String s = algs[i];
			String arquivo = "";
			if(ind.equals(""))
				arquivo = "arquivos/principal_" + metodo + problema.toUpperCase() + "_" + m + s + ind+ ".txt";
			else
				arquivo = "arquivos/principal_" + metodo + problema.toUpperCase() + "_" + m +"_" + ind+ ".txt";
			if(!rank.equals("false"))
				arquivo = "arquivos/principal_" + metodo + problema.toUpperCase() + "_" + m + s + "_" + rank + ".txt";
			if(!poda.equals("false"))
				arquivo = "arquivos/principal_" + metodo + problema.toUpperCase() + "_" + m + s + "_" + poda + ".txt";
			PrintStream ps = new PrintStream(arquivo);
			ps.println("algoritmo = " + metodo);
			ps.println("problema = " + problema);
			ps.println("m = " + m);
			ps.println("n = " + n);
			ps.println("limites_objetivos = " + limitesObjetivos);
			ps.println("objetivos = " + objetivos);
			ps.println("geracoes = " + g);
			ps.println("numeroavaliacoes = " + a);
			ps.println("populacao = " + p);
			ps.println("repositorio = " + r);		
			ps.println("numexec = " + exec);
			ps.println("S = " + s);
			ps.println("rank = " + rank);
			ps.println("poda = " + poda);				
			ps.println("maxobjhiper = " + maxobjhiper);			
			ps.println("direxec =  " + direxec);
			
			
		}

		 
	}
	
	public static void main(String[] args) {

		int[] ms = {2, 3, 5, 10, 15, 20};

		String ind = "";

		try{
			for (int i = 0; i < ms.length; i++) {
				gerarArquivos(ms[i],ind);
			}
			

		} catch (Exception e) {
		}
	}

}
