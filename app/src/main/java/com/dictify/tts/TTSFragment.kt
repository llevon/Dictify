package com.dictify.tts

import android.content.Intent
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.dictify.Language
import com.dictify.LanguageAdapter
import com.dictify.databinding.FragmentTtsBinding
import com.dictify.getLanguageList
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale
import kotlin.concurrent.thread

class TTSFragment: Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentTtsBinding? = null
    private val binding: FragmentTtsBinding
        get() = _binding ?: throw Exception("Binding is null")

    private var sourceLanguage: String? = null
    private var targetLanguage: String? = null
    private lateinit var textToSpeech: TextToSpeech



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTtsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        setupLanguageOptions()
        binding.apply {
            etInputText.addTextChangedListener(inputTextChangeListener)
            setupTranslateForSpeech()
            translate(etInputText.text.toString())
            setupAudio()
            etInputText.addTextChangedListener(inputTextChangeListener)

            btnSwap.setOnClickListener {
                swapSpinners()
            }

            btnVoiceover.setOnClickListener {

                speakText(tvTranslatedText.text.toString())
            }

            btnSwap.setOnClickListener{
                swapSpinners()
            }

            btnShare.setOnClickListener {
                shareText(tvTranslatedText.text.toString())
            }

        }
    }

    private fun setupTranslateForSpeech(){
        val text = arguments?.getString("text")
        binding.etInputText.setText(text)
        binding.etInputText.addTextChangedListener(inputTextChangeListener)
        translate(text ?: "")
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
            targetLanguageSpinner.adapter = adapter
            targetSpinnerListener(targetLanguageSpinner, adapter)
        }
    }

    private fun setupAudio() {
        textToSpeech = TextToSpeech(requireContext(), this)
        binding.tvTranslatedText.addTextChangedListener(object :
            TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                //speakText(s.toString())
            }
        }
        )
    }

    fun translate(inputText: String) {
        if (inputText.isNotEmpty() && sourceLanguage != null && targetLanguage != null && arguments?.getString("text") != "") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage ?: TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLanguage ?: TranslateLanguage.ENGLISH)
                .build()
            val translator = Translation.getClient(options)
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translator.translate(inputText)
                        .addOnSuccessListener { translatedText ->
                            binding.tvTranslatedText.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            binding.tvTranslatedText.text = exception.message.toString()
                        }
                }
                .addOnFailureListener { exception ->
                    binding.tvTranslatedText.text = exception.message.toString()
                }
        }
    }
    private fun swapSpinners() {
        val tempLanguage = sourceLanguage
        sourceLanguage = targetLanguage
        targetLanguage = tempLanguage

        binding.sourceLanguageSpinner.setSelection(getLanguagePosition(sourceLanguage))
        binding.targetLanguageSpinner.setSelection(getLanguagePosition(targetLanguage))
    }

    private fun getLanguagePosition(languageCode: String?): Int {
        if (languageCode != null) {
            val languageList = getLanguageList()
            for (i in languageList.indices) {
                if (languageList[i].code == languageCode) {
                    return i
                }
            }
        }
        return 0
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

    private fun targetSpinnerListener(spinner: Spinner, adapter: ArrayAdapter<Language>) {
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

    private val inputTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // This method is called to notify you that characters within s are about to be replaced with new text with a length of after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // This method is called to notify you that somewhere within s, the text has been changed
        }

        override fun afterTextChanged(s: Editable?) {
            if (s.toString().isNotEmpty()) {
                translate(s.toString())
            } else {
                binding.tvTranslatedText.text = ""
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}