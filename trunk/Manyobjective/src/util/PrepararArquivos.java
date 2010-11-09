package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class PrepararArquivos {
	
	public static int numObjHiper = 10;
	
	
	public void juntarFronteira(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		
		boolean entrou = false;
		for (int j = 0; j < algoritmos.length; j++) {
			String arqfronteira = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_fronteira.txt";
			
			PrintStream psFronteira = new PrintStream(arqfronteira);
			
			for (int i = 0; i < exec; i++) {
				String arqfronteiraTemp = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
				algoritmos[j] + "/" + i + "/" + metodo + "" + problema + "_" + objetivo + "_fronteira.txt";
				//System.out.println(arqfronteiraTemp);
				BufferedReader buff = new BufferedReader(new FileReader(arqfronteiraTemp));
				while(buff.ready()){
					String linha = buff.readLine().replace("\t", " ");
					if(!linha.isEmpty()){
						psFronteira.println(linha);
						entrou = true;
					}
				}
				
				psFronteira.println();
				if(entrou){
					System.out.println(i);
				}
				
				entrou = false;
			}
		}
	}
	
	/**
	 * M�todo que prepara um arquivo contendo um fronteira de pareto para ser executado no PISA
	 * @param dir
	 * @param problema
	 * @param objetivo
	 * @param algoritmos
	 * @param exec
	 * @param metodo
	 * @throws IOException
	 */
	public void inverterMaxMim(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		for (int j = 0; j < algoritmos.length; j++) {
			
			
			String arqfronteira = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_fronteira.txt";
			
			System.out.println(arqfronteira);
			
			/*BufferedReader buff = new BufferedReader(new FileReader(arqfronteira));
			double maior = 0;
			while(buff.ready()){
				String linha = buff.readLine();
				if(!linha.isEmpty()){
					String[] valores = linha.split(" ");
					for (int i = 0; i < valores.length; i++) {
						maior = Math.max(maior, new Double(valores[i].replace(',', '.')).doubleValue());
					}
				}
			}*/
			
			BufferedReader buff = new BufferedReader(new FileReader(arqfronteira));
			
			PrintStream ps = new PrintStream("teste_" + algoritmos[j] + ".txt");
			while(buff.ready()){
				String linha = buff.readLine();
				if(linha.isEmpty()){
					ps.println();
				} else {
					String[] valores = linha.split(" ");
					for (int i = 0; i < valores.length; i++) {
						//double novo_valor = Math.max(0.0001, maior - new Double(valores[i].replace(',', '.')).doubleValue());
						Double novo_valor = new Double(valores[i].replace(',', '.')).doubleValue();
						if(novo_valor>4)
							System.out.println(novo_valor);
						if((novo_valor < 0.001) && novo_valor!=0){
							DecimalFormat formatador = new DecimalFormat("0.0000000000000000");  
							
							ps.print(formatador.format(novo_valor) + " ");
						} else
							ps.print(novo_valor + " ");
					}
					ps.println();
				}
			}
		}
	}
	
	public void gerarComando(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo, Double[][] dados, String medida ) throws IOException{
		
		
		
		
		for (int i = 0; i < algoritmos.length; i++) {
			StringBuffer comandosHyp = new StringBuffer();
			String id = metodo + "" + problema + "_" + objetivo + algoritmos[i] + "_" + medida;
			String arqfronteira = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[i] + "/" +  id + "_comando.txt";
			comandosHyp.append(id + "<-c(");
			for (int j = 0; j < exec; j++) {
				comandosHyp.append(dados[i][j] + ",");
			}
			
			comandosHyp.deleteCharAt(comandosHyp.length()-1);
			comandosHyp.append(")\n");
			
			PrintStream ps = new PrintStream(arqfronteira);
			
			ps.println(comandosHyp);
		}
			
	}
	
		
	
	public void preparArquivosIndicadores(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo, String ind) throws IOException{
		Double[][] valores = new Double[algoritmos.length][exec];
		

		BufferedReader buff;


		for (int j = 0; j < algoritmos.length; j++) {

			String arq = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_" + ind + ".txt";

		

			System.out.println(arq);


			buff = new BufferedReader(new FileReader(arq));
			int tam = 0;
			while(buff.ready()){
				String linha = buff.readLine();
				if(!linha.isEmpty()){
					Double val = new Double(linha);
					valores[j][tam++] = val;	
				}
			}
		}  



		PrintStream psSaida = new PrintStream(dir + "medidas/" + metodo + problema + "_"+ ind + "_" + objetivo + "_indicadores.txt");
		



		for(int j = 0; j<exec; j++){
			for (int i = 0; i < algoritmos.length; i++) {
				try{
					psSaida.print(valores[i][j] + "\t");		
				}catch(NullPointerException ex){ex.printStackTrace();}
			}


			psSaida.println();
		}


	}
	
	
	
	public void preparArquivosComandosWilcox(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		

		BufferedReader h, s, g;
		
		PrintStream hypSaida = new PrintStream(dir + "resultados/hyper_" + objetivo + "_comando.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/spread_" + objetivo + "_comando.txt");
		PrintStream igdSaida = new PrintStream(dir + "resultados/igd_" + objetivo + "_comando.txt");
		
		StringBuffer comandosHyp = new StringBuffer();
		StringBuffer comandosSpread = new StringBuffer();
		StringBuffer comandosigd = new StringBuffer();
		
		String algCompHyp = metodo + problema + "_" + objetivo + "0.5_hipervolume";
		String algCompSpread = metodo + problema + "_" + objetivo + "0.5_spread";
		String algCompigd = metodo + problema + "_" + objetivo + "0.5_igd";
		
		int obj = Integer.parseInt(objetivo);

		for (int j = 0; j < algoritmos.length; j++) {
			String arqHyp = "";
			if(obj<=numObjHiper)
				arqHyp = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_hipervolume_comando.txt";
			String arqSpread = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread_comando.txt";
			String arqigd = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_igd_comando.txt";
			
			if(!algoritmos[j].equals("0.5")){
				String algHyp = metodo + problema + "_" + objetivo + algoritmos[j] + "_hipervolume";
				String algSpread = metodo + problema + "_" + objetivo + algoritmos[j] + "_spread";
				String algigd = metodo + problema + "_" + objetivo + algoritmos[j] + "_igd";
				if(obj<=numObjHiper)
					comandosHyp.append("wilcox.test(" + algHyp + "," + algCompHyp + ")\n");
				comandosSpread.append("wilcox.test(" + algSpread + "," + algCompSpread + ")\n");
				comandosigd.append("wilcox.test(" + algigd + "," + algCompigd + ")\n");
			}
			
			
			if(obj<=numObjHiper)
				h = new BufferedReader(new FileReader(arqHyp));
			else
				h = new BufferedReader(new FileReader(arqSpread));
			s = new BufferedReader(new FileReader(arqSpread));
			g = new BufferedReader(new FileReader(arqigd));
			while(s.ready()){
				if(obj<=numObjHiper)
					hypSaida.println(h.readLine());
				spreSaida.println(s.readLine());
				igdSaida.println(g.readLine());
			}
			
		}  
		
		if(obj<=numObjHiper)
			hypSaida.println();
		spreSaida.println();
		igdSaida.println();
		
		if(obj<=numObjHiper)
			hypSaida.println(comandosHyp);
		spreSaida.println(comandosSpread);
		igdSaida.println(comandosigd);

			
	}
	
public void preparArquivosComandosFriedman(String dir, String dir2, String problema, String objetivo, String[] algoritmos , int exec, String metodo, String ind) throws IOException{
		

		BufferedReader buff;
		
		PrintStream psSaida = new PrintStream(dir + "medidas/" + metodo + problema + "_" + ind +"_" + objetivo + "_comando_friedman.txt");
		
		
		StringBuffer comandos = new StringBuffer();
		
		
		StringBuffer comandosBox = new StringBuffer();
		
		
		
		comandos.append("require(pgirmess)\n AR1 <-cbind(");
		
		
		comandosBox.append("boxplot(");
		

		for (int j = 0; j < algoritmos.length; j++) {
			String arq =  dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_" + ind +"_comando.txt";
			
			
			
			String alg = metodo + problema + "_" + objetivo + algoritmos[j] + "_" + ind;
			
			
			comandos.append(alg + ",");
			
			
			
			comandosBox.append(alg + ",");
			
			
			
			buff = new BufferedReader(new FileReader(arq));
			
			
			
			while(buff.ready()){
			
				psSaida.println(buff.readLine());
				
			}
			
		}  
		
		
		psSaida.println();
		
		
		comandosBox.deleteCharAt(comandosBox.length()-1);
		comandosBox.append(")\n");
		
		
		
		comandos.deleteCharAt(comandos.length()-1);
		comandos.append(")\n");
		
		
		
		comandos.append(	"result<-friedman.test(AR1)\n\n" +
							 "m<-data.frame(result$statistic,result$p.value)\n" +
							 "write.csv2(m,file=\"" +  dir2 + "medidas/result_"+ metodo + problema + objetivo+ "_" + ind+ ".csv\")\n\n" +
							 "pos_teste<-friedmanmc(AR1)\n" +
							 "write.csv2(pos_teste,file=\"" + dir2 + "medidas/friedman_"+ metodo + problema+ objetivo+ "_" + ind +".csv\")");
		
		
		
		
		psSaida.println(comandos);
		psSaida.println(comandosBox);
		
	
		
	}


	
	public static void main(String[] args) {
		PrepararArquivos pre = new PrepararArquivos();
		String dir = "/home/andre/gemini/doutorado/experimentos/neuro/";		
		String dir2 = "/home/andre/doutorado/neuro/";
		int objetivo = 20;
		String problema  = "DTLZ2";
		String[] algs = {"0.25", "0.3", "0.35", "0.4", "0.45", "0.5", "0.55", "0.6", "0.65", "0.7", "0.75"};
		//String[] algs = {"0.5_ar", "0.5_bro", "0.5_mr", "0.5_nsga", "0.5_mr_bro", "0.5_ar_bro", "0.3_nsga", "0.35_nsga", "0.4_nsga" };
		//String[] algs = {"0.4"};
		String metodo = "sigma";
		int exec = 50;
		try{
			
			//pre.juntarFronteira(dir, problema, objetivo, algs, exec, metodo);
			//pre.inverterMaxMim(dir, problema, objetivo, algs, exec, metodo);
			pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "gd");
			pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "igd");
			pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "spread");
			//pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "pnf");
			pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo, "np");
			
			pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "gd");
			pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "igd");
			pre.preparArquivosComandosFriedman(dir, dir2,  problema, ""+objetivo, algs, exec, metodo, "spread");
			//pre.preparArquivosComandosFriedman(dir,  problema, ""+objetivo, algs, exec, metodo, "pnf");
			//pre.preparArquivosComandosFriedman(dir,  problema, ""+objetivo, algs, exec, metodo, "np");
			
			
			
		} catch (IOException ex){ex.printStackTrace();}
		
		
	}

}
