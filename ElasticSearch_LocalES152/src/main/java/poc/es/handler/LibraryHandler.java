package poc.es.handler;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;
import org.elasticsearch.common.settings.ImmutableSettings;

@Component
public class LibraryHandler {
	private final long pauseTime = 2000;
	private static final String indexName = "library";
	private static final String documentType = "book";
	private Client client;

	LibraryHandler(){
		// Create Client
		// client = NodeBuilder.nodeBuilder().client(true).node().client();
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "elasticsearch").build();
		TransportClient transportClient = new TransportClient(settings);
		transportClient = transportClient.addTransportAddress(
				new InetSocketTransportAddress("localhost", 9300));
		client = (Client) transportClient;
	}
	
	// Create Index and set settings and mappings
	public void reCreateIndex() {
		boolean indexExists = client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
		if (indexExists) {
			client.admin().indices().prepareDelete(indexName).execute().actionGet();
		}
		client.admin().indices().prepareCreate(indexName).execute().actionGet();
		System.out.println("Recreated Index : " + indexName);
	}

	//Add a document
	public void addDocument(String documentId, String title, String author, String edition, String price) {
		IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, documentType, documentId);
		// build json object
		try {
			XContentBuilder contentBuilder = jsonBuilder().startObject().prettyPrint();
			contentBuilder.field("title", title).field("author", author).field("edition", edition).field("price",
					price);
			contentBuilder.endObject();
			indexRequestBuilder.setSource(contentBuilder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		IndexResponse indx_response = indexRequestBuilder.execute().actionGet();
		try {
			System.out.println("Inserted book with title: " + title);
			Thread.sleep(pauseTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	//Delete document given its id
	public void removeBook(String documentId) {
		DeleteResponse response = client.prepareDelete(indexName, documentType, documentId).execute().actionGet();
		try {
			System.out.println("Removed book from index");
			Thread.sleep(pauseTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//Retrieve title for the document given its id
	public void retrieveTitle(String documentId) {
		GetRequestBuilder getRequestBuilder = client.prepareGet(indexName, documentType, documentId);
		getRequestBuilder.setFields(new String[] { "title" });
		GetResponse doc_response = getRequestBuilder.execute().actionGet();
		String title = doc_response.getField("title").getValue().toString();
		System.out.println("Retrieved book : "+ documentId +" ,its Title : " + title);
	}


	//Search books given a price range
	public void searchPriceRange(String from, String to) throws InterruptedException {
		SearchResponse response = null;
		try {
			response = client.prepareSearch()
					.setIndices(indexName)
					//.addFields("title", "author")
					.setPostFilter(FilterBuilders.rangeFilter("price").from(from).to(to))
					.execute()
					.actionGet();			
		} catch (Exception e) {
			e.printStackTrace();
		}

		Thread.sleep(pauseTime);
		System.out.println("Total hits for price range search : " + response.getHits().getTotalHits());
		System.out.println(response.toString());
	}
	
	//Field search, search for title of a book
	public void searchTitle(String keyword){
        try{
            SearchResponse response = client.prepareSearch()
            		.setQuery(QueryBuilders.matchQuery("title", keyword))
            		.setSize(10)
            		.execute()
            		.actionGet();
	    		Thread.sleep(pauseTime);
	    		System.out.println("Total hits for title search : " + response.getHits().getTotalHits());
	    		System.out.println(response.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
	}
	
	//Multi-query search
	public void multiSearch(String title, String edition){
			SearchRequestBuilder srb1 = client.prepareSearch()
					.setQuery(QueryBuilders.queryString(title)); //.setSize(1);
			SearchRequestBuilder srb2 = client.prepareSearch()
					.setQuery(QueryBuilders.matchQuery("edition", edition)); //.setSize(1);

			MultiSearchResponse sr = client.prepareMultiSearch()
			        .add(srb1)
			        .add(srb2)
			        .execute().actionGet();

			// You will get all individual responses from MultiSearchResponse#getResponses()
			long nbHits = 0;
			for (MultiSearchResponse.Item item : sr.getResponses()) {
			    SearchResponse response = item.getResponse();
			    nbHits += response.getHits().getTotalHits();
			    System.out.println(response.toString());
			}
			System.out.println("Number of hits : "+ nbHits);
	}
	
	
	//Populate books to library
	void populateBooks(){
		System.out.println("#####Creating index (library)");
		reCreateIndex();
		
		System.out.println("#####Adding documents (book(s)) ");
		//addDocument(String documentId, String title, String author, String edition, int price)
		addDocument("1", "C Programming language", "Kernighan Ritchie", "First",  "21");
		addDocument("2", "C Programming language", "Kernighan Ritchie", "Second", "22");
		addDocument("3", "C Programming language", "Kernighan Ritchie", "Third",  "23");
		addDocument("4", "C Programming language", "Kernighan Ritchie", "Fourth", "24");
		addDocument("5", "C Programming language", "Kernighan Ritchie", "Fifth",  "25");
		
		addDocument("6", "Elastic Search Reference", "Shay Banon", "First",  "30");		
		addDocument("7", "Elasticsearch: The Definitive Guide 1.x", "Shay Banon", "First",  "40");
		addDocument("8", "Elasticsearch: The Definitive Guide 2.x", "Shay Banon", "Second",  "60");

		addDocument("9", "Maven", "Jason van Zyl", "Second",  "60");
	}
	
	
	public void test() throws Exception {
		
   	populateBooks();
		
		System.out.println("#####Retrieving a document (book)");
		retrieveTitle("7");
		
		System.out.println("#####Range search - book 'price' range [30-45]");
		searchPriceRange("30","45");
		System.out.println("#####Range search - book 'price' range [15-45]");
		searchPriceRange("15","45");
		
		System.out.println("#####Search book title");
		searchTitle("Elasticsearch*");
	
		System.out.println("#####Multi-query search, trial #1"
				+ "Find all books having 'Maven' in its title "
				+ "or from Third edition.");
		multiSearch("Maven", "Third"); // 1, 1
		System.out.println("#####Multi-query search, trial #2 "
				+ "Find all books having 'Guide' in its title, "
				+ "or from First edition.");
		multiSearch("Guide", "First"); //2, 3
		
		client.close();
	}
	
}
