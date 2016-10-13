package it.polito.mad.noticeboard.student;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.List;
import java.util.ListIterator;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.Notice;
import it.polito.mad.noticeboard.db.NoticeBoardDB;

/**
 * Created by luigi on 15/06/15.
 */
public class SortDialog extends DialogFragment {

    public interface SortingCallback {
        void onSortingComplete(List<Notice> data);
    }

    private List<Notice.SortParam> params = Notice.getDefaultSortCriteria();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_sort_notices, container, false);
        Dialog dialog = getDialog();
        dialog.setTitle("Sort notices");
        ListView criteriaList = (ListView) root.findViewById(R.id.criteriaListView);
        criteriaList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return params.size();
            }

            @Override
            public Notice.SortParam getItem(int position) {
                return params.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity())
                            .inflate(R.layout.layout_sort_notices_criteria_item, parent, false);
                }
                final Notice.SortParam item = getItem(position);
                ((TextView) convertView.findViewById(R.id.criteria_name)).setText(item.key);
                ImageButton up = (ImageButton) convertView.findViewById(R.id.upButton);
                up.setEnabled(position > 0);
                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListIterator<Notice.SortParam> iterator = params.listIterator();
                        for (int i = 0; i <= position; i++) iterator.next();
                        iterator.remove();
                        iterator.previous();
                        iterator.add(item);
                        Toast.makeText(getActivity(), params.toString(), Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
                ImageButton down = (ImageButton) convertView.findViewById(R.id.downButton);
                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListIterator<Notice.SortParam> iterator = params.listIterator();
                        for (int i = 0; i <= position; i++) iterator.next();
                        iterator.remove();
                        iterator.next();
                        iterator.add(item);
                        Toast.makeText(getActivity(), params.toString(), Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
                down.setEnabled(position < (getCount() - 1));
                CheckBox cbox = (CheckBox) convertView.findViewById(R.id.criteria_select);
                cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.consider = isChecked;
                    }
                });
                cbox.setChecked(item.consider);
                return convertView;
            }
        });


        Button sort = (Button) root.findViewById(R.id.sort_button);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeBoardDB.sortNotices(params, new DBCallbacks.SortCallback<Notice>() {
                    @Override
                    public void done(List<Notice> result) {
                        if (getActivity() instanceof SortingCallback) {
                            ((SortingCallback) getActivity()).onSortingComplete(result);
                        }
                        dismiss();
                    }

                    @Override
                    public void onSortError(ParseException exception) {

                    }
                });
            }
        });
        return root;
    }

}
