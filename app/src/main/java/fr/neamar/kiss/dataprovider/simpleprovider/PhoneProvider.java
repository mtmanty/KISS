package fr.neamar.kiss.dataprovider.simpleprovider;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.regex.Pattern;

import fr.neamar.kiss.pojo.PhonePojo;
import fr.neamar.kiss.pojo.Pojo;
import fr.neamar.kiss.searcher.Searcher;

public class PhoneProvider extends SimpleProvider {
    private static final String PHONE_SCHEME = "phone://";
    private boolean deviceIsPhone;

    // See https://github.com/Neamar/KISS/issues/1137
    private Pattern phonePattern = Pattern.compile("^[*+0-9# ]{3,}$");

    public PhoneProvider(Context context) {
        PackageManager pm = context.getPackageManager();
        deviceIsPhone = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    @Override
    public void requestResults(String query, Searcher searcher) {
        // Append an item only if query looks like a phone number and device has phone capabilities
        if (deviceIsPhone && phonePattern.matcher(query).find()) {
            searcher.addResult(getResult(query, true));
        }
    }

    @Override
    public boolean mayFindById(String id) {
        return id.startsWith(PHONE_SCHEME);
    }

    public Pojo findById(String id) {
        return getResult(id.replaceFirst(Pattern.quote(PHONE_SCHEME), ""), false);
    }

    /**
     *
     * @param phoneNumber phone number to use in the result
     * @param fromSearch true when we're running a search, in which case we're guaranteed to only have one result. Since we don't want this result to be animated in the adapter, set a constant id.
     * @return a result that may have a fake id.
     */
    private Pojo getResult(String phoneNumber, boolean fromSearch) {
        String historyId = PHONE_SCHEME + phoneNumber;
        String id = fromSearch ? PHONE_SCHEME + "search" : historyId;
        PhonePojo pojo = new PhonePojo(id, historyId, phoneNumber);

        String phoneNumberAfterFirstCharacter = phoneNumber.substring(1);
        if(!phoneNumberAfterFirstCharacter.contains("*") && !phoneNumberAfterFirstCharacter.contains("+")) {
            // No * and no + (except maybe as a first character), likely to be a phone number and not a Calculator expression
            pojo.relevance = 20;
        }
        else {
            // Query may be a phone number or a calculator expression, more likely to be an expression
            // Calculator expressions have a relevance of 19, so use something lower
            pojo.relevance = 15;
        }
        pojo.setName(phoneNumber, false);
        return pojo;
    }
}
