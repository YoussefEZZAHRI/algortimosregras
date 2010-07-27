package rank;

import java.util.ArrayList;
import java.util.Iterator;


import solucao.Solucao;

public class RankDominancia extends Rank {
	
	
	
	public RankDominancia(int m) {
		super(m);
	}
	

	@Override
	public void rankear(ArrayList<Solucao> solucoes) {
		ArrayList<Solucao> atual = new ArrayList<Solucao>();
		ArrayList<Solucao> proxima = new ArrayList<Solucao>();
		
		atual.addAll(solucoes);
		
		int rank = 0;
		
		while(atual.size()>0){
			for (Iterator<Solucao> iter = atual.iterator(); iter.hasNext();) {
				Solucao solucao = iter.next();
				solucao.numDominacao = pareto.obterNumDomincao(solucao, atual);
				if(solucao.numDominacao == 0){
					solucao.rank = rank;
				} else
					proxima.add(solucao);
			}
			atual.clear();
			atual.addAll(proxima);
			proxima.clear();
			rank++;
		}

	}

}
