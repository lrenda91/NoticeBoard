package it.polito.mad.noticeboard.student;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.List;

import it.polito.mad.noticeboard.R;
import it.polito.mad.noticeboard.db.DBCallbacks;
import it.polito.mad.noticeboard.db.Advertiser;
import it.polito.mad.noticeboard.db.NoticeBoardDB;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;

/**
 * Created by luigi on 09/06/15.
 */
public class NoticeInfosFragment extends Fragment
        implements DBCallbacks.SingleFetchCallback<Advertiser>, DBCallbacks.NoticeFlagCallback {

    private View cost, location, type, contract, advertiser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notice_infos_uneditable, container, false);
        cost = root.findViewById(R.id.cost_layout);
        location = root.findViewById(R.id.location_layout);
        type = root.findViewById(R.id.type_layout);
        contract = root.findViewById(R.id.contract_layout);
        advertiser = root.findViewById(R.id.advertiser_infos_layout);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final PNoticeData noticeData = ((NoticeDetailsActivity) getActivity()).noticeData;

        ((TextView) cost.findViewById(R.id.name)).setText("Cost");
        ((TextView) cost.findViewById(R.id.value)).setText(noticeData.getCost() + " EURO");

        ((TextView) location.findViewById(R.id.name)).setText("Location");
        ((TextView) location.findViewById(R.id.value)).setText(noticeData.getLocation());

        ((TextView) type.findViewById(R.id.name)).setText("Type");
        ((TextView) type.findViewById(R.id.value)).setText(noticeData.getPropertyType());

        ((TextView) contract.findViewById(R.id.name)).setText("Contract");
        ((TextView) contract.findViewById(R.id.value)).setText(noticeData.getContractType());

        NoticeBoardDB.fetchAdvertiser(noticeData, this);
    }

    @Override
    public void onFetchSuccess(final Advertiser adv) {
        String advName = adv.getFirstName()+" "+adv.getLastName();
        ((TextView) advertiser.findViewById(R.id.name_value)).setText(advName);

        TextView mailTV = (TextView) advertiser.findViewById(R.id.mail_value);
        mailTV.setText(adv.getEMail());
        mailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", adv.getEMail(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView phoneTV = (TextView) advertiser.findViewById(R.id.phone_value);
        phoneTV.setText(adv.getPhoneNumber());
        phoneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + adv.getPhoneNumber()));
                startActivity(callIntent);
            }
        });

    }

    @Override
    public void onFetchError(ParseException exception) {

    }

    @Override
    public void onFlagSuccess() {

    }

    @Override
    public void onFlagError(ParseException exception) {

    }
}
