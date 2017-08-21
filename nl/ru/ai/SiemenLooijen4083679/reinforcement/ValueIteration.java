package nl.ru.ai.SiemenLooijen4083679.reinforcement;

import java.util.ArrayList;
import java.util.Random;

import nl.ru.ai.vroon.mdp.Action;
import nl.ru.ai.vroon.mdp.Field;
import nl.ru.ai.vroon.mdp.MarkovDecisionProblem;

public class ValueIteration
{
	private MarkovDecisionProblem mdp;
	private double[][] values;
	private double[][] newValues;
	private ArrayList<double[][]> vForEachK = new ArrayList<>();
	private Q[][] qForEachK;// naam moet anders, is niet voor each k
	private double discount_factor = 0.9;
	private double threshold = 0.001;

	private int k = 0;

	public ValueIteration(MarkovDecisionProblem mdp)
	{
		this.mdp = mdp;
		this.values = new double[mdp.getWidth()][mdp.getHeight()];
		this.newValues = new double[mdp.getWidth()][mdp.getHeight()];
		this.qForEachK = new Q[mdp.getWidth()][mdp.getHeight()];
		initialize();

		do
		{
			valueIteration();
		}
		while (!terminated());

		System.out.println("lalala");
		while (!mdp.isTerminated())
		{
			int x = mdp.getStateXPosition();
			int y = mdp.getStateYPostion();

			Action best = qForEachK[x][y].getAction();

			mdp.performAction(best);
		}
	}

	private void initialize()
	{
		for (int i = 0; i < values.length; i++)
		{
			//System.out.println(values.length);
			for (int j = 0; j < values[i].length; j++)
			{

				values[i][j] = new Random().nextDouble();
				//System.out.println("INITIAL");
				//System.out.println(values[i][j]);
			}
		}
		vForEachK.add(values);
		System.out.println("dingen0 " + vForEachK.get(0)[0][0]);
	}

	private boolean terminated()
	{
		boolean terminated = true;

		double[][] vCurrent = vForEachK.get(k);
		double[][] vPrevious = vForEachK.get(k - 1);

		for (int i = 0; i < vCurrent.length; i++)
			for (int j = 0; j < vCurrent[i].length; j++)
			{
				//System.out.println("Terminated? : " + (vCurrent[i][j]));
				double currentV = vCurrent[i][j];
				//System.out.println(vPrevious[i][j]);
				double previousV = vPrevious[i][j];
				System.out.println("difference = " + Math.abs(currentV - previousV));
				if (Math.abs(vCurrent[i][j] - vPrevious[i][j]) > threshold)
					terminated = false;

			}

		return terminated;
	}

	private void valueIteration()
	{
		//while not terminated
		this.k++;
		updateV();

	}

	/**
	 * updates values per state
	 */
	private void updateV()
	{

		for (int i = 0; i < values.length; i++)
		{
			for (int j = 0; j < values[i].length; j++)
			{
				for (Action a : Action.values())
					updateThisState(i, j, a);
				System.out.println(qForEachK[i][j]);
			}
		}

		double[][] copyOf = new double[mdp.getWidth()][mdp.getHeight()];
		for (int i = 0; i < newValues.length; i++)
		{
			for (int j = 0; j < newValues[i].length; j++)
			{
				copyOf[i][j] = newValues[i][j];
			}
		}

		vForEachK.add(copyOf);
		System.out.println("vForEachK0[0][0]" + vForEachK.get(0)[0][0]);
		System.out.println("vForEachK1[0][0]" + vForEachK.get(1)[0][0]);

		this.values = new double[mdp.getWidth()][mdp.getHeight()];//nadenken

	}

	/**
	 * updates specifc state for all legal actions
	 * 
	 * @param x
	 * @param y
	 * @param a
	 */
	private void updateThisState(int x, int y, Action a)
	{
		ArrayList<Double> all_p_r_gamma_previousV_per_legal_move_for_current_state = new ArrayList<>();

		ArrayList<Action> actions = new ArrayList<>();//equal size and index to all_p_r_gamme_perviousV_per_legal_move_for_current_state

		//for each legal move from current state, put into arraylist the prob * (immediate reward, discount *Vk-1) Then pick the best one from arraylist and put into v for the state. Update v.
		//mdp.setState(x, y);
		Field f = mdp.getField(x, y);// = current state. Changed this in nl.ru.ai.vroon.mdp.Field

		for (Action action : Action.values())
		{
			int nextX = x;
			int nextY = y;

			double p = 0;
			if (a == action)
			{
				p = mdp.getpPerform();

				if (a == Action.UP)
					nextY++;
				if (a == Action.DOWN)
					nextY--;
				if (a == Action.LEFT)
					nextX--;
				if (a == Action.RIGHT)
					nextX++;
			}
			else
				if (a == Action.backAction(a))
				{
					if (a == Action.UP)
						nextY--;
					if (a == Action.DOWN)
						nextY++;
					if (a == Action.LEFT)
						nextX++;
					if (a == Action.RIGHT)
						nextX--;
				}
				else
					if (a == Action.previousAction(a))
					{
						p = mdp.getpSidestep();
						if (a == Action.UP)
							nextX--;
						if (a == Action.DOWN)
							nextX++;
						if (a == Action.LEFT)
							nextY++;
						if (a == Action.RIGHT)
							nextY--;
					}
					else
						if (a == Action.nextAction(a))
						{
							p = mdp.getpSidestep();
							if (a == Action.UP)
								nextX++;
							if (a == Action.DOWN)
								nextX--;
							if (a == Action.LEFT)
								nextY++;
							if (a == Action.RIGHT)
								nextY--;
						}
			//System.out.println("x = " + x + " y = " + y);
			if (!(nextX < 0 || nextX >= mdp.getWidth() || nextY < 0 || nextY >= mdp.getHeight()))
			{
				all_p_r_gamma_previousV_per_legal_move_for_current_state.add(p * rewardForField(f, nextX, nextY) + discount_factor * vForEachK.get(k - 1)[nextX][nextY]);
				//System.out.println(all_p_r_gamma_previousV_per_legal_move_for_current_state);
				actions.add(action);
			}
		}

		//TODO beste uitkiezen -> functie
		double highest = 0;
		Action bestAction = null;

		for (int i = 0; i < all_p_r_gamma_previousV_per_legal_move_for_current_state.size(); i++)
		{
			double d = all_p_r_gamma_previousV_per_legal_move_for_current_state.get(i);

			if (d > highest)
			{
				highest = d;
				bestAction = actions.get(i);
			}
		}
		//		System.out.println("x = " + x + " y = " + y + " " + values[x][y]);
		//values = //copy van velden, wsl niet hier maar boven?
		newValues[x][y] = highest;
		//actions[x][y][a] = bestAction? q value?
		qForEachK[x][y] = new Q(x, y, bestAction, highest);

	}

	private double rewardForField(Field f, int x, int y)
	{
		if (f != Field.OUTOFBOUNDS && f != Field.OBSTACLE)
			return mdp.getReward(x, y);
		return -9999;
	}

}
