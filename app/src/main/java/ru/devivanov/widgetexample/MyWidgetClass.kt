package ru.devivanov.widgetexample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.devivanov.widgetexample.remote.DoggyApi
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MyWidgetClass : AppWidgetProvider() {
    private val doggyApi: DoggyApi by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://dog.ceo/api/breeds/image/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        retrofit.create(DoggyApi::class.java)
    }
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        //Итерируемся по списку виджетов
        appWidgetIds?.forEach {
            val pendingIntent = Intent(context, MyWidgetClass::class.java).let {
                it.action = MY_WIDGET_ACTION
                PendingIntent.getBroadcast(context, 0, it, 0)
            }

            //Создаем новый варинат верстки

            RemoteViews(
                    context?.packageName,
                    R.layout.example_appwidget
            ).apply {
                setOnClickPendingIntent(R.id.image_button, pendingIntent)
                //Запскаем корутину
                scope.launch {
                    //Получаем ссылку на картинку от Api
                    val url = doggyApi.getRandomDog().message
                    //Получаем битмап по ссылке
                    val bitmap = getBitmapFromURL(url)
                    //Устанавливаем новое изображение
                    setImageViewBitmap(R.id.image_button, bitmap)
                    //Обновляем виджет
                    appWidgetManager?.updateAppWidget(it, this@apply)
                }
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action != MY_WIDGET_ACTION) return
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetComponentName = ComponentName(context!!, MyWidgetClass::class.java)

        RemoteViews(
                context.packageName,
                R.layout.example_appwidget
        ).apply {
            //Запскаем корутину
            scope.launch {
                //Получаем ссылку на картинку от Api
                val url = doggyApi.getRandomDog().message
                //Получаем битмап по ссылке
                val bitmap = getBitmapFromURL(url)
                //Устанавливаем новое изображение
                setImageViewBitmap(R.id.image_button, bitmap)
                //Обновляем виджет
                appWidgetManager?.updateAppWidget(appWidgetComponentName, this@apply)
            }
        }
    }

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
        private const val MY_WIDGET_ACTION = "MY_WIDGET_ACTION"
    }
}