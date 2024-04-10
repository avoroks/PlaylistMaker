package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener

class SearchActivity : AppCompatActivity() {
    private var textValue = EMPTY_TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = this.findViewById<TextView>(R.id.back_from_search)

        backButton.setOnClickListener {
            this.finish()
        }

        val clearButton = this.findViewById<ImageButton>(R.id.button_clear)
        val editTextField = this.findViewById<EditText>(R.id.edittext_search)
        editTextField.setText(textValue)

        clearButton.setOnClickListener {
            editTextField.setText(EMPTY_TEXT)
            hideSoftKeyboard(editTextField)
        }

        editTextField.addTextChangedListener(
            onTextChanged = {
                s, _, _, _ ->
                textValue = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, textValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textValue = savedInstanceState.getString(SEARCH_TEXT, EMPTY_TEXT)
    }

    private fun hideSoftKeyboard(input: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0);
    }

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
    }
}

