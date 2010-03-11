package util;

import java.io.IOException;
import java.io.PrintStream;

public class GerarArquivosConfiguracao {
	
	public static int k = 10;
	
	public static void gerarArquivos(int m) throws IOException{
		
		
		String problema  = "dtlz2";
		String[] algs = {"0.25", "0.30", "0.35", "0.40", "0.45", "0.50", "0.55", "0.60", "0.65", "0.70", "0.75"};
		String metodo = "misa";
		String exec = "30";
		String g = "100";
		String a = "250000";
		String p = "100";
		String rank = "false";
		String taxaclonagem = "7";
		String partesgrid = "25";
		String maxobjhiper = "4";
		String dominance = "false";
		String alg1 = "0.7";
		String alg2 = "normal";
		String indicador = "false";
		
		int n = k+m-1;
		
		StringBuffer limitesObjetivos = new StringBuffer();
		StringBuffer objetivos = new StringBuffer();
		for(int i = 0; i<m; i++){
			limitesObjetivos.append("3 ");
			objetivos.append("- ");
		}
		
		for (int i = 0; i < algs.length; i++) {
			String s = algs[i];
			String arquivo ="principal" + s + ".txt";
			PrintStream ps = new PrintStream(arquivo);
			ps.println("algoritmo = " + metodo);
			ps.println("problema = " + problema);
			ps.println("m = " + m);
			ps.println("n = " + n);
			ps.println("geracoes = " + g);
			ps.println("avaliacoes = " + a);
			ps.println("populacao = " + p);
			ps.println("numexec = " + exec);
			ps.println("S = " + s);
			ps.println("rank = " + rank);
			ps.println("taxaclonagem = " + taxaclonagem);
			ps.println("partesgrid = " + partesgrid);
			ps.println("limites_objetivos = " + limitesObjetivos);
			ps.println("maxobjhiper = " + maxobjhiper);
			ps.println("objetivos = " + objetivos);
			ps.println("dominance = " + dominance);
			ps.println("alg1 = " + alg1);
			ps.println("alg2 = " + alg2);
			ps.println("indicador = " + indicador);
			
		}

		 
	}
	
	public static void main(String[] args) {
		int m = 10;
		try{
			gerarArquivos(m);
		} catch (Exception e) {
		}
	}

}
