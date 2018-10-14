package com.onoma.fd.data;


import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;

public class TrainedModel {
	
	public static final int MAX_IMG_NB = 2060;

	public static TrainedModel singleton;
	private MatVector images;
	private Mat labels;
	private int counter;
	private IntBuffer labelsBuf;
	private Map<Integer, String> labelNameMap;
	private FaceRecognizer model;

	private TrainedModel() {
		counter = 0;
		labelNameMap = new HashMap<>();	
	}

	public static TrainedModel getInstance() {
		if (singleton == null) {
			singleton = new TrainedModel();
		}
		return singleton;
	}
	
	public void addImage(Mat image, int label) {
		images.put(counter, image);
        labelsBuf.put(counter, label);
        counter++;
	}
	
	public void addImage(IplImage image, int label) {
		images.put(image);
        labelsBuf.put(counter, label);
        counter++;
	}
	
	public void init(int nbImages) {
		images = new MatVector(nbImages);
		labels = new Mat(nbImages, 1, CV_32SC1);
		labelsBuf = labels.createBuffer();
	}
	
	public void train() {
		// The LBPHFaceRecognizer uses Extended Local Binary Patterns
	    // (it's probably configurable with other operators at a later
	    // point), and has the following default values
	    //
	    //      radius = 1
	    //      neighbors = 8
	    //      grid_x = 8
	    //      grid_y = 8
	    //      threshold (e.g. 123.0)
	    //
	    //FaceRecognizer model = LBPHFaceRecognizer.createLBPHFaceRecognizer(1,8,8,8,130.0);
		this.model = org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer.create();
		this.model.train(images, labels);
	}
	
	public void addLabelName(int label, String name) {
		labelNameMap.put(label, name);
	}
	
	public MatVector getImages() {
		return images;
	}

	public Mat getLabels() {
		return labels;
	}

	public int getCounter() {
		return counter;
	}
	
	public String getStats() {
		return counter+" images loaded in the model";
	}
	
	public String debug() {
		StringBuilder sb = new StringBuilder();
		sb.append("1st element: ").append("\n");
		sb.append("label:").append(labelsBuf.get(0)).append("\n");
		sb.append("Mat:").append(images.get(0)).append("\n");
		
		return sb.toString();
	}

	public FaceRecognizer getModel() {
		return model;
	}

	public void setModel(FaceRecognizer model) {
		this.model = model;
	}

}
