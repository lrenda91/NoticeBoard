package it.polito.mad.noticeboard.advertiser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import android.widget.AdapterView.OnItemLongClickListener;

import java.io.ByteArrayOutputStream;

import it.polito.mad.noticeboard.EditableFragment;
import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.advertiser.publish.AddNoticeActivity;
import it.polito.mad.noticeboard.db_parcel.PFileData;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

/**
 * Created by luigi on 09/06/15.
 */
public class GalleryFragment extends Fragment implements EditableFragment {

    private static int PHOTO_ID = 0;
    private PNoticeData noticeData;

    private View root;
    private GridView gallery;
    private ImageView photoZoom;

    private ImageButton addPhoto;

    private OnItemLongClickListener mLongPhotoListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notice_gallery_editable, container, false);

        gallery = (GridView) root.findViewById(R.id.gallery);
        gallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        photoZoom = (ImageView) root.findViewById(R.id.photo_zoom);
        addPhoto = (ImageButton) root.findViewById(R.id.add_photo_button);
        addPhoto.setOnClickListener(new View.OnClickListener() {
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


        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        noticeData = ((NoticeDetailsActivity) getActivity()).noticeData;
        if (!noticeData.getPhotos().isEmpty()) {
            PFileData firstPhoto = noticeData.getPhotos().get(0);
            photoZoom.setImageBitmap(BitmapFactory.decodeByteArray(firstPhoto.getData(), 0,
                    firstPhoto.getData().length));
        }


        gallery.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return noticeData.getPhotos().size();
            }

            @Override
            public PFileData getItem(int position) {
                return noticeData.getPhotos().get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity())
                            .inflate(R.layout.layout_gallery_item, parent, false);
                }
                PFileData item = getItem(position);
                byte[] imageBytes = item.getData();
                ImageView iv = (ImageView) convertView.findViewById(R.id.photo);
                iv.setImageBitmap(
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length)
                );
                ((TextView) convertView.findViewById(R.id.photo_name))
                        .setText(item.getName());

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoZoom.setImageDrawable(((ImageView) v).getDrawable());
                    }
                });

                return convertView;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddNoticeActivity.RESULT_OK) {
            byte[] byteArray = null;
            if (requestCode == 1) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            else if (requestCode == 2) {
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
                Bitmap thumbnail = BitmapFactory.decodeFile(imagePath, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            noticeData.newPhoto("photo_"+(++PHOTO_ID), byteArray);
            ((BaseAdapter) gallery.getAdapter()).notifyDataSetChanged();
        }
    }


    @Override
    public void passToEditMode(boolean editMode) {
        addPhoto.setVisibility(editMode ? View.VISIBLE : View.INVISIBLE);
        gallery.setOnItemLongClickListener(editMode ? mLongPhotoListener : null);

    }
}
