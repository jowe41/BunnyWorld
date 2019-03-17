package com.example.bunnyworld;
import java.util.*;

public class Page {
	private String name;
	private ArrayList<Shape> shapes;
	//
	private int ID;
	static private int count = 0;
	//

	Page(String name){
		this.name = name;
		shapes = new ArrayList<>();
		//
		ID = count;
		count++;
		//
	}

	//
	Page(Page p) {
		this.name = p.name;
		this.shapes = p.shapes;
		this.ID = p.ID;
	}

	public int getID() {
		return ID;
	}
	//
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
        for (Page p: Editor.getPages()) {
            if (!p.getshapes().isEmpty()) {
                for (Shape s: p.getshapes()) {
                    String scr = s.getScript();
                    String[] temp = scr.split(";");
                    List<String[]> t = new ArrayList<>();
                    for (String c:temp){
                    	t.add(c.split(" "));
					}
                    String newScr = "";
                    for (String[] l:t){
                    	for (int i = 0; i<l.length;i++){
                    		if (l[i].equals("goto") && l[i+1].equals(name)){
                    			l[i+1] = newName;
							}
						}
					}
					for (String[] l:t) {
						for (int i = 0; i < l.length; i++) {
							newScr += l[i];
							if (i!=(l.length-1)) {
								newScr += " ";
							}
						}
						if (!newScr.equals("")) {
							newScr += ";";
						}
					}
                    s.setScript(newScr);
                }
            }
        }
	    this.name = newName;
	}


	public ArrayList<Shape> getshapes() {
		return shapes;
	}
		
	public void clearPage() {
		for (Shape shape:shapes) {
			shapes.remove(shape);
		}
	}
	
	public void addShape(Shape shape) {
		if(!shapes.contains(shape)) {
			shape.setPageName(this.name);
			shapes.add(shape);
		}
	}
	

	public void removeShape(Shape shape) {
		for (Shape s:shapes) {
			if (shape.equals(s)) {
				s.setPageName("");
				shapes.remove(s);
				return;
			}
		}
		//System.out.println("No such shape.");
	}

}


