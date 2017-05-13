package in.andonsystem.v2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import in.andonsystem.R;
import in.andonsystem.v2.view.LetterImageView;

/**
 * Created by Md Zahid Raza on 17/06/2016.
 */
public class HolderHome extends RecyclerView.ViewHolder {

    LetterImageView icon;
    TextView problem;
    TextView time;
    TextView team;
    TextView buyer;
    TextView issueId;

    public HolderHome(View view){
        super(view);

        icon = (LetterImageView)view.findViewById(R.id.issue_letter_image);
        problem = (TextView)view.findViewById(R.id.issue_prob_name);
        time = (TextView)view.findViewById(R.id.issue_time);
        team = (TextView)view.findViewById(R.id.issue_team_name);
        buyer = (TextView)view.findViewById(R.id.issue_buyer_name);
        issueId = (TextView)view.findViewById(R.id.issue_id);

    }
}
