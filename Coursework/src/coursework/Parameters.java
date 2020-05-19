package coursework;

import java.lang.reflect.Field;
import java.util.Random;
import model.LunarParameters;
import model.NeuralNetwork;
import model.LunarParameters.DataSet;

public class Parameters {
 
	/**
	 * These parameter values can be changed 
	 * You may add other Parameters as required to this class 
	 * 
	 */
	private static int numHidden = 6; // Default: 5
	private static int numGenes = calculateNumGenes();
	public static double minGene = -3; // specifies minimum and maximum weight values
	public static double maxGene = +3; // Default: -3, +3
		
	public static int popSize = 50; // Default: 40
	public static int maxEvaluations = 20000; // Default: 20000
	
	// Parameters for mutation 
	// Rate = probability of changing a gene
	// Change = the maximum +/- adjustment to the gene value
	public static double mutateRate = 0.65; // mutation rate for mutation operator // Default: 0.04
	public static double mutateChange = 0.2; // delta change for mutation operator // Default: 0.1
	
	//Random number generator used throughout the application
	public static long seed = System.currentTimeMillis();
	public static Random random = new Random(seed);

	//set the NeuralNetwork class here to use your code from the GUI
	public static Class neuralNetworkClass = ExampleEvolutionaryAlgorithm.class;

	//Additional Parameters
	public static int initialisation = 2; // 0 - Random, 1 - Seeded, 2 - Opposition based
	public static int seededPopulation = 1000; // Initial population size for seeded population
	public static int selection = 1; // 0 - Tournament, 1 - Roulette Wheel, 2 - Elitism
	public static int t = 10; // Number of individuals to choose for selection tournament
	public static int crossover = 2; // 0 - Uniform, 1 - 1-Point, 2 - 2-Point, 3 - Arithmetic
	public static int replace = 0; // 0 - Worst, 1 - Tournament
	public static int rT = 10; // Number of individuals to choose for replacement tournament if replace = 1


	/**
	 * Do not change any methods that appear below here.
	 * 
	 */
	
	public static int getNumGenes() {					
		return numGenes;
	}

	
	private static int calculateNumGenes() {
		int num = (NeuralNetwork.numInput * numHidden) + (numHidden * NeuralNetwork.numOutput) + numHidden + NeuralNetwork.numOutput;
		return num;
	}

	public static int getNumHidden() {
		return numHidden;
	}
	
	public static void setHidden(int nHidden) {
		numHidden = nHidden;
		numGenes = calculateNumGenes();		
	}

	public static String printParams() {
		String str = "";
		for(Field field : Parameters.class.getDeclaredFields()) {
			String name = field.getName();
			Object val = null;
			try {
				val = field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str += name + " \t" + val + "\r\n";
			
		}
		return str;
	}
	
	public static void setDataSet(DataSet dataSet) {
		LunarParameters.setDataSet(dataSet);
	}
	
	public static DataSet getDataSet() {
		return LunarParameters.getDataSet();
	}
	
	public static void main(String[] args) {
		printParams();
	}
}
