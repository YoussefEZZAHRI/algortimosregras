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
	 * Método que prepara um arquivo contendo um fronteira de pareto para ser executado no PISA
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
	
	public void preparArquivosIndicadoresHiper(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		Double[][] hip = new Double[algoritmos.length][exec];
		
		BufferedReader h;


		for (int j = 0; j < algoritmos.length; j++) {

			String arqhip = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_hipervolume.txt";


			System.out.println(arqhip);


			h = new BufferedReader(new FileReader(arqhip));
			
			int tam = 0;
			while(h.ready()){
				String linha_h = h.readLine();
				if(!linha_h.isEmpty()){
					Double h_val = new Double(linha_h);
					hip[j][tam++] = h_val;	
				}
				
				

			}
		}  



		PrintStream hSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_hiper_" + objetivo + "_indicadores.txt");
		



		for(int j = 0; j<exec; j++){
			for (int i = 0; i < algoritmos.length; i++) {
				try{

					hSaida.print(hip[i][j].toString().replace(".", ",") + "\t");
						
				}catch(NullPointerException ex){ex.printStackTrace();}
			}


			hSaida.println();
			
		}


	}
	
	
	public void preparArquivosIndicadores(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		Double[][] gd = new Double[algoritmos.length][exec];
		Double[][] spread = new Double[algoritmos.length][exec];
		Double[][] igd = new Double[algoritmos.length][exec];

		BufferedReader g, s, ig;


		for (int j = 0; j < algoritmos.length; j++) {

			String arqgd = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_gd.txt";

			String arqSpread = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread.txt";
			String arqigd = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_igd.txt";

			System.out.println(arqgd);


			g = new BufferedReader(new FileReader(arqgd));
			s = new BufferedReader(new FileReader(arqSpread));
			ig = new BufferedReader(new FileReader(arqigd));
			int tam = 0;
			while(g.ready()){
				String linha_gd = g.readLine();
				if(!linha_gd.isEmpty()){
					Double gd_val = new Double(linha_gd);
					gd[j][tam] = gd_val;	
				}
				
				String linha_spre = s.readLine();
				
				if(!linha_spre.isEmpty()){
					Double spre_val = new Double(linha_spre);
					spread[j][tam] = spre_val;
				}

				String linha_igd = ig.readLine();
				if(!linha_igd.isEmpty()){
					Double igd_val = new Double(linha_igd);
					igd[j][tam++] = igd_val;
				}

			}
		}  



		PrintStream gdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_gd_" + objetivo + "_indicadores.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_spread_" + objetivo + "_indicadores.txt");
		PrintStream igdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_igd_" + objetivo + "_indicadores.txt");



		for(int j = 0; j<exec; j++){
			for (int i = 0; i < algoritmos.length; i++) {
				try{

					gdSaida.print(gd[i][j].toString().replace(".", ",") + "\t");
					spreSaida.print(spread[i][j].toString().replace(".", ",") + "\t");
					igdSaida.print(igd[i][j].toString().replace(".", ",") + "\t");	
				}catch(NullPointerException ex){ex.printStackTrace();}
			}


			gdSaida.println();
			spreSaida.println();
			igdSaida.println();
		}


	}
	
	public void preparArquivosIndicadoresVis(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		Double[][] hyper = new Double[algoritmos.length][exec];
		//Double[][] spread = new Double[algoritmos.length][exec];
		Double[][] igd = new Double[algoritmos.length][exec];

		BufferedReader h, s, g;
		
		int obj = Integer.parseInt(objetivo);

		for (int j = 0; j < algoritmos.length; j++) {
			
			String arqHyp = "";
			if(obj<=numObjHiper)
				arqHyp = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + algoritmos[j] + "_hipervolume.txt";
			
			//String arqSpread = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			// algoritmos[j] + "_spread.txt";
			String arqigd = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "_igd.txt";
			
			System.out.println(arqigd);
			try{
			if(obj<=numObjHiper)
				h = new BufferedReader(new FileReader(arqHyp));
			else
				h = null;
			//s = new BufferedReader(new FileReader(arqSpread));
			g = new BufferedReader(new FileReader(arqigd));
			int tam = 0;
			while(g.ready()){
				Double hyp_val = new Double(0);
				if(obj<=numObjHiper)
					hyp_val = new Double(h.readLine());
				
				//Double spre_val = new Double(s.readLine());
				
				Double igd_val = new Double(g.readLine());
				if(obj<=numObjHiper)
					hyper[j][tam] = hyp_val;			
				//spread[j][tam] = spre_val;
				igd[j][tam++] = igd_val;
			}
			}
			catch(IOException ex){}
		}  
		
		PrintStream hypSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_hyper_" + objetivo + "_indicadores.txt");
		//PrintStream spreSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_spread_" + objetivo + "_indicadores.txt");
		PrintStream igdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_igd_" + objetivo + "_indicadores.txt");
		
		
		
		for(int j = 0; j<exec; j++){
			for (int i = 0; i < algoritmos.length; i++) {
			
				if(obj<=numObjHiper)
					hypSaida.print(hyper[i][j].toString().replace(".", ",") + "\t");
				//spreSaida.print(spread[i][j].toString().replace(".", ",") + "\t");
				igdSaida.print(igd[i][j].toString().replace(".", ",") + "\t");	

			}
			
			if(obj<=numObjHiper)
				hypSaida.println();
			//spreSaida.println();
			igdSaida.println();
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
	
public void preparArquivosComandosFriedman(String dir, String dirR, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		

		BufferedReader g, s, ig;
		
		PrintStream gdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_gd_" + objetivo + "_comando_friedman.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_spread_" + objetivo + "_comando_friedman.txt");
		PrintStream igdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_igd_" + objetivo + "_comando_friedman.txt");
		
		StringBuffer comandosGD = new StringBuffer();
		StringBuffer comandosSpread = new StringBuffer();
		StringBuffer comandosigd = new StringBuffer();
		
		StringBuffer comandosGDBox = new StringBuffer();
		StringBuffer comandosSpreadBox = new StringBuffer();
		StringBuffer comandosigdBox = new StringBuffer();
		
		
		comandosGD.append("require(pgirmess)\n AR1 <-cbind(");
		comandosSpread.append("require(pgirmess)\n AR1 <-cbind(");
		comandosigd.append("require(pgirmess)\n AR1 <-cbind(");
		
		comandosGDBox.append("boxplot(");
		comandosSpreadBox.append("boxplot(");
		comandosigdBox.append("boxplot(");

		for (int j = 0; j < algoritmos.length; j++) {
			String arqgd =  dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_gd_comando.txt";
			String arqSpread = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread_comando.txt";
			String arqigd = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_igd_comando.txt";
			
			
			String algGD = metodo + problema + "_" + objetivo + algoritmos[j] + "_gd";
			String algSpread = metodo + problema + "_" + objetivo + algoritmos[j] + "_spread";
			String algigd = metodo + problema + "_" + objetivo + algoritmos[j] + "_igd";
			
			comandosGD.append(algGD + ",");
			comandosSpread.append(algSpread + ",");
			comandosigd.append(algigd + ",");
			
			
			comandosGDBox.append(algGD + ",");
			comandosSpreadBox.append(algSpread + ",");
			comandosigdBox.append(algigd + ",");
			
			
			g = new BufferedReader(new FileReader(arqgd));
			
			s = new BufferedReader(new FileReader(arqSpread));
			ig = new BufferedReader(new FileReader(arqigd));
			while(g.ready()){
			
				gdSaida.println(g.readLine());
				spreSaida.println(s.readLine());
				igdSaida.println(ig.readLine());
			}
			
		}  
		
		
		gdSaida.println();
		spreSaida.println();
		igdSaida.println();
		
		comandosGDBox.deleteCharAt(comandosGDBox.length()-1);
		comandosGDBox.append(")\n");
		
		comandosSpreadBox.deleteCharAt(comandosSpreadBox.length()-1);
		comandosSpreadBox.append(")\n");
		
		comandosigdBox.deleteCharAt(comandosigdBox.length()-1);
		comandosigdBox.append(")\n");
		
		comandosGD.deleteCharAt(comandosGD.length()-1);
		comandosGD.append(")\n");
		
		comandosSpread.deleteCharAt(comandosSpread.length()-1);
		comandosSpread.append(")\n");
		
		comandosigd.deleteCharAt(comandosigd.length()-1);
		comandosigd.append(")\n");
		
		comandosGD.append(	"result<-friedman.test(AR1)\n\n" +
							 "m<-data.frame(result$statistic,result$p.value)\n" +
							 "write.csv2(m,file=\"" +  dirR + "resultados\\\\result_"+ metodo + problema + objetivo+ "_gd.csv\")\n\n" +
							 "pos_teste<-friedmanmc(AR1)\n" +
							 "write.csv2(pos_teste,file=\"" + dirR + "resultados\\\\friedman_"+ metodo + problema+ objetivo+ "_gd.csv\")");
		
		comandosSpread.append(	"result<-friedman.test(AR1)\n\n" +
				 "m<-data.frame(result$statistic,result$p.value)\n" +
				 "write.csv2(m,file=\"" +  dirR + "resultados\\\\result_"+ metodo + problema+ objetivo+ "_spread.csv\")\n\n" +
				 "pos_teste<-friedmanmc(AR1)\n" +
				 "write.csv2(pos_teste,file=\"" + dirR + "resultados\\\\friedman_"+ metodo + problema+ objetivo+ "_spread.csv\")");
		
		comandosigd.append(	"result<-friedman.test(AR1)\n\n" +
				 "m<-data.frame(result$statistic,result$p.value)\n" +
				 "write.csv2(m,file=\"" +  dirR + "resultados\\\\result_"+ metodo + problema+ objetivo+ "_igd.csv\")\n\n" +
				 "pos_teste<-friedmanmc(AR1)\n" +
				 "write.csv2(pos_teste,file=\"" + dirR + "resultados\\\\friedman_"+ metodo + problema+ objetivo+ "_igd.csv\")");
		
		
		
		gdSaida.println(comandosGD);
		gdSaida.println(comandosGDBox);
		
		spreSaida.println(comandosSpread);
		spreSaida.println(comandosSpreadBox);
		igdSaida.println(comandosigd);
		igdSaida.println(comandosigdBox);

		
	}

public void preparArquivosComandosFriedmanVis(String dir, String dirR, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
	

	BufferedReader h, s, g;
	
	PrintStream hypSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_hyper_" + objetivo + "_comando_friedman.txt");
	//PrintStream spreSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_spread_" + objetivo + "_comando_friedman.txt");
	PrintStream igdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_igd_" + objetivo + "_comando_friedman.txt");
	
	StringBuffer comandosHyp = new StringBuffer();
	//StringBuffer comandosSpread = new StringBuffer();
	StringBuffer comandosigd = new StringBuffer();
	
	StringBuffer comandosHypBox = new StringBuffer();
	//StringBuffer comandosSpreadBox = new StringBuffer();
	StringBuffer comandosigdBox = new StringBuffer();
	
	int obj = Integer.parseInt(objetivo);
	
	comandosHyp.append("require(pgirmess)\n AR1 <-cbind(");
	//comandosSpread.append("require(pgirmess)\n AR1 <-cbind(");
	comandosigd.append("require(pgirmess)\n AR1 <-cbind(");
	
	comandosHypBox.append("boxplot(");
	//comandosSpreadBox.append("boxplot(");
	comandosigdBox.append("boxplot(");

	for (int j = 0; j < algoritmos.length; j++) {
		String arqHyp = "";
		if(obj<=numObjHiper)
			arqHyp = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
		algoritmos[j] + "_hipervolume_comando.txt";
		//String arqSpread = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" +algoritmos[j] + "_spread_comando.txt";
		String arqigd = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
		algoritmos[j] + "_igd_comando.txt";
		
		
		String algHyp = metodo + problema + "_" + objetivo + algoritmos[j] + "_hipervolume";
		//String algSpread = metodo + problema + "_" + objetivo + algoritmos[j] + "_spread";
		String algigd = metodo + problema + "_" + objetivo + algoritmos[j] + "_igd";
		if(obj<=numObjHiper)
			comandosHyp.append(algHyp + ",");
		//comandosSpread.append(algSpread + ",");
		comandosigd.append(algigd + ",");
		
		if(obj<=numObjHiper)
			comandosHypBox.append(algHyp + ",");
		//comandosSpreadBox.append(algSpread + ",");
		comandosigdBox.append(algigd + ",");
		if(obj<=numObjHiper)
			h = new BufferedReader(new FileReader(arqHyp));
		else
			h = null;
		//s = new BufferedReader(new FileReader(arqSpread));
		g = new BufferedReader(new FileReader(arqigd));
		while(g.ready()){
			if(obj<=numObjHiper)
				hypSaida.println(h.readLine());
			//spreSaida.println(s.readLine());
			igdSaida.println(g.readLine());
		}
		
	}  
	
	if(obj<=numObjHiper)
		hypSaida.println();
	//spreSaida.println();
	igdSaida.println();
	
	comandosHypBox.deleteCharAt(comandosHypBox.length()-1);
	comandosHypBox.append(")\n");
	
	//comandosSpreadBox.deleteCharAt(comandosSpreadBox.length()-1);
	//comandosSpreadBox.append(")\n");
	
	comandosigdBox.deleteCharAt(comandosigdBox.length()-1);
	comandosigdBox.append(")\n");
	
	comandosHyp.deleteCharAt(comandosHyp.length()-1);
	comandosHyp.append(")\n");
	
	//comandosSpread.deleteCharAt(comandosSpread.length()-1);
	//comandosSpread.append(")\n");
	
	comandosigd.deleteCharAt(comandosigd.length()-1);
	comandosigd.append(")\n");
	
	comandosHyp.append(	"result<-friedman.test(AR1)\n\n" +
						 "m<-data.frame(result$statistic,result$p.value)\n" +
						 "write.csv2(m,file=\"" +  dirR + "resultados\\\\result_"+ metodo + problema + objetivo+ "_hyper.csv\")\n\n" +
						 "pos_teste<-friedmanmc(AR1)\n" +
						 "write.csv2(pos_teste,file=\"" + dirR + "resultados\\\\friedman_"+ metodo + problema+ objetivo+ "_hyper.csv\")");
	
	//comandosSpread.append(	"result<-friedman.test(AR1)\n\n" +
	//		 "m<-data.frame(result$statistic,result$p.value)\n" +
	//		 "write.csv2(m,file=\"" +  dirR + "resultados\\\\result_"+ metodo + problema+ objetivo+ "_spread.csv\")\n\n" +
	//		 "pos_teste<-friedmanmc(AR1)\n" +
	//		 "write.csv2(pos_teste,file=\"" + dirR + "resultados\\\\friedman_"+ metodo + problema+ objetivo+ "_spread.csv\")");
	
	comandosigd.append(	"result<-friedman.test(AR1)\n\n" +
			 "m<-data.frame(result$statistic,result$p.value)\n" +
			 "write.csv2(m,file=\"" +  dirR + "resultados\\\\result_"+ metodo + problema+ objetivo+ "_igd.csv\")\n\n" +
			 "pos_teste<-friedmanmc(AR1)\n" +
			 "write.csv2(pos_teste,file=\"" + dirR + "resultados\\\\friedman_"+ metodo + problema+ objetivo+ "_igd.csv\")");
	
	
	
	if(obj<=numObjHiper){
		hypSaida.println(comandosHyp);
		hypSaida.println(comandosHypBox);
	}
	//spreSaida.println(comandosSpread);
	//spreSaida.println(comandosSpreadBox);
	igdSaida.println(comandosigd);
	igdSaida.println(comandosigdBox);

		
}
	
	public static void main(String[] args) {
		PrepararArquivos pre = new PrepararArquivos();
		String dir = "e:\\Andre\\experimentos2\\";
		String dirR = "e:\\\\Andre\\\\experimentos2\\\\";
		//String dir = "/media/disk/Andre/evoCOP2010/";
		int objetivo = 3;
		String problema  = "DTLZ2";
		String[] algs = {"0.25", "0.3", "0.35", "0.4", "0.45", "0.5", "0.55", "0.6", "0.65", "0.7", "0.75"};
		//String[] algs = {"0.4"};
		String[] algsVis = {"0.25", "0.30", "0.35", "0.40", "0.45", "0.50", "0.55", "0.60", "0.65", "0.70", "0.75"};
		String metodo = "smopso";
		int exec = 50;
		try{
			
			//pre.juntarFronteira(dir, problema, objetivo, algs, exec, metodo);
			//pre.inverterMaxMim(dir, problema, objetivo, algs, exec, metodo);
			//if(metodo.equals("vis")){
				//pre.preparArquivosIndicadoresVis(dir, problema, objetivo, algsVis, exec, metodo);
				//pre.preparArquivosComandosFriedmanVis(dir, dirR, problema, objetivo, algsVis, exec, metodo);
			//}
			pre.preparArquivosIndicadores(dir, problema, ""+objetivo, algs, exec, metodo);
			pre.preparArquivosComandosFriedman(dir, dirR, problema, ""+objetivo, algs, exec, metodo);
			
			if(objetivo<5)
				pre.preparArquivosIndicadoresHiper(dir, problema, ""+objetivo, algs, exec, metodo);
			
		} catch (IOException ex){ex.printStackTrace();}
		
		
	}

}
