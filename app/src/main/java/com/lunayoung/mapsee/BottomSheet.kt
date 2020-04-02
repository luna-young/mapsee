package com.lunayoung.mapsee

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*


//bottom sheet
//https://devofandroid.blogspot.com/2018/04/android-bottom-sheet-kotlin.html

class BottomSheet : BottomSheetDialogFragment(){

    private var bottomSheetListener: BottomSheetListener ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet, container, false)
        view.btnSave.setOnClickListener{
            bottomSheetListener!!.onSaveButtonClicked()
            dismiss()
        }

        return view
    }

    interface BottomSheetListener{
        fun onSaveButtonClicked()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            bottomSheetListener = context as BottomSheetListener?
        }
        catch (e: ClassCastException){
            throw ClassCastException(context.toString())
        }
    }
}