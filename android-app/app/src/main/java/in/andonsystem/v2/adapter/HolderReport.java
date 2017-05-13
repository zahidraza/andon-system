package in.andonsystem.v2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import in.andonsystem.R;
import in.andonsystem.v2.view.LetterImageView;

/**
 * Created by Md Zahid Raza on 26/06/2016.
 */
public class HolderReport extends RecyclerView.ViewHolder{

    LetterImageView icon;
    TextView probName;
    TextView dField1; //Dynamic field1: app1= Line, app2=Team
    TextView dField2; //Dynamic field2: app1= department, app2=Buyer
    TextView downtime;


    public HolderReport(View view){
        super(view);

        icon = (LetterImageView) view.findViewById(R.id.report_icon);
        probName = (TextView) view.findViewById(R.id.report_prob_name);
        dField2 = (TextView)view.findViewById(R.id.report_dynamic_field2);
        dField1 = (TextView) view.findViewById(R.id.report_dynamic_field1);
        downtime = (TextView) view.findViewById(R.id.report_downtime);
    }


}
