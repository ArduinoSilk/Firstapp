package com.arduino;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by ES29 on 3/5/2016.
 */
public class ImageDisplay extends Activity {
    private  int count;
    private Bitmap[] thumbnails;
    private String[] arrpath;
    private boolean[] thumnailselection;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_image);

        final String[] columns= {MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID};
        final String orderBy=MediaStore.Images.Media._ID;
        Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,columns,null,null,orderBy);
        int image_column_index=imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count= imageCursor.getCount();
        this.thumbnails= new Bitmap[this.count];
        this.arrpath=new String[this.count];
        this.thumnailselection=new boolean[this.count];

        for (int i=0;i< this.count;i++){
            imageCursor.moveToPosition(i);
            int id=imageCursor.getInt(image_column_index);
            int datacolumnindex=imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            thumbnails[i]=MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(),id,MediaStore.Images.Thumbnails.MICRO_KIND,null);
            arrpath[i]=imageCursor.getString(datacolumnindex);
        }
        GridView imagegrid=(GridView) findViewById(R.id.grdimgview);
        imageAdapter =new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
        imageCursor.close();


        final Button selectbtn= (Button) findViewById(R.id.load_picture);
        selectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int len =thumnailselection.length;
                int cnt =0;
                String selectImages="";
                for (int i=0;i<len;i++){
                    if (thumnailselection[i]){
                        cnt++;
                        selectImages =selectImages +arrpath[i] +"|";
                    }
                }
                if (cnt == 0){
                    Toast.makeText(getApplicationContext(),"Please select at least one image.",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"you have selected" +cnt+ "images.",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    public class ImageAdapter extends BaseAdapter{
        private LayoutInflater mInflater;

        public ImageAdapter(){
            mInflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView== null){
                holder= new ViewHolder();
                convertView=mInflater.inflate(R.layout.row_multiphoto_item,null);
                holder.imageView=(ImageView)convertView.findViewById(R.id.imgviewitem);
                holder.checkBox=(CheckBox)convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.checkBox.setId(position);
            holder.imageView.setId(position);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumnailselection[id]){
                        cb.setChecked(false);
                        thumnailselection[id] =false;
                    }else {
                        cb.setChecked(true);
                        thumnailselection[id]=true;
                    }
                }
            });

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id =v.getId();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + arrpath[id]),"image/*");
                    startActivity(intent);

                }
            });
            holder.imageView.setImageBitmap(thumbnails[position]);
            holder.checkBox.setChecked(thumnailselection[position]);
            holder.id=position;
            return convertView;
        }
    }
    class ViewHolder{
        ImageView imageView;
        CheckBox checkBox;
        int id;
    }

}