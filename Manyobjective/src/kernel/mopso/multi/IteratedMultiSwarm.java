package kernel.mopso.multi;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import archive.UnboundArchive;

import kernel.AlgoritmoAprendizado;
import kernel.mopso.MOPSO;
import kernel.mopso.Particula;
import kernel.mopso.SMPSO;
import pareto.FronteiraPareto;
import problema.Problema;
import solucao.Solucao;



public class IteratedMultiSwarm extends AlgoritmoAprendizado {
	
	
	public MOPSO basis_mopso;
	
	public MOPSO[] swarms;
	
	//Indicates in which iteration the global archiver will be updated by the archivers from the swarm
	public int update = 1;
	
	//Defines the range of the region that each swarm must search
	public double box_range = 0.1;
	
	public int iterations = 10;
	
	/**
	 * 
	 * @param n Decison variables
	 * @param prob Problem
	 * @param generations Number of iterations
	 * @param evaluations Number of evaluations (if different of -1, runs the evaluations instead the iterations)
	 * @param popSize Population size
	 * @param S Parameter of the CDAS method, default S = 0.5
	 * @param maxmim Define if the objective are maximization or minimization
	 * @param repositorySize Size of the external archive
	 * @param tRank Ranking method (default false)
	 * @param archiving Archiving method
	 * @param leaderChoice Leader's choice
	 * @param eps Epsilon values for eapp and eaps
	 * @param numberSwarms Number of swarms
	 */
	public IteratedMultiSwarm(int n, Problema prob, int generations, int evaluations, int popSize, String S, String[] maxmim, int repositorySize, String tRank, String archiving, String leaderChoice, double eps, int numberSwarms, int update, double range, int pop_swarm, int rep_swarm, int split_iterations, boolean eval_analysis){
		super(n,prob,generations,evaluations, popSize, maxmim,tRank, eps, repositorySize, archiving, eval_analysis);
		
		this.update = update;
		
		box_range = range;
		
		iterations = split_iterations;
		
		//If the parameters are different from the number of swarms, the remaining swarms are defined with the parameters of the SMPSO algorithm
		String[] S_i = S.split(";");
		if(S_i.length < numberSwarms){
			String[] temp = S_i.clone();
			String default_ = "0.5";
			S_i = new String[numberSwarms+1];
			for (int i = 0; i < S_i.length; i++) {
				if(i<temp.length)
					S_i[i] = temp[i];
				else
					S_i[i] = default_;
			}
		}
		
		pareto = new FronteiraPareto(new Double(S_i[0]).doubleValue(), maxmim,rank, eps, problema, archiveSize);
		String[] leaderChoice_i = leaderChoice.split(";");
		if(leaderChoice_i.length < numberSwarms){
			String[] temp = leaderChoice_i.clone();
			String default_ = "tb";
			leaderChoice_i = new String[numberSwarms+1];
			for (int i = 0; i < leaderChoice_i.length; i++) {
				if(i<temp.length)
					leaderChoice_i[i] = temp[i];
				else
					leaderChoice_i[i] = default_;
			}
		}
		String[] archiving_i = archiving.split(";");
		if(archiving_i.length < numberSwarms){
			String[] temp = archiving_i.clone();
			String default_ = "ideal";
			archiving_i = new String[numberSwarms+1];
			for (int i = 0; i <archiving_i.length; i++) {
				if(i<temp.length)
					archiving_i[i] = temp[i];
				else
					archiving_i[i] = default_;
			}
		}
		
		
		
		basis_mopso = new SMPSO(n, prob, generations, evaluations, popSize, S_i[0], maxmim, repositorySize, tRank, new Double(S_i[0]).doubleValue(), archiving_i[0], leaderChoice_i[0], eps, eval_analysis);
		System.out.println("Basis: " + archiving_i[0] + "\t|\t" + leaderChoice_i[0] + "\t|\t" + S_i[0]);
		swarms = new MOPSO[numberSwarms];
		
		for (int i = 0; i < swarms.length; i++) {
			String s = S_i[i+1];
			String archiver = archiving_i[i+1];
			String lc = leaderChoice_i[i+1];
			System.out.println("Swarm " + i + ": " + archiver + "\t|\t" +lc + "\t|\t" + s);

			swarms[i] = new SMPSO(n, prob, generations, evaluations, pop_swarm, s, maxmim, rep_swarm, tRank, new Double(s).doubleValue(), archiver, lc, eps, eval_analysis);

		}
		
	}

	@Override
	public ArrayList<Solucao> executar() {
		
		try{
			PrintStream comunication = new PrintStream("evaluations/comunication.txt");
		
		resetExecution();

		ArrayList<Solucao> initial_front = basis_mopso.executar();
		//if(eval_analysis)
			//evaluationAnalysis(initial_front);
		System.out.println();
		int com = 0;
		for(int k = 0; k<iterations; k++){
			System.out.println("Iteration: " + k);
			int groups[] = new int[initial_front.size()];

			ArrayList<double[]> centroids = clustering(initial_front, AlgoritmoAprendizado.PARAMETER_SPACE,swarms.length, groups);
			comunication.println(com++ + "\t" + problema.avaliacoes);
			//Inicia a populcaao
			initializeSwarms(centroids, groups, initial_front);

			for(int i = 0; i<geracoes; i++){
				if(i%10 == 0)
					System.out.print(i + " ");

				for (int s = 0; s < swarms.length; s++) {
					if(i%10 == 0)
						System.out.print( "s" + s + " ");
					swarms[s].evolutionaryLoop();
				}
				if(i % 10 ==0)
					System.out.println();
				
				/*if(eval_analysis){

					for (int s_i = 0; s_i < swarms.length; s_i++) {
						ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
						for (Iterator<Solucao> iterator = swarm_pareto_i
								.iterator(); iterator.hasNext();) {
							Solucao solucao = (Solucao) iterator.next();
							pareto.add(solucao, new UnboundArchive());
						}
					}

					evaluationAnalysis(pareto.getFronteira());
					pareto.getFronteira().clear();
				}*/
			}

			
			pareto.getFronteira().clear();
			for (int s_i = 0; s_i < swarms.length; s_i++) {
				ArrayList<Solucao> swarm_pareto_i = swarms[s_i].pareto.getFronteira();
				for (Iterator<Solucao> iterator = swarm_pareto_i
						.iterator(); iterator.hasNext();) {
					Solucao solucao = (Solucao) iterator.next();
					pareto.add(solucao, new UnboundArchive());
				}
			}
			
			initial_front.clear();
			initial_front.addAll(pareto.getFronteira());
		}
		if(eval_analysis)
			evaluationAnalysis(pareto.getFronteira());
		return pareto.getFronteira();
		}catch(IOException ex) {ex.printStackTrace(); return null;}
	}

	@Override
	public ArrayList<Solucao> executarAvaliacoes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Initializes the solutions with the search regions defined by the centroids
	 * @param centroids
	 */
	public void initializeSwarms(ArrayList<double[]> centroids, int[] groups, ArrayList<Solucao> initial_front){
		//Initializes the solutions of each swarm
		for (int i = 0; i < swarms.length; i++) {
			MOPSO swarm = swarms[i];
			
			double[][] new_limits = new double[2][n];
			//Get the centroid guide for the swarm i
			double[] centroid = centroids.get(i);
			for(int j = 0; j<n; j++){
				//Defines de lower and upper limits of the new search space. The values can't overcome the maximum values
				//of the search space of the problem
				new_limits[0][j] = Math.max(centroid[j] - box_range, problema.MIN_VALUE);
				new_limits[1][j] = Math.min(centroid[j] + box_range, problema.MAX_VALUE);
				
			}
			
			
			
			for(int j = 0; j< groups.length; j++){
				if(groups[j] == i){
					swarm.pareto.add(initial_front.get(j), swarm.archiver);
					/*if(swarm.populacao.size()<swarm.tamanhoPopulacao){
						SolucaoNumerica s = (SolucaoNumerica)initial_front.get(j);
						Particula particula = new Particula();
						particula.iniciarParticulaAleatoriamente(problema, s);
						problema.calcularObjetivos(s);
						particula.localBestObjetivos = particula.solucao.objetivos;
						swarm.populacao.add(particula);
					}*/
				}
					
			}
			
			
			swarm.inicializarPopulacao(new_limits);
			swarm.atualizarRepositorio();
			
		}
		
	
		for (int i = 0; i < swarms.length; i++) {
			//In each swarm, the population chooses the leader according to its Pareto front  
			swarms[i].escolherLider.escolherLideres(swarms[i].populacao, swarms[i].pareto.getFronteira());
			//Initial mutation for swarm i
			swarms[i].escolherParticulasMutacao();
		}


	}
	
	public void resetExecution(){
		pareto = new FronteiraPareto(pareto.S, pareto.maxmim, pareto.rank, pareto.eps, problema, pareto.archiveSize);
		basis_mopso.pareto = new FronteiraPareto(basis_mopso.pareto.S, basis_mopso.pareto.maxmim, basis_mopso.pareto.rank, basis_mopso.pareto.eps, problema, basis_mopso.pareto.archiveSize);
		
		
		for(int i = 0; i<swarms.length; i++){
			swarms[i].populacao = new ArrayList<Particula>();
			
			swarms[i].pareto = new FronteiraPareto(swarms[i].pareto.S, swarms[i].pareto.maxmim, swarms[i].pareto.rank, swarms[i].pareto.eps, problema, swarms[i].pareto.archiveSize);
			
		}
		
		problema.avaliacoes =0; 
	}

}
