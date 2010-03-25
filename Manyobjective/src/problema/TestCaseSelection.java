package problema;

import java.util.ArrayList;
import java.util.Iterator;

import objetivo.CriterioFuncional;
import objetivo.FuncaoObjetivo;
import objetivo.Tempo;

import solucao.Solucao;
import solucao.SolucaoNumerica;


public class TestCaseSelection extends Problema {

	public ArrayList<FuncaoObjetivo> funcoesObjetivos = null;
	
	public String programa;
	public String diretorio;
	public int numCasosTeste;
	
	public TestCaseSelection(int m, String fo, String p, String dir, int nct){
		super(m);
		funcoesObjetivos = new ArrayList<FuncaoObjetivo>();
		programa = p;
		diretorio = dir;
		addFuncoesObjetivos(fo);
		numCasosTeste = nct;
		
	}
	
	public void addFuncoesObjetivos(String fo){
		String[] funcoes = fo.split(";");
		for (int i = 0; i < funcoes.length; i++) {
			String funcao = funcoes[i];
			String arquivo = diretorio + funcao + "_" + programa + ".txt";
			
			if(funcao.equals("arcos") || funcao.equals("nos") || funcao.equals("pu") || funcao.equals("pudu") || funcao.equals("pdu")){
				CriterioFuncional crit = new CriterioFuncional(arquivo, funcao);
				funcoesObjetivos.add(crit);
			}
			
			if(funcao.equals("tempo")){
				Tempo tempo = new Tempo(arquivo, numCasosTeste);
				funcoesObjetivos.add(tempo);
			}
		}
		
	}
	
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
