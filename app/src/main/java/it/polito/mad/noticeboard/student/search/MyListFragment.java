package it.polito.mad.noticeboard.student.search;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;
import it.polito.mad.noticeboard.student.NoticeDetailsActivity;

/**
 * Created by luigi on 01/06/15.
 */
public class MyListFragment extends ListFragment
        implements NoticesListener {

    private List<PNoticeData> data = new LinkedList<>();

    public MyListFragment(){
        setListAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public PNoticeData getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity())
                            .inflate(R.layout.layout_offers_item, parent, false);
                }
                PNoticeData item = getItem(position);
                ImageView photo = (ImageView) convertView.findViewById(R.id.preferred_photo);
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    stream.write(item.getPhotos().get(0).getData());
                    byte[] data = stream.toByteArray();
                    photo.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                } catch (Throwable t) {
                    photo.setImageResource(R.drawable.ic_edit);
                }
                ((TextView) convertView.findViewById(R.id.offer_title)).setText(item.getTitle());
                ((TextView) convertView.findViewById(R.id.offer_price)).setText(item.getCost() + " " +
                        getActivity().getResources().getString(R.string.euros));
                ((TextView) convertView.findViewById(R.id.offer_location)).setText(item.getLocation());
                ((TextView) convertView.findViewById(R.id.offer_date)).setText(
                        new SimpleDateFormat("dd/MM/yyyy").format(item.getPublishedAt()));

                return convertView;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnScrollListener(new MyScroller());
    }

    @Override
    public void update(List<PNoticeData> notices) {
        data = notices;
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
    }





    private class MyScroller implements AbsListView.OnScrollListener {
        private int priorFirstVisible = -1;
        @Override
        public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
            if (totalCount == 0){
                return;
            }
            if (firstVisible == priorFirstVisible){
                return;
            }
            priorFirstVisible = firstVisible;
            boolean mustLoadMore = firstVisible + visibleCount >= data.size();
            if (mustLoadMore && getActivity() instanceof UpdateRequestReceiver){
                ((UpdateRequestReceiver) getActivity()).onUpdateRequestReceived();
            }
        }
        @Override
        public void onScrollStateChanged(AbsListView v, int s) { }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), NoticeDetailsActivity.class);
        i.putExtra("noticeData", data.get(position));
        getActivity().startActivity(i);
    }

}
