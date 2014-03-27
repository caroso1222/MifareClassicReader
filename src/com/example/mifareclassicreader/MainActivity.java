package com.example.mifareclassicreader;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	
	private Intent mOldIntent = null;
	private NfcAdapter mAdapter;
	private TextView tv;

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
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText("buenas buenas");
        if(mAdapter != null){
        	tv.setText("1");
        }       
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        
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
     * Handle new Intent as a new tag Intent and if the tag/device does not
     * support Mifare Classic, then run {@link TagInfoTool}.
     * @see Common#treatAsNewTag(Intent, android.content.Context)
     * @see TagInfoTool
     */
    @Override
	protected void onNewIntent(Intent intent){
    	tv.setText("holaquetal");
    	/**
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
		Tag mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);  // get the detected tag
		Parcelable[] msgs =
intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefRecord firstRecord = ((NdefMessage)msgs[0]).getRecords()[0];
			byte[] payload = firstRecord.getPayload();
			int payloadLength = payload.length;
			int langLength = payload[0];
			int textLength = payloadLength - langLength - 1;
			byte[] text = new byte[textLength];
			System.arraycopy(payload, 1+langLength, text, 0, textLength);
			Toast.makeText(this, this.getString(R.string.ok_detection)+new String(text), Toast.LENGTH_LONG).show();
					}
					*/
	}

    
    
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
    
}
