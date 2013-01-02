package musi.interfaces.bsu;

import java.io.IOException;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args) throws IOException {
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("spring-main-context.xml");
		BulkStaticUploader bsu = (BulkStaticUploader) context.getBean("bulkStaticUploader");
		bsu.process();
		
		context.registerShutdownHook();
	}
}
