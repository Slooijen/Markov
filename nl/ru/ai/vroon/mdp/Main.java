package nl.ru.ai.vroon.mdp;

import nl.ru.ai.SiemenLooijen4083679.reinforcement.ValueIteration;

/**
 * This main is for testing purposes (and to show you how to use the MDP class).
 * 
 * @author Jered Vroon
 *
 */
public class Main
{

	/**
	 * @param args,
	 *            not used
	 */
	public static void main(String[] args)
	{
		MarkovDecisionProblem mdp = new MarkovDecisionProblem();
		mdp.setInitialState(0, 0);
		mdp.setDeterministic();
		ValueIteration v = new ValueIteration(mdp);

		//MarkovDecisionProblem mdp2 = new MarkovDecisionProblem(10, 10);
		//mdp2.setField(5, 5, Field.REWARD);

	}
}
