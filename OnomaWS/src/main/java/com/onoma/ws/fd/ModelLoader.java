package com.onoma.ws.fd;

import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.bytedeco.javacpp.opencv_core.Mat;

import com.onoma.fd.data.TrainedModel;

public class ModelLoader {
	
	public static final String DATA_ROOT="C:/dev";
	public static final String CSV_FILENAME="faceDB.csv";
	public static final String CSV_SEPARATOR=",";
	public static final String CSV_FILEPATH=DATA_ROOT+"/"+CSV_FILENAME;
	private int nbLinesLoaded;
	
	public void countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        nbLinesLoaded = (count == 0 && !empty) ? 1 : count;
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
	        is.close();
	    }
	}
	
	public boolean loadModelData() {
		
		boolean fileProcessed = false;
		try {
			countLines(CSV_FILEPATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TrainedModel.getInstance().init(nbLinesLoaded);
		String line = "";

		try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILEPATH))) {
	
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] image = line.split(CSV_SEPARATOR);
				String name = image[0];
				int label = Integer.parseInt(image[1]);
				String imgPath = image[2];
				
				if (!imgPath.isEmpty() && label != 0) {
					Mat img = imread(imgPath, CV_LOAD_IMAGE_GRAYSCALE);
					TrainedModel.getInstance().addImage(img, label);
					TrainedModel.getInstance().addLabelName(label, name);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	    fileProcessed=true;
	    return fileProcessed;
	}
	
	public void trainModel() {
		System.out.println(TrainedModel.getInstance().debug());
		TrainedModel.getInstance().train();
	}

}
