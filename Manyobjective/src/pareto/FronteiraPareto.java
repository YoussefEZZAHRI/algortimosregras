package pareto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import kernel.nuvemparticulas.ComparetorCrowdedOperatorParticula;
import kernel.nuvemparticulas.ComparetorRankParticula;
import kernel.nuvemparticulas.Particula;

import solucao.ComparetorCrowdDistance;
import solucao.ComparetorCrowdedOperator;
import solucao.ComparetorRank;
import solucao.Solucao;
import solucao.SolucaoNumerica;
import sun.java2d.pipe.SolidTextRenderer;

public class FronteiraPareto {
	
	public ArrayList<Solucao> fronteira = null;
	
	public ArrayList<Particula> fronteiraNuvem = null;
	
	public double S;
	
	public boolean rank;
	
	public double[] objetivosMaxMin = null;
	
	
	/*public FronteiraPareto(double s){
		fronteira = new ArrayList<Solucao>();
		fronteiraNuvem = new ArrayList<Particula>();
		S = s;
		
	}*/
	
	public FronteiraPareto(double s, String[] maxmim, boolean r){
		fronteira = new ArrayList<Solucao>();
		fronteiraNuvem = new ArrayList<Particula>();
		S = s;
		rank= r;
		preencherObjetivosMaxMin(maxmim);
	}
	
	/**
	 * Método que define para cada objetivo se ele é de maximização ou minimização
	 * @param maxmim
	 */
	public void preencherObjetivosMaxMin(String[] maxmim){
		objetivosMaxMin = new double[maxmim.length];
		for (int i = 0; i < maxmim.length; i++) {
			if(maxmim[i].equals("+"))
				objetivosMaxMin[i] = 1;
			else
				objetivosMaxMin[i] = -1;
		}
	}
	
	public void setFronteira(ArrayList<SolucaoNumerica> temp){
		fronteira.clear();
		for (Iterator<SolucaoNumerica> iter = temp.iterator(); iter.hasNext();) {
			SolucaoNumerica s = (SolucaoNumerica) iter.next();
			fronteira.add(s);
			
		}
	}
	
	public void setFronteiraNuvem(ArrayList<Particula> temp){
		fronteiraNuvem.clear();
		for (Iterator<Particula> iter = temp.iterator(); iter.hasNext();) {
			Particula p = (Particula) iter.next();
			fronteiraNuvem.add(p);
			
		}
	}
	
	public void apagarFronteira(){
		fronteira.clear();
	}
	
	public void apagarFronteiraNuvem(){
		fronteiraNuvem.clear();
	}
	
	/**
	 * Método que adiciona um nova solução na fronteira de pareto
	 * @param regra Regra a ser adicionada
	 * @return Valor booleano que especifica se o elemento foi inserido ou nao na fronteira 
	 */
	public double add(Solucao solucao){
		//Só adiciona na fronteira caso a regra seja da classe passada como parametro
		solucao.numDominacao = 0;
		if(fronteira.size()==0){
			fronteira.add(solucao);
			return solucao.numDominacao;
		}
		
		int comp;
		
		ArrayList<SolucaoNumerica> cloneFronteira = (ArrayList<SolucaoNumerica>)fronteira.clone();
		
		double[] novosObjetivosSolucao = new double[solucao.objetivos.length];

		double r = r(solucao.objetivos);
		for (int i = 0; i < solucao.objetivos.length; i++) {
			novosObjetivosSolucao[i] = modificacaoDominanciaPareto(solucao.objetivos[i], r, S);
		}
		
		
		for (Iterator<SolucaoNumerica> iter = cloneFronteira.iterator(); iter.hasNext();) {
			SolucaoNumerica temp = (SolucaoNumerica) iter.next();
			
			double[] novosObjetivosTemp = new double[temp.objetivos.length];
		
			r = r(temp.objetivos);
			for (int i = 0; i < temp.objetivos.length; i++) {
				novosObjetivosTemp[i] = modificacaoDominanciaPareto(temp.objetivos[i], r, S);
			}
		
			
			comp = compararMedidas(novosObjetivosSolucao, novosObjetivosTemp);
			
			if(comp == -1)
				solucao.numDominacao++;
			if(comp == 1)
				fronteira.remove(temp);
			
		}
		if(solucao.numDominacao == 0){
			fronteira.add(solucao);	
		}
		
		return solucao.numDominacao;
		
	}
	
	
	public double add(Particula particula){
		//Só adiciona na fronteira caso a regra seja da classe passada como parametro
		particula.solucao.numDominacao = 0;
		if(fronteiraNuvem.size()==0){
			fronteiraNuvem.add(particula);
			return particula.solucao.numDominacao;
		}
		
		int comp;
		
		ArrayList<Particula> cloneFronteira = (ArrayList<Particula>)fronteiraNuvem.clone();
		
		SolucaoNumerica solucao = particula.solucao;
		double[] novosObjetivosSolucao = new double[solucao.objetivos.length];
		
		double r = r(solucao.objetivos);
		for (int i = 0; i < solucao.objetivos.length; i++) {
			novosObjetivosSolucao[i] = modificacaoDominanciaPareto(solucao.objetivos[i], r, S);
		}
			
		
		for (Iterator<Particula> iter = cloneFronteira.iterator(); iter.hasNext();) {
			Particula temp = (Particula) iter.next();
			
			double[] novosObjetivosTemp = new double[temp.solucao.objetivos.length];
			r = r(temp.solucao.objetivos);
			for (int i = 0; i < temp.solucao.objetivos.length; i++) {
				novosObjetivosTemp[i] = modificacaoDominanciaPareto(temp.solucao.objetivos[i], r, S);
			}

			
			comp = compararMedidas(novosObjetivosSolucao, novosObjetivosTemp);
			
			
			if(comp == -1){
				particula.solucao.numDominacao++;
			}
			if(comp == 1)
				fronteiraNuvem.remove(temp);
			
		}
		if(particula.solucao.numDominacao==0){
			fronteiraNuvem.add(particula);
			return particula.solucao.numDominacao;
		} else{
			return particula.solucao.numDominacao;
		}
	}
	
	public void podarLideresCrowd(int tamanhoRepositorio){
		if(tamanhoRepositorio<fronteiraNuvem.size()){
			ComparetorCrowdDistance comp = new ComparetorCrowdDistance();
			//ComparetorCrowdedOperator comp = new ComparetorCrowdedOperator();
			Collections.sort(fronteiraNuvem, comp);
			int diferenca = fronteiraNuvem.size() - tamanhoRepositorio; 
			for(int i = 0; i<diferenca; i++)
				fronteiraNuvem.remove(fronteiraNuvem.remove(fronteiraNuvem.size()-1));
			retornarFronteiraNuvem();
		}
	}
	
	public void podarLideresCrowdOperatorParticula(int tamanhoRepositorio){
		if(tamanhoRepositorio<fronteiraNuvem.size()){
			ComparetorCrowdedOperatorParticula comp = new ComparetorCrowdedOperatorParticula();
			Collections.sort(fronteiraNuvem, comp);
			int diferenca = fronteiraNuvem.size() - tamanhoRepositorio; 
			for(int i = 0; i<diferenca; i++)
				fronteiraNuvem.remove(fronteiraNuvem.remove(fronteiraNuvem.size()-1));
			retornarFronteiraNuvem();
		}
	}
	
	public void podarLideresRank(int tamanhoRepositorio){
		if(tamanhoRepositorio<fronteiraNuvem.size()){
			ComparetorRankParticula	comp = new ComparetorRankParticula();
			Collections.sort(fronteiraNuvem, comp);
			int diferenca = fronteiraNuvem.size() - tamanhoRepositorio; 
			for(int i = 0; i<diferenca; i++)
				fronteiraNuvem.remove(fronteiraNuvem.remove(fronteiraNuvem.size()-1));
			retornarFronteiraNuvem();
		}
	}
	
	
	
	
	public void retornarFronteiraNuvem(){
		fronteira.clear();
		for (Iterator<Particula> iterator = fronteiraNuvem.iterator(); iterator.hasNext();) {
			Particula particula = (Particula) iterator.next();
			fronteira.add(particula.solucao);
			
		}
	}
	
	
	
	public ArrayList<Solucao> getFronteira(){
		return fronteira;
	}
	
	public String toString(){
		return fronteira.toString();
	}
	/**
	 * Método que verifica se uma solução domina a outra
	 * @param sol1 Solução que será comparada com as regras pertencentes a fronteira de pareto
	 * @param sol2 Solução pertencente a fronteira de pareto
	 * @return -1 Se sol1 for dominada, 0 se a sol1 nao domina nem eh dominada, 1 sol1 domina sol2 
	 */
	public int compararMedidas(double[] sol1, double[] sol2){
		//Contador que marca quantos valores da regra 1 sao maiores que os da regra2
		//Se cont for igual ao tamanho dos elementos da regra 1 entao a regra 2 eh dominada pela regra1
		//Se cont for igual a 0 a regra2 domina a regra1
		//Se cont for maior do que 0 e menor que o tamanho ela nao domina e nem eh dominada
		int cont = 0; 
		int cont2 = sol1.length;
		for (int i = 0; i < sol1.length; i++) {
			if(sol1[i]*objetivosMaxMin[i]>sol2[i]*objetivosMaxMin[i]){
				++cont;
			} else {
				if(sol1[i]==sol2[i]){
					--cont2;
				}
			}
		}
		if(cont == 0){	
			if(cont2 == 0)
				return 0;
			else
				return -1;
		}
		else{
			if(cont>0 && cont<cont2)
				return 0;
			else return 1;
		}
	}
	
	/*public int compararMedidas(Solucao sol1, Solucao sol2){
		if(sol1.rank>sol2.rank)
			return -1;
		else{
			if(sol1.rank<sol2.rank)
				return 1;
				else
					return 0;
		}
	}*/
	
	/**
	 * Modificação da modificação da dominância de Pareto proposta por Sato
	 * Derivação do Sen do Wi através do Cos
	 * @param fix Valor original da função de objetivo de índice i
	 * @param r Norma do vetor de objetivos
	 * @param si Parâmetro da modificação da dominacia (Varia entre 0 e 1)
	 * @return
	 */
	public double modificacaoDominanciaPareto(double fix, double r, double si){
		double cosWi = fix/r;
		double cosWi2 = cosWi*cosWi;
		double senWi = Math.sqrt(1-cosWi2);
		double senSiPi = Math.sin(si * Math.PI);
		double cosSiPi = Math.cos(si * Math.PI);
		//Formula: r*sen(Wi+SiPi)/sen(SiPi)
		double numerador = r*((senWi*cosSiPi)+(cosWi*senSiPi));
		double novoFix = numerador/senSiPi;
		return Math.max(novoFix, 0);
	}
	
	public double r(double[] objetivos){
		double soma = 0;
		for (int i = 0; i < objetivos.length; i++) {
			double fix = objetivos[i];
			soma+=fix*fix;
		}
		return Math.sqrt(soma);
	}
	
	/**
	 * Método que retorna de quantas soluções a solução passada como parâmetro é dominada
	 * @param solucao Solução a ser contada o numero de dominação		
	 * @return Quantas soluções a solução passada como parametro é dominada
	 */
	public double obterNumDomincao(Solucao solucao, ArrayList<Solucao> solucoes){
		
		int numDominacao = 0;
		
		int comp;
	
		double[] novosObjetivosSolucao = new double[solucao.objetivos.length];
		
		double r = r(solucao.objetivos);
		for (int i = 0; i < solucao.objetivos.length; i++) {
			novosObjetivosSolucao[i] = modificacaoDominanciaPareto(solucao.objetivos[i], r, S);
		}
		
		for (Iterator<Solucao> iter = solucoes.iterator(); iter.hasNext();) {
			Solucao temp = iter.next();
			
			double[] novosObjetivosTemp = new double[temp.objetivos.length];

			r = r(temp.objetivos);
			for (int i = 0; i < temp.objetivos.length; i++) {
				novosObjetivosTemp[i] = modificacaoDominanciaPareto(temp.objetivos[i], r, S);
			}

			
			comp = compararMedidas(novosObjetivosSolucao, novosObjetivosTemp);
			if(comp == -1)
				numDominacao++;
		}
		
		return numDominacao;
	}

}
