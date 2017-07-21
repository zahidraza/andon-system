package in.andonsystem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import in.andonsystem.R;
import in.andonsystem.dto.Contact;

/**
 * Created by Md Zahid Raza on 17/06/2016.
 */
public class AdapterContact extends RecyclerView.Adapter<HolderContact> {

    private List<Contact> list;
    private Context context;

    public AdapterContact(Context context, List<Contact> list){
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
        Contact contact = list.get(position);

        holder.icon.setLetter(contact.getName().charAt(0));
        holder.icon.setOval(true);
        holder.name.setText(contact.getName());
        holder.number.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
