package in.andonsystem.v2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import in.andonsystem.R;

/**
 * Created by Md Zahid Raza on 17/06/2016.
 */
public class AdapterContact extends RecyclerView.Adapter<HolderContact> {

    private List<String> list;
    private Context context;

    public AdapterContact(Context context, List<String> list){
        this.context = context;
        this.list = list;
        Collections.sort(list);
    }

    @Override
    public HolderContact onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contacts,parent,false);
        HolderContact holder = new HolderContact(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HolderContact holder, int position) {
        String detail = list.get(position);

        holder.icon.setLetter(detail.charAt(0));
        holder.icon.setOval(true);
        holder.details.setText(detail);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
