package in.andonsystem.v2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import in.andonsystem.R;
import in.andonsystem.v2.view.LetterImageView;

/**
 * Created by razamd on 4/8/2017.
 */

public class HolderNotification extends RecyclerView.ViewHolder {

    public LetterImageView icon;
    public TextView message;
    public TextView time;
    public TextView id;

    public HolderNotification(View view) {
        super(view);

        icon = (LetterImageView) view.findViewById(R.id.nfn_icon);
        message = (TextView) view.findViewById(R.id.nfn_message);
        time = (TextView) view.findViewById(R.id.nfn_time);
        id = (TextView) view.findViewById(R.id.nfn_id);
    }
}
