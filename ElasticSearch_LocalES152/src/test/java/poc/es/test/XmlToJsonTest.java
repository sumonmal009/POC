package poc.es.test;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.XML;

public class XmlToJsonTest {
	   private static final String XML_TEXT = "<note>\n" +
	            "<to>Tove</to>\n" +
	            "<from>Jani</from>\n" +
	            "<heading>Reminder</heading>\n" +
	            "<body>Don't forget me this weekend!</body>\n" +
	            "</note>";
	    private static final int PRETTY_PRINT_INDENT_FACTOR = 4;

	    @Test
	    public void convert() {
	        JSONObject xmlJSONObj = XML.toJSONObject(XML_TEXT);
	        String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
	        System.out.println(jsonPrettyPrintString);
	    }
	    
	    
	    @Test
	    public void convert_file_xml2json() {
		   String xml;
		   String data;
			try {
				String baseUrl = "C://Users//raghavendra_a//bloomsbury-ws//ElasticSearch_LocalES152//resource//";

				xml = FileUtils.readFileToString(new File(baseUrl + "FPA273.xml"));
				data = org.json.XML.toJSONObject(xml).toString();
				FileUtils.writeStringToFile(new File(baseUrl + "FPA273.json"), data );
				
				xml = FileUtils.readFileToString(new File(baseUrl + "FPA239.xml"));
				data = org.json.XML.toJSONObject(xml).toString();
				FileUtils.writeStringToFile(new File(baseUrl + "FPA239.json"), data );

				xml = FileUtils.readFileToString(new File(baseUrl + "EDch1064.xml"));
				data = org.json.XML.toJSONObject(xml).toString();
				FileUtils.writeStringToFile(new File(baseUrl + "EDch1064.json"), data );

				xml = FileUtils.readFileToString(new File(baseUrl + "EDch1513.xml"));
				data = org.json.XML.toJSONObject(xml).toString();
				FileUtils.writeStringToFile(new File(baseUrl + "EDch1513.json"), data );

				xml = FileUtils.readFileToString(new File(baseUrl + "EDch1712.xml"));
				data = org.json.XML.toJSONObject(xml).toString();
				FileUtils.writeStringToFile(new File(baseUrl + "EDch1712.json"), data );

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    
}
