package ru.devivanov.widgetexample

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
        appWidgetIds?.forEach { id ->
            scope.launch {
                val urlList = arrayListOf<String>()
                //Дважды получаем ссылку на картинку картинку и кладем в список
                repeat(2) {
                    urlList.add(doggyApi.getRandomDog().message)
                }
                //создаем адаптер
                val adapter = Intent(context, WidgetItemService::class.java).apply {
                    //Кладем id в экстраз
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                    //Кладем сам лист в экстраз
                    putExtra(URL_LIST, urlList)
                }
                //Создаем RemoteView в котором прикрепляем адаптер к листу
                val views = RemoteViews(
                        context?.packageName,
                        R.layout.example_appwidget
                ).apply {
                    setRemoteAdapter(R.id.list, adapter)
                }
                //Сначала обновляем виджет
                appWidgetManager?.updateAppWidget(id, views)
                //Потом обновляем адаптер
                appWidgetManager?.notifyAppWidgetViewDataChanged(id, R.id.list)
            }
        }
    }
    //Ключ к списку с ссылками должен быть такой же как и в классе адаптера
    companion object {
        private const val URL_LIST = "URL_LIST"
    }
}