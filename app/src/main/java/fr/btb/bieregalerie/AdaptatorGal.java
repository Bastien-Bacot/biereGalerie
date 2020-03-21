package fr.btb.bieregalerie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

public class AdaptatorGal extends RecyclerView.Adapter<AdaptatorGal.ViewHolder> {
    private ArrayList<Uri> thumbList, fullList;
    private Context context;
    private JSONArray arr_data;


    public AdaptatorGal(Context context, ArrayList<Uri> thumbList, ArrayList<Uri> fullList, JSONArray arr_data) {
        this.thumbList = thumbList;
        this.context = context;
        this.fullList = fullList;
        this.arr_data =arr_data;
        Collections.sort(this.thumbList);
        Collections.sort(this.fullList);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String name = thumbList.get(i).getPath();
        name = name.substring(name.lastIndexOf("/") + 1);
        name = name.replace("_", " ");
        try{
            Bitmap myBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), thumbList.get(i));
            viewHolder.title.setText(name);
            viewHolder.img.setImageBitmap(myBitmap);}
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void arrChanged(JSONArray arr){
        arr_data = new JSONArray();
        arr_data = arr;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return thumbList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView img;
        private static final int FULL_RESULT = 54;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            img = view.findViewById(R.id.img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullScreenActivity.class);
                    intent.putExtra("filepath", fullList.get(getLayoutPosition()));
                    intent.putExtra("hpath", thumbList.get(getLayoutPosition()));
                    String name = thumbList.get(getLayoutPosition()).getPath();
                    name = name.substring(name.lastIndexOf("/") + 1);
                    String type = name.substring(0, name.lastIndexOf("_")+1);
                    name = name.substring(type.length(), name.lastIndexOf("."));
                    try {
                        Data tempData = new Data();
                        for (int i = 0; i < arr_data.length(); i++) {
                            String[] separated = arr_data.getString(i).split(",");
                            if(separated[3].equals("null"))separated[3]="";
                            tempData = new Data(separated[0], separated[1], Integer.parseInt(separated[2]), separated[3], separated[4]);
                            if (name.equals(tempData.getName()) && type.equals(tempData.getCat())) {
                                i = arr_data.length();
                            }
                        }
                        intent.putExtra("data", tempData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ((GalleryActivity)context).startActivityForResult(intent, FULL_RESULT );

                }
            });
        }
    }
}