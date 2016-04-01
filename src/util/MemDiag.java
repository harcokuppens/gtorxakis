package util;

public class MemDiag {
	private static int counter = 0;
	private static double average;
	private static double min = Double.MAX_VALUE;
	private static double max = 0;
	private static double current = 0;

	public static void diagnose() {
		update();
		show();
	}

	public static void update() {
		current = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024d*1024d);
		counter++;
		average += (current - average) / (double) counter;
		if(current < min) {
			min = current;
		}
		if(current > max) {
			max = current;
		}
	}

	public static void show() {
		System.out.println(info());	
	}

	public static String info() {
		return String.format("avg:\t%10.2f MB\tmin:\t%10.2f MB\tmax:\t%10.2f MB\tcurrent:\t%10.2f MB",average, min, max, current);
	}

}