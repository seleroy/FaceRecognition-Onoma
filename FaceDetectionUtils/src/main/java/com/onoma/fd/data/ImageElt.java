package com.onoma.fd.data;

import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

import java.util.Optional;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

import com.onoma.fd.exception.OnomaMissingFileException;

public class ImageElt {

	private String path;
	private Mat originalMat;
	private Mat greyFaceMat;
	private Mat framedFaceOnOrigMat;
	
	
	public ImageElt(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Mat getOriginalMat() {
		return originalMat;
	}
	public void setOriginalMat(Mat originalMat) {
		this.originalMat = originalMat;
	}
	public Mat getGreyFaceMat() {
		return greyFaceMat;
	}
	public void setGreyFaceMat(Mat greyFaceMat) {
		this.greyFaceMat = greyFaceMat;
	}
	public Mat getFramedFaceOnOrigMat() {
		return framedFaceOnOrigMat;
	}
	public void setFramedFaceOnOrigMat(Mat framedFaceOnOrigMat) {
		this.framedFaceOnOrigMat = framedFaceOnOrigMat;
	}
	
	
	public Optional<Mat> detectFace() throws OnomaMissingFileException  {
		
	    if (this.originalMat.empty()) {
	    	System.out.println("Original mat is empty");
	    	return Optional.empty();
	    }
	    
	    Mat greyMat = new Mat();
	    cvtColor(this.originalMat, greyMat, COLOR_BGR2GRAY);
	    RectVector faces = new RectVector();
	    if (!greyMat.empty()){
	    	
	        CascadeClassifier cascade = new CascadeClassifier();
	        if(!cascade.load("C:/dev/opencv/data/haarcascades/haarcascade_frontalface_alt.xml")){
	        	cascade.close();
	            throw new OnomaMissingFileException("Could not open file haarcascade_frontalface_alt");
	        }
	        //cascade.detectMultiScale(greyMat, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(5, 5), new Size(20, 20));
	        cascade.detectMultiScale(greyMat, faces);
	        cascade.close();
	    }
	    
	    System.out.println("Detect multiscale is done");
	    
	    if (faces.size()==0){
	    	return Optional.empty();
	    }

	    // drawing the detected face on the original image
	    int x = faces.get(0).x();
	    int h_temp = faces.get(0).height();    // storing original height
	    int y = (int) (faces.get(0).y() - h_temp*0.3); // y is reduced by 0.3*h
	    int w = faces.get(0).width();
	    int h = (int) (h_temp*1.6);

	    this.framedFaceOnOrigMat = this.originalMat;
	    rectangle(this.framedFaceOnOrigMat,new Point (x,y),new Point(x + w,y +h), new Scalar(0, 255, 0, 255),1,4,0);

	    return Optional.of(greyMat);
		
	}
	
	public Optional<FaceMatch> predictName() {
		
		if (this.path.trim().isEmpty()) {
			return Optional.empty();
		}
		
		this.originalMat = imread(this.path);
	    try {
			this.greyFaceMat = detectFace().orElse(new Mat());
		} catch (OnomaMissingFileException e) {
			e.printStackTrace();
		}
	    
	    if (this.greyFaceMat.empty()) {
	    	FaceMatch noMatch = new FaceMatch();
	    	noMatch.setStatus("No face detected on the provided image");
	    	return Optional.of(noMatch);
	    }
	    

	    
	    if(TrainedModel.getInstance().getImages().isNull() || TrainedModel.getInstance().getLabels().empty()) {
	    	FaceMatch noMatch = new FaceMatch();
	    	noMatch.setStatus("Training Model is empty");
	    	return Optional.of(noMatch);
	    }
	    
	    if (TrainedModel.getInstance().getModel()== null) {
	    	FaceMatch noMatch = new FaceMatch();
	    	noMatch.setStatus("Model is not available");
	    	return Optional.of(noMatch);
	    }
	    
	    IntPointer label = new IntPointer(1);
	    DoublePointer confidence = new DoublePointer(1);
	    TrainedModel.getInstance().getModel().predict(this.greyFaceMat, label, confidence);
	    
	    FaceMatch bestMatch = new FaceMatch();
	    
	    bestMatch.setConfidence(confidence.get());
	    bestMatch.setLabel(label.get(0));
	    
	    if (bestMatch.getLabel()!=-1){
	        bestMatch.setMatchFound(true);
	    }
	    
	    return Optional.of(bestMatch);
	}

}
