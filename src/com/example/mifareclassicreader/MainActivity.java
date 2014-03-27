package com.example.mifareclassicreader;

import java.io.IOException;
import java.nio.charset.Charset;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {
	
	private Intent mOldIntent = null;
	private NfcAdapter mAdapter;
	private TextView tv;
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
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
     * Check for NFC hardware, Mifare Classic support and for external storage.
     * If the directory structure and the std. keys files is not already there
     * it will be created. Also, at the first run of this App, a warning
     * notice will be displayed.
     * @see #copyStdKeysFilesIfNecessary()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "Empieza aplicación", Toast.LENGTH_SHORT).show();
        
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText("");
        tv.setTextSize(12);
        if(mAdapter == null){
        	tv.setText("Encienda el NFC de su dispositivo");
        }         
        
        /** Se hace un PendingIntent para darle prioridad a esta aplicación
         *  de que se quede abierta cuando se detecte un tag. Android "llena" este intent
         *  con los detalles del tag cuando se escanea.
         */
        
        
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] intentFiltersArray = new IntentFilter[] {ndef, };    
        String[][] techListsArray = new String[][] { new String[] { MifareClassic.class.getName() } };
        
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
    	tv.setText("");
		Toast.makeText(getApplicationContext(), "Tag detectado!!!", Toast.LENGTH_SHORT).show();   	
		tv.append("Technology: \n");
    	Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);   
        for (String tech : tagFromIntent.getTechList()) {   
            tv.append(tech + "\n");
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
                autenticado = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);   
                int bCount;   
                int bIndex;   
                if (autenticado) {   
                    metaInfo += "Sector " + j + ": Verified successfully\n";   
                    bCount = mfc.getBlockCountInSector(j);   
                    bIndex = mfc.sectorToBlock(j);   
                    for (int i = 0; i < bCount; i++) {   
                        byte[] data = mfc.readBlock(bIndex); 
                        /*
                        String str = new String(data, Charset.forName("US-ASCII"));
                        metaInfo += "Block " + bIndex + " : " + str + "\n";*/   
                        metaInfo += "Block " + bIndex + " : " + bytesToHex(data) + "\n";
                        bIndex++;   
                    }   
                } else {   
                    metaInfo += "Sector " + j + ": Verified failure\n";   
                }   
            }   
            tv.append(metaInfo);   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    	
    	
    }
    
    /**
    private void processIntent(Intent intent) {    
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);   
        for (String tech : tagFromIntent.getTechList()) {   
            System.out.println(tech);   
        }   
        boolean auth = false;   
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
            metaInfo += "Card type：" + typeS + "n with" + sectorCount + " Sectorsn, "   
                    + mfc.getBlockCount() + " BlocksnStorage Space: " + mfc.getSize() + "Bn";   
            for (int j = 0; j < sectorCount; j++) {   
                //Authenticate a sector with key A.   
                auth = mfc.authenticateSectorWithKeyA(j,   
                        MifareClassic.KEY_DEFAULT);   
                int bCount;   
                int bIndex;   
                if (auth) {   
                    metaInfo += "Sector " + j + ": Verified successfullyn";   
                    bCount = mfc.getBlockCountInSector(j);   
                    bIndex = mfc.sectorToBlock(j);   
                    for (int i = 0; i < bCount; i++) {   
                        byte[] data = mfc.readBlock(bIndex);   
                        metaInfo += "Block " + bIndex + " : "   
                                + "hola" + "n";   
                        bIndex++;   
                    }   
                } else {   
                    metaInfo += "Sector " + j + ": Verified failuren";   
                }   
            }   
            tv.setText(metaInfo);   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
    }   
    */
    
	public String readTag(Tag tag) {
		//Hace una instancia de un tag Mifare Classic
        MifareClassic mifare = MifareClassic.get(tag);
        try {
        	//Abre la comunicación con el tag
            mifare.connect();
            //Autentica el sector 0
            mifare.authenticateSectorWithKeyA(0,hexStringToByteArray("FFFFFFFFFFFF"));
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
}
