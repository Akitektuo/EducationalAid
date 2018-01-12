package com.akitektuo.educationalaid.fragment

import android.content.DialogInterface
import android.media.MediaPlayer
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
    private val resultsDragAndDrop = ArrayList<DragAndDrop>()
    private val dragAndDropKeys = ArrayList<TextView>()
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

    private data class DragAndDrop(val editText: EditText, val textView: TextView, val text: String)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_question, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle = arguments

        with(bundle) {
            textCount.text = getString(R.string.out_of, 1, 2)
            textTask.text = "Add a 25-pixel left and 15-pixel down blue text-shadow."
            when (getInt(KEY_ID) - 2) {
                TYPE_FILL_IN -> decodeForFillIn("p {\ntext-shadow: -_?_25_?_px _?_15_?_px blue;\n}")
                TYPE_SINGLE_CHOICE -> decodeForSingleChoice("_?_inset\ninner\ninside")
                TYPE_MULTIPLE_CHOICE -> decodeForMultipleChoice("Horizontal offset\n_?_Spread distance\nVertical offset\n_?_Blur distance")
                TYPE_DRAG_IN_ORDER -> decodeForDragInOrder("0_?_Horizontal offset\n2_?_Blur\n1_?_Vertical offset\n3_?_Spread\n4_?_Color")
                TYPE_DRAG_AND_DROP -> decodeForDragAndDrop("_?_#test_?_ _?_p_?_ {\n   color: red;\n}_;_.test_;_#p")
            }
            if (getInt(KEY_ID) == 0) {
                imageLocked.visibility = View.VISIBLE
            }
        }

        buildUnlock()
    }

    private fun decodeForFillIn(info: String) {
        reset()
        resultsFillIn.clear()
        layoutFillIn.removeAllViews()
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
            }
            correct()
        }

        buttonContinue.setOnClickListener {
            when (buttonContinue.text.toString()) {
                getString(R.string.check) -> {
                    if (resultsFillIn.none { it.text != it.editText.text.toString() }) {
                        correct()
                    } else {
                        wrong()
                    }
                }
                getString(R.string.continue_button) -> {
                    layoutFillIn.removeAllViews()
                    (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
                }
                getString(R.string.try_again) -> {
                    decodeForFillIn(info)
                    reset()
                }
            }
        }
    }

    private fun decodeForSingleChoice(info: String) {
        radioGroup.removeAllViews()
        reset()
        resultsSingleChoice.clear()
        cardSingleChoice.visibility = View.VISIBLE
        val choices = info.split("\n")
        for (x in choices.shuffled()) {
            radioGroup.addView(generateRadioButton(x))
        }

        onUnlock = { _, _ ->
            //decrement user's xp
            for (x in resultsSingleChoice) {
                x.radioButton.isChecked = x.checked
            }
            correct()
        }

        buttonContinue.setOnClickListener {
            when (buttonContinue.text.toString()) {
                getString(R.string.check) -> {
                    if (resultsSingleChoice.none { it.checked != it.radioButton.isChecked }) {
                        correct()
                    } else {
                        wrong()
                    }
                }
                getString(R.string.continue_button) -> {
                    decodeForSingleChoice(info)
                    (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
                }
                getString(R.string.try_again) -> {
                    decodeForSingleChoice(info)
                }
            }
        }
    }

    private fun decodeForMultipleChoice(info: String) {
        layoutMultipleChoice.removeAllViews()
        reset()
        resultsMultipleChoice.clear()
        cardMultipleChoice.visibility = View.VISIBLE
        val choices = info.split("\n")
        for (x in choices.shuffled()) {
            layoutMultipleChoice.addView(generateCheckBox(x))
        }

        onUnlock = { _, _ ->
            //decrement user's xp
            for (x in resultsMultipleChoice) {
                x.checkBox.isChecked = x.checked
            }
            correct()
        }

        buttonContinue.setOnClickListener {
            when (buttonContinue.text.toString()) {
                getString(R.string.check) -> {
                    if (resultsMultipleChoice.none { it.checked != it.checkBox.isChecked }) {
                        correct()
                    } else {
                        wrong()
                    }
                }
                getString(R.string.continue_button) -> {
                    decodeForMultipleChoice(info)
                    (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
                }
                getString(R.string.try_again) -> {
                    decodeForMultipleChoice(info)
                }
            }
        }
    }

    private fun decodeForDragInOrder(info: String) {
        reset()
        resultDraggable.clear()
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
            var i = 0
            while (i < resultDraggable.size) {
                if (i != resultDraggable[i].position) {
                    listDraggable.adapter.notifyItemMoved(i, resultDraggable[i].position)
                    val movedValue = resultDraggable[i]
                    for (j in i until movedValue.position) {
                        resultDraggable[j] = resultDraggable[j + 1]
                    }
                    resultDraggable[movedValue.position] = movedValue
                    i--
                }
                i++
            }
            correct()
        }

        buttonContinue.setOnClickListener {
            when (buttonContinue.text.toString()) {
                getString(R.string.check) -> {
                    if ((0 until resultDraggable.size).none { it != resultDraggable[it].position }) {
                        correct()
                    } else {
                        wrong()
                    }
                }
                getString(R.string.continue_button) -> {
                    decodeForDragInOrder(info)
                    (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
                }
                getString(R.string.try_again) -> {
                    decodeForDragInOrder(info)
                }
            }
        }
    }

    private fun decodeForDragAndDrop(info: String) {
        reset()
        resultsDragAndDrop.clear()
        dragAndDropKeys.clear()
        val viewKeys = layoutDragAndDrop.getChildAt(layoutDragAndDrop.childCount - 1)
        layoutDragAndDrop.removeAllViews()
        layoutDragAndDrop.visibility = View.VISIBLE
        val parts = info.split("_;_")
        for (lineTemp in parts[0].split("\n")) {
            var textCheck = 0
            var line = lineTemp
            if (line.indexOf("_?_") == 0) {
                textCheck = 1
                line = line.replaceFirst("_?_", "")
            }
            val blocks = line.split("_?_")
            if (blocks.size < 2) {
                if (textCheck == 0) {
                    layoutDragAndDrop.addView(generateTextView(line))
                } else {
                    layoutDragAndDrop.addView(generateEditText(line, false))
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
                        layoutLine.addView(generateEditText(blocks[i], false))
                    }
                }
                layoutDragAndDrop.addView(layoutLine)
            }
        }
        layoutDragAndDrop.addView(viewKeys)
        layoutDragAndDropKeys.removeAllViews()
        layoutDragAndDropKeys.visibility = View.VISIBLE
        resultsDragAndDrop.mapTo(dragAndDropKeys) { it.textView }
        (1 until parts.size).mapTo(dragAndDropKeys) { generateDraggableTextView(parts[it]) }
        dragAndDropKeys.shuffle()
        for (x in dragAndDropKeys) {
            layoutDragAndDropKeys.addView(x)
        }

        onUnlock = { _, _ ->
            //decrement user's xp
            layoutDragAndDropKeys.removeAllViews()
            for (x in dragAndDropKeys) {
                layoutDragAndDropKeys.addView(x)
            }
            for (x in resultsDragAndDrop) {
                x.editText.setText(x.text)
                layoutDragAndDropKeys.removeView(x.textView)
            }
            correct()
        }

        buttonContinue.setOnClickListener {
            when (buttonContinue.text.toString()) {
                getString(R.string.check) -> {
                    if (resultsDragAndDrop.none { it.editText.text.toString() != it.text }) {
                        correct()
                    } else {
                        wrong()
                    }
                }
                getString(R.string.continue_button) -> {
                    decodeForDragAndDrop(info)
                    (activity as com.akitektuo.educationalaid.notifier.Fragment.OnClickContinue).continueOnClick()
                }
                getString(R.string.try_again) -> {
                    decodeForDragAndDrop(info)
                }
            }
        }
    }

    private fun generateTextView(text: String): TextView {
        val textView = TextView(context)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.text = text
        return textView
    }

    private fun generateEditText(text: String, isEditable: Boolean = true): EditText {
        val editText = EditText(context)
        editText.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        editText.setPadding(4, 0, 4, 0)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        if (isEditable) {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(text.length))
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
            editText.maxLines = 1
            editText.inputType = InputType.TYPE_CLASS_TEXT
            resultsFillIn.add(FillIn(editText, text))
        } else {
            editText.inputType = InputType.TYPE_NULL
            editText.gravity = Gravity.CENTER
            editText.isFocusable = false
            editText.setTextColor(context.resources.getColor(R.color.colorAccent))
            editText.setOnClickListener {
                if (editText.text.isNotEmpty()) {
                    layoutDragAndDropKeys.addView(generateDraggableTextView(editText.text.toString()))
                    editText.setText("")
                }
            }
            val draggableTextView = generateDraggableTextView(text)
            resultsDragAndDrop.add(DragAndDrop(editText, draggableTextView, text))
        }
        return editText
    }

    fun unlockFragment() {
        imageLocked.visibility = View.GONE
    }

    private fun buildHint() {
        val builderHint = AlertDialog.Builder(context)
        builderHint.setTitle(getString(R.string.dialog_hint_title))
        builderHint.setMessage(getString(R.string.dialog_hint_body, 258))
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
        builderHint.setNegativeButton(getString(R.string.cancel), null)
        builderHint.show()
    }

    private fun buildUnlock() {
        layoutUnlock.setOnClickListener {
            val builderUnlock = AlertDialog.Builder(context)
            builderUnlock.setTitle(getString(R.string.dialog_unlock_title))
            builderUnlock.setMessage(getString(R.string.dialog_unlock_body, 258))
            builderUnlock.setPositiveButton("Ok", onUnlock)
            builderUnlock.setNegativeButton(getString(R.string.cancel), null)
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

    private fun reset() {
        cardResult.visibility = View.GONE
        layoutResultCorrect.visibility = View.GONE
        layoutResultWrong.visibility = View.GONE
        buttonContinue.text = getString(R.string.check)
    }

    private fun correct() {
        MediaPlayer.create(context, R.raw.correct).start()
        cardResult.visibility = View.VISIBLE
        layoutResultCorrect.visibility = View.VISIBLE
        buttonContinue.text = getString(R.string.continue_button)
    }

    private fun wrong() {
        MediaPlayer.create(context, R.raw.wrong).start()
        cardResult.visibility = View.VISIBLE
        layoutResultWrong.visibility = View.VISIBLE
        buttonContinue.text = getString(R.string.try_again)
    }

    private fun generateDraggableTextView(text: String): TextView {
        val textView = TextView(context)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.text = text
        val padding = (resources.displayMetrics.density * 8 + 0.5f).toInt()
        textView.setPadding(padding, padding, padding, padding)
        textView.setTextColor(resources.getColor(R.color.colorAccent))
        textView.tag = text
        textView.setOnClickListener {
            var canPerform = false
            for (x in resultsDragAndDrop) {
                if (x.editText.text.isEmpty()) {
                    x.editText.setText(text)
                    canPerform = true
                    break
                }
            }
            if (canPerform) {
                layoutDragAndDropKeys.removeView(textView)
            }
        }
        return textView
    }

}