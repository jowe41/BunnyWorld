package com.example.bunnyworld;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

import java.util.*;

public class Game {
	static ArrayList<Page> pages;
	static Inventory possession;
	static Page curPage;
	static Page startingPage;
	static SQLiteDatabase db;
	static String gameName = "default";

        //
	static Context context;
	static BitmapDrawable bgDrawable = null;
	//
	
	public static void main() {

		pages = new ArrayList<>();
		possession = new Inventory();
		curPage = new Page("page1");
		Page Page2 = new Page("page2");
		Page Page3 = new Page("page3");
		Page Page4 = new Page("page4");
		Page Page5 = new Page("page5");
		Shape button1 = new Shape(300,700,100,100);
		button1.setScript("on click goto page2;");
		button1.setMovable(false);
		Shape button2 = new Shape(800,700,100,100);
		button2.setNameG("door2");
		button2.setVisible(false);
		button2.setMovable(false);
		button2.setScript("on click goto page3;");
		Shape button3 = new Shape(1300,700,100,100);
		button3.setScript("on click goto page4;");
		button3.setMovable(false);
		Shape button4 = new Shape(100,700,100,100);
		button4.setScript("on click goto page1;");
		button4.setMovable(false);
		Shape button5 = new Shape(900,700,100,100);
		button5.setScript("on click goto page2;");
		button5.setMovable(false);
		Shape button6 = new Shape(1700,700,100,100);
		button6.setVisible(false);
		button6.setNameG("exit");
		button6.setMovable(false);
		button6.setScript("on click goto page5;");
		Shape text1 = new Shape(800,100,200,200);
		text1.setText("Bunny World!");
		text1.setMovable(false);
		Shape text2 = new Shape(800,300,200,200);
		text2.setText("You are in a maze of twisty little passages, all alike");
		text2.setMovable(false);
		Shape text3 = new Shape(800,700,200,200);
		text3.setText("Mystic Bunny Rub my tummy for a big surprise!");
		text3.setMovable(false);
		Shape text4 = new Shape(400,700, 200,200);
		text4.setText("Eek! Fire-room. Run away!");
		text4.setMovable(false);
		Shape text5 = new Shape(600,700,200,200);
		text5.setText("You must appease the Bunny of Death!");
		text5.setMovable(false);
		Shape text6 = new Shape(200,700,200,200);
		text6.setText("You Win!");
		text6.setMovable(false);
		text6.setScript("on enter play victory;");
		Shape bunny1 = new Shape(800,300,400,400);
		//bunny1.setScaled(true);
		bunny1.setImageName("mystic");
		bunny1.setScript("on click hide carrot1 play carrot-eating; on enter show door2;");
		Shape bunny2 = new Shape(1200,200,400,400);
		//bunny2.setScaled(true);
		bunny2.setImageName("death");
		bunny2.setNameG("death-bunny");
		bunny2.setMovable(false);
		bunny2.setScript("on enter play evil-laugh; on drop carrot1 hide carrot1 play carrot-eating hide death-bunny show exit;"
				+ "on click play evil-laugh");
		Shape trap = new Shape(400,200,400,400);
		//trap.setScaled(true);
		trap.setMovable(false);
		trap.setScript("on enter play fire-sound;");
		trap.setImageName("fire");
		Shape carrot1 = new Shape(1300,1000,200,200);
		carrot1.setMovable(true);
		//carrot1.setScaled(true);
		carrot1.setNameG("carrot1");
		carrot1.setImageName("carrot2");
		Shape carrot2 = new Shape(400,200,300,300);
		carrot2.setImageName("carrot");
		Shape carrot3 = new Shape(800,400,300,300);
		carrot3.setImageName("carrot");
		Shape carrot4 = new Shape(1300,300,300,300);
		carrot4.setImageName("carrot");
		pages.add(curPage);
		pages.add(Page2);
		pages.add(Page3);
		pages.add(Page4);
		pages.add(Page5);
		curPage.addShape(button1);
		curPage.addShape(button2);
		curPage.addShape(button3);
		curPage.addShape(text1);
		curPage.addShape(text2);
		Page2.addShape(button4);
		Page2.addShape(text3);
		Page2.addShape(bunny1);
		Page3.addShape(button5);
		Page3.addShape(text4);
		Page3.addShape(carrot1);
		Page3.addShape(trap);
		Page4.addShape(button6);
		Page4.addShape(text5);
		Page4.addShape(bunny2);
		Page5.addShape(carrot2);
		Page5.addShape(carrot3);
		Page5.addShape(carrot4);
		Page5.addShape(text6);

	}

	static Page findPage(String name){
		for (Page page:pages) {
			if (page.getName().equals(name)) {
				return page;
			}
		}
		return null;
	}

	static void loadGame(SQLiteDatabase db, String gName){
		//for (Page page:pages){
		//    pages.remove(page);
		//}
		gameName = gName;
		possession = new Inventory();
		pages = new ArrayList<Page>();
		ArrayList<Shape> shapes = Database.getGameShapes(db, gameName);


		curPage = new Page(Database.getStartPage(db, gameName));
		startingPage = curPage;
		pages.add(curPage);
		for (Shape shape : shapes){
			//String pageName = shape.getShapeName();
			String pageName = shape.getPageName();
			Page page = findPage(pageName);
			if (page == null) {
				page = new Page(pageName);
				pages.add(page);
			}
			page.addShape(shape);
		}
		for (Shape shape:startingPage.getshapes()){
			shape.entered();
		}
	}


	static void gotoPage(String name) {
		for (Page page:pages) {
			if (page.getName().equals(name)) {
				curPage = page;
				for (Shape shape:curPage.getshapes()) {
					shape.entered();
				}
				return;
			}
		}
		System.out.println("No such page.");
	}
	
	static ArrayList<Shape> getShapesOnCurPage() {
		return curPage.getshapes();
	}
	
	static ArrayList<Shape> getShapesOnPos(){
		return possession.getShapes();
	}
	
	public static void hideShape(String shapeName) {
		for(Page page:pages) {
		/*	if (page.getName().equals("page4")) {
				for (Shape shape: page.getshapes()) {
					Log.d("Debug", "Shapes on page4: " + shape.getName() + ", Image: " + shape.getImageName());
				}
			}
*/
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
		for (Shape s:curPage.getshapes()) {
			if (s.equals(shape)) {
				curPage.removeShape(s);
				curPage.addShape(shape);
				return;
			}
		}
		for (Shape s:possession.getShapes()) {
			if (s.equals(shape)) {
				possession.removeShape(s);
				shape.setWidth(shape.getOriginWidth());
				shape.setHeight(shape.getOriginHeight());
				shape.setUseImageBounds(shape.getOriginImageBounds());
				shape.setProportionalScaling(shape.getOriginPS());
				curPage.addShape(shape);
				return;
			}
		}
	}
	
	static void moveToPos(Shape shape) {
		for (Shape s:curPage.getshapes()) {
			if (s.equals(shape)) {
				curPage.removeShape(s);
				shape.setOriginHeight(shape.getHeight());
				shape.setOriginWidth(shape.getWidth());
				shape.setOriginImageBounds(shape.getImageBounds());
				shape.setOriginPS(shape.getPS());
				shape.setProportionalScaling(false);
				shape.setUseImageBounds(false);
				shape.setHeight(100);
				shape.setWidth(100);

				possession.addShape(shape);
				return;
			}
		}
		for (Shape s:possession.getShapes()) {
			if (s.equals(shape)) {
				possession.removeShape(s);
				possession.addShape(shape);
				return;
			}
		}
	}

//
	static List<String> getDrawables() {
		List<String> drawables = new ArrayList<>(Arrays.asList("Grass", "76ers", "Bobcats", "Bucks"
		, "Bulls", "Cavaliers", "Celtics", "Clippers", "Grizzlies", "Hawks", "Heat", "Hornets"
		, "Jazz", "Kings", "Knicks", "Lakers", "Magic", "Mavericks", "Nets", "Nuggets", "Pacers"
		, "Pistons", "Raptors", "Rockets", "Spurs", "Suns", "Thunder", "Timberwolves", "Trail Blazers"
		, "Warriors", "Wizards"));
		return drawables;
	}

	static void setDrawable(String str) {

		switch (str) {
			case "Grass":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.grass);
				break;
			case "76ers":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.sixers);
				break;
			case "Bobcats":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.bobcats);
				break;
			case "Bucks":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.bucks);
				break;
			case "Bulls":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.bulls);
				break;
			case "Cavaliers":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.cavaliers);
				break;
			case "Celtics":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.celtics);
				break;
			case "Clippers":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.clippers);
				break;
			case "Grizzlies":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.grizzlies);
				break;
			case "Hawks":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.hawks);
				break;
			case "Heat":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.heat);
				break;
			case "Hornets":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.hornets);
				break;
			case "Jazz":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.jazz);
				break;
			case "Kings":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.kings);
				break;
			case "Knicks":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.knicks);
				break;
			case "Lakers":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.lakers);
				break;
			case "Magic":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.magic);
				break;
			case "Mavericks":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.mavericks);
				break;
			case "Nets":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.nets);
				break;
			case "Nuggets":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.nuggets);
				break;
			case "Pacers":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.pacers);
				break;
			case "Pistons":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.pistons);
				break;
			case "Raptors":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.raptors);
				break;
			case "Rockets":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.rockets);
				break;
			case "Spurs":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.spurs);
				break;
			case "Suns":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.suns);
				break;
			case "Thunder":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.thunder);
				break;
			case "Timberwolves":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.timberwolves);
				break;
			case "TrailBlazers":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.trailblazers);
				break;
			case "Warriors":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.warriors);
				break;
			case "Wizards":
				bgDrawable = (BitmapDrawable)context.getResources().getDrawable(R.drawable.wizards);
				break;
		}
	}

	static void saveGame(SQLiteDatabase db) {
		List<String> l = Database.getGames(db);
		int i = 1;
		while (l.contains(gameName + "_" + i)) {
			i++;
		}
		Database.saveGame(db, gameName + "_" + i, curPage.getName(), pages);
	}
	
}
