import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

class Executor implements Watcher, Runnable, DataMonitorListener {
	private static final String LOCALHOST_ADDRESS = "127.0.0.1";
	private static final Logger logger = Logger.getLogger(Executor.class);

	private String znode;
	private DataMonitor dataMonitor;
	private ZooKeeper zooKeeper;
	private String exec[];
	private Process child;

	Executor(String port, String znode, String exec[]) throws KeeperException, IOException {
		this.exec = exec;
        this.znode = znode;
        int sessionTimeout = 3000;
		zooKeeper = new ZooKeeper(LOCALHOST_ADDRESS + ":" + port, sessionTimeout, this);
		dataMonitor = new DataMonitor(zooKeeper, znode, null, this);
		dataMonitor.initChildrenGet();
	}

	public void process(WatchedEvent event) {
		dataMonitor.process(event);
	}

    void ls() {
        try {
            dataMonitor.ls(znode);
        } catch (Exception e) {
            logger.error("Error while printing out znode");
        }
    }

	public void run() {
		try {
			synchronized (this) {
				while (!dataMonitor.dead) {
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
