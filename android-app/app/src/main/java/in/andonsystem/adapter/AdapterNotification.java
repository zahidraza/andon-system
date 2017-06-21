package in.andonsystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import in.andonsystem.R;
import in.andonsystem.activity.v2.IssueDetailActivity;
import in.andonsystem.dto.Notification;

/**
 * Created by razamd on 4/8/2017.
 */

public class AdapterNotification extends RecyclerView.Adapter<HolderNotification> {

    private Context context;
    private TreeSet<Notification> set;
    private int appNo;

    public AdapterNotification(Context context, TreeSet<Notification> set, int appNo){
        this.context = context;
        this.set = set;
        this.appNo = appNo;
    }

    @Override
    public HolderNotification onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification,parent,false);
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.nfn_container);
        container.setOnClickListener(new RelativeLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                String idStr = ((TextView)v.findViewById(R.id.nfn_id)).getText().toString();
                Intent i;
                if (appNo == 1){
                    i = new Intent(context, in.andonsystem.activity.v1.IssueDetailActivity.class);
                }else {
                    i = new Intent(context, IssueDetailActivity.class);
                }
                i.putExtra("issueId",Long.parseLong(idStr));
                context.startActivity(i);
            }
        });

        HolderNotification holder = new HolderNotification(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HolderNotification holder, int position) {
        Object[] array = set.toArray();
        Notification data = (Notification)array[position];

        long time = data.getTime();
        int hours,mins;
        hours = (int) TimeUnit.MILLISECONDS.toHours(time);
        time = time - TimeUnit.HOURS.toMillis(hours);
        mins = (int)TimeUnit.MILLISECONDS.toMinutes(time);
        String at;
        if(hours > 0) {
            at = String.format("%02d hour %02d min ago", hours, mins);
        }else{
            at = String.format("%02d min ago",mins);
        }

        holder.message.setText(data.getMessage());
        holder.id.setText(String.valueOf(data.getIssueId()));
        holder.time.setText(at);

        if(data.getState() == 0){
            holder.icon.setLetter('R');
            holder.icon.setBackgroundColor("#ff4444");
        }else if(data.getState() == 1){
            holder.icon.setLetter('A');
            holder.icon.setBackgroundColor("#0099cc");
        }else{
            holder.icon.setLetter('F');
            holder.icon.setBackgroundColor("#669900");
        }
    }
    @Override
    public int getItemViewType(int position) {
        Object[] array = set.toArray();
        Notification data = (Notification)array[position];
        return data.getState();
    }


    @Override
    public int getItemCount() {
        return set.size();
    }
}
