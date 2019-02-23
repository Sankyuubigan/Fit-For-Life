package com.nilden.fitforlife.pedometer.preferences

import android.content.Context
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceManager
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.EditText

abstract class EditMeasurementPreference : EditTextPreference {
    internal var mIsMetric: Boolean = false

    protected var mTitleResource: Int = 0
    protected var mMetricUnitsResource: Int = 0
    protected var mImperialUnitsResource: Int = 0

    constructor(context: Context) : super(context) {
        initPreferenceDetails()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        initPreferenceDetails()
    }

    constructor(context: Context, attr: AttributeSet, defStyle: Int) : super(context, attr, defStyle) {
        initPreferenceDetails()
    }

    protected abstract fun initPreferenceDetails()

    override fun showDialog(state: Bundle?) {
        mIsMetric = PreferenceManager.getDefaultSharedPreferences(context).getString("units", "imperial") == "metric"
        dialogTitle = context.getString(mTitleResource) +
                " (" +
                context.getString(
                        if (mIsMetric)
                            mMetricUnitsResource
                        else
                            mImperialUnitsResource) +
                ")"

        try {
            java.lang.Float.valueOf(text)
        } catch (e: Exception) {
            text = "20"
        }

        super.showDialog(state)
    }

    override fun onAddEditTextToDialogView(dialogView: View, editText: EditText) {
        editText.setRawInputType(
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        super.onAddEditTextToDialogView(dialogView, editText)
    }

    public override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            try {
                java.lang.Float.valueOf((editText.text as CharSequence).toString())
            } catch (e: NumberFormatException) {
                this.showDialog(null)
                return
            }

        }
        super.onDialogClosed(positiveResult)
    }
}
