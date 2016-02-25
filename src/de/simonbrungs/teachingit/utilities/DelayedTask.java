package de.simonbrungs.teachingit.utilities;

public abstract class DelayedTask {

	public DelayedTask(long pDelay) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(pDelay);
					run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public abstract void run();

}
