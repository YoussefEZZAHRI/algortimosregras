package kernel;

/**
 * Matriz de confusao multiclasses
 * @author Matheus 
 */
public class MatrizConfusaoMultiClasse {
	
	public double[][] matriz;
	public int tamanho;
	
	public MatrizConfusaoMultiClasse(int tamanho){
		this.tamanho = tamanho;
		this.matriz = new double[tamanho][tamanho];
	}
}
