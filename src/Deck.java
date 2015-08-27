package src;

import java.awt.Point;

public class Deck {
	private Point p1;
	public Point getP1() {
		return p1;
	}

	private void setP1(Point p1) {
		this.p1 = p1;
	}

	public Point getP2() {
		return p2;
	}

	private void setP2(Point p2) {
		this.p2 = p2;
	}

	public Point getP3() {
		return p3;
	}

	private void setP3(Point p3) {
		this.p3 = p3;
	}

	public Point getP4() {
		return p4;
	}

	private void setP4(Point p4) {
		this.p4 = p4;
	}
	
	public String getImageNumber() {
		return ImageNumber;
	}

	public void setImageNumber(String imageNumber) {
		ImageNumber = imageNumber;
	}

	private Point p2;
	private Point p3;
	private Point p4;
	String ImageNumber;

	public Deck(Point p1, Point p2, Point p3, Point p4) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}
	
	public Deck(Point p1, Point p2, Point p3, Point p4, String imageNumber) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		ImageNumber = imageNumber;
	}
}
