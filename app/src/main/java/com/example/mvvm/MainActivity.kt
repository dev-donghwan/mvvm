package com.example.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val vm by lazy { MainViewModel() }
    private val compositeDisposable = CompositeDisposable()
    private val recyclerAdapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onResume() {
        super.onResume()
        bindingViewModel()
    }

    override fun onPause() {
        vm.unBindViewModel()
        super.onPause()
    }

    private fun initView() {
        rv_content.adapter = recyclerAdapter
    }

    private fun bindingViewModel() {
        //한번 등록해두면 된다.
        val textChange = RxTextView.textChangeEvents(et_input)
            .debounce(1500L, TimeUnit.MILLISECONDS)
            .map { it.toString() }
        val searchClick = RxView.clicks(btn_search)
            .map { et_input.text.toString() }

        listOf(textChange, searchClick) //listOf()로 두 문자를 묶어서 merge를 통해서 throttleFirst를 공통으로 적용시킨다.
            .merge()
            .filter(String::isNotBlank)
            .throttleFirst(1000L, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(vm::getMoviesInformation)
            .addTo(compositeDisposable)

        vm.errorSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showError)
            .addTo(compositeDisposable)

        vm.loadingSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pb_loading.isVisible = it }
            .addTo(compositeDisposable)

        vm.movieItemsSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(recyclerAdapter::replaceAll)
            .addTo(compositeDisposable)
    }

    private fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
    }
}
