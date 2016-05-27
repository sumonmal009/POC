package poc.ElasticSearch.handler;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.Map;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FieldQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;

@Component
public class Handler {
	private final long pauseTime = 10000;
	private static final String indexName = "indx1";
	private static final String documentType = "doc";
	private static final String documentId = "doc001";
	private Client client;

	public void addGetDocIndexOperation() throws Exception {

		// Create Client
		// client = NodeBuilder.nodeBuilder().client(true).node().client();
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
		TransportClient transportClient = new TransportClient(settings);
		transportClient = transportClient.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		client = (Client) transportClient;

		// Create Index and set settings and mappings

		// CreateIndexRequestBuilder createIndexRequestBuilder =
		// client.admin().indices().prepareCreate(indexName);
		// createIndexRequestBuilder.execute().actionGet();
		boolean indexExists = client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
		if (indexExists) {
			client.admin().indices().prepareDelete(indexName).execute().actionGet();
		}
		client.admin().indices().prepareCreate(indexName).execute().actionGet();

		IndexRequestBuilder indexRequestBuilder;
		XContentBuilder contentBuilder;
		IndexResponse indx_response;

		// Add documents
		indexRequestBuilder = client.prepareIndex(indexName, documentType, documentId);
		// build json object
		contentBuilder = jsonBuilder().startObject().prettyPrint();
		contentBuilder.field("name", "sumon").field("Location", "UK");
		contentBuilder.endObject();
		indexRequestBuilder.setSource(contentBuilder);
		indx_response = indexRequestBuilder.execute().actionGet();
		Thread.sleep(pauseTime);

		// add few more doc 1
		indexRequestBuilder = client.prepareIndex(indexName, "txt", documentId);
		contentBuilder = jsonBuilder().startObject().prettyPrint();
		contentBuilder.field("name", "sumon1").field("Location", "USA");
		contentBuilder.endObject();
		indexRequestBuilder.setSource(contentBuilder).execute().actionGet();
		Thread.sleep(pauseTime);

		// add few more doc 2
		indexRequestBuilder = client.prepareIndex(indexName, documentType, "doc002");
		contentBuilder = jsonBuilder().startObject().prettyPrint();
		contentBuilder.field("name", "sumon").field("Location", "Canada");
		contentBuilder.endObject();
		indexRequestBuilder.setSource(contentBuilder).execute().actionGet();
		Thread.sleep(pauseTime);

		// add few more doc 3
		indexRequestBuilder = client.prepareIndex(indexName, documentType, "doc003");
		contentBuilder = jsonBuilder().startObject().prettyPrint();
		contentBuilder.field("name", "suman").field("Location", "This is a  paragraph simulating content search, written by sumon. content updated");
		contentBuilder.endObject();
		indexRequestBuilder.setSource(contentBuilder).execute().actionGet();
		Thread.sleep(pauseTime);



		// update / overwrite doc 2
		indexRequestBuilder = client.prepareIndex(indexName, documentType, "doc002");
		contentBuilder = jsonBuilder().startObject().prettyPrint();
		contentBuilder.field("name", "Sumon Mal").field("Location", "Canada & UK");
		contentBuilder.endObject();
		indexRequestBuilder.setSource(contentBuilder).execute().actionGet();
		Thread.sleep(pauseTime);

		// Delete doc1 from index1,type:doc,id doc001
		DeleteResponse del_response = client.prepareDelete(indexName, documentType, "doc001").execute().actionGet();
		Thread.sleep(pauseTime);

		// Get known document 
		GetRequestBuilder getRequestBuilder = client.prepareGet(indexName, documentType, "doc002");
		getRequestBuilder.setFields(new String[] { "name" });
		GetResponse doc_response = getRequestBuilder.execute().actionGet();
		String name = doc_response.field("name").getValue().toString();
		System.out.println("Get: " + name);

		//Search
		search("*umon*");
		/*		QueryStringQueryBuilder qsb=QueryBuilders.queryString("+kimchy -dadoonet").field("name");
		FieldQueryBuilder qb=QueryBuilders.fieldQuery("name", "Sumon*");

		SearchResponse response = client.prepareSearch(indexName)
				.setTypes(documentType)
				.setSearchType(SearchType.QUERY_AND_FETCH)
				.setQuery(qsb)
				//.setFrom(0).setSize(60).setExplain(true)
				.execute()
				.actionGet();
		SearchHit[] results = response.getHits().getHits();
		System.out.println("Current results: " + results.length);
		for (SearchHit hit : results) {
			System.out.println("------------------------------");
			Map<String,Object> result = hit.getSource();   
			System.out.println(result);
		}
		 */
		client.close();
	}



	public  void search(String keyword) throws InterruptedException {
		//SearchResponse searchResponse = client.prepareSearch("test").setQuery(termQuery("_all", "n_value1_1")).execute().actionGet();
		SearchResponse response=null;

		//response = client.prepareSearch("indx1").setQuery(QueryBuilders.termQuery("_source.name", "sumon")).execute().actionGet();
		try {
			response = client.prepareSearch()
					.setIndices("indx1")
					//.setTypes("doc")
					.addFields("name","Location")
					.setQuery(QueryBuilders.fieldQuery("_all", "sumon*")).execute().actionGet();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* response = client.prepareSearch("indx1")
                .setQuery(QueryBuilders.matchQuery("_all", "sumon*"))
                .execute()
                .actionGet();
		 */






		/*
		SearchResponse response = client.prepareSearch("indx1", "index2")
		        .setTypes("doc", "txt")
		       // .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("name", "Sumon Mal"))                 // Query
		       // .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
		       // .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();
		 */

		Thread.sleep(pauseTime);
		System.out.println(response.getHits().getTotalHits());
		for (SearchHit hit : response.getHits()) {
			System.out.println(hit.field("name").getValue()+" is in " +hit.field("Location").getValue());
		}
	}
}
