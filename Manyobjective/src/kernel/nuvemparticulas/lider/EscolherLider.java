package kernel.nuvemparticulas.lider;

import java.util.ArrayList;

import kernel.nuvemparticulas.Particula;

import solucao.Solucao;


public abstract class EscolherLider {
	
	public String id;
	
	public abstract void escolherLideres(ArrayList<Particula> populacao, ArrayList<Solucao> lideres);

}
