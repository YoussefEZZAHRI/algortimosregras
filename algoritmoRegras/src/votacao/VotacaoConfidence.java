package votacao;

import java.util.ArrayList;
import java.util.Iterator;

import regra.Regra;
import weka.core.Instance;

/**
 * Classe que implementa a vota��o por confian�a.
 * @author Andr� de Carvalho
 *
 */
public class VotacaoConfidence extends Votacao {

	public VotacaoConfidence(){
		System.out.println("Vota��o: Confidence");
	}
	
	/**
	 * M�todo que implementa a vota��o por confian�a. Todas as regras votam e o ponto adicionado � a confian�a 
	 * da regra.
	 */
	public double votacao(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo = 0;
		double negativo = 0;

		for (Iterator iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			boolean b = regra.compararCorpo(exemplo.toDoubleArray());
			if(b){
				regra.votou = true;
				if(regra.cabeca == regra.classe.indexOfValue(classePositiva))
					positivo+= regra.getConfidence();
				else 
					negativo+= regra.getConfidence();	
			}	
		}
		
		return positivo-negativo;
	}

}
