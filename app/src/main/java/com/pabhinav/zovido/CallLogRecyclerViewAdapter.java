package com.pabhinav.zovido;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CallLogRecyclerViewAdapter extends RecyclerView.Adapter<CallLogRecyclerViewAdapter.CallDataObjectParcelHolder> {

    private ArrayList<CallDataObjectParcel> mDataset;
    private static MyClickListener myClickListener;
    private Context context;
    private TextView recentCounterTextView;

    public CallLogRecyclerViewAdapter(Context context){
        this.context = context;
        recentCounterTextView = (TextView)((Activity)context).findViewById(R.id.recent_count_text_view);
        mDataset = new ArrayList<>();
    }

    public static class CallDataObjectParcelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView timestamp;
        TextView phoneNumber;
        TextView callDuration;
        ImageView callType;
        ImageView tickImage;

        public CallDataObjectParcelHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            phoneNumber = (TextView) itemView.findViewById(R.id.phone_number);
            callDuration = (TextView) itemView.findViewById(R.id.duration);
            callType = (ImageView) itemView.findViewById(R.id.call_type);
            tickImage = (ImageView) itemView.findViewById(R.id.saved_tick_image_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        CallLogRecyclerViewAdapter.myClickListener = myClickListener;
    }

    @Override
    public CallDataObjectParcelHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_recycler_view_item, parent, false);

        CallDataObjectParcelHolder CallDataObjectParcelHolder = new CallDataObjectParcelHolder(view);
        return CallDataObjectParcelHolder;
    }

    @Override
    public void onBindViewHolder(CallDataObjectParcelHolder holder, int position) {

        if(mDataset.get(position).getName() == null){
            holder.name.setText("Unknown");
        } else {
            holder.name.setText(mDataset.get(position).getName());
        }

        holder.phoneNumber.setText(mDataset.get(position).getPhoneNumber());
        holder.timestamp.setText(mDataset.get(position).getCallDate());
        holder.callDuration.setText(mDataset.get(position).getCallDuration());
        if(mDataset.get(position).getCallType().equals(Constants.incoming)){
            holder.callType.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.incoming_call));
        } else if(mDataset.get(position).getCallType().equals(Constants.outgoing)){
            holder.callType.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.outgoing_call));
        } else {
            holder.callType.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.missed_call));
        }
        if(mDataset.get(position).isShowTick()){
            holder.tickImage.setVisibility(View.VISIBLE);
        } else {
            holder.tickImage.setVisibility(View.INVISIBLE);
        }
    }

    public void updateTick(int position){
        if(position != -1){
            mDataset.get(position).setShowTick(true);
            notifyDataSetChanged();
        }
    }

    public void updateAllAtOnce(ArrayList<CallDataObjectParcel> dataObjectParcels){

        mDataset = new ArrayList<>();
        mDataset.addAll(dataObjectParcels);
        notifyDataSetChanged();

        /** Set Counter value in drawer **/
        if(recentCounterTextView != null){
            recentCounterTextView.setText(String.valueOf(mDataset.size()));
        }

        /** Hide loading spinner **/
        hideLoadingSpinner();
    }

    public void hideLoadingSpinner(){

        ImageView spinner = (ImageView) ((Activity)context).findViewById(R.id.spinner);
        ImageView spinnerCover = (ImageView) ((Activity)context).findViewById(R.id.imageView_spinner_cover);
        TextView loadingTextView = (TextView) ((Activity)context).findViewById(R.id.loading_in_call_log);
        RecyclerView recyclerView = (RecyclerView) ((Activity) context).findViewById(R.id.my_recycler_view);

        if(spinner!= null && spinnerCover != null && loadingTextView != null && recyclerView != null) {

            /** Make sure to have empty call log check before assuring that a person don't have call logs **/
            if(getItemCount() == 0 && CallLogTab.isEmptyCallLog){
                spinner.clearAnimation();
                loadingTextView.setText("No Call Logs Found");
                ((Activity)context).findViewById(R.id.spinner_hidden).setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                return;
            } else {
                ((Activity)context).findViewById(R.id.spinner_hidden).setVisibility(View.GONE);
            }

            if(getItemCount() != 0) {
                spinner.setVisibility(View.GONE);
                spinnerCover.setVisibility(View.GONE);
                loadingTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setBackgroundColor(context.getResources().getColor(R.color.recycler_background));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public CallDataObjectParcel getItem(int position){
        return mDataset.get(position);
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public ArrayList<CallDataObjectParcel> getAllItems(){
        return mDataset;
    }
}