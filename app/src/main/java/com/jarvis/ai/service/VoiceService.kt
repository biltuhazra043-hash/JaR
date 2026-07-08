package com.jarvis.ai.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.jarvis.ai.JarvisApp
import com.jarvis.ai.R
import com.jarvis.ai.ui.MainActivity
import com.jarvis.ai.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

@AndroidEntryPoint
class VoiceService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var isListening = false
    private var isSpeaking = false

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState

    private val _speechResult = MutableSharedFlow<String>(replay = 0)
    val speechResult: SharedFlow<String> = _speechResult

    private val _ttsState = MutableStateFlow(TtsState.READY)
    val ttsState: StateFlow<TtsState> = _ttsState

    private var currentLanguage = Locale.ENGLISH

    enum class VoiceState { IDLE, LISTENING, PROCESSING, SPEAKING }
    enum class TtsState { INITIALIZING, READY, SPEAKING, ERROR }

    override fun onCreate() {
        super.onCreate()
        initTts()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_START_LISTENING -> startListening()
            Constants.ACTION_STOP_LISTENING -> stopListening()
        }

        startForeground(Constants.NOTIFICATION_ID_VOICE, createNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, JarvisApp.CHANNEL_VOICE)
            .setContentTitle("Jarvis")
            .setContentText("Standing by...")
            .setSmallIcon(R.drawable.ic_arc_reactor)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun initTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let {
                    it.setLanguage(currentLanguage)
                    it.setSpeechRate(1.0f)
                    it.setPitch(0.9f)
                    _ttsState.value = TtsState.READY
                }
            } else {
                _ttsState.value = TtsState.ERROR
            }
        }
    }

    fun speak(text: String) {
        if (tts == null || _ttsState.value != TtsState.READY) return

        _voiceState.value = VoiceState.SPEAKING
        _ttsState.value = TtsState.SPEAKING
        isSpeaking = true

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_speech_${System.currentTimeMillis()}")

        // Listen for completion
        tts?.setOnUtteranceProgressListener(object : android.speech.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                isSpeaking = false
                _ttsState.value = TtsState.READY
                _voiceState.value = VoiceState.IDLE
            }
            override fun onError(utteranceId: String?) {
                isSpeaking = false
                _ttsState.value = TtsState.READY
                _voiceState.value = VoiceState.IDLE
            }
        })
    }

    fun stopSpeaking() {
        tts?.stop()
        isSpeaking = false
        _ttsState.value = TtsState.READY
        _voiceState.value = VoiceState.IDLE
    }

    fun setLanguage(locale: Locale) {
        currentLanguage = locale
        tts?.setLanguage(locale)
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }

    fun startListening() {
        if (isListening) return
        _voiceState.value = VoiceState.LISTENING
        isListening = true

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(createRecognitionListener())
            startListening(createRecognitionIntent())
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
        _voiceState.value = VoiceState.IDLE
    }

    private fun createRecognitionIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage.language)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
                _voiceState.value = VoiceState.PROCESSING
            }
            override fun onError(error: Int) {
                isListening = false
                _voiceState.value = VoiceState.IDLE
                // Auto-restart listening if it wasn't a critical error
                if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    scope.launch {
                        delay(500)
                        startListening()
                    }
                }
            }
            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    scope.launch {
                        _speechResult.emit(text)
                        // Check for wake word
                        if (text.lowercase().contains(Constants.WAKE_WORD)) {
                            _voiceState.value = VoiceState.LISTENING
                        }
                    }
                }
                _voiceState.value = VoiceState.IDLE
            }
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        }
    }

    fun isSpeaking(): Boolean = isSpeaking

    override fun onDestroy() {
        scope.cancel()
        speechRecognizer?.destroy()
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
