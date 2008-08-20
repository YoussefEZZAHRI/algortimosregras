package regra;

import java.util.Comparator;

public class ComparatorRegraLaplace implements Comparator {
	
	/**
	 * Método que compara duas regras através do seu valor de Laplace
	 * @param o Regra a ser comparada
	 * @return -1 menor, 0 igual, 1 maior
	 */
	public int compare(Object o1, Object o2){
		Regra r1 = (Regra) o1;
		Regra r2 = (Regra) o2;
		if(r1.getLaplace()<r2.getLaplace())
			return -1;
		else{
			if(r1.getLaplace()>r2.getLaplace())
				return 1;
			else
				return 0;
		}
	}

}
