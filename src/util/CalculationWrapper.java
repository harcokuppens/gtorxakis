package util;

public interface CalculationWrapper extends Runnable {
	public void run();

	public void abort();
}