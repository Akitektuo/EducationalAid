package com.akitektuo.educationalaid.fragment

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.akitektuo.educationalaid.R
import com.akitektuo.educationalaid.adapter.DraggableAdapter
import com.akitektuo.educationalaid.util.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_question.*

/**
 * Created by Akitektuo on 03.01.2018.
 */

class QuestionFragment : Fragment(), DraggableAdapter.OnStartDragListener {

    companion object {
        val KEY_ID = "key_id"

        val TYPE_FILL_IN = 0
        val TYPE_SINGLE_CHOICE = 1
        val TYPE_MULTIPLE_CHOICE = 2
        val TYPE_DRAG_IN_ORDER = 3
        val TYPE_DRAG_AND_DROP = 4
    }

    private val resultsFillIn = ArrayList<FillIn>()
    private val resultsSingleChoice = ArrayList<SingleChoice>()
    private val resultsMultipleChoice = ArrayList<MultipleChoice>()
    private val resultDraggable = ArrayList<Draggable>()
    private var onUnlock = { _: DialogInterface, _: Int -> }
    private var itemTouchHelper = ItemTouchHelper(null)

    private data class FillIn(val editText: EditText, val text: String, var nextFocus: EditText? = null) : TextWatcher {

        fun addNextFocus(editText: EditText) {
            nextFocus = editText
            this.editText.addTextChangedListener(this)
        }

        override fun afterTextChanged(editable: Editable) {
            if (editable.toString().length == text.length) {
                nextFocus?.requestFocus()
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    }

    private data class SingleChoice(val radioButton: RadioButton, val checked: Boolean)

    private data class MultipleChoice(val checkBox: CheckBox, val checked: Boolean)

    data class Draggable(val text: String, val position: Int)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_question, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle = arguments
        buttonContinue.text = "Check"

        with(bundle) {
            textCount.text = "1/2"
            textTask.text = "Add a 25-pixel left and 15-pixel down blue text-shadow."
            when (3) {
                TYPE_FILL_IN -> decodeForFillIn("p {\ntext-shadow: -_?_25_?_px _?_15_?_px blue;\n}")
                TYPE_SINGLE_CHOICE -> decodeForSingleChoice("_?_inset\ninner\ninside")
                TYPE_MULTIPLE_CHOICE -> decodeForMultipleChoice("Horizontal offset\n_?_Spread distance\nVertical offset\n_?_Blur distance")
                TYPE_DRAG_IN_ORDER -> decodeForDragInOrder("0_?_Horizontal offset\n2_?_Blur\n1_?_Vertical offset\n3_?_Spread\n4_?_Color")
                TYPE_DRAG_AND_DROP -> decodeForDragAndDrop("p {\ntext-shadow: -_?_25_?_px _?_15_?_px blue;\n}")
            }
            if (getInt(KEY_ID) == 4) {
                imageLocked.visibility = View.VISIBLE
            }
        }

        buildUnlock()
    }

    private fun decodeForFillIn(info: String) {
        for (lineTemp in info.split("\n")) {
            var textCheck = 0
            var line = lineTemp
            if (line.indexOf("_?_") == 0) {
                textCheck = 1
                line = line.replaceFirst("_?_", "")
            }
            val blocks = line.split("_?_")
            if (blocks.size < 2) {
                if (textCheck == 0) {
                    layoutFillIn.addView(generateTextView(line))
                } else {
                    layoutFillIn.addView(generateEditText(line))
                }
            } else {
                val layoutLine = LinearLayout(context)
                layoutLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutLine.orientation = LinearLayout.HORIZONTAL
                layoutLine.gravity = Gravity.CENTER
                for (i in 0 until blocks.size) {
                    if (i % 2 == textCheck) {
                        layoutLine.addView(generateTextView(blocks[i]))
                    } else {
                        layoutLine.addView(generateEditText(blocks[i]))
                    }
                }
                layoutFillIn.addView(layoutLine)
            }
        }

        for (i in 0..resultsFillIn.size - 2) {
            resultsFillIn[i].addNextFocus(resultsFillIn[i + 1].editText)
        }

        layoutFillIn.visibility = View.VISIBLE
        layoutHint.visibility = View.VISIBLE
        layoutHint.setOnClickListener {
            buildHint()
        }

        onUnlock = { _, _ ->
            //decrement user's xp
            for (x in resultsFillIn) {
                x.editText.setText(x.text)
                x.editText.setTextColor(Color.GREEN)
            }
            buttonContinue.text = "Continue"
        }

        buttonContinue.setOnClickListener {
            if (buttonContinue.text.toString() == "Check") {
                if (resultsFillIn.none { it.text != it.editText.text.toString() }) {
                    //good message
                    Toast.makeText(context, "Correct", Toast.LENGTH_SHORT).show()
                    buttonContinue.text = "Continue"
                } else {
                    //wrong message
                    Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show()
                }
                for (x in resultsFillIn) {
                    if (x.editText.text.toString() == x.text) {
                        x.editText.setTextColor(Color.GREEN)
                    } else {
                        x.editText.setTextColor(Color.RED)
                    }
                }
            } else {
                for (x in resultsFillIn) {
                    x.editText.setTextColor(Color.BLACK)
                    x.editText.setText("")
                }
                buttonContinue.text = "Check"
                (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
            }
        }
    }

    private fun decodeForSingleChoice(info: String) {
        cardSingleChoice.visibility = View.VISIBLE
        val choices = info.split("\n")
        for (x in choices.shuffled()) {
            radioGroup.addView(generateRadioButton(x))
        }

        onUnlock = { _, _ ->
            //decrement user's xp
            for (x in resultsSingleChoice) {
                x.radioButton.isChecked = x.checked
                if (x.checked) {
                    x.radioButton.setTextColor(Color.GREEN)
                } else {
                    x.radioButton.setTextColor(Color.BLACK)
                }
            }
            buttonContinue.text = "Continue"
        }

        buttonContinue.setOnClickListener {
            if (buttonContinue.text.toString() == "Check") {
                val isOk = resultsSingleChoice.none { it.checked != it.radioButton.isChecked }
                if (isOk) {
                    //good message
                    Toast.makeText(context, "Correct", Toast.LENGTH_SHORT).show()
                    buttonContinue.text = "Continue"
                } else {
                    //wrong message
                    Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show()
                }
                for (x in resultsSingleChoice) {
                    if (x.radioButton.isChecked) {
                        if (isOk) {
                            x.radioButton.setTextColor(Color.GREEN)
                        } else {
                            x.radioButton.setTextColor(Color.RED)
                        }
                    } else {
                        x.radioButton.setTextColor(Color.BLACK)
                    }
                }
            } else {
                for (x in resultsSingleChoice) {
                    x.radioButton.setTextColor(Color.BLACK)
                    x.radioButton.isChecked = false
                }
                buttonContinue.text = "Check"
                (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
            }
        }
    }

    private fun decodeForMultipleChoice(info: String) {
        cardMultipleChoice.visibility = View.VISIBLE
        val choices = info.split("\n")
        for (x in choices.shuffled()) {
            layoutMultipleChoice.addView(generateCheckBox(x))
        }

        onUnlock = { _, _ ->
            //decrement user's xp
            for (x in resultsMultipleChoice) {
                x.checkBox.isChecked = x.checked
                if (x.checked) {
                    x.checkBox.setTextColor(Color.GREEN)
                } else {
                    x.checkBox.setTextColor(Color.BLACK)
                }
            }
            buttonContinue.text = "Continue"
        }

        buttonContinue.setOnClickListener {
            if (buttonContinue.text.toString() == "Check") {
                val isOk = resultsMultipleChoice.none { it.checked != it.checkBox.isChecked }
                if (isOk) {
                    //good message
                    Toast.makeText(context, "Correct", Toast.LENGTH_SHORT).show()
                    buttonContinue.text = "Continue"
                } else {
                    //wrong message
                    Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show()
                }
                for (x in resultsMultipleChoice) {
                    if (x.checkBox.isChecked) {
                        if (isOk) {
                            x.checkBox.setTextColor(Color.GREEN)
                        } else {
                            x.checkBox.setTextColor(Color.RED)
                        }
                    } else {
                        x.checkBox.setTextColor(Color.BLACK)
                    }
                }
            } else {
                for (x in resultsMultipleChoice) {
                    x.checkBox.setTextColor(Color.BLACK)
                    x.checkBox.isChecked = false
                }
                buttonContinue.text = "Check"
                (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
            }
        }
    }

    private fun decodeForDragInOrder(info: String) {
        listDraggable.visibility = View.VISIBLE
        info.split("\n").shuffled()
                .map { it.split("_?_") }
                .mapTo(resultDraggable) { Draggable(it[1], it[0].toInt()) }
        val adapter = DraggableAdapter(context, resultDraggable, this)
        listDraggable.layoutManager = LinearLayoutManager(context)
        listDraggable.adapter = adapter
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(listDraggable)

        onUnlock = { _, _ ->
            //decrement user's xp
            for (i in 0 until resultDraggable.size) {
                listDraggable.adapter.notifyItemMoved(i, resultDraggable[i].position)
            }
            buttonContinue.text = "Continue"
        }
    }

    private fun decodeForDragAndDrop(info: String) {

    }

    private fun generateTextView(text: String): TextView {
        val textView = TextView(context)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.text = text
        return textView
    }

    private fun generateEditText(text: String): EditText {
        val editText = EditText(context)
        editText.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(text.length))
        editText.setPadding(4, 0, 4, 0)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        editText.imeOptions = EditorInfo.IME_ACTION_DONE
        editText.maxLines = 1
        editText.inputType = InputType.TYPE_CLASS_TEXT
        resultsFillIn.add(FillIn(editText, text))
        return editText
    }

    fun unlockFragment() {
        imageLocked.visibility = View.GONE
    }

    private fun buildHint() {
        val builderHint = AlertDialog.Builder(context)
        builderHint.setTitle("Get a Hint")
        builderHint.setMessage("Use 5XP of your 258XP to get a hint.")
        builderHint.setPositiveButton("Ok", { _, _ ->
            var usedBreak = false
            for (x in resultsFillIn) {
                val current = x.editText.text.toString()
                val expected = x.text
                if (current.isEmpty()) {
                    x.editText.setText(expected[0].toString())
                    usedBreak = true
                    break
                }
                for (i in 0 until current.length) {
                    println("Current \'${current[i]}\' and expected \'${expected[i]}\'")
                    if (current[i] != expected[i]) {
                        x.editText.setText(current.substring(0, i) + expected[i])
                        usedBreak = true
                    }
                }
                if (current.length < expected.length) {
                    x.editText.setText(current + expected[current.length])
                    usedBreak = true
                    break
                }
                if (usedBreak) {
                    break
                }
            }
            if (usedBreak) {
                //decrement total user's xp
            }
        })
        builderHint.setNegativeButton("Cancel", null)
        builderHint.show()
    }

    private fun buildUnlock() {
        layoutUnlock.setOnClickListener {
            val builderUnlock = AlertDialog.Builder(context)
            builderUnlock.setTitle("Unlock Answer")
            builderUnlock.setMessage("Use 15XP of your 258XP to unlock the correct answer.")
            builderUnlock.setPositiveButton("Ok", onUnlock)
            builderUnlock.setNegativeButton("Cancel", null)
            builderUnlock.show()
        }
    }

    private fun generateRadioButton(text: String): RadioButton {
        var line = text
        if (text.startsWith("_?_")) {
            line = text.substring(3, text.length)
        }
        val radioButton = RadioButton(context)
        radioButton.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val padding = (resources.displayMetrics.density * 20 + 0.5f).toInt()
        radioButton.setPadding(padding, padding, padding, padding)
        radioButton.text = line
        resultsSingleChoice.add(SingleChoice(radioButton, text.startsWith("_?_")))
        return radioButton
    }

    private fun generateCheckBox(text: String): CheckBox {
        var line = text
        if (text.startsWith("_?_")) {
            line = text.substring(3, text.length)
        }
        val checkBox = CheckBox(context)
        checkBox.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val padding = (resources.displayMetrics.density * 20 + 0.5f).toInt()
        checkBox.setPadding(padding, padding, padding, padding)
        checkBox.text = line
        resultsMultipleChoice.add(MultipleChoice(checkBox, text.startsWith("_?_")))
        return checkBox
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

}