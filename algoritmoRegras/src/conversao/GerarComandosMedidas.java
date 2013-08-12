package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

public class GerarComandosMedidas {
	
	public String[] auc;
	public String[] acc;
	public String[] prec;
	public String[] rec;
	public String[] fm;
	public String[] precneg;
	public String[] recneg;
	public String[] fmneg;
	
	public void gerarComandos(String caminho, String base, int numMedidas) throws Exception{
		String arquivoMedidas = caminho + base +"\\pso_" + base + "_medidas.txt";
		Reader reader = new FileReader(arquivoMedidas);
		BufferedReader buff = new BufferedReader(reader);
		auc = new String[numMedidas];
		acc = new String[numMedidas];
		prec = new String[numMedidas];
		rec = new String[numMedidas];
		fm = new String[numMedidas];
		precneg = new String[numMedidas];
		recneg = new String[numMedidas];
		fmneg = new String[numMedidas];
		
		int i = 0;
		while(i<numMedidas){
			if(i==300)
				i++;
			String linha = buff.readLine();
			
			String[] medidas = linha.split("\t");
			
			auc[i] = medidas[0].replace(',','.');
			acc[i] = medidas[1].replace(',','.');
			prec[i] = medidas[3].replace(',','.');
			rec[i] = medidas[4].replace(',','.');
			fm[i] = medidas[5].replace(',','.');
			precneg[i] = medidas[7].replace(',','.');
			recneg[i] = medidas[8].replace(',','.');
			fmneg[i] = medidas[9].replace(',','.');
			i++;
		}
	
	String arquivoSaida = caminho + base + "\\pso_" + base + "_comandos.txt";
	PrintStream ps = new PrintStream(arquivoSaida);
		
		
	StringBuffer comando = new StringBuffer(); 
	comando.append("auc_psokc1 <- c(");
	for (int j = 0; j < auc.length; j++) {
		String string = auc[j] + ",";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-1);
	comando.append(")\n\n");
	
	ps.println(comando);
	
	comando = new StringBuffer(); 
	comando.append("acc_psokc1 <- c(");
	for (int j = 0; j < acc.length; j++) {
		String string = acc[j] + ",";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-1);
	comando.append(")\n\n");
	
	ps.println(comando);
	
	comando = new StringBuffer(); 
	comando.append("prec_psokc1 <- c(");
	for (int j = 0; j < prec.length; j++) {
		String string = prec[j] + ",";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-1);
	comando.append(")\n\n");
	
	ps.println(comando);
	
	comando = new StringBuffer(); 
	comando.append("rec_psokc1 <- c(");
	for (int j = 0; j < rec.length; j++) {
		String string = rec[j] + ",";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-1);
	comando.append(")\n\n");
	
	ps.println(comando);
	
	comando = new StringBuffer(); 
	comando.append("fm_psokc1 <- c(");
	for (int j = 0; j < fm.length; j++) {
		String string = fm[j] + ",";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-1);
	comando.append(")\n\n");
	
	ps.println(comando);
	
	comando = new StringBuffer(); 
	comando.append("prec_neg_psokc1 <- c(");
	for (int j = 0; j < precneg.length; j++) {
		String string = precneg[j] + ",";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-1);
	comando.append(")\n\n");
	
	ps.println(comando);
	
	comando = new StringBuffer(); 
	comando.append("rec_neg_psokc1 <- c(");
	for (int j = 0; j < recneg.length; j++) {
		String string = recneg[j] + ",";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-1);
	comando.append(")\n\n");
	
	ps.println(comando);
	
	comando = new StringBuffer(); 
	comando.append("fm_neg_psokc1 <- c(");
	for (int j = 0; j < fmneg.length; j++) {
		String string = fmneg[j] + ",";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-1);
	comando.append(")\n\n");
	
	ps.println(comando);
	

	
	}
	
	public static void main(String[] args) {
		GerarComandosMedidas c = new GerarComandosMedidas();
		String caminho = "C:\\Andre\\revista\\resultados\\pso\\";
		String base = "kc1_class_defeito_numerico";
		try{
			c.gerarComandos(caminho,base,300);
		}catch(Exception ex){ex.printStackTrace();}
		
	}

}
