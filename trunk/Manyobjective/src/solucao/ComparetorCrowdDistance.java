package solucao;

import java.util.Comparator;

import kernel.nuvemparticulas.Particula;

public class ComparetorCrowdDistance implements Comparator<Particula>{
	
	public int compare(Particula p1, Particula p2){
		if(p1.solucao.crowdDistance>p2.solucao.crowdDistance)
			return -1;
		else
			if(p1.solucao.crowdDistance<p2.solucao.crowdDistance)
				return 1;
			else
				return 0;
	}
	

}
