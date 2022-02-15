package com.example.paintfinal.drawing

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.paintfinal.R
import com.example.paintfinal.databinding.DrawingFragmentBinding
import java.nio.channels.SeekableByteChannel
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

import android.widget.TextView
import vadiole.colorpicker.ColorModel
import vadiole.colorpicker.ColorPickerDialog


class DrawingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val MODE_PENCIL = 1
        val MODE_CIRCLE = 2
        val MODE_SQUARE = 3

        val binding: DrawingFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.drawing_fragment, container, false)

        val sk = binding.strokeBar
        sk.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                binding.lienzo.setStroke(i.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.colors.setOnClickListener {
            val colorPicker: ColorPickerDialog = ColorPickerDialog.Builder()

                //  set initial (default) color
                .setInitialColor(ResourcesCompat.getColor(resources, R.color.colorPaint, null))

                //  set Color Model. ARGB, RGB or HSV
                .setColorModel(ColorModel.HSV)

                //  set is user be able to switch color model
                .setColorModelSwitchEnabled(true)

                //  set your localized string resource for OK button
                .setButtonOkText(android.R.string.ok)

                //  set your localized string resource for Cancel button
                .setButtonCancelText(android.R.string.cancel)

                //  callback for picked color (required)
                .onColorSelected { color: Int ->
                    binding.lienzo.setColor(color)
                }

                //  create dialog
                .create()

            colorPicker.show(childFragmentManager, "color_picker")
        }

        binding.fill.setOnClickListener {
            val colorPicker: ColorPickerDialog = ColorPickerDialog.Builder()

                //  set initial (default) color
                .setInitialColor(ResourcesCompat.getColor(resources, R.color.colorPaint, null))

                //  set Color Model. ARGB, RGB or HSV
                .setColorModel(ColorModel.HSV)

                //  set is user be able to switch color model
                .setColorModelSwitchEnabled(true)

                //  set your localized string resource for OK button
                .setButtonOkText(android.R.string.ok)

                //  set your localized string resource for Cancel button
                .setButtonCancelText(android.R.string.cancel)

                //  callback for picked color (required)
                .onColorSelected { color: Int ->
                    binding.lienzo.bgColor = color
                    binding.lienzo.invalidate()
                }

                //  create dialog
                .create()

            colorPicker.show(childFragmentManager, "color_picker")
        }

        binding.square.setOnClickListener {
            binding.lienzo.setMode(MODE_SQUARE)
        }

        binding.circle.setOnClickListener {
            binding.lienzo.setMode(MODE_CIRCLE)
        }

        binding.fillCheck.setOnClickListener {
            if(binding.fillCheck.isChecked) {
                binding.lienzo.setFill(true)
            } else {
                binding.lienzo.setFill(false)
            }
        }

        binding.pencil.setOnClickListener {
            binding.lienzo.setMode(MODE_PENCIL)
            binding.lienzo.setDefaultColor()
        }

        binding.cleanButton.setOnClickListener {
            binding.lienzo.setColor(binding.lienzo.bgColor)
        }

        binding.undo.setOnClickListener {
            if(binding.lienzo.drawing.isNotEmpty())
                binding.lienzo.undo()
        }

        binding.redo.setOnClickListener {
            if(binding.lienzo.drawingRecycler.isNotEmpty())
                binding.lienzo.redo()
        }

        binding.clean.setOnClickListener {
            binding.lienzo.clear()
        }

        return binding.root
    }




}