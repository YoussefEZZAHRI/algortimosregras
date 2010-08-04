package rank;

import java.util.ArrayList;

import solucao.Solucao;

public class BalancedRank extends Rank {
	
	public BalancedRank(int m){
		super(m);
		System.out.println("Rank: BR");
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes, int c) {
		int[][][] A = new int[solucoes.size()][solucoes.size()][m];
		
		double maiorRank = Double.NEGATIVE_INFINITY;
		double menorRank = Double.POSITIVE_INFINITY;

		calcularWinningScore(solucoes, A);

		

		//Calcula do average rank ponderado
		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			solucaoi.rank = 0;
			for(int k = 0; k<m; k++){
				double rank = 0;
				for(int j = 0; j<solucoes.size(); j++){
					if(i!=j){
						if(A[i][j][k]==1)
							rank++;
					}
				}

				double rankObj = (solucoes.size()) - rank;
				//Calculo do AR
				solucaoi.rank+= rankObj;
				//Defini��o dos maiores e menores rankings
				if (rankObj >maiorRank)
					maiorRank = rankObj;
				if(rankObj<menorRank)
					menorRank = rankObj;
			}

			//Calculo da pondera��o
			double diff = (maiorRank - menorRank)/ solucoes.size();
			solucaoi.rank = solucaoi.rank * diff;	
			maiorRank = Math.max(maiorRank, solucaoi.rank);
			menorRank = Math.min(menorRank, solucaoi.rank);
			
				
		}
		if(c!=-1)
		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			solucaoi.rank = solucaoi.rank/maiorRank;
			solucaoi.combRank[c] = solucaoi.rank;
		}

	}

}
