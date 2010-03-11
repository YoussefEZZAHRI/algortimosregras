package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class PrepararArquivos {
	
	public static int numObjHiper = 5;
	
	public void preparArquivosIndicadores(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		Double[][] hyper = new Double[algoritmos.length][exec];
		Double[][] spread = new Double[algoritmos.length][exec];
		Double[][] igd = new Double[algoritmos.length][exec];

		BufferedReader h, s, g;
		
		int obj = Integer.parseInt(objetivo);

		for (int j = 0; j < algoritmos.length; j++) {
			
			String arqHyp = "";
			if(obj<=numObjHiper)
				arqHyp = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_hipervolume.txt";
			
			String arqSpread = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread.txt";
			String arqigd = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_igd.txt";
			
			System.out.println(arqSpread);
			
			if(obj<=numObjHiper)
				h = new BufferedReader(new FileReader(arqHyp));
			else
				h = new BufferedReader(new FileReader(arqSpread));
			s = new BufferedReader(new FileReader(arqSpread));
			g = new BufferedReader(new FileReader(arqigd));
			int tam = 0;
			while(s.ready()){
				Double hyp_val = new Double(0);
				if(obj<=numObjHiper)
					hyp_val = new Double(h.readLine());
				
				Double spre_val = new Double(s.readLine());
				
				Double igd_val = new Double(g.readLine());
				if(obj<=numObjHiper)
					hyper[j][tam] = hyp_val;			
				spread[j][tam] = spre_val;
				igd[j][tam++] = igd_val;
			}
		}  
		
		PrintStream hypSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_hyper_" + objetivo + "_indicadores.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_spread_" + objetivo + "_indicadores.txt");
		PrintStream igdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_igd_" + objetivo + "_indicadores.txt");
		
		
		
		for(int j = 0; j<exec; j++){
			for (int i = 0; i < algoritmos.length; i++) {
			try{
				if(obj<=numObjHiper)
					hypSaida.print(hyper[i][j].toString().replace(".", ",") + "\t");
				spreSaida.print(spread[i][j].toString().replace(".", ",") + "\t");
				igdSaida.print(igd[i][j].toString().replace(".", ",") + "\t");	
			}catch(NullPointerException ex){ex.printStackTrace();}
			}
			
			if(obj<=numObjHiper)
				hypSaida.println();
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
		

		BufferedReader h, s, g;
		
		PrintStream hypSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_hyper_" + objetivo + "_comando_friedman.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_spread_" + objetivo + "_comando_friedman.txt");
		PrintStream igdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_igd_" + objetivo + "_comando_friedman.txt");
		
		StringBuffer comandosHyp = new StringBuffer();
		StringBuffer comandosSpread = new StringBuffer();
		StringBuffer comandosigd = new StringBuffer();
		
		StringBuffer comandosHypBox = new StringBuffer();
		StringBuffer comandosSpreadBox = new StringBuffer();
		StringBuffer comandosigdBox = new StringBuffer();
		
		int obj = Integer.parseInt(objetivo);
		
		comandosHyp.append("require(pgirmess)\n AR1 <-cbind(");
		comandosSpread.append("require(pgirmess)\n AR1 <-cbind(");
		comandosigd.append("require(pgirmess)\n AR1 <-cbind(");
		
		comandosHypBox.append("boxplot(");
		comandosSpreadBox.append("boxplot(");
		comandosigdBox.append("boxplot(");

		for (int j = 0; j < algoritmos.length; j++) {
			String arqHyp = "";
			if(obj<=numObjHiper)
				arqHyp = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_hipervolume_comando.txt";
			String arqSpread = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread_comando.txt";
			String arqigd = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_igd_comando.txt";
			
			
			String algHyp = metodo + problema + "_" + objetivo + algoritmos[j] + "_hipervolume";
			String algSpread = metodo + problema + "_" + objetivo + algoritmos[j] + "_spread";
			String algigd = metodo + problema + "_" + objetivo + algoritmos[j] + "_igd";
			if(obj<=numObjHiper)
				comandosHyp.append(algHyp + ",");
			comandosSpread.append(algSpread + ",");
			comandosigd.append(algigd + ",");
			
			if(obj<=numObjHiper)
				comandosHypBox.append(algHyp + ",");
			comandosSpreadBox.append(algSpread + ",");
			comandosigdBox.append(algigd + ",");
			
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
		
		comandosHypBox.deleteCharAt(comandosHypBox.length()-1);
		comandosHypBox.append(")\n");
		
		comandosSpreadBox.deleteCharAt(comandosSpreadBox.length()-1);
		comandosSpreadBox.append(")\n");
		
		comandosigdBox.deleteCharAt(comandosigdBox.length()-1);
		comandosigdBox.append(")\n");
		
		comandosHyp.deleteCharAt(comandosHyp.length()-1);
		comandosHyp.append(")\n");
		
		comandosSpread.deleteCharAt(comandosSpread.length()-1);
		comandosSpread.append(")\n");
		
		comandosigd.deleteCharAt(comandosigd.length()-1);
		comandosigd.append(")\n");
		
		comandosHyp.append(	"result<-friedman.test(AR1)\n\n" +
							 "m<-data.frame(result$statistic,result$p.value)\n" +
							 "write.csv2(m,file=\"" +  dirR + "resultados\\\\result_"+ metodo + problema + objetivo+ "_hyper.csv\")\n\n" +
							 "pos_teste<-friedmanmc(AR1)\n" +
							 "write.csv2(pos_teste,file=\"" + dirR + "resultados\\\\friedman_"+ metodo + problema+ objetivo+ "_hyper.csv\")");
		
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
		
		
		
		if(obj<=numObjHiper){
			hypSaida.println(comandosHyp);
			hypSaida.println(comandosHypBox);
		}
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
		String dir = "E:\\Andre\\sbrn2010\\";
		String dirR = "E:\\\\Andre\\\\sbrn2010\\\\";
		//String dir = "/media/disk/Andre/evoCOP2010/";
		String objetivo = "10";
		String problema  = "DTLZ2";
		String[] algs = {"0.25", "0.3", "0.35", "0.4", "0.45", "0.5", "0.55", "0.6", "0.65", "0.7", "0.75"};
		String[] algsVis = {"0.25", "0.30", "0.35", "0.40", "0.45", "0.50", "0.55", "0.60", "0.65", "0.70", "0.75"};
		String metodo = "misa";
		int exec = 30;
		try{
			if(metodo.equals("vis")){
				pre.preparArquivosIndicadoresVis(dir, problema, objetivo, algsVis, exec, metodo);
				pre.preparArquivosComandosFriedmanVis(dir, dirR, problema, objetivo, algsVis, exec, metodo);
			}
			pre.preparArquivosIndicadores(dir, problema, objetivo, algs, exec, metodo);
			pre.preparArquivosComandosFriedman(dir, dirR, problema, objetivo, algs, exec, metodo);
			
		} catch (IOException ex){ex.printStackTrace();}
		
		
	}

}
