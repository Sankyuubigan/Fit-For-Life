package com.nilden.fitforlife.pedometer

import android.app.Service
import android.speech.tts.TextToSpeech
import android.text.format.Time
import android.util.Log
import java.util.*

class Utils private constructor() : TextToSpeech.OnInitListener {
    private var mService: Service? = null

    private var mTts: TextToSpeech? = null
    var isSpeakingEnabled = false
        private set
    private var mSpeakingEngineAvailable = false

    val isSpeakingNow: Boolean
        get() = mTts!!.isSpeaking

    fun setService(service: Service) {
        mService = service
    }

    fun initTTS() {
        // Initialize text-to-speech. This is an asynchronous operation.
        // The OnInitListener (second argument) is called after initialization completes.
        Log.i(TAG, "Initializing TextToSpeech...")
        mTts = TextToSpeech(mService,
                this  // TextToSpeech.OnInitListener
        )
    }

    fun shutdownTTS() {
        Log.i(TAG, "Shutting Down TextToSpeech...")

        mSpeakingEngineAvailable = false
        mTts!!.shutdown()
        Log.i(TAG, "TextToSpeech Shut Down.")

    }

    fun say(text: String) {
        if (isSpeakingEnabled && mSpeakingEngineAvailable) {
            mTts!!.speak(text,
                    TextToSpeech.QUEUE_ADD, // Drop all pending entries in the playback queue.
                    null)
        }
    }

    // Implements TextToSpeech.OnInitListener.
    override fun onInit(status: Int) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            val result = mTts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.")
            } else {
                Log.i(TAG, "TextToSpeech Initialized.")
                mSpeakingEngineAvailable = true
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.")
        }
    }

    fun setSpeak(speak: Boolean) {
        isSpeakingEnabled = speak
    }

    fun ding() {}

    companion object {
        private val TAG = "Utils"

        private var instance: Utils? = null

        fun getInstance(): Utils {
            if (instance == null) {
                instance = Utils()
            }
            return instance as Utils
        }

        /********** Time  */

        fun currentTimeInMillis(): Long {
            val time = Time()
            time.setToNow()
            return time.toMillis(false)
        }
    }
}
