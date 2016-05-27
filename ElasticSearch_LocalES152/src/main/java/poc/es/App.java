package poc.es;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import poc.es.handler.LibraryHandler;

@Configuration
public class App {
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		LibraryHandler handler = (LibraryHandler) ctx.getBean(LibraryHandler.class);
		try {
			handler.test();
		} catch (Exception e) {
			e.printStackTrace();
		}
		((ConfigurableApplicationContext) ctx).close();
	}
}
