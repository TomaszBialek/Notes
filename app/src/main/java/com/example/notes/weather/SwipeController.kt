package com.example.notes.weather

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.weather.WeatherAdapter
import java.util.Collections.swap

class SwipeController(
    val context: Context,
    val adapter: WeatherAdapter
) : ItemTouchHelper.Callback() {

    private val iconCall: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.common_full_open_on_phone)
    private val iconSMS: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.googleg_disabled_color_18)

    private val paint = Paint()

    private val colorCall = Color.GREEN
    private val colorSNS = Color.BLUE

    private val heightIconCall = iconCall.height.toFloat()
    private val heightIconSMS = iconSMS.height.toFloat()


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(UP or DOWN, RIGHT or LEFT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        swap(adapter.items, fromPosition, toPosition)
        adapter.notifyItemMoved(fromPosition, toPosition)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val phoneNumber = adapter.items[position].temp
        val intent: Intent

        //Action
        if (direction == ItemTouchHelper.LEFT) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
            intent.putExtra("sms_body", "Hello!")
        } else {
            intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        }

        context.startActivity(intent)
        adapter.notifyItemChanged(viewHolder.adapterPosition)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7F
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE)
            drawButton(c, viewHolder, dX);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawButton(c: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float) {
        val itemView = viewHolder.itemView
        val heightItem = itemView.bottom - itemView.top
        val padding = 30f

        val paddingHeightCall = (heightItem - heightIconCall) / 2
        val paddingHeightSNS = (heightItem - heightIconSMS) / 2

        var background: RectF? = null
        var icon: Bitmap? = null
        var iconDest: RectF? = null

        // Swipe Right
        // set icon
        if (dX > 10) {
            icon = iconCall
            iconDest = RectF(
                itemView.left + padding,
                itemView.top + paddingHeightCall,
                itemView.left + padding + iconCall.width,
                itemView.bottom - paddingHeightCall
            )
        }

        // set background
        if (dX > 200) {
            paint.color = colorCall
            background = RectF(
                0f,
                itemView.top.toFloat(),
                itemView.left + 2 * padding + iconCall.width,
                itemView.bottom.toFloat()
            )
        }

        // Swipe Left
        // set icon
        if (dX < -10) {
            icon = iconSMS
            iconDest = RectF(
                itemView.right - padding - iconSMS.width,
                itemView.top + paddingHeightSNS,
                itemView.right - padding,
                itemView.bottom - paddingHeightSNS
            )
        }

        // set background
        if (dX < -200) {
            paint.color = colorSNS
            background = RectF(
                itemView.right - 2 * padding - iconSMS.width,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )

        }

        if (background != null)
            c.drawRect(background, paint)

        if (icon != null && iconDest != null)
            c.drawBitmap(icon, null, iconDest, paint)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }
}