package ru.lazard.regexp.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * Created by Egor on 11.04.2017.
 */

open class BaseActivity : AppCompatActivity() {
    val rxLifeCycle: Subject<LifeCycle> = PublishSubject.create<LifeCycle>()

    val rxActivityResult: Subject<ActivityResult> = PublishSubject.create<ActivityResult>()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        rxLifeCycle.onNext(LifeCycle.CREATE)
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onPause() {
        rxLifeCycle.onNext(LifeCycle.PAUSE)
        super.onPause()
    }

    override fun onResume() {
        rxLifeCycle.onNext(LifeCycle.RESUME)
        super.onResume()
    }

    override fun onDestroy() {
        rxLifeCycle.onNext(LifeCycle.DESTROY)
        rxLifeCycle.onComplete()
        rxActivityResult.onComplete()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        rxActivityResult.onNext(ActivityResult(requestCode, resultCode, data))
        super.onActivityResult(requestCode, resultCode, data)
    }

    enum class LifeCycle {
        CREATE, DESTROY, PAUSE, RESUME
    }

    class ActivityResult internal constructor(val requestCode: Int, val resultCode: Int, val data: Intent?)

}
