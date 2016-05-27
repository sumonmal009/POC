package awsS3UploadPublish;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import awsS3UploadPublish.handler.AWSS3Handler;

//@Configuration
//@ComponentScan(basePackages = "awsS3UploadPublish")
//@PropertySource(value = { "classpath:application.properties" })
public class App 
{
	/*
	 * PropertySourcesPlaceHolderConfigurer Bean only required for @Value("{}") annotations.
	 * Remove this bean if you are not using @Value annotations for injecting properties.
	 */
	/*@Bean
    public static PropertyPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       // return new PropertySourcesPlaceholderConfigurer();
		
		return new PropertyPlaceholderConfigurer();
    }
	*/ 



	public static void main( String[] args )
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");    

		AWSS3Handler awsHandler= (AWSS3Handler) context.getBean("aws");
		awsHandler.process();
		((ConfigurableApplicationContext)context).close();

	}
}
