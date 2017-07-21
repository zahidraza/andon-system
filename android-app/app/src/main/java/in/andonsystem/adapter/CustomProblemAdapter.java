package in.andonsystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import in.andonsystem.R;
import in.andonsystem.entity.Problem;

/**
 * Created by razamd on 4/1/2017.
 */

public class CustomProblemAdapter extends ArrayAdapter<Problem>{

    private Context mContext;
    private List<Problem> itemList;

    public CustomProblemAdapter(Context context, int resource, List<Problem> itemList) {
        super(context, resource, itemList);
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_list_item,parent,false);

        Problem buyer = itemList.get(position);
        TextView v1 = (TextView) view.findViewById(R.id.spinner_item);
        TextView v2 = (TextView) view.findViewById(R.id.id);
        v1.setText(buyer.getName());
        v2.setText(String.valueOf(buyer.getId()));

        return view;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_list_item,parent,false);

        Problem buyer = itemList.get(position);
        TextView v1 = (TextView) view.findViewById(R.id.spinner_item);
        TextView v2 = (TextView) view.findViewById(R.id.id);
        v1.setText(buyer.getName());
        v2.setText(String.valueOf(buyer.getId()));

        return view;
    }

}
