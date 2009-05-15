package regra;

import java.util.Comparator;

public class ComparatorX implements Comparator{
	
	public int compare(Object o1, Object o2){
		Regra r1 = (Regra) o1;
		Regra r2 = (Regra) o2;
		if(r1.getConfidence()<r2.getConfidence()){
			return -1;
		} else{
			if(r1.getConfidence()>r2.getConfidence())
				return 1;
			else{
				if(r1.getSup()<r2.getSup())
					return -1;
				else{
					if(r1.getSup()>r2.getSup())
						return 1;
					else{
						if(r1.getNumAtributosNaoVazios()<r2.getNumAtributosNaoVazios())
							return -1;
						else{
							if(r1.getNumAtributosNaoVazios()> r2.getNumAtributosNaoVazios())
								return 1;
							else
								return 0;
						}
					}
				}
				
			}
		}
		
	}

}
