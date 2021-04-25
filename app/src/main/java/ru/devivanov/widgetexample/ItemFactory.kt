package ru.devivanov.widgetexample

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

//В конструктор класса нам нужно передать контекст и интент
class ItemFactory(private val context: Context, private val intent: Intent) : RemoteViewsService.RemoteViewsFactory {
    //Здесь будет храниться список с сылками на картинки
    private var urlList = arrayListOf<String>()
    //Здесь будет храниться id виджета
    private var widgetId: Int = 0

    override fun onCreate() {
        //При создании сервиса мы будем получать id виджета через экстраз у intent, в качестве ключа мы используем
        //Константу класса AppWidgetManager.EXTRA_APPWIDGET_ID
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    }

    //Этот метод вызывается когда мы будем обновлять данные в адаптере
    override fun onDataSetChanged() {
        //Чистим список
        urlList.clear()
        //Получаем новый список, опять же из экстраз у интента, ключ у нас кастомный, находиться
        //в companion object
        urlList = intent.getStringArrayListExtra(URL_LIST)!!
    }

    //Этот метод автоматичски вызывается при обновлениии списка в адаптере и наполняет виджет элементами
    override fun getViewAt(position: Int): RemoteViews {
        return RemoteViews(
            context.packageName,
            //Это лэйат спциално для элемента списка, по аналигии как у RecyclerView
            R.layout.item
        ).apply {
            //Здесь мы будем получать битмап и класть в ImageView, который у нас есть в вреске элемента
            val bitmap = getBitmapFromURL(urlList[position])
            setImageViewBitmap(R.id.image, bitmap)
        }
    }

    //Эти методы сейчас не важны
    override fun onDestroy() { }
    override fun getCount(): Int = urlList.size
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = 1L
    override fun hasStableIds(): Boolean = true

    //Метод получения битмапа, такой у нас был в классе виджета
    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            //Создаем URL из строки
            val url = URL(src)
            //Открываем соединение
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            //Говорим, что соедениен будет на прием данных
            connection.doInput = true
            //соединяемся
            connection.connect()
            //Создает поток данных
            val input: InputStream = connection.inputStream
            //Декодируем в битмап
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Логируем исключение
            null
        }
    }

    companion object {
        private const val URL_LIST = "URL_LIST"
    }
}