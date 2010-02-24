package problema;

import java.util.ArrayList;
import java.util.Iterator;

import solucao.Solucao;
import solucao.SolucaoNumerica;


public class TestCaseSelection extends Problema {

	public ArrayList<FuncaoObjetivo> funcoesObjetivos = null;
	
	public TestCaseSelection(int m, String fo){
		super(m);
		funcoesObjetivos = new ArrayList<FuncaoObjetivo>();
		addFuncoesObjetivos(fo);
	}
	
	public void addFuncoesObjetivos(String fo){}
	
	public double[] calcularObjetivos(Solucao solucao) {
		
		
		double[] objetivos = new double[m];
		int i = 0;
		for (Iterator<FuncaoObjetivo> iterator = funcoesObjetivos.iterator(); iterator.hasNext();) {
			FuncaoObjetivo funcao = (FuncaoObjetivo) iterator.next();
			objetivos[i++] = funcao.calcularObjetivo(solucao); 
		}
		
		return objetivos;
	}

	@Override
	public ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol) {
		// TODO Auto-generated method stub
		return null;
	}

}
