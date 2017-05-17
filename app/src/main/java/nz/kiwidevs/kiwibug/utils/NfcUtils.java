package nz.kiwidevs.kiwibug.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Michael on 07.05.2017.
 */

public class NfcUtils {
    private NfcAdapter mAdapter;

    PendingIntent nfcPendingIntent;
    IntentFilter[] intentFiltersArray;

    Context context;

    public NfcUtils(Context context){
        this.context = context;

    }

    public boolean setupNfcTagIntent(){
        Intent nfcIntent = new Intent(context,context.getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


        nfcPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        mAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mAdapter == null) {
            Toast.makeText(context, "NFC is not available!",Toast.LENGTH_LONG).show();
            return false;
        }else if(!mAdapter.isEnabled()){
            Toast.makeText(context, "Please enable NFC!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }


    public void getTagInfoForIntent(Intent intent){
        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {


            String type = intent.getType();

            if ("app/kb".equals(type)) {


                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                new NdefReaderTask((Activity) context).execute(tag);



            } else {

                //Exception for wrong mime type

            }

        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {



            // In case we would still use the Tech Discovered Intent

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            String[] techList = tag.getTechList();

            String searchedTech = Ndef.class.getName();



            for (String tech : techList) {

                if (searchedTech.equals(tech)) {

                    new NdefReaderTask((Activity) context).execute(tag);

                    break;

                }

            }

        }
    }

    public static void getTagIDFromTag(Activity context, Intent intent){
        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();

            if ("app/kb".equals(type)) {


                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                new NdefReaderTask((Activity) context).execute(tag);



            }


        }
    }

    public static boolean writeMessageToTag(Intent intent, String text, String tagID){
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);



        if(tag != null) {


            Ndef ndef = Ndef.get(tag);

            if(ndef == null){
                return false;
            }



            NdefRecord[]records = new NdefRecord[2];

            //@TODO: Implement TagIDs!
            Log.d("NfcUtils", tagID);
            records[0] = NdefRecord.createMime("app/kb",tagID.getBytes());
            records[1] = createTextRecord("en", text);

            NdefMessage message = new NdefMessage(records);

            try {

            ndef.connect();
            ndef.writeNdefMessage(message);
            ndef.close();

                return true;

            } catch (IOException e) {

                e.printStackTrace();

            } catch (FormatException e) {

                e.printStackTrace();

            }

        }
            return false;
    }

    private static NdefRecord createTextRecord(String language, String text) {
        byte[] languageBytes;
        byte[] textBytes;
        try {
            languageBytes = language.getBytes("US-ASCII");
            textBytes = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }

        byte[] recordPayload = new byte[1 + (languageBytes.length & 0x03F) + textBytes.length];

        recordPayload[0] = (byte)(languageBytes.length & 0x03F);
        System.arraycopy(languageBytes, 0, recordPayload, 1, languageBytes.length & 0x03F);
        System.arraycopy(textBytes, 0, recordPayload, 1 + (languageBytes.length & 0x03F), textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, null, recordPayload);
    }

    private static String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();

        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";


        int languageCodeLength = payload[0] & 0063;

        //String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }


    public void enableForegroundDispatch(){
        mAdapter.enableForegroundDispatch((Activity)context, nfcPendingIntent, null, null);
    }

    public void disableForegroundDispatch(){
        //@TODO Implement disableForegroundDispatch
        mAdapter.disableForegroundDispatch((Activity) context);
    }


}
