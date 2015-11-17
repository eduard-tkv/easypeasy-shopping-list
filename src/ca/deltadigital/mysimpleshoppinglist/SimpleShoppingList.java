package ca.deltadigital.mysimpleshoppinglist;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
//import android.app.ListActivity;



public class SimpleShoppingList extends ListActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	public final static String EXTRA_MESSAGE = "ca.deltadigital.easypeasy.MESSAGE";
	public final static String TASK_KEY = "deltadigital_easypeasy_key";
	public static final String MyPREFERENCES = "deltadigital_easypeasy_sharedpref" ;
	private static String itemValue;
	
		
	// OnCreate - if there are tasks it lists them otherwise shows enter new task window
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        SharedPreferences pref = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        
        // Use the lines below to clear all tasks when debugging
        /*
        Editor editor = pref.edit();
        editor.remove(TASK_KEY);
        editor.commit();
        */
        
        String retMessage = pref.getString(TASK_KEY, null);
        
        if (retMessage != null)
        {
            setContentView(R.layout.activity_list_view);
            
            setTextViewNewItem();
            
            int lastCharIndex = retMessage.length();
            int count = 0;
            char graveAccent = '`';
            ArrayList<String> tasks = new ArrayList<String>();
            ArrayList<Integer> graveAccentPos = new ArrayList<Integer>();
            for (int i = 0; i < lastCharIndex; i++) {
                if (retMessage.charAt(i) == graveAccent) {
                    graveAccentPos.add(i);
                    count++;
                }
            }

            graveAccentPos.add(lastCharIndex);

            for (int i = 0; i<count; i++)
            {
                tasks.add(retMessage.substring(graveAccentPos.get(i), graveAccentPos.get(i+1)));
            }
            
            String[] retrievedTasks = new String[count];
            
            for (int i=0; i<count; i++)
            {
            	retrievedTasks[i] = tasks.get(i).substring(1);
            }
            
            this.setListAdapter(new ArrayAdapter<String>(
                    this, R.layout.img_txtview,
                    R.id.Itemname,retrievedTasks));
            
            //register for context menu i.e. long press invokes context menu
            registerForContextMenu(getListView());
        }
        // Else display new task window
        else 
        { 
            setContentView(R.layout.new_task); 
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    // Creates a menu on top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return true;
    }

    // MENU ITEM - FOR NOW JUST ONE ITEM I.E. NEW TASK
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        case R.id.new_task:
        newTaskMethod();
        return true;
        default:
        return super.onOptionsItemSelected(item);
        }
    }
    
 
    public void newTaskMethod ()
    {    	
    	setContentView(R.layout.new_task);     
    	//getActionBar().setDisplayShowHomeEnabled(false);
    }
    
    public void newTask (View view)
    {
    	SharedPreferences pref = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
    	String retMessage = pref.getString(TASK_KEY, null);
    	Editor editor = pref.edit();
    	
    	EditText editTextNewTask = (EditText) findViewById(R.id.new_task);

    	String newTask = editTextNewTask.getText().toString();
	
    	String taskToInsert = "";
    	
    	if (newTask != null && !newTask.isEmpty()) 
    	{
    	    boolean foundDupe = false;
             
    	    /*enter as a new task*/
    		if(newTask.contains("`") == true)
    		{
    		    //grave accent is used as a separator so not permited in an entry
    		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		    builder.setMessage("Grave accent ` not permitted. Delete `")
    		    .setCancelable(false)
    		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		        public void onClick(DialogInterface dialog, int id) {
    		                //do things
    		           }
    		       });
    		    AlertDialog alert = builder.create();
    		    alert.show();
    		    
    		}  
    		else
    		{
                int count = 0;
                char graveAccent = '`';
                //ArrayList<String> tasks = new ArrayList<String>();
                ArrayList<Integer> graveAccentPos = new ArrayList<Integer>();
    		    if (retMessage != null && !retMessage.isEmpty())
    		    {
               //find out if its a duplicate entry
                int lastCharIndex = retMessage.length();

                // counting grave accents to calculate how many tasks there are
                for (int i =0; i<lastCharIndex; i++)
                {
                    if (retMessage.charAt(i) == graveAccent)
                    {
                        graveAccentPos.add(i);
                        count++;
                    }
                }
                graveAccentPos.add(lastCharIndex);
                
                 for (int i = 0; i<count; i++)
                 {
                     String localTaskInsert = retMessage.substring(graveAccentPos.get(i), graveAccentPos.get(i+1));
                     if(localTaskInsert.substring(1).equals(newTask))
                     {   
                         foundDupe = true;
                         new AlertDialog.Builder(this)
                         .setTitle("Item already exists!")
                         .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() 
                         {
                             public void onClick(DialogInterface dialog, int which) 
                             { 
                                 //do nothing, will be taken care of with foundDupe
                             }
                         })
                          .show();
                     }
                 }              
                // end duplicate entry search
    		    }
    		    
                if (foundDupe == false)
                {    
                    //if no grave accents found then insert the entry
                    //new item code start
                    taskToInsert = retMessage + "`" + newTask;
                    editor.putString(TASK_KEY, taskToInsert);
                    editor.commit();
                    retMessage = pref.getString(TASK_KEY, null);
                    setContentView(R.layout.activity_list_view);
                    setTextViewNewItem();
                    
                    int lastCharIndex = retMessage.length();
                    lastCharIndex = retMessage.length();
                    count = 0;
                    // char graveAccent = '`';
                    ArrayList<String> tasks = new ArrayList<String>();
                    graveAccentPos.clear();

                    for (int i =0; i<lastCharIndex; i++)
                    {
                        //counting how many grave accents i.e. number of entries
                        if (retMessage.charAt(i) == graveAccent)
                        {
                            graveAccentPos.add(i);
                            count++;
                        }
                    }

                    graveAccentPos.add(lastCharIndex);
    
                    // adding to tasks with grave accent ?
                    for (int i = 0; i<count; i++)
                    {
                        tasks.add(retMessage.substring(graveAccentPos.get(i), graveAccentPos.get(i+1)));
                    }
                    
                    String[] retrievedTasks = new String[count];
                    
                    // adding to retrieved tasks
                    for (int i=0; i<count; i++)
                    {
                    	retrievedTasks[i] = tasks.get(i).substring(1);
                    }
    
                     
                    this.setListAdapter(new ArrayAdapter<String>(
                            this, R.layout.img_txtview,
                            R.id.Itemname,retrievedTasks));
            		
                    registerForContextMenu(getListView()); 
                    //new item code end
                } 
                else {setContentView(R.layout.new_task); }
    		}
    	} 
    	else { newTaskEmptyDialog("Empty Entry!"); }
    }
    
    // Saves edited task
    // public void editTask (View view)
    public void editTask (View view)
    {
    	SharedPreferences pref = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
    	String retMessage = pref.getString(TASK_KEY, null);
    	Editor editor = pref.edit();
    	EditText editTextEditTask = (EditText) findViewById(R.id.edit_task);
    	String editTask = editTextEditTask.getText().toString();

        // the position of last character
        int lastCharIndex = retMessage.length();
        int count = 0;
        char graveAccent = '`';
        ArrayList<String> tasks = new ArrayList<String>();
        ArrayList<Integer> graveAccentPos = new ArrayList<Integer>();

        // counting grave accents to calculate how many tasks there are
        for (int i =0; i<lastCharIndex; i++)
        {
        	if (retMessage.charAt(i) == graveAccent)
        	{
        		graveAccentPos.add(i);
        		count++;
        	}
        }

        graveAccentPos.add(lastCharIndex);
        
    	String taskToInsert = "";

		 for (int i = 0; i<count; i++)
         {
			 String localTaskInsert = retMessage.substring(graveAccentPos.get(i), graveAccentPos.get(i+1));
	 
			 if(localTaskInsert.substring(1).equals(itemValue))
			 {					
				 tasks.add("`"+editTask);
				 taskToInsert = taskToInsert + "`" + editTask;
			 }
			 else
			 {
				 tasks.add(localTaskInsert);
				 taskToInsert = taskToInsert + localTaskInsert;
			 }	
         }
	   			
		// saving entries to sharedpref
   		editor.putString(TASK_KEY, taskToInsert);
    	editor.commit();
	
		// retrieving all tasks again so that we can display them 
		retMessage = pref.getString(TASK_KEY, null);
		
	 	setContentView(R.layout.activity_list_view);


        String[] retrievedTasks = new String[count];
        
        // adding tasks to string array from arraylist
        for (int i=0; i<count; i++)
        {
            retrievedTasks[i] = tasks.get(i).substring(1);
        }


        
        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.img_txtview,
                R.id.Itemname,retrievedTasks));
        setTextViewNewItem();
        registerForContextMenu(getListView());
        //getActionBar().setDisplayShowHomeEnabled(false);
    }//edittask() end
    
    public void deleteTask ()
    {

        SharedPreferences pref = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        String retMessage = pref.getString(TASK_KEY, null);
        Editor editor = pref.edit();

        // the position of last character
        int lastCharIndex = retMessage.length();
        int count = 0;
        char graveAccent = '`';

        //tasks are stored with a grave accent separator i.e.
        // `mytask1`mytask2`mytask3
        ArrayList<String> tasks = new ArrayList<String>();
        ArrayList<Integer> graveAccentPos = new ArrayList<Integer>();

        // counting grave accents to calculate how many tasks there are
        for (int i =0; i<lastCharIndex; i++)
        {
            if (retMessage.charAt(i) == graveAccent)
            {
                graveAccentPos.add(i);
                count++;
            }
        }
        

        graveAccentPos.add(lastCharIndex);
        
        // a simple string that will hold all tasks to be inserted.
        String taskToInsert = "";
    
         for (int i = 0; i<count; i++)
         {
             String localTaskInsert = retMessage.substring(graveAccentPos.get(i), graveAccentPos.get(i+1));

             
             if(!(localTaskInsert.substring(1)).equals(itemValue))
             {   
                 //all other tasks re-inserted unchanged.
                 tasks.add(localTaskInsert);
                 taskToInsert = taskToInsert + localTaskInsert;
             }
             
             /*
             if(localTaskInsert.substring(1).equals(itemValue))
             {   
                 //do nothing
             }
             else
             {
                 //otherwise all other tasks re-inserted unchanged.
                 tasks.add(localTaskInsert);
                 taskToInsert = taskToInsert + localTaskInsert;
             }
               */
         }
                
        // uploading tasks to sharedpref
        editor.putString(TASK_KEY, taskToInsert);
        editor.commit();
    
        // retrieving all tasks again so that we can display them 
        retMessage = pref.getString(TASK_KEY, null);
        
        setContentView(R.layout.activity_list_view);
        setTextViewNewItem();

        
        --count;
        String[] retrievedTasks = new String[count];

        
        for (int i=0; i<count; i++)
        {
            retrievedTasks[i] = tasks.get(i).substring(1);
        }



        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.img_txtview,
                R.id.Itemname,retrievedTasks));
        
        Toast.makeText(getApplicationContext(), "Entry deleted", Toast.LENGTH_LONG).show();
        registerForContextMenu(getListView());
    
    }//edittask() end

    
    public void onCreateContextMenu(ContextMenu contextMenu,View v,ContextMenu.ContextMenuInfo menuInfo) 
    {
        //TextView tv = (TextView) findViewById(R.id.Itemname);
        //String genus = tv.getText().toString();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // saving the pressed entry into global itemValue
        itemValue = ((TextView) info.targetView.findViewById(R.id.Itemname)).getText().toString();
        //itemValue = ((TextView) info.targetView).getText().toString();
        
        //selectedWordId = info.id;

        contextMenu.setHeaderTitle(itemValue);
        contextMenu.add(0, 1, 0, R.string.context_menu_new);
        contextMenu.add(0, 2, 0, R.string.context_menu_edit);
        contextMenu.add(0, 3, 1, R.string.context_menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case 1:
            newTaskMethod ();
            return true;
        case 2:
            setContentView(R.layout.edit_task);
            EditText editText = (EditText)findViewById(R.id.edit_task);
            editText.setText(itemValue, TextView.BufferType.EDITABLE);  
            //Toast.makeText(getApplicationContext(), "Clicked edit", Toast.LENGTH_LONG).show();
            return true;
        case 3:
            deleteDialog(itemValue);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }
    
    
    public void deleteDialog(String headerMsg)
    {
        new AlertDialog.Builder(this)
        .setTitle(headerMsg)
        .setMessage("Delete?")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                deleteTask();
            }
         })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
         .show();
    }

    public void newTaskEmptyDialog(String headerMsg)
    {
        new AlertDialog.Builder(this)
        .setTitle(headerMsg)
        //.setMessage("Empty entry!")
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                //do nothing
            }
         })
         .show();
    }    
    
    
    public void setTextViewNewItem()
    {
        TextView txtFirst = (TextView) findViewById(R.id.newItem);
        txtFirst.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.new_task);
            }
        });
        
    }
    
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TAuto-generated method stub
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // Auto-generated method stub
        /*
        ImageView theStar = (ImageView) findViewById(R.id.icon);
        theStar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);
                Toast.makeText(getApplicationContext(), "item clicked: "+Integer.toString(position), Toast.LENGTH_LONG).show();
            }
           });
          */ 
           
        
    }
    
    /*
    //this can be deleted if click on image doesnt work
    public void clickTheStar()
    {
    ImageView theStar = (ImageView) findViewById(R.id.icon);
    theStar.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
            Toast.makeText(getApplicationContext(), "item clicked: "+Integer.toString(position), Toast.LENGTH_LONG).show();
        }
       });
    }
    */
    
    
    @Override
    public void onClick(View v) {
        // Auto-generated method stub

        
    }

    /*
    @SuppressLint("UseValueOf")
    public View getView(int position, View convertView, ViewGroup parent) {
        
        LayoutInflater layoutInflater=LayoutInflater.from(getApplicationContext());
        //View row=layoutInflater.inflate(R.layout.img_txtview,null,false);
        
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.img_txtview, parent, false);
        }
        

        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        imageView.setTag(new Integer(position));
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Image click row = "+view.getTag().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
    */

    // this is for toggle button 
    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        
        
        
        if (on) {
            // Enable vibrate
            Toast.makeText(getApplicationContext(), "Image click ON = ", Toast.LENGTH_LONG).show();
            
            
        } else {
            // Disable vibrate
            Toast.makeText(getApplicationContext(), "Image click OFF = ", Toast.LENGTH_LONG).show();
            
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
}//end Activity
