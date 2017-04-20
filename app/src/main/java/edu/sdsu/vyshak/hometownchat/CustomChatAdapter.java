package edu.sdsu.vyshak.hometownchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vysha on 4/17/2017.
 */

public class CustomChatAdapter extends ArrayAdapter<ChatMessage> {


    private ArrayList<ChatMessage> dataSet;
        Context mContext;



// View lookup cache
private static class ViewHolder {
    TextView txtSender;
    TextView txtMessageSender;
    TextView txtReciever;
    TextView txtMessageReciever;
}

    public CustomChatAdapter(ArrayList<ChatMessage> data, Context context) {
        super(context, R.layout.message, data);
        this.dataSet = data;
        this.mContext=context;

    }

    public ArrayList<ChatMessage> getData() {
        return dataSet;
    }

    public void setDataSet(ArrayList<ChatMessage> dataSet) {
        this.dataSet.addAll(dataSet);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= LayoutInflater.from(getContext());
            View rowView=inflater.inflate(R.layout.message, null,true);
            ChatMessage user = dataSet.get(position);
            TextView userName = (TextView) rowView.findViewById(R.id.sender);
            TextView lastMessage = (TextView) rowView.findViewById(R.id.message_text_sent);
            userName.setText(user.getSender());
            lastMessage.setText(user.getMessageText());
            return rowView;
        };
    }
