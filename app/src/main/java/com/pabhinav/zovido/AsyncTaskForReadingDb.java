package com.pabhinav.zovido;

import android.os.AsyncTask;

import java.util.ArrayList;

class AsyncTaskForReadingDb extends AsyncTask<Void, Void, ArrayList<DataParcel>> {

    private TaskComplete taskComplete;
    private DatabaseHelper databaseHelper;

    public AsyncTaskForReadingDb(DatabaseHelper databaseHelper){
        this.databaseHelper = databaseHelper;
    }

    @Override
    protected ArrayList<DataParcel> doInBackground(Void... params) {
        return databaseHelper.getAllDataParcels();
    }

    @Override
    protected void onPostExecute(ArrayList<DataParcel> dataParcels){
        taskComplete.onTaskComplete(dataParcels);
    }

    public void setTaskComplete(TaskComplete taskComplete){
        this.taskComplete = taskComplete;
    }

    interface TaskComplete{
        void onTaskComplete(ArrayList<DataParcel> dataParcels);
    }
}
