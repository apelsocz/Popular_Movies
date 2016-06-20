package com.pelsoczi.popularmovies.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.pelsoczi.popularmovies.R;
import com.pelsoczi.popularmovies.models.Movie;
import com.pelsoczi.popularmovies.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewDialog extends DialogFragment {

    public static final String TAG = ReviewDialog.class.getSimpleName();

    private static final String MOVIE = "movie";
    private static final String REVIEWS = "reviews";

    private List<Review> reviews;
    private Movie movie;

    static ReviewDialog newInstance(List<Review> reviews, Movie movie) {
        ReviewDialog dialog = new ReviewDialog();

        Bundle args = new Bundle();
        args.putParcelable(MOVIE, movie);
        args.putParcelableArrayList(REVIEWS, (ArrayList) reviews);
        dialog.setArguments(args);

        return dialog;
    }

    public ReviewDialog() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        reviews = getArguments().getParcelableArrayList(REVIEWS);

        ArrayList<String> list = new ArrayList<String>();
        for (Review review : reviews) {
            list.add("\n-------- " + review.getAuthor() + " --------\n"
                    + review.getContent() + "\n");
        }
        CharSequence[] items = list.toArray(new CharSequence[list.size()]);

        movie = getArguments().getParcelable(MOVIE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(String.valueOf(list.size() + " Reviews"))
                .setItems(items, null)
                .setPositiveButton(R.string.dialog_review_label_neutral, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}