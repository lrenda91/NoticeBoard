package it.polito.mad.noticeboard.db;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.parse.*;

import it.polito.mad.noticeboard.db.DBCallbacks.*;
import it.polito.mad.noticeboard.db.DBCallbacks.DeleteCallback;
import it.polito.mad.noticeboard.db_parcel.PAdvertiserData;
import it.polito.mad.noticeboard.db_parcel.PFileData;
import it.polito.mad.noticeboard.db_parcel.PNoticeData;
import it.polito.mad.noticeboard.db_parcel.PStudentData;
import it.polito.mad.noticeboard.student.search.NoticesListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luigi on 29/05/15.
 */
public class NoticeBoardDB {

    /*
     * My application ID and key to connect to my personal Parse account
     */
    private static final String APPLICATION_ID = "D1XKWrxtqaFmHqR2r8zBktG8qov5GEdh7S3Er761";
    private static final String CLIENT_KEY = "RQ4suqmhVWpNfHy6lpOLQxgPtDmVmthRy0oxwGy7";

    public static final String OBJ_ID_KEY = "objectId";
    public static final String STUDENT_KEY = "student";
    public static final String ADVERTISER_KEY = "advertiser";

    private NoticeBoardDB() {
    }

    public static void initialize(Context context, Class<? extends ParseObject>... classesToRegister) {
        if (context == null) {
            throw new RuntimeException("context must be non null");
        }
        for (Class cl : classesToRegister) {
            ParseObject.registerSubclass(cl);
        }
        Parse.enableLocalDatastore(context);
        Parse.initialize(context, APPLICATION_ID, CLIENT_KEY);
    }

    public static Student getCurrentStudent(){
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null || !(user.get(STUDENT_KEY) instanceof Student)){
            throw new AssertionError("Current user is not a student");
        }
        return (Student) user.get(STUDENT_KEY);
    }

    public static Advertiser getCurrentAdvertiser(){
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null || !(user.get(ADVERTISER_KEY) instanceof Advertiser)){
            throw new AssertionError("Current user is not an advertiser");
        }
        return (Advertiser) user.get(ADVERTISER_KEY);
    }

    public static void signUpStudent(final String username, final String password,
                                     final PStudentData studentData,
                                     final StudentSignUpCallback listener) {
        final ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        if (studentData.getEmail() != null){
            newUser.setEmail(studentData.getEmail());
        }
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) {
                        listener.onStudentSignUpException(e);
                    }
                    return;
                }
                final Student student = studentData.build();
                student.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            if (listener != null) {
                                listener.onStudentSignUpException(e);
                            }
                            return;
                        }
                        newUser.put(STUDENT_KEY, student);
                        newUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    if (listener != null) {
                                        listener.onStudentSignUpException(e);
                                    }
                                    return;
                                }
                                listener.onStudentSignUpSuccess(student);
                            }
                        });
                    }
                });
            }
        });
    }

    public static void signUpAdvertiser(final String username, final String password,
                                        final PAdvertiserData advData,
                                        final AdvertiserSignUpCallback listener) {
        final ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        if (advData.geteMail() != null){
            newUser.setEmail(advData.geteMail());
        }
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) {
                        listener.onAdvertiserSignUpError(e);
                    }
                    return;
                }
                final Advertiser homeOwner = advData.build();
                homeOwner.setEMail(advData.geteMail());
                homeOwner.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            if (listener != null) {
                                listener.onAdvertiserSignUpError(e);
                            }
                            return;
                        }
                        newUser.put(ADVERTISER_KEY, homeOwner);
                        newUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    if (listener != null) {
                                        listener.onAdvertiserSignUpError(e);
                                    }
                                    return;
                                }
                                listener.onAdvertiserSignUpSuccess(homeOwner);
                            }
                        });
                    }
                });
            }
        });
    }

    public static void downloadNewNoticeChunk(int size, int offset,
                                              final DownloadCallback<Notice> listener){
        ParseQuery<Notice> query = ParseQuery.getQuery(Notice.class);
        query.setLimit(size);
        query.setSkip(offset);
        query.findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onDownloadError();
                    return;
                }
                if (listener != null) listener.onNewDataReturned(list);
            }
        });
    }


    public static ParseObject tryLocalLogin() {
        ParseObject po = null;
        ParseUser current = ParseUser.getCurrentUser();
        if (current == null) {
            return null;
        }
        if (current.get(STUDENT_KEY) != null){
            po = (Student) current.get(STUDENT_KEY);
        }
        else if (current.get(ADVERTISER_KEY) != null){
            po = (Advertiser) current.get(ADVERTISER_KEY);
        }
        else {
            return null;
        }
        try {
            po.fetchFromLocalDatastore();
        } catch (ParseException e) {
            return null;
        }
        return po;
    }

    public static void remoteLogIn(final String username, final String password,
                                   final UserLoginCallback listener) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser == null && listener != null) {
                    listener.onLoginError(e);
                    return;
                }
                ParseObject relatedUser;
                if (parseUser.get(STUDENT_KEY) != null) {
                    relatedUser = (Student) parseUser.get(STUDENT_KEY);
                } else if (parseUser.get(ADVERTISER_KEY) != null) {
                    relatedUser = (Advertiser) parseUser.get(ADVERTISER_KEY);
                } else {
                    if (listener != null) {
                        listener.onLoginError(
                                new ParseException(-1, "Neither student nor homeowner associated"));
                    }
                    return;
                }

                try {
                    relatedUser.fetchIfNeeded();
                    /*String k = (relatedUser instanceof Student) ? "photo" : "logo";
                    ParseFile f = (ParseFile) relatedUser.get(k);
                    if (f != null) {
                        f.save();
                    }
                    */
                    relatedUser.pin();
                } catch (ParseException ex) {
                    if (listener != null) {
                        listener.onLoginError(ex);
                    }
                    return;
                }
                if (listener != null) {
                    if (relatedUser instanceof Student) {
                        listener.onStudentLoginSuccess((Student) relatedUser);
                    } else {
                        listener.onAdvertiserLoginSuccess((Advertiser) relatedUser);
                    }
                }
            }
        });
    }

    public static void logOut(final UserLogoutCallback listener) {
        ParseObject relatedUser;
        ParseUser current = ParseUser.getCurrentUser();
        if (current == null){
            if (listener != null){
                listener.onLogoutError(new ParseException(-1, "No current parse user"));
            }
            return;
        }
        if (current.get(STUDENT_KEY) != null){
            relatedUser = (Student) current.get(STUDENT_KEY);
        }
        else if (current.get(ADVERTISER_KEY) != null){
            relatedUser = (Advertiser) current.get(ADVERTISER_KEY);
        }
        else {
            if (listener != null){
                listener.onLogoutError(new ParseException(-1, "No current parse user"));
            }
            return;
        }
        try {
            relatedUser.unpin();
        }
        catch (ParseException e) {
            if (listener != null) {
                listener.onLogoutError(e);
            }
        }
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (listener != null) {
                    if (e != null) {
                        listener.onLogoutError(e);
                    } else {
                        listener.onLogoutSuccess();
                    }
                }
            }
        });
    }

    public static void updateStudentData(PStudentData data,
                                         final UpdateCallback<Student> listener){
        ParseObject po = (ParseObject) ParseUser.getCurrentUser().get(STUDENT_KEY);
        if (po == null || !(po instanceof Student)) throw new AssertionError();
        final Student currentStudent = (Student) po;
        if (data.getFirstName() != null) currentStudent.setFirstName(data.getFirstName());
        if (data.getLastName() != null) currentStudent.setLastName(data.getLastName());
        if (data.getEmail() != null) currentStudent.setEMail(data.getEmail());
        currentStudent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) {
                        listener.onUpdateError(e);
                    }
                    return;
                }
                if (listener != null) {
                    listener.onUpdateSuccess(currentStudent);
                }
            }
        });
    }

    public static void updateHomeownerData(PAdvertiserData data,
                                         final UpdateCallback<Advertiser> listener){
        ParseObject po = (ParseObject) ParseUser.getCurrentUser().get(ADVERTISER_KEY);
        if (po == null || !(po instanceof Advertiser)) throw new AssertionError();
        final Advertiser curAdvertiser = (Advertiser) po;
        if (data.getFirstName() != null) curAdvertiser.setFirstName(data.getFirstName());
        if (data.getLastName() != null) curAdvertiser.setLastName(data.getLastName());
        if (data.geteMail() != null) curAdvertiser.setEMail(data.geteMail());
        if (data.getPhoneNumber() != null) curAdvertiser.setPhoneNumber(data.getPhoneNumber());
        curAdvertiser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) {
                        listener.onUpdateError(e);
                    }
                    return;
                }
                if (listener != null) {
                    listener.onUpdateSuccess(curAdvertiser);
                }
            }
        });
    }

    public static void updateNoticeData(final PNoticeData data,
                                           final UpdateCallback<Notice> listener){
        //first, get Notice object
        ParseQuery.getQuery(Notice.class).getInBackground(data.getObjID(), new GetCallback<Notice>() {
            @Override
            public void done(final Notice notice, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onUpdateError(e);
                    return;
                }
                //then, update elementary fields
                if (data.getTitle() != null) notice.setTitle(data.getTitle());
                if (data.getDescription() != null) notice.setDescription(data.getDescription());
                notice.setSize(data.getSize());
                notice.setCost(data.getCost());
                if (data.getPropertyType() != null) notice.setPropertyType(data.getPropertyType());
                if (data.getContractType() != null) notice.setContractType(data.getContractType());
                if (data.getLocation() != null) notice.setLocationName(data.getLocation());
                notice.setLocationPoint(new ParseGeoPoint(data.getLatitude(), data.getLongitude()));
                if (data.getAvailableFrom() != null)
                    notice.setAvailableFrom(data.getAvailableFrom());
                notice.addTags(data.getTags());

                //cancel all previous files
                notice.getPhotos().clear();
                notice.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            if (listener != null) listener.onUpdateError(e);
                            return;
                        }
                        //now, asynchronously save all actual photo files
                        new AsyncTask<Void, Void, ParseException>() {
                            @Override
                            protected ParseException doInBackground(Void[] params) {
                                List<ParseFile> photoFiles = new LinkedList<>();
                                for (PFileData fileData : data.getPhotos()) {
                                    ParseFile file = fileData.build();
                                    try {
                                        file.save();
                                    } catch (ParseException e) {
                                        Log.e("pe", e.getMessage());
                                        return e;
                                    }
                                    photoFiles.add(file);
                                }
                                notice.addPhotos(photoFiles);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(ParseException e) {
                                super.onPostExecute(e);
                                if (e == null) {
                                    notice.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                if (listener != null) listener.onUpdateError(e);
                                                return;
                                            }
                                            if (listener != null) listener.onUpdateSuccess(notice);
                                        }
                                    });
                                }
                            }
                        }.execute();
                    }
                });

            }
        });
    }

    public static void publishNewNotice(final PNoticeData data,
                                        final UpdateCallback<Notice> listener){
        ParseObject po = (ParseObject) ParseUser.getCurrentUser().get(ADVERTISER_KEY);
        if (po == null || !(po instanceof Advertiser)) throw new AssertionError();
        final Advertiser currentHomeowner = (Advertiser) po;
        final Notice built = data.build();

        new AsyncTask<Void,Void,ParseException>() {
            @Override
            protected ParseException doInBackground(Void[] params) {
                List<ParseFile> photoFiles = new LinkedList<>();
                for (PFileData fileData : data.getPhotos()){
                    ParseFile file = fileData.build();
                    try{
                        file.save();
                    }catch(ParseException e){
                        Log.e("pe",e.getMessage());
                        return e;
                    }
                    photoFiles.add(file);
                }
                built.addPhotos(photoFiles);
                return null;
            }
            @Override
            protected void onPostExecute(ParseException e) {
                super.onPostExecute(e);
                if (e == null){
                    built.setOwner(currentHomeowner);
                    built.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                if (listener != null) listener.onUpdateError(e);
                                return;
                            }
                            currentHomeowner.addNotice(built);
                            currentHomeowner.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null){
                                        if (listener != null) listener.onUpdateError(e);
                                        return;
                                    }
                                    if (listener != null) listener.onUpdateSuccess(built);
                                }
                            });
                        }
                    });
                }
            }
        }.execute();
    }

    public static void deleteNotice(final Notice notice, final DeleteCallback<Notice> listener){
        notice.deleteInBackground(new com.parse.DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onDeleteError(e);
                    return;
                }
                ParseQuery<Report> q = ParseQuery.getQuery(Report.class);
                q.whereEqualTo(Report.NOTICE, notice);
                q.findInBackground(new FindCallback<Report>() {
                    @Override
                    public void done(List<Report> list, ParseException e) {
                        if (e != null) {
                            if (listener != null) listener.onDeleteError(e);
                            return;
                        }
                        ParseObject.deleteAllInBackground(list, new com.parse.DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    if (listener != null) listener.onDeleteError(e);
                                    return;
                                }
                                if (listener != null) listener.onDeleteSuccess();
                            }
                        });
                    }
                });
            }
        });
    }
/*
    public static ParseQuery<Notice> getNoticeAdvancedQuery(final Notice.FilterData filterData){
        ParseQuery<Notice> query = ParseQuery.getQuery(Notice.class);
        ParseQuery<Notice> finalQuery = null;
        if (filterData != null) {
            query.whereLessThanOrEqualTo(Notice.PRICE, filterData.maxPrice)
                    .whereGreaterThanOrEqualTo(Notice.PRICE, filterData.minPrice)
                    .whereLessThanOrEqualTo(Notice.SIZE, filterData.maxSize)
                    .whereGreaterThanOrEqualTo(Notice.SIZE, filterData.minSize);

            if (filterData.location != null) {
                query.whereContains(Notice.LOCATION_POINT, filterData.location);
            }
            if (filterData.contractType != null) {
                query.whereEqualTo(Notice.CONTRACT_TYPE, filterData.contractType);
            }
            if (filterData.propertyType != null) {
                query.whereEqualTo(Notice.PROPERTY_TYPE, filterData.propertyType);
            }
            List<ParseQuery<Notice>> res = new LinkedList<>();
            res.add(query);
            for (String tag : filterData.tags) {
                List<ParseQuery<Notice>> queries = new LinkedList<>();
                queries.add(ParseQuery.getQuery(Notice.class).whereContains(Notice.TITLE, tag));
                queries.add(ParseQuery.getQuery(Notice.class).whereContains(Notice.DESCRIPTION, tag));
                ParseQuery<Notice> union = ParseQuery.or(queries);
                res.add(union);
            }
            finalQuery = ParseQuery.or(res);
        }
        else{
            finalQuery = query;
        }
        return finalQuery;
    }
*/

    public static void simpleNoticeFilter(String title, String locationName,
                                          Double latitude, Double longitude,
                                          final FilterCallback<Notice> listener){
        Notice.FilterData fiterData = new Notice.FilterData();
        fiterData.title(title)
                //.location(locationName)
                .latitude(latitude)
                .longitude(longitude);
        advancedNoticeFilter(fiterData, listener);
    }

    public static void advancedNoticeFilter(final Notice.FilterData filterData,
                                            final FilterCallback<Notice> listener){
        ParseQuery<Notice> query = ParseQuery.getQuery(Notice.class);
        ParseQuery<Notice> finalQuery = null;
        if (filterData != null) {
            query.whereLessThanOrEqualTo(Notice.PRICE, filterData.maxPrice)
                    .whereGreaterThanOrEqualTo(Notice.PRICE, filterData.minPrice)
                    .whereLessThanOrEqualTo(Notice.SIZE, filterData.maxSize)
                    .whereGreaterThanOrEqualTo(Notice.SIZE, filterData.minSize);

            if (filterData.title != null){
                query.whereContains(Notice.TITLE, filterData.title);
            }
            if (filterData.location != null) {
                query.whereContains(Notice.LOCATION_STRING, filterData.location);
            }
            if (filterData.latitude != null && filterData.longitude != null){
                ParseGeoPoint point = new ParseGeoPoint(filterData.latitude, filterData.longitude);
                query.whereWithinKilometers(Notice.LOCATION_POINT, point, filterData.within);
            }
            if (filterData.contractType != null) {
                query.whereEqualTo(Notice.CONTRACT_TYPE, filterData.contractType);
            }
            if (filterData.propertyType != null) {
                query.whereEqualTo(Notice.PROPERTY_TYPE, filterData.propertyType);
            }
            if (filterData.tags != null && !filterData.tags.isEmpty()) {
                query.whereContainsAll(Notice.TAGS, filterData.tags);
            }
            List<ParseQuery<Notice>> res = new LinkedList<>();
            res.add(query);
            for (String tag : filterData.tags) {
                List<ParseQuery<Notice>> queries = new LinkedList<>();
                queries.add(ParseQuery.getQuery(Notice.class).whereContains(Notice.TITLE, tag));
                queries.add(ParseQuery.getQuery(Notice.class).whereContains(Notice.DESCRIPTION, tag));
                ParseQuery<Notice> union = ParseQuery.or(queries);
                res.add(union);
            }
            finalQuery = ParseQuery.or(res);
        }
        else{
            finalQuery = query;
        }
        finalQuery.findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFilterError(e);
                    return;
                }
                if (listener != null) listener.onDataFiltered(list);
            }
        });
    }

    public static void flagNoticeAsInadequate(String reason, String email, String details,
                                              String noticeID, final NoticeFlagCallback listener){
        final Report report = new Report();
        report.setReason(reason);
        report.setReporterEmail(email);
        report.setDetails(details);
        report.setDisplayed(false);
        ParseQuery.getQuery(Notice.class).getInBackground(noticeID, new GetCallback<Notice>() {
            @Override
            public void done(Notice notice, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFlagError(e);
                    return;
                }
                report.setNotice(notice);
                report.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            if (listener != null) listener.onFlagError(e);
                            return;
                        }
                        if (listener != null) listener.onFlagSuccess();
                    }
                });
            }
        });
    }

    public static void sortNotices(final List<Notice.SortParam> criteria,
                                   final SortCallback<Notice> listener){
        ParseQuery<Notice> query = ParseQuery.getQuery(Notice.class);
        for (Notice.SortParam criteriaHolder : criteria){
            if (criteriaHolder.consider){
                query.addAscendingOrder(criteriaHolder.key);
            }
        }
        query.findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onSortError(e);
                    return;
                }
                if (listener != null) listener.done(list);
            }
        });
    }

    public static void fetchAdvertiser(PNoticeData noticeData,
                                       final SingleFetchCallback<Advertiser> listener){
        ParseQuery.getQuery(Notice.class).getInBackground(noticeData.getObjID(),
                new GetCallback<Notice>() {
                    @Override
                    public void done(Notice notice, ParseException e) {
                        if (e != null) {
                            if (listener != null) listener.onFetchError(e);
                            return;
                        }
                        notice.getOwner().fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e != null || !(parseObject instanceof Advertiser)) {
                                    if (listener != null) listener.onFetchError(e);
                                    return;
                                }
                                if (listener != null)
                                    listener.onFetchSuccess((Advertiser) parseObject);
                            }
                        });
                    }
                });
    }

    public static void lookForNewReport(final MultipleFetchCallback<Report> listener){
        ParseObject po = (ParseObject) ParseUser.getCurrentUser().get(ADVERTISER_KEY);
        if (po == null || !(po instanceof Advertiser)) throw new AssertionError();
        final Advertiser currAdvertiser = (Advertiser) po;

        ParseQuery<Notice> myNoticesQ = ParseQuery.getQuery(Notice.class);
        myNoticesQ.whereEqualTo(Notice.OWNER, currAdvertiser);

        ParseQuery<Report> notDisplayedQ = ParseQuery.getQuery(Report.class);
        notDisplayedQ.whereEqualTo(Report.DISPLAYED, false);

        notDisplayedQ.whereMatchesKeyInQuery(Report.NOTICE, OBJ_ID_KEY, myNoticesQ);

        notDisplayedQ.findInBackground(new FindCallback<Report>() {
            @Override
            public void done(List<Report> list, ParseException e) {
                if (e != null) {
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (listener != null) listener.onFetchSuccess(list);
            }
        });
    }



    public static void getFavorite(final String noticeID, final SingleFetchCallback<Notice> listener){
        ParseObject po = (ParseObject) ParseUser.getCurrentUser().get(STUDENT_KEY);
        if (po == null || !(po instanceof Student)) throw new AssertionError();
        final Student currentStudent = (Student) po;
        ParseRelation<Notice> list = currentStudent.getFavorites();
        list.getQuery().findInBackground(new FindCallback<Notice>() {
            @Override
            public void done(List<Notice> list, ParseException e) {
                if (e != null){
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                for (Notice n : list){
                    if (n.getObjectId().equals(noticeID) && listener != null){
                        listener.onFetchSuccess(n);
                        return;
                    }
                }
                if (listener != null) listener.onFetchError(new ParseException(-1,"no data"));
            }
        });
        /*list.getQuery().getInBackground(noticeID, new GetCallback<Notice>() {
            @Override
            public void done(Notice notice, ParseException e) {
                if (e != null){
                    if (listener != null) listener.onFetchError(e);
                    return;
                }
                if (listener != null) listener.onFetchSuccess(notice);
            }
        });*/
    }


    public static void toggleFavoriteNotice(final String noticeID,
                                            final SetFavoriteCallback listener){
        ParseObject po = (ParseObject) ParseUser.getCurrentUser().get(STUDENT_KEY);
        if (po == null || !(po instanceof Student)) throw new AssertionError();
        final Student currentStudent = (Student) po;
        getFavorite(noticeID, new SingleFetchCallback<Notice>() {
            @Override
            public void onFetchSuccess(Notice result) {
                currentStudent.removeFavorite(result);
                currentStudent.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            if (listener != null) listener.onSetFavoriteError(e);
                            return;
                        }
                        if (listener != null) listener.onSetFavoriteSuccess(false);
                    }
                });
            }
            @Override
            public void onFetchError(ParseException exception) {
                ParseQuery.getQuery(Notice.class).getInBackground(noticeID, new GetCallback<Notice>() {
                    @Override
                    public void done(Notice notice, ParseException e) {
                        if (e != null){
                            if (listener != null) listener.onSetFavoriteError(e);
                            return;
                        }
                        currentStudent.addFavorite(notice);
                        currentStudent.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null){
                                    if (listener != null) listener.onSetFavoriteError(e);
                                    return;
                                }
                                if (listener != null) listener.onSetFavoriteSuccess(true);
                            }
                        });
                    }
                });
            }
        });
    }
}
