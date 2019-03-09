
package com.nilden.fitforlife.pedometer

import android.os.Bundle
import android.preference.PreferenceActivity
import com.nilden.fitforlife.R

class Settings : PreferenceActivity() {
    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)
    }
}
