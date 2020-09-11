package com.example.mvvm

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject

class MainViewModel {
    private val compositeDisposable = CompositeDisposable()

    //behavior 쓰는 이유 데이터를 넣는 시점과 subscribe하는 시점이 안맞을 수도 있기 때문에 이전 데이터를 확인해야하기 때문
    val errorSubject = BehaviorSubject.create<Throwable>()
    val loadingSubject = BehaviorSubject.createDefault(false)
    val movieItemsSubject = BehaviorSubject.create<List<Data.BoxOfficeResult.DailyBoxOffice>>()

    fun getMoviesInformation(date: String) {
        NetworkManager.api.getMovieInformation("3556a74b41fbe77f6fb9360a792a5e58", date)
            .doOnError { it.printStackTrace() }
            .doOnSubscribe { loadingSubject.onNext(true) }
            .doOnTerminate { loadingSubject.onNext(false) }
            .map { it.boxOfficeResult.dailyBoxOfficeList }
            .subscribe(movieItemsSubject::onNext)
            .addTo(compositeDisposable)
    }

    fun unBindViewModel() {
        this.compositeDisposable.clear()
    }
}