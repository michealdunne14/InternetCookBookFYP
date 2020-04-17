package com.example.internetcookbook.fragmentpresenter

import android.view.View
import com.example.internetcookbook.MainApp
import com.example.internetcookbook.base.BasePresenter
import com.example.internetcookbook.base.BaseView
import com.example.internetcookbook.models.DataModel
import com.example.internetcookbook.models.PostModel
import com.example.internetcookbook.models.UserMasterModel
import com.example.internetcookbook.network.InformationStore
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class HomeFragPresenter(view: BaseView): BasePresenter(view), AnkoLogger {

    override var app : MainApp = view.activity?.application as MainApp
    var infoStore: InformationStore? = null

    init {
        infoStore = app.informationStore as InformationStore
    }

    override fun doSendComment(
        comment: String,
        dataModel: DataModel
    ) {
        doAsync {
            infoStore!!.sendComment(comment, dataModel)
            onComplete {
                view.commentAdded()
            }
        }
    }

    fun doRefreshData(swipeView: View) {
        doAsync {
            infoStore!!.getPostData()
            onComplete {
                doFindHomeData()
                swipeView.swipeToRefresh.isRefreshing = false
                view.initScrollListener()
            }
        }
    }

    fun doFilterDifficulty(difficultyLevel: String) {
        doAsync {
            infoStore!!.getFilterDataDifficulty(difficultyLevel)
            onComplete {
                view.showInformation(infoStore!!.getFilteredData())
            }
        }
    }

    fun doFilterTop(){
        doAsync {
            infoStore!!.getFilterDataTop()
            onComplete {
                view.showInformation(infoStore!!.getFilteredData())
            }
        }
    }


    fun loadMoreData(filterUsed: String, difficultyLevel: String) {
        var moreDataAvilable = false
        doAsync {
            if (filterUsed == "difficulty"){
                moreDataAvilable = infoStore!!.getMoreDataDifficulty(difficultyLevel)
            }else if(filterUsed == "top"){
                moreDataAvilable = infoStore!!.getMoreDataTop()
            }else{
                moreDataAvilable = infoStore!!.getMoreData()
            }
            onComplete {
                if (moreDataAvilable) {
                    view.initScrollListener()
                    view.removeLoading(findData(),filterUsed)
                }else{
                    view.noDataAvilable()
                }
            }
        }
    }

    fun findIngredientsSearch(): ArrayList<DataModel?> {
        return infoStore!!.getFilteredData()
    }

    fun findData(): ArrayList<DataModel?> {
        return infoStore!!.getHomeData()
    }

    fun doFindHomeData(){
        view.showInformation(infoStore!!.getHomeData())
    }

    fun doAddBasket(dataModel: DataModel) {
        doAsync {
            infoStore!!.basketAdd(dataModel)
            onComplete {
                doAsync {
                    infoStore!!.getBasketData()
                }
            }
        }
    }

    fun doFindCurrentUser(): UserMasterModel {
        return infoStore!!.getCurrentUser()
    }

    override fun doHeartData(postModel: PostModel) {
        doAsync {
            infoStore!!.putHeart(postModel)
        }
        doAsync {
            infoStore!!.doUpdateUserHeart(postModel)
        }
    }

    fun doRemoveHeart(postModel: PostModel){
        doAsync {
            infoStore!!.removeHeart(postModel)
        }
    }
}