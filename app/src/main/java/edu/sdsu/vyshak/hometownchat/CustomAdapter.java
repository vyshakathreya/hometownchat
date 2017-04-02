package edu.sdsu.vyshak.hometownchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vysha on 3/31/2017.
 */

public class CustomAdapter extends ArrayAdapter<User> {

    private ArrayList<User> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtYear;
        TextView txtState;
        TextView txtCountry;
        ImageView info;
    }

    public CustomAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtYear = (TextView) convertView.findViewById(R.id.year);
            viewHolder.txtState = (TextView) convertView.findViewById(R.id.state);
            viewHolder.txtCountry = (TextView) convertView.findViewById(R.id.country);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

            /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;
*/
        viewHolder.txtName.setText(dataModel.getNickname());
        viewHolder.txtCountry.setText(dataModel.getCountry());
        viewHolder.txtState.setText(dataModel.getState());
        viewHolder.txtYear.setText(dataModel.getYear());
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
