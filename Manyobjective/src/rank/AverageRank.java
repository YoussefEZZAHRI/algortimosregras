package rank;

import java.util.ArrayList;

import solucao.Solucao;

public class AverageRank extends Rank {
	
	public AverageRank(int m){
		super(m);
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes) {
		int[][][] A = new int[solucoes.size()][solucoes.size()][m];

		calcularWinningScore(solucoes, A);

		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			solucaoi.rank = 0;
		}

		for(int i = 0; i<solucoes.size(); i++){
			Solucao solucaoi = solucoes.get(i);
			for(int k = 0; k<m; k++){
				double rank = 0;
				for(int j = 0; j<solucoes.size(); j++){
					if(i!=j){
						if(A[i][j][k]==1)
							rank++;
					}
				}

				double rankObj = (solucoes.size()) - rank;
				solucaoi.rank+= rankObj;


			}
		}

	}

}
