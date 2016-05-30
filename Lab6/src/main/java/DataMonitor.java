import org.apache.log4j.Logger;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;
import java.util.List;

class DataMonitor implements Watcher, StatCallback {

	boolean dead;
	private ZooKeeper zooKeeper;
	private String znode;
	private Watcher chainedWatcher;
	private DataMonitorListener listener;
	private byte prevData[];
	private static final Logger logger = Logger.getLogger(DataMonitor.class);

	DataMonitor(ZooKeeper zooKeeper, String znode, Watcher chainedWatcher, DataMonitorListener listener) {
		this.zooKeeper = zooKeeper;
		this.znode = znode;
		this.chainedWatcher = chainedWatcher;
		this.listener = listener;
		zooKeeper.exists(znode, true, this, null);
	}

	void initChildrenGet() {
		try {
			zooKeeper.getChildren(znode, this);
		} catch (Exception ex) {
			logger.error("Error while getting children", ex);
		}
	}

	public void process(WatchedEvent event) {
		String path = event.getPath();
		if (event.getType() == Event.EventType.NodeChildrenChanged) {
			try {
				count(znode);
			} catch (Exception ex) {
				logger.error("Error while printing out nodes", ex);
			}
		}
		if (event.getType() == Event.EventType.None) {
			switch (event.getState()) {
			case SyncConnected:
				break;
			case Expired:
				dead = true;
				listener.closing(KeeperException.Code.SessionExpired);
				break;
			}
		} else {
			if (path != null && path.equals(znode)) {
				zooKeeper.exists(znode, true, this, null);
			}
		}
		if (chainedWatcher != null) {
			chainedWatcher.process(event);
		}
	}

	public void processResult(int rc, String path, Object ctx, Stat stat) {
		boolean exists;
		switch (rc) {
		case Code.Ok:
			exists = true;
			break;
		case Code.NoNode:
			exists = false;
			break;
		case Code.SessionExpired:
		case Code.NoAuth:
			dead = true;
			listener.closing(rc);
			return;
		default:
			zooKeeper.exists(znode, true, this, null);
			return;
		}

		byte b[] = null;
		if (exists) {
			try {
				b = zooKeeper.getData(znode, false, null);
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				return;
			}
		}
		if ((b == null && null != prevData) || (b != null && !Arrays.equals(prevData, b))) {
			listener.exists(b);
			prevData = b;
		}
	}

	private void count(String path) throws KeeperException, InterruptedException {
		try {
			List<String> children = zooKeeper.getChildren(path, false);
			System.out.println(children.size());
		} catch (KeeperException.NoNodeException e) {
			logger.error("Group doest not exist, e");
			System.exit(1);
		}
		initChildrenGet();
	}

	private void ls(String path) throws KeeperException, InterruptedException {
		try {
			List<String> children = zooKeeper.getChildren(path, false);
			for (String child : children) {
				System.out.println(path + "/" + child + zooKeeper.getChildren(path + "/" + child, false));
			}
		} catch (KeeperException.NoNodeException e) {
			logger.error("Group doest not exist, e");
			System.exit(1);
		}
		initChildrenGet();
	}

}