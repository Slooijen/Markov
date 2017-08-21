package nl.ru.ai.SiemenLooijen4083679.reinforcement;

import nl.ru.ai.vroon.mdp.Action;

public class Q
{
	private int x, y;
	private Action action;
	private double value;

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Action getAction()
	{
		return action;
	}

	public double getValue()
	{
		return value;
	}

	public Q(int x, int y, Action action, double value)
	{
		this.x = x;
		this.y = y;
		this.action = action;
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "x = " + x + " y = " + y + " Action = " + action + " value = " + value;
	}
}
