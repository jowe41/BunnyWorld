package com.example.bunnyworld;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Database {
    protected static ArrayList<String> getGames(SQLiteDatabase db) {
        ArrayList<String> gameList = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT game FROM gameList;",null);
        while (cursor.moveToNext()) {
            gameList.add(cursor.getString(0));
        }
        return gameList;
    }

    protected static String getStartPage(SQLiteDatabase db, String name) {
        if (!getGames(db).contains(name)) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT startPage FROM gameList WHERE game = '" + name + "';",null);
        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    protected static ArrayList<Shape> getGameShapes(SQLiteDatabase db, String name) {
        if (!getGames(db).contains(name)) {
            return null;
        }
        ArrayList<Shape> shapeList = new ArrayList<Shape>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + name + ";",null);
        while (cursor.moveToNext()) {
            shapeList.add(new Shape(cursor.getFloat(0), cursor.getFloat(1),
                    cursor.getFloat(2), cursor.getFloat(3), cursor.getInt(4),
                    cursor.getString(5), cursor.getString(6), cursor.getString(7),
                    cursor.getString(8), cursor.getFloat(9), cursor.getString(10),
                    cursor.getInt(11), cursor.getInt(12), cursor.getInt(13),
                    cursor.getInt(14), cursor.getInt(15), cursor.getInt(16),
                    cursor.getInt(17)));
        }
        return shapeList;
    }

    protected static void saveGame(SQLiteDatabase db, String name, String startPage, List<Page> pageList) {
        if (getGames(db).contains(name)) {
            db.execSQL("UPDATE gameList SET startPage = '" + startPage + "' WHERE game = '" + name + "';");
            db.execSQL("DROP TABLE IF EXISTS " + name + ";");
        } else {
            db.execSQL("INSERT INTO gameList VALUES ('" + name + "', '" + startPage + "', NULL);");
        }
        db.execSQL("CREATE TABLE " + name + " (x FLOAT, y FLOAT, w FLOAT, h FLOAT, typ INTEGER, " +
                "sName TEXT, imName TEXT, scr TEXT, txt TEXT, tSize FLOAT, pName TEXT, mov INTEGER, " +
                "vis INTEGER, use INTEGER, pro INTEGER, tFont INTEGER, tBold INTEGER, tItalic INTEGER, _id INTEGER PRIMARY KEY AUTOINCREMENT);");
        for (Page p : pageList) {
            for (Shape s : p.getshapes()) {
                db.execSQL("INSERT INTO " + name + " VALUES (" + s.getX() + ", " + s.getY() + ", "
                        + s.getWidth() + ", " + s.getHeight() + ", " + s.getType() + ", '" + s.getName()
                        + "', '" + s.getImageName() + "', '" + s.getScript() + "', '" + s.getText()
                        + "', " + s.getTextSize() + ", '" + s.getPageName() + "', " + s.getMov()
                        + ", " + s.getVis() + ", " + s.getUse() + ", " + s.getPro() + ", " + s.getTextFont()
                        + ", " + s.getTextBold() + ", " + s.getTextItalic() + ", NULL);");
            }
        }
    }

    protected static void deleteGame(SQLiteDatabase db, String name) {
        if (getGames(db).contains(name)) {
            db.execSQL("DELETE FROM gameList WHERE game = '" + name + "';");
            db.execSQL("DROP TABLE IF EXISTS " + name + ";");
        }
    }

    protected static void emptyDB(SQLiteDatabase db) {
        for (String str : getGames(db)) {
            deleteGame(db, str);
        }
    }
}
