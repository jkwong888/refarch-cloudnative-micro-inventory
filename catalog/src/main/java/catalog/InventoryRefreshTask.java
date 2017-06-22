package catalog;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import catalog.client.InventoryServiceClient;
import catalog.client.Item;

@Component
public class InventoryRefreshTask  implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(InventoryRefreshTask.class);
		
	@Autowired
	private InventoryServiceClient invClient;
		
	@Autowired
	private ElasticSearch elasticSearch;
		
	public void run() {
		while (true) {
			try {
				logger.info("Querying Inventory Service for all items ...");
				List<Item> allItems = invClient.getAllItems();
				
				elasticSearch.loadRows(allItems);
			} catch (Exception e) {
				logger.warn("Caught exception, ignoring", e);
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				logger.warn("Caught InterruptedException, quitting");
				break;
			}
		}
	}
}