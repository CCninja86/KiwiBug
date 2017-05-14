package nz.kiwidevs.kiwibug;

/**
 * Created by james on 11/05/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by James on 12.05.2017.
 */
public class HintListViewAdapter extends ArrayAdapter<Hint> {

    private ArrayList<Hint> hints;
    private  ArrayList<Integer> selectedItems;
    private int layout;

    public HintListViewAdapter(Activity context, int layout, ArrayList<Hint> hints){
        super(context, layout, hints);
        this.layout = layout;
        this.hints = hints;
        this.selectedItems = new ArrayList<>();
    }

    public ArrayList<Integer> getSelectedItems(){
        return  this.selectedItems;
    }

    public void toggleSelected(Integer position){
        if(selectedItems.contains(position)){
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
    }

    @Override
    public int getCount(){
        return hints.size();
    }

    @Override
    public Hint getItem(int position){
        return hints.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
        }

        Hint hint = hints.get(position);

        if(hint != null){
            TextView textViewHint = (TextView) convertView.findViewById(R.id.textViewHint);
            TextView textViewUserSubmitted = (TextView) convertView.findViewById(R.id.textViewUserSubmitted);
            TextView textViewTimestamp = (TextView) convertView.findViewById(R.id.textViewTimestamp);

            if(textViewHint != null){
                textViewHint.setText(hint.getHint());
            }

            if(textViewUserSubmitted != null){
                textViewUserSubmitted.setText(hint.getUserSubmitted());
            }

            if(textViewTimestamp != null){
                textViewTimestamp.setText(hint.getTimeStamp());
            }
        }

        return  convertView;
    }

}