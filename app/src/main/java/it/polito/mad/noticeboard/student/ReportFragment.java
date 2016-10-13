package it.polito.mad.noticeboard.student;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.List;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.Advertiser;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

/**
 * Created by luigi on 09/06/15.
 */
public class ReportFragment extends Fragment implements DBCallbacks.NoticeFlagCallback {

    private RadioGroup group;
    private EditText reporterEmailEditText, reporterDetails;
    private Button confirmButton;
    private ProgressBar waitPB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_report_notice, container, false);
        group = (RadioGroup) root.findViewById(R.id.problem_type_radio_group);
        reporterEmailEditText = (EditText) root.findViewById(R.id.reporter_mail_edit_text);
        reporterDetails = (EditText) root.findViewById(R.id.report_details_edit_text);
        confirmButton = (Button) root.findViewById(R.id.flag_confirm_button);
        waitPB = (ProgressBar) root.findViewById(R.id.wait);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final PNoticeData noticeData = ((NoticeDetailsActivity) getActivity()).noticeData;
        confirmButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (reporterEmailEditText.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(), "Please type email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        RadioButton rb = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
                        String reason = rb.getText().toString();
                        String reporterEmail = reporterEmailEditText.getText().toString();
                        String details = reporterDetails.getText().toString();
                        NoticeBoardDB.flagNoticeAsInadequate(reason, reporterEmail, details,
                                noticeData.getObjID(), ReportFragment.this);
                        confirmButton.setEnabled(false);
                        waitPB.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    @Override
    public void onFlagSuccess() {
        confirmButton.setEnabled(true);
        waitPB.setVisibility(View.INVISIBLE);
        Toast.makeText(getActivity(), "Report successfully published.\n" +
                "The advertiser will be norified", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFlagError(ParseException exception) {

    }
}
