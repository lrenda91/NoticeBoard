package it.polito.mad.noticeboard.advertiser.publish;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

public class Page2 extends Fragment implements NoticeUpdater {

    private GridView galleryView;
    private GridView tagsView;
    private Button chooseButton, newTagButton;
    private EditText newTagEditText;

    private List<Bitmap> imagesData = new ArrayList<>();
    private List<String> tagsData = new ArrayList<>();

    @Override
    public void update(PNoticeData data) {
        for (int i=0;i<imagesData.size();i++){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagesData.get(i).compress(Bitmap.CompressFormat.JPEG, 90, stream);
            data.newPhoto("photo_"+i, stream.toByteArray());
        }
        for (String tag : tagsData){
            data.newTag(tag);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_page2, container, false);
        chooseButton = (Button) root.findViewById(R.id.newPhotoButton);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Take Photo", "Choose fillFrom Library",
                        "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        } else if (items[item].equals("Choose fillFrom Library")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select File"),
                                    2);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        galleryView = (GridView) root.findViewById(R.id.gallery);
        galleryView.setAdapter(new GalleryAdapter());

        tagsView = (GridView) root.findViewById(R.id.tags_grid);
        tagsView.setAdapter(new TagsAdapter());
        newTagEditText = (EditText) root.findViewById(R.id.new_tag_edit_text);
        newTagButton = (Button) root.findViewById(R.id.new_tag_confirm);
        newTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedTag = newTagEditText.getText().toString();
                if (typedTag.isEmpty() || tagsData.contains(typedTag)) {
                    Toast.makeText(getActivity(), "Tag '" + typedTag + "' already set", Toast.LENGTH_SHORT).show();
                    return;
                }
                tagsData.add(newTagEditText.getText().toString());
                ((BaseAdapter) tagsView.getAdapter()).notifyDataSetChanged();
                newTagEditText.getText().clear();
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            outState.putStringArrayList("tags", new ArrayList<>(tagsData));
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            tagsData = savedInstanceState.getStringArrayList("tags");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddNoticeActivity.RESULT_OK) {
            if (requestCode == 1) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                /*File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                imagesData.add(thumbnail);
                ((BaseAdapter) galleryView.getAdapter()).notifyDataSetChanged();
                //mImageView.setImageBitmap(thumbnail);
            }else if (requestCode == 2) {
                // Let's read picked image data - its URI
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
                // Now we need to set the GUI ImageView data with data read fillFrom the picked file.
                imagesData.add(bm);
                ((BaseAdapter) galleryView.getAdapter()).notifyDataSetChanged();
                // At the end remember to close the cursor or you will end with the RuntimeException!
            }
        }
    }

    class TagsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return tagsData.size();
        }

        @Override
        public String getItem(int position) {
            return tagsData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.layout_tags_grid_item, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.tag_value)).setText("#"+getItem(position));
            return convertView;
        }
    }


    class GalleryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imagesData.size();
        }

        @Override
        public Bitmap getItem(int position) {
            return imagesData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = new ImageView(getActivity());
            }
            ((ImageView) convertView).setImageBitmap(getItem(position));
            return convertView;
        }
    }

}