package com.dictify.translate
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.dictify.Language
import com.dictify.LanguageAdapter
import com.dictify.databinding.FragmentHomeBinding
import com.dictify.databinding.FragmentTranslateBinding
import com.dictify.getLanguageList
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class TranslateFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentTranslateBinding? = null
    private val binding: FragmentTranslateBinding
        get() = _binding ?: throw Exception("Binding is null")
    private  var sourceLanguage: String? = null
    private  var targetLanguage: String? = null
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var  recognitionIntent: Intent
    private var isListening = false

    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTranslateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    private fun init(){
        setupLanguageOptions()
        setupAudio()
        binding.apply {
            btnSpeak.setOnClickListener {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                setupSpeechRecognizer()
                startListening()
            }

            btnTranslate.setOnClickListener {
                translate(etInputText.text.toString())
            }

            etInputText.addTextChangedListener(inputTextChangeListener)
        }
    }

    private fun setupLanguageOptions() {
        binding.apply {
            val languageList = getLanguageList()
            val adapter = LanguageAdapter(requireContext(), languageList)
            sourceLanguageSpinner.adapter = adapter
            sourceSpinnerListener(sourceLanguageSpinner, adapter)
            targetLanguageSpinner.adapter = adapter
            targetSpinnerListener(targetLanguageSpinner, adapter)
        }
    }

    private fun setupAudio() {
        textToSpeech = TextToSpeech(requireContext(), this)
        binding.tvTranslatedText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                speakText(s.toString())
            }
        })
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(speechRecognitionListener)
        recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        val supportedLanguages = arrayOf(sourceLanguage)
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, supportedLanguages.joinToString(","))
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

    }

    private fun startListening() {
        try {
            binding.btnSpeak.apply {
                setText("Listening...")
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
    fun translate(inputText:String){
        if(inputText.isNotEmpty() && sourceLanguage != null && targetLanguage != null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage?: TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLanguage?: TranslateLanguage.ENGLISH)
                .build()
            val translator = Translation.getClient(options)
            var conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translator.translate(inputText)
                        .addOnSuccessListener { translatedText ->
                            binding.progressBar.visibility = View.GONE
                            binding.tvTranslatedText.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            binding.progressBar.visibility = View.GONE
                            binding.tvTranslatedText.text = exception.message.toString()

                        }
                }
                .addOnFailureListener { exception ->
                    binding.progressBar.visibility = View.GONE
                    binding.tvTranslatedText.text = exception.message.toString()
                }
        }

    }
    fun sourceSpinnerListener(spinner: Spinner, adapter: ArrayAdapter<Language>){
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

    fun targetSpinnerListener(spinner: Spinner, adapter: ArrayAdapter<Language>){
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = adapter.getItem(position)
                selectedLanguage?.let {
                    targetLanguage = it.code
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

    val inputTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // This method is called to notify you that characters within s are about to be replaced with new text with a length of after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // This method is called to notify you that somewhere within s, the text has been changed
        }

        override fun afterTextChanged(s: Editable?) {
            // This method is called to notify you that the characters within s have been changed
            if (s.toString().isNotEmpty()) {
                translate(s.toString())
            } else {
                binding.tvTranslatedText.text = ""
            }
            // Do something with the entered text
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}