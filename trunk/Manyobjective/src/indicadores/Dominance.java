package indicadores;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import solucao.Solucao;

/**
 * Classe que calcula quantos pontos de uma fronteira dominam pontos da segunda fronteira e vice-versa
 * @author Andre
 *
 */
public class Dominance extends Indicador {
	
	public String idExec1;
	public String idExec2;
	
	public Dominance(int m){
		super(m,"","");
	}
	
	public Dominance(int m, String caminho, String idExec1, String idExec2){
		super(m, caminho, "");
		indicador = "dominance";
		this.idExec1 = idExec1;
		this.idExec2 = idExec2;
	}

	@Override
	public double calcular() {
		return 0;
		
	}
	
	/**
	 * Método que calcula dominância entre as duas fronteiras
	 * @param fronteira1 Primeira fronteira
	 * @param fronteira2 Segunda fronteira
	 * @return
	 */
	public double[] calcular(ArrayList<PontoFronteira> fronteira1, ArrayList<PontoFronteira> fronteira2) {
		if(objetivosMaxMin == null){
			System.err.println("Erro: Não foi definido se cada objetivo é de maximização ou minimização (Executar Método preencherObjetivosMaxMin)");
			System.exit(0);
		}
		double dominacao[] = new double[2];
		for (Iterator<PontoFronteira> iterator = fronteira1.iterator(); iterator.hasNext();) {
			PontoFronteira pontoFronteira1 = (PontoFronteira) iterator.next();
			for (Iterator<PontoFronteira> iterator2 = fronteira2.iterator(); iterator2
					.hasNext();) {
				PontoFronteira pontoFronteira2 = (PontoFronteira) iterator2
						.next();
				int dom = compararMedidas(pontoFronteira1.objetivos, pontoFronteira2.objetivos);
				if(dom == 1)
					dominacao[0]++;
				if(dom == -1)
					dominacao[1]++;
			}
		}
		return dominacao;	
	}
	
	public double[] calcular2(ArrayList<Solucao> fronteira1, ArrayList<Solucao> fronteira2) {
		if(objetivosMaxMin == null){
			System.err.println("Erro: Não foi definido se cada objetivo é de maximização ou minimização (Executar Método preencherObjetivosMaxMin)");
			System.exit(0);
		}
		double dominacao[] = new double[2];
		for (Iterator<Solucao> iterator = fronteira1.iterator(); iterator.hasNext();) {
			Solucao pontoFronteira1 = (Solucao) iterator.next();
			for (Iterator<Solucao> iterator2 = fronteira2.iterator(); iterator2
					.hasNext();) {
				Solucao pontoFronteira2 = (Solucao) iterator2
						.next();
				int dom = compararMedidas(pontoFronteira1.objetivos, pontoFronteira2.objetivos);
				if(dom == 1)
					dominacao[0]++;
				if(dom == -1)
					dominacao[1]++;
			}
		}
		return dominacao;	
	}
	
	/**
	 * Método que calcula o ranking de dominacia entre dois conjuntos de fronteiras
	 * @param nomeArquivo1 Fronteira 1
	 * @param nomeArquivo2 Fronteira 2
	 * @throws IOException
	 */
	public void calcularDominanceArquivo(String nomeArquivo1, String nomeArquivo2) throws IOException{
		
		ArrayList<ArrayList<PontoFronteira>> fronteiras1 = new ArrayList<ArrayList<PontoFronteira>>();
		ArrayList<ArrayList<PontoFronteira>> fronteiras2 = new ArrayList<ArrayList<PontoFronteira>>();
		
		File dir = new File(caminhoSaida);
		dir.mkdirs();
		
		PrintStream psIndGeralF1 = new PrintStream(caminhoSaida + idExec1 + idExec2  + "_" + indicador +".txt");
		PrintStream psIndGeralF2 = new PrintStream(caminhoSaida + idExec2 + idExec1  + "_" + indicador +".txt");
		
		PrintStream psIndComando = new PrintStream(caminhoSaida + idExec1 + idExec2 + "_" + indicador +"_comando.txt");
		
		
		StringBuffer comando1 = new StringBuffer();
		comando1.append(idExec1 + "_" + indicador +"<- c(");
		
		StringBuffer comando2 = new StringBuffer();
		comando2.append(idExec2 + "_" + indicador +"<- c(");
		
		carregarArrayList(nomeArquivo1, fronteiras1);
		carregarArrayList(nomeArquivo2, fronteiras2);
		
		for (int i = 0; i< fronteiras1.size(); i++) {
			ArrayList<PontoFronteira> fronteira1 = (ArrayList<PontoFronteira>) fronteiras1.get(i);
			for (int j = 0; j< fronteiras2.size(); j++) {
				ArrayList<PontoFronteira> fronteira2 = (ArrayList<PontoFronteira>) fronteiras2.get(j);
				
				double[] ranking = calcular(fronteira1, fronteira2);
				
				comando1.append(ranking[0] + ",");
				psIndGeralF1.println(ranking[0]);
				
				comando2.append(ranking[1] + ",");
				psIndGeralF2.println(ranking[1]);
			}
			
		}
		
		
		comando1.deleteCharAt(comando1.length()-1);
		comando1.append(")");
		psIndComando.println(comando1);
		
		comando2.deleteCharAt(comando2.length()-1);
		comando2.append(")");
		
		psIndComando.println();
		
		psIndComando.println(comando2);
		
		psIndComando.println();
		
		psIndComando.println("wilcox.test(" + idExec1 + "_" + indicador + "," + idExec2  + "_" + indicador + ")" );
		psIndComando.println("boxplot(" + idExec1 + "_" + indicador + "," + idExec2  + "_" + indicador + ")" );
		
		
		psIndComando.flush();
		psIndComando.close();
		
		psIndGeralF1.flush();
		psIndGeralF1.close();
		
		psIndGeralF2.flush();
		psIndGeralF2.close();
	}
	

}
