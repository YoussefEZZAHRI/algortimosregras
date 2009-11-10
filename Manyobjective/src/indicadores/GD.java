package indicadores;

import java.util.ArrayList;

/**
 * Classe que represta o método General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class GD extends Indicador {
	
	
	
	/*
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public GD(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "gd";
	}
	
	public GD(int m, String caminho, String idExec, ArrayList<PontoFronteira> pftrue){
		super(m, caminho, idExec);
		indicador = "gd";
		PFtrue = new ArrayList<PontoFronteira>();
		PFtrue.addAll(pftrue);
	}
	

	@Override
	public double calcular() {
		double gd = 0;
		if(objetivosMaxMin == null){
			System.err.println("Erro: Não foi definido se cada objetivo é de maximização ou minimização (Executar Método preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			
			double soma = 0;
			//Percorre todos os pontos do conjunto de aproximacao
			for(int i = 0; i<fronteira.size(); i++){
				PontoFronteira pontoAproximacao = fronteira.get(i);
				double menor_distancia = Double.MAX_VALUE;
				//Obtem a menor distância entre um ponto do conjunto de aproximação e um ponto da fronteira de pareto real
				for(int j = 0; j<PFtrue.size();j++){
					PontoFronteira pontoPFtrue = PFtrue.get(j);
					menor_distancia = Math.min(distanciaEuclidiana(pontoAproximacao.objetivos, pontoPFtrue.objetivos), menor_distancia);
				}
				soma+=menor_distancia;
			}
			
			gd = Math.sqrt(soma)/(double) fronteira.size();
		} else{
			System.err.println("Erro no cálculo do Hipervolume: Fronteira de Pareto não carregada.");
			System.exit(0);
			return 0;
		}	
		return gd;
	}

}
