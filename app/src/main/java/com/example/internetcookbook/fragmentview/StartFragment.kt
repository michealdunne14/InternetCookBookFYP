package com.example.internetcookbook.fragmentview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.internetcookbook.R
import com.example.internetcookbook.pager.PagerFragmentViewDirections
import kotlinx.android.synthetic.main.fragment_start.view.*

class StartFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons(view)

    }

    private fun initButtons(view: View) {
        view.mStartLogin.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToLoginFragmentView()
            view.findNavController().navigate(action)
        }
        view.mStartRegister.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToRegisterFragment()
            view.findNavController().navigate(action)
        }
    }


}
