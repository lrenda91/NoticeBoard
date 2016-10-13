package it.polito.mad.noticeboard.db.controls;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.controls.FillOnScrollListView;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;
import it.polito.mad.noticeboard.student.NoticeDetailsActivity;

/**
 * Created by luigi on 03/06/15.
 */
public class AdvNoticesListView extends FillOnScrollListView<Notice> {

    private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public AdvNoticesListView(Context context){
        super(context);
    }

    public AdvNoticesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvNoticesListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View getView(final Notice item, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_offers_item, parent, false);
        }
        ImageView photo = (ImageView) convertView.findViewById(R.id.preferred_photo);
        try{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(item.getPhotos().get(0).getData());
            byte[] data = stream.toByteArray();
            photo.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
        catch(Throwable t){
            photo.setImageResource(R.drawable.ic_image);
        }
        /*if (item.getReports() > 0){
            ((TextView) convertView.findViewById(R.id.flags_number)).setText(String.valueOf(item.getReports()));
        }*/
        ((TextView) convertView.findViewById(R.id.offer_title)).setText(item.getTitle());
        ((TextView) convertView.findViewById(R.id.offer_price)).setText(item.getCost()+" "+
            getContext().getResources().getString(R.string.euros));
        ((TextView) convertView.findViewById(R.id.offer_location)).setText(item.getLocationName());
        ((TextView) convertView.findViewById(R.id.offer_date)).setText(df.format(item.getPublishedAt()));

        return convertView;
    }

    @Override
    protected int getItemsPerDownload() {
        return 10;
    }

    @Override
    protected ParseQuery<Notice> getQuery() {
        return ParseQuery.getQuery(Notice.class);
    }

}
