package poc.ElasticSearch;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import poc.ElasticSearch.handler.Handler;

@Configuration
public class App {
	public static void main(String[] args) {
		// JavaConfigApplicationContext ctx = new
		// JavaConfigApplicationContext(App.class);
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		Handler handler = (Handler) ctx.getBean(Handler.class);

		try {
			handler.addGetDocIndexOperation();
		} catch (Exception e) {
			e.printStackTrace();
		}

		((ConfigurableApplicationContext) ctx).close();
	}
}
