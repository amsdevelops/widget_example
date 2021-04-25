package ru.devivanov.widgetexample

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetItemService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ItemFactory(applicationContext, intent)
    }
}