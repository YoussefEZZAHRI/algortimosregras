package conversao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class Teste {
	
	int jogos[] = new int[100];
	
	public void sorteio(){
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		for(int i=0; i<50; i++){
			/*int num = rand.nextInt();
			int numero = Math.abs(num%100);*/
			double num = Math.random();
			int numero = (int)Math.floor(num*100);
			jogos[numero]++;
		}
	}
	
	public void executarSorteios(long numSor){
		for(long i = 0; i<numSor;i++){
			sorteio();
		}
		
		ArrayList<Numero> sorteios = new ArrayList<Numero>();
		
		for (int i = 0; i < jogos.length; i++) {
			Numero num = new Numero(i,jogos[i]);
			sorteios.add(num);
		}
		
		Collections.sort(sorteios);
		
		for (Iterator iter = sorteios.iterator(); iter.hasNext();) {
			Numero element = (Numero) iter.next();
			System.out.println(element);
			
		}
	}
	
	public static void main(String[] args) {
		Teste t = new Teste();
		t.executarSorteios(1000000);
		
	}
	
	
	
	private class Numero implements Comparable{
		int numero;
		int frequencia;
		
		public Numero(int num, int f){
			numero = num;
			frequencia = f;
		}
		
		public String toString(){
			return numero + " = " + frequencia;
		}
		
		public int compareTo(Object o){
			Numero num2 = (Numero) o;
			if(this.frequencia<num2.frequencia)
				return 1;
			else
				if(this.frequencia>num2.frequencia)
					return -1;
				else
					return 0;
		}
		
		
	}

}


