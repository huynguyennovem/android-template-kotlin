package com.template.customurltextview

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class CustomUrlTextView : AppCompatTextView {

    var mListener: OnClickLinkListener? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyTextUrl(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        applyTextUrl(context, attrs)
    }

    private fun applyTextUrl(context: Context, attrs: AttributeSet) {
        val attributeArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomUrlTextView)
        val separateCharStart = attributeArray.getString(R.styleable.CustomUrlTextView_separateCharStart)
        val separateCharEnd = attributeArray.getString(R.styleable.CustomUrlTextView_separateCharEnd)

        val term = this.text
        val spannableStringBuilder = SpannableStringBuilder(term)
        val click = object : ClickableSpan() {
            override fun onClick(widget: View) {
                mListener!!.onClick()
            }
        }
        spannableStringBuilder.setSpan(click, term.indexOf(separateCharStart!!) + 1, term.indexOf(separateCharEnd!!), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.replace(term.indexOf(separateCharStart), term.indexOf(separateCharStart) + 1, "")
        spannableStringBuilder.replace(term.indexOf(separateCharEnd) - 1, term.indexOf(separateCharEnd), "")
        this.text = spannableStringBuilder
        this.movementMethod = LinkMovementMethod.getInstance()

        attributeArray.recycle()
    }

    fun setListener(listener: OnClickLinkListener) {
        mListener = listener
    }

    interface OnClickLinkListener {
        fun onClick()
    }
}