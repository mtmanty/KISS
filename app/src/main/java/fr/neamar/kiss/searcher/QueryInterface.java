package fr.neamar.kiss.searcher;

import android.app.DialogFragment;

import fr.neamar.kiss.ui.ListPopup;

public interface QueryInterface {
    void temporarilyDisableTranscriptMode();
    void updateTranscriptMode(int transcriptMode);

    void launchOccurred();

    void registerPopup(ListPopup popup);

    void showDialog(DialogFragment dialog);
}
