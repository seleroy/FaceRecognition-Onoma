package com.onoma.fd.data;

public class FaceMatch {
	
	private int label;
	private double confidence;
	private boolean matchFound;
	private String status;
	
	public int getLabel() {
		return label;
	}
	public void setLabel(int label) {
		this.label = label;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public boolean isMatchFound() {
		return matchFound;
	}
	public void setMatchFound(boolean matchFound) {
		this.matchFound = matchFound;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
