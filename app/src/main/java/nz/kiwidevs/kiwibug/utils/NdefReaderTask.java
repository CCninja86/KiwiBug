package nz.kiwidevs.kiwibug.utils;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import nz.kiwidevs.kiwibug.TagFoundActivity;

/**
 * Created by Michael on 07.05.2017.
 */

public class NdefReaderTask extends AsyncTask<Tag,Void,String> {

    WeakReference<Activity> mWeakActivity;


    public NdefReaderTask(Activity mainActivity){
        mWeakActivity = new WeakReference<Activity>(mainActivity);

    }

    /**
     * Reads the NFC Tag
     * @param params [0] is the tag
     * @return Content of the tag
     */
    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];

        Ndef ndef = Ndef.get(tag);

        if(ndef == null){
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        NdefRecord[] records = ndefMessage.getRecords();

        for(NdefRecord ndefRecord : records){
           // if(ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(),NdefRecord.RTD_TEXT)){
            if(ndefRecord.getTnf() == NdefRecord.TNF_MIME_MEDIA){
                try{
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }


    private String readText(NdefRecord record) throws UnsupportedEncodingException {

        byte[] payload = record.getPayload();
    /*
        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        */
        return new String(payload);
    }



    @Override
    /**
     NFC Tag has been read, update the UI from here

     **/
    protected void onPostExecute(String result) {
        if(result != null){

            Intent myIntent = new Intent(mWeakActivity.get(), TagFoundActivity.class);
            myIntent.putExtra("nfcData", result); //Optional parameters
            mWeakActivity.get().startActivity(myIntent);


        }
    }
}
