package ru.lazard.regexp.ui.activity


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.pawegio.kandroid.start
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.app_bar_main.*
import ru.lazard.regexp.R
import ru.lazard.regexp.application.selectedFileManager
import ru.lazard.regexp.ui.BaseActivity
import ru.lazard.regexp.ui.fragments.find.FindFragment
import ru.lazard.regexp.ui.fragments.replace.ReplaceFragment
import ru.lazard.regexp.ui.fragments.replace.ReplaceInFileFragment
import ru.lazard.regexp.ui.fragments.view.ViewFragment


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    val rxNavigationButtonSelected: Subject<Int> = PublishSubject.create<Int>()


    override fun onDestroy() {
        rxNavigationButtonSelected.onComplete()
        rxFloatinActionButtonClick.onComplete()
        super.onDestroy()
    }

    //private ViewStackController viewStackController;
    private val findFragment = FindFragment()
    private val replaceFragment = ReplaceFragment()
    private val viewFragment = ViewFragment()
    private val replaceInFileFragment = ReplaceInFileFragment()

    fun hideFloatingActionButton() {
        fab.hide()
    }

    fun showFloatingActionButton() {
        fab.show()
    }

    val rxFloatinActionButtonClick = PublishSubject.create<View>();

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        fab.setOnClickListener { rxFloatinActionButtonClick.onNext(it) }
        fab.visibility = View.GONE;

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        showFragment(findFragment)


        SelectedFileViewController(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            // if (viewStackController.popView()!=null)return;
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_change_file) {
            selectedFileManager.selectFile(this)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        rxNavigationButtonSelected.onNext(id)
        when (id) {
            R.id.nav_find -> showFragment(findFragment)
            R.id.nav_replace -> showFragment(replaceFragment)
            R.id.nav_replace_in_file -> showFragmentWithPreloadFile(replaceInFileFragment)
            R.id.nav_view_file -> showFragmentWithPreloadFile(viewFragment)

            R.id.nav_send -> {
            }
            R.id.nav_settings -> {
            }
            R.id.nav_share -> {
                Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                    start(this@MainActivity)
                }
            }
            R.id.nav_email -> {
                Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                    putExtra(Intent.EXTRA_TEXT, "")
                    Intent.createChooser(this, getString(R.string.support_choicer)).start(this@MainActivity)
                }
            }

            else -> {
            }
        }


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun showFragmentWithPreloadFile(fragment: Fragment) {
        selectedFileManager.selectFileIfNotSelected(this, { showFragment(fragment) })
    }

    fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        //wqgetSupportFragmentManager().getFragments().stream().filter((x) -> x.isVisible()).forEach(f -> fragmentTransaction.remove(f));

        fragmentTransaction.replace(R.id.fragments, fragment, "fragments")
        fragmentTransaction.commit()
    }

    private var progressDialog: ProgressDialog? = null

    fun showFileChoiceWaiterForView() {


        showWaiter()
    }

    private fun showWaiter() {
        hideWaiter()
        progressDialog = ProgressDialog.show(this, "Wait", "wait")
    }

    fun hideFileChoiceWaiterForView() {
        hideWaiter()
    }

    private fun hideWaiter() {
        if (progressDialog != null) {
            progressDialog!!.cancel()
        }
    }
}
