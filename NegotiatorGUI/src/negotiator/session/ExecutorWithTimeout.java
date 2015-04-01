package negotiator.session;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * execute commands within the set timout limits. Compute and keeps remaining
 * time for further calls. This executor can be called multiple times with
 * different {@link Callable}s so that the total accumulated time will stay
 * within the total timeoutms that is given in the constructor.
 * 
 * This executor will run a separate timer and kill the {@link Callable} with
 * thread.stop() to make a pretty hard kill attempt if the time runs out.
 * 
 * @author W.Pasman, David Festen 1apr15
 *
 */
public class ExecutorWithTimeout {

	private long remainingTimeMs;

	/**
	 * Construct an executor with a total available amount of time.
	 * 
	 * @param timeoutms
	 *            the total available time that this executor can spend.
	 */
	public ExecutorWithTimeout(long timeoutms) {
		remainingTimeMs = timeoutms;
	}

	/**
	 * Inner thread class, this is the thread where the Callable will be run.
	 * 
	 * @author W.Pasman 1apr15
	 *
	 * @param <V>
	 *            the return type of the callable.
	 */
	class myThread<V> extends Thread {
		// flag indicating that the thread is done.
		BlockingQueue<Boolean> ready = new ArrayBlockingQueue<Boolean>(1);

		private V result = null;
		private Throwable resultError = null;

		Callable<V> callable;

		public myThread(Callable<V> c) {
			callable = c;
		}

		@Override
		public void run() {
			try {
				result = callable.call();
			} catch (Throwable e) {
				resultError = e;
			}
			try {
				ready.put(true);
			} catch (InterruptedException e) {
				// at this point, either result or resultError has been set
				// already.
			}
		}

		/**
		 * Execute this thread and wait for thread to terminate or terminate
		 * after timeout millis. Blocking call. After return, the Callable has
		 * been executed, OR we have thrown.
		 * 
		 * @param timeout
		 *            timeout in millis.
		 * @throws TimeoutException
		 *             if the callable did not complete within the available
		 *             time.
		 */
		public V executeWithTimeout(long timeout) throws ExecutionException,
				TimeoutException {
			start();

			// wait for the thread to finish, but at most timeout ms.
			try {
				if (ready.poll(timeout, TimeUnit.MILLISECONDS) == null) {
					/*
					 * not finished. terminate and throw. stop() is only way to
					 * force thread to die. interrupt() is too weak.
					 * executorService only supports interrupt(), not stop().
					 * Therefore we use plain Threads here.
					 */
					stop();
					throw new TimeoutException(
							"agent passed deadline and was killed");
				}
			} catch (InterruptedException e) {
				/*
				 * we should not get here. If we get here, that means that
				 * poll() was interrupted, so someone interrupted the thread we
				 * are running IN (instead of the thread we are running).
				 */
				e.printStackTrace();
				System.exit(1);
			}
			// if we get here, thread ended and result or resultError was set.
			if (resultError != null) {
				throw new ExecutionException(resultError);
			}
			return result;
		}
	}

	/**
	 * Execute the command within the remaining time of this executor. Blocking
	 * call. Used time will be subtracted from the quotum of this Executor. This
	 * function is synchronized and can execute only 1 Callable at any time.
	 * 
	 * @param command
	 *            the {@link Callable} to execute
	 * @return the result V
	 * @throws ExecutionException
	 *             if the {@link Callable} threw an exception. The
	 *             {@link ExecutionException} will contain the exception from
	 *             the {@link Callable}.
	 * @throws TimeoutException
	 *             if the {@link Callable} did not finish in time.
	 */
	public synchronized <V> V execute(final Callable<V> command)
			throws ExecutionException, TimeoutException {

		return new myThread<V>(command).executeWithTimeout(remainingTimeMs);
	}

	/**
	 * @return remaining time for this executor
	 */
	public long getRemainingTimeMs() {
		return remainingTimeMs;
	}

}