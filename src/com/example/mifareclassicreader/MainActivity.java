package com.example.mifareclassicreader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.example.mifareclassicreader.CustomAdapter;
import com.example.mifareclassicreader.MainActivity;
import com.example.mifareclassicreader.Sector;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.res.Resources;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {
	
	private Intent mOldIntent = null;
	private NfcAdapter mAdapter;
	private TextView tv;
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	private static final byte[] KEY_A_2 = new byte[] { (byte)0x67, (byte)0xa1, (byte)0x34,
	    (byte)0xe0, (byte)0x4a, (byte) 0x6a};
	
	private static final byte[] KEY_A = new byte[] { (byte)0xff, (byte)0xff, (byte)0xff,
	    (byte)0xff, (byte)0xff, (byte) 0xff};
	/**
	 * Filtro para activar/desactivar el foreground dispatch.
	 */
	private IntentFilter[] intentFiltersArray;
	
	/**
	 * Lista de tecnologías que soporta la app.
	 */
	private String[][] techListsArray;
	
	/**
	 * PendingIntent que utiliza el Foreground Dispatch System (FDS)
	 */
	private PendingIntent pendingIntent;
	
	/**
	 * ListView para visualizar los sectores
	 */
	ListView list;
	
	/**
	 * Adapter para el listview
	 */
    CustomAdapter adapter;
    
    /**
	 * La imagen que se muestra al principio
	 */
    ImageView im;
    
    /**
     * Arraylist para guardar los sectores que se muestran en la lista
     */
    public  ArrayList<Sector> CustomListViewValuesArr = new ArrayList<Sector>();
    
    private boolean listaHecha;
	
	
	/**
     * Check for NFC hardware, Mifare Classic support and for external storage.
     * If the directory structure and the std. keys files is not already there
     * it will be created. Also, at the first run of this App, a warning
     * notice will be displayed.
     * @see #copyStdKeysFilesIfNecessary()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "Posicione el tag", Toast.LENGTH_SHORT).show();
        
        //Inicialización del Pending Intent para que sea esta aplicación la prioridad cuando esté abierta
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        //tv = (TextView) findViewById(R.id.textView1);
        //tv.setText("");
        //tv.setTextSize(12);
        if(mAdapter == null){
        	//tv.setText("Active NFC en su dispositivo");
        }         
        
        /** Se hace un PendingIntent para darle prioridad a esta aplicación
         *  de que se quede abierta cuando se detecte un tag. Android "llena" este intent
         *  con los detalles del tag cuando se escanea.
         */
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFiltersArray = new IntentFilter[] {ndef, };    
        techListsArray = new String[][] { new String[] { MifareClassic.class.getName() } };
        
        /**
         * Esto es lo que concierne a la listview
         */
        list= ( ListView )findViewById( R.id.list);
        //list.setEnabled(false);
        //list.setOnClickListener(null);
        /**************** Create Custom Adapter *********/
        //setListData();
        adapter=new CustomAdapter( this, CustomListViewValuesArr,getResources() );
        listaHecha = false;
        //list.setAdapter( adapter );
        im = (ImageView) findViewById(R.id.imageView1);
        im.setImageResource(R.drawable.home);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    /**
     * Cuando la actividad pierde focus se deshabilita el FDS.
     */
    public void onPause(){
    	super.onPause();
    	mAdapter.disableForegroundDispatch(this);
    }
    
    /**
     * Cuando se reanuda la actividad se habilita el FDS.
     */
    public void onResume(){
    	super.onResume();
        mAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }
    
    public void onNewIntent(Intent intent) {
//tv.setText("");
    	
    	if(!listaHecha){
    		list.setAdapter( adapter );
    		im.setImageResource(android.R.color.transparent);
    		listaHecha = true;
    		View someView = findViewById(R.id.imageView1);
    		View root = someView.getRootView();
    		//root.setBackgroundColor(getResources().getColor(Color.GRAY));
    		root.setBackgroundColor(Color.parseColor("#bFbFbF"));
    	}
    	
    	if(CustomListViewValuesArr.size()>0){
        	CustomListViewValuesArr.clear();
    	}
    	//CustomListViewValuesArr.clear();
		Toast.makeText(getApplicationContext(), "Tag detectado", Toast.LENGTH_SHORT).show();   	
		//tv.append("Technology: \n");
    	Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);   
        for (String tech : tagFromIntent.getTechList()) {   
          //  tv.append(tech + "\n");
        }   

        boolean autenticado = false;   
        MifareClassic mfc = MifareClassic.get(tagFromIntent);   
        try {   
            String metaInfo = "";   
            //Enable I/O operations to the tag from this TagTechnology object.   
            mfc.connect();   
            int type = mfc.getType(); 
            int sectorCount = mfc.getSectorCount();   
            String typeS = "";   
            switch (type) {   
            case MifareClassic.TYPE_CLASSIC:   
                typeS = "TYPE_CLASSIC";   
                break;   
            case MifareClassic.TYPE_PLUS:   
                typeS = "TYPE_PLUS";   
                break;   
            case MifareClassic.TYPE_PRO:   
                typeS = "TYPE_PRO";   
                break;   
            case MifareClassic.TYPE_UNKNOWN:   
                typeS = "TYPE_UNKNOWN";   
                break;   
            }   
            metaInfo += "Card type：" + typeS + " with " + sectorCount + " Sectors, "   
                    + mfc.getBlockCount() + " Blocks Storage Space: " + mfc.getSize() + "B\n";   
            for (int j = 0; j < sectorCount; j++) {   
                //Authenticate a sector with key A.   
                //autenticado = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
            	autenticado = mfc.authenticateSectorWithKeyA(j, KEY_A); 
                int bCount;   
                int bIndex;   
                Sector sector = new Sector();
            	sector.setNumero(j);
                if (autenticado) {   
                	sector.setAutenticado(true);
                    metaInfo += "Sector " + j + ": Verified successfully\n";   
                    bCount = mfc.getBlockCountInSector(j);   
                    bIndex = mfc.sectorToBlock(j);   
                    for (int i = 0; i < bCount; i++) {   
                        byte[] data = mfc.readBlock(bIndex); 
                        /*
                        String str = new String(data, Charset.forName("US-ASCII"));
                        metaInfo += "Block " + bIndex + " : " + str + "\n";*/   
                        metaInfo += "Block " + bIndex + " : " + bytesToHex(data) + "\n";
                        sector.setBloqueValue(i, bytesToHex(data));
                        bIndex++;   
                    }   
                    
                } else {   
                	sector.setAutenticado(false);
                    metaInfo += "Sector " + j + ": Verified failure\n";   
                }   
                CustomListViewValuesArr.add( sector );
            }   
           // tv.append(metaInfo);   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    	adapter.notifyDataSetChanged();
    }
    
	public String readTag(Tag tag) {
		//Hace una instancia de un tag Mifare Classic
        MifareClassic mifare = MifareClassic.get(tag);
        try {
        	//Abre la comunicación con el tag
            mifare.connect();
            //Autentica el sector 0
            //mifare.authenticateSectorWithKeyA(0,hexStringToByteArray("FFFFFFFFFFFF"));
            
            //Lee el bloque 0
            byte[] payload = mifare.readBlock(0);
            //Retorna lo que lee en el bloque 0 en formato String
            return new String(payload, Charset.forName("US-ASCII"));
        } catch (IOException e) {
        	//Se encarga de los errores que haya en la lectura
        	Log.e(mifare.getClass().getSimpleName(), "IOException while closing MifareClassic...", e);
        } finally {
            if (mifare != null) {
               try {
            	   //Cerrar la comunicación con el tag
                   mifare.close();
               }
               catch (IOException e) {
                   Log.e(mifare.getClass().getSimpleName(), "Error closing tag...", e);
               }
            }
        }
        return null;
    }
	
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	private void processIntent(Intent intent){
Toast.makeText(getApplicationContext(), "Tag detectado!!!", Toast.LENGTH_SHORT).show();   	
    	
    	Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);   
        for (String tech : tagFromIntent.getTechList()) {   
            tv.append(tech + "\n");
        }
	}
	
	private String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public void setListData()
    {
         
        for (int i = 0; i < 16; i++) {
             
            Sector sched = new Sector();
                 
              /******* Firstly take data in model object ******/
               sched.setNumero(i);
                
            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add( sched );
        }
    }
	
	
	
}
