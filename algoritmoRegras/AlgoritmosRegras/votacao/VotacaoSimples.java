package votacao;

import java.util.ArrayList;
import java.util.Iterator;

import regra.Regra;
import weka.core.Instance;

/**
 * Classe que implementa a vota��o simples, com a adi��o de um ponto para cada voto da regra.
 * @author Andr� de Carvalho
 *
 */
public class VotacaoSimples extends Votacao {

	public VotacaoSimples(){
		System.out.println("Vota��o: Simples");
	}
	
	/**
	 * M�todo de vota��o simples. Todas regras votam e um ponto para cada voto.
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

}
