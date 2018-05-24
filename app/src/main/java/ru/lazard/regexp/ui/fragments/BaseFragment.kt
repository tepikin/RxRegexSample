package ru.lazard.regexp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.trello.rxlifecycle2.components.support.RxFragment

import java.util.regex.Pattern

import ru.lazard.regexp.R
import ru.lazard.regexp.application.regexManager
import ru.lazard.regexp.manage.RegexManager

/**
 * Created by Egor on 17.04.2017.
 */

open class BaseFragment : RxFragment() {
    init {
        setHasOptionsMenu(true)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_test_find, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val regexpFlags = regexManager.flags
        menu!!.findItem(R.id.action_filter_case_insensitive).isChecked = regexpFlags and Pattern.CASE_INSENSITIVE > 0
        menu.findItem(R.id.action_filter_comments).isChecked = regexpFlags and Pattern.COMMENTS > 0
        menu.findItem(R.id.action_filter_dotall).isChecked = regexpFlags and Pattern.DOTALL > 0
        menu.findItem(R.id.action_filter_literal).isChecked = regexpFlags and Pattern.LITERAL > 0
        menu.findItem(R.id.action_filter_multiline).isChecked = regexpFlags and Pattern.MULTILINE > 0
        menu.findItem(R.id.action_filter_unicode_case).isChecked = regexpFlags and Pattern.UNICODE_CASE > 0
        menu.findItem(R.id.action_filter_unix_lines).isChecked = regexpFlags and Pattern.UNIX_LINES > 0
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.action_filter_case_insensitive -> {
                switchFlag(Pattern.CASE_INSENSITIVE, item)
                return true
            }
            R.id.action_filter_comments -> {
                switchFlag(Pattern.COMMENTS, item)
                return true
            }
            R.id.action_filter_dotall -> {
                switchFlag(Pattern.DOTALL, item)
                return true
            }
            R.id.action_filter_literal -> {
                switchFlag(Pattern.LITERAL, item)
                return true
            }
            R.id.action_filter_multiline -> {
                switchFlag(Pattern.MULTILINE, item)
                return true
            }
            R.id.action_filter_unicode_case -> {
                switchFlag(Pattern.UNICODE_CASE, item)
                return true
            }
            R.id.action_filter_unix_lines -> {
                switchFlag(Pattern.UNIX_LINES, item)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun switchFlag(flag: Int, item: MenuItem) {

        var regexpFlags = regexManager.flags
        regexpFlags = regexpFlags xor flag
        item.isChecked = regexpFlags and flag > 0
        regexManager.flags = regexpFlags
    }

}
