Download the Elastic Search (version 1.5.2) from elasticsearch.org

Extract the archive

Go to it's bin folder

Run the elasticsearch.bat /sh as per your environment
---------------------------------------------------------

The AWS Elastic Server currently used is of version 1.5.2.
So, using the same version in local Elastic Server setup
Configure below in pom.xml
		
		<es-version>1.5.2</es-version>
	
		<dependency>
			<groupId>org.elasticsearch</groupId>
			<artifactId>elasticsearch</artifactId>
			<version>${es-version}</version>
		</dependency>
		
---------------------------------------------------------

Install plugins
Head
	use this command to install:  >plugin install mobz/elasticsearch-head
	to verify the installation restart the elasticsearch, in log [head] should appear
	
Access head:   http://localhost:9200/_plugin/head/
----------------------------------------------------------		

NOTE : Can use Sense App for latest versions of Elasticsearch 2.3.2

----------------------------------------------------------		


curl -XGET "http://localhost:9200/library"

curl -XGET "http://localhost:9200/library?pretty"


curl -XGET "http://localhost:9200/library/book/1?pretty"


# Find all books of 'First' edition
curl -XGET "http://localhost:9200/library/book/_search?pretty" -d'
{
  "query": {
    "match": {
      "edition": "First"
    }
  }
}'


# Find all books having price > 30

curl -XGET "http://localhost:9200/library/book/_search?pretty" -d'
{
    "query" : {
        "filtered" : {
            "filter" : {
                "range" : {
                    "price" : { "gt" : 30 }
                }
            }
        }
    }
}'


# Find all First edition books having price more than 30

curl -XGET "http://localhost:9200/library/book/_search?pretty" -d'
{
    "query" : {
        "filtered" : {
            "filter" : {
                "range" : {
                    "price" : { "gt" : 30 }
                }
            },
            "query" : {
                "match" : {
                    "edition" : "First"
                }
            }
        }
    }
}'

---------------------------------------------------------

The Output log running the client code for local Elasticserver instance, ver 1.5.2.

1.Recreate the index (library)
2.Create document (book) for the index (library)
3.Retrieval of the document
4.Range search, count of hits
5.Field search
6.Multi-query search


