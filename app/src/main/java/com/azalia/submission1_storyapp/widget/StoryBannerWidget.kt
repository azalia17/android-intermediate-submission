package com.azalia.submission1_storyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.azalia.submission1_storyapp.R

class StoryBannerWidget : AppWidgetProvider() {

    companion object {
        private const val TOAST_ACTION = "com.azalia.submission1_storyapp.widget.TOAST_ACTION"
        const val EXTRA_ITEM = "com.azalia.submission1_storyapp.widget.EXTRA_ITEM"
        private const val ITEMS_CLICK = "com.azalia.submission1_storyapp.widget.ITEMS_CLICK_FROM_LIST"
        private const val TITLE_CLICK = "com.azalia.submission1_storyapp.widget.USER_CLICK_WIDGET_TITLE"
        private const val REFRESH_CLICK = "com.azalia.submission1_storyapp.widget.USER_REFRESH_WIDGET_DATA"

        private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.story_banner_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_text)

            initStackItems(views, context, appWidgetId)
            initClickLabel(views, context)
            initClickRefresh(views, context)

            val toastIntent = Intent(context, StoryBannerWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
               else 0
            )

            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        /* init action when user click image items from widget -> open detail story */
        private fun initStackItems(views: RemoteViews, context: Context, appWidgetId: Int) {
            val clickIntent = Intent(context, StoryBannerWidget::class.java)
            clickIntent.action = ITEMS_CLICK
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, clickIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )
            views.setPendingIntentTemplate(R.id.stack_view, pendingIntent)
            views.setEmptyView(R.id.stack_view, R.id.stack_view)
        }

        /* init action when user click widget title in homescreen (Dico Story) -> open apps */
        private fun initClickLabel(views: RemoteViews, context: Context) {
            val openAppIntent = Intent(context, StoryBannerWidget::class.java)
            openAppIntent.action = TITLE_CLICK
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, openAppIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )
            views.setOnClickPendingIntent(R.id.banner_text, pendingIntent)
        }

        /* init action when user click refresh button in homescreen -> reload recent story data */
        private fun initClickRefresh(views: RemoteViews, context: Context) {
            val refreshIntent = Intent(context, StoryBannerWidget::class.java)
            refreshIntent.action = REFRESH_CLICK
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, refreshIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )
            views.setOnClickPendingIntent(R.id.btn_refresh, pendingIntent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            if (intent.action != null) {
                val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
                Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}