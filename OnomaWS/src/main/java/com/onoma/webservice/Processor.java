package com.onoma.webservice;


import com.google.gson.Gson;
import com.onoma.fd.data.*;

import java.util.Optional;

public class Processor {
	
	public String identifyPerson(String imgPath) {
		
		String result;
		
		ImageElt imgElt = new ImageElt(imgPath);
		Optional<FaceMatch> opMatch = imgElt.predictName();

		if (opMatch.isPresent()) {
			FaceMatch match = opMatch.get();
			Gson gson = new Gson();
			//convert FaceMatch to JSON and to string
			result = gson.toJson(match);
		} else {
			result = "nothing detected for "+imgPath;
		}
		
		return result;
	}

}
