package solucao;

import java.util.Random;

public class SolucaoBinaria extends Solucao {

	private String[] variaveis;
	
	
	public SolucaoBinaria(int n, int m){
		super(n,m);
		
		variaveis = new String[n];
	}
	
	@Override
	public void iniciarSolucaoAleatoria() {
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		for(int i = 0; i<n; i++){
			String valor = "" + (Math.abs(rand.nextInt())%2);
			variaveis[i] = valor;
		}

	}
	
	public void setVariavel(int i, String valor){	
		variaveis[i] = valor;
	}
	
	public String getVariavel(int i){
		return variaveis[i];
	}
	
	public String[] getVariaveis(){
		return variaveis;
	}
	
	public static void main(String[] args) {
		SolucaoBinaria sol = new SolucaoBinaria(10, 3);
		sol.iniciarSolucaoAleatoria();
	}
	
	public boolean  isNumerica(){
		return false;
	}
	
	public boolean  isBinaria(){
		return true;
	}

}
