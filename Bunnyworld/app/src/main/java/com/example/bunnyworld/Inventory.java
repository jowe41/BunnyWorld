package com.example.bunnyworld;
import java.util.*;

public class Inventory {
	private ArrayList<Shape> shapes;
	
	Inventory() {
		shapes = new ArrayList<>();
	}
	
	public ArrayList<Shape> getShapes() {
		return shapes;
	}
	
	public void addShape(Shape shape) {
		if(!shapes.contains(shape)) {
			shape.setPageName("poss");
			shapes.add(shape);
		}
	}
	

	public void removeShape(Shape shape) {
		for (Shape s:shapes) {
			if (shape.equals(s)) {
				shapes.remove(s);
				return;
			}
		}
	}
}

