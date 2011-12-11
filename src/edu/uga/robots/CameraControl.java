package edu.uga.robots;

public interface CameraControl {

	public byte[] takeJpegPictureSync();
	public void takeJpegPictureAsync(CameraControlCallback c);
	
	public interface CameraControlCallback {
		public void setJpegPhoto(byte[] b);
	}
}
