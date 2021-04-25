package ru.devivanov.widgetexample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RemoteViews
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class ConfigurationActivity : AppCompatActivity() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        //На тот случай если пользователь не закончит настройку и выйдет
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_configuration)
        //Получаем айди виджета
        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        ).also {
            if (it == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish()
                return
            }
        }
        //Находим элемиенты из верстки для взаимодействия
        val button = findViewById<Button>(R.id.button)
        val toggle = findViewById<ToggleButton>(R.id.toggle)

        //Получаем доступ к AppWidgetManager
        val widgetManager = AppWidgetManager.getInstance(this)
        //Создаем RemoteViews
        val views = RemoteViews(
            packageName,
            R.layout.example_appwidget
        )
        //Вешаем на кнопку слушатель
        button.setOnClickListener {
            //Если тоггл включен присваем пендинг интент на нажатие
            if (toggle.isChecked) {
                val pendingIntent = Intent(this, MainActivity::class.java).let {
                    PendingIntent.getActivity(this, 0, it, 0)
                }
                views.setOnClickPendingIntent(R.id.image_button, pendingIntent)
            }
            widgetManager.updateAppWidget(widgetId!!, views)

            //Говорим что пользователь закончил конфигурацию
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}