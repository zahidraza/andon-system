package in.andonsystem.v2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import in.andonsystem.R;
import in.andonsystem.v2.view.LetterImageView;


/**
 * Created by Md Zahid Raza on 17/06/2016.
 */
public class HolderContact extends RecyclerView.ViewHolder {

    LetterImageView icon;
    TextView details;

    public HolderContact(View view){
        super(view);

        icon = (LetterImageView)view.findViewById(R.id.contact_icon);
        details = (TextView)view.findViewById(R.id.contact_datails);

    }
}
