package in.andonsystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.TreeSet;

import in.andonsystem.R;
import in.andonsystem.activity.v2.IssueDetailActivity;
import in.andonsystem.dto.Problem;

public class AdapterHome extends RecyclerView.Adapter<HolderHome> {

    private Context context;
    private TreeSet<Problem> set;

    public AdapterHome(Context context, TreeSet<Problem> set){
        this.context = context;
        this.set = set;
    }

    @Override
    public HolderHome onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home,parent,false);
        final LinearLayout container = (LinearLayout) view.findViewById(R.id.issue_container);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = ((TextView)v.findViewById(R.id.issue_id)).getText().toString();
                String appNo = ((TextView)v.findViewById(R.id.appNo)).getText().toString();
                Intent intent;
                Log.d("Adapter","$$$ appNo = " + appNo);
                if (appNo.equals("1")) {
                    intent = new Intent(context, in.andonsystem.activity.v1.IssueDetailActivity.class);
                }else {
                    intent = new Intent(context, IssueDetailActivity.class);
                }
                intent.putExtra("issueId", Long.parseLong(idStr));
                context.startActivity(intent);
            }
        });
        if(viewType == 0){
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.tomato));
        }else if(viewType == 1){
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
        }else if(viewType == 2){
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.limeGreen));
        }

        return new HolderHome(view);
    }

    @Override
    public void onBindViewHolder(HolderHome holder, int position) {

        Object[] array = set.toArray();
        Problem problem = (Problem)array[position];
        String prob = problem.getProbName();
        if (problem.getCritical() != null && problem.getCritical().equalsIgnoreCase("YES")) {
            prob = prob + "*";
        }
        holder.icon.setLetter(prob.charAt(0));
        holder.icon.setOval(true);
        holder.problem.setText(prob);
        holder.time.setText(problem.getRaiseTime());
        holder.team.setText(problem.getdField1());
        holder.appNo.setText(String.valueOf(problem.getAppNo()));
        String field2 = problem.getdField2();
        if (problem.getDowntime() >= 0){
            field2 = String.format("%s [ %03d min ]",field2,problem.getDowntime()/(1000*60));
        }
        holder.buyer.setText(field2);
        holder.issueId.setText(String.valueOf(problem.getIssueId()));
    }

    @Override
    public int getItemCount() {
        return set.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object[] array = set.toArray();
        Problem problem = (Problem)array[position];
        return problem.getFlag();
    }

    public void insert(Problem problem){
        set.add(problem);
        notifyDataSetChanged();
    }

    public void delete(Problem problem) {
        set.remove(problem);
        notifyDataSetChanged();
    }

    /**
     * Since Comparison is based on id, fixAt, ackAt and raisedAt fields, and the received object has changed state,
     * So, it is required to try deleting by moving it into previous states
     * @param problem
     */
    public void update(Problem problem){
        Problem temp = new Problem();
        temp.setIssueId(problem.getIssueId());
        temp.setFlag(0);
        set.remove(temp);  //try removing if it was earlier in raised state
        temp.setFlag(1);
        set.remove(temp);  //try removing if it was earlier in acknowledged state
        temp.setFlag(2);
        set.remove(temp);  //try removing if it was earlier in fixed state

        set.add(problem);
        notifyDataSetChanged();
    }
}
