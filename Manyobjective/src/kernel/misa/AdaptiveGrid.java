package kernel.misa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


import solucao.SolucaoNumerica;

/**
 * Classe que representa um grid adaptativo proposto por Knowles e Corne utilizado como a popula��o secund�ria no algoritmo MISA
 * @author Andre
 *
 */
public class AdaptiveGrid {
	
	//Array de n�mero reais que representa os pontos do grid
	public double[][] grid = null;
	//Tabela hash que cont�m as solu��es de cada c�lula do grid
	public HashMap<Integer, ArrayList<SolucaoNumerica>> solucoes = null;
	//Tabela hash que cont�m qtas solu��es existem em cada c�lula
	public HashMap<Integer, Integer> lotacao = null;
	//N�mero de intervalos do grid passado como par�metro
	public int numIntervalos;
	//Array que cont�m os maiores valores encontrados at� ent�o para cada dimens�o do grid
	public double[] maxValGrid = null;
	//Popula��o m�xima do grid
	public final int MAX_POP = 100;
	
	
	/**
	 * Construtor da classe. 
	 * @param m N�mero de objetivos do problema
	 * @param p N�mero de parti��es do grid
	 */
	public AdaptiveGrid(int m, int p){
		maxValGrid = new double[m];
		grid = new double[m][p+1];
		
		numIntervalos = p;
		
		solucoes = new HashMap<Integer, ArrayList<SolucaoNumerica>>();
		lotacao = new HashMap<Integer, Integer>();
	}
	
	/**
	 * M�todo que adapta o grid aos novos objetivos que ser�o inseridos
	 * Reinsere os elementos no grid de acordo com as novas c�lulas
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
		//Caso sim, calcula as novas c�lulas do grid
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
	 * Obt�m qual c�lula a solu��o ser� inserida. A c�lula � definda por um ponto no grid.
	 * @param objetivos Valores dos objetivos da solu��o
	 * @return O �ndice da c�lula 
	 */
	public int obterCelula(double[] objetivos){
		int indices[] = new int[objetivos.length];
		//Obt�m qual ponto do grid os objetivos est�o associados
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
		//Obt�m um identificador �nica para cada c�lula de acordo com o ponto do grid	
		int quad = indices[0];
		for (int k = 1; k < indices.length; k++) {
			quad +=   indices[k]*(Math.pow(numIntervalos, k)); 
		}
		//System.out.println(quad);
		
		return quad;
	}
	
	/**
	 * M�todo que adiciona um elemento no grid de acordo com o algoritmo MISA
	 * @param solucao Solu��o a ser adicionada
	 * @return True caso sim, false caso n�o
	 */
	public boolean add(SolucaoNumerica solucao){
		construirGrid(solucao.objetivos);
		Integer cel = new Integer(obterCelula(solucao.objetivos));
		ArrayList<SolucaoNumerica> solKey = solucoes.get(cel);
		if(solKey == null){
			solKey = new ArrayList<SolucaoNumerica>();
		}
		//Se a solu��o j� existe no hash n�o a adiciona
		if(solKey.contains(solucao))
			return false;
		
		
		//Se o grid estiver cheio retira um elemento da c�lula mais cheia
		if(isFull()){
			Integer mostCrowded = obterMostCrowded();
			//Se a solu��o pertence � c�lula mais cheia n�o a adiciona
			if(cel.equals(mostCrowded))
			   return false;
			//Retira uma solu��o da c�lula mais cheia aleatoriamente
			ArrayList<SolucaoNumerica> celulaCrowded = solucoes.get(mostCrowded);
			int elementoEliminado = (int)((Math.random() * MAX_POP) % celulaCrowded.size());
			celulaCrowded.remove(elementoEliminado);
			solucoes.put(mostCrowded, celulaCrowded);
			
			Integer contCelula = lotacao.get(mostCrowded)-1;
			lotacao.put(mostCrowded, contCelula);
				
		}
		
		
		//Adiciona a solu��o no grid
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
	 * M�todo que obt�m qual c�lula est� mais cheia no grid
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
	 * Retorna a m�dia de ocupa��o de cada c�lula do grid
	 * @return M�dia
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
	 * M�todo que retorna todas as solu��es dso grid em uma s� lista	
	 * @return Lista com todas as solu��es do grid
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
