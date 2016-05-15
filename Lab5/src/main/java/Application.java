import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {
	private static final Logger logger = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
		logger.info("Starting app!");
		Starter starter = new Starter();
		starter.initialize();
	}


}
