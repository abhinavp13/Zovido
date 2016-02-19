package com.pabhinav.zovido;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author pabhinav
 */
public class SavedLogRecyclerViewAdapter extends RecyclerView.Adapter<SavedLogRecyclerViewAdapter.SavedDataObjectParcelHolder>{

    private Context context;
    private ArrayList<DataParcel> mDataSet;
    private static MyClickListener myClickListener;
    private RecyclerView recyclerView;
    private TextView savedCounterTextView;

    public SavedLogRecyclerViewAdapter(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
        savedCounterTextView = (TextView)((Activity)context).findViewById(R.id.saved_count_text_view);
        mDataSet = new ArrayList<>();
    }

    @Override
    public SavedLogRecyclerViewAdapter.SavedDataObjectParcelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_log_recycler_view_item, parent, false);

        return new SavedDataObjectParcelHolder(view);
    }

    @Override
    public void onBindViewHolder(SavedLogRecyclerViewAdapter.SavedDataObjectParcelHolder holder, int position) {

        if(mDataSet.get(position).getName() == null){
            holder.name.setText("Unknown");
        } else {
            holder.name.setText(mDataSet.get(position).getName());
        }

        holder.phoneNumber.setText(mDataSet.get(position).getUserMobileNumber());
        holder.timestamp.setText(mDataSet.get(position).getTimestamp());
        holder.callDuration.setText(mDataSet.get(position).getCallDuration());
        holder.userType.setText(mDataSet.get(position).getUserType());
        holder.productType.setText(mDataSet.get(position).getProductType());
    }

    @Override
    public int getItemCount() {
        if(savedCounterTextView!=null) {
            savedCounterTextView.setText(String.valueOf(mDataSet.size()));
        }
        return mDataSet.size();
    }

    public DataParcel getItem(int position){
        return mDataSet.get(position);
    }

    public ArrayList<DataParcel> getAllItems(){
        return mDataSet;
    }


    public void setOnItemClickListener(MyClickListener myClickListener) {
        SavedLogRecyclerViewAdapter.myClickListener = myClickListener;
    }


    public class SavedDataObjectParcelHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView timestamp;
        TextView phoneNumber;
        TextView userType;
        TextView productType;
        TextView callDuration;
        LinearLayout editLinearLayout;
        RelativeLayout deleteRelativeLayout;
        RelativeLayout modifyRelativeLayout;

        private boolean toggleEditSettings;

        public SavedDataObjectParcelHolder(View itemView) {
            super(itemView);

            toggleEditSettings = false;
            name = (TextView) itemView.findViewById(R.id.saved_user_name);
            timestamp = (TextView) itemView.findViewById(R.id.saved_timestamp);
            phoneNumber = (TextView) itemView.findViewById(R.id.saved_phone_number);
            userType = (TextView) itemView.findViewById(R.id.user_type_text_view);
            productType = (TextView) itemView.findViewById(R.id.product_type_text_view);
            callDuration = (TextView) itemView.findViewById(R.id.saved_duration);
            editLinearLayout = (LinearLayout) itemView.findViewById(R.id.edit_linear_layout);
            deleteRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.delete_relative_layout);
            modifyRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.modify_relative_layout);

            deleteRelativeLayout.setOnClickListener(this);
            modifyRelativeLayout.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.delete_relative_layout){
                myClickListener.onDeleteForItemClicked(getPosition(), v);
                return;
            } else if(v.getId() == R.id.modify_relative_layout){
                myClickListener.onModifyForItemClicked(getPosition(), v);
                return;
            }

            /** Else it is edit settings clicked **/
            if(!toggleEditSettings){
                editLinearLayout.setVisibility(View.VISIBLE);
            } else {
                editLinearLayout.setVisibility(View.GONE);
            }
            toggleEditSettings = !toggleEditSettings;
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void addItem(DataParcel dataObj, int index) {
        mDataSet.add(dataObj);
        notifyDataSetChanged();

        if(savedCounterTextView!=null) {
            savedCounterTextView.setText(String.valueOf(mDataSet.size()));
        }
        showBackgroundToHideNoSavedLogsMssg();
    }

    public void addAllItem(ArrayList<DataParcel> dataParcels, int index){

        /** Changed logic to first delete everything **/
        mDataSet =  new ArrayList<>();

        mDataSet.addAll(index, dataParcels);
        notifyDataSetChanged();

        if(savedCounterTextView!=null) {
            savedCounterTextView.setText(String.valueOf(mDataSet.size()));
        }
        showBackgroundToHideNoSavedLogsMssg();
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyDataSetChanged();

        if(savedCounterTextView!=null) {
            savedCounterTextView.setText(String.valueOf(mDataSet.size()));
        }
        hideBackgroundToShowNoSavedLogsMssg();

        /** update Ticks **/
        CallLogTab.updateTick(mDataSet);
    }

    public void deleteAll(){
        mDataSet = new ArrayList<>();
        notifyDataSetChanged();

        if(savedCounterTextView!=null) {
            savedCounterTextView.setText(String.valueOf(mDataSet.size()));
        }
        hideBackgroundToShowNoSavedLogsMssg();

        /** update Ticks **/
        CallLogTab.updateTick(mDataSet);
    }

    private void showBackgroundToHideNoSavedLogsMssg(){
        if(mDataSet.size() >= 1){
            recyclerView.setBackgroundColor(context.getResources().getColor(R.color.recycler_background));
        }
    }

    private void hideBackgroundToShowNoSavedLogsMssg(){
        if(mDataSet.size() == 0){
            recyclerView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
        void onDeleteForItemClicked(int position , View v);
        void onModifyForItemClicked(int position, View v);
    }
}
