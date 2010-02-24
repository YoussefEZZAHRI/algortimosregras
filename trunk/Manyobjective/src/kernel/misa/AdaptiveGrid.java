package kernel.misa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


import solucao.SolucaoNumerica;

/**
 * Classe que representa um grid adaptativo proposto por Knowles e Corne utilizado como a população secundária no algoritmo MISA
 * @author Andre
 *
 */
public class AdaptiveGrid {
	
	//Array de número reais que representa os pontos do grid
	public double[][] grid = null;
	//Tabela hash que contém as soluções de cada célula do grid
	public HashMap<Integer, ArrayList<SolucaoNumerica>> solucoes = null;
	//Tabela hash que contém qtas soluções existem em cada célula
	public HashMap<Integer, Integer> lotacao = null;
	//Número de intervalos do grid passado como parâmetro
	public int numIntervalos;
	//Array que contém os maiores valores encontrados até então para cada dimensão do grid
	public double[] maxValGrid = null;
	//População máxima do grid
	public final int MAX_POP = 100;
	
	
	/**
	 * Construtor da classe. 
	 * @param m Número de objetivos do problema
	 * @param p Número de partições do grid
	 */
	public AdaptiveGrid(int m, int p){
		maxValGrid = new double[m];
		grid = new double[m][p+1];
		
		numIntervalos = p;
		
		solucoes = new HashMap<Integer, ArrayList<SolucaoNumerica>>();
		lotacao = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Método que adapta o grid aos novos objetivos que serão inseridos
	 * Reinsere os elementos no grid de acordo com as novas células
	 * @param objetivos
	 */
	public void construirGrid(double[] objetivos){
		boolean modificaoGrid = false;
		//Verifica se os valores do objetivos excedem os limites do grid
		for (int i = 0; i < objetivos.length; i++) {
			double objetivo = objetivos[i];
			if(objetivo>maxValGrid[i]){
				modificaoGrid = true;
				maxValGrid[i] = objetivo;
			}
		}
		//Caso sim, calcula as novas células do grid
		if(modificaoGrid){
			for(int i = 0; i<objetivos.length; i++){
				double intervalo = maxValGrid[i]/numIntervalos;
				grid[i][0] = 0;
				for(int j = 1; j<=numIntervalos; j++)
					grid[i][j] = grid[i][j-1] + intervalo;		 
			}
			//Reinsere os elementos no novo grid
			HashMap<Integer, ArrayList<SolucaoNumerica>> clone = new HashMap<Integer, ArrayList<SolucaoNumerica>>();
			clone.putAll(solucoes);
			solucoes = new HashMap<Integer, ArrayList<SolucaoNumerica>>();
			lotacao = new HashMap<Integer, Integer>();
			for (Iterator<ArrayList<SolucaoNumerica>> iterator = clone.values().iterator(); iterator.hasNext();) {
				ArrayList<SolucaoNumerica> sols = iterator.next();
				for (Iterator<SolucaoNumerica> iterator2 = sols.iterator(); iterator2.hasNext();) {
					SolucaoNumerica solucao = (SolucaoNumerica) iterator2.next();
					//add(solucao);
					Integer cel = new Integer(obterCelula(solucao.objetivos));
					ArrayList<SolucaoNumerica> solKey = solucoes.get(cel);
					if(solKey == null){
						solKey = new ArrayList<SolucaoNumerica>();
					}
					solKey.add(solucao);
					solucoes.put(cel, solKey);
					
					Integer contCelula = lotacao.get(cel);
					if(contCelula == null)
						contCelula = new Integer(0);
					contCelula++;
					lotacao.put(cel, contCelula);
				}
			}
			/*for(int i = 0; i<objetivos.length; i++){
				System.out.print(i + ": ");
				for(int j = 0; j<=numIntervalos; j++)
					System.out.print(grid[i][j] + " ");
				System.out.println();

			}*/
		}
	}
	
	/**
	 * Obtém qual célula a solução será inserida. A célula é definda por um ponto no grid.
	 * @param objetivos Valores dos objetivos da solução
	 * @return O índice da célula 
	 */
	public int obterCelula(double[] objetivos){
		int indices[] = new int[objetivos.length];
		//Obtém qual ponto do grid os objetivos estão associados
		//Representado pelo indice do ponto na matriz grid
		for (int i = 0; i < objetivos.length; i++) {
			double objetivo = objetivos[i];
			int indice = 0;
			for (int j = 1; j < grid[i].length; j++) {
				double ponto = grid[i][j];
				if(objetivo<ponto)
					break;
				else
					indice++;
			}
			indices[i] = indice;
		}
		//Obtém um identificador única para cada célula de acordo com o ponto do grid	
		int quad = indices[0];
		for (int k = 1; k < indices.length; k++) {
			quad +=   indices[k]*(Math.pow(numIntervalos, k)); 
		}
		//System.out.println(quad);
		
		return quad;
	}
	
	/**
	 * Método que adiciona um elemento no grid de acordo com o algoritmo MISA
	 * @param solucao Solução a ser adicionada
	 * @return True caso sim, false caso não
	 */
	public boolean add(SolucaoNumerica solucao){
		construirGrid(solucao.objetivos);
		Integer cel = new Integer(obterCelula(solucao.objetivos));
		ArrayList<SolucaoNumerica> solKey = solucoes.get(cel);
		if(solKey == null){
			solKey = new ArrayList<SolucaoNumerica>();
		}
		//Se a solução já existe no hash não a adiciona
		if(solKey.contains(solucao))
			return false;
		
		
		//Se o grid estiver cheio retira um elemento da célula mais cheia
		if(isFull()){
			Integer mostCrowded = obterMostCrowded();
			//Se a solução pertence à célula mais cheia não a adiciona
			if(cel.equals(mostCrowded))
			   return false;
			//Retira uma solução da célula mais cheia aleatoriamente
			ArrayList<SolucaoNumerica> celulaCrowded = solucoes.get(mostCrowded);
			int elementoEliminado = (int)((Math.random() * MAX_POP) % celulaCrowded.size());
			celulaCrowded.remove(elementoEliminado);
			solucoes.put(mostCrowded, celulaCrowded);
			
			Integer contCelula = lotacao.get(mostCrowded)-1;
			lotacao.put(mostCrowded, contCelula);
				
		}
		
		
		//Adiciona a solução no grid
		solKey.add(solucao);
		solucoes.put(cel, solKey);
		
		Integer contCelula = lotacao.get(cel);
		if(contCelula == null)
			contCelula = new Integer(0);
		contCelula++;
		lotacao.put(cel, contCelula);
		
		return true;
	}
	
	/**
	 * Método que obtém qual célula está mais cheia no grid
	 * @return
	 */
	public Integer obterMostCrowded(){
		int mostCrowded = 0;
		int crowdedCel = -1;
		for (Iterator<Integer> iterator = lotacao.keySet().iterator(); iterator.hasNext();) {
			Integer celula = (Integer) iterator.next();
			Integer celCrowded = lotacao.get(celula);
			if(celCrowded>mostCrowded){
				mostCrowded = celCrowded.intValue();
				crowdedCel = celula.intValue(); 
			}
				
			
		}
		return new Integer(crowdedCel);
	}
	
	public int obterLotacao(Integer celula, SolucaoNumerica solucao){
		Integer lot = lotacao.get(celula);
		return lot.intValue();
	}
	
	
	/**
	 * Retorna a média de ocupação de cada célula do grid
	 * @return Média
	 */
	public double obterMediaOcupacao(){
		int soma = 0;
		Set<Integer> lot = lotacao.keySet();
		for (Iterator<Integer> iterator = lot.iterator(); iterator.hasNext();) {
			Integer integer = (Integer) iterator.next();
			soma+= integer;
		}
		return soma/lot.size();
	}
	
	/**
	 * Método que retorna todas as soluções dso grid em uma só lista	
	 * @return Lista com todas as soluções do grid
	 */
	public ArrayList<SolucaoNumerica> getAll(){
		ArrayList<SolucaoNumerica> retorno = new ArrayList<SolucaoNumerica>();
		for (Iterator<ArrayList<SolucaoNumerica>> iterator = solucoes.values().iterator(); iterator.hasNext();) {
			ArrayList<SolucaoNumerica> sols = iterator.next();
			retorno.addAll(sols);
		}
		return retorno;
	}
	
	
	public boolean isFull(){
		if(size()>=MAX_POP)
			return true;
		else 
			return false;
	}
	
	public int size(){
		int soma = 0;
		for (Iterator<Integer> iterator = lotacao.keySet().iterator(); iterator.hasNext();) {
			Integer celula = (Integer) iterator.next();
			soma += lotacao.get(celula);
		}
				
		return soma;
	}
	
	public int contains(Object o){
		SolucaoNumerica solucao = (SolucaoNumerica) o;
		for (Iterator<Integer> iterator = lotacao.keySet().iterator(); iterator.hasNext();) {
			Integer celula = (Integer) iterator.next();
			ArrayList<SolucaoNumerica> solKey = solucoes.get(celula);
			if(solKey.contains(solucao)){
				return celula;
			}
		}
		return -1;
		
	}
	
	public static void main(String[] args) {
		
			
	}

}
