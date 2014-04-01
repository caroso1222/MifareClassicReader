package com.example.mifareclassicreader;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends BaseAdapter{

	
	 /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    private static LayoutInflater inflaterFailure=null;
    private Typeface dosisFont;
    private Typeface droidFont;
    public Resources res;
    Sector tempValues=null;
    int i=0;
    
    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(Activity a, ArrayList d,Resources resLocal) {
         
           /********** Take passed values **********/
            activity = a;
            data=d;
            res = resLocal;
            
            droidFont = Typeface.createFromAsset(activity.getAssets(), "fonts/DroidSansMono.ttf");
            dosisFont = Typeface.createFromAsset(activity.getAssets(), "fonts/Dosis-Medium.ttf");
        
            /***********  Layout inflator to call external xml layout () ***********/
             inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             //inflaterFailure = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
    }
    
    /******** What is the size of Passed Arraylist Size ************/
    @Override
	public int getCount() {
         
        if(data.size()<=0)
            return 1;
        return data.size();
    }
    
    @Override
	public Object getItem(int position) {
        return position;
    }
    
    @Override
	public long getItemId(int position) {
        return position;
    }
    
    /****** Depends upon data size called for each row , Create each ListView row *****/
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        
        View vi = convertView;
         
        if(convertView==null){
            /****** View Holder Object to contain tabitem.xml file elements ******/
            vi = inflater.inflate(R.layout.sector_fila, null);
        }
         
        if(data.size()>2)
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( Sector ) data.get( position );
            if(tempValues.isAutenticado()){
            	vi = inflater.inflate(R.layout.sector_fila, null);
            	 
            	 
            	 /****** Inflate tabitem.xml file for each row ( Defined below ) *******/

            /************  Set Model values in Holder elements ***********/

            	TextView eltxt = (TextView) vi.findViewById(R.id.textSector);
            
            	eltxt.setTextColor(Color.WHITE);                 
            	eltxt.setTextColor(Color.DKGRAY);
            	eltxt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            	Float alpha = 0.15f;
	           	//holder.textSector.setAlpha(alpha);
	            eltxt.setTextSize(16);
	            eltxt.setTypeface(dosisFont);
	            eltxt.setText("Sector " + tempValues.getNumero());
	            
	            arreglarTextviewBlock((TextView) vi.findViewById(R.id.textB0));
	            arreglarTextviewBlock((TextView) vi.findViewById(R.id.textB1));
	            arreglarTextviewBlock((TextView) vi.findViewById(R.id.textB2));
	            arreglarTextviewBlock((TextView) vi.findViewById(R.id.textB3));
	            
	            arreglarTextviewValue((TextView) vi.findViewById(R.id.textB0Value),  tempValues.getBloque0());
	            arreglarTextviewValue((TextView) vi.findViewById(R.id.textB1Value),  tempValues.getBloque1());
	            arreglarTextviewValue((TextView) vi.findViewById(R.id.textB2Value),  tempValues.getBloque2());
	            arreglarTextviewValue((TextView) vi.findViewById(R.id.textB3Value),  tempValues.getBloque3());
             }else{
                 vi = inflater.inflate(R.layout.sector_fila_failure, null);
                 TextView elTextview = (TextView) vi.findViewById(R.id.textSect);
            	 
               	//holder.textSector.setTextColor(Color.WHITE);
            	 elTextview.setTextColor(Color.DKGRAY);
            	 elTextview.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                 Float alpha = 0.15f;
                	//holder.textSector.setAlpha(alpha);
                 elTextview.setTextSize(16);
                 elTextview.setTypeface(dosisFont);
                 elTextview.setText("Sector " + tempValues.getNumero());
                 
            	 
            	 elTextview = (TextView) vi.findViewById(R.id.textFailure);
            	 elTextview.setTextColor(Color.WHITE);
             	 elTextview.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
             	 alpha = 0.8f;
             	 elTextview.setAlpha(alpha);
             	 elTextview.setTextSize(14);
             	 elTextview.setTypeface(droidFont);
             }
        }        
        return vi;
    }
    
    private void arreglarTextviewBlock(TextView elTextview){
    	elTextview.setTextColor(Color.WHITE);
    	elTextview.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
    	Float alpha = 0.3f;
    	elTextview.setAlpha(alpha);
       	elTextview.setTextSize(12);
       	elTextview.setTypeface(dosisFont);
    }
    
    private void arreglarTextviewValue(TextView elTextview, String val){
    	elTextview.setTextColor(Color.WHITE);
    	elTextview.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
    	Float alpha = 0.8f;
    	elTextview.setAlpha(alpha);
    	elTextview.setTextSize(14);
    	elTextview.setTypeface(droidFont);
    	elTextview.setText(val);
    }
    
    @Override
    public boolean isEnabled(int position) {
    	// TODO Auto-generated method stub
    	return false;
    }
    
}
