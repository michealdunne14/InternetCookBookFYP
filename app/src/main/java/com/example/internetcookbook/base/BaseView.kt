package com.example.internetcookbook.base

import androidx.fragment.app.Fragment
import com.example.internetcookbook.models.PostModel
import org.jetbrains.anko.AnkoLogger

open class BaseView: Fragment(), AnkoLogger {
    lateinit var baseFragmentPresenter: BasePresenter

    fun initPresenter(presenter: BasePresenter): BasePresenter {
        baseFragmentPresenter = presenter
        return presenter
    }

    open fun showHillforts(post: List<PostModel>) {}

    open fun showFloatingAction(){}
    open fun hideFloatingAction(){}
}