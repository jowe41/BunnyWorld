package com.example.bunnyworld;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

public class Editor {
    static ArrayList<Page> pages;
    static Inventory possession;
    static Page curPage;
    static Page startingPage;
    static Shape cutShape;
    static Shape copyShape;
    static Shape selectedShape = null;
    static String gameName;
    static ArrayList<Shape> shapesOnPages;
    //
    static Stack<Page> pageBackup = new Stack<Page>();
    static Stack<Shape> shapeBackup = new Stack<Shape>();
    //


    public static void main() {
        noSelect();
        shapesOnPages = new ArrayList<Shape>();
        pages = new ArrayList<>();
        possession = new Inventory();
        curPage = new Page("page1");
        startingPage = curPage;
        pages.add(curPage);

        Shape bunny1 = new Shape(200, 1000, 100,100);
        bunny1.setName("bunny1");
        bunny1.setImageName("death");
        Shape bunny2 = new Shape(400, 1000,100,100);
        bunny2.setName("bunny2");
        bunny2.setImageName("mystic");
        Shape carrot1 = new Shape(600, 1000, 100,100);
        carrot1.setName("carrot1");
        carrot1.setImageName("carrot");
        Shape carrot2 = new Shape(800, 1000, 100,100);
        carrot2.setName("carrot2");
        carrot2.setImageName("carrot2");
        Shape trap = new Shape(1000, 1000, 100,100);
        trap.setName("trap");
        trap.setImageName("fire");
        Shape duck = new Shape(1200, 1000, 100,100);
        duck.setName("duck");
        duck.setImageName("duck");
        Shape rect = new Shape(1400, 1000, 100,100);
        Shape text = new Shape(1600, 1000, 100,100);
        text.setText("Text");
        possession.addShape(bunny1);
        possession.addShape(bunny2);
        possession.addShape(carrot1);
        possession.addShape(carrot2);
        possession.addShape(trap);
        possession.addShape(duck);
        possession.addShape(rect);
        possession.addShape(text);
    }

    static Page findPage(String name){
        for (Page page:pages) {
            if (page.getName().equals(name)) {
                return page;
            }
        }
        return null;
    }

    static boolean setGameName(String name){
        if (!name.equals("gameList")) {
            gameName = name;
            return true;
        } else{
            return false;
        }
    }

    static void saveGame(SQLiteDatabase db){
        Database.saveGame(db, gameName, startingPage.getName(), pages);
    }

    static void loadGame(SQLiteDatabase db, String gName){
        pages = new ArrayList<Page>();
        gameName = gName;
        possession = new Inventory();
        //selectedShape = null;
        noSelect();
        Shape bunny1 = new Shape(200, 1000, 100,100);
        bunny1.setName("bunny1");
        bunny1.setImageName("death");
        Shape bunny2 = new Shape(400, 1000,100,100);
        bunny2.setName("bunny2");
        bunny2.setImageName("mystic");
        Shape carrot1 = new Shape(600, 1000, 100,100);
        carrot1.setName("carrot1");
        carrot1.setImageName("carrot");
        Shape carrot2 = new Shape(800, 1000, 100,100);
        carrot2.setName("carrot2");
        carrot2.setImageName("carrot2");
        Shape trap = new Shape(1000, 1000, 100,100);
        trap.setName("trap");
        trap.setImageName("fire");
        Shape duck = new Shape(1200, 1000, 100,100);
        duck.setName("duck");
        duck.setImageName("duck");
        Shape rect = new Shape(1400, 1000, 100,100);
        Shape text = new Shape(1600, 1000, 100,100);
        text.setText("Text");
        possession.addShape(bunny1);
        possession.addShape(bunny2);
        possession.addShape(carrot1);
        possession.addShape(carrot2);
        possession.addShape(trap);
        possession.addShape(duck);
        possession.addShape(rect);
        possession.addShape(text);

        shapesOnPages = Database.getGameShapes(db, gName);
        //ArrayList<Shape> shapes = Database.getGameShapes(db, gName);
        curPage = new Page(Database.getStartPage(db, gName));
        startingPage = curPage;
        pages.add(curPage);
        for (Shape shape:shapesOnPages){
            //String pageName = shape.getShapeName();
            String pageName = shape.getPageName();
            Page page = findPage(pageName);
            if (page == null) {
                page = new Page(pageName);
                pages.add(page);
            }
            page.addShape(shape);
        }
    }

    static void gotoPage(String name) {
        noSelect();
        curPage = findPage(name);
        for (Shape shape:curPage.getshapes()) {
            shape.entered();
        }
        return;
    }

    static void clearPage(String name){
        noSelect();
        Page page = findPage(name);
        page.clearPage();
    }


    static void addPage(String name){
        if (name.equals("")){
            Integer length = pages.size() + 1;
            Page page = new Page("page"+Integer.toString(length));
            pages.add(page);
        }
        else if (findPage(name) == null) {
            Page page = new Page(name);
            pages.add(page);
            //
            pageBackup.push(new Page(page));
            //
        }
    }

    static ArrayList<Page> getPages(){
        return pages;
    }

    static boolean deletePage(String name) {
        if (name.equals(startingPage.getName())){
            return false;
        }
        Page page = findPage(name);
        //
        pageBackup.push(new Page(page));
        //
        if (page.equals(curPage)){
            curPage = startingPage;
            gotoPage(curPage.getName());
        }
        pages.remove(page);
        return true;
    }

    static void setPageName(String name, String newName){
        Page page = findPage(name);
        //
        pageBackup.push(new Page(page));
        //
        page.setName(newName);
        for (Shape s:page.getshapes()){
            s.setPageName(newName);
        }
    }

    //Set the name of shape to be default
    static void setShapeName(Shape shape){
        shape.setName("shape" + Integer.toString(shapesOnPages.size() + 1));
        shapesOnPages.add(shape);
    }

    //
    static String undoPage() {
        if (pageBackup.empty()) {
            return "Failed! No more actions to undo";
        } else {
            Page p = pageBackup.pop();
            for (Page page : pages) {
                if (page.getID() == p.getID()) {
                    if (page.getName() == p.getName()) {
                        if (startingPage == page) {
                            pageBackup.push(p);
                            return "Failed! Cannot undo adding a page after it becomes the starting page";
                        } else {
                            pages.remove(page);
                            if (curPage == page) {
                                curPage = startingPage;
                            }
                            return "Succeeded! The added page has been deleted";
                        }
                    } else {
                        page.setName(p.getName());
                        for (Shape s : page.getshapes()) {
                            s.setPageName(page.getName());
                        }
                        return "Succeeded! The name of the page has been recovered";
                    }
                }
            }
            pages.add(p);
            return "Succeeded! The deleted page has been recovered";
        }
    }


    static String undoShape() {
        selectedShape = null;
        if (shapeBackup.empty()) {
            return "Failed! No more actions to undo";
        } else {
            Shape s = shapeBackup.pop();
            for (Page p : pages) {
                for (Shape shape : p.getshapes()) {
                    if (shape.getID() == s.getID()) {
                        if (s.getPageName() == "") {
                            p.removeShape(shape);
                            return "Succeeded! The added shape has been deleted";
                        } else {
                            for (Page pp : pages) {
                                if (pp.getName() == s.getPageName()) {
                                    p.removeShape(shape);
                                    pp.addShape(s);
                                    return "Succeeded! The modified shape has been recovered";
                                }
                            }
                            shapeBackup.push(s);
                            return "Failed! Cannot undo the modification after its previous page has gone";
                        }
                    }
                }
            }
            for (Page p : pages) {
                if (s.getPageName() == p.getName()) {
                    p.addShape(s);
                    return "Succeeded! The deleted shape has been recovered";
                }
            }
            shapeBackup.push(s);
            return "Failed! Cannot undo deleting a shape after its page is gone";
        }
    }
    //

    static ArrayList<Shape> getShapesOnPage() {return shapesOnPages;}

    static ArrayList<Shape> getShapesOnCurPage() {
        return curPage.getshapes();
    }

    static ArrayList<Shape> getShapesOnPos(){
        return possession.getShapes();
    }

    public static void hideShape(String shapeName) {
        for(Page page:pages) {
            for (Shape shape: page.getshapes()) {
                if (shape.getName().equals(shapeName)) {
                    shape.setVisible(false);
                }
            }
        }
    }

    public static void showShape(String shapeName) {
        for(Page page:pages) {
            for (Shape shape: page.getshapes()) {
                if (shape.getName().equals(shapeName)) {
                    shape.setVisible(true);
                }
            }
        }
    }

    public static void select(Shape shape){
        selectedShape = shape;
    }

    public static Shape getSelectedShape() {
        return selectedShape;
    }

    public static void noSelect(){
        selectedShape = null;
    }

    public static void cutShape(){
        curPage.removeShape(selectedShape);
        cutShape = new Shape(selectedShape);
        selectedShape = null;
        copyShape = null;
    }

    public static void copyShape(){
         copyShape = new Shape(selectedShape);
    }

    public static void pasteShape(){
        if (cutShape!=null){
            setShapeName(cutShape);
            curPage.addShape(cutShape);
            cutShape.setX(500);
            cutShape.setY(500);
            selectedShape = cutShape;
            cutShape = null;
        }
        else if (copyShape!=null) {
            Shape pasteShape = new Shape(copyShape);
            setShapeName(pasteShape);
            curPage.addShape(pasteShape);
            pasteShape.setPageName(curPage.getName());
            pasteShape.setX(500);
            pasteShape.setY(500);
        }
    }

    public static void putDividingBoundary(float h) {
        for (Page page:pages) {
            for (Shape shape:page.getshapes()) {
                shape.limitTopHeight(h);
            }
        }
        for (Shape shape:possession.getShapes()) {
            shape.limitBottomHeight(h);
        }
    }

    static void moveToCurPage(Shape shape) {
        setShapeName(shape);
        curPage.addShape(shape);
        shape.setPageName(curPage.getName());
    }
}
