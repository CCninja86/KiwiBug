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

import nz.kiwidevs.kiwibug.TagActivity;

/**
 * Created by Michael on 07.05.2017.
 */

public class NdefReaderTask extends AsyncTask<Tag,Void,NdefRecord[]> {

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
    protected NdefRecord[] doInBackground(Tag... params) {
        Tag data = params[0];

        Ndef ndef = Ndef.get(data);

        if(ndef == null){
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        NdefRecord[] records = ndefMessage.getRecords();

        if(records.length > 0){
            return records;
        }
//        for(NdefRecord ndefRecord : records){
//           // if(ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(),NdefRecord.RTD_TEXT)){
//            if(ndefRecord.getTnf() == NdefRecord.TNF_MIME_MEDIA){
//                try{
//                    return readText(ndefRecord);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return null;

    }


    private String readText(NdefRecord record) throws UnsupportedEncodingException {

        byte[] payload = record.getPayload();

        String string = new String(payload);

        if(!string.equals("Hello KiwiBug")){
            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            string = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        return string;
    }



    @Override
    /**
     NFC Tag has been read, update the UI from here

     **/
    protected void onPostExecute(NdefRecord[] records) {
        if(records != null){
            boolean correctType = false;
            String message = "";

            try {
                if(readText(records[0]).equals("Hello KiwiBug")){
                    correctType = true;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(correctType){
                try {
                    message = readText(records[1]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Intent myIntent = new Intent(mWeakActivity.get(), TagActivity.class);
                myIntent.putExtra("nfcData", message); //Optional parameters
                mWeakActivity.get().startActivity(myIntent);
            }
        }
    }
}
