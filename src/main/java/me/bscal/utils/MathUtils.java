package me.bscal.utils;

public class MathUtils
{
	public static int ClampB(byte value, byte min, byte max)
	{
		return value > max ? max : value < min ? min : value;
	}

	public static int ClampI(int value, int min, int max)
	{
		return value > max ? max : value < min ? min : value;
	}

	public static long ClampL(long value, long min, long max)
	{
		return value > max ? max : value < min ? min : value;
	}

	public static float ClampF(float value, float min, float max)
	{
		return value > max ? max : value < min ? min : value;
	}

	public static double ClampD(double value, double min, double max)
	{
		return value > max ? max : value < min ? min : value;
	}
}
