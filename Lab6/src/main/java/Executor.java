import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Executor implements Watcher, Runnable, DataMonitorListener {
	public static final String LOCALHOST_ADDRESS = "127.0.0.1";
	private static final Logger logger = Logger.getLogger(Executor.class);

	private String znode;

	private DataMonitor dm;

	private ZooKeeper zk;

	private String exec[];

	private Process child;

	private Executor(String port, String znode, String exec[]) throws KeeperException, IOException {
		this.exec = exec;
		int sessionTimeout = 3000;
		zk = new ZooKeeper(LOCALHOST_ADDRESS + ":" + port, sessionTimeout, this);
		dm = new DataMonitor(zk, znode, null, this);
	}

	public static void main(String[] args) {
		loggerInit();

		if (args.length < 2) {
			System.err.println("USAGE: port program [args ...]");
			System.exit(2);
		}
		String hostPort = args[0];
		String znode = "/znode_testowy";
		String exec[] = new String[args.length - 2];
		System.arraycopy(args, 2, exec, 0, exec.length);
		try {
			new Executor(hostPort, znode, exec).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loggerInit() {
		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
	}

	public void process(WatchedEvent event) {
		dm.process(event);
	}

	public void run() {
		try {
			synchronized (this) {
				while (!dm.dead) {
					wait();
				}
			}
		} catch (InterruptedException e) {
			logger.error("Error while running process", e);
		}
	}

	public void closing(int rc) {
		synchronized (this) {
			notifyAll();
		}
	}

	public void exists(byte[] data) {
		if (data == null) {
			if (child != null) {
				System.out.println("Killing process");
				child.destroy();
				try {
					child.waitFor();
				} catch (InterruptedException e) {
					logger.error("Error while killing process", e);
				}
			}
			child = null;
		} else {
			if (child != null) {
				System.out.println("Stopping child");
				child.destroy();
				try {
					child.waitFor();
				} catch (InterruptedException e) {
					logger.error("Error while stopping process", e);
				}
			}
			try {
				System.out.println("Starting child");
				child = Runtime.getRuntime().exec(exec);
				new StreamWriter(child.getInputStream(), System.out);
				new StreamWriter(child.getErrorStream(), System.err);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class StreamWriter extends Thread {
		private OutputStream os;

		private InputStream is;

		StreamWriter(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
			start();
		}

		public void run() {
			byte b[] = new byte[80];
			int rc;
			try {
				while ((rc = is.read(b)) > 0) {
					os.write(b, 0, rc);
				}
			} catch (IOException e) {
			}

		}
	}
}
