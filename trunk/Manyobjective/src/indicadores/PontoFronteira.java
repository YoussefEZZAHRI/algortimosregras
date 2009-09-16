package indicadores;

public class PontoFronteira{
	
	public double[] objetivos = null;
	
	public double soma;
	public int objetivosAcimaMedia;
	
	public PontoFronteira(double[] o){
		objetivos = new double[o.length];
		objetivosAcimaMedia = 0;
		soma =  0;
		for (int i = 0; i < objetivos.length; i++) {
			objetivos[i] = o[i];
			soma += objetivos[i];
		}
	}
	
}	