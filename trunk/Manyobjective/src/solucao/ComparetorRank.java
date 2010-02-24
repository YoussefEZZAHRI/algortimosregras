package solucao;

import java.util.Comparator;

public class ComparetorRank implements Comparator<SolucaoNumerica>{
	
	public int compare(SolucaoNumerica s1, SolucaoNumerica s2){
		if(s1.rank<s2.rank)
			return -1;
		else
			if(s1.rank>s2.rank)
				return 1;
			else
				return 0;
	}
	

}
