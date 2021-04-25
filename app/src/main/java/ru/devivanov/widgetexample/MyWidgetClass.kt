package ru.devivanov.widgetexample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.devivanov.widgetexample.remote.DoggyApi


class MyWidgetClass : AppWidgetProvider() {
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        //Итерируемся по списку виджетов
        appWidgetIds?.forEach { id ->
            val pendingIntent = Intent(context, MainActivity::class.java).let {
                PendingIntent.getActivity(context, 0, it, 0)
            }

            val views = RemoteViews(
                context?.packageName,
                R.layout.example_appwidget
            ).apply {
                setOnClickPendingIntent(R.id.image_button, pendingIntent)
            }
            //Сначала обновляем виджет
            appWidgetManager?.updateAppWidget(id, views)
        }
    }
}