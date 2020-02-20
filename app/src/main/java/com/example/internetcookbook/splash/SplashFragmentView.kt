package com.example.internetcookbook.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.internetcookbook.R
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.register.RegisterFragmentPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete


class SplashFragmentView : BaseView() {

    lateinit var presenter: SplashFragmentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = initPresenter(SplashFragmentPresenter(this)) as SplashFragmentPresenter
        presenter.doLoadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}
