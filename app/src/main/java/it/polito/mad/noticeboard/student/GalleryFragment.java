package it.polito.mad.noticeboard.student;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db_parcel.PFileData;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

/**
 * Created by luigi on 09/06/15.
 */
public class GalleryFragment extends Fragment {

    private GridView gallery;
    private ImageView photoZoom;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notice_gallery_uneditable, container, false);
        gallery = (GridView) root.findViewById(R.id.gallery);
        photoZoom = (ImageView) root.findViewById(R.id.photo_zoom);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final PNoticeData noticeData = ((NoticeDetailsActivity) getActivity()).noticeData;

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
}
