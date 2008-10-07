package votacao;

import java.util.ArrayList;
import java.util.Iterator;

import regra.Regra;
import weka.core.Instance;

/**
 * Classe que implementa a votação simples, com a adição de um ponto para cada voto da regra.
 * @author André de Carvalho
 *
 */
public class VotacaoSimples extends Votacao {

	public VotacaoSimples(){
		System.out.println("Votação: Simples");
	}
	
	/**
	 * Método de votação simples. Todas regras votam e um ponto para cada voto.
	 */
	public double votacao(ArrayList<Regra> regras, Instance exemplo, String classePositiva) {
		int positivo = 0;
		int negativo = 0;

		for (Iterator iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			if(b){
				regra.votou = true;
				if(regra.cabeca == regra.classe.indexOfValue(classePositiva))
					positivo++;
				else 
					negativo++;	
			}	
		}
		
		return positivo-negativo;
	}

	/**
	 * Método de votação simples multiclasse. Todas regras votam e um ponto para cada voto.
	 * @author matheus
	 * @return a classe mais votada pelo conjunto de regras 
	 */
	public int votacaoMultiClasse(ArrayList<Regra> regras, Instance exemplo, int numClasses) {
		int[] classePontuacao = new int[numClasses];
		int classeMaisVotada = 0;
		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			// em caso da regra atual cobrir o exemplo adiciona-se um ponto para ela no array classePontuacao
			if(b){
				classePontuacao[regra.cabeca]++; 
			}	
		}
		//verifica qual foi a mais votada
		for(int i = 0; i < (numClasses - 1); i++){
			if(classePontuacao[i+1] > classePontuacao[i]){
				classeMaisVotada = i+1; 
			}			
		}
		
		return classeMaisVotada;
	}

}
