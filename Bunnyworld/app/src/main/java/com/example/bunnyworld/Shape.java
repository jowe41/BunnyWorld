package com.example.bunnyworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shape {

	private float x, y, width, height, oWidth, oHeight;         // x and y are the coordinates of the center of the shape

	private int type;                      // determine if the shape is rectangle, image or text
	private String name;
	private String imageName;            // Name of the associated image
	private Script script;
	private String text;
	private float textSize;
	private int textFont;
	private boolean textBold;
	private boolean textItalic;
	private String pageName;    // List of pages which contain the corresponding shape
	
	private boolean movable;             // True if the shape is movable
	private boolean visible;             // True if the shape is visible
	private boolean visibleInEditor;       // True if the shape is set to be invisible in editor
	private boolean useImageBounds;              // True if the user choose to use default size of the image
	private boolean proportionalScaling;            // True if the user wants to resize the shape with the original proportion
	private boolean originUseImageBounds;
	private boolean originProportionalScaling;

	//
	private int ID;
	private static int count = 0;
	//
	
	private static int shapeNum = 0;         // Incremented when a new shape is created
	private static final int RECTANGLE = 1;
	private static final int IMAGE = 2;
	private static final int TEXT = 3;

	private static final float DEFAULT_TEXTSIZE = 50.0f;
	static final int DEFAULT_FONT = 0;
	static final int MONOSPACE = 1;
	static final int SANS_SERIF = 2;

	private static final ArrayList<String> imageList = new ArrayList<String>(Arrays.asList("carrot", "carrot2", "death", "duck", "fire", "mystic"));
	private static final ArrayList<String> fontList = new ArrayList<String>(Arrays.asList(""));
	private static final Map<String, Float> proportionList = new HashMap<String, Float>(){
		{
			put("carrot", 0.840f);
			put("carrot2", 0.944f);
			put("death", 0.933f);
			put("duck", 1.547f);
			put("fire", 1.226f);
			put("mystic", 0.810f);
		}
	};

	// Trigger and Actions
	private static final String ONCLICK = "on click";
	private static final String ONENTER = "on enter";
	private static final String ONDROP = "on drop";
	private static final String GOTO = "goto";
	private static final String PLAY = "play";
	private static final String HIDE = "hide";
	private static final String SHOW = "show";

	// Name of Sounds
	private static final String CARROT = "carrot-eating";
	private static final String EVIL = "evil-laugh";
	private static final String FIRE = "fire-sound";
	private static final String HOORAY = "victory";
	private static final String MUNCH = "munch";
	private static final String MUNCHING = "munching";
	private static final String WOOF = "woof";

	private Paint grayFillPaint;
	private Paint opaquePaint;
	private Paint blueOutlinePaint;
	private Paint textPaint;
	static Context context;
	
	public Shape(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		type = RECTANGLE;
		shapeNum++;
		name = "shape" + shapeNum;
		imageName = "";
		script = new Script("");
		text = "";
		pageName = "";
		textSize = DEFAULT_TEXTSIZE;
		textFont = DEFAULT_FONT;
		textBold = false; textItalic = false;
		
		movable = true;
		visible = true;
		useImageBounds = false;
		proportionalScaling = false;

		grayFillPaint = new Paint();
		grayFillPaint.setColor(Color.rgb(211, 211, 211));
		blueOutlinePaint = new Paint();
		blueOutlinePaint.setColor(Color.BLUE);
		blueOutlinePaint.setStyle(Paint.Style.STROKE);
		blueOutlinePaint.setStrokeWidth(20.0f);

		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);

                //
		ID = count;
		count++;
		//
	}

	// Alternative constructor
	// Parameters:
	// shape name, page name, type, x, y, width, height,
	// image name ("" if no image), text ("" if no text), script,
	// movable, visible, use image bounds (true if you want to use the default size of the image)
	public Shape(float x, float y, float width, float height, int typ, String sName, String imName,
				 String scr, String txt, float tSize, String pName, int mov, int vis, int useImBounds,
				 int proScaling, int tFont, int tBold, int tItalic) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		shapeNum++;
        name = sName;
		pageName = pName;
		script = new Script(scr);
		//
		ID = count;
		count++;
		//

		//********************Use Integer to represent Boolean in Database**********************
		if (mov == 0) {
			movable = false;
		} else {
			movable = true;
		}

		if (vis == 0) {
			visible = false;
		} else {
			visible = true;
		}

		if (useImBounds == 0) {
			useImageBounds = false;
		} else {
			useImageBounds = true;
		}

		if (proScaling == 0) {
			proportionalScaling = false;
		} else {
			proportionalScaling = true;
		}

		if (tBold == 0) {
			textBold = false;
		} else {
			textBold = true;
		}

		if (tItalic == 0) {
			textItalic = false;
		} else {
			textItalic = true;
		}
		//*************************************************************************************

		if (typ != RECTANGLE && typ != IMAGE && typ != TEXT) {
			type = RECTANGLE;
		} else {
			type = typ;
		}

		if (type == IMAGE) {
			imageName = imName;
		} else {
			imageName = "";
		}

		if (type == TEXT) {
			text = txt;
		} else {
			text = "";
		}

		textSize = tSize;
		textFont = tFont;

		grayFillPaint = new Paint();
		grayFillPaint.setColor(Color.rgb(211, 211, 211));
		blueOutlinePaint = new Paint();
		blueOutlinePaint.setColor(Color.BLUE);
		blueOutlinePaint.setStyle(Paint.Style.STROKE);
		blueOutlinePaint.setStrokeWidth(5.0f);

		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
	}

	// Copy Constructor, use this constructor to copy a shape
	// Pass in a Shape object you want to copy from
	// The name of the shape will be the name of the copied shape plus "_copy"
	public Shape(Shape shape) {
		this.x = shape.x;
		this.y = shape.y;
		this.width = shape.width;
		this.height = shape.height;

		shapeNum++;
		name = shape.name + "_copy";
		type = shape.type;
		imageName = shape.imageName;
		script = shape.script;
		text = shape.text;
		textSize = shape.textSize;
		textFont = shape.textFont;
		textBold = shape.textBold;
		textItalic = shape.textItalic;
		pageName = shape.pageName;
		movable = shape.movable;
		visible = shape.visible;
		useImageBounds = shape.useImageBounds;
		proportionalScaling = shape.proportionalScaling;

		grayFillPaint = new Paint();
		grayFillPaint.setColor(Color.rgb(211, 211, 211));
		blueOutlinePaint = new Paint();
		blueOutlinePaint.setColor(Color.BLUE);
		blueOutlinePaint.setStyle(Paint.Style.STROKE);
		blueOutlinePaint.setStrokeWidth(5.0f);

		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		//
		ID = count;
		count++;
		//
	}

//
    public int getID() {
	    return ID;
    }

    public void setID(int a) {
	    ID = a;
    }

    //

	//-------------------------------Set shape parameters--------------------------------------------------
	// Here setX and setY is only used for game editing, in game playing we use move()
	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setWidth(float width) {
		if (proportionalScaling && !useImageBounds && type == IMAGE && proportionList.get(this.getImageName()) != null) {
			this.width = height * proportionList.get(this.getImageName());
		} else {
			this.width = width;
		}
	}

	// Here we assume that height will depend on width if the user choose proportional scaling
	public void setHeight(float height) {
		if (proportionalScaling && !useImageBounds && type == IMAGE && proportionList.get(this.getImageName()) != null) {
			this.height = width / proportionList.get(this.getImageName());
		} else {
			this.height = height;
		}
	}

	public void setOriginWidth(float width) {
		this.oWidth = width;
	}

	public void setOriginHeight(float height) {
		this.oHeight = height;
	}

	// Replace the new name with the old name in the script when setting a new name for the shape
	public void setName(String str) {
//       	checkName(str, getName());
		Log.d("Debug1", name);
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
						for (int i = 0; i<l.length-1;i++){
							if ((l[i].equals("drop") || l[i].equals("hide") || l[i].equals("show")) &&
									(l[i+1].equals(name) || l[i+1].equals(name + "_copy"))){
								l[i+1] = str;
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
		this.name = str;
	}

	public void setNameG(String newName){
		this.name = newName;
	}

	public void setImageName(String imageName) {
		// If there's no such image, shape remains to be a rectangle
		if (imageList.contains(imageName)) {
			this.imageName = imageName;
			// If shape contains text, text takes precedence
			if (text.equals("")) {
				type = IMAGE;
			} else {
				type = TEXT;
			}
			// Refer to the image indicated by imageName
		}
	}
	
	public void setScript(String str) {
		String concatenatedStr = concatenateClause(str);
		this.script = new Script(concatenatedStr);
	}
	
	public void setText(String text) {
		this.text = text;
		type = TEXT;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public void setTextFont(int textFont) {
		this.textFont = textFont;
	}

	public void setTextBold(boolean bool) {
		this.textBold = bool;
	}

	public void setTextItalic(boolean bool) {
		this.textItalic = bool;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
//		if (!visible) {
//			this.movable = false;
//		}
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
//		if (!visible) {
//			this.movable = false;
//		}
	}
	
	public void setUseImageBounds(boolean bool) {
		this.useImageBounds = bool;
	}

	public void setProportionalScaling(boolean bool) {
		this.proportionalScaling = bool;
	}

	public void setOriginImageBounds(boolean bool) {this.originUseImageBounds = bool;}

	public void setOriginPS(boolean bool) {
		this.originProportionalScaling = bool;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	//-------------------------------Get shape parameters--------------------------------------------------
	public boolean getImageBounds(){return useImageBounds;}

	public boolean getPS(){return proportionalScaling;}

	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}

	public float getOriginWidth() {
		return oWidth;
	}

	public float getOriginHeight() {
		return oHeight;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImageName() {
		return imageName;
	}

	public String getPageName() {
		return pageName;
	}
	
	public String getScript() {
		return script.toString();
	}
	
	public String getText() {
		return text;
	}

	public float getTextSize() {
		return textSize;
	}

	public int getTextFont() {
		return textFont;
	}

	//-------------------Used for data saving-------------------------
	public int getMov() {
		if (movable) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getVis() {
		if (visible) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getUse() {
		if (useImageBounds) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getPro() {
		if (proportionalScaling) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getTextBold() {
		if (textBold) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getTextItalic() {
		if (textItalic) {
			return 1;
		} else {
			return 0;
		}
	}
	//-------------------------------------------------------------------
	public boolean getOriginImageBounds() {
		return originUseImageBounds;
	}

	public boolean getOriginPS() {
		return originProportionalScaling;
	}

	public boolean isMovable() {
		return movable;
	}

	public boolean isVisible() {
		return visible;
	}

	//-------------------------------Shape Related Functions--------------------------------------------------
	public void drawOutline(Canvas canvas) {
        canvas.drawRect(x - width/2, y - height/2, x + width/2, y + height/2, blueOutlinePaint);
    }

    // Draw thenselves
	public void draw(Canvas canvas, Shape shape) {
		BitmapDrawable carrotDrawable, carrot2Drawable, deathDrawable, duckDrawable,fireDrawable, mysticDrawable;
		if (visible) {
		    if (shape != null && this.droppableBy(shape)) {
                canvas.drawRect(x - width/2, y - height/2, x + width/2, y + height/2, blueOutlinePaint);
            }
			if (type == RECTANGLE) {
				// Draw a light gray rectangle
				canvas.drawRect(x - width/2, y - height/2, x + width/2, y + height/2, grayFillPaint);
			} else if (type == IMAGE) {
				// If the shape does not have an image or the image cannot be loaded, draw a light gray rectangle
				if (!imageList.contains(imageName)) {
					canvas.drawRect(x - width/2, y - height/2, x + width/2, y + height/2, grayFillPaint);
				} else {
					// Load Bitmap resource and draw it on canvas
					if (!useImageBounds) {
						if (imageName.equals("carrot")) {
							carrotDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.carrot);
							canvas.drawBitmap(carrotDrawable.getBitmap(), null, new RectF(x - width/2, y - height/2, x + width/2, y + height/2), null);
						} else if (imageName.equals("carrot2")) {
							carrot2Drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.carrot2);
							canvas.drawBitmap(carrot2Drawable.getBitmap(), null, new RectF(x - width/2, y - height/2, x + width/2, y + height/2), null);
						} else if (imageName.equals("death")) {
							deathDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.death);
							canvas.drawBitmap(deathDrawable.getBitmap(), null, new RectF(x - width/2, y - height/2, x + width/2, y + height/2), null);
						} else if (imageName.equals("duck")) {
							duckDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.duck);
							canvas.drawBitmap(duckDrawable.getBitmap(), null, new RectF(x - width/2, y - height/2, x + width/2, y + height/2), null);
						} else if (imageName.equals("fire")) {
							fireDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.fire);
							canvas.drawBitmap(fireDrawable.getBitmap(), null, new RectF(x - width/2, y - height/2, x + width/2, y + height/2), null);
						} else if (imageName.equals("mystic")) {
							mysticDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mystic);
							canvas.drawBitmap(mysticDrawable.getBitmap(), null, new RectF(x - width/2, y - height/2, x + width/2, y + height/2), null);
						}
					} else {
						// Use default size of the image regardless of the input width and height
						if (imageName.equals("carrot")) {
							carrotDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.carrot);
							Bitmap carrotMap = carrotDrawable.getBitmap();
							canvas.drawBitmap(carrotMap, x - carrotMap.getWidth()/2.0f, y - carrotMap.getHeight()/2.0f, null);
							width = (float)carrotMap.getWidth(); height = (float)carrotMap.getHeight();
						} else if (imageName.equals("carrot2")) {
							carrot2Drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.carrot2);
							Bitmap carrot2Map = carrot2Drawable.getBitmap();
							canvas.drawBitmap(carrot2Map, x - carrot2Map.getWidth()/2.0f, y - carrot2Map.getHeight()/2.0f, null);
							width = (float)carrot2Map.getWidth(); height = (float)carrot2Map.getHeight();
						} else if (imageName.equals("death")) {
							deathDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.death);
							Bitmap deathMap = deathDrawable.getBitmap();
							canvas.drawBitmap(deathMap, x - deathMap.getWidth()/2.0f, y - deathMap.getHeight()/2.0f, null);
							width = (float)deathMap.getWidth(); height = (float)deathMap.getHeight();
						} else if (imageName.equals("duck")) {
							duckDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.duck);
							Bitmap duckMap = duckDrawable.getBitmap();
							canvas.drawBitmap(duckMap, x - duckMap.getWidth()/2.0f, y - duckMap.getHeight()/2.0f, null);
							width = (float)duckMap.getWidth(); height = (float)duckMap.getHeight();
						} else if (imageName.equals("fire")) {
							fireDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.fire);
							Bitmap fireMap = fireDrawable.getBitmap();
							canvas.drawBitmap(fireMap, x - fireMap.getWidth()/2.0f, y - fireMap.getHeight()/2.0f, null);
							width = (float)fireMap.getWidth(); height = (float)fireMap.getHeight();
						} else if (imageName.equals("mystic")) {
							mysticDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mystic);
							Bitmap mysticMap = mysticDrawable.getBitmap();
							canvas.drawBitmap(mysticMap, x - mysticMap.getWidth()/2.0f, y - mysticMap.getHeight()/2.0f, null);
							width = (float)mysticMap.getWidth(); height = (float)mysticMap.getHeight();
						}
					}
				}
			} else {
				// Display text
				if (!textBold && !textItalic) {
					if (textFont == DEFAULT_FONT) {
						Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL); textPaint.setTypeface(font);
					} else if (textFont == MONOSPACE) {
						Typeface font = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL); textPaint.setTypeface(font);
					} else if (textFont == SANS_SERIF) {
						Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL); textPaint.setTypeface(font);
					}
				} else if (textBold && !textItalic) {
					if (textFont == DEFAULT_FONT) {
						Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textPaint.setTypeface(font);
					} else if (textFont == MONOSPACE) {
						Typeface font = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD); textPaint.setTypeface(font);
					} else if (textFont == SANS_SERIF) {
						Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD); textPaint.setTypeface(font);
					}
				} else if (!textBold && textItalic) {
					if (textFont == DEFAULT_FONT) {
						Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC); textPaint.setTypeface(font);
					} else if (textFont == MONOSPACE) {
						Typeface font = Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC); textPaint.setTypeface(font);
					} else if (textFont == SANS_SERIF) {
						Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC); textPaint.setTypeface(font);
					}
				} else {
					if (textFont == DEFAULT_FONT) {
						Typeface font = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC); textPaint.setTypeface(font);
					} else if (textFont == MONOSPACE) {
						Typeface font = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD_ITALIC); textPaint.setTypeface(font);
					} else if (textFont == SANS_SERIF) {
						Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC); textPaint.setTypeface(font);
					}
				}
				textPaint.setTextSize(textSize);
				textPaint.setTextAlign(Paint.Align.CENTER);
				Rect bounds = new Rect();
				textPaint.getTextBounds(text,0, text.length(), bounds);
				canvas.drawText(text, x, y, textPaint);
				this.width = bounds.width();
				this.height = 2 * bounds.height();
			}
		}

	}

//	// Only used to show invisible shapes in editor
//	public void draw(Canvas canvas) {
//		BitmapDrawable carrotDrawable, carrot2Drawable, deathDrawable, duckDrawable,fireDrawable, mysticDrawable;
//		opaquePaint = new Paint();
//		opaquePaint.setAlpha(50);
//
//		if (!visible && type == IMAGE) {
//
//		}
//	}

	// Move to a certain location (x, y)
	public boolean move(float x, float y) {
		if (movable && visible) {
			this.x = x;
			this.y = y;
			return true;
		} else {
			return false;
		}
	}

	// If more than half of the shape is above the limited height, move the shape up
	public void limitTopHeight(float y) {
		if (this.y + height/2 > y) {
			this.y = y - height / 2;

		}
	}

	// If more than half of the shape is below the limited height, move the shape down
	public void limitBottomHeight(float y) {
		if (this.y + height/2 > y) {
			this.y = y + height / 2;

		}
	}

	// Determine if a point (xval, yval) is inside the shape
	public boolean contains(float xval, float yval) {
		if (visible) {
			if (Math.abs(this.x - xval) <= width/2 && Math.abs(this.y - yval) <= height/2) {
				return true;
			}
		}
		return false;
	}

	// Trigger onClick effects
	public void clicked() {
		if (visible) {
			ArrayList<String> clickAction = script.getActions(ONCLICK, "");
			executeActions(clickAction);
		}
		// Execute actions in the ArrayList
	}

	// Trigger onDrop effects by shape s
	public void droppedBy(Shape s) {
		if (droppableBy(s)) {
			String shapeName = s.getName();
			ArrayList<String> dropAction = script.getActions(ONDROP, shapeName);
			executeActions(dropAction);
		}
	}

	public void entered() {
		if (visible) {
			ArrayList<String> enterAction = script.getActions(ONENTER, "");
			executeActions(enterAction);
		}
	}

	// Execute actions according to the ArrayList of actions
	public void executeActions(ArrayList<String> actionList) {
		MediaPlayer mp;
		for (int i=0; i<actionList.size(); i++) {
			String action = actionList.get(i);
			if (action.equals(GOTO)) {
				String pageName = actionList.get(i + 1);
				Game.gotoPage(pageName);
			} else if (action.equals(PLAY)) {
				String soundName = actionList.get(i + 1);
				if (soundName.equals(CARROT)) {
					mp = MediaPlayer.create(context, R.raw.carrotcarrotcarrot);
					mp.start();
				} else if (soundName.equals(EVIL)) {
					mp = MediaPlayer.create(context, R.raw.evillaugh);
					mp.start();
				} else if (soundName.equals(FIRE)) {
					mp = MediaPlayer.create(context, R.raw.fire);
					mp.start();
				} else if (soundName.equals(HOORAY)) {
					mp = MediaPlayer.create(context, R.raw.hooray);
					mp.start();
				} else if (soundName.equals(MUNCH)) {
					mp = MediaPlayer.create(context, R.raw.munch);
					mp.start();
				} else if (soundName.equals(MUNCHING)) {
					mp = MediaPlayer.create(context, R.raw.munching);
					mp.start();
				} else if (soundName.equals(WOOF)) {
					mp = MediaPlayer.create(context, R.raw.woof);
					mp.start();
				}
			} else if (action.equals(HIDE)) {
				String shapeName = actionList.get(i + 1);
				Game.hideShape(shapeName);
			} else if (action.equals(SHOW)) {
				String shapeName = actionList.get(i + 1);
				Game.showShape(shapeName);
			}
		}
	}

	// Determine if this shape is droppable by another shape s
	// Claimed public for future use
	public boolean droppableBy(Shape s) {
		if (visible && s.isVisible()) {
			String shapeName = s.getName();
			ArrayList<String> dropAction = script.getActions(ONDROP, shapeName);
			if (dropAction.size() != 0) {
				return true;
			}
		}
		return false;
	}

	// Concatenate clause
	public String concatenateClause(String scr) {
	    if (!scr.isEmpty()) {
            String[] splitedScript = scr.split(";");
            ArrayList<String> cList = new ArrayList<String>();
            for (String str: splitedScript) {
                cList.add(str.trim());
            }
            ArrayList<String> clickList = new ArrayList<String>();
            ArrayList<String> enterList = new ArrayList<String>();
            ArrayList<String> dropList = new ArrayList<String>();

            for (String str: cList) {
                if (str.contains(ONCLICK)) {
                    clickList.add(str);
                } else if (str.contains(ONENTER)) {
                    enterList.add(str);
                } else if (str.contains(ONDROP)) {
                    dropList.add(str);
                }
            }

            String onClickClause = "";
			if (clickList.size() > 0) {
				onClickClause = clickList.get(0);
			}
            if (clickList.size() > 1) {
                for (int i=1; i<clickList.size(); i++) {
                    onClickClause += clickList.get(i).replace(ONCLICK, "");
                }
            }
            if (!onClickClause.isEmpty()) {
				onClickClause += "; ";
			}

			String onEnterClause = "";
            if (enterList.size() > 0) {
				onEnterClause = enterList.get(0);
			}
            if (enterList.size() > 1) {
                for (int i=1; i<enterList.size(); i++) {
                    onEnterClause += enterList.get(i).replace(ONENTER, "");
                }
            }
            if (!onEnterClause.isEmpty()) {
            	onEnterClause += "; ";
			}

			String onDropClause = "";
            ArrayList<String> onDropObjectList = new ArrayList<String>();
            Map<String, String> onDropMap = new HashMap<String, String>();
            for (String str: dropList) {
                String[] splittedstr = splitFirstWord(str.replace(ONDROP, "").trim());
                if (!onDropObjectList.contains(splittedstr[0])) {
					onDropObjectList.add(splittedstr[0]);
				}
                if (!onDropMap.containsKey(splittedstr[0])) {
                    onDropMap.put(splittedstr[0], splittedstr[1]);
                } else {
                    String oldStr = onDropMap.get(splittedstr[0]);
                    onDropMap.put(splittedstr[0], oldStr + " " + splittedstr[1]);
                }
            }
            if (dropList.size() > 0) {
                for (String str: onDropObjectList) {
                    String onDropObjectClause = ONDROP + " ";
                    onDropObjectClause += (str + " " + onDropMap.get(str) + "; ");
                    onDropClause += onDropObjectClause;
                }
            }

            return onClickClause + onEnterClause + onDropClause;
        }
		return "";
	}

	public String[] splitFirstWord(String sentence) {
	    int endIndex = 0; String[] result = new String[2];
	    for (int i=0; i<sentence.length(); i++) {
	        if (sentence.charAt(i) == ' ') {
	            endIndex = i;
	            break;
            }
        }
        result[0] = sentence.substring(0, endIndex);
	    result[1] = sentence.substring(endIndex + 1);
	    return result;
    }

	@Override
	public String toString() {
		return name;
	}

	//-------------------------------Error Check--------------------------------------------------
	// Check if the shape name is duplicated in the script
//	public void checkName(String newName, String oldName) {
//		for (Page p: Editor.getPages()) {
//			if (!p.getshapes().isEmpty()) {
//				for (Shape s: p.getshapes()) {
//					String scr = s.getScript();
//					ArrayList<String> splittedScript = splitString(scr);
//					if (splittedScript.size() > 1) {
//						for (int i=0; i<splittedScript.size()-1; i++) {
//							String str = splittedScript.get(i);
//							String nextStr = splittedScript.get(i+1);
//							if ((str.equals("drop") || str.equals("hide") || str.equals("show"))) {
//								splittedScript.set(i+1, newName);
//							}
//						}
//					}
//					String newScript = "";
//					for (String newScr: splittedScript) {
//						newScript += (newScr + " ");
//					}
//					s.setScript(newScript);
//					Log.d("Debug1", newScript);
//				}
//			}
//		}
//	}
//
//	public ArrayList<String> splitString(String str) {
//		ArrayList<String> result = new ArrayList<String>();
//		String[] splittedStr = str.split(";");
//		for (String str1: splittedStr) {
//			str1.trim();
//			String[] str2 = str1.split(" ");
//			for (int i=0; i<str2.length; i++) {
//				if (i == str2.length - 1) {
//					result.add(str2[i].trim() + ";");
//				} else {
//					result.add(str2[i].trim());
//				}
//			}
//		}
//		return result;
//	}

}
