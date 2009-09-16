package indicadores;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;



/**
 * Calcula o hipervolume de um conjunto de pontos, somente para minimização
 * @author Andre
 *
 */
public class Hipervolume extends Indicador{
	
	public double[] limites;
	
	/**
	 * Construtor da classe que calcula o hipervolume.
	 * @param m Número de objetivos do problema
	 * @param l Limites superiores para cada objetivo
	 */
	public Hipervolume(int m, String caminho, String idExec, double[] l){
		super(m, caminho, idExec);
		indicador = "hipervolume";
		limites = l;
	}
		
	/**
	 * Funciona pior que ordenar as solucoes pelo primeiro objetivo
	 * Classe que ordena as soluções de acordo com seus objetivos
	 * Soluções com valores mais baixos dos objetivos são melhores
	 * @param fronteira
	 */
	/*@Deprecated 
	public void ordenarSolucoes(){
		if(fronteira != null){
			double[] medias = new double[m];
			//Calcula a média para cada objetivo
			for (Iterator<PontoFronteira> iterator = fronteira.iterator(); iterator.hasNext();) {
				PontoFronteira solucao = (PontoFronteira) iterator.next();
				solucao.objetivosAcimaMedia = 0;
				for (int i = 0; i < medias.length; i++) {
					solucao.soma += solucao.objetivos[i]; 
					medias[i]+= solucao.objetivos[i];
				}	
			}

			for (int i = 0; i < medias.length; i++) {
				medias[i] /= fronteira.size(); 
			}


			//Marca para cada solução quantos objetivos estão acima da média
			for (Iterator<PontoFronteira> iterator = fronteira.iterator(); iterator.hasNext();) {
				PontoFronteira solucao = iterator.next();
				for(int i = 0; i<m; i++){
					if(solucao.objetivos[i]>medias[i])
						solucao.objetivosAcimaMedia++;
				}
			}

			ComparetorHipervolume comp = new ComparetorHipervolume();
			Collections.sort(fronteira, comp);
		} else{
			System.err.println("Fronteira de Pareto não carregada");
			System.exit(0);
		}
	}*/
	
	/**
	 * Método que calcula o hipervolume atraves do algoritmo de Leb Measure
	 * @param list
	 * @return
	 */
	public double calcular(){
		
		if(objetivosMaxMin == null){
			System.err.println("Erro: Não foi definido se cada objetivo é de maximização ou minimização (Executar Método preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			Stack<double[]> list = new Stack<double[]>();
			
			//Ordena as soluções pelo primeiro objetivo para acelerar o calculo do hipervolume
			ComparetorObjetivoPontoFronteira comp = new ComparetorObjetivoPontoFronteira(0);
			Collections.sort(fronteira, comp);

			//ordenarSolucoes();

			for (Iterator<PontoFronteira> iterator = fronteira.iterator(); iterator
			.hasNext();) {
				PontoFronteira sol = iterator.next();	
				list.add(sol.objetivos);
			}

			double lebMeasure = 0;

			ArrayList<double[]> spawnData = new ArrayList<double[]>();
			int k = 0;
			//int tamAnterior = list.size();
			//System.out.println(k + " - " + list.size());
			while(list.size()>0){
				/*if(tamAnterior - list.size() == 10){
					tamAnterior = list.size();
					System.out.println(k + " - " + list.size());
				}*/
				k++;
				double lopOffVol = 1;
				double[] p1 = list.pop();
				for(int i = 0; i<m; i++){
					double bi = getBoundValue(p1[i], i, list);
					double[] spawnVector = spawnVector(p1, i, bi);
					spawnData.add(spawnVector);
					lopOffVol *= Math.abs(p1[i]-bi);
				}
				lebMeasure +=lopOffVol;
				ndFilter(list, spawnData);


				spawnData.clear();
			}

			return lebMeasure;
		} else{
			System.err.println("Erro no cálculo do Hipervolume: Fronteira de Pareto não carregada.");
			System.exit(0);
			return 0;
		}
		
	}
	
	
	
	/**
	 * Método que retorna o menor valor que é dominado pelo valor fx para o objetivo i, dentre as solucoes em tail
	 * @param fxi Valor da objetivo corrente
	 * @param i Índice do objetivo
	 * @param tail Soluções em que será procurado o valor limite
	 * @return
	 */
	public double getBoundValue(double fxi, int i, Stack<double[]> tail){
		double ui_li = limites[i];
		for (Iterator<double[]> iterator = tail.iterator(); iterator.hasNext();) {
			double[] objetivos_i = (double[]) iterator.next();
			//Se o objetivo for de maximizacao, busca o menor valor que seja maior que fxi
			if(objetivosMaxMin[i] == -1){
				if(objetivos_i[i]>fxi){
					if(objetivos_i[i]<ui_li)
						ui_li = objetivos_i[i];
				}
			}
			else{
				if(objetivos_i[i]<fxi){
					if(objetivos_i[i]>ui_li)
						ui_li = objetivos_i[i];
				}
			}
			
		}
		return ui_li;
	}
	
	/**
	 * Método que cria m novos vetores baseado na remoção de p1 da lista
	 * @param p1 Vetor que será retirado da lista
	 * @param i Índice do objetivo tratado 
	 * @param bi Limite para o índice do objetivo
	 * @return Novo vetor baseado em p1
	 */
	public double[] spawnVector(double[] p1, int i, double bi){
		double[] novoVetor = new double[m];
		for (int k = 0; k < p1.length; k++) {
			novoVetor[k] = p1[k];
		}
		novoVetor[i] = bi;

		return novoVetor;
	}
	
	/**
	 * Método que retira dos spawn vector as soluções que sejam dominadas por soluções presentes na lista
	 * @param list Lista com as soluções que será utilizadas para o cálculo do hipervolume
	 * @param spawn spawn vector obtido através da solucao p1
	 */
	public void ndFilter(Stack<double[]> list, ArrayList<double[]> spawn){
		
		ArrayList<double[]> temp = new ArrayList<double[]>();
		for (Iterator<double[]> iterator = spawn.iterator(); iterator.hasNext();) {
			boolean adiconar = true;
			double[] ds = (double[]) iterator.next();
			for (int i = 0; i < ds.length; i++) {
				if(ds[i] == limites[i]){
					adiconar = false;
					break;
				}
			}
			if(adiconar){
				for (Iterator<double[]> iterator2 = list.iterator(); iterator2.hasNext();) {
					double[] ds2 = (double[]) iterator2.next();
					int dom = compararMedidas(ds2, ds);
					if(dom == 1){
						adiconar = false;
						break;
					}
				}
				if(adiconar)
					temp.add(ds);
			}
		}
		
		for (int i = 1; i <=temp.size(); i++) {	
			double[] ds = (double[]) temp.get(temp.size()-i);
			list.push(ds);
		}
			
	}
	
	

}
