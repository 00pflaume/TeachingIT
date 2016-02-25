package de.simonbrungs.teachingit.utilities;

public abstract class RepeatingTask {
	private Thread thread = null;
	private boolean shouldRun = true;

	public RepeatingTask(long pDelay) {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (shouldRun) {
						Thread.sleep(pDelay);
						run();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public abstract void run();

	public void stop() {
		thread.interrupt();
		shouldRun = false;
	}
}
