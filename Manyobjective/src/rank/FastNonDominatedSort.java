package rank;

import java.util.ArrayList;
import java.util.Iterator;


import solucao.Solucao;

public class FastNonDominatedSort extends Rank {
	

	public FastNonDominatedSort(int m) {
		super(m);
	}

	@Override
	public void rankear(ArrayList<Solucao> solucoes) {
		ArrayList<Solucao> dominadas = new ArrayList<Solucao>();
		ArrayList<Solucao> naoDominadas = new ArrayList<Solucao>();
		int rank = 0;
		for (Iterator<Solucao> iterator = solucoes.iterator(); iterator.hasNext();) {
			Solucao p =  iterator.next();
			p.numDominacao = 0;
			for (Iterator<Solucao> iterator2 = solucoes.iterator(); iterator2.hasNext();) {
				Solucao q =  iterator2.next();
				int comp = pareto.compararMedidas(p.objetivos, q.objetivos);
				if(comp == 1)
					dominadas.add(q);
				else
					if(comp == -1)
						p.numDominacao++;
			}
			if(p.numDominacao == 0){
				p.rank = rank;
				naoDominadas.add(p);
			}
		}


		while(naoDominadas.size()>0){
			ArrayList<Solucao> H = new ArrayList<Solucao>();
			for (Iterator<Solucao> iterator = naoDominadas.iterator(); iterator.hasNext();) {
				rank++;
				for (Iterator<Solucao> iterator2 = dominadas.iterator(); iterator2.hasNext();) {
					Solucao q =  iterator2.next();
					q.numDominacao--;
					if(q.numDominacao == 0){
						H.add(q);
						q.rank = rank;
					}
				}
			}
			naoDominadas.clear();
			naoDominadas.addAll(H);
		}
	}

}
