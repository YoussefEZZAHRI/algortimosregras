package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class PrepararArquivos {
	
	public void preparArquivosIndicadores(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		Double[][] hyper = new Double[algoritmos.length][exec];
		Double[][] spread = new Double[algoritmos.length][exec];
		Double[][] gd = new Double[algoritmos.length][exec];

		BufferedReader h, s, g;
		
		int obj = Integer.parseInt(objetivo);

		for (int j = 0; j < algoritmos.length; j++) {
			
			String arqHyp = "";
			if(obj<4)
				arqHyp = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_hipervolume.txt";
			
			String arqSpread = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread.txt";
			String arqGd = dir + "resultados/" + metodo + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_gd.txt";
			
			System.out.println(arqSpread);
			
			if(obj<4)
				h = new BufferedReader(new FileReader(arqHyp));
			else
				h = new BufferedReader(new FileReader(arqSpread));
			s = new BufferedReader(new FileReader(arqSpread));
			g = new BufferedReader(new FileReader(arqGd));
			int tam = 0;
			while(s.ready()){
				Double hyp_val = new Double(0);
				if(obj<4)
					hyp_val = new Double(h.readLine());
				
				Double spre_val = new Double(s.readLine());
				
				Double gd_val = new Double(g.readLine());
				if(obj<4)
					hyper[j][tam] = hyp_val;			
				spread[j][tam] = spre_val;
				gd[j][tam++] = gd_val;
			}
		}  
		
		PrintStream hypSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_hyper_" + objetivo + "_indicadores.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_spread_" + objetivo + "_indicadores.txt");
		PrintStream gdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_gd_" + objetivo + "_indicadores.txt");
		
		for(int j = 0; j<exec; j++){
			for (int i = 0; i < algoritmos.length; i++) {
			try{
				if(obj<4)
					hypSaida.print(hyper[i][j].toString().replace(".", ",") + "\t");
				spreSaida.print(spread[i][j].toString().replace(".", ",") + "\t");
				gdSaida.print(gd[i][j].toString().replace(".", ",") + "\t");	
			}catch(NullPointerException ex){ex.printStackTrace();}
			}
		}
			if(obj<4)
				hypSaida.println();
			spreSaida.println();
			gdSaida.println();
		
	}
	
	public void preparArquivosComandosWilcox(String dir, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		

		BufferedReader h, s, g;
		
		PrintStream hypSaida = new PrintStream(dir + "resultados/hyper_" + objetivo + "_comando.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/spread_" + objetivo + "_comando.txt");
		PrintStream gdSaida = new PrintStream(dir + "resultados/gd_" + objetivo + "_comando.txt");
		
		StringBuffer comandosHyp = new StringBuffer();
		StringBuffer comandosSpread = new StringBuffer();
		StringBuffer comandosGD = new StringBuffer();
		
		String algCompHyp = metodo + problema + "_" + objetivo + "0.5_hipervolume";
		String algCompSpread = metodo + problema + "_" + objetivo + "0.5_spread";
		String algCompGD = metodo + problema + "_" + objetivo + "0.5_gd";
		
		int obj = Integer.parseInt(objetivo);

		for (int j = 0; j < algoritmos.length; j++) {
			String arqHyp = "";
			if(obj<4)
				arqHyp = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_hipervolume_comando.txt";
			String arqSpread = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread_comando.txt";
			String arqGd = dir + "resultados/" + metodo + problema + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_gd_comando.txt";
			
			if(!algoritmos[j].equals("0.5")){
				String algHyp = metodo + problema + "_" + objetivo + algoritmos[j] + "_hipervolume";
				String algSpread = metodo + problema + "_" + objetivo + algoritmos[j] + "_spread";
				String algGD = metodo + problema + "_" + objetivo + algoritmos[j] + "_gd";
				if(obj<4)
					comandosHyp.append("wilcox.test(" + algHyp + "," + algCompHyp + ")\n");
				comandosSpread.append("wilcox.test(" + algSpread + "," + algCompSpread + ")\n");
				comandosGD.append("wilcox.test(" + algGD + "," + algCompGD + ")\n");
			}
			
			
			if(obj<4)
				h = new BufferedReader(new FileReader(arqHyp));
			else
				h = new BufferedReader(new FileReader(arqSpread));
			s = new BufferedReader(new FileReader(arqSpread));
			g = new BufferedReader(new FileReader(arqGd));
			while(s.ready()){
				if(obj<4)
					hypSaida.println(h.readLine());
				spreSaida.println(s.readLine());
				gdSaida.println(g.readLine());
			}
			
		}  
		
		if(obj<4)
			hypSaida.println();
		spreSaida.println();
		gdSaida.println();
		
		if(obj<4)
			hypSaida.println(comandosHyp);
		spreSaida.println(comandosSpread);
		gdSaida.println(comandosGD);

			
	}
	
public void preparArquivosComandosFriedman(String dir, String dirR, String problema, String objetivo, String[] algoritmos , int exec, String metodo) throws IOException{
		

		BufferedReader h, s, g;
		
		PrintStream hypSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_hyper_" + objetivo + "_comando_friedman.txt");
		PrintStream spreSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_spread_" + objetivo + "_comando_friedman.txt");
		PrintStream gdSaida = new PrintStream(dir + "resultados/" + metodo + problema + "_gd_" + objetivo + "_comando_friedman.txt");
		
		StringBuffer comandosHyp = new StringBuffer();
		StringBuffer comandosSpread = new StringBuffer();
		StringBuffer comandosGD = new StringBuffer();
		
		StringBuffer comandosHypBox = new StringBuffer();
		StringBuffer comandosSpreadBox = new StringBuffer();
		StringBuffer comandosGDBox = new StringBuffer();
		
		int obj = Integer.parseInt(objetivo);
		
		comandosHyp.append("require(pgirmess)\n AR1 <-cbind(");
		comandosSpread.append("require(pgirmess)\n AR1 <-cbind(");
		comandosGD.append("require(pgirmess)\n AR1 <-cbind(");
		
		comandosHypBox.append("boxplot(");
		comandosSpreadBox.append("boxplot(");
		comandosGDBox.append("boxplot(");

		for (int j = 0; j < algoritmos.length; j++) {
			String arqHyp = "";
			if(obj<4)
				arqHyp = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_hipervolume_comando.txt";
			String arqSpread = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_spread_comando.txt";
			String arqGd = dir + "resultados/" + metodo  + "/" + problema + "/" + objetivo + "/" + 
			algoritmos[j] + "/" + metodo + "" + problema + "_" + objetivo + algoritmos[j] + "_gd_comando.txt";
			
			
			String algHyp = metodo + problema + "_" + objetivo + algoritmos[j] + "_hipervolume";
			String algSpread = metodo + problema + "_" + objetivo + algoritmos[j] + "_spread";
			String algGD = metodo + problema + "_" + objetivo + algoritmos[j] + "_gd";
			if(obj<4)
				comandosHyp.append(algHyp + ",");
			comandosSpread.append(algSpread + ",");
			comandosGD.append(algGD + ",");
			
			if(obj<4)
				comandosHypBox.append(algHyp + ",");
			comandosSpreadBox.append(algSpread + ",");
			comandosGDBox.append(algGD + ",");
			
			if(obj<4)
				h = new BufferedReader(new FileReader(arqHyp));
			else
				h = new BufferedReader(new FileReader(arqSpread));
			s = new BufferedReader(new FileReader(arqSpread));
			g = new BufferedReader(new FileReader(arqGd));
			while(s.ready()){
				if(obj<4)
					hypSaida.println(h.readLine());
				spreSaida.println(s.readLine());
				gdSaida.println(g.readLine());
			}
			
		}  
		
		if(obj<4)
			hypSaida.println();
		spreSaida.println();
		gdSaida.println();
		
		comandosHypBox.deleteCharAt(comandosHypBox.length()-1);
		comandosHypBox.append(")\n");
		
		comandosSpreadBox.deleteCharAt(comandosSpreadBox.length()-1);
		comandosSpreadBox.append(")\n");
		
		comandosGDBox.deleteCharAt(comandosGDBox.length()-1);
		comandosGDBox.append(")\n");
		
		comandosHyp.deleteCharAt(comandosHyp.length()-1);
		comandosHyp.append(")\n");
		
		comandosSpread.deleteCharAt(comandosSpread.length()-1);
		comandosSpread.append(")\n");
		
		comandosGD.deleteCharAt(comandosGD.length()-1);
		comandosGD.append(")\n");
		
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
		
		comandosGD.append(	"result<-friedman.test(AR1)\n\n" +
				 "m<-data.frame(result$statistic,result$p.value)\n" +
				 "write.csv2(m,file=\"" +  dirR + "resultados\\\\result_"+ metodo + problema+ objetivo+ "_gd.csv\")\n\n" +
				 "pos_teste<-friedmanmc(AR1)\n" +
				 "write.csv2(pos_teste,file=\"" + dirR + "resultados\\\\friedman_"+ metodo + problema+ objetivo+ "_gd.csv\")");
		
		
		
		if(obj<4){
			hypSaida.println(comandosHyp);
			hypSaida.println(comandosHypBox);
		}
		spreSaida.println(comandosSpread);
		spreSaida.println(comandosSpreadBox);
		gdSaida.println(comandosGD);
		gdSaida.println(comandosGDBox);

			
	}
	
	public static void main(String[] args) {
		PrepararArquivos pre = new PrepararArquivos();
		String dir = "E:\\Andre\\evoCOP2010\\";
		String dirR = "E:\\\\Andre\\\\evoCOP2010\\\\";
		//String dir = "/media/disk/Andre/evoCOP2010/";
		String objetivo = "4";
		String problema  = "DTLZ4";
		String[] algs = {"0.25", "0.3", "0.35", "0.4", "0.45", "0.5", "0.55", "0.6", "0.65", "0.7", "0.75"};
		String metodo = "sigma";
		int exec = 50;
		try{
			pre.preparArquivosIndicadores(dir, problema, objetivo, algs, exec, metodo);
			pre.preparArquivosComandosFriedman(dir, dirR, problema, objetivo, algs, exec, metodo);
		} catch (IOException ex){ex.printStackTrace();}
		
		
	}

}
