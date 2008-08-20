package votacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import regra.ComparatorRegraLaplace;
import regra.ComparatorX;
import regra.Regra;
import weka.core.Instance;

/**
 * Classe que implementa a vota��o por confian�a com a sele��o das regras atrav�s do valor de Laplace. Implementa��o
 * diferente, as regras s�o ordenadas e depois selecionadas.
 * @author Andr� de Carvalho
 *
 */
public class VotacaoConfidenceLaplaceOrdenacao extends Votacao {

	
	public VotacaoConfidenceLaplaceOrdenacao(){
		System.out.println("Vota��o: Confidence Laplace com ordena��o");
	}
	
	/**
	 * M�todo que restringe a vota��o � somente k regras por classe, onde k representa o
	 * n�mero de classes do problema. Inicialmente ordena todas as regras, depois executa a vota��o.
	 */
	public double votacao(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo = 0;
		double negativo = 0;
		
		ArrayList<Regra> regrasVotacaoPositiva = new ArrayList<Regra>();
		ArrayList<Regra> regrasVotacaoNegativa = new ArrayList<Regra>();
		double k = exemplo.classAttribute().numValues();
		
		obterRegrasVotam(regrasVotacaoPositiva, regrasVotacaoNegativa, regras, exemplo, classePositiva);
		
		ComparatorRegraLaplace comp = new ComparatorRegraLaplace();
		
		Collections.sort(regrasVotacaoPositiva, comp);
		Collections.sort(regrasVotacaoNegativa, comp);
		
		
		//Vota��o das k melhores regras para cada classe
		
		positivo = votarConfidenceMedia(regrasVotacaoPositiva, k);
		negativo = votarConfidenceMedia(regrasVotacaoNegativa, k);
		
		return positivo-negativo;
	}

	/*METODO ANTIGO
	public double votacaotemp(ArrayList<Regra> regras, Instance exemplo,
			String classePositiva) {
		double positivo = 0;
		double negativo = 0;
		
		ArrayList<Regra> regrasVotacaoPositiva = new ArrayList<Regra>();
		ArrayList<Regra> regrasVotacaoNegativa = new ArrayList<Regra>();
		double k = exemplo.classAttribute().numValues();
		
		obterRegrasVotam(regrasVotacaoPositiva, regrasVotacaoNegativa, regras, exemplo, classePositiva);
		
		ComparatorRegraLaplace comp = new ComparatorRegraLaplace();
		
		Collections.sort(regrasVotacaoPositiva, comp);
		Collections.sort(regrasVotacaoNegativa, comp);
		
		
		//Vota��o das k melhores regras para cada classe
		

		for (int i = 0; i<k;i++) {
			if(i<regrasVotacaoPositiva.size()){
				int indice = (regrasVotacaoPositiva.size()-1) - i;
				Regra regraPos = regrasVotacaoPositiva.get(indice);
				regraPos.votou = true;
				positivo += regraPos.getConfidence();
			}
			
			if(i<regrasVotacaoNegativa.size()){
				int indice = (regrasVotacaoNegativa.size()-1) - i;
				Regra regraNeg = regrasVotacaoNegativa.get(indice);
				regraNeg.votou = true;
				negativo += regraNeg.getConfidence();
			}
		}
		
		if(regrasVotacaoPositiva.size()<k && regrasVotacaoPositiva.size()!=0)
			positivo = positivo/regrasVotacaoPositiva.size();
		else
			positivo = positivo/k;
		
		if(regrasVotacaoNegativa.size()<k && regrasVotacaoNegativa.size()!=0)
			negativo = negativo/regrasVotacaoNegativa.size();
		else
			negativo = negativo/k;
		
		return positivo-negativo;
	}
	*/
}
