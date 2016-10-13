package it.polito.mad.noticeboard.advertiser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


import com.parse.ParseException;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.controls.AdvNoticesListView;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db.controls.MyNoticesListView;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

/**
 * Created by luigi on 01/06/15.
 */
public class AdvertiserNoticesListFragment extends Fragment {

    private ViewGroup listContainer;
    private MyNoticesListView listView;

    public AdvertiserNoticesListFragment(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_offers_list, container, false);
        listContainer = (ViewGroup) root.findViewById(R.id.list_container);
        listView = new MyNoticesListView(getActivity());
        listContainer.addView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Notice notice = listView.getData().get(position);
                final String[] items = getResources().getStringArray(R.array.notice_menu_items);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Notice");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals(getResources().getString(R.string.edit))) {
                            Intent toDetailsAct = new Intent(getActivity(), NoticeDetailsActivity.class);
                            PNoticeData noticeData = new PNoticeData();
                            noticeData.fillFrom(notice);
                            toDetailsAct.putExtra("noticeData", noticeData);
                            startActivity(toDetailsAct);
                        } else if (items[item].equals(getResources().getString(R.string.remove))) {
                            NoticeBoardDB.deleteNotice(notice, new DBCallbacks.DeleteCallback<Notice>() {
                                @Override
                                public void onDeleteSuccess() {
                                    listView.getData().remove(notice);
                                    listView.getAdapter().notifyDataSetChanged();
                                }

                                @Override
                                public void onDeleteError(ParseException exception) {

                                }
                            });
                        } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
        return root;
    }

    public void refreshList(){
        if (listView != null) {
            listView.refresh();
        }
    }

    /*
    public void notifyReports(List<Report> reports){
        List<Notice> notices = listView.getData();
        for (Report report : reports){
            for (Notice n : notices){
                if (report.getNotice().getObjectId().equals(n.getObjectId())){
                    n.increaseReports();
                }
            }
        }
        listView.refresh();
    }
*/
}
