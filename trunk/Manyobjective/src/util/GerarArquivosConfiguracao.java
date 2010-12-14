package util;

import java.io.IOException;
import java.io.PrintStream;

public class GerarArquivosConfiguracao {
	
	public static int k = 10;
	
	public static void gerarArquivos(int m, String ind) throws IOException{
		
		
		String problema  = "dtlz4";
		//String[] algs = {"0.25", "0.30", "0.35", "0.40", "0.45", "0.50", "0.55", "0.60", "0.65", "0.70", "0.75"};
		String[] algs = {"0.50"};
		String metodo = "smopso";
		String exec = "30";
		String g = "250";
		String a = "-1";
		String p = "250";
		String r = "50";
		String rank = "br_gb";
		String taxaclonagem = "7";
		String partesgrid = "25";
		String maxobjhiper = "1";
		String dominance = "false";
		String alg1 = "all";
		String alg2 = "normal";
		String indicador = ind;
		String  direxec = "/home/andre/doutorado/experimentos/rank/";
		String  num_sol_fronteira = "10000";
		String programaes = "schedule2";
		String numerocasosteste = "2710";
		String funcoesobjetivo = "pdu;tempo";
		
		int n = k+m-1;
		
		StringBuffer limitesObjetivos = new StringBuffer();
		StringBuffer objetivos = new StringBuffer();
		for(int i = 0; i<m; i++){
			limitesObjetivos.append("4 ");
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
				arquivo = "arquivos/principal_" + metodo + problema.toUpperCase() + "_" + m + s + "_" + rank + ind+ ".txt";
			else
				arquivo = "arquivos/principal_" + metodo + problema.toUpperCase() + "_" + m +"_" + ind+ ".txt";
			//if(!rank.equals("false"))
				//arquivo = "principal" + s + "_" + rank + "_" + m +".txt";
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
			ps.println("taxaclonagem = " + taxaclonagem);
			ps.println("partesgrid = " + partesgrid);			
			ps.println("maxobjhiper = " + maxobjhiper);			
			ps.println("dominance = " + dominance);
			ps.println("alg1 = " + alg1);
			ps.println("alg2 = " + alg2);
			ps.println("indicador = " + indicador);
			ps.println("direxec =  " + direxec);
			ps.println("num_sol_fronteira = " + num_sol_fronteira);
			ps.println("programaes = " + programaes);
			ps.println("numerocasosteste = " + numerocasosteste);
			ps.println("funcoesobjetivo = " + funcoesobjetivo);
			
			
		}

		 
	}
	
	public static void main(String[] args) {
		int[] ms = {2, 3, 5, 10,15,20};
		String ind = "";
		try{
			for (int i = 0; i < ms.length; i++) {
				gerarArquivos(ms[i],ind);
			}
			
		} catch (Exception e) {
		}
	}

}
