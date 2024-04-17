package com.dictify.stt

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dictify.Language
import com.dictify.LanguageAdapter
import com.dictify.R
import com.dictify.databinding.FragmentSttBinding
import com.dictify.getLanguageList
import java.util.Locale

class SpeechFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentSttBinding? = null
    private val binding: FragmentSttBinding
        get() = _binding ?: throw Exception("Binding is null")
    private var sourceLanguage: String? = null
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var recognitionIntent: Intent

    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSttBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupLanguageOptions()
        binding.apply {

            btnSpeak. apply {
                setOnClickListener {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                setupSpeechRecognizer()
                startListening()}
            }
            btnStop.setOnClickListener{
                stopListening()
            }
            btnShare.setOnClickListener {
                shareText(etInputText.text.toString())
            }
            btnTranslate.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("text", etInputText.text.toString())
                }
                findNavController().navigate(R.id.action_translateFragment2_to_TTSFragment, bundle)

            }
        }
    }
    private fun shareText(content: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun setupLanguageOptions() {
        binding.apply {
            val languageList = getLanguageList()
            val adapter = LanguageAdapter(requireContext(), languageList)
            sourceLanguageSpinner.adapter = adapter
            sourceSpinnerListener(sourceLanguageSpinner, adapter)
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(speechRecognitionListener)
        recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        val supportedLanguages = arrayOf(sourceLanguage)
        recognitionIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            supportedLanguages.joinToString(",")
        )
        recognitionIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
    }

    private fun startListening() {
        try {
            binding.btnSpeak.apply {
                text = "Listening..."
                isEnabled = false
            }
            speechRecognizer.startListening(recognitionIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            binding.btnSpeak.apply {
                text = "Speak"
                isEnabled = true
            }
        }
    }

    private fun stopListening() {
        try {
            speechRecognizer.cancel()
            speechRecognizer.stopListening() // Stop listening
            binding.btnSpeak.apply {
                text = "Speak"
                isEnabled = true
            }
            val speakToast =
                Toast.makeText(requireActivity(), "Listening stopped", Toast.LENGTH_SHORT)
            speakToast.show()
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
    private fun sourceSpinnerListener(spinner: Spinner, adapter: ArrayAdapter<Language>) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = adapter.getItem(position)
                selectedLanguage?.let {
                    sourceLanguage = it.code
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the language for text-to-speech
            val result = textToSpeech.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle language not supported
                // You might want to notify the user or choose a different language
            }
        } else {
            // Handle initialization failure
            // You might want to notify the user or take appropriate action
        }
    }

    private fun speakText(text: String) {
        if (text == "") {
            val speakToast =
                Toast.makeText(requireActivity(), "Your text field is empty", Toast.LENGTH_SHORT)
            speakToast.show()
        } else
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    val speechRecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            binding.btnSpeak.apply {
                setText("Speak")
                isEnabled = true
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.get(0)?.let {
                binding.etInputText.setText(it)
            }
            binding.btnSpeak.apply {
                setText("Speak")
                isEnabled = true
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}