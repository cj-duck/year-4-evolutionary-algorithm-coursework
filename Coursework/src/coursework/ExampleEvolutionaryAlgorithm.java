package coursework;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 *
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork}
 *
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {

	// EA PARAMETERS:

	// t for Tournament Selection
	int t = 2;


	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {
		//Initialise a population of Individuals with random weights
		population = initialise();
		System.out.println(population.size());

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */

		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 *
			 */
			// Select 2 Individuals from the current population.
			Individual parent1 = select();
			Individual parent2 = select();

			// Generate Children
			ArrayList<Individual> children = reproduce(parent1, parent2);

			//mutate the offspring
			mutate(children);

			// Evaluate the children
			evaluateIndividuals(children);

			// Replace children in population
			replace(children);


			// check to see if the best has improved
			best = getBest();

			// Implemented in NN class. 
			outputStats();

			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}



	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 *
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 *
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 *
	 */
	private ArrayList<Individual> initialise() {

		switch(Parameters.initialisation) {
			case 0: // Random
				population = new ArrayList<>();
				for (int i = 0; i < Parameters.popSize; ++i) {
					//chromosome weights are initialised randomly in the constructor
					Individual individual = new Individual();
					population.add(individual);
				}
				evaluateIndividuals(population);

			case 1: // Seeded
				ArrayList<Individual> seededPopulation = new ArrayList<>();
				for (int i = 0; i < Parameters.seededPopulation; i++) {
					//chromosome weights are initialised randomly in the constructor
					Individual individual = new Individual();
					seededPopulation.add(individual);
				}
				evaluateIndividuals(seededPopulation);
				population = seededPopulation;
				best = getBest();
				System.out.println("Best From Initialisation " + best);
				Collections.sort(seededPopulation, Comparator.comparing(i -> i.fitness));
				population = new ArrayList<>(seededPopulation.subList(0, Parameters.popSize));
				evaluateIndividuals(population);
			case 2:
				population = new ArrayList<>();
				for (int i = 0; i < Parameters.popSize; i++) {
					Individual individual = new Individual();
					Individual oppositeIndividual = individual.copy();
					for (int x = 0; x < individual.chromosome.length; x++) {
						oppositeIndividual.chromosome[x] = 0 - individual.chromosome[x];
					}
					ArrayList<Individual> evaluation = new ArrayList<>();
					evaluation.add(individual);
					evaluation.add(oppositeIndividual);
					evaluateIndividuals(evaluation);
					if (individual.fitness < oppositeIndividual.fitness) {
						population.add(individual);
					} else {
						population.add(oppositeIndividual);
					}
				}
				evaluateIndividuals(population);
		}
		return population;
	}

	/**
	 * Selection
	 */
	private Individual select() {
		Individual selection = new Individual();

		switch(Parameters.selection) {
			case 0: // Tournament Selection
				Individual next;
				Individual currentBest = population.get(Parameters.random.nextInt(Parameters.popSize));

				for (int i = 1; i < Parameters.t; i++) {
					next = population.get(Parameters.random.nextInt(Parameters.popSize));
					if (next.fitness < currentBest.fitness) {
						currentBest = next;
					}
				}

				selection = currentBest;
			case 1: // Roulette Wheel Selection
				double totalFitness = 0.0;
				double fitnessSoFar = 0.0;

				for (int i = 0; i < population.size(); i++) {
					totalFitness += (1 - population.get(i).fitness);
				}

				double roulette = Parameters.random.nextDouble() * totalFitness;
				for (int i = 0; i < population.size(); i++) {
					roulette -= (1 - population.get(i).fitness);
					if (roulette < 0) {
						return population.get(i).copy();
					}
				}
			case 2: // Ranked Selection - NOT IMPLEMENTED RAN OUT OF TIME
				Collections.sort(population, Comparator.comparing(i -> i.fitness));
				int rand = Parameters.random.nextInt(Parameters.popSize * (Parameters.popSize / 2));

		}

		return selection.copy();

	}

	/**
	 * Crossover / Reproduction
	 */
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<Individual>();
		Individual child1 = new Individual();
		Individual child2 = new Individual();

		switch(Parameters.crossover) {
			case 0: // Uniform crossover
				for (int i = 0; i < parent1.chromosome.length; i++)
					if (Parameters.random.nextBoolean()) {
						child1.chromosome[i] = parent1.chromosome[i];
						child2.chromosome[i] = parent2.chromosome[i];
					} else {
						child1.chromosome[i] = parent2.chromosome[i];
						child2.chromosome[i] = parent1.chromosome[i];
					}
			case 1: // 1-Point crossover
				int cutPoint = Parameters.random.nextInt(parent1.chromosome.length);
				for (int i = 0; i < cutPoint; i++) {
					child1.chromosome[i] = parent1.chromosome[i];
					child2.chromosome[i] = parent2.chromosome[i];
				}
				for (int i = cutPoint; i < parent1.chromosome.length; i++) {
					child1.chromosome[i] = parent2.chromosome[i];
					child2.chromosome[i] = parent1.chromosome[i];
				}
			case 2: // 2-Point crossover
				int cutPoint1 = Parameters.random.nextInt(parent1.chromosome.length);
				int cutPoint2 = Parameters.random.nextInt(parent1.chromosome.length);

				if (cutPoint1 == cutPoint2) {
					if (cutPoint1 == 0) {
						cutPoint2++;
					} else {
						cutPoint1--;
					}
				}

				if ( cutPoint1 > cutPoint2) {
					cutPoint1 = cutPoint1 + cutPoint2;
					cutPoint2 = cutPoint1 - cutPoint2;
					cutPoint1 = cutPoint1 - cutPoint2;
				}

				for (int i = 0; i < parent1.chromosome.length; i++) {
					if (i < cutPoint1 || i > cutPoint2) {
						child1.chromosome[i] = parent1.chromosome[i];
						child2.chromosome[i] = parent2.chromosome[i];
					} else {
						child1.chromosome[i] = parent2.chromosome[i];
						child2.chromosome[i] = parent1.chromosome[i];
					}
				}
			case 3: // Arithmetic crossover
				for (int i = 0; i < parent1.chromosome.length; i++) {
					child1.chromosome[i] = (parent1.chromosome[i] + parent2.chromosome[i]) / 2;
					child2.chromosome[i] = (parent1.chromosome[i] + parent2.chromosome[i]) / 2;
				}
		}

		children.add(child1);
		children.add(child2);

		return children;
	}

	/**
	 * Mutation
	 *
	 *
	 */
	private void mutate(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}
	}

	/**
	 *
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 *
	 */
	private void replace(ArrayList<Individual> individuals) {

		switch(Parameters.replace) {
			case 0: // Replace Worst
				for (Individual individual : individuals) {
					int idx = getWorstIndex();
					population.set(idx, individual);
				}
			case 1: // Replace Tournament
				for (Individual individual : individuals) {
					Individual next;
					Individual currentWorst = population.get(Parameters.random.nextInt(Parameters.popSize - 1));
					for (int i = 1; i < Parameters.rT; i++) {
						next = population.get(Parameters.random.nextInt(Parameters.popSize - 1));
						if (next.fitness > currentWorst.fitness) {
							currentWorst = next;
						}
					}
					population.set(getIndex(currentWorst), individual);
				}
		}
	}



	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i;
			}
		}
		return idx;
	}

	private int getIndex(Individual individual) {
		for (int i = 0; i < population.size(); i++) {
			if (population.get(i) == individual) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}
