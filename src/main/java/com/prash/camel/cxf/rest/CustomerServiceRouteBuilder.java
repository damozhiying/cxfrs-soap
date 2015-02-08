package com.prash.camel.cxf.rest;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.prash.sample.model.Customer;

public class CustomerServiceRouteBuilder extends RouteBuilder {

	public void configure1() throws Exception {
		// configure we want to use servlet as the component for the rest DSL
		// and we enable json binding mode
		restConfiguration().component("servlet").bindingMode(RestBindingMode.json)
				// and output using pretty print
				.dataFormatProperty("prettyPrint", "true")
				// setup context path and port number that Apache Tomcat will deploy
				// this application with, as we use the servlet component, then we
				// need to aid Camel to tell it these details so Camel knows the url
				// to the REST services.
				// Notice: This is optional, but needed if the RestRegistry should
				// enlist accurate information. You can access the RestRegistry from JMX at runtime
				.contextPath("/rest-sample")
				.port(8080);
		rest("/customerservice").description("Customer rest service")
				.consumes("application/json").produces("application/json")
				.get("/{id}").description("Find user by id")
				.outType(Customer.class)
				.to("bean:customerService?method=getCustomer(${body})");
	}
	
	@Override
	public void configure() throws Exception {
		System.out.println("prashanth creating the routes");
		from("cxfrs:bean:rsServer")
		.process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				Resource resource = new ClassPathResource("data/soapsample.xml");
			    File file = resource.getFile();
			    if (!file.exists()) {
			        throw new FileNotFoundException(String.format("File %s not found on %s", "soapsample.xml", "data/"));
			    }
			    exchange.getIn().setBody(file);
			}
		})
		.convertBodyTo(String.class)
		.to("log:${body}")
		.setHeader(CxfConstants.OPERATION_NAME, simple("getCardDetails"))
		.setHeader(CxfConstants.OPERATION_NAMESPACE, simple("http://jaxws.sample.camel.prash.com/"))
		.process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				String soapContent = exchange.getIn().getBody(String.class);
				System.out.println("route invoked "+soapContent);
			}
		})
		.to("log:${body}")
		.to("cxf:bean:soapEndpoint?dataFormat=MESSAGE");
	}


}
