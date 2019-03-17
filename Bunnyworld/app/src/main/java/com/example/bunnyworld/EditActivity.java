package com.example.bunnyworld;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.microedition.khronos.egl.EGLDisplay;

import static java.security.AccessController.getContext;

public class EditActivity extends AppCompatActivity{

    private int whichSound = 0;
    private int whichTrigger = 0;
    private int whichScript = 0;
    private String scriptContent;
    private String tempScript;
    private int whichPage = 0;
    private int whichShape = 0;
    private int whichFont = 0;
    //Editor newEditor = new Editor();
    private ArrayList<String> triggers = new ArrayList<String>();
    private ArrayList<String> scripts = new ArrayList<String>();
    private ArrayList<String> sounds = new ArrayList<String>();
    private static final String ONCLICK = "on click";
    private static final String ONENTER = "on enter";
    private static final String ONDROP = "on drop";


    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shape.context = this;

        db = openOrCreateDatabase("GamesDB", MODE_PRIVATE, null);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        /*
        String[] shapeArray = {"Script", "Create Script", "Set Property", "Edit Text",
                "Copy Shape", "Paste Shape", "Delete Shape",};
        Spinner spinner = findViewById(R.id.shapeMenu);
        SpinnerAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shapeArray);
        spinner.setAdapter(adapter);
        */
        triggers.add("on click");
        triggers.add("on enter");
        triggers.add("on drop");
        scripts.add("goto");
        scripts.add("play");
        scripts.add("hide");
        scripts.add("show");
        sounds.add("carrot-eating");
        sounds.add("evil-laugh");
        sounds.add("fire-sound");
        sounds.add("victory");
        sounds.add("munch");
        sounds.add("munching");
        sounds.add("woof");


        String gameName;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                gameName = null;
            } else {
                gameName = extras.getString("STRING_I_NEED");
            }
        } else {
            gameName = (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }

        Log.d("debugdebug", gameName);

        if (gameName.equals("Create a new game")) {
            Log.d("debugdebug", "create a new game");
            Editor.main();
        } else {
            Editor.loadGame(db, gameName);
        }
        //Editor.main();
        //ArrayList<Shape> newShapes = new ArrayList<Shape>();
        //Editor.loadGame(newShapes);
        //Editor.loadGame();
        setTitle(R.string.app_name);
        setTitle("Starting Page: " + Editor.startingPage.getName() + " Current Page: " + Editor.curPage.getName());
        setContentView(R.layout.activity_edit);
    }

    public void scriptMenuPop(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        //popup.setOnMenuItemClickListener(this);//通过按钮打开菜单
        final Shape clickedShape = Editor.getSelectedShape();
        if (clickedShape == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Log.d("Debug1", clickedShape.getName());
            builder.setTitle("Please select a shape first!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dialog.cancel();
                    // 是否自己输入string

                    dialog.cancel();
                    return;
                }

            });
            //builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    switch (item.getItemId()) {
                        case R.id.itemCreate:

                            // Select a trigger
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                            arrayAdapter.add("On Click");

                            arrayAdapter.add("On Enter");

                            arrayAdapter.add("On Drop");

                            builder.setTitle("Select a trigger");
                            builder.setCancelable(true);
                            //final int checkedItem = 0; //this will checked the item when user open the dialog
                            whichTrigger = 0;



                            builder.setSingleChoiceItems(arrayAdapter, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                    // get the trigger
                                    if (which != 0) {
                                        whichTrigger = which;
                                    }

                                }
                            });

                            builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //dialog.cancel();
                                    Shape clickedShape = Editor.getSelectedShape();
                                    //scriptContent = clickedShape.getScript();
                                    tempScript = "";
                                    tempScript += triggers.get(whichTrigger);
                                    //clickedShape.setScript(scriptContent);
                                    //Log.d("sht", Integer.toString(whichTrigger));
                                    if (whichTrigger == 2) {


                                        Log.d("sht", "yinggaixiandani");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                        builder.setTitle("choose a shape");
                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                        ArrayList<Page> pages = Editor.getPages();
                                        final ArrayList<String> shapes = new ArrayList<String>();
                                        for (Page tempPage : pages) {
                                            //arrayAdapter.add(tempPage.getName());
                                            ArrayList<Shape> tempShapes = tempPage.getshapes();
                                            for (Shape tempShape : tempShapes) {
                                                if (!tempShape.getName().equals(clickedShape.getName())) {
                                                    arrayAdapter.add("Page: " + tempPage.getName() + ", Shape: " + tempShape.getName());
                                                    shapes.add(tempShape.getName());
                                                }


                                            }
                                        }
                                        final int checkedItem = 0;
                                        builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                whichShape = which;
                                            }
                                        });

                                        builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //dialog.cancel();
                                                dialog.dismiss();

                                                //Log.d("sht", Boolean.toString(go));
                                                tempScript += " " + shapes.get(whichShape);
                                                // need a function to get the current clicked shape

                                                dialog.cancel();
                                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                Log.d("sht", "buyinggaixiandani");
                                                arrayAdapter.add("goto");

                                                arrayAdapter.add("play");
                                                arrayAdapter.add("hide");
                                                arrayAdapter.add("show");

                                                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                builder.setTitle("Choose Script");
                                                builder.setCancelable(true);
                                                final int checkedItem = 0; //this will checked the item when user open the dialog

                                                builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                        // get the script
                                                        whichScript = which;

                                                    }
                                                });

                                                builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        // goto
                                                        Shape clickedShape = Editor.getSelectedShape();
                                                        scriptContent = clickedShape.getScript();
                                                        //scriptContent = "";
                                                        //scriptContent += triggers.get(whichTrigger);
                                                        scriptContent += tempScript;
                                                        // ondrop needs the shape name here
                                                        scriptContent += " " + scripts.get(whichScript);
                                                        System.out.println(which);
                                                        Log.d("debugbutton", Integer.toString(whichScript));
                                                        if (whichScript == 0) {
                                                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                            final ArrayList<Page> pages = Editor.getPages();
                                                            for (Page temp : pages) {
                                                                arrayAdapter.add(temp.getName());
                                                            }
                                                            //arrayAdapter.add("Create a new game");
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                            builder.setTitle("Choose a page");
                                                            builder.setCancelable(true);
                                                            int checkedItem = 0; //this will checked the item when user open the dialog
                                                            builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                                    whichPage = which;
                                                                }
                                                            });

                                                            builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //dialog.cancel();
                                                                    dialog.dismiss();
                                                                    scriptContent += " " + arrayAdapter.getItem(whichPage) + ";";
                                                                    // need a function to get the current clicked shape
                                                                    Shape clickedShape = Editor.getSelectedShape();
                                                                    if (clickedShape != null) {
                                                                        clickedShape.setScript(scriptContent);
                                                                        scriptContent = "";
                                                                    }
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    scriptContent = "";
                                                                    whichPage = 0;
                                                                    whichScript = 0;
                                                                    whichShape = 0;
                                                                    whichSound = 0;
                                                                    whichTrigger = 0;
                                                                    dialog.cancel();
                                                                }
                                                            });

                                                            builder.show();

                                                        }
                                                        // play sound
                                                        else if (whichScript == 1) {
                                                            final ArrayAdapter<String> soundarrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                            soundarrayAdapter.add("carrot");

                                                            soundarrayAdapter.add("evillaugh");
                                                            soundarrayAdapter.add("fire");
                                                            soundarrayAdapter.add("hooray");
                                                            soundarrayAdapter.add("munch");
                                                            soundarrayAdapter.add("munching");
                                                            soundarrayAdapter.add("woof");
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                            builder.setTitle("Choose a sound");
                                                            builder.setCancelable(true);
                                                            int checkedItem = 0; //this will checked the item when user open the dialog
                                                            builder.setSingleChoiceItems(soundarrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + soundarrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                                    MediaPlayer mp;
                                                                    if (which == 0) {
                                                                        mp = MediaPlayer.create(EditActivity.this, R.raw.carrotcarrotcarrot);
                                                                    } else if (which == 1) {
                                                                        mp = MediaPlayer.create(EditActivity.this, R.raw.evillaugh);

                                                                    } else if (which == 2) {
                                                                        mp = MediaPlayer.create(EditActivity.this, R.raw.fire);

                                                                    } else if (which == 3) {
                                                                        mp = MediaPlayer.create(EditActivity.this, R.raw.hooray);

                                                                    } else if (which == 4) {
                                                                        mp = MediaPlayer.create(EditActivity.this, R.raw.munch);

                                                                    } else if (which == 5) {
                                                                        mp = MediaPlayer.create(EditActivity.this, R.raw.munching);

                                                                    } else {
                                                                        mp = MediaPlayer.create(EditActivity.this, R.raw.woof);
                                                                    }
                                                                    whichSound = which;
                                                                    mp.start();
                                                                }
                                                            });

                                                            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //dialog.cancel();
                                                                    scriptContent += " " + sounds.get(whichSound) + ";";
                                                                    // need a function to get the current clicked shape
                                                                    Shape clickedShape = Editor.getSelectedShape();
                                                                    if (clickedShape != null) {
                                                                        clickedShape.setScript(scriptContent);
                                                                        scriptContent = "";
                                                                        whichPage = 0;
                                                                        whichScript = 0;
                                                                        whichShape = 0;
                                                                        whichSound = 0;
                                                                        whichTrigger = 0;
                                                                    }

                                                                    dialog.dismiss();

                                                                }

                                                            });

                                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                    whichPage = 0;
                                                                    whichScript = 0;
                                                                    whichShape = 0;
                                                                    whichSound = 0;
                                                                    whichTrigger = 0;
                                                                }
                                                            });

                                                            builder.show();
                                                        }
                                                        // hide & show

                                                        else if (whichScript == 2) {
                                                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                            final ArrayList<Page> pages = Editor.getPages();
                                                            final ArrayList<String> shapeList = new ArrayList<String>();
                                                            for (Page temp : pages) {
                                                                for (Shape tempShape : temp.getshapes()) {
                                                                    arrayAdapter.add("Page: " + temp.getName() + ", Shape: " + tempShape.getName());
                                                                    shapeList.add(tempShape.getName());
                                                                }
                                                            }

                                                            //arrayAdapter.add("Create a new game");
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                            builder.setTitle("Choose a shape");
                                                            builder.setCancelable(true);
                                                            int checkedItem = 0; //this will checked the item when user open the dialog

                                                            builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                                    whichShape = which;
                                                                }
                                                            });

                                                            builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //dialog.cancel();

                                                                    scriptContent += " " + shapeList.get(whichShape) + ";";
                                                                    // need a function to get the current clicked shape
                                                                    Shape clickedShape = Editor.getSelectedShape();
                                                                    if (clickedShape != null) {
                                                                        clickedShape.setScript(scriptContent);
                                                                        scriptContent = "";
                                                                        whichPage = 0;
                                                                        whichScript = 0;
                                                                        whichShape = 0;
                                                                        whichSound = 0;
                                                                        whichTrigger = 0;
                                                                    }
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    scriptContent = "";
                                                                    whichPage = 0;
                                                                    whichScript = 0;
                                                                    whichShape = 0;
                                                                    whichSound = 0;
                                                                    whichTrigger = 0;
                                                                    dialog.cancel();
                                                                }
                                                            });

                                                            builder.show();

                                                        } else if (whichScript == 3){
                                                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                            final ArrayList<Page> pages = Editor.getPages();
                                                            final ArrayList<String> shapeList1 = new ArrayList<String>();
                                                            for (Page temp : pages) {
                                                                for (Shape tempShape : temp.getshapes()) {
                                                                    shapeList1.add(tempShape.getName());
                                                                    arrayAdapter.add("Page: " + temp.getName() + ", Shape: " + tempShape.getName());
                                                                }
                                                            }

                                                            //arrayAdapter.add("Create a new game");
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                            builder.setTitle("Choose a shape");
                                                            builder.setCancelable(true);
                                                            int checkedItem = 0; //this will checked the item when user open the dialog
                                                            builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                                    whichShape = which;
                                                                }
                                                            });

                                                            builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //dialog.cancel();

                                                                    scriptContent += " " + shapeList1.get(whichShape) + ";";

                                                                    // need a function to get the current clicked shape
                                                                    Shape clickedShape = Editor.getSelectedShape();
                                                                    if (clickedShape != null) {
                                                                        clickedShape.setScript(scriptContent);
                                                                        whichPage = 0;
                                                                        whichScript = 0;
                                                                        whichShape = 0;
                                                                        whichSound = 0;
                                                                        whichTrigger = 0;
                                                                        scriptContent = "";
                                                                    }
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    scriptContent = "";
                                                                    whichPage = 0;
                                                                    whichScript = 0;
                                                                    whichShape = 0;
                                                                    whichSound = 0;
                                                                    whichTrigger = 0;
                                                                    dialog.cancel();
                                                                }
                                                            });

                                                            builder.show();


                                                        }
                                                    }

                                                });

                                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                        whichPage = 0;
                                                        whichScript = 0;
                                                        whichShape = 0;
                                                        whichSound = 0;
                                                        whichTrigger = 0;
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                whichPage = 0;
                                                whichScript = 0;
                                                whichShape = 0;
                                                whichSound = 0;
                                                whichTrigger = 0;
                                            }
                                        });

                                        builder.show();
                                    }





                                   // Log.d("sht", Boolean.toString(go));
                                    //dialog.dismiss();
                                    else {
                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                        Log.d("sht", "buyinggaixiandani");
                                        arrayAdapter.add("goto");

                                        arrayAdapter.add("play");
                                        arrayAdapter.add("hide");
                                        arrayAdapter.add("show");

                                        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                        builder.setTitle("Choose Script");
                                        builder.setCancelable(true);
                                        final int checkedItem = 0; //this will checked the item when user open the dialog

                                        builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                // get the script
                                                whichScript = which;

                                            }
                                        });

                                        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // goto
                                                Shape clickedShape = Editor.getSelectedShape();
                                                scriptContent = clickedShape.getScript();
                                                //scriptContent = "";
                                                //scriptContent += triggers.get(whichTrigger);
                                                scriptContent += tempScript;
                                                // ondrop needs the shape name here
                                                scriptContent += " " + scripts.get(whichScript);
                                                System.out.println(which);
                                                if (whichScript == 0) {
                                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                    final ArrayList<Page> pages = Editor.getPages();
                                                    for (Page temp : pages) {
                                                        arrayAdapter.add(temp.getName());
                                                    }
                                                    //arrayAdapter.add("Create a new game");
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                    builder.setTitle("Choose a page");
                                                    builder.setCancelable(true);
                                                    whichPage = 0;
                                                    int checkedItem = 0; //this will checked the item when user open the dialog
                                                    builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                            whichPage = which;
                                                        }
                                                    });

                                                    builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //dialog.cancel();
                                                            dialog.dismiss();
                                                            scriptContent += " " + arrayAdapter.getItem(whichPage) + ";";
                                                            // need a function to get the current clicked shape
                                                            Shape clickedShape = Editor.getSelectedShape();
                                                            if (clickedShape != null) {
                                                                clickedShape.setScript(scriptContent);
                                                                scriptContent = "";
                                                            }
                                                            dialog.cancel();
                                                        }
                                                    });
                                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            scriptContent = "";
                                                            whichPage = 0;
                                                            whichScript = 0;
                                                            whichShape = 0;
                                                            whichSound = 0;
                                                            whichTrigger = 0;
                                                            dialog.cancel();
                                                        }
                                                    });

                                                    builder.show();

                                                }
                                                // play sound
                                                else if (whichScript == 1) {
                                                    final ArrayAdapter<String> soundarrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                    soundarrayAdapter.add("carrot");

                                                    soundarrayAdapter.add("evillaugh");
                                                    soundarrayAdapter.add("fire");
                                                    soundarrayAdapter.add("hooray");
                                                    soundarrayAdapter.add("munch");
                                                    soundarrayAdapter.add("munching");
                                                    soundarrayAdapter.add("woof");
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                    builder.setTitle("Choose a sound");
                                                    builder.setCancelable(true);
                                                    int checkedItem = 0; //this will checked the item when user open the dialog
                                                    builder.setSingleChoiceItems(soundarrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + soundarrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                            MediaPlayer mp;
                                                            if (which == 0) {
                                                                mp = MediaPlayer.create(EditActivity.this, R.raw.carrotcarrotcarrot);
                                                            } else if (which == 1) {
                                                                mp = MediaPlayer.create(EditActivity.this, R.raw.evillaugh);

                                                            } else if (which == 2) {
                                                                mp = MediaPlayer.create(EditActivity.this, R.raw.fire);

                                                            } else if (which == 3) {
                                                                mp = MediaPlayer.create(EditActivity.this, R.raw.hooray);

                                                            } else if (which == 4) {
                                                                mp = MediaPlayer.create(EditActivity.this, R.raw.munch);

                                                            } else if (which == 5) {
                                                                mp = MediaPlayer.create(EditActivity.this, R.raw.munching);

                                                            } else {
                                                                mp = MediaPlayer.create(EditActivity.this, R.raw.woof);
                                                            }
                                                            whichSound = which;
                                                            mp.start();
                                                        }
                                                    });

                                                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //dialog.cancel();
                                                            scriptContent += " " + sounds.get(whichSound) + ";";
                                                            // need a function to get the current clicked shape
                                                            Shape clickedShape = Editor.getSelectedShape();
                                                            if (clickedShape != null) {
                                                                clickedShape.setScript(scriptContent);
                                                                whichPage = 0;
                                                                whichScript = 0;
                                                                whichShape = 0;
                                                                whichSound = 0;
                                                                whichTrigger = 0;
                                                                scriptContent = "";
                                                            }

                                                            dialog.dismiss();

                                                        }

                                                    });

                                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                            whichPage = 0;
                                                            whichScript = 0;
                                                            whichShape = 0;
                                                            whichSound = 0;
                                                            whichTrigger = 0;
                                                        }
                                                    });

                                                    builder.show();
                                                }
                                                // hide & show

                                                else if (whichScript == 2) {
                                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                    final ArrayList<Page> pages = Editor.getPages();
                                                    final ArrayList<String> shapeList = new ArrayList<String>();
                                                    for (Page temp : pages) {
                                                        for (Shape tempShape : temp.getshapes()) {
                                                            arrayAdapter.add("Page: " + temp.getName() + ", Shape: " + tempShape.getName());
                                                            shapeList.add(tempShape.getName());
                                                        }
                                                    }

                                                    //arrayAdapter.add("Create a new game");
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                    builder.setTitle("Choose a shape");
                                                    builder.setCancelable(true);
                                                    int checkedItem = 0; //this will checked the item when user open the dialog
                                                    builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                            whichShape = which;
                                                        }
                                                    });

                                                    builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //dialog.cancel();
                                                            dialog.dismiss();
                                                            scriptContent += " " + shapeList.get(whichShape) + ";";
                                                            Log.d("debug2debug", scriptContent.toString());
                                                            // need a function to get the current clicked shape
                                                            Shape clickedShape = Editor.getSelectedShape();
                                                            if (clickedShape != null) {
                                                                clickedShape.setScript(scriptContent);
                                                                scriptContent = "";
                                                                whichPage = 0;
                                                                whichScript = 0;
                                                                whichShape = 0;
                                                                whichSound = 0;
                                                                whichTrigger = 0;
                                                            }
                                                            dialog.cancel();
                                                        }
                                                    });
                                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            scriptContent = "";
                                                            whichPage = 0;
                                                            whichScript = 0;
                                                            whichShape = 0;
                                                            whichSound = 0;
                                                            whichTrigger = 0;
                                                            dialog.cancel();
                                                        }
                                                    });

                                                    builder.show();

                                                } else {
                                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                                                    final ArrayList<Page> pages = Editor.getPages();
                                                    final ArrayList<String> shapeList1 = new ArrayList<String>();
                                                    for (Page temp : pages) {
                                                        for (Shape tempShape : temp.getshapes()) {
                                                            arrayAdapter.add("Page: " + temp.getName() + ", Shape: " + tempShape.getName());
                                                            shapeList1.add(tempShape.getName());
                                                        }
                                                    }

                                                    //arrayAdapter.add("Create a new game");
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                                    builder.setTitle("Choose a shape");
                                                    builder.setCancelable(true);
                                                    int checkedItem = 0; //this will checked the item when user open the dialog
                                                    builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                                            whichShape = which;
                                                        }
                                                    });

                                                    builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //dialog.cancel();
                                                            dialog.dismiss();
                                                            scriptContent += " " + shapeList1.get(whichShape) + ";";
                                                            // need a function to get the current clicked shape
                                                            Shape clickedShape = Editor.getSelectedShape();
                                                            if (clickedShape != null) {
                                                                clickedShape.setScript(scriptContent);
                                                                scriptContent = "";
                                                                whichPage = 0;
                                                                whichScript = 0;
                                                                whichShape = 0;
                                                                whichSound = 0;
                                                                whichTrigger = 0;
                                                            }
                                                            dialog.cancel();
                                                        }
                                                    });
                                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            scriptContent = "";
                                                            whichPage = 0;
                                                            whichScript = 0;
                                                            whichShape = 0;
                                                            whichSound = 0;
                                                            whichTrigger = 0;
                                                            dialog.cancel();
                                                        }
                                                    });

                                                    builder.show();


                                                }
                                            }

                                        });

                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                whichPage = 0;
                                                whichScript = 0;
                                                whichShape = 0;
                                                whichSound = 0;
                                                whichTrigger = 0;
                                            }
                                        });
                                        builder.show();
                                    }



                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    whichPage = 0;
                                    whichScript = 0;
                                    whichShape = 0;
                                    whichSound = 0;
                                    whichTrigger = 0;
                                }
                            });
                            builder.show();
                            return true;

                        // show script here.
                        case R.id.itemShow:

                            builder.setTitle("Here is the script: ");

                            final EditText editText = new EditText(EditActivity.this);
                            Shape clickedShape = Editor.getSelectedShape();
                            editText.setText(clickedShape.getScript());
                            builder.setView(editText);

                            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //dialog.cancel();
                                    // 是否自己输入string
                                    Shape clickedShape = Editor.getSelectedShape();
                                    if (clickedShape != null) {
                                        clickedShape.setScript(editText.getText().toString().toLowerCase());
                                    }

                                    dialog.cancel();
                                }

                            });
                            builder.setNegativeButton("Cancel", null);

                            AlertDialog dialog = builder.create();
                            dialog.show();


                            return true;
                        default:
                            return false;
                    }
                }
            });

            popup.inflate(R.menu.popup_menu_script);
            popup.show();
        }
    }



    public void shapeMenuPop(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        //popup.setOnMenuItemClickListener(this);//通过按钮打开菜单
        /*
        Shape clickedShape = Editor.getSelectedShape();
        if (clickedShape == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please select a shape first!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dialog.cancel();
                    // 是否自己输入string

                    dialog.cancel();
                    return;
                }

            });
            //builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
        */


            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Shape clickedShape = Editor.getSelectedShape();
                    switch (item.getItemId()) {
                        case R.id.itemSet:
                        /*
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                        builder.setTitle("Set Property: ");

                        final EditText editText = new EditText(EditActivity.this);

                        builder.setView(editText);



                        LinearLayout layout = new LinearLayout(EditActivity.this);
                        layout.setOrientation(LinearLayout.HORIZONTAL);

                        // Add a TextView here for the "Title" label, as noted in the comments
                        final EditText titleBox = new EditText(EditActivity.this);
                        titleBox.setHint("Left");
                        layout.addView(titleBox); // Notice this is an add method

                        // Add another TextView here for the "Description" label
                        final EditText descriptionBox = new EditText(EditActivity.this);
                        descriptionBox.setHint("Top ");
                        layout.addView(descriptionBox); // Another add method

                        builder.setView(layout);

                        builder.setPositiveButton("Done", null);
                        builder.setNegativeButton("Cancel", null);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        */

                            if (clickedShape == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                builder.setTitle("Please select a shape first!");

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dialog.cancel();
                                        // 是否自己输入string

                                        dialog.cancel();
                                        return;
                                    }

                                });
                                //builder.setNegativeButton("Cancel", null);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                //move this part to the right place
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                builder.setTitle("Properties: ");

                                //LayoutInflater inflater = this.getLayoutInflater();
                                LayoutInflater inflater = LayoutInflater.from(EditActivity.this);
                                View dialogView = inflater.inflate(R.layout.properties, null);
                                builder.setView(dialogView);
                                Shape selectedShape = Editor.getSelectedShape();
                                final EditText editTextX = dialogView.findViewById(R.id.editX);
                                editTextX.setText(Float.toString(selectedShape.getX()));
//                        builder.setView(editTextX);
                                Log.d("debug1", editTextX.getText().toString());
                                final EditText editTextY = dialogView.findViewById(R.id.editY);
                                editTextY.setText(Float.toString(selectedShape.getY()));

                                final EditText editTextWidth = dialogView.findViewById(R.id.editWidth);
                                editTextWidth.setText(Float.toString(selectedShape.getWidth()));

                                final EditText editTextHeight = dialogView.findViewById(R.id.editHeight);
                                editTextHeight.setText(Float.toString(selectedShape.getHeight()));

                                final Switch moveSwitch = dialogView.findViewById(R.id.movable);
                                moveSwitch.setChecked(selectedShape.isMovable());
                                final Switch hideSwitch = dialogView.findViewById(R.id.visible);
                                moveSwitch.setChecked(selectedShape.isVisible());

                                final Switch boundSwitch = dialogView.findViewById(R.id.useImageBounds);
                                boundSwitch.setChecked(selectedShape.getImageBounds());
                                final Switch scalingSwitch = dialogView.findViewById(R.id.proportionalScaling);
                                scalingSwitch.setChecked(selectedShape.getPS());

                                final EditText editTextName = dialogView.findViewById(R.id.currentShape);
                                editTextName.setText(selectedShape.getName());

                                TextView textView = dialogView.findViewById(R.id.currentImage);
                                textView.setText(clickedShape.getImageName());

                                final EditText editTextFont = dialogView.findViewById(R.id.fontsize);
                                editTextFont.setText(Float.toString(selectedShape.getTextSize()));


                                //final EditText editTextImage = dialogView.findViewById(R.id.imageName);
                                //editTextImage.setText(selectedShape.getImageName());
//set the current state of a Switch

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dialog.cancel();


                                        Shape selectedShape = Editor.getSelectedShape();
                                        Shape newShape = new Shape(selectedShape);
                                        newShape.setID(selectedShape.getID());
                                        newShape.setName(selectedShape.getName());
                                        Editor.shapeBackup.push(newShape);

                                        selectedShape.setUseImageBounds(boundSwitch.isChecked());
                                        selectedShape.setProportionalScaling(scalingSwitch.isChecked());


                                        selectedShape.setX(Float.parseFloat(editTextX.getText().toString()));
                                        selectedShape.setY(Float.parseFloat(editTextY.getText().toString()));
                                        selectedShape.setWidth(Float.parseFloat(editTextWidth.getText().toString()));
                                        selectedShape.setHeight(Float.parseFloat(editTextHeight.getText().toString()));
                                        selectedShape.setMovable(moveSwitch.isChecked());
                                        Log.d("debug1", Boolean.toString(moveSwitch.isChecked()));
                                        selectedShape.setVisible(hideSwitch.isChecked());
                                        selectedShape.setName(editTextName.getText().toString());
                                        //selectedShape.setImageName(editTextImage.getText().toString());
                                        selectedShape.setName(editTextName.getText().toString());
                                        selectedShape.setTextSize(Float.parseFloat(editTextFont.getText().toString()));



                                        // 需要一个namelist of pages
                                        //newEditor.gotoPage(editTextLeft.toString());

                                        setContentView(R.layout.activity_edit);
                                        dialog.cancel();
                                    }

                                });
                                builder.setNegativeButton("Cancel", null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            return true;

                        case R.id.itemCopy:

                            if (clickedShape == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                builder.setTitle("Please select a shape first!");

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dialog.cancel();
                                        // 是否自己输入string

                                        dialog.cancel();
                                        return;
                                    }

                                });
                                //builder.setNegativeButton("Cancel", null);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                Editor.copyShape();
                            }
                            return true;
                        case R.id.itemDelete:
                            if (clickedShape == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                builder.setTitle("Please select a shape first!");

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dialog.cancel();
                                        // 是否自己输入string

                                        dialog.cancel();
                                        return;
                                    }

                                });
                                //builder.setNegativeButton("Cancel", null);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                clickedShape = Editor.getSelectedShape();
                                //clickedShape = Editor.getSelectedShape();
                                Editor.noSelect();
                                Editor.curPage.removeShape(clickedShape);
                                setContentView(R.layout.activity_edit);
                            }
                            return true;

                        case R.id.undoShape:
                            Toast.makeText(EditActivity.this, Editor.undoShape(), Toast.LENGTH_LONG).show();

                        case R.id.itemPaste:
                            Editor.pasteShape();
                            setContentView(R.layout.activity_edit);
                            return true;
                        case R.id.itemCut:
                            if (clickedShape == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                builder.setTitle("Please select a shape first!");

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dialog.cancel();
                                        // 是否自己输入string

                                        dialog.cancel();
                                        return;
                                    }

                                });
                                //builder.setNegativeButton("Cancel", null);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                Editor.cutShape();
                                setContentView(R.layout.activity_edit);
                            }
                            return true;
                        case R.id.itemText:
                            if (clickedShape == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                builder.setTitle("Please select a shape first!");

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dialog.cancel();
                                        // 是否自己输入string

                                        dialog.cancel();
                                        return;
                                    }

                                });
                                //builder.setNegativeButton("Cancel", null);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                //final Shape clickedShapeText = Editor.getSelectedShape();
                                //clickedShapeText = Editor.getSelectedShape();
                                final Shape text = Editor.getSelectedShape();
                                if (text.getType() == 3) {

                                    AlertDialog.Builder builderText = new AlertDialog.Builder(EditActivity.this);
                                    builderText.setTitle("Enter the name of the text: ");

                                    LayoutInflater inflater2 = LayoutInflater.from(EditActivity.this);
                                    final View dialogView2 = inflater2.inflate(R.layout.edit_text, null);


                                    final EditText newEditText = dialogView2.findViewById(R.id.textName);

                                    newEditText.setText(text.getText());
                                    //text.setText(newEditText.getText().toString());
                                    builderText.setView(newEditText);


                                    builderText.setView(dialogView2);


                                    final RadioGroup fontGroup = dialogView2.findViewById(R.id.font_group);

                                    if(text.getTextFont() == 0){
                                        fontGroup.check(R.id.defaultFont);
                                    }

                                    if(text.getTextFont() == 1){
                                        fontGroup.check(R.id.monospace);
                                    }

                                    if(text.getTextFont() == 2){
                                        fontGroup.check(R.id.sans);
                                    }

                                    final Switch boldSwitch = dialogView2.findViewById(R.id.bold);
                                    boldSwitch.setChecked(text.getTextBold() == 1);
                                    final Switch italicSwitch = dialogView2.findViewById(R.id.italic);
                                    italicSwitch.setChecked(text.getTextItalic() == 1);

                                    builderText.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //dialog.cancel();text.getTextBold()
                                            int buttonId = fontGroup.getCheckedRadioButtonId();
                                            whichFont = buttonId;
                                            text.setText(newEditText.getText().toString());

                                            //boldSwitch.setChecked(text.getTextBold() == 1);
                                            //italicSwitch.setChecked(text.getTextItalic() == 1);

                                            if (buttonId == R.id.defaultFont) {
                                                text.setTextFont(Shape.DEFAULT_FONT);

                                            }
                                            if (buttonId == R.id.monospace) {
                                                text.setTextFont(Shape.MONOSPACE);
                                            }
                                            if (buttonId == R.id.sans) {
                                                text.setTextFont(Shape.SANS_SERIF);
                                            }


                                            text.setTextBold(boldSwitch.isChecked());

                                            text.setTextItalic(italicSwitch.isChecked());



                                            setContentView(R.layout.activity_edit);
                                            dialog.cancel();

                                        }

                                    });
                                    builderText.setNegativeButton("Cancel", null);

                                    AlertDialog dialogText = builderText.create();
                                    dialogText.show();
                                    // setContentView(R.layout.activity_edit);

                                }


                            }
                            return true;
                        default:
                            return false;
                    }
                }
            });

            popup.inflate(R.menu.popup_menu_shape);
            popup.show();

    }


public void pageMenuPop(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        //popup.setOnMenuItemClickListener(this);//通过按钮打开菜单

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
                ArrayList<Page> pages = Editor.getPages();
                for (Page tempPage : pages) {
                    arrayAdapter.add(tempPage.getName());
                }
                final int checkedItem = pages.indexOf(Editor.curPage);
                switch (item.getItemId()) {
                    case R.id.createPage:
                        //AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                        builder.setTitle("Enter the name of the new page: ");

                        final EditText editTextCreate = new EditText(EditActivity.this);
                        builder.setView(editTextCreate);


                        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.cancel();
                                // 是否自己输入string
                                Editor.addPage(editTextCreate.getText().toString().toLowerCase());

                                dialog.cancel();
                            }

                        });
                        builder.setNegativeButton("Cancel", null);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;

                    case R.id.renamePage:
                        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Enter the new name of the page: ");

                        final EditText editTextRename = new EditText(EditActivity.this);
                        builder.setView(editTextRename);

                        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.cancel();
                                // 是否自己输入string
                                Editor.setPageName(Editor.curPage.getName(), editTextRename.getText().toString().toLowerCase());
                                setTitle("Starting Page: " + Editor.startingPage.getName() + " Current Page: " + Editor.curPage.getName());
                                setContentView(R.layout.activity_edit);
                                dialog.cancel();

                            }

                        });
                        builder.setNegativeButton("Cancel", null);

                        AlertDialog dialogRename = builder.create();
                        dialogRename.show();

                        return true;
                    case R.id.deletePage:
                        builder.setTitle("Choose the page you want to delete: ");



//                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
//                        ArrayList<Page> pages = Editor.getPages();
//                        for (Page tempPage : pages) {
//                            arrayAdapter.add(tempPage.getName());
//                        }


//                        final int checkedItem = pages.indexOf(Editor.curPage)
                        builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                // get the script
                                whichPage = which;

                            }
                        });
                        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.cancel();

                                // 需要一个namelist of pages
                                dialog.dismiss();
                                Log.d("debug1", Editor.curPage.getName());
                                Log.d("debug1", arrayAdapter.getItem(whichPage));
                                if (Editor.startingPage.getName() == arrayAdapter.getItem(whichPage)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                                    builder.setTitle("Cannot delete the starting page!");



                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //dialog.cancel();
                                            // 是否自己输入string
                                            //Editor.setPageName(Editor.curPage.getName(), editText.getText().toString());
                                            dialog.cancel();
                                        }

                                    });


                                    AlertDialog dialogDelete = builder.create();
                                    dialogDelete.show();
                                } else {
                                    Editor.deletePage(arrayAdapter.getItem(whichPage));
                                    //setTitle(Editor.curPage.getName());
                                    setContentView(R.layout.activity_edit);
                                    dialog.cancel();
                                }


                            }

                        });
                        builder.setNegativeButton("Cancel", null);

                        AlertDialog dialogDelete = builder.create();
                        dialogDelete.show();
                        return true;

                    case R.id.transferPage:
                        builder.setTitle("Choose the page you want to transfer to: ");


//                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.select_dialog_singlechoice);
//                        ArrayList<Page> pages = Editor.getPages();
//                        for (Page tempPage : pages) {
//                            arrayAdapter.add(tempPage.getName())
//;                        }


//                        final int checkedItem = pages.indexOf(Editor.curPage);
                        builder.setSingleChoiceItems(arrayAdapter, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(EditActivity.this, "Position: " + which + " Value: " + arrayAdapter.getItem(which), Toast.LENGTH_LONG).show();
                                // get the script
                                whichPage = which;

                            }
                        });
                        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.cancel();

                                // 需要一个namelist of pages
                                dialog.dismiss();
                                Log.d("debug1", Editor.curPage.getName());
                                Log.d("debug1", arrayAdapter.getItem(whichPage));
                                Editor.gotoPage(arrayAdapter.getItem(whichPage));
                                setTitle("Starting Page: " + Editor.startingPage.getName() + " Current Page: " + Editor.curPage.getName());
                                setContentView(R.layout.activity_edit);
                                dialog.cancel();

                            }

                        });
                        builder.setNegativeButton("Cancel", null);

                        AlertDialog dialogTransfer = builder.create();
                        dialogTransfer.show();
                        return true;
                    case R.id.setPage:
                        Editor.startingPage = Editor.curPage;
                    case R.id.undoPage:
                        Toast.makeText(EditActivity.this, Editor.undoPage(), Toast.LENGTH_LONG).show();


                    default:
                        return false;
                }
            }
        });

        popup.inflate(R.menu.popup_menu_page);
        popup.show();
    }


/*
    public void createPage(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the name of the new page: ");

        final EditText editText = new EditText(this);
        builder.setView(editText);


        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
                // 是否自己输入string
                Editor.addPage(editText.getText().toString());
                
                dialog.cancel();
            }

        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void transferPage(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the name of the page you want to transfer to: ");

        final EditText editText = new EditText(this);
        builder.setView(editText);

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();

                // 需要一个namelist of pages
                Editor.gotoPage(editText.getText().toString());
                dialog.cancel();
            }

        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deletePage(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the name of the page you want to delete: ");

        final EditText editText = new EditText(this);
        builder.setView(editText);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();

                Editor.deletePage(editText.getText().toString());
                dialog.cancel();
            }

        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void renamePage(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the new name of the page: ");

        final EditText editText = new EditText(this);
        builder.setView(editText);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
                // 是否自己输入string
                Editor.setPageName(Editor.curPage.getName(), editText.getText().toString());
                dialog.cancel();
            }

        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
*/
    public void saveGame(View view) {

        db = openOrCreateDatabase("GamesDB", MODE_PRIVATE, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save the game ?");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Editor.saveGame(db);
                dialog.cancel();
                Toast.makeText(EditActivity.this, Editor.gameName + " is saved!", Toast.LENGTH_LONG).show();
            }

        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
