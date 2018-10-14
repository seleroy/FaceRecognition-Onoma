package com.onoma.webservice;

import javax.ws.rs.ext.Provider;

import com.onoma.fd.data.TrainedModel;
import com.onoma.ws.fd.ModelLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
@Provider
public class OnomaListener implements ServletContextListener  {

	 @Override
	    public void contextInitialized(ServletContextEvent arg0) {
	        System.out.println("------------ Custom Initialization start -----------");  
	        ModelLoader loader = new ModelLoader();
	        if(!loader.loadModelData()) {
	        	System.out.println("Error while loading the model, shuting down");
	        	System.exit(1);
	        }
	        System.out.println(TrainedModel.getInstance().getStats());
	        loader.trainModel();
			System.out.println("Model has been trained");
	        System.out.println("------------ Custom Initialization end -------------"); 
	    }

	    @Override
	    public void contextDestroyed(ServletContextEvent arg0) {
	    }
}